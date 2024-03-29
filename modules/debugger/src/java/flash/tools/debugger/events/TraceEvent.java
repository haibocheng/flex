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
 * Trace is a special operation by the player that
 * allows text strings to be displayed during the
 * execution of some ActionScript.  
 * <p>
 * The event provides notification that a trace 
 * statement was encountered. The info string 
 * contains the contenxt of the trace message.
 */
public class TraceEvent extends DebugEvent
{
	public TraceEvent(String s) { super(s); }
}
