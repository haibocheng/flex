////////////////////////////////////////////////////////////////////////////////
//
//  ADOBE SYSTEMS INCORPORATED
//  Copyright 2005-2007 Adobe Systems Incorporated
//  All Rights Reserved.
//
//  NOTICE: Adobe permits you to use, modify, and distribute this file
//  in accordance with the terms of the license agreement accompanying it.
//
////////////////////////////////////////////////////////////////////////////////

package flex2.compiler.util;

import java.util.*;

/**
 * @author Clement Wong
 */
//TODO This class should just extend HashMap, it doesn't need to be an adapter
public class MultiNameMap
{
	public MultiNameMap()
	{
		s = null;
		key = null;
	}

	public MultiNameMap(int size)
	{
		this();
		preferredSize = size;
	}

	private HashMap<MultiName, QName> s;
	private MultiName key;
	private int preferredSize;

	public boolean containsKey(String[] ns, String name)
	{
		if (s == null) return false;

		if (key == null)
		{
			key = new MultiName(ns, name);
		}
		else
		{
			key.namespaceURI = ns;
			key.localPart = name;
		}

		return s.containsKey(key);
	}

	public void putAll(MultiNameMap c)
	{
		if (c.s == null) return;

		if (s == null)
		{
			s = new HashMap<MultiName, QName>(preferredSize);
		}

		s.putAll(c.s);
	}

	public QName put(MultiName key, QName value)
	{
		if (s == null)
		{
			s = new HashMap<MultiName, QName>(preferredSize);
		}

		return s.put(key, value);
	}

	public QName get(MultiName key)
	{
		if (s != null)
		{
			return s.get(key);
		}
		else
		{
			return null;
		}
	}

	public Set<Map.Entry<MultiName, QName>> entrySet()
	{
		if (s != null)
		{
			return s.entrySet();
		}
		else
		{
			return Collections.emptySet();
		}
	}

	public Set<MultiName> keySet()
	{
		if (s != null)
		{
			return s.keySet();
		}
		else
		{
			return Collections.emptySet();
		}
	}

	public Collection<QName> values()
	{
		if (s != null)
		{
			return s.values();
		}
		else
		{
			return Collections.emptySet();
		}
	}

	public int size()
	{
		return s == null ? 0 : s.size();
	}

	public void clear()
	{
		if (s != null)
		{
			s.clear();
		}
	}
	
	public String toString()
	{
		return s == null ? "" : s.toString();
	}
}


