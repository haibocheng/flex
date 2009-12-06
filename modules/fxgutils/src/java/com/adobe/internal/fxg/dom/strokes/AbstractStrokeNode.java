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

package com.adobe.internal.fxg.dom.strokes;

import static com.adobe.fxg.FXGConstants.*;
import com.adobe.fxg.FXGException;
import com.adobe.fxg.FXGVersion;
import com.adobe.internal.fxg.dom.AbstractFXGNode;
import com.adobe.internal.fxg.dom.DOMParserHelper;
import com.adobe.internal.fxg.dom.GraphicNode;
import com.adobe.internal.fxg.dom.StrokeNode;
import com.adobe.internal.fxg.dom.types.Caps;
import com.adobe.internal.fxg.dom.types.Joints;
import com.adobe.internal.fxg.dom.types.ScaleMode;

/**
 * Base class for all FXG stroke nodes.
 * 
 * @author Peter Farland
 */
public abstract class AbstractStrokeNode extends AbstractFXGNode implements StrokeNode
{
    protected static final double MITERLIMIT_MIN_INCLUSIVE = 1.0;
    protected static final double MITERLIMIT_MAX_INCLUSIVE = 255.0;
    protected static final double WEIGHT_MIN_INCLUSIVE = 0.0;
    protected static final double WEIGHT_MAX_INCLUSIVE = 255.0;

    //--------------------------------------------------------------------------
    //
    // Attributes
    //
    //--------------------------------------------------------------------------

    //------------
    // id
    //------------

    protected String id;

    /**
     * An id attribute provides a well defined name to a content node.
     */
    public String getId()
    {
        return id;
    }

    /**
     * Sets the node id.
     * @param value - the node id as a String.
     */
    public void setId(String value)
    {
        id = value;
    }

    public ScaleMode scaleMode = ScaleMode.NORMAL;
    public Caps caps = Caps.ROUND;
    public boolean pixelHinting = false;
    public Joints joints = Joints.ROUND;
    public double miterLimit = 3.0;
    
    private double weight = Double.NaN;
    protected double weight_v_1 = 0.0;
    protected double weight_v_1_later = 1.0;
    
    /**
     * Stroke weight. Default value is FXG Version specific.
     * FXG 1.0 - default "0.0"
     * FXG 2.0 - default "2.0"
     */
    public double getWeight()
    {
    	if (Double.isNaN(weight))
    	{
        	if (((GraphicNode)this.getDocumentNode()).getVersion().equals(FXGVersion.v1_0))
        		weight = weight_v_1;       
        	else
        		weight = weight_v_1_later;
    	}
    	return weight;
    }
    
    /**
     * Get scaleX. 
     * @return Double.NaN as default.
     */
    public double getScaleX()
    {
        return Double.NaN;
    }

    /**
     * Get scaleY. 
     * @return Double.NaN as default.
     */
    public double getScaleY()
    {
        return Double.NaN;
    }
    
    //--------------------------------------------------------------------------
    //
    // FXGNode Implementation
    //
    //--------------------------------------------------------------------------

    /**
     * Sets an FXG attribute on this stroke node.
     * 
     * @param name - the unqualified attribute name.
     * @param value - the attribute value.
     * @throws FXGException if the attribute name is not supported by this node.
     */
    @Override
    public void setAttribute(String name, String value)
    {
        if (FXG_SCALEMODE_ATTRIBUTE.equals(name))
            scaleMode = getScaleMode(value);
        else if (FXG_CAPS_ATTRIBUTE.equals(name))
            caps = getCaps(value);
        else if (FXG_WEIGHT_ATTRIBUTE.equals(name))
            weight = DOMParserHelper.parseDouble(this, value, name, WEIGHT_MIN_INCLUSIVE, WEIGHT_MAX_INCLUSIVE, weight);
        else if (FXG_PIXELHINTING_ATTRIBUTE.equals(name))
            pixelHinting = DOMParserHelper.parseBoolean(this, value, name);
        else if (FXG_JOINTS_ATTRIBUTE.equals(name))
            joints = getJoints(value);
        else if (FXG_MITERLIMIT_ATTRIBUTE.equals(name))
            miterLimit = DOMParserHelper.parseDouble(this, value, name, MITERLIMIT_MIN_INCLUSIVE, MITERLIMIT_MAX_INCLUSIVE, miterLimit);
        else if (FXG_ID_ATTRIBUTE.equals(name))
            id = value;
        else
            super.setAttribute(name, value);
    }

    //--------------------------------------------------------------------------
    //
    // Helper Methods
    //
    //--------------------------------------------------------------------------

    /**
     * Convert an FXG String value to a Caps enumeration.
     * 
     * @param value - the FXG String value.
     * @return the matching Caps type.
     * @throws FXGException if the String did not match a known
     * Caps type.
     */
    protected Caps getCaps(String value)
    {
        if (FXG_CAPS_ROUND_VALUE.equals(value))
            return Caps.ROUND;
        else if (FXG_CAPS_SQUARE_VALUE.equals(value))
            return Caps.SQUARE;
        else if (FXG_CAPS_NONE_VALUE.equals(value))
            return Caps.NONE;
        else
        	//Exception:Unsupported caps setting {0}.
            throw new FXGException(getStartLine(), getStartColumn(), "UnsupportedCapsSetting", value);
    }

    /**
     * Convert an FXG String value to a Joints enumeration.
     * 
     * @param value - the FXG String value.
     * @return the matching Joints type.
     * @throws FXGException if the String did not match a known
     * Joints type.
     */
    protected Joints getJoints(String value)
    {
        if (FXG_JOINTS_ROUND_VALUE.equals(value))
            return Joints.ROUND;
        if (FXG_JOINTS_MITER_VALUE.equals(value))
            return Joints.MITER;
        if (FXG_JOINTS_BEVEL_VALUE.equals(value))
            return Joints.BEVEL;
        else
        	//Exception: Unsupported joints setting: {0}.
            throw new FXGException(getStartLine(), getStartColumn(), "UnsupportedJointsSetting", value);
    }

    /**
     * Convert an FXG String value to a ScaleMode enumeration.
     * 
     * @param value - the FXG String value.
     * @return the matching ScaleMode.
     * @throws FXGException if the String did not match a known
     * ScaleMode.
     */
    protected ScaleMode getScaleMode(String value)
    {
        if (FXG_SCALEMODE_NONE_VALUE.equals(value))
            return ScaleMode.NONE;
        else if (FXG_SCALEMODE_VERTICAL_VALUE.equals(value))
            return ScaleMode.VERTICAL;
        else if (FXG_SCALEMODE_NORMAL_VALUE.equals(value))
            return ScaleMode.NORMAL;
        else if (FXG_SCALEMODE_HORIZONTAL_VALUE.equals(value))
            return ScaleMode.HORIZONTAL;
        else
        	//Exception: Unsupported scaleMode setting: {0}.
            throw new FXGException(getStartLine(), getStartColumn(), "UnsupportedScaleMode", value);
    }
}