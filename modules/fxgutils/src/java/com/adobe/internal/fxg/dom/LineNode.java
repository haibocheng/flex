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

import java.util.List;

import com.adobe.internal.fxg.dom.strokes.AbstractStrokeNode;
import com.adobe.internal.fxg.swf.ShapeHelper;

import flash.swf.types.LineStyle;
import flash.swf.types.Rect;
import flash.swf.types.ShapeRecord;

/**
 * @author Peter Farland
 */
public class LineNode extends AbstractShapeNode
{
    //--------------------------------------------------------------------------
    //
    // Attributes
    //
    //--------------------------------------------------------------------------

    public double xFrom = 0.0;
    public double yFrom = 0.0;
    public double xTo = 0.0;
    public double yTo = 0.0;

    //--------------------------------------------------------------------------
    //
    // FXGNode Implementation
    //
    //--------------------------------------------------------------------------

    /**
     * @return The unqualified name of a Line node, without tag markup.
     */
    public String getNodeName()
    {
        return FXG_LINE_ELEMENT;
    }

    @Override
    public void setAttribute(String name, String value)
    {
        if (FXG_XFROM_ATTRIBUTE.equals(name))
            xFrom = parseDouble(value);
        else if (FXG_YFROM_ATTRIBUTE.equals(name))
            yFrom = parseDouble(value);
        else if (FXG_XTO_ATTRIBUTE.equals(name))
            xTo = parseDouble(value);
        else if (FXG_YTO_ATTRIBUTE.equals(name))
            yTo = parseDouble(value);
        else
            super.setAttribute(name, value);
    }
    
    /**
     * Returns the bounds of the line
     */
    public Rect getBounds(List<ShapeRecord> records, LineStyle ls)
    {
    	return ShapeHelper.getBounds(records, ls, (AbstractStrokeNode)stroke);
    }
}
