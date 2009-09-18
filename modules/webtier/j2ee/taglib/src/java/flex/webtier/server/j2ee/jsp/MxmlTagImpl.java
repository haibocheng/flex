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
package flex.webtier.server.j2ee.jsp;

import flash.localization.LocalizationManager;
import flash.localization.ResourceBundleLocalizer;
import flash.localization.XLRLocalizer;
import flash.util.StringUtils;
import flex.webtier.compiler.ConfigConstants;
import flex.webtier.server.j2ee.URLHelper;
import flex.webtier.server.j2ee.cache.CacheKeyUtils;
import flex.webtier.server.j2ee.wrappers.J2EEWrapper;
import flex.webtier.services.ServiceFactory;
import flex.webtier.services.extensions.ExtensionManager;
import flex.webtier.util.PathResolver;
import flex.webtier.util.ServletPathResolver;
import flex.webtier.util.Trace;
import flex2.compiler.common.LocalFilePathResolver;
import flex2.compiler.util.ThreadLocalToolkit;
import flex2.compiler.util.URLPathResolver;
import flex.webtier.server.j2ee.BaseCompileFilter;
import flex.webtier.server.j2ee.CompileFilter;
import flex.webtier.server.j2ee.DetectionSettingsFactory;
import flex.webtier.server.j2ee.HistorySettingsFactory;
import flex.webtier.server.j2ee.MxmlContext;
import flex.webtier.server.j2ee.MxmlFilter;
import flex.webtier.server.j2ee.PathExistsFilter;
import flex.webtier.server.j2ee.RecompileFilter;
import flex.webtier.server.j2ee.events.FileSourceCodeLoader;
import flex.webtier.server.j2ee.events.CompileEventLogger;
import flex.webtier.server.j2ee.html.DetectionSettings;
import flex.webtier.server.j2ee.html.HistorySettings;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.PageContext;

public class MxmlTagImpl extends CompileAgentTagSupport implements MxmlTag, ConfigConstants
{
    private String mxmlExt;
    private String mxmlSwfExt;
    private String swfExt;

    private ServletContext servletContext;
    private SourceCache sourceCache;

    private HttpServletRequest request;
    private HttpServletResponse response;

    // jsp tag attributes
    private String source;
    private HashMap flashVars = new HashMap(10);
    private String swfId;
    private String height;
    private String width;
    private String useHistoryManagement;
    private String usePlayerDetection;
    private String useExpressInstall;
    private String alternateContentPage;

    private DetectionSettings detectionSettings;
    private HistorySettings historySettings;

    public void setPageContext(PageContext pageContext)
    {
        super.setPageContext(pageContext);
        servletContext = J2EEWrapper.getServletContext(pageContext.getServletContext());
        request = (HttpServletRequest)J2EEWrapper.getRequest(pageContext.getRequest());
        // encoding must be set before any access to request parameters
        try
        {
            request.setCharacterEncoding("UTF-8");
        }
        catch (UnsupportedEncodingException uee)
        {
            // don't worry, the app server will handle
        }
        response = (HttpServletResponse)pageContext.getResponse();
    }

    public int doStartTag() throws JspException
    {
        initCache();
        ExtensionManager extensionManager = ServiceFactory.getExtensionManager();
        mxmlExt = extensionManager.getMxmlExt();
        mxmlSwfExt = ServiceFactory.isMxmlSwfExtension()? extensionManager.getMxmlSwfExt() : extensionManager.getSwfExt();
        swfExt = extensionManager.getSwfExt();

        // setup the detection settings which can be overridden by the tag attributes
        detectionSettings = (DetectionSettings)DetectionSettingsFactory.getInstance(request.getContextPath()).clone();

        if ((usePlayerDetection != null) && (usePlayerDetection.equalsIgnoreCase("true")))
        {
            detectionSettings.setEnabled(true);
        }
        else if ((usePlayerDetection != null) && (usePlayerDetection.equalsIgnoreCase("false")))
        {
            detectionSettings.setEnabled(false);
        }

        if ((useExpressInstall != null) && (useExpressInstall.equalsIgnoreCase("true")))
        {
            detectionSettings.setUseExpressInstall(true);
        }
        else if ((useExpressInstall != null) && (useExpressInstall.equalsIgnoreCase("false")))
        {
            detectionSettings.setUseExpressInstall(false);
        }

        if (alternateContentPage != null)
        {
            alternateContentPage = StringUtils.substitute(alternateContentPage, TOKEN_CONTEXT_ROOT, request.getContextPath());
            detectionSettings.setAlternateContentPage(alternateContentPage);
        }

        // setup the history settings which can be overridden by the tag attributes
        historySettings = (HistorySettings) HistorySettingsFactory.getInstance(request.getContextPath()).clone();

        if ((useHistoryManagement != null) && (useHistoryManagement.equalsIgnoreCase("true")))
        {
            historySettings.setEnabled(true);
        }
        else if ((useHistoryManagement != null) && (useHistoryManagement.equalsIgnoreCase("false")))
        {
            historySettings.setEnabled(false);
        }

        return EVAL_BODY_BUFFERED;
    }

