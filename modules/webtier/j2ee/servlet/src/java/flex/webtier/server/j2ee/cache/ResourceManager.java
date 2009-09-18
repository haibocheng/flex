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
import java.io.File;

public class ResourceManager
{
    public static long checkInterval = 1000; // 1 second
    private LRUCache resourceCache = new ResourceLRUCache(256, 8096);

    public Resource getResource(String path)
    {
        return (Resource)resourceCache.get(path);
    }

    static class ResourceLRUCache extends LRUCache
    {
        static final long serialVersionUID = 5940200644671535951L;

        public ResourceLRUCache(int initialSize, int maxSize)
        {
            super(initialSize, maxSize);
        }

        protected Object fetch(Object key)
        {
            String path = (String) key;

            // Create a new File
            File file = new File(path);
            return new Resource(path, file);
        }
    }
}

