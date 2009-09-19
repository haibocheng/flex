////////////////////////////////////////////////////////////////////////////////
//
//  ADOBE SYSTEMS INCORPORATED
//  Copyright 2004-2006 Adobe Systems Incorporated
//  All Rights Reserved.
//
//  NOTICE: Adobe permits you to use, modify, and distribute this file
//  in accordance with the terms of the license agreement accompanying it.
//
////////////////////////////////////////////////////////////////////////////////

package flex2.compiler;

/**
 * @author Clement Wong
 */
public class CompilerException extends Exception
{
    private static final long serialVersionUID = 1587688606009074835L;

    public CompilerException()
    {
    }

	public CompilerException(String message)
	{
		super(message);
	}

    public CompilerException(String message, Throwable cause)
    {
        super(message, cause);
    }

    public CompilerException(Throwable cause)
    {
        super(cause);
    }
}
