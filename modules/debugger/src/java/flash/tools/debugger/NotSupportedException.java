////////////////////////////////////////////////////////////////////////////////
//
//  ADOBE SYSTEMS INCORPORATED
//  Copyright 2008 Adobe Systems Incorporated
//  All Rights Reserved.
//
//  NOTICE: Adobe permits you to use, modify, and distribute this file
//  in accordance with the terms of the license agreement accompanying it.
//
////////////////////////////////////////////////////////////////////////////////

package flash.tools.debugger;

/**
 * Indicates that a debugger feature is not supported by the Flash
 * player that is being targeted.  For example, newer players
 * support the ability to have the debugger call arbitrary
 * functions, but older ones do not.
 * 
 * @author Mike Morearty
 */
public class NotSupportedException extends PlayerDebugException {
	private static final long serialVersionUID = -8873935118857320824L;

	/**
	 * @param s an error message, e.g. "Target player does not support
	 * function calls," or "Target player does not support watchpoints".
	 */
	public NotSupportedException(String s)
	{
		super(s);
	}
}
