////////////////////////////////////////////////////////////////////////////////
//
//  ADOBE SYSTEMS INCORPORATED
//  Copyright 2007 Adobe Systems Incorporated
//  All Rights Reserved.
//
//  NOTICE: Adobe permits you to use, modify, and distribute this file
//  in accordance with the terms of the license agreement accompanying it.
//
////////////////////////////////////////////////////////////////////////////////

package flash.tools.debugger.expression;

import java.io.IOException;
import java.io.Reader;
import java.text.ParseException;

public interface IASTBuilder
{
	/**
	 * A parser that should do a fairly good job at
	 * parsing a general expression string.
	 * 
	 * Exceptions:
	 *  ParseException - a general parsing error occurred.
	 * 
	 */
	public ValueExp parse(Reader in) throws IOException, ParseException;
}
