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

import java.io.IOException;
import java.util.Map;
import java.util.SortedSet;

public interface SourceCodeLoader
{
    /**
     * Retrieve the requested line numbers for the file specified.
     *
     * @param path The path of the source file.
     * @param srcLineNumbers All the line numbers wanted in the current file.
     * @param encoding actionscript-file-encoding
     * @return srcLinesText Index of line numbers to source text.
     */
    Map getSourceForLineNumbers(String path, SortedSet srcLineNumbers, String encoding) throws IOException;
}