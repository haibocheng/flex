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
package flex.webtier.server.j2ee;

import flex.webtier.util.J2EEUtil;
import flex.webtier.util.Trace;
import flex2.compiler.common.ConfigurationPathResolver;
import flex2.compiler.io.FileUtil;
import flex2.compiler.io.LocalFile;
import flex2.compiler.io.VirtualFile;
import java.io.File;
import javax.servlet.ServletContext;

public class ServletPathResolver extends ConfigurationPathResolver
{
    private String root = null;
    private ServletContext context = null;

    public ServletPathResolver(ServletContext context)
    {
        this.context = context;
    }

    public void setRoot(String root)
    {
        this.root = resolve(root).getName();
    }

    /**
     * A local file must exist and be specified absolutely.
     */
    private LocalFile getLocalFile(String path)
    {
        File f = new File(path);
        if (f != null && f.exists() && f.isAbsolute())
        {
            return new LocalFile(f);
        }
        else
        {
            return null;
        }
    }

    private LocalFile getLocalFileFromRealPath(String path)
    {
        String realpath = J2EEUtil.getRealPath(path, context);
        if (realpath != null)
        {
            return getLocalFile(realpath);
        }
        else
        {
            return null;
        }
    }

    public VirtualFile resolve(String path)
    {
        VirtualFile resolved = null;

        try
        {
            if (path != null)
            {
                boolean isWindows = File.separator.equals("\\");

                // on windows, try to resolve relative to context root first
                if ((isWindows) && (resolved == null) && (context != null) && (path.startsWith("/")))
                {
                    resolved = getLocalFileFromRealPath(path);
                }

                if (resolved == null)
                {
                    File f = FileUtil.openFile(path);
                    // try to resolve an absolute file
                    resolved = getLocalFile(path);

                    if ((!isWindows) && (resolved == null) && (context != null) && (path.startsWith("/")))
                   {
                        resolved = getLocalFileFromRealPath(path);
                    }

                    // try to resolve relative the the specified root
                    // unless the original file was specified absolutely
                    if ((resolved == null) && (root != null) && (!f.isAbsolute()))
                    {
                        path = root + File.separator + path;
                        resolved = getLocalFile(path);
                    }
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

}
