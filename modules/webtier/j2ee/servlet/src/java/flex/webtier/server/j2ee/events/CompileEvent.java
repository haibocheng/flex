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


public class CompileEvent
{
    public int code;
    public int line;
    public String path;
    public String description;
    public int asLine;
    public String asPath;
    public int eventType;   // Error, Warning, Exception, etc
    public String eventTypeName;
    public Throwable exception;
    protected SourceCodeSnippet sourceSnippet;
    public boolean oneTime;

    public static final int EVT_INFO = 0;
    public static final int EVT_ERROR = 1;
    public static final int EVT_WARNING = 2;
    public static final int EVT_EXCEPTION = 3;
    public static final int EVT_TOTAL = 4;  // Number of event types
    public String[] eventNames = new String[]{"Info", "Error", "Warning", "Exception"};

    public CompileEvent(int eventType, String eventTypeName, int code, int line, String path, int asLine, String asPath, String description, boolean oneTime)
    {
        this.code = code;
        this.line = line;
        this.path = path;
        this.description = description;
        this.eventType = eventType;
        this.eventTypeName = eventTypeName;
        this.asPath = asPath;
        this.asLine = asLine;
        this.sourceSnippet = null;
        this.oneTime = oneTime;
    }

    public CompileEvent(Throwable e)
    {
        this.exception = e;
        this.eventType = EVT_EXCEPTION;
    }

    public String getEventTypeName()
    {
        if (eventType == EVT_EXCEPTION)
        {
            return "Exception";
        }
        else if (eventTypeName != null)
        {
            return eventTypeName;
        }
        else
        {
            return "Unknown";
        }
    }

    public boolean isOneTime()
    {
        return oneTime;
    }
}
