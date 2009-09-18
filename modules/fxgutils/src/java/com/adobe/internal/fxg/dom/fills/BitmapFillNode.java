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

    public double getX()
    {
        return x;
    }

    public void setX(double x)
    {
        this.x = x;
    }

    public double getY()
    {
        return y;
    }

    public void setY(double y)
    {
        this.y = y;
    }

    public boolean isRepeat()
    {
        return repeat;
    }

    public void setRepeat(boolean repeat)
    {
        this.repeat = repeat;
    }

    public double getRotation()
    {
        return rotation;
    }

    public void setRotation(double rotation)
    {
        this.rotation = rotation;
    }

    public double getScaleX()
    {
        return scaleX;
    }

    public void setScaleX(double scaleX)
    {
        this.scaleX = scaleX;
    }

    public double getScaleY()
    {
        return scaleY;
    }

    public void setScaleY(double scaleY)
    {
        this.scaleY = scaleY;
    }

    public String getSource()
    {
        return source;
    }

    public void setSource(String source)
    {
        this.source = source;
    }

    public MatrixNode getMatrix()
    {
        return matrix;
    }

    public void setMatrix(MatrixNode matrix)
    {
        this.matrix = matrix;
    }
    
    public double x = 0.0;
    public double y = 0.0;
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
            x = parseDouble(value);
        else if (FXG_Y_ATTRIBUTE.equals(name))
            y = parseDouble(value);
        else if ((getFileVersion().equalTo(FXGVersion.v1_0)) && (FXG_REPEAT_ATTRIBUTE.equals(name)))
            repeat = parseBoolean(value);
        else if (FXG_ROTATION_ATTRIBUTE.equals(name))
            rotation = parseDouble(value);
        else if (FXG_SCALEX_ATTRIBUTE.equals(name))
            scaleX = parseDouble(value);
        else if (FXG_SCALEY_ATTRIBUTE.equals(name))
            scaleY = parseDouble(value);
        else if (FXG_SOURCE_ATTRIBUTE.equals(name))
            source = value;
        else if (!(getFileVersion().equalTo(FXGVersion.v1_0)) && (FXG_FILLMODE_ATTRIBUTE.equals(name)))
            fillMode = parseFillMode(value, fillMode);
        else
            super.setAttribute(name, value);
    }
    
}
