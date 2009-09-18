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
 * A marker interface to denote that an FXG node represents a type of fill. 
 * 
 * @author Peter Farland
 */
public interface FillNode extends FXGNode
{
    /**
     * An id attribute provides a well defined name to a fill node.
     * @return the node id.
     */
    public String getId();

    /**
     * Sets the node id.
     * @param value - the node id as a String.
     */
    public void setId(String value);
}
