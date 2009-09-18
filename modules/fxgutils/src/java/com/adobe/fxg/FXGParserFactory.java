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

package com.adobe.fxg;

import com.adobe.internal.fxg.sax.FXGSAXParser;

/**
 * A simple factory to create an instance of an FXGParser implementation.
 */
public class FXGParserFactory
{
    private FXGParserFactory()
    {
    }

    /**
     * Creates a new instance of the default implementation of FXGParser.
     * 
     * @return an FXGParser instance
     * @see com.adobe.fxg.FXGParser 
     */
    public static FXGParser createDefaultParser()
    {
        return new FXGSAXParser();
    }
    
}
