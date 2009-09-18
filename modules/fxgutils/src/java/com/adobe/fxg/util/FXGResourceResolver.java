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

package com.adobe.fxg.util;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

/**
 * Implementations of ResourceResolver are used to locate and load resources,
 * such as embedded images, while processing an FXG DOM.
 */
public interface FXGResourceResolver
{
	String getRootPath();
	
	void setRootPath(String dir);

	InputStream openStream(String path) throws IOException;

	InputStream openStream(URL url) throws IOException;
}
