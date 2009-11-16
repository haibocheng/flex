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

package com.adobe.internal.fxg.dom.richtext;

import static com.adobe.fxg.FXGConstants.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.adobe.fxg.FXGException;
import com.adobe.fxg.dom.FXGNode;
import com.adobe.internal.fxg.dom.AbstractFXGNode;
import com.adobe.internal.fxg.dom.CDATANode;
import com.adobe.internal.fxg.dom.TextNode;

/**
 * A base class for all FXG nodes concerned with formatted text.
 * 
 * @since 2.0
 * @author Min Plunkett
 */
public abstract class AbstractRichTextNode extends AbstractFXGNode implements TextNode
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
    // Text Node Attribute Helpers
    //
    //--------------------------------------------------------------------------

    /**
     * The attributes set on this node.
     */
    protected Map<String, String> textAttributes;

    /**
     * @return A Map recording the attribute names and values set on this
     * text node.
     */
    public Map<String, String> getTextAttributes()
    {
        return textAttributes;
    }

    /**
     * This nodes child text nodes.
     */
    protected List<TextNode> content;

    /**
     * @return The List of child nodes of this text node. 
     */
    public List<TextNode> getTextChildren()
    {
        return content;
    }

    /**
     * @return The Map of child property nodes of this text node.
     */
    public Map<String, TextNode> getTextProperties()
    {
        return null;
    }

    /**
     * A text node may also have special child property nodes that represent
     * complex property values that cannot be set via a simple attribute.
     */
    public void addTextProperty(String propertyName, TextNode node)
    {
        addChild(node);
    }

    /**
     * Remember that an attribute was set on this node.
     * 
     * @param name - the unqualified attribute name.
     * @param value - the attribute value.
     */
    protected void rememberAttribute(String name, String value)
    {
        if (textAttributes == null)
            textAttributes = new HashMap<String, String>(4);

        textAttributes.put(name, value);
    }
    
    /**
     * Keep a reference to the parent node.
     */
    public FXGNode parentNode; 
    
    /**
     * Set the parent node.
     * 
     * @param parent - the parent node.
     */
    public void setParent(FXGNode parent)
    {
    	parentNode = parent;
    }

    //--------------------------------------------------------------------------
    //
    // FXGNode Implementation
    //
    //--------------------------------------------------------------------------

    /**
     * Check child node to ensure that exception isn't thrown for ignorable 
     * white spaces.
     * 
     * @param child - a child FXG node to be added to this node.
     */
    public void addChild(FXGNode child)
    {
        if (content == null)
        {
        	if (child instanceof CDATANode && TextHelper.ignorableWhitespace(((CDATANode)child).content))
        	{
                /**
                 * Ignorable white spaces don't break content contiguous 
                 * rule and should be ignored if they are at the beginning 
                 * of a element value.
                 */
        		return;
        	}
        	else
        	{
        	    super.addChild(child);
        	}
        }
        else 
        {
            super.addChild(child);
        }           
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
        {
            id = value;
        }
        else
        {
            super.setAttribute(name, value);
            return;
        }

        // Remember attribute was set on this node.
        rememberAttribute(name, value);
    }
}