    private void initCache()
    {
        if (sourceCache == null)
        {
            sourceCache = SourceCache.getInstance(servletContext);
        }
    }

    private void coordinateMultipleTags(CompileEventLogger logger, MxmlContext context)
    {
        Integer count = (Integer) request.getAttribute(getClass().getName());
        boolean firstOnPage = count == null;
        int appid = firstOnPage ? 0 : count.intValue();
        request.setAttribute(getClass().getName(), new Integer(appid+1));
        context.setFirstOnPage(firstOnPage);

        Boolean history = (Boolean)request.getAttribute(getClass().getName() + "_HISTORY");
        // use the settings so that the default is included in the firstHistory calculation
        if ((history == null) && (historySettings.enabled()))
        {
            context.setFirstHistory(true);
            request.setAttribute(getClass().getName() + "_HISTORY", Boolean.TRUE);
        }
        else if (history != null)
        {
            logger.logWarning("There is more than one swf on the page which will prevent history management from working correctly. Please set useHistoryManagement on the mxml JSP tag to 'false'.", true);
        }

        Boolean detection = (Boolean)request.getAttribute(getClass().getName() + "_DETECTION");
        // use the settings so that the default is included in the firstDetection calculation
        if ((detection == null) && (detectionSettings.enabled()))
        {
            context.setFirstDetection(true);
            request.setAttribute(getClass().getName() + "_DETECTION", Boolean.TRUE);
        }
    }

    public void setSource(String source)
    {
        this.source = source;
    }

    public void setFlashVar(Object name, Object value) throws JspTagException
    {
        // check to make sure no body text exists yet
        if (!isBodyEmpty())
        {
            throw new JspTagException("Parameters must be set before inlined MXML");
        }

        if (name != null)
        {
            flashVars.put(name, value);
        }
    }

    public void setId(String id)
    {
        this.swfId = id;
    }

    /**
     * This is used for the height of the html wrapper. If a height is specified for the Application in mxml, then the mxml height is used
     * to compile the swf and the jsp attribute height is used in the wrapper.
     *
     * @param height - A positive integer or a percentage of the available vertical space
     */
    public void setHeight(String height) throws JspTagException
    {
        if (!height.endsWith("%"))
        {
            try
            {
                Integer.parseInt(height);
            }
            catch (NumberFormatException nfe)
            {
                throw new JspTagException("The height attribute must be a number or a percent.");
            }
        }

        this.height = height;
    }

    /**
     * This is used for the width of the html wrapper. If a width is specified for the Application in mxml, then the mxml width is used
     * to compile the swf and the jsp attribute width is used in the wrapper.
     *
     * @param width - A positive integer or a percentage of the available horizontal space
     */
    public void setWidth(String width) throws JspTagException
    {
        if (!width.endsWith("%"))
        {
            try
            {
                Integer.parseInt(width);
            }
            catch (NumberFormatException nfe)
            {
                throw new JspTagException("The 'width' attribute must be a number or a percent.");
            }
        }

        this.width = width;
    }

    public void setUseHistoryManagement(String useHistoryManagement) throws JspTagException
    {
        if ((useHistoryManagement.equalsIgnoreCase("true") || useHistoryManagement.equalsIgnoreCase("false")))
        {
            this.useHistoryManagement = useHistoryManagement;
        }
        else
        {
            throw new JspTagException("The 'useHistoryManagement' attribute must be 'true' or 'false'.");
        }
    }

