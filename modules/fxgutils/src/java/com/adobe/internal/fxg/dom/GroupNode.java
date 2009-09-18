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
 * @author Peter Farland
 */
public class GroupNode extends GraphicContentNode implements MaskingNode
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

    /**
     * Adds an FXG child node to this Group node.
     * 
     * @param child - a child FXG node to be added to this node.
     * @throws FXGException if the child is not supported by this node.
     */
    @Override
    public void addChild(FXGNode child)
    {
        if (child instanceof GraphicContentNode)
        {
            if (children == null)
                children = new ArrayList<GraphicContentNode>();

            GraphicContentNode graphicContent = (GraphicContentNode)child;
            graphicContent.setParentGraphicContext(createGraphicContext());

            if (child instanceof GroupNode)
            {
                if (isInsideScaleGrid())
                {
                    // Exception:A child Group cannot exist in a Group that
                    // defines the scale grid
                    throw new FXGException(child.getStartLine(), child.getStartColumn(), "InvalidScaleGridGroupChild");
                }
            }

            children.add(graphicContent);
        }
        else
        {
            super.addChild(child);
        }
    }

    /**
     * @return The unqualified name of a Group node, without tag markup.
     */
    public String getNodeName()
    {
        return FXG_GROUP_ELEMENT;
    }

    /**
     * Sets an FXG attribute on this Group node.
     * 
     * @param name - the unqualified attribute name
     * @param value - the attribute value
     * @throws FXGException if the attribute is not supported by this node.
     */
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
        else
        {
            super.setAttribute(name, value);
        }

        if ((definesScaleGrid) && (this.rotationSet))
        {
            throw new FXGException(getStartLine(), getStartColumn(), "InvalidScaleGridRotationAttribute");
        }

    }

    @Override
    public GraphicContext createGraphicContext()
    {
        GraphicContext context = super.createGraphicContext();

        if (definesScaleGrid())
        {
            ScalingGrid scalingGrid = new ScalingGrid();
            scalingGrid.scaleGridLeft = scaleGridLeft;
            scalingGrid.scaleGridTop = scaleGridTop;
            scalingGrid.scaleGridRight = scaleGridRight;
            scalingGrid.scaleGridBottom = scaleGridBottom;
            context.scalingGrid = scalingGrid;
        }

        return context;
    }

    public boolean definesScaleGrid()
    {
        return definesScaleGrid;
    }

    public boolean isInsideScaleGrid()
    {
        return insideScaleGrid || definesScaleGrid;
    }

    public void setInsideScaleGrid(boolean value)
    {
        insideScaleGrid = value;
    }

    private boolean definesScaleGrid;
    private boolean insideScaleGrid;
}
