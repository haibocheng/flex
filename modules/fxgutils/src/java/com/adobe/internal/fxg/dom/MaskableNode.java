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

import com.adobe.fxg.dom.FXGNode;
import com.adobe.internal.fxg.dom.types.MaskType;

/**
 * This interface implies that a node may also have a mask.
 * 
 * @author Peter Farland
 */
public interface MaskableNode extends FXGNode
{
    public MaskingNode getMask();

    public MaskType getMaskType();
}
