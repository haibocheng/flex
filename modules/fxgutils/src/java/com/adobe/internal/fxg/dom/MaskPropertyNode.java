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

import static com.adobe.fxg.FXGConstants.*;

import com.adobe.fxg.dom.FXGNode;

/**
 * The mask property node is a special delegate that associates a mask with a
 * parent graphic content node. 
 * 
 * @author Peter Farland
 */
public class MaskPropertyNode extends DelegateNode
{
    public MaskingNode mask;

    //--------------------------------------------------------------------------
    //
    // FXGNode Implementation
    //
    //--------------------------------------------------------------------------

    /**
     * @return The unqualified name of a mask node, without tag markup.
     */
    public String getNodeName()
    {
        return FXG_MASK_ELEMENT;
    }

    public void addChild(FXGNode child)
    {
        if (mask == null && child instanceof MaskingNode)
        {
            mask = (MaskingNode)child;
            delegate.addChild(this);
        }
        else
        {
            super.addChild(child);
        }
    }
}
