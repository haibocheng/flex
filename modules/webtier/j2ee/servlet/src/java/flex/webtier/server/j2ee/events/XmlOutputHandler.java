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

import flash.util.StringUtils;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Vector;


/**
 * format errors in XML for consumption by dreamweaver.  See messages.xsd for
 * the real definition of this format.
 * @author Edwin Smith
 */
public class XmlOutputHandler extends BaseOutputHandler
{
    private PrintWriter out;
    private String webRoot;

    public XmlOutputHandler(PrintWriter out, String mxmlFileName, String webRoot, Vector events, SourceCodeLoader sourceCodeLoader)
    {
        super(mxmlFileName, webRoot, events, sourceCodeLoader);
        this.out = out;
        this.webRoot = webRoot;
    }

    private String normalizePath(String path)
    {
		int i = 0;

	    if(path == null || path.equals(webRoot)){
		    path = sourceFileName;
	    }
        if(path.startsWith(webRoot)){
	        if (webRoot.endsWith("/") || webRoot.endsWith("\\"))
	            i = 1;
            path = path.substring(webRoot.length()-i).replace('\\','/');
        }
        return path;
    }


	private static String xmlTagName(int eventType){
			if(eventType == CompileEvent.EVT_ERROR)
				return "error";
			else if(eventType == CompileEvent.EVT_WARNING)
				return "warning";
			else if(eventType == CompileEvent.EVT_EXCEPTION)
				return "exception";
			else if(eventType == CompileEvent.EVT_INFO)
				return "info";
			else
				return "unknown";
	}

	protected void outputEvents(){
		out.println("<messages>");

		for(int i = 0; i < events.size(); i++){
			CompileEvent ce = (CompileEvent) events.get(i);
		    String tagName = xmlTagName(ce.eventType);

		    out.println("<" + tagName + ">");
			if(ce.eventType == CompileEvent.EVT_EXCEPTION){
            	out.println("    <exceptionType>" + ce.exception + "</exceptionType>");
			    if(ce.description != null){
					out.println("    <description>" + StringUtils.entitizeHtml(ce.description) + "</description>");
			    }
				// Ignore system-wide showStackTrace setting here.  Anyone using this interface
				// can decide to show the traces or not depending on their usage.
				StringWriter stackTrace = new StringWriter();
				PrintWriter pw = new PrintWriter(stackTrace);
				ce.exception.printStackTrace(pw);
				out.println("<stackTrace>" + stackTrace + "</stackTrace>");
			} else {

				if (ce.code > 0){
			         out.println("  <code>" + ce.code + "</code>");
			    }
		        out.println("  <path>" + normalizePath(ce.path) + "</path>");
			    out.println("  <line>" + ce.line + "</line>");
			    out.println("  <description>" + StringUtils.entitizeHtml(ce.description) + "</description>");

				/* Brady is not currently accepting the actionscript line numbers
				if(ce.asPath != null){
					out.println("   <asPath>" + ce.asPath + "</asPath>");
					out.println("   <asLine>" + ce.asLine + "</asLine>");
				}
				*/

			}
			out.println("</" + tagName + ">");
		}
		out.println("</messages>");
	}
}
