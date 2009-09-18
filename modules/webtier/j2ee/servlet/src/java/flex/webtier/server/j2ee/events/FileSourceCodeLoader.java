////////////////////////////////////////////////////////////////////////////////
//
//  ADOBE SYSTEMS INCORPORATED
//  Copyright 2006-2007 Adobe Systems Incorporated
//  All Rights Reserved.
//
//  NOTICE: Adobe permits you to use, modify, and distribute this file
//  in accordance with the terms of the license agreement accompanying it.
//
////////////////////////////////////////////////////////////////////////////////
package flex.webtier.server.j2ee.events;

import flash.util.FileUtils;
import flex.webtier.util.PathResolver;

import java.io.BufferedReader;
import java.io.StringReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedSet;

public class FileSourceCodeLoader implements SourceCodeLoader
{
    protected PathResolver pathResolver;
    
    public FileSourceCodeLoader(PathResolver pathResolver)
    {
        this.pathResolver = pathResolver;
    }

    /**
     * Retrieve the requested line numbers for the file specified.
     *
     * @param path The name of the source file.
     * @param srcLineNumbers All the line numbers wanted in the current file.
     * @return srcLinesText Index of line numbers to source text.
     */
    public Map getSourceForLineNumbers(String path, SortedSet srcLineNumbers, String encoding) throws IOException
    {
        HashMap srcLinesText = new HashMap();
        String s = null;
        
        File f = pathResolver.resolveFile(path, false);

        if (f == null)
        {
            throw new FileNotFoundException(path);
        }

        BufferedReader in = new BufferedReader(new StringReader(FileUtils.readFile(f, encoding)));
        int line = 0;

        // Fetch each line of interest from the file
        while (!srcLineNumbers.isEmpty())
        {
            while (line < ((Integer) srcLineNumbers.first()).intValue())
            {
                s = in.readLine();
                line++;
            }
            if (s != null)
            {
                srcLinesText.put(srcLineNumbers.first(), s);
            }
            srcLineNumbers.remove(srcLineNumbers.first());
        }
        
        return srcLinesText;
    }
}