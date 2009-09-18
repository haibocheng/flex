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

package com.adobe.internal.fxg.dom.filters;

import static com.adobe.fxg.FXGConstants.*;
import com.adobe.fxg.FXGException;
import com.adobe.internal.fxg.dom.AbstractFXGNode;
import com.adobe.internal.fxg.dom.FilterNode;
import com.adobe.internal.fxg.dom.types.BevelType;

/**
 * @author Peter Farland
 */
public abstract class AbstractFilterNode extends AbstractFXGNode implements FilterNode
{
    protected static final int QUALITY_MIN_INCLUSIVE = 1;
    protected static final int QUALITY_MAX_INCLUSIVE = 3;

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

    //--------------------------------------------------------------------------
    //
    // Helper Methods
    //
    //--------------------------------------------------------------------------

    /**
     * Convert an FXG String value to a BevelType enumeration.
     * 
     * @param value - the FXG String value.
     * @return the matching BevelType type.
     * @throws FXGException if the String did not match a known
     * BevelType type.
     */
    protected BevelType getBevelType(String value)
    {
        if (FXG_BEVEL_INNER_VALUE.equals(value))
            return BevelType.INNER;
        else if (FXG_BEVEL_OUTER_VALUE.equals(value))
            return BevelType.OUTER;
        else if (FXG_BEVEL_FULL_VALUE.equals(value))
            return BevelType.FULL;
        else
        	//Exception:Unknown bevel type: {0}.
            throw new FXGException(getStartLine(), getStartColumn(), "UnknownBevelType", value);
    }
}
