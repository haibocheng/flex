////////////////////////////////////////////////////////////////////////////////
//
//  ADOBE SYSTEMS INCORPORATED
//  Copyright 2003-2006 Adobe Systems Incorporated
//  All Rights Reserved.
//
//  NOTICE: Adobe permits you to use, modify, and distribute this file
//  in accordance with the terms of the license agreement accompanying it.
//
////////////////////////////////////////////////////////////////////////////////

package flash.tools.debugger;

import java.util.HashMap;
import java.util.Map;

/**
 * NoResponseException is thrown when the Player does
 * not respond to the command that was issued.
 * 
 * The field m_waitedFor contains the number of
 * milliseconds waited for the response.
 */
public class NoResponseException extends PlayerDebugException
{
	private static final long serialVersionUID = -3704426811630352537L;
    
    /**
	 * Number of milliseconds that elapsed causing the timeout
	 * -1 means unknown.
	 */
	public int m_waitedFor;

	public NoResponseException(int t) 
	{
		m_waitedFor = t;
	}

	public String getMessage()
	{
		Map<String, String> args = new HashMap<String, String>();
		String formatString;
		if (m_waitedFor != -1 && m_waitedFor != 0)
		{
			formatString = "timeout"; //$NON-NLS-1$
			args.put("time", Integer.toString(m_waitedFor)); //$NON-NLS-1$
		}
		else
		{
			formatString = "timeoutAfterUnknownDelay"; //$NON-NLS-1$
		}
		return Bootstrap.getLocalizationManager().getLocalizedTextString(formatString, args);
	}
}
