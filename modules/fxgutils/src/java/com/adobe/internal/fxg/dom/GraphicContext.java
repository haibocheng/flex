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

import com.adobe.fxg.FXGException;
import com.adobe.internal.fxg.dom.transforms.ColorTransformNode;
import com.adobe.internal.fxg.dom.types.BlendMode;
import com.adobe.internal.fxg.dom.types.MaskType;
import com.adobe.internal.fxg.dom.types.ScalingGrid;
import com.adobe.internal.fxg.types.FXGMatrix;

/**
 * A simple context holding inheritable graphic transformation information to be
 * used for placing a symbol on stage.
 * 
 * @author Peter Farland
 */
public class GraphicContext implements Cloneable
{
    private FXGMatrix transform;

    public GraphicContext()
    {
    }

    public BlendMode blendMode;
    public MaskType maskType;
    public List<FilterNode> filters;
    public ColorTransformNode colorTransform;
    public ScalingGrid scalingGrid;

    public FXGMatrix getTransform()
    {
        if (transform == null)
            transform = new FXGMatrix();

        return transform;
    }

    public void setTransform(FXGMatrix matrix)
    {
    	transform = matrix;
    }
    
    public void addFilters(List<FilterNode> list)
    {
        if (filters == null)
            filters = list;
        else
            filters.addAll(list);
    }

    public Object clone()
    {
        GraphicContext copy = null;
        try
        {
            copy = (GraphicContext)super.clone();
            copy.transform = null;
            if (colorTransform != null)
                copy.colorTransform = (ColorTransformNode)colorTransform.clone();
            copy.maskType = maskType;
            copy.blendMode = blendMode;
            copy.scalingGrid = scalingGrid;
        }
        catch (CloneNotSupportedException e)
        {
            throw new FXGException("InternalProcessingError", e);
        }
        return copy;
    }
}
