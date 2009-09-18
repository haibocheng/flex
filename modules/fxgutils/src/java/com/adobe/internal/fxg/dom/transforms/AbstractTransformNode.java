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

package com.adobe.internal.fxg.dom.transforms;

import static com.adobe.fxg.FXGConstants.FXG_ID_ATTRIBUTE;
import com.adobe.fxg.FXGException;
import com.adobe.internal.fxg.dom.AbstractFXGNode;
import com.adobe.internal.fxg.dom.TransformNode;

/**
 * A base class for all FXG nodes that represent a transform.
 * 
 * @author Peter Farland
 */
public abstract class AbstractTransformNode extends AbstractFXGNode implements TransformNode 
{
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
    // FXGNode Implementation
    //
    //--------------------------------------------------------------------------

    /**
     * Sets an FXG attribute on this transform node.
     * 
     * @param name - the unqualified attribute name.
     * @param value - the attribute value.
     * @throws FXGException if the attribute name is not supported by this node.
     */
    @Override
    public void setAttribute(String name, String value)
    {
        if (FXG_ID_ATTRIBUTE.equals(name))
            id = value;
        else
            super.setAttribute(name, value);
    }
}
