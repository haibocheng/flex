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

package com.adobe.internal.fxg.util;

import com.adobe.fxg.util.FXGLogger;

/**
 * An abstract FXGLogger implementation to redirect the various utility
 * logging methods to the core log() API:
 * <pre>
 * void log(int level, Object message, Throwable t, String location, int line, int column);
 * <pre> 
 */
public abstract class AbstractLogger implements FXGLogger
{
    protected int level;

    protected AbstractLogger(int level)
    {
        this.level = level;
    }

    public void debug(Object message)
    {
        log(DEBUG, message);
    }

    public void debug(Object message, Throwable t)
    {
        log(DEBUG, message, t);
    }

    public void debug(Object message, Throwable t, String location, int line, int column)
    {
        log(DEBUG, message, t, location, line, column);
    }

    public void error(Object message)
    {
        log(ERROR, message);
    }

    public void error(Object message, Throwable t)
    {
        log(ERROR, message, t);
    }

    public void error(Object message, Throwable t, String location, int line, int column)
    {
        log(ERROR, message, t, location, line, column);
    }
    
    public void info(Object message)
    {
        log(INFO, message);
    }

    public void info(Object message, Throwable t)
    {
        log(INFO, message, t);
    }

    public void info(Object message, Throwable t, String location, int line, int column)
    {
        log(INFO, message, t, location, line, column);
    }
    
    public int getLevel()
    {
        return level;
    }

    public void setLevel(int value)
    {
        level = value;
    }

    public void log(int level, Object message)
    {
        log(level, message, null);
    }

    public void log(int level, Object message, Throwable t)
    {
        log(level, message, t, null, 0, 0);
    }

    public void warn(Object message)
    {
        log(WARN, message);
    }

    public void warn(Object message, Throwable t)
    {
        log(WARN, message, t);
    }

    public void warn(Object message, Throwable t, String location, int line, int column)
    {
        log(WARN, message, t, location, line, column);
    }
}
