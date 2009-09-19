////////////////////////////////////////////////////////////////////////////////
//
//  ADOBE SYSTEMS INCORPORATED
//  Copyright 2009 Adobe Systems Incorporated
//  All Rights Reserved.
//
//  NOTICE: Adobe permits you to use, modify, and distribute this file
//  in accordance with the terms of the license agreement accompanying it.
//
////////////////////////////////////////////////////////////////////////////////

package flex2.compiler.css;

/**
 * This class represents a ClassReference() or PropertyReference() CSS
 * function.  It is used as a flash.css.StyleProperty value.
 */
public class Reference
{
    private String value;
    private boolean isClassReference;
    private String path;
    private int lineNumber;

    public Reference(String value, String path, int lineNumber)
    {
        this.value = value;
        this.path = path;
        this.lineNumber = lineNumber;
    }

    public int getLineNumber()
    {
        return lineNumber;
    }

    public String getPath()
    {
        return path;
    }

    public String getValue()
    {
        return value;
    }

    public boolean isClassReference()
    {
        return isClassReference;
    }

    public void setClassReference(boolean isClassReference)
    {
        this.isClassReference = isClassReference;
    }

    /**
     * Used by Velocity templates, so that we don't have to test for
     * instances of Reference and if so, call getValue().
     */
    public String toString()
    {
        return value;
    }
}
