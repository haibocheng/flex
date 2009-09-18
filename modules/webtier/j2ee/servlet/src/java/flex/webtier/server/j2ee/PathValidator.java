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

import flex.webtier.compiler.WrongCaseException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.StringTokenizer;
import javax.servlet.http.HttpServletRequest;

public class PathValidator
{
    private static boolean isWindows = File.separatorChar == '\\';
        
    public static void checkFileExists(String mxmlFileName, HttpServletRequest request) throws FileNotFoundException
    {
        File mxmlFile = new File(mxmlFileName);
        if ((!mxmlFile.isFile()) || (!mxmlFile.exists()))
        {
            throw new FileNotFoundException("File not found: " + mxmlFileName);
        }
        else
        {
            if (isWindows)
            {
                String filename = mxmlFile.getName().toUpperCase();
                if (mxmlFile.getName().lastIndexOf('.') != -1)
                {
                    filename = filename.substring(0, filename.lastIndexOf('.')).toUpperCase();
                }

                if (filename.endsWith("NUL") ||
                    filename.endsWith("PRN") ||
                    filename.endsWith("LPT1") ||
                    filename.endsWith("LPT2") ||
                    filename.endsWith("COM2") ||
                    filename.endsWith("AUX") ||
                    filename.endsWith("CON") ||
                    filename.endsWith("COM1") ||
                    filename.endsWith("COM3"))
                {
                    throw new FileNotFoundException("File not found: " + mxmlFileName);
                }
            }
            
            try
            {
                String sourceFileName =
                    FileNameGenerator.sourceFileName( request.getServletPath() );
                checkServletPath(sourceFileName, mxmlFileName);
            }
            catch (WrongCaseException wrongCaseException)
            {
                throw new FileNotFoundException(wrongCaseException.getMessage());
            }
        }
    }

    // [preilly] Borrowed from JRun's jrun.jsp.Translator.
    private static void checkServletPath(String servletPath, String realPath)
        throws WrongCaseException
    {
        String newPath;
        try {
            // cmurphy - getRealPath on WebSphere/Windows uses case of request
            // use getCanonicalPath to get the case on the file system
            newPath = new File(realPath).getCanonicalPath();
        } catch (IOException ioe) {
            newPath = realPath;
       }

        if (File.separatorChar == '\\')
        {
            String fileRev = reversePathElements(newPath.replace('\\', '/'));
            String servletPathRev = reversePathElements(servletPath);

            StringTokenizer fileStringTokenizer = new StringTokenizer(fileRev, "/");
            StringTokenizer servletPathStringTokenizer = new StringTokenizer(servletPathRev, "/");

            while (fileStringTokenizer.hasMoreTokens() &&
                   servletPathStringTokenizer.hasMoreTokens())
            {
                String servletPathElement = servletPathStringTokenizer.nextToken();
                String fileElement = fileStringTokenizer.nextToken();
                if (!servletPathElement.equalsIgnoreCase(fileElement))
                {
                    break;
                }
                if (!servletPathElement.equals(fileElement))
                {
                    throw new WrongCaseException("The request path, " + servletPath +
                                                 ", had the wrong case.  Use: " +
                                                 fileElement);
                }
            }
        }
    }

    // [preilly] Borrowed from JRun's jrun.jsp.Translator.
    private static String reversePathElements(String path)
    {
        StringTokenizer stringTokenizer = new StringTokenizer(path, "/");
        StringBuffer results = new StringBuffer(path.length());

        while (stringTokenizer.hasMoreElements())
        {
            results.insert(0, stringTokenizer.nextToken());
            results.insert(0, '/');
        }

        return results.toString();
    }
}
