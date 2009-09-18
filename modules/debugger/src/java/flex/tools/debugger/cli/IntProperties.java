////////////////////////////////////////////////////////////////////////////////
//
//  ADOBE SYSTEMS INCORPORATED
//  Copyright 2003-2005 Adobe Systems Incorporated
//  All Rights Reserved.
//
//  NOTICE: Adobe permits you to use, modify, and distribute this file
//  in accordance with the terms of the license agreement accompanying it.
//
////////////////////////////////////////////////////////////////////////////////

package flex.tools.debugger.cli;

import java.util.HashMap;
import java.util.Set;

public class IntProperties
{
	HashMap<String, Integer> m_map = new HashMap<String, Integer>();

	/* getters */
	public Integer					getInteger(String s)	{ return m_map.get(s); }
	public int						get(String s)			{ return getInteger(s).intValue(); }
	public Set<String>				keySet()				{ return m_map.keySet(); }
	public HashMap<String, Integer>	map()					{ return m_map; }

	/* setters */
	public void put(String s, int value)		{ m_map.put(s, new Integer(value)); }

}
