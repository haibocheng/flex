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
 * VersionException is thrown when the Session
 * is connected to a Player that does not support
 * a given operation.
 */
public class VersionException extends PlayerDebugException
{
	private static final long serialVersionUID = 4966523681921720567L;

    public String getMessage()
	{
		return Bootstrap.getLocalizationManager().getLocalizedTextString("unexpectedPlayerVersion"); //$NON-NLS-1$
	}
}
