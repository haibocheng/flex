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
package flex.webtier.services.logging;

import flash.localization.LocalizationManager;
import flex.webtier.services.ServiceFactory;
import flex2.compiler.ILocalizableMessage;
import flex2.compiler.io.FileUtil;
import flex2.compiler.util.AbstractLogger;
import flex2.compiler.util.ThreadLocalToolkit;

public class WebtierIntegratedLogger extends AbstractLogger implements flex2.compiler.Logger
{    
    private Logger webtierLogger;
    private static final String lineSeparator = System.getProperty("line.separator");

    public WebtierIntegratedLogger()
    {
        setLocalizationManager(ThreadLocalToolkit.getLocalizationManager());
        webtierLogger = ServiceFactory.getLogger();
    }

	public void log( ILocalizableMessage m)
	{
		log(m, null);
	}

    public void log( ILocalizableMessage m, String source)
    {
        if (m.getLevel() == ILocalizableMessage.WARNING)
        {
            if (!webtierLogger.isWarningEnabled())
            {
                return;
            }
        }
        else
        {
            if (!webtierLogger.isInfoEnabled())
            {
                return;
            }
        }

        String prefix = formatPrefix( getLocalizationManager(), m );
        boolean found = true;
	    LocalizationManager loc = getLocalizationManager();
        String text = loc.getLocalizedTextString( m );
        if (text == null)
        {
            text = m.getClass().getName();
            found = false;
        }
	    String exText = formatExceptionDetail(m, loc);
	    if (m.getPath() != null && m.getLine() != -1)
	    {
		    exText += lineSeparator + lineSeparator + (source == null ? getLineText(m.getPath(), m.getLine()) : source);
	    }
	    if (m.getColumn() != -1)
	    {
		    exText += lineSeparator + getLinePointer(m.getColumn(), source);
	    }
	    if (m.getLevel() == ILocalizableMessage.INFO)
	    {
		    logInfo(prefix + text + exText);
	    }
	    else if (m.getLevel() == ILocalizableMessage.WARNING)
	    {
		    logWarning(prefix + text + exText);
	    }
	    else
	    {
		    logError(prefix + text + exText);
	    }
	    assert found : "Localized text missing for " + m.getClass().getName();
    }

    public void logDebug(String debug)
    {
        if (webtierLogger.isDebugEnabled())
        {
            webtierLogger.logDebug(debug);
        }
    }

    public void logDebug(String path, String debug)
    {
        logDebug(path + lineSeparator + lineSeparator + debug);
    }

    public void logDebug(String path, int line, String debug)
    {
        logDebug(path + "(" + line + "):" + " " + debug);
    }

    public void logDebug(String path, int line, int col, String debug)
    {
		logDebug(path + "(" + line + "): " + COL + ": " + col + " " + debug);
    }

    public void logInfo(String info)
    {
        if (webtierLogger.isInfoEnabled())
        {
            webtierLogger.logInfo(info);
        }
    }

    public void logInfo(String path, int line, String info)
    {
        logInfo(path, line, info);
    }

    public void logInfo(String path, String info)
    {
        logInfo(path + lineSeparator + lineSeparator + info);
    }

    public void logInfo(String path, int line, int col, String info)
    {
        logInfo(path + "(" + line + "): " + COL + ": " + col + " " + info);
    }

    public void logWarning(String warning)
    {
        if (webtierLogger.isWarningEnabled())
        {
            webtierLogger.logWarning(warning);
        }
    }

    public void logWarning(String path, String warning)
    {
        logWarning(path + lineSeparator + lineSeparator + warning);
    }

    public void logWarning(String path, String warning, int errorCode)
    {
        logWarning(path, warning);
    }

    public void logWarning(String path, int line, String warning)
    {
        if (webtierLogger.isWarningEnabled())
        {
            logWarning(path + "(" + line + "):" + " " + WARNING + ": " + warning + lineSeparator + lineSeparator +
                 getLineText(path, line));
        }
    }

	public void logWarning(String path, int line, String warning, int errorCode)
	{
		logWarning(path, line, warning);
	}

    public void logWarning(String path, int line, int col, String warning)
	{
		if (webtierLogger.isWarningEnabled())
		{
		    String lineText = getLineText(path, line);
		    logWarning(path + "(" + line + "): " + COL + ": " + col + " " + WARNING + ": " + warning + lineSeparator + lineSeparator +
		         lineText + lineSeparator + getLinePointer(col, lineText));
        }
	}

    public void logWarning(String path, int line, int col, String warning, String source)
	{
		if (webtierLogger.isWarningEnabled())
        {
		    logWarning(path + "(" + line + "): " + COL + ": " + col + " " + WARNING + ": " + warning + lineSeparator + lineSeparator +
		         source + lineSeparator + getLinePointer(col, source));
        }
    }

	public void logWarning(String path, int line, int col, String warning, String source, int errorCode)
	{
		logWarning(path, line, col, warning, source);
	}

    public void logError(String error)
    {
        webtierLogger.logError(error);
    }

    public void logError(String path, String error)
    {
        logError(path + lineSeparator + lineSeparator + ERROR + ": " + error);
    }

    public void logError(String path, String error, int errorCode)
    {
        logError(path, error);
    }

    public void logError(String path, int line, String error)
    {
        logError(path + "(" + line + "): " + " " + ERROR + ": " + error + lineSeparator + lineSeparator +
                 getLineText(path, line));
    }

    public void logError(String path, int line, String error, int errorCode)
    {
        logError(path, line, error);
    }

    public void logError(String path, int line, int col, String error)
    {
        String lineText = getLineText(path, line);
        logError(path + "(" + line + "): " + COL + ": " + col + " " + ERROR + ": " + error + lineSeparator + lineSeparator +
                   lineText + lineSeparator + getLinePointer(col, lineText));
    }

	public void logError(String path, int line, int col, String error, String source)
	{
		logError(path + "(" + line + "): " + COL + ": " + col + " " + ERROR + ": " + error + lineSeparator + lineSeparator +
		         source + lineSeparator + getLinePointer(col, source));
	}

	public void logError(String path, int line, int col, String error, String source, int errorCode)
	{
		logError(path, line, col, error, source);
	}

    private String getLineText(String path, int line)
    {
        String text = null;
        try
        {
            text = FileUtil.readLine(path, line);
        }
        catch (Throwable t)
        {
            // ignore
        }
        return text;
    }

    private String getLinePointer(int col, String source)
    {
        if (col <= 0) // col == 0 is likely an error...
        {
            return "^";
        }

        StringBuffer b = new StringBuffer(col);
        for (int i = 0; i < col - 1; i++)
        {
	        if (source != null && i < source.length() && source.charAt(i) == '\t')
	        {
                b.append('\t');
	        }
	        else
	        {
		        b.append(' ');
	        }
        }

        b.append('^');

        return b.toString();
    }

    
    // ignore
    public void includedFileAffected(String path) {} 
    public void needsCompilation(String path, String reason) {}        
    public void includedFileUpdated(String path) {}
    public int errorCount() { return -1; }
    public int warningCount() { return -1; }
}
