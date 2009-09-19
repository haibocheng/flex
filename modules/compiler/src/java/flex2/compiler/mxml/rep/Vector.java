////////////////////////////////////////////////////////////////////////////////
//
//  ADOBE SYSTEMS INCORPORATED
//  Copyright 2004-2006 Adobe Systems Incorporated
//  All Rights Reserved.
//
//  NOTICE: Adobe permits you to use, modify, and distribute this file
//  in accordance with the terms of the license agreement accompanying it.
//
////////////////////////////////////////////////////////////////////////////////

package flex2.compiler.mxml.rep;

import flex2.compiler.mxml.reflect.Type;
import flex2.compiler.util.NameFormatter;

/**
 *
 */
public class Vector extends Array
{
    private boolean fixed;

    public Vector(MxmlDocument document, int line, Type elementType, boolean fixed)
    {
        this(document, null, line, elementType, fixed);
    }

    public Vector(MxmlDocument document, Model parent, int line,
                  Type elementType, boolean fixed)
    {
        super(document, document.getTypeTable().getVectorType(elementType), parent, line, elementType);
        this.fixed = fixed;
    }

    public String getElementTypeName()
    {
        return NameFormatter.toDot(elementType.getName());
    }

    public boolean isFixed()
    {
        return fixed;
    }
}