    public void setUsePlayerDetection(String usePlayerDetection) throws JspTagException
    {
        if (usePlayerDetection.equalsIgnoreCase("true") || usePlayerDetection.equalsIgnoreCase("false"))
        {
            this.usePlayerDetection = usePlayerDetection;
        }
        else
        {
            throw new JspTagException("The 'usePlayerDetection' attribute must be 'true' or 'false'.");
        }
    }

    public void setUseExpressInstall(String useExpressInstall) throws JspTagException
    {
        if (useExpressInstall.equalsIgnoreCase("true") || useExpressInstall.equalsIgnoreCase("false"))
        {
            this.useExpressInstall = useExpressInstall;
        }
        else
        {
            throw new JspTagException("The 'useExpressInstall' attribute must be 'true' or 'false'.");
        }
    }

    public void setAlternateContentPage(String alternateContentPage) throws JspTagException
    {
        if (!alternateContentPage.equals(""))
        {
            this.alternateContentPage = alternateContentPage;
        }
        else
        {
            throw new JspTagException("The 'alternateContentPage' attribute must not be the empty string.");
        }
    }

    /**
     * cmurphy - Use doEndTag instead of doAfterBody.
     * For the cases where the mxml is contained within the body, we're not modifying the body
     * bug simply reading it.  The doEndTag method is sufficient.
     * For the cases where the mxml is external and there isn't a separate end tag, TomCat and
     * WebSphere never called doAfterBody so doEndTag was required.
     */
    public int doEndTag() throws JspException
    {
        PathResolver.setThreadLocalPathResolver( new ServletPathResolver(servletContext) );

        try
        {
            if ((request.getCharacterEncoding() == null) || (request.getCharacterEncoding().equals("utf-8")))
            {
                request.setCharacterEncoding("UTF-8");
            }
        }
        catch (UnsupportedEncodingException uce)
        {
        }

        try
        {
            if (source != null)
            {
                compileExternal();
            }
            else
            {
                compileInlined();
            }
        }
        catch (IOException e)
        {
            if (Trace.error)
            {
                e.printStackTrace();
            }

            throw new JspException(e);
        }

        return EVAL_PAGE;
    }

    public void release()
	{
        request = null;
        response = null;

		// reset tag attributes
        source = null;
		flashVars = new HashMap();
        height = null;
        width = null;
        useHistoryManagement = null;
        usePlayerDetection = null;
        useExpressInstall = null;
        alternateContentPage = null;

        detectionSettings = null;
        historySettings = null;

        super.release();
	}

    private void compileInlined() throws IOException, JspException
    {
        String sourceCode = bodyContent.getString();
        bodyContent.clearBody();

        // .../foo.jsp  =>  .../1234567890.mxml.swf
        String sourceHash = SourceCache.sourceToKey(sourceCode);
        String dependencyKey = CacheKeyUtils.generateJspCacheKey(request, sourceHash);
        SourceCacheEntry sourceEntry = sourceCache.getEntry(dependencyKey);

        if (sourceEntry == null)
        {
            // add to the source cache for recompile later
            String servletPath = request.getServletPath();
            String jspFilename = servletContext.getRealPath(servletPath);
            StringBuffer swfUri = new StringBuffer(servletPath.substring(0, servletPath.lastIndexOf('/') + 1) + sourceHash + mxmlSwfExt);
            CacheKeyUtils.appendCompilerDirectives(swfUri, request.getQueryString());
            sourceEntry = new SourceCacheEntry(sourceCode, jspFilename, dependencyKey, sourceHash);
            sourceCache.addEntry(sourceEntry);
        }

        MxmlFilter filterChain = createInlineCompileChain(swfExt);
        MxmlContext mxmlContext = new MxmlContext();
        setupInlineCompileMxmlContext(servletContext, mxmlContext, request, sourceHash, sourceCode);
        invoke(request, response, mxmlContext, filterChain);
    }

