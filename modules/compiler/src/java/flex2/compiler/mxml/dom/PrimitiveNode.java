////////////////////////////////////////////////////////////////////////////////
//
//  ADOBE SYSTEMS INCORPORATED
//  Copyright 2005-2006 Adobe Systems Incorporated
//  All Rights Reserved.
//
//  NOTICE: Adobe permits you to use, modify, and distribute this file
//  in accordance with the terms of the license agreement accompanying it.
//
////////////////////////////////////////////////////////////////////////////////

package flex2.compiler.mxml.dom;

import flex2.compiler.mxml.Token;
import flex2.compiler.mxml.lang.StandardDefs;
import flex2.compiler.util.QName;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author Clement Wong
 */
public abstract class PrimitiveNode extends Node
{
	public static final Set<QName> attributes;

	static
	{
		attributes = new HashSet<QName>();
		attributes.add(new QName("", StandardDefs.PROP_ID));
		attributes.add(new QName("", StandardDefs.PROP_SOURCE));
		attributes.add(new QName("", StandardDefs.PROP_INCLUDE_STATES));
		attributes.add(new QName("", StandardDefs.PROP_EXCLUDE_STATES));
		attributes.add(new QName("", StandardDefs.PROP_ITEM_CREATION_POLICY));
		attributes.add(new QName("", StandardDefs.PROP_ITEM_DESTRUCTION_POLICY));
	}

	PrimitiveNode(String uri, String localName, int size)
	{
		super(uri, localName, size);
	}

	private CDATANode sourceFile;

	public void setSourceFile(CDATANode cdata)
	{
		sourceFile = cdata;
	}

	public CDATANode getSourceFile()
	{
		return sourceFile;
	}

	public int getChildCount()
	{
		return sourceFile != null ? 1 : super.getChildCount();
	}

	public Token getChildAt(int index)
	{
		return sourceFile != null && index == 0 ? sourceFile : super.getChildAt(index);
	}

	public List<Token> getChildren()
	{
		return sourceFile != null ? Collections.<Token>singletonList(sourceFile) : super.getChildren();
	}
}
