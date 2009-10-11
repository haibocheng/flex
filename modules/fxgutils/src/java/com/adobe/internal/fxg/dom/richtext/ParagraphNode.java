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

import static com.adobe.fxg.FXGConstants.FXG_LINKACTIVEFORMAT_PROPERTY_ELEMENT;
import static com.adobe.fxg.FXGConstants.FXG_LINKHOVERFORMAT_PROPERTY_ELEMENT;
import static com.adobe.fxg.FXGConstants.FXG_LINKNORMALFORMAT_PROPERTY_ELEMENT;
import static com.adobe.fxg.FXGConstants.FXG_P_ELEMENT;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.adobe.fxg.FXGException;
import com.adobe.fxg.dom.FXGNode;
import com.adobe.internal.fxg.dom.CDATANode;
import com.adobe.internal.fxg.dom.TextNode;

/**
 * Represents a &lt;p /&gt; child tag of FXG &lt;RichText&gt; content. A
 * &lt;p&gt; tag starts a new paragraph in text content.
 * 
 * @since 2.0
 * @author Min Plunkett
 */
public class ParagraphNode extends AbstractRichParagraphNode
{    
    //--------------------------------------------------------------------------
    //
    // Attributes
    //
    //--------------------------------------------------------------------------

    // Link format properties
    public TextLayoutFormatNode linkNormalFormat = null;
    public TextLayoutFormatNode linkHoverFormat = null;
    public TextLayoutFormatNode linkActiveFormat = null;    
    
    //--------------------------------------------------------------------------
    //
    // Text Node Attribute Helpers
    //
    //--------------------------------------------------------------------------

    /**
     * This node's child property nodes.
     */
    protected Map<String, TextNode> properties;

    /**
     * @return The List of child property nodes of this text node.
     */
    public Map<String, TextNode> getTextProperties()
    {
        return properties;
    }

    /**
     * A paragraph node can also have special child property nodes that
     * represent complex property values that cannot be set via a simple
     * attribute.
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

    /**
     * @return The unqualified name of a paragraph node, without tag markup.
     */
    public String getNodeName()
    {
        return FXG_P_ELEMENT;
    }

    /**
     * Adds an FXG child node to this Paragraph node. Supported child nodes
     * include text content nodes (e.g. tcy, a, span, tab, br, and img).
     * 
     * Note that link format nodes (e.g. linkNormalFormat, linkHoverFormat, and 
     * linkActiveFormat) are complex properties rather than child nodes.
     * 
     * @param child - a child FXG node to be added to this node.
     * @throws FXGException if the child is not supported by this node.
     */
    public void addChild(FXGNode child)
    {
        if (child instanceof TCYNode
                || child instanceof LinkNode
                || child instanceof SpanNode
                || child instanceof BRNode
                || child instanceof TabNode
                || child instanceof ImgNode
                || child instanceof CDATANode)
        {
        	if (child instanceof LinkNode && (((LinkNode)child).href == null))
        	{
        		// Exception: Missing href attribute in <a> element.
                throw new FXGException(getStartLine(), getStartColumn(), "MissingHref");        		
        	}  
        	
            if (content == null)
                content = new ArrayList<TextNode>();

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
}
