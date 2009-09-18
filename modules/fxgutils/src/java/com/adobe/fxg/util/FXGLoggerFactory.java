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

import com.adobe.internal.fxg.util.SystemLogger;

/**
 * A simple factory to create an instance of an FXGLogger implementation.
 */
public class FXGLoggerFactory
{
    /**
     * Creates a new instance of the default implementation of FXGLogger.
     * 
     * @return an FXGLogger instance
     * @see com.adobe.fxg.FXGLogger 
     */
    public static FXGLogger createDefaultLogger()
    {
        return new SystemLogger();
    }
}
