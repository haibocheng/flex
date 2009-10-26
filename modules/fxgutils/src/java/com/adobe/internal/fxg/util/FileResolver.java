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

package com.adobe.internal.fxg.util;

import com.adobe.fxg.util.FXGResourceResolver;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

/**
 * Simple implementation of ResourceResolver to locate and load local files
 * from a specified path. This path may be resolved relative from an
 * additionally configured root path.
 */
public class FileResolver implements FXGResourceResolver
{
    private String rootPath;

    public FileResolver(String dir)
    {
        rootPath = dir;
    }
    
    public FileResolver()
    {        
    }

    public String getRootPath()
    {
        return rootPath;
    }

    public void setRootPath(String dir)
    {
        rootPath = dir;
    }

    public String resolve(String path)
    {
        File file = new File(path);
        if (!file.isAbsolute())
            file = new File(rootPath, path);

        return file.getAbsolutePath();
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
