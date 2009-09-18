////////////////////////////////////////////////////////////////////////////////
//
//  ADOBE SYSTEMS INCORPORATED
//  Copyright 2003-2007 Adobe Systems Incorporated
//  All Rights Reserved.
//
//  NOTICE: Adobe permits you to use, modify, and distribute this file
//  in accordance with the terms of the license agreement accompanying it.
//
////////////////////////////////////////////////////////////////////////////////
package flex.webtier.util;

import java.io.PrintStream;
import java.io.PrintWriter;

public class ChainedException extends Exception
{
    static final long serialVersionUID = -4540367574502630178L;

    private Throwable cause = null;

    public ChainedException()
    {
        super();
    }

    public ChainedException(Throwable cause)
    {
        super();
        this.cause = cause;
    }

    public ChainedException(String message)
    {
        super(message);
    }

    public ChainedException(String message, Throwable cause)
    {
        super(message);
        this.cause = cause;
    }

    public Throwable getCause()
    {
        return cause;
    }

    public void printStackTrace()
    {
        super.printStackTrace();
        if (cause != null)
        {
            System.err.println("Caused by:");
            cause.printStackTrace();
        }
    }

    public void printStackTrace(PrintStream ps)
    {
        super.printStackTrace(ps);
        if (cause != null)
        {
            ps.println("Caused by:");
            cause.printStackTrace(ps);
        }
    }

    public void printStackTrace(PrintWriter pw)
    {
        super.printStackTrace(pw);
        if (cause != null)
        {
            pw.println("Caused by:");
            cause.printStackTrace(pw);
        }
    }
}
