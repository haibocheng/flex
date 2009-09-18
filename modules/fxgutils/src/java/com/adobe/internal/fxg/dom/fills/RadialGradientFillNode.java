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

import java.util.ArrayList;
import java.util.List;

import static com.adobe.fxg.FXGConstants.*;
import com.adobe.fxg.FXGException;
import com.adobe.fxg.dom.FXGNode;
import com.adobe.internal.fxg.dom.GradientEntryNode;
import com.adobe.internal.fxg.dom.ScalableGradientNode;
import com.adobe.internal.fxg.dom.transforms.MatrixNode;
import com.adobe.internal.fxg.dom.types.InterpolationMethod;
import com.adobe.internal.fxg.dom.types.SpreadMethod;

/**
 * @author Peter Farland
 */
public class RadialGradientFillNode extends AbstractFillNode implements ScalableGradientNode
{
    private static final double FOCAL_MIN_INCLUSIVE = -1.0;
    private static final double FOCAL_MAX_INCLUSIVE = 1.0;

    //--------------------------------------------------------------------------
    //
    // Attributes
    //
    //--------------------------------------------------------------------------

    public double x = Double.NaN;
    public double y = Double.NaN;
    public double scaleX = Double.NaN;
    public double scaleY = Double.NaN;
    public double rotation = 0.0;
    public SpreadMethod spreadMethod = SpreadMethod.PAD;
    public InterpolationMethod interpolationMethod = InterpolationMethod.RGB;
    public double focalPointRatio = 0.0;

    private boolean translateSet;
    private boolean scaleSet;
    private boolean rotationSet;

    //--------------------------------------------------------------------------
    //
    // Children
    //
    //--------------------------------------------------------------------------

    public MatrixNode matrix;
    public List<GradientEntryNode> entries;

    //--------------------------------------------------------------------------
    //
    // ScalableGradientNode Implementation
    //
    //--------------------------------------------------------------------------

    public double getX()
    {
        return x;
    }

    public double getY()
    {
        return y;
    }

    public double getScaleX()
    {
         return scaleX;
    }

    public double getScaleY()
    {
        return scaleY;
    }

    public double getRotation()
    {
        return rotation;
    }

    public MatrixNode getMatrixNode()
    {
        return matrix;
    }

    public boolean isLinear()
    {
        return false;
    }

    //--------------------------------------------------------------------------
    //
    // FXGNode Implementation
    //
    //--------------------------------------------------------------------------

    public void addChild(FXGNode child)
    {
        if (child instanceof MatrixNode)
        {
            if (translateSet || scaleSet || rotationSet)
            	//Exception:Cannot supply a matrix child if transformation attributes were provided.
                throw new FXGException(child.getStartLine(), child.getStartColumn(), "InvalidChildMatrixNode");

            matrix = (MatrixNode)child;
        }
        else if (child instanceof GradientEntryNode)
        {
            if (entries == null)
            {
                entries = new ArrayList<GradientEntryNode>(4);
            }
            else if (entries.size() >= GRADIENT_ENTRIES_MAX_INCLUSIVE)
            {
            	//Exception:A RadialGradient cannot define more than 16 GradientEntry elements.
                throw new FXGException(child.getStartLine(), child.getStartColumn(), "InvalidRadialGradientNumElements");
            }

            entries.add((GradientEntryNode)child);
        }
        else
        {
            super.addChild(child);
        }
    }

    /**
     * @return The unqualified name of a RadialGradient node, without tag markup.
     */
    public String getNodeName()
    {
        return FXG_RADIALGRADIENT_ELEMENT;
    }

    @Override
    public void setAttribute(String name, String value)
    {
        if (FXG_X_ATTRIBUTE.equals(name))
        {
            x = parseDouble(value);
            translateSet = true;
        }
        else if (FXG_Y_ATTRIBUTE.equals(name))
        {
            y = parseDouble(value);
            translateSet = true;
        }
        else if (FXG_ROTATION_ATTRIBUTE.equals(name))
        {
            rotation = parseDouble(value);
            rotationSet = true;
        }
        else if (FXG_SCALEX_ATTRIBUTE.equals(name))
        {
            scaleX = parseDouble(value);
            scaleSet = true;
        }
        else if (FXG_SCALEY_ATTRIBUTE.equals(name))
        {
            scaleY = parseDouble(value);
            scaleSet = true;
        }
        else if (FXG_SPREADMETHOD_ATTRIBUTE.equals(name))
        {
            spreadMethod = parseSpreadMethod(value, spreadMethod);
        }
        else if (FXG_INTERPOLATIONMETHOD_ATTRIBUTE.equals(name))
        {
            interpolationMethod = parseInterpolationMethod(value, interpolationMethod);
        }
        else if (FXG_FOCALPOINTRATIO_ATTRIBUTE.equals(name))
        {
            focalPointRatio = parseDouble(value, FOCAL_MIN_INCLUSIVE, FOCAL_MAX_INCLUSIVE, focalPointRatio);
        }
        else
        {
            super.setAttribute(name, value);
        }
    }
}