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

package com.adobe.internal.fxg.dom.transforms;

import static com.adobe.fxg.FXGConstants.*;

import com.adobe.fxg.FXGException;
import com.adobe.internal.fxg.dom.DOMParserHelper;

/**
 * @author Peter Farland
 */
public class ColorTransformNode extends AbstractTransformNode implements Cloneable
{
    private static final double MIN_OFFSET_INCLUSIVE = -255.0;
    private static final double MAX_OFFSET_INCLUSIVE = 255.0;

    //--------------------------------------------------------------------------
    //
    // Attributes
    //
    //--------------------------------------------------------------------------

    public double alphaMultiplier = 1.0;
    public double redMultiplier = 1.0;
    public double blueMultiplier = 1.0;
    public double greenMultiplier = 1.0;
    public double alphaOffset = 0.0;
    public double redOffset = 0.0;
    public double blueOffset = 0.0;
    public double greenOffset = 0.0;

    //--------------------------------------------------------------------------
    //
    // FXGNode Implementation
    //
    //--------------------------------------------------------------------------

    /**
     * @return The unqualified name of a ColorTransform node, without tag markup.
     */
    public String getNodeName()
    {
        return FXG_COLORTRANSFORM_ELEMENT;
    }

    @Override
    public void setAttribute(String name, String value)
    {
        if (FXG_ALPHAMULTIPLIER_ATTRIBUTE.equals(name))
            alphaMultiplier = DOMParserHelper.parseDouble(this, value, name);
        else if (FXG_REDMULTIPLIER_ATTRIBUTE.equals(name))
            redMultiplier = DOMParserHelper.parseDouble(this, value, name);
        else if (FXG_BLUEMULTIPLIER_ATTRIBUTE.equals(name))
            blueMultiplier = DOMParserHelper.parseDouble(this, value, name);
        else if (FXG_GREENMULTIPLIER_ATTRIBUTE.equals(name))
            greenMultiplier = DOMParserHelper.parseDouble(this, value, name);
        else if (FXG_ALPHAOFFSET_ATTRIBUTE.equals(name))
            alphaOffset = DOMParserHelper.parseDouble(this, value, name, MIN_OFFSET_INCLUSIVE, MAX_OFFSET_INCLUSIVE, alphaOffset);
        else if (FXG_REDOFFSET_ATTRIBUTE.equals(name))
            redOffset = DOMParserHelper.parseDouble(this, value, name, MIN_OFFSET_INCLUSIVE, MAX_OFFSET_INCLUSIVE, redOffset);
        else if (FXG_BLUEOFFSET_ATTRIBUTE.equals(name))
            blueOffset = DOMParserHelper.parseDouble(this, value, name, MIN_OFFSET_INCLUSIVE, MAX_OFFSET_INCLUSIVE, blueOffset);
        else if (FXG_GREENOFFSET_ATTRIBUTE.equals(name))
            greenOffset = DOMParserHelper.parseDouble(this, value, name, MIN_OFFSET_INCLUSIVE, MAX_OFFSET_INCLUSIVE, greenOffset);
    }

    //--------------------------------------------------------------------------
    //
    // Cloneable Implementation
    //
    //--------------------------------------------------------------------------

    public Object clone()
    {
        ColorTransformNode copy = null;
        try
        {
            copy = (ColorTransformNode)super.clone();
            copy.alphaMultiplier = alphaMultiplier;
            copy.redMultiplier = redMultiplier;
            copy.blueMultiplier = blueMultiplier;
            copy.greenMultiplier = greenMultiplier;
            copy.alphaOffset = alphaOffset;
            copy.redOffset = redOffset;
            copy.blueOffset = blueOffset;
            copy.greenOffset = greenOffset;
        }
        catch (CloneNotSupportedException e)
        {
            throw new FXGException(getStartLine(), getStartColumn(), "InternalProcessingError", e);
       }
 
        return copy;
    }
}
