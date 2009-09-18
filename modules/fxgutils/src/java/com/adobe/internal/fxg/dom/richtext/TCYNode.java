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
import java.util.List;

import com.adobe.fxg.FXGException;
import com.adobe.fxg.dom.FXGNode;
import com.adobe.internal.fxg.dom.CDATANode;
import com.adobe.internal.fxg.dom.TextNode;

/**
 * Represents a &lt;br /&gt; child tag of FXG &lt;RichText&gt; content. A
 * &lt;br /&gt; tag starts a new tcy in text content.
 * <p>
 * This is an empty tag - text content or child tags are not expected.
 * </p>
 * @since 2.0
 * @author Min Plunkett
 */
public class TCYNode extends AbstractRichTextLeafNode
{    
    //--------------------------------------------------------------------------
    //
    // Attributes
    //
    //--------------------------------------------------------------------------

    // LinkFormats property
    public LinkNormalFormatNode linkNormalFormat = null;
    public LinkHoverFormatNode linkHoverFormat = null;
    public LinkActiveFormatNode linkActiveFormat = null;    

    //--------------------------------------------------------------------------
    //
    // Text Node Attribute Helpers
    //
    //--------------------------------------------------------------------------

    /**
     * This node's child property nodes.
     */
    protected List<TextNode> properties;

    /**
     * @return The List of child property nodes of this text node.
     */
    public List<TextNode> getTextProperties()
    {
        return properties;
    }

    //--------------------------------------------------------------------------
    //
    // FXGNode Implementation
    //
    //--------------------------------------------------------------------------

    /**
     * @return The unqualified name of a tcy node, without tag markup.
     */
    public String getNodeName()
    {
        return FXG_TCY_ELEMENT;
    }
    
    /**
     * Adds an FXG child node to this TCY node. Supported child nodes
     * include text content nodes (e.g. span, br, tab, and img), and 
     * format nodes (e.g. linkNormalFormat, linkHoverFormat, and 
     * linkActiveFormat).
     * 
     * @param child - a child FXG node to be added to this node.
     * @throws FXGException if the child is not supported by this node.
     */
    public void addChild(FXGNode child)
    {
        if (child instanceof LinkNormalFormatNode)
        {
            if (linkNormalFormat == null)
            {
                linkNormalFormat = (LinkNormalFormatNode)child;
                addProperty(linkNormalFormat);
            }
            else
            {
                // Exception: Multiple LinkFormat elements are not allowed.
                throw new FXGException(getStartLine(), getStartColumn(), "MultipleLinkFormatElements");
            }
        }
        else if (child instanceof LinkHoverFormatNode)
        {
            if (linkHoverFormat == null)
            {
                linkHoverFormat = (LinkHoverFormatNode)child;
                addProperty(linkHoverFormat);
            }
            else
            {
                // Exception: Multiple LinkFormat elements are not allowed.
                throw new FXGException(getStartLine(), getStartColumn(), "MultipleLinkFormatElements");
            }
        }
        else if (child instanceof LinkActiveFormatNode)
        {
            if (linkActiveFormat == null)
            {
                linkActiveFormat = (LinkActiveFormatNode)child;
                addProperty(linkActiveFormat);
            }
            else
            {
                // Exception: Multiple LinkFormat elements are not allowed. 
                throw new FXGException(getStartLine(), getStartColumn(), "MultipleLinkFormatElements");
            }
        }
        else if (child instanceof SpanNode
                || child instanceof BRNode
                || child instanceof TabNode
                || child instanceof ImgNode
                || child instanceof LinkNode
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

    /**
     * A tcy node can also have special child property nodes that represent
     * complex property values that cannot be set via a simple attribute.
     */
    protected void addProperty(TextNode node)
    {
        if (properties == null)
            properties = new ArrayList<TextNode>(3);

        properties.add(node);
    }
}
