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

package flex2.compiler.mxml.dom;

import flex2.compiler.mxml.lang.StandardDefs;
import flex2.compiler.util.QName;

import java.util.HashSet;
import java.util.Set;

/**
 * @author Paul Reilly
 */
public class VectorNode extends Node
{
	public static final Set<QName> attributes;

	static
	{
		attributes = new HashSet<QName>();
		attributes.add(new QName("", StandardDefs.PROP_ID));
		attributes.add(new QName("", StandardDefs.PROP_TYPE));
		attributes.add(new QName("", StandardDefs.PROP_FIXED));
		attributes.add(new QName("", StandardDefs.PROP_INCLUDE_STATES));
		attributes.add(new QName("", StandardDefs.PROP_EXCLUDE_STATES));
		attributes.add(new QName("", StandardDefs.PROP_ITEM_CREATION_POLICY));
		attributes.add(new QName("", StandardDefs.PROP_ITEM_DESTRUCTION_POLICY));
	}

	VectorNode(String uri, String localName, int size)
	{
		super(uri, localName, size);
	}

	public void analyze(Analyzer analyzer)
	{
		analyzer.prepare(this);
		analyzer.analyze(this);
	}
}
