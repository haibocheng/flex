////////////////////////////////////////////////////////////////////////////////
//
//  ADOBE SYSTEMS INCORPORATED
//  Copyright 2008 Adobe Systems Incorporated
//  All Rights Reserved.
//
//  NOTICE: Adobe permits you to use, modify, and distribute this file
//  in accordance with the terms of the license agreement accompanying it.
//
////////////////////////////////////////////////////////////////////////////////

package com.adobe.fxg.util;

import com.adobe.internal.fxg.util.FileResolver;

/**
 * A simple factory used to create an instance of a ResourceResolver
 * implementation.
 */
public class FXGResourceResolverFactory
{
    /**
     * Creates a new instance of the default implementation of FXGParser.
     * 
     * @return an FXGParser instance
     * @see com.adobe.fxg.FXGParser 
     */
    public static FXGResourceResolver createDefaultResourceResolver()
    {
        return new FileResolver();
    }
}
