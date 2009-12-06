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

package com.adobe.internal.fxg.dom.fills;

import static com.adobe.fxg.FXGConstants.*;

import com.adobe.fxg.FXGVersion;
import com.adobe.fxg.dom.FXGNode;
import com.adobe.internal.fxg.dom.DOMParserHelper;
import com.adobe.internal.fxg.dom.transforms.MatrixNode;
import com.adobe.internal.fxg.dom.types.FillMode;

/**
 * @author Peter Farland
 * @author Sujata Das
 */
public class BitmapFillNode extends AbstractFillNode
{
    //--------------------------------------------------------------------------
    //
    // Attributes
    //
    //--------------------------------------------------------------------------

    public double x = Double.NaN;
    public double y = Double.NaN;
    public boolean repeat = true;
    public double rotation = 0.0;
    public double scaleX = Double.NaN;
    public double scaleY = Double.NaN;
    public String source;
    public FillMode fillMode = FillMode.SCALE;

    //--------------------------------------------------------------------------
    //
    // Children
    //
    //--------------------------------------------------------------------------

    public MatrixNode matrix;

    //--------------------------------------------------------------------------
    //
    // FXGNode Implementation
    //
    //--------------------------------------------------------------------------

    public void addChild(FXGNode child)
    {
        if (child instanceof MatrixNode)
            matrix = (MatrixNode)child;
        else
            super.addChild(child);
    }

    /**
     * @return The unqualified name of a BitmapFill node, without tag markup.
     */
    public String getNodeName()
    {
        return FXG_BITMAPFILL_ELEMENT;
    }

    @Override
    public void setAttribute(String name, String value)
    {
        if (FXG_X_ATTRIBUTE.equals(name))
            x = DOMParserHelper.parseDouble(this, value, name);
        else if (FXG_Y_ATTRIBUTE.equals(name))
            y = DOMParserHelper.parseDouble(this, value, name);
        else if ((getFileVersion().equalTo(FXGVersion.v1_0)) && (FXG_REPEAT_ATTRIBUTE.equals(name)))
            repeat = DOMParserHelper.parseBoolean(this, value, name);
        else if (FXG_ROTATION_ATTRIBUTE.equals(name))
            rotation = DOMParserHelper.parseDouble(this, value, name);
        else if (FXG_SCALEX_ATTRIBUTE.equals(name))
            scaleX = DOMParserHelper.parseDouble(this, value, name);
        else if (FXG_SCALEY_ATTRIBUTE.equals(name))
            scaleY = DOMParserHelper.parseDouble(this, value, name);
        else if (FXG_SOURCE_ATTRIBUTE.equals(name))
            source = value;
        else if (!(getFileVersion().equalTo(FXGVersion.v1_0)) && (FXG_FILLMODE_ATTRIBUTE.equals(name)))
            fillMode = DOMParserHelper.parseFillMode(this, value, fillMode);
        else
            super.setAttribute(name, value);
    }
    
}
