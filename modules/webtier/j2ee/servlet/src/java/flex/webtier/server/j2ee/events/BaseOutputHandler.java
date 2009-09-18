////////////////////////////////////////////////////////////////////////////////
//
//  ADOBE SYSTEMS INCORPORATED
//  Copyright 2005-2008 Adobe Systems Incorporated
//  All Rights Reserved.
//
//  NOTICE: Adobe permits you to use, modify, and distribute this file
//  in accordance with the terms of the license agreement accompanying it.
//
////////////////////////////////////////////////////////////////////////////////
package flex.webtier.server.j2ee.events;

import flex.webtier.services.ServiceFactory;
import flex.webtier.services.config.ServerConfiguration;
import flex.webtier.services.logging.Logger;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Enumeration;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.Vector;

/**
 * @author Cathy Reilly
 * @author Dan Smith
 */

public abstract class BaseOutputHandler
{

    protected String sourceFileName;
    protected String defaultPath;
    protected boolean warnings = true;
    public Vector events;
    protected boolean showSourceCodeInCompilerErrors = true;
    protected boolean showStacktrace = true;
    protected boolean logCompilerErrors = true;
    protected boolean keepGeneratedAS = true;
    protected String encoding;
    protected SourceCodeLoader sourceCodeLoader;

    public BaseOutputHandler(String mxmlFileName, String rootPath, Vector events, SourceCodeLoader sourceCodeLoader)
    {
        super();

        ServerConfiguration cfg = ServiceFactory.getConfigurator().getServerConfiguration();
        showSourceCodeInCompilerErrors = cfg.getDebuggingConfiguration().showSourceInCompilerErrors();
        showStacktrace = cfg.getDebuggingConfiguration().showStacktrace();
        logCompilerErrors = cfg.getDebuggingConfiguration().logCompilerErrors();
        keepGeneratedAS = cfg.getFlexConfigConfiguration().getCompilerConfiguration().keepGeneratedActionScript();
        encoding = cfg.getFlexConfigConfiguration().getCompilerConfiguration().getActionscriptFileEncoding();

        this.sourceFileName = mxmlFileName;
        this.defaultPath = rootPath;

        this.events = events;

        this.sourceCodeLoader = sourceCodeLoader;
    }

    /**
     * Print a copy of an exception
     *
     * @param throwable exception to be printed
     * @param buf       StringBuffer where exception is formatted
     */
    private void appendException(Throwable throwable, StringBuffer buf)
    {
        String msg, desc;
        int pos;
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);

        desc = throwable.toString();
        if ((pos = desc.indexOf(":")) > -1)
            desc = desc.substring(0, pos);

        msg = throwable.getMessage();
        if (msg == null)
            msg = "";
        buf.append("Exception ");
        buf.append(desc);
        buf.append("\n");
        buf.append(msg);


        throwable.printStackTrace(pw);
        pos = sw.toString().indexOf('\n');
        buf.append(sw.toString().substring(pos + 1));

        // Print a nested exception if it exists - use reflection instead
        // of directly casting to a ServletException, keeping servlet classes
        // out of this shared cross-platform code
        Throwable rootCause = null;
        try
        {
            java.lang.reflect.Method rootCauseMethod = throwable.getClass().getMethod("getRootCause", (Class) null);
            rootCause = (Throwable) rootCauseMethod.invoke(throwable, (Object) null);
        }
        catch (Exception ignorableReflectionEx)
        {}

