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

package flex2.compiler.util;

import java.util.HashMap;
import java.util.Map;

/**
 * This class represents a pairing of a local part with a set of
 * namespace URI's.  For example: (["mx.controls", "mx.core"],
 * "Button")
 *
 * @author Clement Wong
 */
public final class MultiName extends Name
{
	private static final String Empty = "".intern();
	private static final String[] EmptyNS = new String[] {Empty};

	private static final Map<String, String[]> nsMap = new HashMap<String, String[]>();

	static
	{
		nsMap.put(Empty, EmptyNS);
	}

	MultiName()
	{
		this(EmptyNS, Empty);
	}

	public MultiName(String qname)
	{
		int index = qname.indexOf(':');
		if (index == -1)
		{
			namespaceURI = EmptyNS;
			localPart = qname;
		}
		else
		{
			String ns = qname.substring(0, index);
			String[] nsSet = nsMap.get(ns);
			if (nsSet == null)
			{
				nsSet = new String[] {ns};
				nsMap.put(ns, nsSet);
			}
			namespaceURI = nsSet;
			localPart = qname.substring(index + 1);
            assert localPart.indexOf(":") < 0;
		}
	}

	public MultiName(final String namespaceURI, final String localPart)
	{
        assert localPart.indexOf(":") < 0;
		String[] nsSet = nsMap.get(namespaceURI);
		if (nsSet == null)
		{
			nsSet = new String[] {namespaceURI};
			nsMap.put(namespaceURI, nsSet);
		}
		this.namespaceURI = nsSet;
		this.localPart = localPart;
	}

	public MultiName(final String[] namespaceURI, final String localPart)
	{
        assert localPart.indexOf(":") < 0;
		this.namespaceURI = namespaceURI;
		this.localPart = localPart;
	}

	public String[] namespaceURI;

	public String[] getNamespace()
	{
		return namespaceURI;
	}

	public boolean equals(Object obj)
	{
		if (obj instanceof MultiName)
		{
			MultiName mName = (MultiName) obj;
			String[] nsURI = mName.namespaceURI;

			if (nsURI.length != namespaceURI.length)
			{
				return false;
			}

			boolean match = false;

			if (nsURI == namespaceURI)
			{
				match = true;
			}
			else if (nsURI.length > 0)
			{
				for (int i = 0, length = namespaceURI.length; i < length; i++)
				{
					if (nsURI[i].equals(namespaceURI[i]))
					{
						match = true;
					}
					else
					{
						match = false;
						break;
					}
				}
			}
			else
			{
				match = true;
			}

			if (match && super.equals(obj))
			{
				return true;
			}
		}

		return false;
	}

    public int getNumQNames()
    {
        return namespaceURI.length;
    }

    public QName getQName( int which )
    {
        return new QName(namespaceURI[which], localPart);
    }

	public int hashCode()
	{
		if (namespaceURI.length > 0)
		{
			int hash = namespaceURI[0].hashCode();
			for (int i = 1, length = namespaceURI.length; i < length; i++)
			{
				hash ^= namespaceURI[i].hashCode();
			}

			return hash ^ super.hashCode();
		}
		else
		{
			return super.hashCode();
		}
	}

	public String toString()
	{
		StringBuilder b = new StringBuilder("[");
		for (int i = 0, length = namespaceURI.length; i < length; i++)
		{
			b.append(namespaceURI[i]);
			if (i < length - 1)
			{
				b.append(", ");
			}
		}
		b.append("]::");
		b.append(localPart);

		return b.toString();
// 		return "multiname(" + localPart + ")";
	}
}