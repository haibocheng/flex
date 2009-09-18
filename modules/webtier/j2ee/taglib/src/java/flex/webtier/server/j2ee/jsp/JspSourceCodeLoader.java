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
package flex.webtier.server.j2ee.jsp;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedSet;
import java.util.StringTokenizer;
import flex.webtier.server.j2ee.events.SourceCodeLoader;

public class JspSourceCodeLoader implements SourceCodeLoader
{
    protected String sourceCode;

    public JspSourceCodeLoader(String sourceCode)
    {
        this.sourceCode = sourceCode;
    }

    /**
     * Retrieve the requested line numbers for the file specified.
     *
     * @param key The key to the jsp source code in the source cache.
     * @param srcLineNumbers All the line numbers wanted in the current file.
     * @return srcLinesText Index of line numbers to source text.
     */
    public Map getSourceForLineNumbers(String key, SortedSet srcLineNumbers, String encoding) throws IOException
    {
        HashMap srcLinesText = new HashMap();
        String s = null;

        if (sourceCode == null)
        {
            throw new FileNotFoundException();
        }

        StringTokenizer t = new StringTokenizer(sourceCode, "\n");

        int line = 0;

        // Fetch each line of interest from the file
        while (!srcLineNumbers.isEmpty() && (t.hasMoreTokens()))
        {
            while ((line < ((Integer) srcLineNumbers.first()).intValue()) && t.hasMoreTokens())
            {
                s = (String)t.nextToken();
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