    private void compileExternal() throws IOException, JspException, JspTagException
    {
        if (!isBodyEmpty())
        {
            throw new JspTagException("Inline MXML not allowed when source attribute is given");
        }

        // assume the user passed in something.mxml
        if (source.length() > 0 && source.charAt(0) != '/')
        {
            String sp = request.getServletPath();
            source = sp.substring(0,sp.lastIndexOf('/')+1) + source;
        }

        MxmlFilter filterChain = createExternalCompileChain(swfExt);
        MxmlContext mxmlContext = new MxmlContext();
        setupExternalCompileMxmlContext(servletContext, mxmlContext, request, source);
        invoke(request, response, mxmlContext, filterChain);
    }

    private boolean isBodyEmpty()
    {
        // check to make sure no body text exists yet
        if (bodyContent != null)
        {
            String content = bodyContent.getString();
            if (content != null && content.trim().length() > 0)
            {
                return false;
            }
        }
        return true;
    }

    public void invoke(HttpServletRequest request, HttpServletResponse response, MxmlContext context, MxmlFilter filterChain) throws IOException, JspException
    {
        try
        {
            if (!ServiceFactory.getLicenseService().isMxmlCompileEnabled())
            {
                response.sendError(481, "The current license does not support this feature.");
                ServiceFactory.getLogger().logError("The current license does not support this feature. request=" + request.getServletPath());
                return;
            }

            PathResolver.setThreadLocalPathResolver(new ServletPathResolver(servletContext));
            flex2.compiler.common.PathResolver resolver = new flex2.compiler.common.PathResolver();
            resolver.addSinglePathResolver(new flex.webtier.server.j2ee.ServletPathResolver(servletContext));
            resolver.addSinglePathResolver(LocalFilePathResolver.getSingleton());
            resolver.addSinglePathResolver(URLPathResolver.getSingleton());
            ThreadLocalToolkit.setPathResolver(resolver);

            // set up for localizing messages
            LocalizationManager localizationManager = new LocalizationManager();
            localizationManager.addLocalizer(new XLRLocalizer());
            localizationManager.addLocalizer(new ResourceBundleLocalizer());
            ThreadLocalToolkit.setLocalizationManager(localizationManager);

            CompileEventLogger logger = setupCompileEventLogger(context, request);
            coordinateMultipleTags(logger, context);

            filterChain.invoke(context);
        }
        catch (Throwable t)
        {
            // return an error page
            ServiceFactory.getLogger().logError("Unknown error " + request.getServletPath() + ": " + t.getMessage(), t);
            throw new JspException(t);
        }
        finally
        {
            PathResolver.setThreadLocalPathResolver(null);
            ThreadLocalToolkit.setLocalizationManager(null);
            ThreadLocalToolkit.setLogger(null);
            ThreadLocalToolkit.setPathResolver(null);
        }
    }

    private MxmlFilter createInlineCompileChain(String swfExt)
    {
        // compile filter - no incremental compile
        // incremental compile doesn't currently integrate with inline compile because
        // incremental compile only applies to a subsequent compile of the same filename
        // with jsp compile, anytime the source changes the filename changes and therefore
        // there are never subsequent compiles

        RecompileFilter recompileFilter = new RecompileFilter();

        // special jsp compile filter which looks to the source cache for the source code
        BaseCompileFilter compileFilter = new JspCompileFilter(swfExt);

        // render filter, uses special jsp render filter which includes only the wrapper snippet
        JspRenderFilter renderFilter = new JspRenderFilter();

        recompileFilter.setNext(compileFilter);
        compileFilter.setNext(renderFilter);
        return recompileFilter;
    }

    private MxmlFilter createExternalCompileChain(String swfExt)
    {
        // check whether or not the path exists before proceeding
        PathExistsFilter pathExistsFilter = new PathExistsFilter();

        RecompileFilter recompileFilter = new RecompileFilter();

        // use the regular mxml compile filter
        BaseCompileFilter compileFilter = new CompileFilter(swfExt);

        // render filter, uses special jsp render filter which includes only the wrapper snippet
        JspRenderFilter renderFilter = new JspRenderFilter();

        pathExistsFilter.setNext(recompileFilter);
        recompileFilter.setNext(compileFilter);
        compileFilter.setNext(renderFilter);
        return pathExistsFilter;
    }

