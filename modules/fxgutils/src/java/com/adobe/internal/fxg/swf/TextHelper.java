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

package com.adobe.internal.fxg.swf;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Utilities to help create Text.
 * 
 * @author Min Plunkett
 */
public class TextHelper
{
    protected static Pattern whitespacePattern = Pattern.compile ("(\\s+)");
    
	/**
	 * Determine if a string contains only ignorable white spaces.
	 * 
	 * @param value - value to be checked.
	 * @return true if value contains only ignorable white spaces, else, return false.
	 */
	public static boolean ignorableWhitespace(String value)
    {
        Matcher m;

        m = whitespacePattern.matcher(value);
        if (m.matches ())
            return true; 
        else
            return false;
    }
	
}
