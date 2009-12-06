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
import com.adobe.fxg.FXGException;
import com.adobe.fxg.dom.FXGNode;

/**
 * A &lt;Definition&gt; is a special template node that is not itself rendered
 * but rather can be referenced by name in an FXG document.
 * 
 * @author Peter Farland
 */
public class DefinitionNode extends AbstractFXGNode
{
    //--------------------------------------------------------------------------
    //
    // Attributes
    //
    //--------------------------------------------------------------------------

    public String name;

    //--------------------------------------------------------------------------
    //
    // Children
    //
    //--------------------------------------------------------------------------

    public GroupDefinitionNode groupDefinition;

    //--------------------------------------------------------------------------
    //
    // FXGNode Implementation
    //
    //--------------------------------------------------------------------------

    @Override
    public void addChild(FXGNode child)
    {
        if (child instanceof GroupDefinitionNode)
        {
            if (groupDefinition != null)
            	//Exception:Definitions must define a single Group child node.
                throw new FXGException(child.getStartLine(), child.getStartColumn(), "MissingGroupChildNode");

            groupDefinition = (GroupDefinitionNode)child;
        }
        else
        {
            super.addChild(child);
        }
    }

    /**
     * @return The unqualified name of a Definition node, without tag markup.
     * i.e. literally 'Definition'. To retrieve the Definition name attribute,
     * refer to the name attribute itself.
     */
    public String getNodeName()
    {
        return FXG_DEFINITION_ELEMENT;
    }

    @Override
    public void setAttribute(String name, String value)
    {
        if (FXG_NAME_ATTRIBUTE.equals(name))
        {
            this.name = DOMParserHelper.parseIdentifier(this, value, name);
            if (((GraphicNode)this.getDocumentNode()).reservedNodes.containsKey(value))
                throw new FXGException(getStartLine(), getStartColumn(), "InvalidDefinitionName", value);
        }
        else
        {
            super.setAttribute(name, value);
        }
    }
}
