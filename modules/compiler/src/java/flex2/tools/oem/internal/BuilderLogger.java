////////////////////////////////////////////////////////////////////////////////
//
//  ADOBE SYSTEMS INCORPORATED
//  Copyright 2006 Adobe Systems Incorporated
//  All Rights Reserved.
//
//  NOTICE: Adobe permits you to use, modify, and distribute this file
//  in accordance with the terms of the license agreement accompanying it.
//
////////////////////////////////////////////////////////////////////////////////

package flex2.tools.oem.internal;

import java.util.List;

import flex2.tools.oem.Logger;
import flex2.tools.oem.Message;

public class BuilderLogger implements Logger
{
	public BuilderLogger(Logger logger, List<Message> messages)
	{
		this.messages = messages;
		userLogger = logger;
	}
	
	private List<Message> messages;
	private Logger userLogger;
	
	public void log(Message message, int errorCode, String source)
	{
		if (messages != null)
		{
			messages.add(message);
		}
		
		if (userLogger != null)
		{
			userLogger.log(message, errorCode, source);
		}
	}
}
