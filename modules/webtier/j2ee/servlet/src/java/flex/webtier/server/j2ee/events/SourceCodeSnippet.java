/////////////////////////////////////////////////////////////////////////////////
//
//  ADOBE SYSTEMS INCORPORATED
//  Copyright 2005-2007 Adobe Systems Incorporated
//  All Rights Reserved.
//
//  NOTICE: Adobe permits you to use, modify, and distribute this file
//  in accordance with the terms of the license agreement accompanying it.
//
////////////////////////////////////////////////////////////////////////////////
package flex.webtier.server.j2ee.events;

/**
 * SourceCodeSnippet -- continous lines from a file
 */
public class SourceCodeSnippet 
{
    int startingLine;
    String[] lines;

    public SourceCodeSnippet(int startingLine, String[] lines) 
    {
        this.startingLine = startingLine;
        this.lines = lines;
    }

    public String toString() 
    {
        StringBuffer result = new StringBuffer();
        for (int i = 0; i < lines.length; i++) 
        {
            result.append((startingLine + i) + ":" + lines[i] + "\n");
        }
        return result.toString();
    }
}