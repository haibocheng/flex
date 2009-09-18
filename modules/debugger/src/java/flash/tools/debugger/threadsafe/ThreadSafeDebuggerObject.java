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

/**
 * Intended to be subclassed.
 * 
 * @author Mike Morearty
 */
class ThreadSafeDebuggerObject {

	private Object fSyncObj;

	protected ThreadSafeDebuggerObject(Object syncObj) {
		fSyncObj = syncObj;
	}

	public final Object getSyncObject() {
		return fSyncObj;
	}
}
