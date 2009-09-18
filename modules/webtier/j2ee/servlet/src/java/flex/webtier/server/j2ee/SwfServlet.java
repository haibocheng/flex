////////////////////////////////////////////////////////////////////////////////
//
//  ADOBE SYSTEMS INCORPORATED
//  Copyright 2003-2008 Adobe Systems Incorporated
//  All Rights Reserved.
//
//  NOTICE: Adobe permits you to use, modify, and distribute this file
//  in accordance with the terms of the license agreement accompanying it.
//
////////////////////////////////////////////////////////////////////////////////
package flex.webtier.server.j2ee;

import flash.localization.LocalizationManager;
import flash.localization.ResourceBundleLocalizer;
import flash.localization.XLRLocalizer;
import flex2.compiler.util.ThreadLocalToolkit;
import flex2.compiler.util.URLPathResolver;
import flex2.compiler.common.LocalFilePathResolver;
import flex.webtier.services.ServiceFactory;
import flex.webtier.services.config.FlashPlayerConfiguration;
import flex.webtier.services.extensions.ExtensionManager;
import flex.webtier.util.HttpConstants;
import flex.webtier.util.PathResolver;
import flex.webtier.util.ServletPathResolver;
import flex.webtier.util.ServletUtils;
import flex.webtier.util.Trace;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Constructor;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/**
 * this servlet responds to *.swf requests, and serves up swf content
 * swf servlet must be able to compile swf in the event of a cache miss
 *
 * If the request is for a precompiled swf, the swf is served off disk.
 *
 * @author Edwin Smith
 * @author Cathy Murphy
 */
public class SwfServlet extends CompileAgentServlet
{
    static final long serialVersionUID = -2187066346561637934L;

    private String asSwfExt;
    private String mxmlSwdExt;
    private String mxmlSwfExt;
    private String swdExt;
    private String swfExt;
    private String swcSwfExt;
    private String swcSwdExt;
    private MxmlFilter compileChain;
    private MxmlFilter precompileChain;
    private boolean logCompilerErrors;

    public void init() throws ServletException
    {
        // check if the general services were started up correctly
        if ((ServiceFactory.getConfigurator() == null) || (ServiceFactory.getExtensionManager() == null))
        {
            throw new ServletException("Invalid Configuration: see previous failures.");
        }

        initializeExtensions();
        compileChain = createCompileChain();
        precompileChain = createPrecompileChain();

        logCompilerErrors = ServiceFactory.getConfigurator().getServerConfiguration().getDebuggingConfiguration().logCompilerErrors();
    }

