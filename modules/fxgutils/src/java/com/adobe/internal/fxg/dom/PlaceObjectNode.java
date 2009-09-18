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

/**
 * A PlaceObject node does not appear itself in an FXG document but rather
 * represents an instance of a DefinitionNode. An instance may redefine
 * attributes that override the defaults of the definition.
 * 
 * @author Peter Farland
 */
public class PlaceObjectNode extends GraphicContentNode implements MaskingNode
{
    /**
     * The Definition referenced by this instance.
     */
    public DefinitionNode definition;

    //--------------------------------------------------------------------------
    //
    // FXGNode Implementation
    //
    //--------------------------------------------------------------------------

    /**
     * @return The unqualified name of an instance of a definition (also known
     * as a 'PlaceObject' node), without tag markup.
     */
    public String getNodeName()
    {
        return definition != null ? definition.name : null;
    }
}
