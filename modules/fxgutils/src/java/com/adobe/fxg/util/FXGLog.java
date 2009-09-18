////////////////////////////////////////////////////////////////////////////////
//
//  ADOBE SYSTEMS INCORPORATED
//  Copyright 2009 Adobe Systems Incorporated
//  All Rights Reserved.
//
//  NOTICE: Adobe permits you to use, modify, and distribute this file
//  in accordance with the terms of the license agreement accompanying it.
//
////////////////////////////////////////////////////////////////////////////////

package com.adobe.fxg.util;

/**
 * Utility API to get access to the logger for the current thread from anywhere
 * in the library.
 */
public class FXGLog
{
    private static ThreadLocal<FXGLogger> currentLogger = new ThreadLocal<FXGLogger>();

    private FXGLog()
    {
    }

    /**
     * @return The logger for the current thread. 
     */
    public static FXGLogger getLogger()
    {
        FXGLogger logger = currentLogger.get();

        if (logger == null)
        {
            logger = FXGLoggerFactory.createDefaultLogger();
            setLogger(logger);
        }

        return logger;
    }

    /**
     * @return Sets the logger for the current thread. 
     */
    public static void setLogger(FXGLogger value)
    {
        currentLogger.set(value);
    }
    
}
