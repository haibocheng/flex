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
import flash.util.Trace;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * This class should be used to resolve all Flex paths.
 *
 * @author Paul Reilly
 */

public class PathResolver
{
    protected String appPath;
    protected String localPath;
    protected String rootPath;

    private static ThreadLocal threadLocal = new ThreadLocal();
    private static String flexDir;

    public PathResolver()
    {
    }

    public PathResolver(String rootPath)
    {
        this.rootPath = FileUtils.canonicalPath(rootPath);
        File rootPathFile = new File(this.rootPath);
        assert rootPathFile.exists();
    }

    public static final void setThreadLocalPathResolver(PathResolver pathResolver)
    {
        threadLocal.set(pathResolver);
    }

    public static final PathResolver getThreadLocalPathResolver()
    {
        return (PathResolver) threadLocal.get();
    }

    public String getLocalPath()
    {
        return localPath;
    }

    public String getRootPath()
    {
        return rootPath;
    }


    /**
     * Try to resolve the path as an absolute path or relative path.
     *
     * @param path An absolute or relative path.
     * @param useLocalPath If true, the localPath will be used to try to resolve the path.
     * @return A File if successful, null otherwise.
     */

    public File resolveFile(String path, boolean useLocalPath)
    {
        File file = null;

        if (Trace.pathResolver)
        {
            Trace.trace("Looking for " + path + " : " + useLocalPath + " : " + rootPath + " : " + localPath);            
        }

        if (path != null)
        {
			try
			{
    		    file = new File(path);
			}
			catch (Error nf)
			{}

            if (file != null && file.exists() && file.isAbsolute())
            {
                try
                {
                    // we need to make sure the canonical file exists before returning it, otherwise when we later get
                    // the canonical file we may have a bad file reference.  Now, in theory, the regular file and
                    // the canonical file should both either exist or not exist.  But I've seen this bug with Java,
                    // so we need to do this here
                    file = FileUtils.getCanonicalFile(file);
                }
                catch (IOException ioe)
                {
                    if (Trace.error || Trace.pathResolver)
                        ioe.printStackTrace();

                    file = null;
                }

                if (Trace.pathResolver)
                    Trace.trace("Base file exists: " + file);
            }

            if ((file == null) || (!file.exists()) || (!file.isAbsolute()))
            {
                file = deduceFile(path, file, useLocalPath);

                if ( (file != null) && ((! file.exists() ) || (!file.isAbsolute())) )
                {
                    file = null;

                    if (Trace.pathResolver)
                        Trace.trace("File does not exist");
                }
                else if (Trace.pathResolver)
                {
                    Trace.trace("Resolved file: " + file);
                }
            }
        }

        return file;
    }

    protected File deduceFile(String path, File file, boolean useLocalPath)
    {
        // The reason we allow forward slash and backward slash is because there
        // are places in the compiler where we try to find "/WEB-INF/*", in order
        // for the look up to work via the web and the command line, we need to
        // support both slashes.
        if ((rootPath != null) && (path.startsWith("/") ||
                                        path.startsWith(File.separator)))
        {
			try
			{
				file = new File(rootPath + path);
			}
			catch (Error nf)
			{}

            if (Trace.pathResolver)
                Trace.trace("Attempting to resolve from root path " + rootPath);
        }
        else
        {
            if (appPath != null)
            {
				try
				{
					file = new File(appPath + File.separator + path);
				} 
				catch (Error nf)
				{}

                if (Trace.pathResolver)
                    Trace.trace("Attempting to resolve from app path " + appPath);
            }

            if (useLocalPath && ((file == null || !file.exists()) && (localPath != null)))
            {
                file = new File(localPath + File.separator + path);

                if (Trace.pathResolver)
                    Trace.trace("Attempting to resolve from local path " + localPath);
            }
        }
        return file;
    }

    /**
     * Try to turn a path into a URL relative to contextRoot.
     * This will not work with virtual directory mappings!
     * @param path the file to resolve.
     * @return either the relative URL, or null if not found.
     */
    public String reverseResolve( String contextRoot, String path )
    {
        File resolved = resolveFile( path, true );
        if (resolved == null)
            return null;
        String resolvedpath = resolved.getAbsolutePath();

        if (resolvedpath.startsWith( rootPath ))
        {
            String truncated = resolvedpath.substring( rootPath.length() );

            truncated = truncated.replace( File.separatorChar, '/' );

            return FileUtils.addPathComponents( contextRoot, truncated, '/' );
        }

        return null;
    }

    /**
	 * Try to resolve a URI as either a URL or File.
	 *
	 * @param uri The path to the resource trying to be located.
	 * @return Either a URL, a File or null if not found.
	 */
	public Object resolveLocation(String uri)
	{
        Object location = null;
        
		try
		{
			location = new URL(uri);
		}
		catch (MalformedURLException malformedURLException)
		{
			File file = resolveFile(uri, true);

			if ((file != null) && file.exists())
            {
                location = file;
            }
		}
        
        return location;
	}

    /**
     * This sets the appPath and the localPath, because when you first start compiling an
     * mxml document they will always be the same.
     */
    public void setAppPath( String appPath )
    {
        this.appPath = appPath;
        this.localPath = appPath;
    }

    public String getAppPath()
    {
        return appPath;
    }

    public void setLocalPath( String localPath )
    {
        this.localPath = localPath;
    }

    public String getFlexReadPath()
    {
        File result;

        // cmurphy: note that using a system property restricts the user to a single Flex application in the same
        // vm;  when using flex.dir, we do not support multiple web applications (dev.war/flex.war/samples.war)
        // in the same vm
        if (System.getProperty("flex.dir") != null)
        {
            // default location for platform can be overridden
            result = resolveFile( System.getProperty("flex.dir"), false );
        }
        else if (System.getProperty("flex.platform.CLR") != null)
        {
            result = resolveFile("/bin/flex", false);
        }
        // used by CF integration
        else if (flexDir != null)
        {
            result = resolveFile(flexDir, false);
        }
        else
        {
            // Java version looks into WEB-INF
            result = resolveFile("/WEB-INF/flex", false);
        }
        return (result == null)? null : FileUtils.canonicalPath( result );
    }

    public static void setFlexDir(String flexDirName)
    {
        PathResolver.flexDir = flexDirName;
    }

	public String getFlexWritePath()
	{
		File result;                                            

        // cmurphy: note that using a system property restricts the user to a single Flex application in the same
        // vm;  when using flex.dir, we do not support multiple web applications (dev.war/flex.war/samples.war)
        // in the same vm
		if (System.getProperty("flex.dir") != null)
		{
			// default location for platform can be overridden
			result = resolveFile( System.getProperty("flex.dir"), false );
		}
		else if (System.getProperty("flex.platform.CLR") != null)
		{
			result = resolveFile(rootPath, false);
		}
        // used by CF integration
        else if (flexDir != null)
        {
            result = resolveFile(flexDir, false);
        }
		else
		{
			// Java version looks into WEB-INF
			result = resolveFile("/WEB-INF/flex", false);
		}
		return (result == null)? null : FileUtils.canonicalPath( result );
	}
}
