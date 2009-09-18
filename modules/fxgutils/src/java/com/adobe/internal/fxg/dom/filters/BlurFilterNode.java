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
public class BlurFilterNode extends AbstractFilterNode
{
    //--------------------------------------------------------------------------
    //
    // Attributes
    //
    //--------------------------------------------------------------------------

    public double blurX = 4.0;
    public double blurY = 4.0;
    public int quality = 1;

    //--------------------------------------------------------------------------
    //
    // FXGNode Implementation
    //
    //--------------------------------------------------------------------------

    /**
     * @return The unqualified name of a BlurFilter node, without tag markup.
     */
    public String getNodeName()
    {
        return FXG_BLURFILTER_ELEMENT;
    }

    @Override
    public void setAttribute(String name, String value)
    {
        if (FXG_BLURX_ATTRIBUTE.equals(name))
            blurX = parseDouble(value);
        else if (FXG_BLURY_ATTRIBUTE.equals(name))
            blurY = parseDouble(value);
        else if (FXG_QUALITY_ATTRIBUTE.equals(name))
            quality = parseInt(value, QUALITY_MIN_INCLUSIVE, QUALITY_MAX_INCLUSIVE, quality);
		else
            super.setAttribute(name, value);
    }
}
