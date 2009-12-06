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

import com.adobe.internal.fxg.dom.DOMParserHelper;
import com.adobe.internal.fxg.dom.types.BevelType;

/**
 * @author Peter Farland
 */
public class BevelFilterNode extends AbstractFilterNode
{
    //--------------------------------------------------------------------------
    //
    // Attributes
    //
    //--------------------------------------------------------------------------

    public double blurX = 4.0;
    public double blurY = 4.0;
    public int quality = 1;
    public double angle = 45.0;
    public double distance = 4.0;
    public double highlightAlpha = 1.0;
    public int highlightColor = COLOR_WHITE;
    public boolean knockout = false;
    public double shadowAlpha = 1.0;
    public int shadowColor = COLOR_BLACK;
    public double strength = 1.0;
    public BevelType type = BevelType.INNER;

    //--------------------------------------------------------------------------
    //
    // FXGNode Implementation
    //
    //--------------------------------------------------------------------------

    /**
     * @return The unqualified name of a BevelFilter node, without tag markup.
     */
    public String getNodeName()
    {
        return FXG_BEVELFILTER_ELEMENT;
    }

    @Override
    public void setAttribute(String name, String value)
    {
        if (FXG_BLURX_ATTRIBUTE.equals(name))
            blurX = DOMParserHelper.parseDouble(this, value, name);
        else if (FXG_BLURY_ATTRIBUTE.equals(name))
            blurY = DOMParserHelper.parseDouble(this, value, name);
        else if (FXG_QUALITY_ATTRIBUTE.equals(name))
            quality = DOMParserHelper.parseInt(this, value, name, QUALITY_MIN_INCLUSIVE, QUALITY_MAX_INCLUSIVE, quality);
        else if (FXG_ANGLE_ATTRIBUTE.equals(name))
            angle = DOMParserHelper.parseDouble(this, value, name);
        else if (FXG_DISTANCE_ATTRIBUTE.equals(name))
            distance = DOMParserHelper.parseDouble(this, value, name);
        else if (FXG_HIGHLIGHTALPHA_ATTRIBUTE.equals(name))
            highlightAlpha = DOMParserHelper.parseDouble(this, value, name, ALPHA_MIN_INCLUSIVE, ALPHA_MAX_INCLUSIVE, highlightAlpha);
        else if (FXG_HIGHLIGHTCOLOR_ATTRIBUTE.equals(name))
            highlightColor = DOMParserHelper.parseRGB(this, value, name);
        else if (FXG_KNOCKOUT_ATTRIBUTE.equals(name))
            knockout = DOMParserHelper.parseBoolean(this, value, name);
        else if (FXG_SHADOWALPHA_ATTRIBUTE.equals(name))
            shadowAlpha = DOMParserHelper.parseDouble(this, value, name, ALPHA_MIN_INCLUSIVE, ALPHA_MAX_INCLUSIVE, shadowAlpha);
        else if (FXG_SHADOWCOLOR_ATTRIBUTE.equals(name))
            shadowColor = DOMParserHelper.parseRGB(this, value, name);
        else if (FXG_STRENGTH_ATTRIBUTE.equals(name))
            strength = DOMParserHelper.parseDouble(this, value, name);
        else if (FXG_TYPE_ATTRIBUTE.equals(name))
            type = getBevelType(value);
		else
			super.setAttribute(name, value);
    }
}