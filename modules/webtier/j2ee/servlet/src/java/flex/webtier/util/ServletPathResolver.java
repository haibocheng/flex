////////////////////////////////////////////////////////////////////////////////
//
//  ADOBE SYSTEMS INCORPORATED
//  Copyright 2004-2007 Adobe Systems Incorporated
//  All Rights Reserved.
//
//  NOTICE: Adobe permits you to use, modify, and distribute this file
//  in accordance with the terms of the license agreement accompanying it.
//
////////////////////////////////////////////////////////////////////////////////
package flex.webtier.util;

import flash.util.FileUtils;

import java.io.File;

import javax.servlet.ServletContext;

/**
 * J2EE Servlet-specific PathResolver utility functions.
 */
public class ServletPathResolver extends PathResolver
{
    private ServletContext servletContext;

    public ServletPathResolver(ServletContext servletContext)
    {
        this.servletContext = servletContext;
        rootPath = FileUtils.canonicalPath(J2EEUtil.getRealPath("/", servletContext));      
        assert (rootPath != null);
        File rootPathFile = new File(rootPath);
        assert (rootPathFile.exists());
    }


    public File resolveFile(String path, boolean useLocalPath)
    {
        File resolved = null;

        try
        {
            if (path != null)
            {
                boolean isWindows = File.separator.equals("\\");

                if ((isWindows) && (resolved == null) && (servletContext != null) && (path.startsWith("/")))
                {
                    resolved = getLocalFileFromRealPath(path);
                }

                if (resolved == null)
                {
                    resolved = super.resolveFile(path, useLocalPath);    
                }
            }            
        }
        catch (Throwable t)
        {
            if (Trace.error)
            {
                t.printStackTrace();
            }
        }

        return resolved;
    }

    protected File deduceFile(String path, File file, boolean useLocalPath)
    {
        if ((servletContext != null) && path.startsWith("/"))
        {
            file = resolveRealPath(path);

            if (Trace.pathResolver)
                Trace.trace("Attempted to resolve real path " + path + " : " + file);
        }
        else
        {
            file = super.deduceFile(path, file, useLocalPath);
        }
        return file;
    }

    private File getLocalFileFromRealPath(String path)
    {
        String realpath = J2EEUtil.getRealPath(path, servletContext);
        if (realpath != null)
        {
            return getLocalFile(realpath);
        }
        else
        {
            return null;
        }
    }

    /**
     * A local file must exist and be specified absolutely.
     */
    private File getLocalFile(String path)
    {
        File f = new File(path);
        if (f != null && f.exists() && f.isAbsolute())
        {
            return f;
        }
        else
        {
            return null;
        }
    }

    /**
     * Try to resolve a URI only as a File.  This is useful if path is know not to be a
     * URL.
     *
	 * @param path The path to a virtual resource.
	 * @return A File or null if not found.
     */
    private File resolveRealPath(String path)
    {
        File result = null;

        if (servletContext != null)
        {
            String realPath = J2EEUtil.getRealPath(path, servletContext);

            if (realPath != null)
            {
                result = new File(realPath);

                if (! result.exists())
                {
                    result = null;
                }
            }
        }

        return result;
    }

    public String getRealPath(String path, boolean mustExist)
    {
        String resolvedPath = null;

        if (servletContext != null)
        {
            resolvedPath = J2EEUtil.getRealPath(path, servletContext);

            if (mustExist)
            {
                if (resolvedPath != null)
                {
                    File resolved = new File(resolvedPath);
                    if ((!resolved.exists()) || (resolved.isAbsolute()))
                    {
                        resolvedPath = null;
                    }
                }
            }
        }

        return resolvedPath;
    }
}
