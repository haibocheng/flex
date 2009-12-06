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

import flash.swf.SwfConstants;
import flash.swf.types.LineStyle;
import flash.swf.types.Rect;
import flash.swf.types.ShapeRecord;

/**
 * @author Peter Farland
 * @author Sujata Das
 */
public class EllipseNode extends AbstractShapeNode
{
    //--------------------------------------------------------------------------
    //
    // Attributes
    //
    //--------------------------------------------------------------------------

    public double width = 0.0;
    public double height = 0.0;

    //--------------------------------------------------------------------------
    //
    // FXGNode Implementation
    //
    //--------------------------------------------------------------------------

    /**
     * @return The unqualified name of an Ellipse node, without tag markup.
     */
    public String getNodeName()
    {
        return FXG_ELLIPSE_ELEMENT;
    }

    @Override
    public void setAttribute(String name, String value)
    {
        if (FXG_WIDTH_ATTRIBUTE.equals(name))
            width = DOMParserHelper.parseDouble(this, value, name);
        else if (FXG_HEIGHT_ATTRIBUTE.equals(name))
            height = DOMParserHelper.parseDouble(this, value, name);
        else
            super.setAttribute(name, value);
    }
    
    /**
     * Returns the bounds of the ellipse
     */
    public Rect getBounds(List<ShapeRecord> records, LineStyle ls)
    {
        int x1 = 0;
        int y1 = 0;
        int x2 = (int) (width*SwfConstants.TWIPS_PER_PIXEL);
        int y2 = (int) (height*SwfConstants.TWIPS_PER_PIXEL);
        if (ls != null)
        {
            int width = SwfConstants.TWIPS_PER_PIXEL;
            if (width < ls.width)
            	width = ls.width;
            int stroke = (int)Math.rint(width / 2.0);
            x1 = x1 - stroke;
            y1 = y1 - stroke;
            x2 = x2 + stroke;
            y2 = y2 + stroke;
        }
 
        return new Rect(x1, x2, y1, y2);
    }
}
