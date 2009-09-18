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

import flex2.compiler.ILocalizableMessage;
import flex2.compiler.Logger;
import flex2.compiler.util.AbstractLogger;
import flex.webtier.server.j2ee.MxmlContext;
import flex.webtier.services.ServiceFactory;
import flex.webtier.util.Trace;

import java.util.Locale;

public class CompileEventLogger extends AbstractLogger implements Logger
{
    private MxmlContext context;

    // MXML compiler always tracks warnings & errors
    private int warningCount;
    private int errorCount;

    private flex.webtier.services.logging.Logger logger;

    public CompileEventLogger(MxmlContext context)
    {
        this.context = context;
        this.logger = ServiceFactory.getLogger();
        init(getLocalizationManager());
    }

    public void needsCompilation(String path, String reason)
    {
        logger.logDebug("Needs compilation path: " + path);
        logger.logDebug("Needs compilation reason: " + reason);
    }

    public void includedFileAffected(String path) {}

    public void includedFileUpdated(String path) {}

    /** part of the interface with the MXML Compiler Logger
     * this is not used by the web tier
     */
    public int errorCount()
    {
        return errorCount;
    }

    /** part of the interface with the MXML Compiler Logger
     * this is not used by the web tier
     */
    public int warningCount()
    {
        return warningCount;
    }

    public void logInfo(String info)
    {
        // log info events from compiler as debug, most aren't relevant to the developer using a web browser
        logger.logDebug(info);
    }

    public void logDebug(String debug)
    {
        logger.logDebug(debug);
    }

    public void logWarning(String warning)    
    {
        logWarning(warning, false);    
    }
    
    /**
     * log warning 
     * If cached content is displayed and oneTime is true, then don't show the warning again.
     */
    public void logWarning(String warning, boolean oneTime)
    {
        warningCount++;
        context.addEvent(new CompileEvent(CompileEvent.EVT_WARNING, WARNING, 0, 0, null, 0, null, warning, oneTime));

        if (Trace.error)
        {
            Thread.dumpStack();
        }
    }

    public void logError(String error)
    {
        errorCount++;
        context.addEvent(new CompileEvent(CompileEvent.EVT_ERROR, ERROR, 0, 0, null, 0, null, error, false));

        if (Trace.error)
        {
            Thread.dumpStack();
        }
    }

    public void logInfo(String path, String info)
    {
        // log info events from compiler as debug, most aren't relevant to the developer using a web browser
        logger.logDebug(path);
        logger.logDebug(info);
    }

    public void logDebug(String path, String debug)
    {
        logger.logDebug(path);
        logger.logDebug(debug);
    }

    public void logWarning(String path, String warning)
    {
        warningCount++;
        context.addEvent(new CompileEvent(CompileEvent.EVT_WARNING, WARNING, 0, 0, null, 0, null, warning, false));

        if (Trace.error)
        {
            Thread.dumpStack();
        }
    }

    public void logWarning(String path, String warning, int errorCode)
    {
        logWarning(path, warning);
    }

    public void logError(String path, String error)
    {
        errorCount++;
        context.addEvent(new CompileEvent(CompileEvent.EVT_ERROR, ERROR, 0, 0, null, 0, null, error, false));

        if (Trace.error)
        {
            Thread.dumpStack();
        }
    }

    public void logError(String path, String error, int errorCode)
    {
        logError(path, error);
    }

    public void logInfo(String path, int line, String info)
    {
        // log info events from compiler as debug, most aren't relevant to the developer using a web browser
        logger.logDebug(path + ":" + line);
        logger.logDebug(info);
    }

    public void logDebug(String path, int line, String debug)
    {
        logger.logDebug(path + ":" + line);
        logger.logDebug(debug);
    }

    public void logWarning(String path, int line, String warning)
    {
        warningCount++;
        context.addEvent(new CompileEvent(CompileEvent.EVT_WARNING, WARNING, 0, line, path, 0, null, warning, false));

        if (Trace.error)
        {
            Thread.dumpStack();
        }
    }

    public void logWarning(String path, int line, String warning, int errorCode)
    {
        logWarning(path, line, warning);
    }

    public void logError(String path, int line, String error)
    {
        errorCount++;
        context.addEvent(new CompileEvent(CompileEvent.EVT_ERROR, ERROR, 0, line, path, 0, null, error, false));

        if (Trace.error)
        {
            Thread.dumpStack();
        }
    }

    public void logError(String path, int line, String error, int errorCode)
    {
        logError(path, line, error);
    }

    public void logInfo(String path, int line, int col, String info)
    {
        // log info events from compiler as debug, most aren't relevant to the developer using a web browser
        logger.logDebug(path + ":" + line + ":" + col);
        logger.logDebug(info);
    }

    public void logDebug(String path, int line, int col, String debug)
    {
        logger.logDebug(path + ":" + line + ":" + col);
        logger.logDebug(debug);
    }

    public void logWarning(String path, int line, int col, String warning)
    {
        logWarning(path, line, warning);
    }

    public void logError(String path, int line, int col, String error)
    {
        logError(path, line, error);
    }

    public void logWarning(String path, int line, int col, String warning, String source)
    {
        // C: If source is available, this logger doesn't have to read the source file...
        logWarning(path, line, warning);
    }

    public void logWarning(String path, int line, int col, String warning, String source, int errorCode)
    {
        logWarning(path, line, warning);
    }

    public void logError(String path, int line, int col, String error, String source)
    {
        // C: If source is available, this logger doesn't have to read the source file...
        logError(path, line, error);
    }

    public void logError(String path, int line, int col, String error, String source, int errorCode)
    {
        logError(path, line, error);
    }

    public void log(ILocalizableMessage m, String source) {
        // ignore source, the event logger looks this up separately
        log(m);
    }

    public void log(ILocalizableMessage m)
    {
        boolean found = true;
        String text = getLocalizationManager().getLocalizedTextString(Locale.getDefault(), m);
        if (text == null)
        {
            text = m.getClass().getName();
            found = false;
        }
        assert found : "Localized text missing for " + m.getClass().getName();

        if (m.getLevel() == ILocalizableMessage.ERROR)
        {
            if ((m.getPath() != null) && (m.getLine() != -1))
            {
                logError(m.getPath(), m.getLine(), text);
            }
            else if (m.getPath() != null)
            {
                logError(m.getPath(), text);
            }
            else
            {
                logError(text);
            }
        }
        else if (m.getLevel() == ILocalizableMessage.WARNING)
        {
            if ((m.getPath() != null) && (m.getLine() != -1))
            {
                logWarning(m.getPath(), m.getLine(), text);
            }
            else if (m.getPath() != null)
            {
                logWarning(m.getPath(), text);
            }
            else
            {
                logWarning(text);
            }
        }
        else
        {
            if ((m.getPath() != null) && (m.getLine() != -1))
            {
                logInfo(m.getPath(), m.getLine(), text);
            }
            else if (m.getPath() != null)
            {
                logInfo(m.getPath(), text);
            }
            else
            {
                logInfo(text);
            }
        }
    }
}
