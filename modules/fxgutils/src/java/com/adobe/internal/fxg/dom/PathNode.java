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

import com.adobe.fxg.FXGException;
import com.adobe.internal.fxg.dom.strokes.AbstractStrokeNode;
import com.adobe.internal.fxg.dom.types.Winding;
import com.adobe.internal.fxg.swf.ShapeHelper;

import flash.swf.types.LineStyle;
import flash.swf.types.Rect;
import flash.swf.types.ShapeRecord;

/**
 * @author Peter Farland
 * @author Sujata Das
 * @author Min Plunkett
 */
public class PathNode extends AbstractShapeNode
{
    //--------------------------------------------------------------------------
    //
    // Attributes
    //
    //--------------------------------------------------------------------------

    public Winding winding = Winding.EVEN_ODD;
    public String data = "";

    //--------------------------------------------------------------------------
    //
    // FXGNode Implementation
    //
    //--------------------------------------------------------------------------

    @Override
    public void setAttribute(String name, String value)
    {
        if (FXG_WINDING_ATTRIBUTE.equals(name))
            winding = getWinding(value);
        else if (FXG_DATA_ATTRIBUTE.equals(name))
            data = value;
        else
            super.setAttribute(name, value);
    }

    /**
     * @return The unqualified name of a Path node, without tag markup.
     */
    public String getNodeName()
    {
        return FXG_PATH_ELEMENT;
    }

    //--------------------------------------------------------------------------
    //
    // Helper Methods
    //
    //--------------------------------------------------------------------------
    
    protected Winding getWinding(String value)
    {
        if (FXG_WINDING_EVENODD_VALUE.equals(value))
            return Winding.EVEN_ODD;
        else if (FXG_WINDING_NONZERO_VALUE.equals(value))
            return Winding.NON_ZERO;
        else
        	//Exception:Unknown winding value: {0}.
            throw new FXGException(getStartLine(), getStartColumn(), "UnknownWindingValue", value);
    }
    
    /**
     * Returns the bounds of the path
     */
    public Rect getBounds(List<ShapeRecord> records, LineStyle ls)
    {
    	return ShapeHelper.getBounds(records, ls, (AbstractStrokeNode)stroke);
    }
}
