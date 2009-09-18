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
package flex.webtier.services.config;

import flash.localization.LocalizationManager;
import flex2.compiler.Logger;
import flex2.compiler.ILocalizableMessage;
import flex2.compiler.util.AbstractLogger;
import flex2.compiler.util.ThreadLocalToolkit;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Locale;

public class ExceptionLogger extends AbstractLogger implements Logger
{
    public ExceptionLogger()
    {
        setLocalizationManager(ThreadLocalToolkit.getLocalizationManager());
    }
    public String getMessage(ILocalizableMessage m)
    {
        String prefix = formatPrefix(getLocalizationManager(), m);
        boolean found = true;
        LocalizationManager loc = getLocalizationManager();
        String text = loc.getLocalizedTextString(Locale.getDefault(), m);
        if (text == null)
        {
            text = m.getClass().getName();
            found = false;
        }
        String exText = formatExceptionDetail(m, loc);
        System.err.println( prefix + text + exText );
        assert found : "Localized text missing for " + m.getClass().getName();

        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        pw.println(prefix + text + exText);
        return sw.toString();
    }

    public void includedFileAffected(String path) {} 

    // don't really need these but wanted the utility methods in AbstractLogger
    public void needsCompilation(String path, String reason) {}        
    public void includedFileUpdated(String path) {}
    public int errorCount() { return -1; }
    public int warningCount() { return -1; }
    public void log(ILocalizableMessage m) {}
    //temporary solution for sdk build 134219's API Change, bug entered 165911 to implement this 
    public void log(ILocalizableMessage m,String str) {}
    public void logInfo(String info) {}
    public void logDebug(String debug) {}
    public void logWarning(String warning) {}
    public void logError(String error) {}
    public void logInfo(String path, String info) {}
    public void logDebug(String path, String debug) {}
    public void logWarning(String path, String warning) {}
    public void logWarning(String path, String warning, int errorCode) {}
    public void logError(String path, String error) {}
    public void logError(String path, String error, int errorCode) {}
    public void logInfo(String path, int line, String info) {}
    public void logDebug(String path, int line, String debug) {}
    public void logWarning(String path, int line, String warning) {}
    public void logWarning(String path, int line, String warning, int errorCode) {}
    public void logError(String path, int line, String error) {}
    public void logError(String path, int line, String error, int errorCode) {}
    public void logInfo(String path, int line, int col, String info) {}
    public void logDebug(String path, int line, int col, String debug) {}
    public void logWarning(String path, int line, int col, String warning) {}
    public void logError(String path, int line, int col, String error) {}
    public void logWarning(String path, int line, int col, String warning, String source) {}
    public void logWarning(String path, int line, int col, String warning, String source, int errorCode) {}
    public void logError(String path, int line, int col, String error, String source) {}
    public void logError(String path, int line, int col, String error, String source, int errorCode) {}
}
