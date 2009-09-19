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

package flex2.compiler.mxml;

/**
 * This class represents an attribute of an MXML DOM element. It is used to
 * keep track of the line number for the attribute.
 */
public class Attribute
{
    Attribute(String uri, String localPart, Object value, int line)
    {
        this.uri = uri;
        this.localPart = localPart;
        this.value = value;
        this.line = line;
    }

    private String uri;
    private String localPart;
    private Object value;
    private int line;

    public String getLocalPart()
    {
        return localPart;
    }

    public String getNamespace()
    {
        return uri;
    }

    public Object getValue()
    {
        return value;
    }

    public int getLine()
    {
        return line;
    }
}
