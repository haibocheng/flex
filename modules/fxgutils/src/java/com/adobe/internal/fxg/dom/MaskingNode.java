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

/**
 * Marker interface to imply node can be used to create a mask.
 * 
 * @author Peter Farland
 */
public interface MaskingNode extends FXGNode
{
    GraphicContext createGraphicContext();

    /**
     * @return the index of a mask in a parent DisplayObject's list of children.
     * This can be used to access the mask programmatically at runtime.
     */
    int getMaskIndex();

    /**
     * Records the index of this mask in the parent DisplayObject's list of
     * children. (Optional).
     * @param index - the child index to the mask  
     */
    void setMaskIndex(int index);
}
