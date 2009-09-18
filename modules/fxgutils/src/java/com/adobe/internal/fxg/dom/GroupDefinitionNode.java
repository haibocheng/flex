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

import java.util.ArrayList;
import java.util.List;

import com.adobe.fxg.FXGException;
import com.adobe.fxg.dom.FXGNode;
import com.adobe.internal.fxg.dom.types.ScalingGrid;

import static com.adobe.fxg.FXGConstants.*;

/**
 * A GroupDefinition represents the special use of Group as the basis for an
 * FXG Library Definition. It acts as the base graphic context for a symbol
 * definition. A GroupDefinition differs from a Group instance in that it
 * cannot define a transform, filters or have an id attribute.
 * 
 * @author Peter Farland
 */
public class GroupDefinitionNode extends AbstractFXGNode
{
    //--------------------------------------------------------------------------
    //
    // Attributes
    //
    //--------------------------------------------------------------------------

    public double scaleGridLeft = 0.0;
    public double scaleGridRight = 0.0;
    public double scaleGridTop = 0.0;
    public double scaleGridBottom = 0.0;

    //--------------------------------------------------------------------------
    //
    // Children
    //
    //--------------------------------------------------------------------------
    
    public List<GraphicContentNode> children;

    //--------------------------------------------------------------------------
    //
    // FXGNode Implementation
    //
    //--------------------------------------------------------------------------

    @Override
    public void addChild(FXGNode child)
    {
        if (child instanceof GraphicContentNode)
        {
            if (child instanceof TextGraphicNode)
                textCount++;

            if (children == null)
                children = new ArrayList<GraphicContentNode>();

            children.add((GraphicContentNode)child);
        }
        else
        {
            super.addChild(child);
        }
    }

    public String getNodeName()
    {
        return FXG_GROUP_ELEMENT;
    }

    @Override
    public void setAttribute(String name, String value)
    {
        if (FXG_SCALEGRIDLEFT_ATTRIBUTE.equals(name))
        {
            scaleGridLeft = parseDouble(value);
            definesScaleGrid = true;
        }
        else if (FXG_SCALEGRIDTOP_ATTRIBUTE.equals(name))
        {
            scaleGridTop = parseDouble(value);
            definesScaleGrid = true;
        }
        else if (FXG_SCALEGRIDRIGHT_ATTRIBUTE.equals(name))
        {
            scaleGridRight = parseDouble(value);
            definesScaleGrid = true;
        }
        else if (FXG_SCALEGRIDBOTTOM_ATTRIBUTE.equals(name))
        {
            scaleGridBottom = parseDouble(value);
            definesScaleGrid = true;
        }
        else if (FXG_ID_ATTRIBUTE.equals(name))
        {
        	//Exception:The id attribute is not allowed on the Group child a Definition.
            throw new FXGException(getStartLine(), getStartColumn(), "InvalidGroupIDAttribute");
        }
        else
        {
            super.setAttribute(name, value);
        }
    }

    //--------------------------------------------------------------------------
    //
    // Helper Methods
    //
    //--------------------------------------------------------------------------

    public ScalingGrid getScalingGrid()
    {
        ScalingGrid scalingGrid = null;

        if (definesScaleGrid())
        {
            scalingGrid = new ScalingGrid();
            scalingGrid.scaleGridLeft = scaleGridLeft;
            scalingGrid.scaleGridTop = scaleGridTop;
            scalingGrid.scaleGridRight = scaleGridRight;
            scalingGrid.scaleGridBottom = scaleGridBottom;
        }

        return scalingGrid;
    }

    public boolean definesScaleGrid()
    {
        return definesScaleGrid;
    }

    public int getTextCount()
    {
        return textCount;
    }

    private boolean definesScaleGrid;
    private int textCount;}
