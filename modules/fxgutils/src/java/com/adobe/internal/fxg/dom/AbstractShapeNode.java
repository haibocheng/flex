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

import com.adobe.fxg.dom.FXGNode;

/**
 * A base class for all FXG nodes that represent a stroke.
 * 
 * @author Peter Farland
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
}
