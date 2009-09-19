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

import com.adobe.internal.fxg.dom.text.ParagraphNode;

/**
 * A Flex specific override for ParagraphNode used catch attributes that need to
 * be renamed on a &lt;p&gt; tag.
 * 
 * @author Peter Farland
 * @since 1.0
 */
public class FlexParagraphNode extends ParagraphNode
{
    /**
     * Flex specific override to keep track of attributes that need to be
     * renamed.
     * 
     * @param name the attribute name
     * @param value the attribute value
     * @see ParagraphNode#setAttribute(String, String)
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
