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

import com.adobe.fxg.FXGException;
import com.adobe.fxg.dom.FXGNode;

/**
 * Represents a &lt;br /&gt; child tag of FXG &lt;RichText&gt; content. A
 * &lt;br /&gt; tag starts a new tab in text content.
 * <p>
 * This is an empty tag - text content or child tags are not expected.
 * </p>
 * @since 2.0
 * @author Min Plunkett
 */
public class TabNode extends AbstractRichTextLeafNode
{    
    //--------------------------------------------------------------------------
    //
    // FXGNode Implementation
    //
    //--------------------------------------------------------------------------

    /**
     * @return The unqualified name of a tab node, without tag markup.
     */
    public String getNodeName()
    {
        return FXG_TAB_ELEMENT;
    }
    
    /**
     * Tab node doesn't allow any children. Throws an exception when adding an 
     * FXG child node to this Tab node.
     *
     * @param child - a child FXG node to be added to this node.
     * @throws FXGException if the child is not supported by this node.
     */
    public void addChild(FXGNode child)
    {
        throw new FXGException(child.getStartLine(), child.getStartColumn(), "InvalidChildNode",  child.getNodeName(), getNodeName());            
    }
}