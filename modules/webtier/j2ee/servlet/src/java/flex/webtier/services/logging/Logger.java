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

public interface Logger {

    void initializeLogger(LoggingConfiguration loggingConfiguration);
    void setLogLevel(String logLevel);
    void addLogEventHandler(LogEventHandler handler);
    void start();
    void stop();

    boolean isDebugEnabled();
    boolean isInfoEnabled();
    boolean isWarningEnabled();
    void logDebug(String message);
    void logDebug(String message, Throwable t);
    void logWarning(String message);
    void logWarning(String message, Throwable t);
    void logError(String message);
    void logError(String message, Throwable t);
    void logInfo(String message);
}
