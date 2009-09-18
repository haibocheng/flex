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

/**
 * NotSuspendedException is thrown when the Player 
 * is in a state for which the action cannot be performed.
 */
public class NotSuspendedException extends PlayerDebugException
{
	private static final long serialVersionUID = 1373922470760042675L;

    public String getMessage()
	{
		return Bootstrap.getLocalizationManager().getLocalizedTextString("notSuspended"); //$NON-NLS-1$
	}
}
