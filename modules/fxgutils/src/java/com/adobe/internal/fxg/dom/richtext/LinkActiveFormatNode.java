////////////////////////////////////////////////////////////////////////////////
//
//  ADOBE SYSTEMS INCORPORATED
//  Copyright 2009 Adobe Systems Incorporated
//  All Rights Reserved.
//
//  NOTICE: Adobe permits you to use, modify, and distribute this file
//  in accordance with the terms of the license agreement accompanying it.
//
////////////////////////////////////////////////////////////////////////////////

package com.adobe.internal.fxg.dom.richtext;

import static com.adobe.fxg.FXGConstants.*;



/**
 * Format applied to a link when active.
 * 
 * @since 2.0
 * @author Min Plunkett
 */
public class LinkActiveFormatNode extends AbstractRichTextLeafNode
{
    //--------------------------------------------------------------------------
    //
    // FXGNode Implementation
    //
    //--------------------------------------------------------------------------

    /**
     * @return The unqualified name of a linkActiveFormat node, without tag markup.
     */
    public String getNodeName()
    {
        return FXG_LINKACTIVEFORMAT_PROPERTY_ELEMENT;
    }
}
