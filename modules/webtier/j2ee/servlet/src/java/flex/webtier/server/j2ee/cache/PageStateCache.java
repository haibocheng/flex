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
package flex.webtier.server.j2ee.cache;

import flash.util.LRUCache;
import flex.webtier.util.Trace;

import java.util.Map;
import java.util.Iterator;
import java.util.HashMap;



// C: Use LRUCache for PageState instances. Some MXML code fragments from JSP are dynamic.
//    Without limiting the size, the compiler will run out of memory.
public class PageStateCache extends LRUCache
{
    static final long serialVersionUID = 3535777947388015185L;

    PageStateCache(int initial, int max)
    {
        super(initial, max, 1);
    }

    synchronized Map getPageStates()
    {
        Map map = new HashMap();
        for (Iterator i = entrySet().iterator();i.hasNext();)
        {
            Map.Entry e = (Map.Entry) i.next();
            Object key = e.getKey();
            // C: Get the value this way so this doesn't affect the LRU logic. Argh!
            LRUListEntry value = (LRUListEntry) e.getValue();
            if (value != null)
            {
                map.put(key, value.getValue());
            }
        }
        return map;
    }

	protected Object fetch(Object key)
	{
	    return null;
    }

    protected void handleLRUElementPurged(Object key, Object value)
    {
        PageState pageState = (PageState) value;
        if (pageState != null)
        {
            if (Trace.dependency)
			{
                // cmurphy - temporary remove
			    // Trace.trace("DependencyChecker: Reached the mxml-size limit (" + size + "). Purged " + key);
            }
            // cmurphy - temporary remove
			// pageState.destroy();
        }
    }
}