    protected void setupInlineCompileMxmlContext(ServletContext servletContext, MxmlContext context, HttpServletRequest request, String sourceHash, String sourceCode)
    {
        context.setRequest(request);
        context.setResponse(response);
        context.setResponseWriter(new PrintWriter(bodyContent.getEnclosingWriter()));
        context.setServletContext(servletContext);
        context.setPageTitle(request.getServletPath());
        context.setSwfId(swfId);
        context.setJspHeight(height);
        context.setJspWidth(width);

        context.setDetectionSettings(detectionSettings);
        context.setHistorySettings(historySettings);
        context.setFlashVars(flashVars);
        context.setSourceCodeLoader(new JspSourceCodeLoader(sourceCode));

        String jspFilename = servletContext.getRealPath(request.getServletPath());
        String parentDir = new File(jspFilename).getParent();
        context.setAppPath(parentDir + File.separator + "jsp" + sourceHash + mxmlExt);
        context.setParentDir(parentDir);

        // .../foo.jsp  =>  .../1234567890.mxml.swf
        String dependencyKey = CacheKeyUtils.generateJspCacheKey(request, sourceHash);
        context.setDependencyKey(dependencyKey);
        context.setCacheKey(dependencyKey);

        String servletPath = request.getServletPath();
        StringBuffer swfUri = new StringBuffer(servletPath.substring(0, servletPath.lastIndexOf('/') + 1));
        // If the mxml.swf.extension is disabled, prefix swfUri with "jsp"  
        if (!ServiceFactory.isMxmlSwfExtension()) 
           swfUri.append("jsp"); 
        swfUri.append(sourceHash).append(mxmlSwfExt);        

        CacheKeyUtils.appendCompilerDirectives(swfUri, request.getQueryString());
        context.setSwfUri(swfUri.toString());
    }

    protected void setupExternalCompileMxmlContext(ServletContext servletContext, MxmlContext context, HttpServletRequest request, String source)
    {
        context.setRequest(request);
        context.setResponse(response);
        context.setResponseWriter(new PrintWriter(bodyContent.getEnclosingWriter()));
        context.setServletContext(servletContext);
        context.setPageTitle(request.getServletPath());
        context.setSwfId(swfId);        
        context.setJspHeight(height);
        context.setJspWidth(width);
        context.setDetectionSettings(detectionSettings);
        context.setHistorySettings(historySettings);
        context.setSourceCodeLoader(new FileSourceCodeLoader(PathResolver.getThreadLocalPathResolver()));

        // if there is a query string, save it now; then remove it
        Map paramMap = null;
        String queryString = null;
        if (source.indexOf("?") != -1)
        {
            queryString = source.substring(source.indexOf("?") + 1);
            paramMap = URLHelper.getParameterMap(queryString);
            source = source.substring(0, source.indexOf("?"));
        }

        // strip off any extensions for the source key
        String sourceKey = source;
        if (source.indexOf(mxmlExt) != -1)
        {
            sourceKey = source.substring(0, source.indexOf(mxmlExt));
        }

        // create the swf key
        StringBuffer swfUri = new StringBuffer(sourceKey + mxmlSwfExt);
        if ((request.getQueryString() != null) && (queryString != null))
        {
            queryString = request.getQueryString() + "&" + queryString;
        }
        else if (request.getQueryString() != null)
        {
            queryString = request.getQueryString();
        }
        CacheKeyUtils.appendCompilerDirectives(swfUri, queryString);
        context.setSwfUri(swfUri.toString());

        // strip off query string for source file name
        String mxmlFilename = servletContext.getRealPath(source);
        context.setSourceFileName(mxmlFilename);

        String parentDir = new File(mxmlFilename).getParent();
        context.setAppPath(mxmlFilename);
        context.setParentDir(parentDir);

        // get the dependency and cache keys
        String dependencyKey = CacheKeyUtils.generateJspExternalCacheKey(request, sourceKey, queryString);
        context.setDependencyKey(dependencyKey);
        context.setCacheKey(dependencyKey);

        // add any additional flashvars specified in the source attribute
        if (paramMap != null)
        {
            Set entries = paramMap.entrySet();
            Iterator iter = entries.iterator();
            while (iter.hasNext())
            {
                Map.Entry entry = (Map.Entry)iter.next();
                if (!CacheKeyUtils.isCompilerDirective((String)entry.getKey()))
                {
                    flashVars.put(entry.getKey(), entry.getValue());
                }
            }
        }
        context.setFlashVars(flashVars);
    }
}
