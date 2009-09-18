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
package flex.webtier.services.config;

import flex2.compiler.config.ConfigurationValue;
import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * @author cmurphy
 */
public class DebuggingConfiguration
{
    private ServerConfiguration parent;
    private boolean processDebugQueryParams = false;
    private boolean generateProfileSwfs = false;
    private boolean keepGeneratedSwfs = false;
    private boolean showAllWarnings = false;
    private boolean showBindingWarnings = false;
    private boolean showOverrideWarnings = false;
    private boolean showStacktrace = false;
    private boolean createCompileReport = false;
    private boolean showSourceInCompilerErrors = false;
    private boolean logCompilerErrors = false;

    public DebuggingConfiguration(ServerConfiguration parent) {
        this.parent = parent;
    }

    public boolean processDebugQueryParams() {
        return !parent.isProductionMode() && processDebugQueryParams;
    }

    public void cfgProcessDebugQueryParams(ConfigurationValue cv, boolean processDebugQueryParams) {
        this.processDebugQueryParams = processDebugQueryParams;
    }

    public boolean generateProfileSwfs() {
        return !parent.isProductionMode() && generateProfileSwfs;
    }

    public void cfgGenerateProfileSwfs(ConfigurationValue cv, boolean generateProfileSwfs) {
        this.generateProfileSwfs = generateProfileSwfs;
    }

    public boolean keepGeneratedSwfs() {
        return !parent.isProductionMode() && keepGeneratedSwfs;
    }

    public void cfgKeepGeneratedSwfs(ConfigurationValue cv, boolean keepGeneratedSwfs) {
        this.keepGeneratedSwfs = keepGeneratedSwfs;
    }

    public boolean showAllWarnings() {
        return !parent.isProductionMode() && showAllWarnings;
    }

    public void cfgShowAllWarnings(ConfigurationValue cv, boolean showAllWarnings) {
            this.showAllWarnings = showAllWarnings;
    }

    public boolean showOverrideWarnings() {
        return !parent.isProductionMode() && showOverrideWarnings;
    }

    public void cfgShowOverrideWarnings(ConfigurationValue cv, boolean showOverrideWarnings) {
            this.showOverrideWarnings = showOverrideWarnings;
    }

    public boolean showStacktrace() {
        return !parent.isProductionMode() && showStacktrace;
    }

    public void cfgShowStacktracesInBrowser(ConfigurationValue cv, boolean stacktrace) {
            showStacktrace = stacktrace;
    }

    public boolean showSourceInCompilerErrors() {
        return !parent.isProductionMode() && showSourceInCompilerErrors;
    }

    public void cfgShowSourceInCompilerErrors(ConfigurationValue cv, boolean showSourceInCompilerErrors) {
            this.showSourceInCompilerErrors = showSourceInCompilerErrors;
    }

    public boolean logCompilerErrors() {
        return !parent.isProductionMode() && logCompilerErrors;
    }

    public void cfgLogCompilerErrors(ConfigurationValue cv, boolean logCompilerErrors) {
            this.logCompilerErrors = logCompilerErrors;
    }

    public boolean createCompileReport() {
        return !parent.isProductionMode() && createCompileReport;
    }

    public void cfgCreateCompileReport(ConfigurationValue cv, boolean createCompileReport) {
            this.createCompileReport = createCompileReport;
    }

    public String toString() {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);

        pw.println("DebuggingConfiguration:");
        if (parent.isProductionMode())
        {
            pw.println("production mode - debugging flags are disabled" );
        }
        else
        {
            pw.println("processDebugQueryParams = " + processDebugQueryParams);
            pw.println("generateProfileSwfs = " + generateProfileSwfs);
            pw.println("keepGeneratedSwfs = " + keepGeneratedSwfs);
            pw.println("showAllWarnings = " + showAllWarnings);
            pw.println("showBindingWarnings = " + showBindingWarnings);
            pw.println("showOverrideWarnings = " + showOverrideWarnings);
            pw.println("showStacktrace = " + showStacktrace);
            pw.println("showSourceInCompilerErrors = " + showSourceInCompilerErrors);
            pw.println("logCompilerErrors = " + logCompilerErrors);
            pw.println("createCompileReport = " + createCompileReport);
        }

        return sw.toString();
    }
}

