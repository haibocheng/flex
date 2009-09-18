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

package com.adobe.internal.fxg.dom.filters;

import static com.adobe.fxg.FXGConstants.*;

/**
 * @author Peter Farland
 */
public class GlowFilterNode extends AbstractFilterNode
{
    //--------------------------------------------------------------------------
    //
    // Attributes
    //
    //--------------------------------------------------------------------------

    public double alpha = 1.0;
    public double blurX = 4.0;
    public double blurY = 4.0;
    public int color = COLOR_RED;
    public boolean inner = false;
    public boolean knockout = false;
    public int quality = 1;
    public double strength = 1.0;

    //--------------------------------------------------------------------------
    //
    // FXGNode Implementation
    //
    //--------------------------------------------------------------------------

    /**
     * @return The unqualified name of a GlowFilter node, without tag markup.
     */
    public String getNodeName()
    {
        return FXG_GLOWFILTER_ELEMENT;
    }

    @Override
    public void setAttribute(String name, String value)
    {
        if (FXG_ALPHA_ATTRIBUTE.equals(name))
            alpha = parseDouble(value, ALPHA_MIN_INCLUSIVE, ALPHA_MAX_INCLUSIVE, alpha);
        else if (FXG_BLURX_ATTRIBUTE.equals(name))
            blurX = parseDouble(value);
        else if (FXG_BLURY_ATTRIBUTE.equals(name))
            blurY = parseDouble(value);
        else if (FXG_COLOR_ATTRIBUTE.equals(name))
            color = parseRGB(value, color);
        else if (FXG_INNER_ATTRIBUTE.equals(name))
            inner = parseBoolean(value);
        else if (FXG_KNOCKOUT_ATTRIBUTE.equals(name))
            knockout = parseBoolean(value);
        else if (FXG_QUALITY_ATTRIBUTE.equals(name))
            quality = parseInt(value, QUALITY_MIN_INCLUSIVE, QUALITY_MAX_INCLUSIVE, quality);
        else if (FXG_STRENGTH_ATTRIBUTE.equals(name))
            strength = parseDouble(value);
        else
            super.setAttribute(name, value);
    }

}
