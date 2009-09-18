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

package com.adobe.internal.fxg.dom;

import static com.adobe.fxg.FXGConstants.*;
import com.adobe.fxg.FXGException;
import com.adobe.internal.fxg.dom.types.Winding;

/**
 * @author Peter Farland
 */
public class PathNode extends AbstractShapeNode
{
    //--------------------------------------------------------------------------
    //
    // Attributes
    //
    //--------------------------------------------------------------------------

    public Winding winding = Winding.EVEN_ODD;
    public String data = "";

    //--------------------------------------------------------------------------
    //
    // FXGNode Implementation
    //
    //--------------------------------------------------------------------------

    @Override
    public void setAttribute(String name, String value)
    {
        if (FXG_WINDING_ATTRIBUTE.equals(name))
            winding = getWinding(value);
        else if (FXG_DATA_ATTRIBUTE.equals(name))
            data = value;
        else
            super.setAttribute(name, value);
    }

    /**
     * @return The unqualified name of a Path node, without tag markup.
     */
    public String getNodeName()
    {
        return FXG_PATH_ELEMENT;
    }

    //--------------------------------------------------------------------------
    //
    // Helper Methods
    //
    //--------------------------------------------------------------------------
    
    protected Winding getWinding(String value)
    {
        if (FXG_WINDING_EVENODD_VALUE.equals(value))
            return Winding.EVEN_ODD;
        else if (FXG_WINDING_NONZERO_VALUE.equals(value))
            return Winding.NON_ZERO;
        else
        	//Exception:Unknown winding value: {0}.
            throw new FXGException(getStartLine(), getStartColumn(), "UnknownWindingValue", value);
    }
}
