////////////////////////////////////////////////////////////////////////////////
//
//  ADOBE SYSTEMS INCORPORATED
//  Copyright 2008 Adobe Systems Incorporated
//  All Rights Reserved.
//
//  NOTICE: Adobe permits you to use, modify, and distribute this file
//  in accordance with the terms of the license agreement accompanying it.
//
////////////////////////////////////////////////////////////////////////////////

package flex2.compiler.as3;

import flex2.compiler.ILocalizableMessage;
import flex2.compiler.Logger;
import flex2.compiler.util.AbstractLogAdapter;

/**
 * This is a Logger implementation for use with AST generation.  It
 * handles offsetting the line number reported from ASC with the line
 * number of the code fragment in the Mxml document.
 *
 * @author Paul Reilly
 */
public final class CodeFragmentLogAdapter extends AbstractLogAdapter
{
    private int lineNumberOffset;

    public CodeFragmentLogAdapter(Logger original, int lineNumberOffset)
    {
        super(original);
        this.lineNumberOffset = lineNumberOffset  - 1;
    }

    public void logInfo(String path, int line, String info)
    {
        original.logInfo(path, line + lineNumberOffset, info);
    }

    public void logDebug(String path, int line, String debug)
    {
        original.logDebug(path, line + lineNumberOffset, debug);
    }

    public void logWarning(String path, int line, String warning)
    {
        original.logWarning(path, line + lineNumberOffset, warning);
    }

    public void logWarning(String path, int line, String warning, int errorCode)
    {
        original.logWarning(path, line + lineNumberOffset, warning, errorCode);
    }

    public void logError(String path, int line, String error)
    {
        original.logError(path, line + lineNumberOffset, error);
    }

    public void logError(String path, int line, String error, int errorCode)
    {
        original.logError(path, line + lineNumberOffset, error, errorCode);
    }

    public void logInfo(String path, int line, int col, String info)
    {
        original.logInfo(path, line + lineNumberOffset, info);
    }

    public void logDebug(String path, int line, int col, String debug)
    {
        original.logDebug(path, line + lineNumberOffset, debug);
    }

    public void logWarning(String path, int line, int col, String warning)
    {
        original.logWarning(path, line + lineNumberOffset, warning);
    }

    public void logError(String path, int line, int col, String error)
    {
        original.logError(path, line + lineNumberOffset, error);
    }

    public void logWarning(String path, int line, int col, String warning, String source)
    {
        original.logWarning(path, line + lineNumberOffset, warning);
    }

    public void logWarning(String path, int line, int col, String warning, String source, int errorCode)
    {
        original.logWarning(path, line + lineNumberOffset, warning, errorCode);
    }

    public void logError(String path, int line, int col, String error, String source)
    {
        original.logError(path, line + lineNumberOffset, error);
    }

    public void logError(String path, int line, int col, String error, String source, int errorCode)
    {
        original.logError(path, line + lineNumberOffset, error, errorCode);
    }

    public void log(ILocalizableMessage m)
    {
        m.setLine(m.getLine() + lineNumberOffset);
        original.log(m);
    }

    public void log(ILocalizableMessage m, String source)
    {
        m.setLine(m.getLine() + lineNumberOffset);
        original.log(m, source);
    }
}
