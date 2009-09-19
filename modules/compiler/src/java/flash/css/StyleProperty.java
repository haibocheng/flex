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

package flash.css;

public class StyleProperty
{
    private String name;
    private Object value;
    private int lineNumber;

    public StyleProperty(String name, Object value, int lineNumber)
    {
        this.name = name;
        this.value = value;
        this.lineNumber = lineNumber;
    }

    public int getLineNumber()
    {
        return lineNumber;
    }

    public String getName()
    {
        return name;
    }

    /**
     * Can be a String, a flex2.compiler.mxml.rep.AtEmbed, or a
     * flex2.compiler.css.Reference.
     */
    public Object getValue()
    {
        return value;
    }

    public String toString()
    {
        return name + ":" + value + ";";
    }
}
