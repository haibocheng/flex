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

import flash.tools.debugger.Location;
import flash.tools.debugger.SourceFile;

/**
 * Thread-safe wrapper for flash.tools.debugger.Location
 * @author Mike Morearty
 */
public class ThreadSafeLocation extends ThreadSafeDebuggerObject implements Location {

	private Location fLocation;
	
	private ThreadSafeLocation(Object syncObj, Location location) {
		super(syncObj);
		fLocation = location;
	}

	/**
	 * Wraps a Location inside a ThreadSafeLocation.  If the passed-in Location
	 * is null, then this function returns null.
	 */
	public static ThreadSafeLocation wrap(Object syncObj, Location location) {
		if (location != null)
			return new ThreadSafeLocation(syncObj, location);
		else
			return null;
	}

	/**
	 * Wraps an array of Locations inside an array of ThreadSafeLocations.
	 */
	public static ThreadSafeLocation[] wrapArray(Object syncObj, Location[] locations) {
		ThreadSafeLocation[] threadSafeLocations = new ThreadSafeLocation[locations.length];
		for (int i=0; i<locations.length; ++i) {
			threadSafeLocations[i] = wrap(syncObj, locations[i]);
		}
		return threadSafeLocations;
	}

	/**
	 * Returns the raw Location underlying a ThreadSafeLocation.
	 */
	public static Location getRaw(Location l) {
		if (l instanceof ThreadSafeLocation)
			return ((ThreadSafeLocation)l).fLocation;
		else
			return l;
	}

	public static Object getSyncObject(Location l) {
		return ((ThreadSafeLocation)l).getSyncObject();
	}

	public SourceFile getFile() {
		synchronized (getSyncObject()) {
			return ThreadSafeSourceFile.wrap(getSyncObject(), fLocation.getFile());
		}
	}
	
	public int getLine() {
		synchronized (getSyncObject()) {
			return fLocation.getLine();
		}
	}
}
