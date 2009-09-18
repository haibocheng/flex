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

import flash.tools.debugger.Watch;

/**
 * Thread-safe wrapper for flash.tools.debugger.Watch
 * @author Mike Morearty
 */
public class ThreadSafeWatch extends ThreadSafeDebuggerObject implements Watch {
	
	private Watch fWatch;
	
	private ThreadSafeWatch(Object syncObj, Watch watch) {
		super(syncObj);
		fWatch = watch;
	}

	/**
	 * Wraps a Watch inside a ThreadSafeWatch.  If the passed-in Watch
	 * is null, then this function returns null.
	 */
	public static ThreadSafeWatch wrap(Object syncObj, Watch watch) {
		if (watch != null)
			return new ThreadSafeWatch(syncObj, watch);
		else
			return null;
	}

	/**
	 * Wraps an array of Watches inside an array of ThreadSafeWatches.
	 */
	public static ThreadSafeWatch[] wrapArray(Object syncObj, Watch[] watchs) {
		ThreadSafeWatch[] threadSafeWatches = new ThreadSafeWatch[watchs.length];
		for (int i=0; i<watchs.length; ++i) {
			threadSafeWatches[i] = wrap(syncObj, watchs[i]);
		}
		return threadSafeWatches;
	}

	/**
	 * Returns the raw Watch underlying a ThreadSafeWatch.
	 */
	public static Watch getRaw(Watch w) {
		if (w instanceof ThreadSafeWatch)
			return ((ThreadSafeWatch)w).fWatch;
		else
			return w;
	}

	public static Object getSyncObject(Watch w) {
		return ((ThreadSafeWatch)w).getSyncObject();
	}

	public int getKind() {
		synchronized (getSyncObject()) {
			return fWatch.getKind();
		}
	}

	public String getMemberName() {
		synchronized (getSyncObject()) {
			return fWatch.getMemberName();
		}
	}

	public long getValueId() {
		synchronized (getSyncObject()) {
			return fWatch.getValueId();
		}
	}
}
