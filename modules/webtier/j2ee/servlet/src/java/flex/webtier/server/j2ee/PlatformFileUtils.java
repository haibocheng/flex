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
package flex.webtier.server.j2ee;

import java.io.File;

/**
 * FileUtils methods that are available only on a JVM.
 * The .NET version of the file is PlatformFileUtils.jsl.
 */
public final class PlatformFileUtils
{
    public static boolean setLastModified(File f, long time)
    {
        try
        {
            f.setLastModified(time);
        }
        catch (Exception w)
        {
            return false;
        }
        return true;
    }

    /**
     * Normalize the given path/file name. This includes converting control
     * characters and removing "//" and "/./" patterns. Null will be returned
     * if the path could not be normalized. Reasons include:
     * <dir>
     * Invalid encoding characters
     * Encoded Control characters
     * Trailing "/.." in the path
     * </dir>
     *
     * @return The normalized path, or null if it could not be normalized
     */
    public static String normalize(String path)
    {
        path = removeDots(path);
        path = removeTwoSlashes(path);
        path = trimTrailingDotsSpacesNull(path);

        return path;
    }

    /* stolen from JRun 4 jrunx.utils.FileUtils, who grabbed it from apache 2.0.48 (server/util.c) */
    /* assumes path has been normalized to unix-style seperators */
    private static String removeDots(String path)
    {
        // Don't bother if no dots.
        if (path.indexOf('.') < 0)
        {
            return path;
        }

        /* Four paseses, as per RFC 1808 */
        /* a) remove ./ path segments */
        while (path.startsWith("./"))
        {
            path = path.substring(2);
        }

        int i;
        while ((i = path.indexOf("/./")) >= 0)
        {
            // delete "./"
            StringBuffer name = new StringBuffer(path);
            name.delete(i + 1, i + 3);
            path = name.toString();
        }

        /* b) remove trailing . path, segment */
        if (path.equals("."))
        {
            path = "";
        }
        else if (path.endsWith("/."))
        {
            // remove '.' character from end
            path = path.substring(0, path.length() - 1);
        }

        /* c) remove all xx/../ segments. (including leading ../ and /../) */
        StringBuffer name = new StringBuffer(path);
        int l = 0;
        while (l + 2 < name.length())
        {
            if (name.charAt(l) == '.' && name.charAt(l + 1) == '.' && name.charAt(l + 2) == '/'
                    && (l == 0 || name.charAt(l - 1) == '/'))
            {
                int m = l + 3, n;

                l = l - 2;
                if (l >= 0)
                {
                    while (l >= 0 && name.charAt(l) != '/')
                    {
                        l--;
                    }
                    l++;
                }
                else
                {
                    l = 0;
                }
                n = l;
                name.delete(n, m);
            }
            else
            {
                ++l;
            }
        }
        l = name.length();

        /* d) remove trailing xx/.. segment. */
        if (l == 2 && name.charAt(0) == '.' && name.charAt(1) == '.')
        {
            name.setLength(0);
        }
        else if (l > 2 && name.charAt(l - 1) == '.' && name.charAt(l - 2) == '.'
                && name.charAt(l - 3) == '/')
        {
            l = l - 4;
            if (l >= 0)
            {
                while (l >= 0 && name.charAt(l) != '/')
                {
                    l--;
                }
                l++;
            }
            else
            {
                l = 0;
            }
            name.setLength(l);
        }

        return name.toString();
    }

    private static String removeTwoSlashes(String uri)
    {
        // hueristic
        if (uri.indexOf("//") == -1)
            return uri;

        StringBuffer newUri = new StringBuffer();
        char[] buf = uri.toCharArray();

        for (int i = 0; i < buf.length; i++)
        {
            char c = buf[i];
            newUri.append(c);
            if (c == '/')
            {
                for (; i + 1 < buf.length && buf[i + 1] == '/'; i++)
                {
                    ;
                }
            }
        }
        return newUri.toString();
    }

    /**
     * On Windows "foo.jsp" == "foo.jsp." == "foo.jsp "
     * On Windows / Unix "foo.jsp" ==  "foo.jsp\u0000*"
     * This function removes trailing dots/spaces, all chars including and after null
     */
    public static String trimTrailingDotsSpacesNull(String uri)
    {
        // Remove nulls and anything subsequent to it.  Must do this first so
        // for example, foo.bar%20%20%00%00%20 has all spaces after foo.bar removed.
        int checkNull = uri.indexOf("\u0000");
        if (checkNull != -1 && checkNull < uri.length())
        {
            uri = uri.substring(0, checkNull);
        }

        // Check for Windows
        if (File.separatorChar == '\\')
        {
            // Remove trailing dots and spaces
            while (uri.endsWith(" ") || uri.endsWith("."))
            {
                uri = uri.substring(0, uri.length() - 1);
            }
        }
        return uri;
    }
}
