////////////////////////////////////////////////////////////////////////////////
//
//  ADOBE SYSTEMS INCORPORATED
//  Copyright 2004-2007 Adobe Systems Incorporated
//  All Rights Reserved.
//
//  NOTICE: Adobe permits you to use, modify, and distribute this file
//  in accordance with the terms of the license agreement accompanying it.
//
////////////////////////////////////////////////////////////////////////////////
package flex.webtier.compiler;

/**
 * This exception is used by MxmlBaseServlet's checkServletPath().
 *
 * @author Paul Reilly
 */
public class WrongCaseException extends Exception
{
    static final long serialVersionUID = 3545834001617590316L;

    public WrongCaseException(String message)
    {
        super(message);
    }
}

