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
package flex.webtier.server.j2ee.events;

import java.util.Vector;

/**
 * This logger simply outputs if log-compiler-errors is in the configuration file is true.
 * Since the application logger is used, application logging must be turned on and set to
 * to at least the error level.
 */
public class LogOutputHandler extends BaseOutputHandler
{
    public LogOutputHandler(String mxmlFileName, String rootPath, Vector events, SourceCodeLoader sourceCodeLoader)
    {
        super(mxmlFileName, rootPath, events, sourceCodeLoader);
    }

    // no implementation for the log output handler handled by logCompilerResults() instead
    protected void outputEvents() {}
}