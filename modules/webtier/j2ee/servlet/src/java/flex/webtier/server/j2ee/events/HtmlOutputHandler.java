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

import javax.servlet.ServletException;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Vector;

/**
 * HtmlOutputHandler - Creates a vector of the events generated during compilation.
 *
 * @author Cathy Reilly
 * @author Dan Smith
 */
public class HtmlOutputHandler extends BaseOutputHandler {
    protected Writer out;
    private int uniqueIdValue = 0;
    protected String webRoot;
    private boolean showAllWarnings;

    // TODO - these need to be updated to Flex4 URLS when they become available
    private final String FLEX_DOC_URL = "http://www.adobe.com/go/flex3_apps";
    private final String FLEX_API_URL = "http://www.adobe.com/go/flex3_reference";
               
    public HtmlOutputHandler(String mxmlFileName, String webRoot, Writer out, Vector events, boolean showAllWarnings, SourceCodeLoader sourceCodeLoader)
    {
        super(mxmlFileName, webRoot, events, sourceCodeLoader);
        this.out = out;
        this.webRoot = webRoot;
        this.showAllWarnings = showAllWarnings;
     }

    protected String alignRight(int number, int width)
    {
        StringBuffer result = new StringBuffer();
        String value = String.valueOf(number);

        width -= value.length();
        if (width < 0)
        {
            width = 0;
        }
        while (width-- > 0)
        {
            result.append(" ");
        }
        result.append(value);
        return result.toString();
    }

    /**
     * Quote embedded HTML chars
     * @param s input string
     * @return copy of string with quoted <,>, and &
     */
    protected String entitize(String s)
    {
        StringBuffer stringBuffer = null;

        if (s == null)
            return null;

        stringBuffer = new StringBuffer();

        for (int i = 0; i < s.length(); i++)
        {
            switch (s.charAt(i))
            {
                case '&':
                    stringBuffer.append("&amp;");
                    break;
                case '<':
                    stringBuffer.append("&lt;");
                    break;
                case '>':
                    stringBuffer.append("&gt;");
                    break;
                default:
                    stringBuffer.append(s.charAt(i));
                    break;
            }
        }
        return stringBuffer.toString();
    }

    private String emitCollapsibleText(Writer out, String text, String caption)  throws IOException
    {
        String id = new String("Hidden_" + uniqueId());

        // Print stack trace as a collapsible text area
        out.write(" <a href=\"javascript:;\" onMouseOver=\"window.status='Click to expand';return true;\"");
        out.write("onMouseOut=\"window.status='';return true;\" ");
        out.write("onClick=\"showHide('" + id + "');return true;\">" + caption + "</a>");
        out.write("<table width=\"500\" cellpadding=\"0\" cellspacing=\"0\">");
        out.write("   <tr><td valign=\"top\">");
        out.write("    </td></tr>\n<td id='" + id + "' style=\"display:none\">");
        out.write("<pre>");
        out.write(text);
        out.write("</pre></td>");
        out.write(" </tr>");
        out.write("</table>");

        return id;
    }

    protected void emitError(CompileEvent ce, PrintWriter out)
    {
        String desc = ce.description;
        String lineRef;
        String formattedLine;

        desc = quoteLeadingSpaces(entitize(desc));

        // Ignore line numbers of zero, they are typically link errors that are not associated with a line
        if (ce.line == 0)
        {
            lineRef = "";
        }
        else
        {
            lineRef = ":" + ce.line;
        }
        out.println("<span class='header'>" + ce.getEventTypeName() + " <span class='reference'>" + normalizePath(ce.path) + lineRef + "</span></span>");
        out.println("<div class='indent'>" + desc + "<br>");
        if (keepGeneratedAS && ce.asPath != null)
        {
            out.println("at <span class='reference'>" + normalizePath(ce.asPath) + ":" + ce.asLine + "</span><br>&nbsp;");
        }
        out.println("<br></div>");
        if (ce.sourceSnippet != null)
        {
            out.print("<div class='indent'><table class='code'>");
            for (int j = 0; j < ce.sourceSnippet.lines.length; j++)
            {
                String srcLine = ce.sourceSnippet.lines[j];
                if (srcLine != null)
                {
                    out.print("<tr><td align='right' valign='top'>" + alignRight(ce.sourceSnippet.startingLine + j, 4) + ":</td><td>");
                    formattedLine = quoteLeadingSpaces(expandTabs(entitize(srcLine)));
                    if ((ce.sourceSnippet.startingLine + j) == ce.line) // error line
                    {
                        out.print("<span class='highlight'>" + formattedLine + "</span>");
                    }
                    else
                    {
                        out.println(formattedLine);
                    }
                }
                out.println("</td></tr>");
            }
            out.print("</table></div>");
        }
        out.println("<br>");
    }


