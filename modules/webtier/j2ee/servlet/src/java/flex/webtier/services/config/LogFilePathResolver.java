////////////////////////////////////////////////////////////////////////////////
//
//  ADOBE SYSTEMS INCORPORATED
//  Copyright 2005-2007 Adobe Systems Incorporated
//  All Rights Reserved.
//
//  NOTICE: Adobe permits you to use, modify, and distribute this file
//  in accordance with the terms of the license agreement accompanying it.
//
////////////////////////////////////////////////////////////////////////////////
package flex.webtier.services.config;

import flex.webtier.util.PathResolver;
import flex.webtier.util.ServletPathResolver;

import java.io.File;

/**
 * The log file has its own path resolver because unlike other path resolution.  The log file
 * nor its parent directories are required to exist when the path is resolved.
 */
public class LogFilePathResolver extends PathResolver
{
    ServletPathResolver servletPathResolver = null;

    public LogFilePathResolver()
    {

    }

    public LogFilePathResolver(ServletPathResolver servletPathResolver)
    {
        this.servletPathResolver = servletPathResolver;
    }

    public File resolveFile(String path, boolean useLocalPath)
    {
        File resolved = null;
        String resolvedPath = null;
        boolean isWindows = File.separator.equals("\\");

        if (path != null)
        {
            if (isWindows)
            {
                if ((path.startsWith("/")) && (servletPathResolver != null))
                {
                    resolvedPath = servletPathResolver.getRealPath(path, false);
                }
                else if (new File(path).isAbsolute())
                {
                    resolvedPath = path;
                }
            }
            else
            {
                File testFile = new File(path);
                if (testFile.isAbsolute())
                {
                    // try to find a parent directory which exists
                    // this gives some clue as to whether
                    while (testFile != null && !testFile.exists())
                    {
                        testFile = testFile.getParentFile();
                    }

                    if ((testFile.toString().equals("/")) && (servletPathResolver != null))
                    {
                        resolvedPath = servletPathResolver.getRealPath(path, false);
                    }
                    else
                    {
                        resolvedPath = path;
                    }
                }
            }
        }

        if (resolvedPath != null)
        {
            resolved = new File(resolvedPath);
        }

        return resolved;
    }

}
