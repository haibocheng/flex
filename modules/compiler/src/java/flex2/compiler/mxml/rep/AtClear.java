////////////////////////////////////////////////////////////////////////////////
//
//  ADOBE SYSTEMS INCORPORATED
//  Copyright 2005-2008 Adobe Systems Incorporated
//  All Rights Reserved.
//
//  NOTICE: Adobe permits you to use, modify, and distribute this file
//  in accordance with the terms of the license agreement accompanying it.
//
////////////////////////////////////////////////////////////////////////////////

package flex2.compiler.mxml.rep;

import flex2.compiler.Source;

/**
 * This class represents an MXML @Clear() use as a 'reset' for state-specific
 * properties.
 */
public class AtClear implements LineNumberMapped
{
	private int lineNumber;

	public AtClear(Source source, int lineNumber)
	{
		this.lineNumber = lineNumber;
	}

	public int getXmlLineNumber()
	{
		return lineNumber;
	}

	public void setXmlLineNumber(int xmlLineNumber)
	{
		this.lineNumber = xmlLineNumber;
	}
}
