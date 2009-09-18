////////////////////////////////////////////////////////////////////////////////
//
//  ADOBE SYSTEMS INCORPORATED
//  Copyright 2004-2007 Adobe Systems Incorporated
//  All Rights Reserved.
//
//  NOTICE: Adobe permits you to use, modify, and distribute this file
//  in accordance with the terms of the license agreement accompanying it.
//
////////////////////////////////////////////////////////////////////////////////
package flex.webtier.server.j2ee.cache;

import flex.webtier.server.j2ee.FileNameGenerator;
import flex.webtier.server.j2ee.URLHelper;
import flex.webtier.services.ServiceFactory;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import javax.servlet.http.HttpServletRequest;

public class CacheKeyUtils {

    private static String asExt;
    private static String asSwfExt;
    private static String mxmlExt;
    private static String mxmlSwdExt;
    private static String mxmlSwfExt;

    public static String generateGenericCacheKey(HttpServletRequest request, UrlData data)
    {
        StringBuffer key = new StringBuffer("//");

        // use the whole url because web service proxy url depends on the whole thing
        String uri = getRequestURL(request);
        uri = parseUri(uri, data);
        key.append(uri);

        return key.toString();
    }


    public static String generateSwfCacheKey(HttpServletRequest request, UrlData data)
    {
        StringBuffer key = new StringBuffer("//");

        // use the whole url because web service proxy url depends on the whole thing
        String uri = getRequestURL(request);
        uri = parseUri(uri, data);
        
        // If the mxml.swf.extension has been disabled and "jsp" prefix was  
        // added before, remove it, so that the right key is generated. 
        if (!ServiceFactory.isMxmlSwfExtension())
        {                        
            int lastSlashIndex = uri.lastIndexOf('/');
            if (lastSlashIndex != -1)
            {
                String base = uri.substring(lastSlashIndex + 1);            
                if (base.length() >= 32 && base.startsWith("jsp"))
                    uri = uri.replaceFirst(base, base.substring(3));
            }
        }
                        
        key.append(FileNameGenerator.swfFileName(uri));
        appendCompilerDirectives(key, request.getQueryString());

        return key.toString();
    }

    public static String generateJspCacheKey(HttpServletRequest request, String sourceKey)
    {
        if (mxmlSwfExt == null) 
        {
            if (ServiceFactory.isMxmlSwfExtension())
                mxmlSwfExt = ServiceFactory.getExtensionManager().getMxmlSwfExt();
            else
                mxmlSwfExt = ServiceFactory.getExtensionManager().getSwfExt();
        }
        
        String requestUrl = getRequestURL(request);
        StringBuffer key = new StringBuffer("//");
        key.append(requestUrl.substring(0, requestUrl.lastIndexOf('/') + 1));
        key.append(sourceKey);
        key.append(mxmlSwfExt);
        appendCompilerDirectives(key, request.getQueryString());

        return key.toString();
    }

    public static String generateJspExternalCacheKey(HttpServletRequest request, String sourceKey, String queryString)
    {
        if (mxmlSwfExt == null) 
        {
            if (ServiceFactory.isMxmlSwfExtension())
                mxmlSwfExt = ServiceFactory.getExtensionManager().getMxmlSwfExt();
            else
                mxmlSwfExt = ServiceFactory.getExtensionManager().getSwfExt();            
        }
        
        String requestUrl =  getRequestURL(request);
        StringBuffer key = new StringBuffer("//");

        // strip off after the context path
        int contextPathEnd = requestUrl.indexOf(request.getContextPath()) + request.getContextPath().length();
        key.append(requestUrl.substring(0, contextPathEnd));

        // sourceKey starts with '/'
        key.append(sourceKey);
        key.append(mxmlSwfExt);
        appendCompilerDirectives(key, queryString);

        return key.toString();
    }

