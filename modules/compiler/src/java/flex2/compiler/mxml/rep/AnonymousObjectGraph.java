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

import flex2.compiler.mxml.reflect.Type;

/**
 * This class represents a set of name/value pairs defined as Mxml
 * nodes.  For example:
 * <pre>
 * &lt;Object&gt;
 *   &lt;a&gt;b&lt;/a&gt;
 *   &lt;c&gt;d&lt;/c&gt;
 * &lt;/Object&gt;
 * </pre>
 */
public class AnonymousObjectGraph extends Model
{
	public AnonymousObjectGraph(MxmlDocument document, Type objectType, int line)
	{
		super(document, objectType, line);
	}

}
