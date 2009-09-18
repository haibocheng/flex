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

import javax.servlet.ServletContext;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Stores serverInfo and a modified getRealPath call that's needed for ATG
 *
 * @author Brian Deitte
 */
public class J2EEUtil
{
    private static String serverInfo;
    private static boolean atg;

    /**
     * Call this method instead of context.getRealPath(path).
     */
    public static String getRealPath(String path, ServletContext context)
    {
        if (context == null)
        {
            return path;
        }

        if (serverInfo == null)
        {
            setServerInfo(context.getServerInfo());
        }

        if (isATG())
        {
            // since getRealPath doesn't return anything useful for non-jsp/html pages on ATG,
            // we need to fake it.  We get the base real path directory and append what we're looking for,
            // and then just hope this is a real file
            String slashPath = context.getRealPath("/");
            File slashFile = new File(slashPath);
            if (slashFile.isFile())
            {
                // the default page is returned if present, but we don't want it
                slashPath = slashFile.getParent();
            }
            if (path != null && path.length() > 1)
            {
                path = slashPath + path;
            }
    	    else
    	    {
    	        path = slashPath;
    	    }
        }
        else
        {
            if (context.getRealPath(path) != null)
            {
                path = context.getRealPath(path);
            }
            // This happens with unexpanded war files
            else
            {
                try
                {
                    URL pathURL = context.getResource(path);                    
                    if (pathURL != null)
                    {
                        path = pathURL.getPath();
                    }
                } catch (MalformedURLException e)
                {
                    // path remains the same
                }
            }
        }
        return path;
    }

    public static void setServerInfo(String si)
    {
        serverInfo = si;
        atg = serverInfo.indexOf("ATG") != -1;
    }

    public static String getServerInfo()
    {
        return serverInfo;
    }

    public static boolean isATG()
    {
        if (serverInfo == null)
            throw new IllegalStateException("serverInfo has not been set");

        return atg;
    }
}
