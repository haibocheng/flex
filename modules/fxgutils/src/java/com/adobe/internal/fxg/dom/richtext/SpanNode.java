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

import com.adobe.fxg.FXGException;
import com.adobe.fxg.dom.FXGNode;
import com.adobe.internal.fxg.dom.CDATANode;
import com.adobe.internal.fxg.dom.TextNode;

/**
 * Represents a &lt;p /&gt; child tag of FXG &lt;RichText&gt; content. A
 * &lt;p&gt; tag starts a new span in text content.
 * 
 * @since 2.0
 * @author Min Plunkett
 */
public class SpanNode extends AbstractRichTextLeafNode
{    
    //--------------------------------------------------------------------------
    //
    // FXGNode Implementation
    //
    //--------------------------------------------------------------------------

    /**
     * @return The unqualified name of a span node, without tag markup.
     */
    public String getNodeName()
    {
        return FXG_SPAN_ELEMENT;
    }
    
    /**
     * Adds an FXG child node to this span node. Supported child nodes
     * include text content nodes: br, tab. Number of instances: unbounded.
     * 
     * @param child - a child FXG node to be added to this node.
     * @throws FXGException if the child is not supported by this node.
     */
    public void addChild(FXGNode child)
    {
        if (child instanceof BRNode
                || child instanceof TabNode
                || child instanceof CDATANode)
        {
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
}
