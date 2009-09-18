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

/**
 * A simple interface to report information while processing an FXG docuemnt.
 */
public interface FXGLogger
{
    public static final int ALL = 0;
    public static final int DEBUG = 10000;
    public static final int INFO = 20000;
    public static final int WARN = 30000;
    public static final int ERROR = 40000;
    public static final int NONE = Integer.MAX_VALUE;

    int getLevel();
    void setLevel(int level);

    void debug(Object message);
    void debug(Object message, Throwable t);
    void debug(Object message, Throwable t, String location, int line, int column);

    void error(Object message);
    void error(Object message, Throwable t);
    void error(Object message, Throwable t, String location, int line, int column);

    void info(Object message);
    void info(Object message, Throwable t);
    void info(Object message, Throwable t, String location, int line, int column);

    void log(int level, Object message);
    void log(int level, Object message, Throwable t);
    void log(int level, Object message, Throwable t, String location, int line, int column);

    void warn(Object message);
    void warn(Object message, Throwable t);
    void warn(Object message, Throwable t, String location, int line, int column);
}
