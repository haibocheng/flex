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

package com.adobe.internal.fxg.dom.text;

import static com.adobe.fxg.FXGConstants.*;

/**
 * Represents a &lt;br /&gt; child tag of FXG &lt;TextGraphic&gt; content. A
 * &lt;br /&gt; tag acts as a line separator for text content.
 * <p>
 * This is an empty tag - text content or child tags are not expected.
 * </p>
 * @author Peter Farland
 */
public class BRNode extends AbstractTextNode
{
    //--------------------------------------------------------------------------
    //
    // FXGNode Implementation
    //
    //--------------------------------------------------------------------------

    /**
     * @return The unqualified name of a br node, without tag markup.
     */
    public String getNodeName()
    {
        return FXG_BR_ELEMENT;
    }
}
