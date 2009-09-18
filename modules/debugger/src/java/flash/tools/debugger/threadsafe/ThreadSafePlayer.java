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

import java.io.File;

import flash.tools.debugger.Browser;
import flash.tools.debugger.Player;

/**
 * Thread-safe wrapper for flash.tools.debugger.Player
 * @author Mike Morearty
 */
public class ThreadSafePlayer extends ThreadSafeDebuggerObject implements Player {

	private Player fPlayer;
	
	private ThreadSafePlayer(Object syncObj, Player player) {
		super(syncObj);
		fPlayer = player;
	}

	/**
	 * Wraps a Player inside a ThreadSafePlayer.  If the passed-in Player
	 * is null, then this function returns null.
	 */
	public static Player wrap(Object syncObj, Player player) {
		if (player != null)
			return new ThreadSafePlayer(syncObj, player);
		else
			return null;
	}

	/*
	 * @see flash.tools.debugger.Player#getType()
	 */
	public int getType() {
		synchronized (getSyncObject()) {
			return fPlayer.getType();
		}
	}

	/*
	 * @see flash.tools.debugger.Player#getPath()
	 */
	public File getPath() {
		synchronized (getSyncObject()) {
			return fPlayer.getPath();
		}
	}

	public Browser getBrowser() {
		synchronized (getSyncObject()) {
			return fPlayer.getBrowser();
		}
	}

}
