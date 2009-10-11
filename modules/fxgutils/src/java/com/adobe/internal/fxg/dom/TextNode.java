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
import java.util.Map;

import com.adobe.fxg.dom.FXGNode;

/**
 * A marker interface to determine whether a node constitutes an element
 * of a text flow.
 * 
 * @author Peter Farland
 */
public interface TextNode extends FXGNode
{
    /**
     * An id attribute provides a well defined name to a text node.
     * @return the node id.
     */
    public String getId();

    /**
     * Sets the node id.
     * @param value - the node id as a String.
     */
    public void setId(String value);
    
    /**
     * @return A Map that records the attribute names and values set on this
     * text node.
     */
    public Map<String, String> getTextAttributes();

    /**
     * @return The List of child nodes of this text node. 
     */
    public List<TextNode> getTextChildren();

    /**
     * @return The list of child property nodes of this text node.
     */
    public Map<String, TextNode> getTextProperties();

    /**
     * Add a child property to this text node.
     * @param propertyName - the property's local name
     * @param node - the value node
     */
    public void addTextProperty(String propertyName, TextNode node);
}
