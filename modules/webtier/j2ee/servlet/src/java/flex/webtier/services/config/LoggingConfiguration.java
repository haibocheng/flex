////////////////////////////////////////////////////////////////////////////////
//
//  ADOBE SYSTEMS INCORPORATED
//  Copyright 2003-2007 Adobe Systems Incorporated
//  All Rights Reserved.
//
//  NOTICE: Adobe permits you to use, modify, and distribute this file
//  in accordance with the terms of the license agreement accompanying it.
//
////////////////////////////////////////////////////////////////////////////////
package flex.webtier.services.config;

import flex.webtier.util.PathResolver;
import flex2.compiler.config.ConfigurationException;
import flex2.compiler.config.ConfigurationValue;

import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;

public class LoggingConfiguration {

    private static boolean isCLR = System.getProperty("flex.platform.CLR") != null;
    private String logLevel = "info";
    private ConsoleConfiguration consoleConfiguration;
    private FileConfiguration fileConfiguration;
    private boolean eventEnable; // .NET specific
    private String eventLogName; // .NET specific

    public LoggingConfiguration() {
        fileConfiguration = new FileConfiguration();
        consoleConfiguration = new ConsoleConfiguration();
        if (isCLR){
            setDefaultsCLR();
        } else {
            setDefaultsJava();
        }
    }

    private void setDefaultsCLR() {
        consoleConfiguration.setEnable(false);
        // cmurphy - removing event logging until .NET implementation
        eventEnable = true;
        fileConfiguration.setEnable(false);
        // We shouldn't set a default for filename here.
    }

    private void setDefaultsJava() { 
        fileConfiguration.setFileName(PathResolver.getThreadLocalPathResolver().getFlexWritePath() + File.separator + "logs" + File.separator + "flex.log");
        consoleConfiguration.setEnable(true);
        fileConfiguration.setEnable(true);
        eventEnable = false;
    }

	public ConsoleConfiguration getConsoleConfiguration() {
		return consoleConfiguration;
	}

    public FileConfiguration getFileConfiguration() {
        return fileConfiguration;
    }

    public boolean isConsoleEnable() {
        return consoleConfiguration.isEnable();
    }

    public boolean isFileEnable() {
        return fileConfiguration.isEnable();
    }

    public String getFileName() {
        return fileConfiguration.getFileName();
    }

    public String getFileMaximumSize() {
        return fileConfiguration.getFileMaximumSize();
    }

    public int getFileMaximumBackups() {
        return fileConfiguration.getFileMaximumBackups();
    }

    public void cfgLevel(ConfigurationValue cv, String logLevel) throws ConfigurationException {
        logLevel = logLevel.trim().toLowerCase();
        if (logLevel.equals("error") || logLevel.equals("warn") || logLevel.equals("info") || logLevel.equals("debug")) {
            this.logLevel = logLevel;
        } else {
            throw new ConfigurationException("level must be one of error, warn, info, debug", cv.getSource(), cv.getLine());
        }
    }

    public String getLogLevel() {
        return logLevel;
    }

    public boolean isEventEnable() {
        return eventEnable;
    }

    public String getEventLogName() {
        return eventLogName;
    }

    public String toString() {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);

        pw.println("LoggingConfiguration:");
        pw.println("logLevel = " + logLevel);
        pw.println("consoleEnable = " + consoleConfiguration.isEnable());
        pw.println("fileEnable = " + fileConfiguration.isEnable());
        pw.println("fileName = " + fileConfiguration.getFileName());
        pw.println("fileMaximumSize = " + fileConfiguration.getFileMaximumSize());
        pw.println("fileMaximumBackups = " + fileConfiguration.getFileMaximumBackups());

        return sw.toString();
    }
}
