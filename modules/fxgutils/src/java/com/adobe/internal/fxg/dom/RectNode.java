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

import com.adobe.fxg.FXGException;

/**
 * @author Peter Farland
 * @author Sujata Das
 */
public class RectNode extends AbstractShapeNode
{
    //--------------------------------------------------------------------------
    //
    // Attributes
    //
    //--------------------------------------------------------------------------

    public double width = 0.0;
    public double height = 0.0;
    public double radiusX = 0.0;
    public double radiusY = 0.0;
    public double topLeftRadiusX = Double.NaN;
    public double topLeftRadiusY = Double.NaN;
    public double topRightRadiusY = Double.NaN;
    public double topRightRadiusX = Double.NaN;
    public double bottomRightRadiusX = Double.NaN;
    public double bottomRightRadiusY = Double.NaN;
    public double bottomLeftRadiusX = Double.NaN;
    public double bottomLeftRadiusY = Double.NaN;    

    //--------------------------------------------------------------------------
    //
    // FXGNode Implementation
    //
    //--------------------------------------------------------------------------

    /**
     * @return The unqualified name of a Rect node, without tag markup.
     */
    public String getNodeName()
    {
        return FXG_RECT_ELEMENT;
    }

    @Override
    public void setAttribute(String name, String value)
    {
        if (FXG_WIDTH_ATTRIBUTE.equals(name))
        {
            width = parseDouble(value);
        }
        else if (FXG_HEIGHT_ATTRIBUTE.equals(name))
        {
            height = parseDouble(value);
        }
        else if (FXG_RADIUSX_ATTRIBUTE.equals(name))
        {
            radiusX = parseDouble(value);
            if (radiusX < 0) 
                // RadiusX, RadiusY, TopLeftRadiusX, TopLeftRadiusY, 
            	// TopRightRadiusX, TopRightRadiusY, BottomRightRadiusX, 
            	// BottomRightRadiusY, BottomLeftRadiusX, BottomLeftRadiusX 
            	// must be greater than 0.
                throw new FXGException(getStartLine(), getStartColumn(), "InvalidRectRadiusXRadiusYAttribute");
        }
        else if (FXG_RADIUSY_ATTRIBUTE.equals(name))
        {
            radiusY = parseDouble(value);
            if (radiusY < 0)
                // RadiusX, RadiusY, TopLeftRadiusX, TopLeftRadiusY, 
            	// TopRightRadiusX, TopRightRadiusY, BottomRightRadiusX, 
            	// BottomRightRadiusY, BottomLeftRadiusX, BottomLeftRadiusX 
            	// must be greater than 0.
                throw new FXGException(getStartLine(), getStartColumn(), "InvalidRectRadiusXRadiusYAttribute");
        }
        else if (FXG_TOPLEFTRADIUSX_ATTRIBUTE.equals(name))
        {
        	topLeftRadiusX = parseDouble(value);
            if (topLeftRadiusX < 0) 
                // RadiusX and RadiusY must be greater than 0.
                throw new FXGException(getStartLine(), getStartColumn(), "InvalidRectRadiusXRadiusYAttribute");
        }
        else if (FXG_TOPLEFTRADIUSY_ATTRIBUTE.equals(name))
        {
        	topLeftRadiusY = parseDouble(value);
            if (topLeftRadiusY < 0)
                // RadiusX and RadiusY must be greater than 0.
                throw new FXGException(getStartLine(), getStartColumn(), "InvalidRectRadiusXRadiusYAttribute");
        }
        else if (FXG_TOPRIGHTRADIUSX_ATTRIBUTE.equals(name))
        {
        	topRightRadiusX = parseDouble(value);
            if (topRightRadiusX < 0) 
                // RadiusX and RadiusY must be greater than 0.
                throw new FXGException(getStartLine(), getStartColumn(), "InvalidRectRadiusXRadiusYAttribute");
        }
        else if (FXG_TOPRIGHTRADIUSY_ATTRIBUTE.equals(name))
        {
        	topRightRadiusY = parseDouble(value);
            if (topRightRadiusY < 0)
                // RadiusX and RadiusY must be greater than 0.
                throw new FXGException(getStartLine(), getStartColumn(), "InvalidRectRadiusXRadiusYAttribute");
        }
        else if (FXG_BOTTOMLEFTRADIUSX_ATTRIBUTE.equals(name))
        {
        	bottomLeftRadiusX = parseDouble(value);
            if (bottomLeftRadiusX < 0) 
                // RadiusX and RadiusY must be greater than 0.
                throw new FXGException(getStartLine(), getStartColumn(), "InvalidRectRadiusXRadiusYAttribute");
        }
        else if (FXG_BOTTOMLEFTRADIUSY_ATTRIBUTE.equals(name))
        {
        	bottomLeftRadiusY = parseDouble(value);
            if (bottomLeftRadiusY < 0)
                // RadiusX and RadiusY must be greater than 0.
                throw new FXGException(getStartLine(), getStartColumn(), "InvalidRectRadiusXRadiusYAttribute");
        }
        else if (FXG_BOTTOMRIGHTRADIUSX_ATTRIBUTE.equals(name))
        {
        	bottomRightRadiusX = parseDouble(value);
            if (bottomRightRadiusX < 0) 
                // RadiusX and RadiusY must be greater than 0.
                throw new FXGException(getStartLine(), getStartColumn(), "InvalidRectRadiusXRadiusYAttribute");
        }
        else if (FXG_BOTTOMRIGHTRADIUSY_ATTRIBUTE.equals(name))
        {
            bottomRightRadiusY = parseDouble(value);
            if (bottomRightRadiusY < 0)
                // RadiusX and RadiusY must be greater than 0.
                throw new FXGException(getStartLine(), getStartColumn(), "InvalidRectRadiusXRadiusYAttribute");
        }
        else
        {
            super.setAttribute(name, value);
        }
    }
}
