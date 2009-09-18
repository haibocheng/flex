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

import java.util.HashMap;

import com.adobe.fxg.FXGException;
import com.adobe.fxg.dom.FXGNode;


import static com.adobe.fxg.FXGConstants.*;

/**
 * Represents the special &lt;Library&gt; section of an FXG document.
 * <p>
 * A Library contains a series of named &lt;Definition&gt; nodes that themselves
 * do not contribute to the visual representation but rather serve as
 * 'templates' that can be referenced by name throughout the document. A
 * reference to a definition is known as an 'instance' and is represented in the
 * tree as a special PlaceObjectNode (the term PlaceObject refers to the SWF tag
 * that places an instance on the stage). Instances can provide their own values
 * that override the defaults from the definition.
 * </p>
 * 
 * @author Peter Farland
 */
public class LibraryNode extends AbstractFXGNode
{
    //--------------------------------------------------------------------------
    //
    // Children
    //
    //--------------------------------------------------------------------------

    //---------------
    // <Definition>
    //---------------

    public HashMap<String, DefinitionNode> definitions;

    /**
     * Locates a Definition node in this Library by name.
     * 
     * @param name - the name of the definition
     * @return a Definition for the given name, or null if none exists.
     */
    public DefinitionNode getDefinition(String name)
    {
        if (definitions != null)
            return definitions.get(name);
        else
            return null;
    }

    //--------------------------------------------------------------------------
    //
    // FXGNode Implementation
    //
    //--------------------------------------------------------------------------

    @Override
    public void addChild(FXGNode child)
    {
        if (child instanceof DefinitionNode)
        {
            if (definitions == null)
                definitions = new HashMap<String, DefinitionNode>();

            DefinitionNode node = (DefinitionNode)child;
            if (node.name == null)
                throw new FXGException(child.getStartLine(), child.getStartColumn(), "MissingDefinitionName");

            definitions.put(node.name, node);
        }
        else
        {
            super.addChild(child);
        }
    }

    /**
     * @return The unqualified name of a Library node, without tag markup.
     */
    public String getNodeName()
    {
        return FXG_LIBRARY_ELEMENT;
    }
}
