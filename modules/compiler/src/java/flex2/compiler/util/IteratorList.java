////////////////////////////////////////////////////////////////////////////////
//
//  ADOBE SYSTEMS INCORPORATED
//  Copyright 2005 Adobe Systems Incorporated
//  All Rights Reserved.
//
//  NOTICE: Adobe permits you to use, modify, and distribute this file
//  in accordance with the terms of the license agreement accompanying it.
//
////////////////////////////////////////////////////////////////////////////////

package flex2.compiler.util;

import org.apache.commons.collections.iterators.IteratorChain;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Collections;

/**
 * utility wrapper for IteratorChain, culls empty adds and exploits singletons
 */
public class IteratorList extends ArrayList<Iterator>
{
	private static final long serialVersionUID = -5093248926480065063L;

    public boolean add(Iterator iter)
	{
		if (iter.hasNext())
		{
			return super.add(iter);
		}
        return false;
	}

	public Iterator toIterator()
	{
		switch (size())
		{
            case 0:     return Collections.emptyList().iterator();
			case 1: 	return get(0);
			default: 	return new IteratorChain(this);
		}
	}
}
