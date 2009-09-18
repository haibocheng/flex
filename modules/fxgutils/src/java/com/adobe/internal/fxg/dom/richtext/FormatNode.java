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
 * Represents a &lt;p /&gt; FXG text format.
 * 
 * @since 2.0
 * @author Min Plunkett
 */
public class FormatNode extends AbstractRichBlockTextNode
{
    //--------------------------------------------------------------------------
    //
    // Attributes
    //
    //--------------------------------------------------------------------------

    public String name;
    
    //--------------------------------------------------------------------------
    //
    // FXGNode Implementation
    //
    //--------------------------------------------------------------------------

    public String getNodeName()
    {
        return FXG_FORMAT_PROPERTY_ELEMENT;
    }

    /**
     * This implementation processes format attributes that are relevant to
     * the &lt;p&gt; tag, as well as delegates to the parent class to process
     * character attributes that are also relevant to the &lt;p&gt; tag.
     * 
     * <p>
     * Format attributes include:
     * <ul>
     * <li><b>name</b> (String): The name of the format. Default is "".</li>
     * </ul>
     * </p>
     * 
     * @param name the attribute name
     * @param value the attribute value
     * @see AbstractRichTextNode#setAttribute(String, String)
     */
    @Override
    public void setAttribute(String name, String value)
    {
        if (FXG_NAME_ATTRIBUTE.equals(name))
        {
            this.name = value;
        }
        else
        {
            super.setAttribute(name, value);
            return;
        }

        // Remember attribute was set on this node.
        rememberAttribute(name, value);        
    }
}
