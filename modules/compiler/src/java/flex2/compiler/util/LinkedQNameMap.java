////////////////////////////////////////////////////////////////////////////////
//
//  ADOBE SYSTEMS INCORPORATED
//  Copyright 2006 Adobe Systems Incorporated
//  All Rights Reserved.
//
//  NOTICE: Adobe permits you to use, modify, and distribute this file
//  in accordance with the terms of the license agreement accompanying it.
//
////////////////////////////////////////////////////////////////////////////////

package flex2.compiler.util;

import java.util.LinkedHashMap;

/**
 * @author Clement Wong
 */
public class LinkedQNameMap<V extends Object> extends LinkedHashMap<QName, V>
{
	private static final long serialVersionUID = 3204095789256503873L;

    public LinkedQNameMap()
	{
		super();
		key = new QName();
	}

	public LinkedQNameMap(int size)
	{
		super(size);
		key = new QName();
	}

	private QName key;

	public boolean containsKey(String ns, String name)
	{
		key.setNamespace(ns);
		key.setLocalPart(name);
		return containsKey(key);
	}

	public V get(String ns, String name)
	{
		key.setNamespace(ns);
		key.setLocalPart(name);
		return get(key);
	}

	public V put(String ns, String name, V value)
	{
		return put(new QName(ns, name), value);
	}
}
