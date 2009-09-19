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

package flex2.compiler.mxml.dom;

import java.util.HashSet;
import java.util.Set;

import flex2.compiler.mxml.lang.StandardDefs;
import flex2.compiler.util.QName;

/**
 * @author Paul Reilly
 */
public class DesignLayerNode extends LayeredNode
{
	public static final Set<QName> attributes;

	static
	{
		attributes = new HashSet<QName>();
		attributes.add(new QName("", StandardDefs.PROP_ID));
		attributes.add(new QName("", "visible"));
		attributes.add(new QName("", "alpha"));
	}

	DesignLayerNode(String uri, String localName, int size, DesignLayerNode parent)
	{
		super(uri, localName, size, parent);
	}

	public void analyze(Analyzer analyzer)
	{
		analyzer.prepare(this);
		analyzer.analyze(this);
	}
}
