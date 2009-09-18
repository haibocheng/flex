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

/**
 * @author Peter Farland
 */
public class EllipseNode extends AbstractShapeNode
{
    //--------------------------------------------------------------------------
    //
    // Attributes
    //
    //--------------------------------------------------------------------------

    public double width = 0.0;
    public double height = 0.0;

    //--------------------------------------------------------------------------
    //
    // FXGNode Implementation
    //
    //--------------------------------------------------------------------------

    /**
     * @return The unqualified name of an Ellipse node, without tag markup.
     */
    public String getNodeName()
    {
        return FXG_ELLIPSE_ELEMENT;
    }

    @Override
    public void setAttribute(String name, String value)
    {
        if (FXG_WIDTH_ATTRIBUTE.equals(name))
            width = parseDouble(value);
        else if (FXG_HEIGHT_ATTRIBUTE.equals(name))
            height = parseDouble(value);
        else
            super.setAttribute(name, value);
    }
}
