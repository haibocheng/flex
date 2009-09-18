////////////////////////////////////////////////////////////////////////////////
//
//  ADOBE SYSTEMS INCORPORATED
//  Copyright 2003-2007 Adobe Systems Incorporated
//  All Rights Reserved.
//
//  NOTICE: Adobe permits you to use, modify, and distribute this file
//  in accordance with the terms of the license agreement accompanying it.
//
////////////////////////////////////////////////////////////////////////////////
package flex.webtier.server.j2ee;

import java.util.HashMap;
import java.util.TreeMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.ServletContext;

import flex.webtier.util.J2EEUtil;
import flex.webtier.server.j2ee.cache.CacheKeyUtils;
import flex.webtier.services.ServiceFactory;
import flex.webtier.services.extensions.ExtensionManager;

public class RequestContext {

    private String dependencyKey;
    private String sourceFileName;
    private String swfUri;
    private String objectUri;
    private String objectQuery;
    private Map<String, String> flashVarsMap;
    
    public RequestContext(ServletContext context, HttpServletRequest request)
    {
        String requestFileName = J2EEUtil.getRealPath(request.getServletPath(), context);

        ExtensionManager extensionManager = ServiceFactory.getExtensionManager();
        String mxmlSwfExt = extensionManager.getMxmlSwfExt();
        String mxmlSwdExt = extensionManager.getMxmlSwdExt();
        String swfExt = extensionManager.getSwfExt();
        String swdExt = extensionManager.getSwdExt();
        String asSwfExt = extensionManager.getAsSwfExt();

        if ((requestFileName.lastIndexOf(mxmlSwfExt) != -1) ||
                (requestFileName.lastIndexOf(mxmlSwdExt) != -1) ||
                (requestFileName.lastIndexOf(asSwfExt) != -1))
        {
            setupSwfRequest(context, request);
        }
        else if ((requestFileName.lastIndexOf(swfExt) != -1) ||
                (requestFileName.lastIndexOf(swdExt) != -1))
        {
            setupPrecompileRequest(request);
        }
        else
        {
            setupMxmlRequest(context, request);
        }
    }

    /**
     * set up for a static swf file
     * @param request
     */
    private void setupPrecompileRequest(HttpServletRequest request)
    {
        dependencyKey = CacheKeyUtils.generateGenericCacheKey(request, null);
    }

    /**
     * set up for requesting dynamically generated SWF
     * @param context
     * @param request
     */
    private void setupSwfRequest(ServletContext context, HttpServletRequest request)
    {

        String requestPath = request.getServletPath();
        sourceFileName = J2EEUtil.getRealPath(FileNameGenerator.sourceFileName(requestPath), context);
        dependencyKey = CacheKeyUtils.generateSwfCacheKey(request, null);
    }

    /**
     * set up for a call to an mxml file
     * @param context
     * @param request
     */
    private void setupMxmlRequest(ServletContext context, HttpServletRequest request)
    {

        String query = getSwfQueryString(request);

        // links to the mxml document itself
        String requestPath = request.getServletPath();
        String requestFileName = J2EEUtil.getRealPath(requestPath, context);
        sourceFileName = requestFileName;
        dependencyKey = CacheKeyUtils.generateSwfCacheKey(request, null);

        // the swf request string plus compiler directive query parameters
        swfUri = FileNameGenerator.swfFileName(requestPath);
        if (query != null)
        {
            swfUri = swfUri + "?" + query;
        }

        // put query params on later
        objectUri = FileNameGenerator.sourceFileName(requestPath);
        objectQuery = getObjectQueryString(request);
        
        // query params with compiler directives stripped
        flashVarsMap = getFlashVars(request);
    }

    public String getDependencyKey()
    {
        return dependencyKey;
    }

    public String getSourceFileName()
    {
        return sourceFileName;
    }

    public String getSwfUri()
    {
        return swfUri;
    }

    public String getObjectUri()
    {
        String uri = objectUri;
        if (objectQuery != null)
        {
            uri = uri + "?" + objectQuery;
        }

        return uri;
    }

    public String getObjectQueryString(HttpServletRequest request)
    {
        TreeMap queries = new TreeMap();
        String query = null;

        Map parameterMap = request.getParameterMap();
        Set keySet;
        if (parameterMap!=null && (keySet=parameterMap.keySet())!=null)
        {
            Iterator iterator = keySet.iterator();
            if (iterator!=null)
            {
                while (iterator.hasNext())
                {
                    String key=(String)iterator.next();
                    String value=request.getParameter(key);

                    if (value != null)
                    {
                        queries.put(key, value);
                    }
                }
            }
        }

        if (queries.size() > 0)
        {
            query = URLHelper.encode(queries);
        }

        return query;
    }

    public String getSwfQueryString(HttpServletRequest request)
    {

        TreeMap directives = new TreeMap();
        String query = null;

        Map parameterMap = request.getParameterMap();
        Set keySet;
        if (parameterMap!=null && (keySet=parameterMap.keySet())!=null)
        {
            Iterator iterator = keySet.iterator();
            if (iterator!=null)
            {
                while (iterator.hasNext())
                {
                    String key=(String)iterator.next();
                    String value=request.getParameter(key);

                    if (CacheKeyUtils.isCompilerDirective(key))
                    {
                        if (value != null)
                        {
                            directives.put(key, value);
                        }
                    }
                }
            }
        }

        if (directives.size() > 0)
        {
            query = URLHelper.encode(directives);
        }

        return query;
    }
    
    public Map<String, String> getFlashVars()
    {
        return flashVarsMap;
    }
    
    public Map<String,String> getFlashVars(HttpServletRequest request)
    {
        TreeMap directives = new TreeMap();
        String query = null;

        Map parameterMap = request.getParameterMap();
        Set keySet;
        if (parameterMap!=null && (keySet=parameterMap.keySet())!=null)
        {
            Iterator iterator = keySet.iterator();
            if (iterator!=null)
            {
                while (iterator.hasNext())
                {
                    String key=(String)iterator.next();
                    String value=request.getParameter(key);

                    if (!CacheKeyUtils.isCompilerDirective(key))
                    {
                        if (value != null)
                        {
                            directives.put(key, value);
                        }
                    }
                }
            }
        }

        return directives;
    }

}
