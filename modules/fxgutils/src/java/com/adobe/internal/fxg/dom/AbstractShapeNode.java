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

import java.util.List;

import com.adobe.fxg.dom.FXGNode;

import flash.swf.types.LineStyle;
import flash.swf.types.Rect;
import flash.swf.types.ShapeRecord;

/**
 * A base class for all FXG nodes that represent a stroke.
 * 
 * @author Peter Farland
 * @author Sujata Das
 */
public abstract class AbstractShapeNode extends GraphicContentNode
{
    //--------------------------------------------------------------------------
    //
    // Children
    //
    //--------------------------------------------------------------------------

    public FillNode fill;
    public StrokeNode stroke;

    //--------------------------------------------------------------------------
    //
    // FXGNode Implementation
    //
    //--------------------------------------------------------------------------

    public void addChild(FXGNode child)
    {
        if (child instanceof FillNode)
            fill = (FillNode)child;
        else if (child instanceof StrokeNode)
            stroke = (StrokeNode)child;
        else
            super.addChild(child);
    }
    
    /**
     * Returns the bounds of the shapes
     * Default implementation - to be overridden by individual classes
     */
    public Rect getBounds(List<ShapeRecord> records, LineStyle ls)
    {
    	return null;
    }
}
