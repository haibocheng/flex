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
package flex.webtier.server.j2ee.html;

import flex.webtier.util.J2EEUtil;
import flex.webtier.server.j2ee.MxmlContext;
import flex.webtier.server.j2ee.events.HtmlOutputHandler;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;

import javax.servlet.ServletContext;

public class EventsRenderer
{
    public void renderEventsStyle(PrintWriter output)
    {
        output.println(" body,pre { font-family:Tahoma,Helvetica,sans-serif; font-size:10pt; }");
        output.println(" div.indent { margin-left: 20;}");
        output.println(" span.reference { font-weight: bold }");
        output.println(" span.header {white-space:nowrap }");
        output.println(" span.title {font-size:12pt; color:blue}");
        output.println(" span.highlight { background-color: #FFFFB7 }");
        output.println(" table.code { font-family: courier; font-size: 9pt;}");
        output.println("span.fade { color:#666666; font-size:11px }");
    }

    public void renderEventsScript(PrintWriter output)
    {
        output.println("    <script language='JavaScript' charset='utf-8'>");
        output.println("    function showHide(targetName) {  ");
        output.println("        if( document.getElementById ) { // NS6+");
        output.println("            target = document.getElementById(targetName);");
        output.println("        } else if( document.all ) { // IE4+");
        output.println("            target = document.all[targetName];");
        output.println("        }");

        output.println("        if( target ) {");
        output.println("            if( target.style.display == \"none\" ) {");
        output.println("                target.style.display = \"inline\";");
        output.println("            } else {");
        output.println("                target.style.display = \"none\";");
        output.println("            }");
        output.println("        }");
        output.println("    }");
        output.println("    </script>");
    }

    public void renderEvents(MxmlContext context, Writer output) throws IOException
    {
        ServletContext servletContext = context.getServletContext();
        String mxmlFileName = J2EEUtil.getRealPath(context.getRequest().getServletPath(), servletContext);
        String webRoot = J2EEUtil.getRealPath("/", servletContext);
        HtmlOutputHandler htmlOutputHandler = new HtmlOutputHandler(mxmlFileName, webRoot, output, context.getEvents(), context.showAllWarnings(), context.getSourceCodeLoader());
        htmlOutputHandler.output();
    }
}
