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

import java.io.PrintStream;

/**
 * Sends log events to the console (stdout and stderr)
 *
 * @author Karl Moss
 * @since 09Apr2001
 */
public class ConsoleLogEventHandler
	extends LogEventHandler
{
	public boolean logEvent(LogEvent event)
	{
		// First, determine where the message is going
		PrintStream out = System.out;
		switch (event.getType()) 
		{
			case LogEvent.LOG_ERROR:
				out = System.err;
				break;
			default:
			    out = System.out;
		}

        synchronized(this)
        {
            out.println(event.getFormattedMessage(getFormat()));
        }
		return true;
	}
}
