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

/**
 * @author Karl Moss
 * @since 09Apr2001
 */
public abstract class LogEventHandler
{
	private String format;

    // service methods
    public void init() {}
    public void start() {}
	public void stop() {}

    public boolean log(LogEvent event)
    {
        //System.out.println("logging to " + this.getClass() + " event " + event.getMessage());
        
        return logEvent(event);
    }

    public abstract boolean logEvent(LogEvent event);

	public void setFormat(String format)
	{
		this.format = format;
	}

	public String getFormat()
	{
		return format;
	}

	public void flush()
	{
		// No-op by default
	}
}
