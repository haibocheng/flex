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

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import com.adobe.fxg.util.FXGResourceResolver;

import flex2.compiler.common.SinglePathResolver;
import flex2.compiler.io.VirtualFile;

/**
 * Provides a bridge between mxmlc's SinglePathResolver and fxgutils'
 * FXGResourceResolver.
 */
public class FlexResourceResolver implements FXGResourceResolver
{
    protected SinglePathResolver resolver;
    protected String rootPath;

    public FlexResourceResolver(SinglePathResolver resolver)
    {
        this.resolver = resolver;
    }

    public String getRootPath()
    {
        return rootPath;
    }

    public void setRootPath(String dir)
    {
        rootPath = dir;
    }

    public String resolve(String relative)
    {
        VirtualFile f = resolver.resolve(relative);
        if (f != null)
            return f.getName();

        return null; 
    }
    
    public InputStream openStream(String path) throws IOException
    {
        VirtualFile f = resolver.resolve(path);
        if (f != null)
            return f.getInputStream();

        return null;
    }

    public InputStream openStream(URL url) throws IOException
    {
        return url.openStream();
    }
}
