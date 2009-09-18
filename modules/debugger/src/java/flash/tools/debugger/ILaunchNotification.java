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

import java.io.IOException;

/**
 * Used to notify caller in case of ADL Exit Code 1: Successful invocation of an already running 
 * AIR application. ADL exits immediately.
 * 
 * @author sakkus
 */
public interface ILaunchNotification
{
	/**
	 * Notifies the listener that the launch is done, and, if it failed,
	 * an exception with information about why it failed.
	 * 
	 * @param e
	 *            an exception if the launch failed, or null if the launch
	 *            succeeded.
	 */
	public void notify(IOException e);
}
