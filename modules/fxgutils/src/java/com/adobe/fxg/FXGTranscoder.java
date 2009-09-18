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
package com.adobe.fxg;

import com.adobe.fxg.dom.FXGNode;
import com.adobe.fxg.util.FXGResourceResolver;

/**
 * Simple interface for a transcoder on an FXG DOM.
 */
public interface FXGTranscoder
{
    /**
     * Establishes the ResourceResolver implementation used to locate and load
     * resources such as embedded images for BitmapGraphic nodes.
     * 
     * @param resolver
     */
    public void setResourceResolver(FXGResourceResolver resolver);


    /**
     * Traverses the FXG DOM from the given node, returning a transcoded result.
     * 
     * @param node
     * @return
     */
	public Object transcode(FXGNode node);
}
