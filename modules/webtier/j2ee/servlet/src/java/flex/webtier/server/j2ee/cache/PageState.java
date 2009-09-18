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

import flex.webtier.util.Trace;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * All generated files will use the same key which is the mxml file name.
 *
 */
public class PageState implements Serializable
{
    static final long serialVersionUID = 6632526751227714786L;

    private Map dependencies = Collections.synchronizedMap(new HashMap());

    /**
     * the key that owns this PageState.  used for troubleshooting only
     */
    private String key;

    private long lastModified;

    private transient ResourceManager resourceManager;


    /**
     * Until dependencies are set, this page will always be fresh.
     *
     * This is useful in the case that an mxml page is in compilation.
     * The swf will wait for the mxml page to compile if it knows it's fresh.
     */
    public PageState(String key, HashMap dependencies, long lastModified, ResourceManager resourceManager)
    {
        this.key = key;
        this.lastModified = lastModified;
        this.resourceManager = resourceManager;
        this.dependencies = dependencies;
    }

    public void setResourceManager(ResourceManager resourceManager)
    {
        this.resourceManager = resourceManager;
    }

    public boolean isUpToDate()
    {
        assert resourceManager != null : "resourceManager must be set";

        boolean uptodate = true;
        Set entries = dependencies.entrySet();
        Iterator iter = entries.iterator();
        while (iter.hasNext() && uptodate)
        {
            Map.Entry entry = (Map.Entry)iter.next();

            Resource resource = resourceManager.getResource((String)entry.getKey());
            if (resource != null)
            {
                try
                {
                    long last = ((Long)entry.getValue()).longValue();
                    long current = calculateLastModified(resource.getLastModified());
                    // since the resource manager only test every second
                    if (Math.abs(current - last) > 1000)
                    {
                        if (Trace.cache)
                        {
                            Trace.trace("[cache] file modified = " + entry.getKey() + "; last = " + last + "; current = " + current);
                        }
                        uptodate = false;
                    }
                }
                catch (InvalidResourceException ire)
                {
                    if (Trace.cache)
                    {
                        Trace.trace("[cache] invalid resource exception = " + entry.getKey());
                    }
                    uptodate = false;
                }
            }
            else
            {
                if (Trace.cache)
                {
                    Trace.trace("[cache] invalid resource = " + entry.getKey());
                }
                uptodate = false;
            }
        }
        return uptodate;
    }

    private long calculateLastModified(long lastModified)
    {
        lastModified -= (lastModified % 1000);
        return lastModified;
    }

    public long getLastModified()
    {
        return lastModified;
    }
}

