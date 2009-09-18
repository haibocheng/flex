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

import java.io.PrintStream;


/**
 * A simple logger that traces messages out to System.out. 
 */
public class SystemLogger extends AbstractLogger
{
    public SystemLogger()
    {
        super(NONE);
    }

    public void log(int level, Object message, Throwable t, String location, int line, int column)
    {
        if (level < getLevel())
            return;

        PrintStream ps = level > INFO ? System.err : System.out;

        try
        {
            StringBuilder sb = new StringBuilder();

            if (location != null)
            {
                sb.append(location).append(":");
            }

            if (line > 0)
            {
                sb.append(" Line ").append(line);

                if (column > 0)
                {
                    sb.append(", Column ").append(column);
                }
            }

            if (message != null)
            {
                sb.append(": ").append(message);
            }

            if (t != null)
            {
                sb.append(": ").append(t.getLocalizedMessage());
            }

            ps.println(sb.toString());
        }
        catch (Throwable ex)
        {
        }
    }
}
