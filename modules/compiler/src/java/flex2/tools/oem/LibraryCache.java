////////////////////////////////////////////////////////////////////////////////
//
//  ADOBE SYSTEMS INCORPORATED
//  Copyright 2007 Adobe Systems Incorporated
//  All Rights Reserved.
//
//  NOTICE: Adobe permits you to use, modify, and distribute this file
//  in accordance with the terms of the license agreement accompanying it.
//
////////////////////////////////////////////////////////////////////////////////

package flex2.tools.oem;

import flex2.compiler.swc.SwcCache;
import macromedia.asc.util.ContextStatics;

/**
 * A cache of library files that is narrowly designed to be used to
 * compile Application and Library objects that use the same library
 * path.
 *
 * @since 3.0
 * @author dloverin
 */
public class LibraryCache
{
    private SwcCache swcCache;      // the cache that does all the work.
    private ContextStatics contextStatics;
    
    public LibraryCache()
    {
    }
    
    /**
     * Get the ContextStatics used by the SwcCache.
     *
     * @return the current ContextStatics.
     */
    ContextStatics getContextStatics()
    {
        return contextStatics;
    }

    /**
     * Set the contextStatics to be used with the SwcCache. The
     * reference to the previous contextStatics is overwritten.
     *
     * @param contextStatics the new ContextStatics object.
     */
    void setContextStatics(ContextStatics contextStatics)
    {
        this.contextStatics = contextStatics;
    }

    /**
     * Get the SwcCache current being used by this class.
     * 
     * @return the current SwcCache.
     */
    SwcCache getSwcCache()
    {
        return swcCache;
    }

    /**
     * Set the swcCache to be used by this cache. The reference to the
     * previous cache is overwritten.
     * 
     * @param swcCache the new SwcCache object.
     */
    void setSwcCache(SwcCache swcCache)
    {
        this.swcCache = swcCache;
    }
}
