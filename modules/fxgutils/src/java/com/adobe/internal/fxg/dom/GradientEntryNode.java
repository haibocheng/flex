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
public class GradientEntryNode extends AbstractFXGNode
{
    private static final double RATIO_MIN_INCLUSIVE = 0.0;
    private static final double RATIO_MAX_INCLUSIVE = 1.0;

    //--------------------------------------------------------------------------
    //
    // Attributes
    //
    //--------------------------------------------------------------------------

    public int color = COLOR_BLACK;
    public double alpha = 1.0;
    public double ratio = Double.NaN;

    //--------------------------------------------------------------------------
    //
    // FXGNode Implementation
    //
    //--------------------------------------------------------------------------

    /**
     * @return The unqualified name of a GradientEntry node, without tag markup.
     */
    public String getNodeName()
    {
        return FXG_GRADIENTENTRY_ELEMENT;
    }

    @Override
    public void setAttribute(String name, String value)
    {
        if (FXG_COLOR_ATTRIBUTE.equals(name))
            color = DOMParserHelper.parseRGB(this, value, name);
        else if (FXG_ALPHA_ATTRIBUTE.equals(name))
            alpha = DOMParserHelper.parseDouble(this, value, name, ALPHA_MIN_INCLUSIVE, ALPHA_MAX_INCLUSIVE, alpha);
        else if (FXG_RATIO_ATTRIBUTE.equals(name))
            ratio = DOMParserHelper.parseDouble(this, value, name, RATIO_MIN_INCLUSIVE, RATIO_MAX_INCLUSIVE, ratio);
        else
            super.setAttribute(name, value);
    }
}
