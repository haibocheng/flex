////////////////////////////////////////////////////////////////////////////////
//
//  ADOBE SYSTEMS INCORPORATED
//  Copyright 2004-2005 Adobe Systems Incorporated
//  All Rights Reserved.
//
//  NOTICE: Adobe permits you to use, modify, and distribute this file
//  in accordance with the terms of the license agreement accompanying it.
//
////////////////////////////////////////////////////////////////////////////////

package flex2.compiler.mxml.rep;

/**
 * AtEmbed, AtResource, EventHandler, Model, and Script implement this
 * interface, but it's no longer used.
 *
 * @author Paul Reilly
 */
interface LineNumberMapped
{
	public int getXmlLineNumber();
	public void setXmlLineNumber(int xmlLineNumber);
}
