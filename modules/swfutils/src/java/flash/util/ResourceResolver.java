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

package flash.util;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

public interface ResourceResolver
{
    String getRootPath();
    void setRootPath(String dir);

    InputStream openStream(String path) throws IOException;

    InputStream openStream(URL url) throws IOException;
}
