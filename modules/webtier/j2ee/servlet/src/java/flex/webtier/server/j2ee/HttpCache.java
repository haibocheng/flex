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

import flex.webtier.util.HttpConstants;

import javax.servlet.http.HttpServletResponse;

/**
 * handles HTTP cache protocol logic
 *
 * @author Edwin Smith
 */
public class HttpCache
{
    // workaround to indicate no cache
    private static long NO_CACHE = 946080000000L; //Approx Jan 1, 2000

    private static void setNoCacheHeaders(HttpServletResponse response)
	{
		response.setDateHeader("Expires", NO_CACHE);
        response.setDateHeader(HttpConstants.LAST_MODIFIED, System.currentTimeMillis());
        // cmurphy - bug fix 96292: don't use CACHE_CONTROL: no-cache because of IE/SSL/HTTP1.1 bug
		// response.setHeader(HttpConstants.CACHE_CONTROL, "no-cache");
    }

    public static void setCacheHeaders(boolean cacheEnabled, long maxage, long lastModified, HttpServletResponse response)
    {
        if (cacheEnabled) {
            response.setDateHeader(HttpConstants.LAST_MODIFIED, lastModified);
            response.setHeader(HttpConstants.CACHE_CONTROL, "max-age=" + maxage + ", must-revalidate");
            long expires = System.currentTimeMillis();
            // HTTP dates are second granularity
            expires -= expires % 1000;
            expires += maxage *1000;
            response.setDateHeader(HttpConstants.EXPIRES, expires);

            // set Pragma needed for ATG with HTTPS and IE
            response.setHeader("Pragma", "public");
        } else {
            setNoCacheHeaders(response);
        }
    }
}
