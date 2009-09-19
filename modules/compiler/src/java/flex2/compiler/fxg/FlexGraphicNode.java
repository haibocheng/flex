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

package flex2.compiler.fxg;

import com.adobe.internal.fxg.dom.GraphicNode;

/**
 * A Flex specific override for GraphicNode used to record whether the
 * FXG document includes any &lt;TextGraphic&gt; child nodes.
 * 
 * @author Peter Farland
 */
public class FlexGraphicNode extends GraphicNode
{
    public boolean hasText;
}
