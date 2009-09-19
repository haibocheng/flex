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

package flex2.compiler.as3.reflect;

import macromedia.asc.parser.LiteralStringNode;
import macromedia.asc.parser.NamespaceDefinitionNode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import flex2.compiler.abc.MetaData;

/**
 * @author Clement Wong
 */
final class Namespace implements flex2.compiler.abc.Namespace
{
	Namespace(NamespaceDefinitionNode node)
	{
		name = node.name.name;
		value = (node.value instanceof LiteralStringNode) ? ((LiteralStringNode) node.value).value : null;

		if (node.attrs != null)
		{
			attributes = new Attributes(node.attrs);
		}

		this.metadata = TypeTable.createMetaData(node);
	}

	private String name;
	private String value;
	private Attributes attributes;
	private List<MetaData> metadata;

	public String getName()
	{
		return name;
	}

	public String getValue()
	{
		return value;
	}

	public flex2.compiler.abc.Attributes getAttributes()
	{
		return attributes;
	}

	public List<MetaData> getMetaData()
	{
		return metadata != null ? metadata : Collections.<MetaData>emptyList();
	}

	public List<MetaData> getMetaData(String id)
	{
		if (metadata == null)
		{
			return null;
		}
		else
		{
			List<MetaData> list = null;
			for (int i = 0, length = metadata.size(); i < length; i++)
			{
				if (id.equals((metadata.get(i)).getID()))
				{
					if (list == null)
					{
						list = new ArrayList<MetaData>();
					}
					list.add(metadata.get(i));
				}
			}
			return list;
		}
	}
}
