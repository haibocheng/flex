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

package flash.tools.debugger.threadsafe;

import flash.tools.debugger.Bootstrap;

/**
 * Thread-safe wrapper for flash.tools.debugger.Bootstrap
 * @author Mike Morearty
 */
public class ThreadSafeBootstrap {

	private static ThreadSafeSessionManager fMgr;

	private ThreadSafeBootstrap() {} // prevent instantiation

	public static synchronized ThreadSafeSessionManager sessionManager()
	{
		if (fMgr == null) {
			fMgr = ThreadSafeSessionManager.wrap(Bootstrap.sessionManager());
		}
		return fMgr;
	}
}
