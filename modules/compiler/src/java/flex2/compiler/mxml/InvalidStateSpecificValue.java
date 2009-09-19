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

package flex2.compiler.mxml;

import flex2.compiler.util.CompilerMessage;

public class InvalidStateSpecificValue extends CompilerMessage.CompilerError
{
	private static final long serialVersionUID = 7417396262769478428L;
	public final String name;
    public InvalidStateSpecificValue(String text) { this.name = text; }
}