    private void initializeExtensions()
    {
        ExtensionManager extensionManager = ServiceFactory.getExtensionManager();
        asSwfExt = extensionManager.getAsSwfExt();
        mxmlSwdExt = extensionManager.getMxmlSwdExt();
        mxmlSwfExt = extensionManager.getMxmlSwfExt();
        swdExt = extensionManager.getSwdExt();
        swfExt = extensionManager.getSwfExt();
        swcSwdExt = extensionManager.getSwcSwdExt();
        swcSwfExt = extensionManager.getSwcSwfExt();
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {

        request.setCharacterEncoding("UTF-8");
        String url = request.getRequestURL().toString();
        url = URLDecoder.decode(url);

        PathResolver.setThreadLocalPathResolver(new ServletPathResolver(getServletContext()));
        flex2.compiler.common.PathResolver resolver = new flex2.compiler.common.PathResolver();
        resolver.addSinglePathResolver(new flex.webtier.server.j2ee.ServletPathResolver(getServletContext()));
        resolver.addSinglePathResolver(LocalFilePathResolver.getSingleton());
        resolver.addSinglePathResolver(URLPathResolver.getSingleton());
        ThreadLocalToolkit.setPathResolver(resolver);

        // set up for localizing messages
        LocalizationManager localizationManager = new LocalizationManager();
        localizationManager.addLocalizer(new XLRLocalizer());
        localizationManager.addLocalizer(new ResourceBundleLocalizer());
        ThreadLocalToolkit.setLocalizationManager(localizationManager);

        try
        {
            // process request
            if (isContypeRequest(request, url))
            {
                handleRequestFromContype(response);
            }
            else if (isJspMxml(url))
            {
                handleJspMxmlRequest(request, response);
            }
            else if (isMxmlSwfRequest(url) || isMxmlSwdRequest(url) || isAsSwfRequest(url))
            {
                assert (url.indexOf(mxmlSwdExt) == -1) : (swdExt + " requests should all be handled through the cache filter");
                handleMxmlRequest(request, response);
            }
            else if (isSwcRequest(url))
            {
                handleSwcExtractRequest();
            }
            else if (isPrecompiledSwfRequest(url) || isPrecompiledSwdRequest(url))
            {
                handlePrecompiledSwfRequest(request, response);
            }
        }
        finally
        {
        	ServletUtils.clearThreadLocals();
        }
    }

    public static void sendNotAvailableResponse(HttpServletResponse response, String status, String message, Throwable t) throws IOException
    {
        response.sendError(481, status);
        // unexpected, always log this error
        ServiceFactory.getLogger().logError(message, t);
    }


    /**
     * return an empty response but set the content type
     */
    private void handleRequestFromContype(HttpServletResponse response)
    {
        response.setContentType(FlashPlayerConfiguration.CONTENT_TYPE);
    }

    private void handleSwcExtractRequest() {}

    private void handlePrecompiledSwfRequest(HttpServletRequest request, HttpServletResponse response)
        throws IOException
    {
        try
        {
            MxmlContext context = new MxmlContext();
            context.setRequest(request);
            context.setResponse(response);
            context.setServletContext(getServletContext());
            setupMxmlContextKeys(getServletContext(), context, request);

            setupCompileEventLogger(context, request);

            precompileChain.invoke(context);
        }
        catch (FileNotFoundException fnfe)
        {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            if (logCompilerErrors)
            {
                ServiceFactory.getLogger().logError(fnfe.getMessage(), fnfe);
            }
        }
        catch (Throwable t)
        {
            if (Trace.error)
            {
                t.printStackTrace();
            }
            sendNotAvailableResponse(response, "Unknown error", "Unknown error " + request.getServletPath() + ": " + t.getMessage(), t);
        }
    }

    private void handleMxmlRequest(HttpServletRequest request, HttpServletResponse response) throws IOException
    {
        MxmlContext context = new MxmlContext();
        context.setRequest(request);
        context.setResponse(response);
        context.setServletContext(getServletContext());
        context.setPageTitle(request.getServletPath());
        setupMxmlContextKeys(getServletContext(), context, request);
        setupSourceCodeLoader(context);

        try

        {
            if (!ServiceFactory.getLicenseService().isMxmlCompileEnabled())
            {
                response.sendError(481, "The current license does not support this feature.");
                ServiceFactory.getLogger().logError("The current license does not support this feature. request=" + request.getServletPath());
                return;
            }

            setupCompileEventLogger(context, request);

            compileChain.invoke(context);
        }
        catch (FileNotFoundException fnfe)
        {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            if (logCompilerErrors)
            {
                ServiceFactory.getLogger().logError(fnfe.getMessage(), fnfe);
            }
        }
        catch (Throwable t)
        {
            if (Trace.error)
            {
                t.printStackTrace();
            }

            sendNotAvailableResponse(response, "Unknown error", "Unknown error " + request.getServletPath() + ": " + t.getMessage(), t);
        }
    }

    private void handleJspMxmlRequest(HttpServletRequest request, HttpServletResponse response) throws IOException
    {
        try
        {
            Class handlerClass = Class.forName("flex.webtier.server.j2ee.jsp.JspSwfRequestHandler");
            Constructor constructor = handlerClass.getConstructor(new Class[] {});
            SwfRequestHandler rh = (SwfRequestHandler) constructor.newInstance(new Object[] {});
            rh.processRequest(request, response, getServletContext());
        }
        catch (Exception e)
        {
            sendNotAvailableResponse(response, "JspSwflRequest Handler Unavailable", "JspSwfRequest Handler Unavailable", e);
        }
    }

    private MxmlFilter createCompileChain()
    {
        // check whether or not the path exists before proceeding
        PathExistsFilter pathExistsFilter = new PathExistsFilter();

        // process recompile=true query param
        RecompileFilter recompileFilter = new RecompileFilter();

        // return 304 if the browser content is up-to-date
        BrowserCacheFilter browserCacheFilter = new BrowserCacheFilter();

        // compile filer - either incremental or regular
        BaseCompileFilter compileFilter;
        if (ServiceFactory.getConfigurator().getServerConfiguration().isIncrementalCompile())
        {
            compileFilter = new IncrementalCompileFilter(swfExt);
        }
        else
        {
            compileFilter = new CompileFilter(swfExt);
        }
        // writes swf/swd to response and render warnings/errors to console/log
        SwfRenderFilter swfRenderFilter = new SwfRenderFilter(swfExt, swdExt);

        pathExistsFilter.setNext(recompileFilter);
        recompileFilter.setNext(browserCacheFilter);
        browserCacheFilter.setNext(compileFilter);
        compileFilter.setNext(swfRenderFilter);

        return pathExistsFilter;
    }

    private MxmlFilter createPrecompileChain()
    {
        // process recompile=true query param
        RecompileFilter recompileFilter = new RecompileFilter();

        // return 304 if the browser content is up-to-date
        BrowserCacheFilter browserCacheFilter = new BrowserCacheFilter();

        // reload swf from disk or return from cache
        PrecompileFilter precompileFilter = new PrecompileFilter(swfExt);

        // writes swf/swd to response and render warnings/errors to console/log
        SwfRenderFilter swfRenderFilter = new SwfRenderFilter(swfExt, swdExt);

        recompileFilter.setNext(browserCacheFilter);
        browserCacheFilter.setNext(precompileFilter);
        precompileFilter.setNext(swfRenderFilter);

        return recompileFilter;
    }

    private boolean isContypeRequest(HttpServletRequest request, String path)
    {
        boolean isContype = false;

        if ((path.endsWith(swfExt)) &&
            ((request.getHeader(HttpConstants.USER_AGENT) != null) && (request.getHeader(HttpConstants.USER_AGENT).equalsIgnoreCase("contype"))))
        {
            isContype = true;
        }

        return isContype;
    }

    private boolean isJspMxml(String path)
    {
        boolean isJspMxml = false;

        if (isMxmlSwfRequest(path))
        {
            // check that it's 32 characters
            String base = path.substring(path.lastIndexOf('/') + 1, path.indexOf(mxmlSwfExt));
            if (base.length() == 32)
            {
                // now that the initial checks are complete, assume it's a jsp until we figure out otherwise
                isJspMxml = true;
                for (int i = 0; i < 32 && isJspMxml; i ++)
                {
                    if (!((Character.isDigit(base.charAt(i))) ||
                    (base.charAt(i) == 'A') ||
                    (base.charAt(i) == 'B') ||
                    (base.charAt(i) == 'C') ||
                    (base.charAt(i) == 'D') ||
                    (base.charAt(i) == 'E') ||
                    (base.charAt(i) == 'F')))
                    {
                        isJspMxml = false;
                    }
                }
            }
        }

        return isJspMxml;
    }

    private boolean isMxmlSwfRequest(String path)
    {
        boolean isMxmlSwf = false;

        if (path.endsWith(mxmlSwfExt))
        {
            isMxmlSwf = true;
        }

        return isMxmlSwf;
    }

    private boolean isAsSwfRequest(String path)
    {
        boolean isAsSwf = false;

        if (path.endsWith(asSwfExt))
        {
            isAsSwf = true;
        }

        return isAsSwf;
    }

    private boolean isMxmlSwdRequest(String path)
    {
        boolean isMxmlSwd = false;

        if ((path.endsWith(mxmlSwdExt)))
        {
            isMxmlSwd = true;
        }

        return isMxmlSwd;
    }

    private boolean isSwcRequest(String path)
    {
        boolean isSwc = false;

        if (path.endsWith(swcSwfExt) || path.endsWith(swcSwdExt))
        {
            isSwc = true;
        }

        return isSwc;
    }

    private boolean isPrecompiledSwfRequest(String path)
    {
        boolean isPrecompiledSwf = false;

        if (path.endsWith(swfExt) && !path.endsWith(mxmlSwfExt) && !path.endsWith(swcSwfExt))
        {
            isPrecompiledSwf = true;
        }

        return isPrecompiledSwf;
    }

    private boolean isPrecompiledSwdRequest(String path)
    {
        boolean isPrecompiledSwd = false;

        if (path.endsWith(swdExt) && !path.endsWith(mxmlSwdExt) && !path.endsWith(swcSwdExt))
        {
            isPrecompiledSwd = true;
        }

        return isPrecompiledSwd;
    }
}
