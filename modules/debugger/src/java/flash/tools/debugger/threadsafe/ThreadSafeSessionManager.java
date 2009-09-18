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

import java.io.IOException;

import flash.tools.debugger.AIRLaunchInfo;
import flash.tools.debugger.IDebuggerCallbacks;
import flash.tools.debugger.ILaunchNotification;
import flash.tools.debugger.IProgress;
import flash.tools.debugger.Player;
import flash.tools.debugger.Session;
import flash.tools.debugger.SessionManager;

/**
 * Thread-safe wrapper for flash.tools.debugger.SessionManager
 * @author Mike Morearty
 */
public class ThreadSafeSessionManager extends ThreadSafeDebuggerObject implements SessionManager {

	private SessionManager fSessionManager;
	
	private ThreadSafeSessionManager(SessionManager sessionManager) {
		super(new Object());
		fSessionManager = sessionManager;
	}

	/**
	 * Wraps a SessionManager inside a ThreadSafeSessionManager.  If the passed-in SessionManager
	 * is null, then this function returns null.
	 */
	public static ThreadSafeSessionManager wrap(SessionManager sessionManager) {
		if (sessionManager != null)
			return new ThreadSafeSessionManager(sessionManager);
		else
			return null;
	}

	public static Object getSyncObject(SessionManager sm) {
		return ((ThreadSafeSessionManager)sm).getSyncObject();
	}

	public Session accept(IProgress waitReporter) throws IOException {
		// WARNING: This function is not thread-safe.
		//
		// accept() can take a very long time -- e.g. if there is something wrong,
		// then it might hang for two minutes while waiting for the Flash player.
		// So, it is not acceptable to put this in a "synchronized" block.
		return ThreadSafeSession.wrap(getSyncObject(), fSessionManager.accept(waitReporter));
	}

	public int getPreference(String pref) throws NullPointerException {
		synchronized (getSyncObject()) {
			return fSessionManager.getPreference(pref);
		}
	}

	public boolean isListening() {
		synchronized (getSyncObject()) {
			return fSessionManager.isListening();
		}
	}

	public Session launch(String uri, AIRLaunchInfo airLaunchInfo, boolean forDebugging, IProgress waitReporter, ILaunchNotification launchNotification) throws IOException {
		// WARNING: This function is not thread-safe.
		//
		// launch() can take a very long time -- e.g. if there is something wrong,
		// then it might hang for two minutes while waiting for the Flash player.
		// So, it is not acceptable to put this in a "synchronized" block.
		return ThreadSafeSession.wrap(getSyncObject(), fSessionManager.launch(uri, airLaunchInfo, forDebugging, waitReporter,launchNotification));
	}

	public Player playerForUri(String uri, AIRLaunchInfo airLaunchInfo) {
		synchronized (getSyncObject()) {
			return ThreadSafePlayer.wrap(getSyncObject(), fSessionManager.playerForUri(uri, airLaunchInfo));
		}
	}

	public boolean supportsLaunch()
	{
		synchronized (getSyncObject()) {
			return fSessionManager.supportsLaunch();
		}
	}

	public void setPreference(String pref, int value) {
		synchronized (getSyncObject()) {
			fSessionManager.setPreference(pref, value);
		}
	}

	public void setPreference(String pref, String value) {
		synchronized (getSyncObject()) {
			fSessionManager.setPreference(pref, value);
		}
	}

	public void startListening() throws IOException {
		synchronized (getSyncObject()) {
			fSessionManager.startListening();
		}
	}

	public void stopListening() throws IOException {
		synchronized (getSyncObject()) {
			fSessionManager.stopListening();
		}
	}

	public void setDebuggerCallbacks(IDebuggerCallbacks debuggerCallbacks) {
		synchronized (getSyncObject()) {
			fSessionManager.setDebuggerCallbacks(debuggerCallbacks);
		}
	}
}
