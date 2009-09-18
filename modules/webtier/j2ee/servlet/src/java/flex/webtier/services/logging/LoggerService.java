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
package flex.webtier.services.logging;

import flex.webtier.services.config.LoggingConfiguration;

import java.util.ArrayList;
import java.util.Iterator;


/**
 * The main entry point into the logging subsystem. The LoggerService
 * is responsible for creating a LogEvent and routing it to the appropriate
 * LogEventHandler instances
 *
 * @author Karl Moss
 * @since 09Apr2001
 */
public class LoggerService
    implements Logger
{
    private static boolean errorEnabled = true;
    private static boolean warningEnabled = true;
    private static boolean infoEnabled = true;
    private static boolean debugEnabled = false;

    private String format;

    // Make the default logger a singleton
    private static Logger defaultLogger;
    private static String serverLogFormat;

    private ArrayList handlers = new ArrayList();

    public static synchronized Logger createWindowsDefaultLogger(String logName)
    {
        if (defaultLogger == null)
        {
            defaultLogger = new LoggerService();
            ((LoggerService)defaultLogger).setFormat(serverLogFormat);
            if (System.getProperty("flex.platform.CLR") != null)
            {
                defaultLogger.addLogEventHandler(new WindowsLogEventHandler(logName));
            }
            else
            {
                defaultLogger.addLogEventHandler(new ConsoleLogEventHandler());
            }
        } 
        return defaultLogger;       
    }
    
    public static synchronized Logger createDefaultLogger()
    {
        if (defaultLogger == null)
        {
            defaultLogger = new LoggerService();
            ((LoggerService)defaultLogger).setFormat(serverLogFormat);
            defaultLogger.addLogEventHandler(new ConsoleLogEventHandler());
        }

        return defaultLogger;
    }

    public void addLogEventHandler(LogEventHandler handler)
    {
        handlers.add(handler);
    }

    public boolean isErrorEnabled()
    {
        return errorEnabled;
    }

    public boolean isWarningEnabled()
    {
        return warningEnabled;
    }

    public boolean isInfoEnabled()
    {
        return infoEnabled;
    }

    public boolean isDebugEnabled()
    {
        return debugEnabled;
    }

    public void logError(String msg)
    {
        logError(msg, null);
    }

    public void logError(Throwable throwable)
    {
        if (throwable != null)
        {
            logError(throwable.getMessage(), throwable);
        }
        else
        {
            logError("");
        }
    }

    public void logError(String msg, Throwable throwable)
    {
        if (isErrorEnabled())
        {
            log(new LogEvent(LogEvent.LOG_ERROR, msg, throwable));
        }
    }

    public void logWarning(String msg)
    {
        logWarning(msg, null);
    }

    public void logWarning(Throwable throwable)
    {
        if (throwable != null)
        {
            logWarning(throwable.getMessage(), throwable);
        }
        else
        {
            logWarning("");
        }
    }

    public void logWarning(String msg, Throwable throwable)
    {
        if (isWarningEnabled())
        {
            log(new LogEvent(LogEvent.LOG_WARNING, msg, throwable));
        }
    }

    public void logInfo(String msg)
    {
        logInfo(msg, null);
    }

    public void logInfo(String msg, Throwable throwable)
    {
        if (isInfoEnabled())
        {
            log(new LogEvent(LogEvent.LOG_INFO, msg, throwable));
        }
    }

    public void logDebug(String msg)
    {
        logDebug(msg, null);
    }

    public void logDebug(String msg, Throwable throwable)
    {
        if (isDebugEnabled())
        {
            log(new LogEvent(LogEvent.LOG_DEBUG, msg, throwable));
        }
    }

    public Logger getLoggerService()
    {
        return this;
    }

    public void setFormat(String format)
    {
        this.format = format;

        //Use the same format string for default loggers created subsequently for this server.
        if (format != null) 
        {
            serverLogFormat = format;
        }
    }

    public String getFormat()
    {
        return (format != null) ? format : LogEvent.DEFAULT_FORMAT;
    }

    public void flush()
    {
        Iterator i = handlers.iterator();
        while (i.hasNext())
        {
            LogEventHandler eh = (LogEventHandler)i.next();
            eh.flush();
        }
    }

    public void start()
    {
        Iterator i = handlers.iterator();
        while (i.hasNext())
        {
            LogEventHandler handler = (LogEventHandler)i.next();
            handler.init();
            handler.start();
        }
    }

    public void stop()
    {
        Iterator i = handlers.iterator();
        while (i.hasNext())
        {
            LogEventHandler handler = (LogEventHandler)i.next();
            handler.stop();
        }
    }

    public static String getServerLogFormat() 
    {
        return serverLogFormat;
    }

    protected void log(LogEvent event)
    {
        // Set the default format
        // event.setFormat(FileLogEventHandler.expandServerDate(getFormat()));

        Iterator i = handlers.iterator();
        while (i.hasNext())
        {
            LogEventHandler eh = (LogEventHandler)i.next();
            boolean b = eh.log(event);

            // The logger requested that it be the last in the chain
            if (!b)
            {
                break;
            }
        }
    }

    public void initializeLogger(LoggingConfiguration loggingConfiguration) 
    {
        if (loggingConfiguration.isConsoleEnable() || loggingConfiguration.isFileEnable() || loggingConfiguration.isEventEnable())
        {
            ThreadedLogEventHandler threaded = new ThreadedLogEventHandler();
            handlers.add(threaded);
            if (loggingConfiguration.isEventEnable())
            {
                WindowsLogEventHandler eventLogger = new WindowsLogEventHandler(loggingConfiguration.getEventLogName());
                threaded.addLogEventHandler(eventLogger);
            }
            if (loggingConfiguration.isConsoleEnable())
            {
                ConsoleLogEventHandler consoleLogger = new ConsoleLogEventHandler();
                threaded.addLogEventHandler(consoleLogger);
            }
            if (loggingConfiguration.isFileEnable())
            {
                if (loggingConfiguration.getFileName() != null)
                {
                    FileLogEventHandler fileLogger = new FileLogEventHandler();
                    fileLogger.setFilename(loggingConfiguration.getFileName());
                    fileLogger.setRotationFiles(loggingConfiguration.getFileMaximumBackups());

                    String size = loggingConfiguration.getFileMaximumSize();
                    if (size.endsWith("B"))
                    {
                        size = size.substring(0, size.length()-1);
                    }
                    fileLogger.setRotationSize(size);
                    threaded.addLogEventHandler(fileLogger);
                }
            }

            String logLevel = loggingConfiguration.getLogLevel();
            setLogLevel(logLevel);
        }
    }

    public void setLogLevel(String logLevel)
    {
        if (logLevel != null)
        {
            if (logLevel.equalsIgnoreCase("error"))
            {
                errorEnabled = true;
                warningEnabled = false;
                infoEnabled = false;
                debugEnabled = false;
            }
            else if (logLevel.equalsIgnoreCase("warn"))
            {
                errorEnabled = true;
                warningEnabled = true;
                infoEnabled = false;
                debugEnabled = false;
            }
            else if (logLevel.equalsIgnoreCase("info"))
            {
                errorEnabled = true;
                warningEnabled = true;
                infoEnabled = true;
                debugEnabled = false;
            }
            else if (logLevel.equalsIgnoreCase("debug"))
            {
                errorEnabled = true;
                warningEnabled = true;
                infoEnabled = true;
                debugEnabled = true;
            }
        }
    }
}