    public static StringBuffer appendCompilerDirectives(StringBuffer url, String queryString)
    {
        // cache key includes the query string with only compiler directives
        if (queryString != null) {
            Map params = URLHelper.getParameterMap(queryString);
            Set entries = params.entrySet();
            Iterator iter = entries.iterator();
            while (iter.hasNext()) {
               Map.Entry entry = (Map.Entry)iter.next();
                if (!isCompilerDirective((String)entry.getKey())) {
                    iter.remove();
                }
            }
            String query = URLHelper.encode(params);
            if (query != null) {
                if (url.indexOf("?") == -1)
                {
                    url.append("?");
                }
                else
                {
                    url.append("&");
                }
                url.append(query);
            }
        }

        return url;
    }


    private static String parseUri(String uri, UrlData data)
    {
        String newUri = uri;
        String sessionPathMatch = ";JSESSIONID=";
        int index = uri.indexOf(sessionPathMatch);
        if (index == -1) {

            sessionPathMatch = ";jsessionid=";
            index = uri.indexOf(sessionPathMatch);
        }
        if (index != -1) {
            String id = uri.substring(index + sessionPathMatch.length());
            newUri = uri.substring(0, index);

            // See if there are any more path parameters
            index = id.indexOf(';');
            if (index < 0) index = id.length();

            // Now get the session ID
            if (data != null) {
                data.sessionId = id.substring(0, index);
            }
        }
        return newUri;
    }

    public static UrlData getUrlData(HttpServletRequest request)
    {
        if (mxmlSwdExt == null) {
            mxmlSwdExt = ServiceFactory.getExtensionManager().getMxmlSwdExt();
        }
        if (mxmlSwfExt == null) 
        {
            if (ServiceFactory.isMxmlSwfExtension())
                mxmlSwfExt = ServiceFactory.getExtensionManager().getMxmlSwfExt();
            else
                mxmlSwfExt = ServiceFactory.getExtensionManager().getSwfExt();         
        }
        if (mxmlExt == null) {
            mxmlExt = ServiceFactory.getExtensionManager().getMxmlExt();
        }
        if (asExt == null) {
            asExt = ServiceFactory.getExtensionManager().getAsExt();
        }
        if (asSwfExt == null) {
            asSwfExt = ServiceFactory.getExtensionManager().getAsSwfExt();
        }


        UrlData data = new UrlData();

        // use the whole url because web service proxy url depends on the whole thing
        String url =  getRequestURL(request);

        data.isSwd = url.indexOf(mxmlSwdExt) != -1;
        if ( (url.indexOf(mxmlSwfExt) != -1) || (url.indexOf(asSwfExt) != -1) || data.isSwd ) {
            data.cacheKey = generateSwfCacheKey(request, data);
        } else if ( (url.indexOf(mxmlExt) != -1) || (url.indexOf(asExt) != -1) ) {
            data.cacheKey = generateSwfCacheKey(request, data);
        } else {
            // *.swf (precompile), *.swd (precompile)
            data.cacheKey = generateGenericCacheKey(request, data);
        }

        return data;
    }

    /** each of the compiler directives will change the actual swf created
     * For example, with debug or asprofile information, the swf contains additional
     * debug or profile information.
     */
    public static boolean isCompilerDirective(String key) {
        boolean directive = false;
        if ((key != null) && (key.equals("accessible") || key.equals("debug") || key.equals("asprofile") ||
                key.equals("verboseStacktraces") || key.equals("networkCapturePort"))) {
            directive = true;
        }

        return directive;
    }
    
    /**
     * A helper method that extracts the request url from a request. In some 
     * environments, such as portlets in JBoss, request.getRequestURL() returns 
     * null due to different interpretation of portal spec. In that case, we 
     * attempt to construct requestURL from the request ourselves. 
     * 
     * @param request
     * @return
     */
    private static String getRequestURL(HttpServletRequest request)
    {
        StringBuffer requestURL = request.getRequestURL();        
        if (requestURL == null)
        {
            requestURL = new StringBuffer(request.getScheme() + "://"
                    + request.getServerName() + ":" + request.getServerPort()
                    + request.getRequestURI());
        }
        return requestURL != null? requestURL.toString() : null;
    }

    public static class UrlData
    {
        public String cacheKey;
        public String sessionId;
        public boolean isSwd = false;
    }

}
