////////////////////////////////////////////////////////////////////////////////
//
//  ADOBE SYSTEMS INCORPORATED
//  Copyright 2005-2007 Adobe Systems Incorporated
//  All Rights Reserved.
//
//  NOTICE: Adobe permits you to use, modify, and distribute this file
//  in accordance with the terms of the license agreement accompanying it.
//
////////////////////////////////////////////////////////////////////////////////
package flex.webtier.server.j2ee;

import flex.webtier.server.j2ee.cache.CacheHelper;
import flex.webtier.server.j2ee.cache.CacheKeyUtils;

/**
 * If the browser content is up-to-date, this filter will return 304 Not-Modified and end the request.
 * Otherwise, the full filter chain is executed.
 */
public class BrowserCacheFilter extends MxmlFilter
{
    public void invoke(MxmlContext context) throws Throwable
    {
        String cacheKey = CacheKeyUtils.getUrlData(context.getRequest()).cacheKey;
        if (CacheHelper.isBrowserUpToDate(context.getRequest(), context.getServletContext(), cacheKey))
        {
            context.getResponse().setStatus(304);
        }
        else
        {
            if (next != null)
            {
                next.invoke(context);
            }
        }
    }
}
