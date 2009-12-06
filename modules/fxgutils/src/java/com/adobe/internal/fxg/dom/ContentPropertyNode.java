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

import com.adobe.fxg.FXGException;
import com.adobe.fxg.dom.FXGNode;

/**
 * This is a special delegate which special cases content node children for
 * the TextNode and RichTextNode classes.
 * 
 */
public class ContentPropertyNode extends DelegateNode
{
    public void setDelegate(FXGNode delegate)
    {
        if (delegate instanceof RichTextNode && ((RichTextNode)delegate).content != null)
            throw new FXGException(getStartLine(), getStartColumn(), "MultipleContentElements");             
   
        super.setDelegate(delegate);
    }
    
    //--------------------------------------------------------------------------
    //
    // FXGNode Implementation
    //
    //--------------------------------------------------------------------------

    public void addChild(FXGNode child)
    {
        if (delegate instanceof TextGraphicNode)
        {
            ((TextGraphicNode)delegate).addContentChild(child);
        }
        else if (delegate instanceof RichTextNode)
        {
            ((RichTextNode)delegate).addContentChild(child);
        }
        else
        {
            super.addChild(child);
        }
    }
}