        if (rootCause != null)
        {
            pw.flush();
            sw.flush();
            desc = rootCause.toString();
            if ((pos = desc.indexOf(":")) > -1)
                desc = desc.substring(0, pos);
            msg = throwable.getMessage();
            if (msg == null)
                msg = "";
            // header is created separately from the stacktrace because it must not be entitize'ed
            buf.append("Root Cause Exception ");
            buf.append(desc);
            buf.append(" ");
            buf.append(msg);
            buf.append("\n");
            rootCause.printStackTrace(pw);
            pos = sw.toString().indexOf('\n');
            buf.append(sw.toString().substring(pos + 1));
        }
    }

    /**
     * Load the source code number (w/ contextual lines) for all compile events
     * <p/>
     * Note: the same line may be referenced in multiple errors so all line numbers referenced in the same
     * file are processed in the same pass.
     */
    private void loadSourceCodeSnippets()
    {
        CompileEvent ce;
        final int contextLines = 1;           // Number of lines of context before and after the source line
        SortedSet srcLineNumbers = new TreeSet();  // All line numbers wanted in the current file

        for (int currentEventIndex = 0; currentEventIndex < events.size(); currentEventIndex++)
        {
            ce = (CompileEvent) events.get(currentEventIndex);

            if (ce.path != null && ce.line != 0 && ce.sourceSnippet == null)
            {
                String path = ce.path;

                // Create a sorted list of line numbers of interest in this file from all compile events
                for (int j = currentEventIndex; j < events.size(); j++)
                {
                    CompileEvent ce2 = (CompileEvent) events.get(j);

                    if ((ce2.sourceSnippet == null) && (ce2.line != 0) &&
                        (ce2.path != null) && ce2.path.equals(path))
                    {
                        int l = ce2.line - contextLines;
                        if (l < 0)
                        {
                            l = 0;
                        }
                        for (; l <= (ce2.line + contextLines); l++)
                        {
                            srcLineNumbers.add(new Integer(l));
                        }
                    }
                }

                try {

                    if (sourceCodeLoader == null)
                    {
                        Logger logger = ServiceFactory.getLogger();
                        logger.logWarning("Skipping source code snippet because '" + ce.path + "' could not be loaded.");
                    }
                    else
                    {
                        Map srcLinesText = sourceCodeLoader.getSourceForLineNumbers(path, srcLineNumbers, encoding);

                        // Format the lines and store in the compile event
                        for (int j = currentEventIndex; j < events.size(); j++)
                        {
                            CompileEvent ce2 = (CompileEvent) events.get(j);

                            if ((ce2.line != 0) && (ce2.sourceSnippet == null) &&
                                (ce2.path != null) && ce2.path.equals(path))
                            {
                                int startingLine = ce2.line - contextLines;
                                String[] lines;
                                int numberOfLines;

                                if (startingLine < 0)
                                {
                                    startingLine = 0;
                                }
                                numberOfLines = ce2.line + contextLines - startingLine + 1;
                                lines = new String[numberOfLines];

                                for (int k = 0; k < numberOfLines; k++)
                                {
                                    lines[k] = ((String) srcLinesText.get(new Integer(startingLine + k)));
                                }
                                ce2.sourceSnippet = new SourceCodeSnippet(startingLine, lines);
                            }
                        }

                        srcLineNumbers.clear();
                        srcLinesText.clear();
                    }

                }
                catch (IOException ioe)
                {
                    Logger logger = ServiceFactory.getLogger();
                    logger.logWarning("Skipping source code snippet because '" + ce.path + "' could not be loaded.");
                }
            }
        }
    }

    private void clearSourceCodeSnippets()
    {

        for (int i = 0; i < events.size(); i++)
        {
            CompileEvent ce = (CompileEvent)events.get(i);
            if (ce.sourceSnippet != null)
            {
                ce.sourceSnippet = null;
            }
        }
    }

    private boolean hasException()
    {
        boolean exception = false;
        Enumeration enumeration = events.elements();
        while (enumeration.hasMoreElements() && (!exception))
        {
            CompileEvent event = (CompileEvent)enumeration.nextElement();
            if (event.eventType == CompileEvent.EVT_EXCEPTION)
            {
                exception = true;
            }
        }
        return exception;
    }
    /**
     * Output results of compilation into system log.  Exceptions not caught during compilation are always
     * logged as errors.  The user may also configure that all results of compilations go to the system log.
     */
    public void logCompilerResults()
    {
        // exceptions are always logged
        if (hasException() && !(logCompilerErrors))
        {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            pw.println("Exception(s) generated while compiling " + this.sourceFileName);
            for (int i = 0; i < events.size(); i++)
            {
                CompileEvent ce = (CompileEvent) events.get(i);
                if (ce.eventType == CompileEvent.EVT_EXCEPTION)
                {
                    ce.exception.printStackTrace(pw);
                }
            }
            ServiceFactory.getLogger().logError(sw.toString());
        }
        else if (events.size() != 0 && (logCompilerErrors))
        {
            logIndividualEventsToLogger();
        }
    }


    public final void output()  throws IOException
    {

        logCompilerResults();

        if (showSourceCodeInCompilerErrors)
        {
            loadSourceCodeSnippets();
        }

        outputEvents();

        clearSourceCodeSnippets();
    }

    /**
     * Abstract function for displaying compiler events in the appropriate format (plain text, HTML, HTML fragment, etc)
     */
    protected abstract void outputEvents() throws IOException;

    /**
     * Create a summary of the number and type of CompileEvents stored in a Vector
     *
     * @return String  Summary of events in log
     */
    public String summary()
    {
        int arr[] = new int[CompileEvent.EVT_TOTAL];
        String names[] = new String[CompileEvent.EVT_TOTAL];
        StringBuffer buf = new StringBuffer();
        int totalEvents = events.size();
        CompileEvent ce;

        ce = null;
        for (int i = 0; i < events.size(); i++)
        {
            ce = (CompileEvent) events.get(i);
            arr[ce.eventType]++;
            names[ce.eventType] = ce.getEventTypeName();
        }
        for (int i = 0; i < CompileEvent.EVT_TOTAL; i++)
        {
            if (arr[i] > 0)
            {
                buf.append(arr[i]);
                buf.append(" ");
                buf.append(names[i]);
                if (arr[i] > 1)
                {
                    buf.append("s");
                }
                totalEvents -= arr[i];
                if (totalEvents > 0)            // * more to follow...
                {
                    buf.append(", ");
                }
            }
        }
        buf.append(" found.");
        return buf.toString();
    }

    public String toString()
    {
        StringBuffer result = new StringBuffer();

        result.append(summary());
        result.append("\n");

        for (int i = 0; i < events.size(); i++)
        {
            CompileEvent ce = (CompileEvent) events.get(i);

            if (ce.eventType == CompileEvent.EVT_EXCEPTION)
            {
                appendException(ce.exception, result);
            }
            else
            {
                logIndividualEvent(ce, result, false);
            }
            result.append("\n");
        }
        return result.toString();
    }

    protected void logIndividualEvent(CompileEvent ce, StringBuffer sb, boolean toLogger)
    {
        String lineRef;
         // Ignore line numbers of zero, they are typically link errors that are not associated with a line
         if (ce.line == 0)
         {
             lineRef = "";
         }
         else
         {
             lineRef = ":" + ce.line;
         }
         String docPath;
         if (ce.path == null)
         {
             docPath = "";
         }
         else
         {
             docPath = ce.path;
         }
         if (!toLogger)
         {
             sb.append(ce.getEventTypeName());
             sb.append(" ");
         }
         sb.append(docPath);
         sb.append(lineRef);
         sb.append("\n");
         sb.append(ce.description);
         sb.append("\n");
         if (keepGeneratedAS && ce.asPath != null)
         {
             sb.append("at ");
             sb.append(ce.asPath);
             sb.append(":");
             sb.append(ce.asLine);
             sb.append("\n");
         }
         if (ce.sourceSnippet != null)
         {
             sb.append("\n");
             sb.append(ce.sourceSnippet);
         }
    }

    public void logIndividualEventsToLogger()
    {
        Logger logger = ServiceFactory.getLogger();
        for (int i = 0; i < events.size(); i++)
        {
            CompileEvent ce = (CompileEvent) events.get(i);

            if (ce.eventType == CompileEvent.EVT_EXCEPTION)
            {
                StringWriter sw = new StringWriter();
                PrintWriter pw = new PrintWriter(sw);
                ce.exception.printStackTrace(pw);
                logger.logError(sw.toString());
            }
            else if ((ce.eventType == CompileEvent.EVT_ERROR))
            {
                StringBuffer sb = new StringBuffer();
                logIndividualEvent(ce, sb, true);
                logger.logError(sb.toString());
            }
            else if ((ce.eventType == CompileEvent.EVT_WARNING) && (logger.isWarningEnabled()))
            {
                StringBuffer sb = new StringBuffer();
                logIndividualEvent(ce, sb, true);
                logger.logWarning(sb.toString());
            }
            else if ((ce.eventType == CompileEvent.EVT_INFO) && (logger.isInfoEnabled()))
            {
                StringBuffer sb = new StringBuffer();
                logIndividualEvent(ce, sb, true);
                logger.logInfo(sb.toString());
            }
        }
    }
}
