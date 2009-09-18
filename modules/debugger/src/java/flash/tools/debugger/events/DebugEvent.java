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

package flash.tools.debugger.events;

/**
 * The top of the event hierarchy for debug events.  All debug events are of this type
 */
public abstract class DebugEvent
{
	public String information;

	public DebugEvent()					{ information = ""; } //$NON-NLS-1$
	public DebugEvent(String info)		{ information = info; }
}
