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

package com.adobe.internal.fxg.dom.strokes;

import static com.adobe.fxg.FXGConstants.*;

/**
 * @author Peter Farland
 */
public class SolidColorStrokeNode extends AbstractStrokeNode
{
    //--------------------------------------------------------------------------
    //
    // Attributes
    //
    //--------------------------------------------------------------------------
    
    public int color = COLOR_BLACK; 
    public double alpha = 1.0;

    //--------------------------------------------------------------------------
    //
    // FXGNode Implementation
    //
    //--------------------------------------------------------------------------
    
    /**
     * @return The unqualified name of a SolidColorStroke node, without tag
     * markup.
     */
    public String getNodeName()
    {
        return FXG_SOLIDCOLORSTROKE_ELEMENT;
    }

    @Override
    public void setAttribute(String name, String value)
    {
        if (FXG_COLOR_ATTRIBUTE.equals(name))
            color = parseRGB(value, color);
        else if (FXG_ALPHA_ATTRIBUTE.equals(name))
            alpha = parseDouble(value, ALPHA_MIN_INCLUSIVE, ALPHA_MAX_INCLUSIVE, alpha);
        else
            super.setAttribute(name, value);
    }
}