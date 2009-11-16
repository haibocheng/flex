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

import com.adobe.fxg.FXGVersion;
import com.adobe.internal.fxg.dom.types.FillMode;

/**
 * @author Peter Farland
 * @author Sujata Das
 */
public class BitmapGraphicNode extends GraphicContentNode
{
    //--------------------------------------------------------------------------
    //
    // Attributes
    //
    //--------------------------------------------------------------------------

    public double width = Double.NaN;
    public double height = Double.NaN;
    public String source;
    public boolean repeat = true;
    public FillMode fillMode = FillMode.SCALE;

    //--------------------------------------------------------------------------
    //
    // FXGNode Implementation
    //
    //--------------------------------------------------------------------------

    /**
     * @return The unqualified name of a BitmapGraphic node, without tag markup.
     */
    public String getNodeName()
    {
    	if (this.getFileVersion().equals(FXGVersion.v1_0) )
    		return FXG_BITMAPGRAPHIC_ELEMENT;
    	else
    		return FXG_BITMAPIMAGE_ELEMENT;
    }

    @Override
    public void setAttribute(String name, String value)
    {
        if (FXG_WIDTH_ATTRIBUTE.equals(name))
            width = parseDouble(value);
        else if (FXG_HEIGHT_ATTRIBUTE.equals(name))
            height = parseDouble(value);
        else if (FXG_SOURCE_ATTRIBUTE.equals(name))
            source = value;
        else if ((getFileVersion().equalTo(FXGVersion.v1_0)) && (FXG_REPEAT_ATTRIBUTE.equals(name)))
            repeat = parseBoolean(value);
        else if (!(getFileVersion().equalTo(FXGVersion.v1_0)) && (FXG_FILLMODE_ATTRIBUTE.equals(name)))
            fillMode = parseFillMode(value, fillMode);
        else
            super.setAttribute(name, value);
    }
    
}
