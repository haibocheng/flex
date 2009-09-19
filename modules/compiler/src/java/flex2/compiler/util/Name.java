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

/**
 * A common base class for MultiName and QName to enable putting them
 * in Map's and Set's together.
 *
 * @author Paul Reilly
 */
public abstract class Name
{
	public String localPart;

	public String getLocalPart()
	{
		return localPart;
	}

	public void setLocalPart(String localPart)
	{
        assert localPart.indexOf(":") < 0;
		this.localPart = localPart;
	}

    public boolean equals(Object object)
    {
        boolean result = false;

        if (object instanceof Name)
        {
            result = localPart.equals(((Name) object).localPart);
        }

        return result;
    }

    public int hashCode()
    {
        return localPart.hashCode();
    }
}
