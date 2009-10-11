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

package com.adobe.internal.fxg.dom.richtext;

import static com.adobe.fxg.FXGConstants.*;

import java.util.ArrayList;
import java.util.HashMap;

import com.adobe.fxg.FXGException;
import com.adobe.fxg.dom.FXGNode;
import com.adobe.internal.fxg.dom.CDATANode;
import com.adobe.internal.fxg.dom.TextNode;

/**
 * Represents a &lt;p /&gt; FXG link node.
 * 
 * @since 2.0
 * @author Min Plunkett
 */
public class LinkNode extends AbstractRichTextLeafNode
{
    //--------------------------------------------------------------------------
    //
    // Attributes
    //
    //--------------------------------------------------------------------------

    // Link attributes
    public String href;  // Required
    public String target = "";
    
    // Link format properties
    public TextLayoutFormatNode linkNormalFormat = null;
    public TextLayoutFormatNode linkHoverFormat = null;
    public TextLayoutFormatNode linkActiveFormat = null;    

    //--------------------------------------------------------------------------
    //
    // TextNode Helpers
    //
    //--------------------------------------------------------------------------

    /**
     * This node's child property nodes.
     */
    protected HashMap<String, TextNode> properties;

    /**
     * @return The List of child property nodes of this text node.
     */
    public HashMap<String, TextNode> getTextProperties()
    {
        return properties;
    }

    /**
     * A link node can also have special child property nodes that represent
     * complex property values that cannot be set via a simple attribute.
     */
    public void addTextProperty(String propertyName, TextNode node)
    {
        if (node instanceof TextLayoutFormatNode)
        {
            if (FXG_LINKACTIVEFORMAT_PROPERTY_ELEMENT.equals(propertyName))
            {
                if (linkActiveFormat == null)
                {
                    linkActiveFormat = (TextLayoutFormatNode)node;
                    linkActiveFormat.setParent(this);

                    if (properties == null)
                        properties = new HashMap<String, TextNode>(3);
                    properties.put(propertyName, linkActiveFormat);
                }
                else
                {
                    // Exception: Multiple LinkFormat elements are not allowed.
                    throw new FXGException(getStartLine(), getStartColumn(), "MultipleLinkFormatElements");
                }
            }
            else if (FXG_LINKHOVERFORMAT_PROPERTY_ELEMENT.equals(propertyName))
            {
                if (linkHoverFormat == null)
                {
                    linkHoverFormat = (TextLayoutFormatNode)node;
                    linkHoverFormat.setParent(this);

                    if (properties == null)
                        properties = new HashMap<String, TextNode>(3);
                    properties.put(propertyName, linkHoverFormat);
                }
                else
                {
                    // Exception: Multiple LinkFormat elements are not allowed.
                    throw new FXGException(getStartLine(), getStartColumn(), "MultipleLinkFormatElements");
                }
            }
            else if (FXG_LINKNORMALFORMAT_PROPERTY_ELEMENT.equals(propertyName))
            {
                if (linkNormalFormat == null)
                {
                    linkNormalFormat = (TextLayoutFormatNode)node;
                    linkNormalFormat.setParent(this);

                    if (properties == null)
                        properties = new HashMap<String, TextNode>(3);
                    properties.put(propertyName, linkNormalFormat);
                }
                else
                {
                    // Exception: Multiple LinkFormat elements are not allowed. 
                    throw new FXGException(getStartLine(), getStartColumn(), "MultipleLinkFormatElements");
                }
            }
            else
            {
                // Exception: Unknown LinkFormat element. 
                throw new FXGException(node.getStartLine(), node.getStartColumn(), "UnknownLinkFormat", propertyName);
            }
        }
        else
        {
            addChild(node);
        }
    }


    //--------------------------------------------------------------------------
    //
    // FXGNode Implementation
    //
    //--------------------------------------------------------------------------

    public String getNodeName()
    {
        return FXG_A_ELEMENT;
    }

    /**
     * Adds an FXG child node to this link node. Supported child nodes
     * include text content nodes: span, br, tab, img.
     * 
     * @param child - a child FXG node to be added to this node.
     * @throws FXGException if the child is not supported by this node.
     */
    public void addChild(FXGNode child)
    {
        if (child instanceof SpanNode
                || child instanceof BRNode
                || child instanceof TabNode
                || child instanceof ImgNode
                || child instanceof TCYNode
                || child instanceof CDATANode)
        {
        	/**
        	 * When <a> has a <tcy> child, the <tcy> child is FORBIDDEN to have 
        	 * an <a> child of its own. AND vice versa. If a <tcy> has an <a> 
        	 * child, the <a> child is FORBIDDEN to have a <tcy> child.
        	 */
        	if (child instanceof TCYNode && this.parentNode instanceof TCYNode)
        	{
                // Exception: <tcy> element is forbidden as child of <a>, which 
        		// is child of another <tcy>. And vice versa.
                throw new FXGException(getStartLine(), getStartColumn(), "InvalidNestingElements");
        	}
            if (content == null)
            {
                content = new ArrayList<TextNode>();
            }
            content.add((TextNode)child);
        }
        else 
        {
            super.addChild(child);
            return;
        }

        if (child instanceof AbstractRichTextNode)
        	((AbstractRichTextNode)child).setParent(this);                
    }

    /**
     * This implementation processes link attributes that are relevant to
     * the &lt;p&gt; tag, as well as delegates to the parent class to process
     * character attributes that are also relevant to the &lt;p&gt; tag.
     *  
     * @param name the attribute name
     * @param value the attribute value
     * @see AbstractTextNode#setAttribute(String, String)
     */
    @Override
    public void setAttribute(String name, String value)
    {
        if (FXG_HREF_ATTRIBUTE.equals(name))
        {
            href = value;
        }
        else if(FXG_TARGET_ATTRIBUTE.equals(name))
        {
            target = value;
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