    /**
     * Print a copy of an exception
     *
     * @param throwable   exception to be printed
     * @param out         PrintWriter where exception is written
     */
    protected void emitException(Throwable throwable, PrintWriter out)
    {
        String stackTrace, msg, desc;
        int pos;
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);

        desc = throwable.toString();
        if ((pos = desc.indexOf(":")) > -1)
            desc = desc.substring(0, pos);

        msg = throwable.getMessage();
        if (msg == null)
            msg = "";
        msg = quoteLeadingSpaces(entitize(msg));
        out.println("Exception <span class='reference'>" + desc + "</span>");
        out.println("<div class='indent'>" + msg);

        if (showStacktrace)
        {
            throwable.printStackTrace(pw);
            pos = sw.toString().indexOf('\n');
            stackTrace = sw.toString().substring(pos + 1);

            // Print a nested exception if it exists
            if (throwable instanceof ServletException)
            {
                Throwable rootCause = ((ServletException) throwable).getRootCause();
                String tmp, header;

                if (rootCause != null)
                {
                    pw.flush();
                    sw.flush();
                    desc = rootCause.toString();
                    if ((pos = desc.indexOf(":")) > -1)
                    {
                        desc = desc.substring(0, pos);
                    }
                    msg = throwable.getMessage();
                    if (msg == null)
                    {
                        msg = "";
                    }
                    // header is created separately from the stacktrace because it must not be entitize'ed
                    header = "<br>&nbsp;<br>Root Cause Exception <span class='reference'>" + desc + "</span><br><div class='indent'>" +
                            quoteLeadingSpaces(entitize(msg)) + "<br>";

                    rootCause.printStackTrace(pw);
                    pos = sw.toString().indexOf('\n');
                    tmp = "<pre>" + entitize(sw.toString().substring(pos + 1)) + "</pre></div>";
                    stackTrace += header + tmp;
                }
            }
			try{
				emitCollapsibleText(out, stackTrace, "(View stack trace)");
			}catch(IOException e){
				e.printStackTrace();
			}

        }
        out.println("</div><br>");          // This div closes out the indent <div class="indent">
    }

    /**
     * Replace tabs in a string assuming 4-column tabs.
     *
     * @param s input string; assumed to start in column one of output.
     * @return  String without tabs
     */
    protected String expandTabs(String s)
    {
        StringBuffer result = new StringBuffer();
        int column = 0;

        for (int i = 0; i < s.length(); i++)
        {
            char c = s.charAt(i);
            if (c == '\n')
            {
                column = 0;
            }
            if (c == '\t')
            {
                int curCol = column;
                column = (column + 4) & ~3;
                while (curCol != column)
                {
                    curCol++;
                    result.append(" ");
                }
            }
            else
            {
                result.append(c);
                column++;
            }
        }
        return result.toString();
    }

    protected String normalizePath(String path)
    {
        int i = 0;

        if(path == null || path.equals(defaultPath))
        {
            path = sourceFileName;
        }
        if(path.startsWith(webRoot))
        {
            if (webRoot.endsWith("/") || webRoot.endsWith("\\"))
            {
                i = 1;
            }
            path = path.substring(webRoot.length()-i).replace('\\','/');
        }
        return path;
    }


    /**
     * Convert leading white spaces in multi-line string to &nbsp;
     *
     * @param s string being converted.
     * @return Copy of converted string; null is returned for null input string.
     */
    protected String quoteLeadingSpaces(String s)
    {
        StringBuffer stringbuffer;
        boolean leadingWhiteSpace = true;

        if (s != null)
        {
            stringbuffer = new StringBuffer(s.length());
            for (int i = 0; i < s.length(); i++)
            {
                char c = s.charAt(i);
                switch (c)
                {
                    case '\n':
                        leadingWhiteSpace = true;
                        stringbuffer.append("<br>");
                        break;
                    case ' ':
                        if (leadingWhiteSpace)
                            stringbuffer.append("&nbsp;");
                        else
                            stringbuffer.append(c);
                        break;
                    default:
                        stringbuffer.append(c);
                        leadingWhiteSpace = false;
                        break;
                }

            }
            s = stringbuffer.toString();
        }
        return s;
    }

    /**
     * Print the results of a compilation to the servlet response stream.   This entry point assumes
     * it controls the entire output stream and is free to emit the head and body portions of the response.
     */
    public void outputEvents() throws IOException
    {
        out.write("<span class='title'>Compilation Results</span>"+"\n");
        out.write("<br>"+"\n");
        out.write("&nbsp;"+"\n");
        out.write("<br>"+"\n");
        out.write("Errors, warnings or exceptions were found while compiling <span class='reference'>" + entitize(normalizePath(sourceFileName)) + "</span>."+"\n");
        out.write("Visit the online Flex <a href=\"" + FLEX_DOC_URL + "\">documentation</a> or " +
                "<a href=\"" + FLEX_API_URL + "\">API reference</a> for further information.\n");

        out.write("<br>&nbsp;<br>"+"\n");
        out.write("<hr>"+"\n");
        out.write("<br>"+"\n");

        //Use buffers as we need the depreciated code to appear before the summary and normal errors
        StringWriter deprecatedBuffer = new StringWriter(4096);
        PrintWriter deprecatedWriter = new PrintWriter(deprecatedBuffer);

        StringWriter errorBuffer = new StringWriter(4096);
        PrintWriter errorWriter = new PrintWriter(errorBuffer);

        for (int i = 0; i < events.size(); i++)
        {
            CompileEvent ce = (CompileEvent) events.get(i);

            if (ce.eventType == CompileEvent.EVT_EXCEPTION)
            {
                emitException(ce.exception, errorWriter);
            }
            else if (showAllWarnings && isDeprecatedWarning(ce))
            {
                emitError(ce, deprecatedWriter);
            }
            else if (showAllWarnings && isWarning(ce))
            {
                emitError(ce, errorWriter);
            }
            else
            {
                emitError(ce, errorWriter);
            }
        }

        //Ensure all content is printed to the backing string buffer
        deprecatedWriter.flush();
        errorWriter.flush();

        String deprecatedText = deprecatedBuffer.toString();
        if (deprecatedText != null && deprecatedText.length() > 0)
        {
            out.write("Deprecated features were used in this application.");
            emitCollapsibleText(this.out, deprecatedText, " Click here to see the list of deprecated features.<br>");
        }

        out.write("<br>"+"\n");
        out.write(summary()+"\n");
        out.write("<br>&nbsp;<br>"+"\n");

        String errorText = errorBuffer.toString();
        out.write(errorText);

        out.write("<br>&nbsp;<br>"+"\n");
        out.write("<br><hr><br>"+"\n");
    }

    /**
     * Create a summary of the number and type of CompileEvents stored in a Vector, but
     * ensure not to count the deprecated warnings in the totals
     * It will not count warnings which are not displayed when showAllWarnings is false;
     *
     * @return String  Sumamry of events in log
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
            if ((!showAllWarnings) || (!isDeprecatedWarning(ce)))
            {
                arr[ce.eventType]++;
                names[ce.eventType] = ce.getEventTypeName();
            }
            else
            {
                totalEvents--;
            }
        }

        boolean eventReported = false;

        for (int i = 0; i < CompileEvent.EVT_TOTAL; i++)
        {
            if (arr[i] > 0)
            {
                eventReported = true;
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

        if (eventReported)
        {
            buf.append(" found.");
        }

        return buf.toString();
    }

    public boolean isDeprecatedWarning(CompileEvent ce)
    {
        return ce.eventType == CompileEvent.EVT_WARNING
                && (ce.code == AsMsgs.kDeprecatedWithSuggestionWarning
                    || ce.code == AsMsgs.kDeprecatedWarning);
    }

    public boolean isWarning(CompileEvent ce)
    {
        return ce.eventType == CompileEvent.EVT_WARNING;
    }

    private int uniqueId()
    {
        return uniqueIdValue++;
    }
}