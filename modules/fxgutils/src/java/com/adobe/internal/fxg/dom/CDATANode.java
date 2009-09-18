////////////////////////////////////////////////////////////////////////////////
//
//  ADOBE SYSTEMS INCORPORATED
//  Copyright 2009 Adobe Systems Incorporated
//  All Rights Reserved.
//
//  NOTICE: Adobe permits you to use, modify, and distribute this file
//  in accordance with the terms of the license agreement accompanying it.
//
////////////////////////////////////////////////////////////////////////////////

package com.adobe.internal.fxg.dom;

import static com.adobe.fxg.FXGConstants.FXG_ID_ATTRIBUTE;

import java.util.List;
import java.util.Map;

import com.adobe.fxg.FXGException;

/**
 * A class to determine whether a node constitutes an CData in 
 * a text flow.
 * @author Min Plunkett
 */
public class CDATANode extends AbstractFXGNode implements TextNode
{
    //--------------------------------------------------------------------------
    //
    // Attributes
    //
    //--------------------------------------------------------------------------
    public String content = null; 
    
    //------------
    // id
    //------------

    protected String id;

    /**
     * An id attribute provides a well defined name to a text node.
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
     * @return null, as character data has no name.
     */
    public String getNodeName()
    {
        return null;
    }
    
    /**
     * @return A Map recording the attribute names and values set on this
     * text node.
     */
    public Map<String, String> getTextAttributes()
    {
        return null;
    }

    /**
     * @return The List of child nodes of this text node. 
     */
    public List<TextNode> getTextChildren()
    {
        return null;
    }

    /**
     * @return The List of child property nodes of this text node.
     */
    public List<TextNode> getTextProperties()
    {
        return null;
    }

    /**
     * Sets an FXG attribute on this text node.
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
