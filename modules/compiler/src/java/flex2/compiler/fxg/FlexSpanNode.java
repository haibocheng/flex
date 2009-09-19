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

package flex2.compiler.fxg;

import com.adobe.internal.fxg.dom.text.SpanNode;

/**
 * A Flex specific override for SpanNode used to capture the 
 * attributes specified on a &lt;span&gt; node in FXG.
 * 
 * @author Peter Farland
 */
public class FlexSpanNode extends SpanNode
{
    /**
     * Flex specific override to keep track of the attributes set on this
     * span node.
     * 
     * @param name the attribute name
     * @param value the attribute value
     * @see SpanNode#setAttribute(String, String)
     * @see AbstractTextNode#setAttribute(String, String)
     */
    @Override
    public void setAttribute(String name, String value)
    {
        super.setAttribute(name, value);

        // Translate FXG attributes to equivalent Flex properties
        String newName = FlexTextGraphicNode.translateAttribute(name);
        if (!name.equals(newName))
        {
            if (textAttributes != null)
                textAttributes.remove(name);

            rememberAttribute(newName, value);
        }
    }
}
