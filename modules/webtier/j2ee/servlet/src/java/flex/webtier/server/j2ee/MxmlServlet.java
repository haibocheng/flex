////////////////////////////////////////////////////////////////////////////////
//
//  ADOBE SYSTEMS INCORPORATED
//  Copyright 2005-2008 Adobe Systems Incorporated
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
import flex.webtier.services.VersionInfo;
import flex.webtier.services.extensions.ExtensionManager;
import flex.webtier.services.license.LicenseService;
import flex.webtier.util.PathResolver;
import flex.webtier.util.ServiceUtil;
import flex.webtier.util.ServletPathResolver;
import flex.webtier.util.ServletUtils;
import java.io.FileNotFoundException;
import java.io.IOException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;

import org.apache.commons.logging.LogFactory;

public class MxmlServlet extends CompileAgentServlet
{
    static final long serialVersionUID = -714667980223685920L;

    private ServiceFactory serviceImpl;
    private MxmlFilter filterChain;
    private MxmlFilter objectFilterChain;
    private String swfExt;
    private boolean logCompilerErrors;

    public void init(ServletConfig servletConfig) throws ServletException
    {
        super.init(servletConfig);

        serviceImpl = ServiceUtil.setupFlexService(getServletContext(), servletConfig);

        LicenseService license = ServiceFactory.getLicenseService();
        if (license.isBetaExpired())
        {
            log(license.expiredMessage());
            throw new ServletException(license.expiredMessage());
        }

        // print version and beta days left message, if applicable
        String message = "Starting Adobe Flex Web Tier Compiler";
        if (license.getBetaRemainingMessage() != null)
        {
            message += license.getBetaRemainingMessage();
        }
        log(message);
        log(VersionInfo.buildMessage());

        initializeExtensions();
        filterChain = createFilterChain();
        objectFilterChain = createObjectFilterChain();

        logCompilerErrors = ServiceFactory.getConfigurator().getServerConfiguration().getDebuggingConfiguration().logCompilerErrors();

    }

    public void destroy()
    {
    	LogFactory.releaseAll();
        if (serviceImpl != null)
        {
            serviceImpl.stop();
        }
        super.destroy();
    }

    private void initializeExtensions()
    {
        ExtensionManager extensionManager = ServiceFactory.getExtensionManager();
        swfExt = extensionManager.getSwfExt();
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException
    {
        try
        {
            // encoding must be set before any access to request parameters
            request.setCharacterEncoding("UTF-8");

            MxmlContext context = new MxmlContext();
            context.setRequest(request);
            context.setResponse(response);           
            context.setServletContext(getServletContext());
            context.setPageTitle(request.getServletPath());
            setupMxmlContextKeys(getServletContext(), context, request);
            context.setDetectionSettings(DetectionSettingsFactory.getInstance(request.getContextPath()));
            context.setHistorySettings(HistorySettingsFactory.getInstance(request.getContextPath()));

            if (!ServiceFactory.getLicenseService().isMxmlCompileEnabled())
            {
                response.sendError(481, "The current license does not support this feature.");
                ServiceFactory.getLogger().logError("The current license does not support this feature. request=" + request.getServletPath());
                return;
            }

            if (isObjectRequest(request))
            {
                if (request.getParameter("w") != null)
                {
                    context.setWidth(request.getParameter("w"));
                }
                else
                {
                    context.setWidth("100%");
                }
                if (request.getParameter("h") != null)
                {
                    context.setHeight(request.getParameter("h"));
                }
                else
                {
                    context.setHeight("100%");
                }
                objectFilterChain.invoke(context);
            }
            else
            {
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

                setupCompileEventLogger(context, request);
                setupSourceCodeLoader(context);

                filterChain.invoke(context);
            }
        }
        catch (FileNotFoundException fnfe)
        {
            // return an error page
            HttpCache.setCacheHeaders(false, 0, -1, response);
            response.sendError(HttpServletResponse.SC_NOT_FOUND, fnfe.getMessage());

            if (logCompilerErrors)
            {
                ServiceFactory.getLogger().logError(fnfe.getMessage(), fnfe);
            }
        }
        catch (Throwable t)
        {
            // return an error page
            ServiceFactory.getLogger().logError("Unknown error " + request.getServletPath() + ": " + t.getMessage(), t);
            throw new ServletException(t);
        }
        finally
        {            
        	ServletUtils.clearThreadLocals();
        }
    }

    // handles request for the javascript with object/embed tags
    private boolean isObjectRequest(HttpServletRequest request)
    {
        if (request.getParameter("objectWrapperJs") != null)
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    private MxmlFilter createFilterChain()
    {
    	
        // check whether or not the path exists before proceeding
        PathExistsFilter pathExistsFilter = new PathExistsFilter();

        // process ?recompile query param
        RecompileFilter recompileFilter = new RecompileFilter();

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
        // render filter, uses html or xml adapter
        RenderFilter renderFilter = new RenderFilter();

        pathExistsFilter.setNext(recompileFilter);
        recompileFilter.setNext(compileFilter);
        compileFilter.setNext(renderFilter);

        return pathExistsFilter;
    }

    private MxmlFilter createObjectFilterChain()
    {
        // check licensing access
        RenderFilter renderFilter = new RenderFilter();
        return renderFilter;
    }
}
