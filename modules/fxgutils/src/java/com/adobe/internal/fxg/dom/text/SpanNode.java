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

package com.adobe.internal.fxg.dom.text;

import java.util.ArrayList;

import static com.adobe.fxg.FXGConstants.*;

import com.adobe.fxg.dom.FXGNode;
import com.adobe.internal.fxg.dom.CDATANode;
import com.adobe.internal.fxg.dom.TextNode;

/**
 * Represents a &lt;span /&gt; child tag of FXG text content. A &lt;span&gt;
 * tag starts a new section of formatting in a paragraph of text content.
 * 
 * @author Peter Farland
 */
public class SpanNode extends AbstractCharacterTextNode
{
    //--------------------------------------------------------------------------
    //
    // FXGNode Implementation
    //
    //--------------------------------------------------------------------------

    /**
     * A &lt;span&gt; allows child &lt;br /&gt; tags, as well as character
     * data (text content).
     */
    @Override
    public void addChild(FXGNode child)
    {
        if (child instanceof BRNode || child instanceof CDATANode)
        {
            if (content == null)
                content = new ArrayList<TextNode>();

            content.add((TextNode)child);
        }
        else 
        {
            super.addChild(child);
        }
    }

    /**
     * @return The unqualified name of a span node, without tag markup.
     */
    public String getNodeName()
    {
        return FXG_SPAN_ELEMENT;
    }
}
