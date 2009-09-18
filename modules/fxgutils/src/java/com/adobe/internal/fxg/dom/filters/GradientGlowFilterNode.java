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

import java.util.ArrayList;
import java.util.List;

import com.adobe.fxg.FXGException;
import com.adobe.fxg.dom.FXGNode;
import com.adobe.internal.fxg.dom.GradientEntryNode;

/**
 * @author Peter Farland
 */
public class GradientGlowFilterNode extends AbstractFilterNode
{
    //--------------------------------------------------------------------------
    //
    // Attributes
    //
    //--------------------------------------------------------------------------

    public double angle = 45.0;
    public double distance = 4.0;
    public double blurX = 4.0;
    public double blurY = 4.0;
    public boolean inner = false;
    public boolean knockout = false;
    public int quality = 1;
    public double strength = 1.0;

    //--------------------------------------------------------------------------
    //
    // Children
    //
    //--------------------------------------------------------------------------

    public List<GradientEntryNode> entries;

    //--------------------------------------------------------------------------
    //
    // FXGNode Implementation
    //
    //--------------------------------------------------------------------------

    @Override
    public void addChild(FXGNode child)
    {
        if (child instanceof GradientEntryNode)
        {
            if (entries == null)
            {
                entries = new ArrayList<GradientEntryNode>(4);
            }
            else if (entries.size() >= GRADIENT_ENTRIES_MAX_INCLUSIVE)
            {
            	//Exception:A GradientGlowFilter cannot define more than 16 GradientEntry elements.
                throw new FXGException(child.getStartLine(), child.getStartColumn(), "InvalidGradientGlowFilterNumElements");
            }

            entries.add((GradientEntryNode)child);
        }
        else
        {
            super.addChild(child);
        }
    }

    /**
     * @return The unqualified name of a GradientGlowFilter node, without tag
     * markup.
     */
    public String getNodeName()
    {
        return FXG_GRADIENTGLOWFILTER_ELEMENT;
    }
    
    @Override
    public void setAttribute(String name, String value)
    {
        if (FXG_ANGLE_ATTRIBUTE.equals(name))
            angle = parseDouble(value);
        else if (FXG_BLURX_ATTRIBUTE.equals(name))
            blurX = parseDouble(value);
        else if (FXG_BLURY_ATTRIBUTE.equals(name))
            blurY = parseDouble(value);
        else if (FXG_DISTANCE_ATTRIBUTE.equals(name))
            distance = parseDouble(value);
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
