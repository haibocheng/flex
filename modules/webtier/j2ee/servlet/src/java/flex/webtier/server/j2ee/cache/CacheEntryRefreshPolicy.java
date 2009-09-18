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
package flex.webtier.server.j2ee.cache;

import com.opensymphony.oscache.base.Cache;
import com.opensymphony.oscache.base.CacheEntry;
import com.opensymphony.oscache.base.EntryRefreshPolicy;

import flex.webtier.server.j2ee.SWFInfo;
import flex.webtier.server.j2ee.IncrementalCompileFilter;

public class CacheEntryRefreshPolicy implements EntryRefreshPolicy
{
    static final long serialVersionUID = 1526067755123821662L;

    private boolean incremental;
    private String key;
    private CacheHelper ch;
    private Cache cache;

    public void init(CacheHelper ch, Cache cache, String key, boolean incremental)
    {
        this.ch = ch;
        this.cache = cache;
        this.key = key;
        this.incremental = incremental;
    }

    public boolean needsRefresh(CacheEntry entry)
    {
        boolean recompile = false;

        SWFInfo swfInfo = (SWFInfo)entry.getContent();
        if (cache.isFlushed(entry))
        {
            recompile = true;
        }
        else if (ch.needsRecompile(key, swfInfo.getLastModified()))
        {
            if (incremental)
            {
                // the incremental compile requires the target information, which is stored in the cache
                // however, when a cache entry is stale, the entry is not returned and you don't have acces
                // to the target information throw the cache entry. To get around this, we're storing
                // the incremental compile target in the thread local which is then used by the
                // incremental compile filter
                IncrementalCompileFilter.targetThreadLocal.set(swfInfo.getTarget());
            }
            recompile = true;
        }
        else
        {
            recompile = false;
        }

        return recompile;
    }
}
