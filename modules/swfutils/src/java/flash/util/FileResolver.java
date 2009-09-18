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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

public class FileResolver implements ResourceResolver
{
    private String rootPath;

    public String getRootPath()
    {
        return rootPath;
    }

    public void setRootPath(String dir)
    {
        rootPath = dir;
    }

    public InputStream openStream(String path) throws IOException
    {
        File file = new File(path);
        if (!file.isAbsolute())
            file = new File(rootPath, path);

        FileInputStream fis = new FileInputStream(file);
        return fis;
    }

    public InputStream openStream(URL url) throws IOException
    {
        return url.openStream();
    }
}
