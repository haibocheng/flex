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

import com.adobe.fxg.FXGException;
import com.adobe.fxg.dom.FXGNode;
import com.adobe.internal.fxg.dom.DelegateNode;
import com.adobe.internal.fxg.dom.TextNode;

/**
 * 
 * @author Peter Farland
 */
public class TextPropertyNode extends DelegateNode
{
    //--------------------------------------------------------------------------
    //
    // FXGNode Implementation
    //
    //--------------------------------------------------------------------------

    public void addChild(FXGNode child)
    {
        if (delegate instanceof TextNode && child instanceof TextNode)
        {
            ((TextNode)delegate).addTextProperty(getNodeName(), (TextNode)child);
        }
        else if (this instanceof TextPropertyNode && !(delegate instanceof TextNode))
        {
            throw new FXGException(child.getStartLine(), child.getStartColumn(), "InvalidChildNode",  getNodeName(), delegate.getNodeName());                        
        }
        else    
        {
            super.addChild(child);
        }
    }
}
