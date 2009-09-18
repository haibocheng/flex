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

public class RecompileFilter extends MxmlFilter
{
    public void invoke(MxmlContext context) throws Throwable
    {
        CacheHelper ch = CacheHelper.getInstance(context.getServletContext());

        if (ch.forceRecompile(context.getRequest()))
        {
            String cacheKey = context.getCacheKey();
            ch.removeFromCache(cacheKey);
        }

        if (next != null)
        {
            next.invoke(context);
        }
    }
}
