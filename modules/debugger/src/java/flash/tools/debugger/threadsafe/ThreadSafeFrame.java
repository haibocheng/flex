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

import flash.tools.debugger.Frame;
import flash.tools.debugger.Location;
import flash.tools.debugger.NoResponseException;
import flash.tools.debugger.NotConnectedException;
import flash.tools.debugger.NotSuspendedException;
import flash.tools.debugger.Session;
import flash.tools.debugger.Variable;

/**
 * Thread-safe wrapper for flash.tools.debugger.Frame
 * @author Mike Morearty
 */
public class ThreadSafeFrame extends ThreadSafeDebuggerObject implements Frame {
	
	private Frame fFrame;
	
	private ThreadSafeFrame(Object syncObj, Frame frame) {
		super(syncObj);
		fFrame = frame;
	}

	/**
	 * Wraps a Frame inside a ThreadSafeFrame.  If the passed-in Frame
	 * is null, then this function returns null.
	 */
	public static ThreadSafeFrame wrap(Object syncObj, Frame frame) {
		if (frame != null)
			return new ThreadSafeFrame(syncObj, frame);
		else
			return null;
	}

	/**
	 * Wraps an array of Frames inside an array of ThreadSafeFrames.
	 */
	public static ThreadSafeFrame[] wrapArray(Object syncObj, Frame[] frames) {
		ThreadSafeFrame[] threadSafeFrames = new ThreadSafeFrame[frames.length];
		for (int i=0; i<frames.length; ++i) {
			threadSafeFrames[i] = wrap(syncObj, frames[i]);
		}
		return threadSafeFrames;
	}

	public static Object getSyncObject(Frame f) {
		return ((ThreadSafeFrame)f).getSyncObject();
	}
	
	public int hashCode() {
		synchronized (getSyncObject()) {
			return fFrame.hashCode();
		}
	}

	public boolean equals(Object other) {
		synchronized (getSyncObject()) {
			if (other == null)
				return false;
			if (other instanceof ThreadSafeFrame) {
				return (fFrame.equals(((ThreadSafeFrame)other).fFrame));
			}
			if (other instanceof Frame) {
				return (fFrame.equals(other));
			}
			return false;
		}
	}

	public String toString() {
		synchronized (getSyncObject()) {
			return fFrame.toString();
		}
	}

	// -- beginning of delegate functions --

	public Variable[] getArguments(Session s) throws NoResponseException, NotSuspendedException, NotConnectedException {
		synchronized (getSyncObject()) {
			return ThreadSafeVariable.wrapArray(getSyncObject(), fFrame.getArguments(ThreadSafeSession.getRaw(s)));
		}
	}

	public String getCallSignature() {
		synchronized (getSyncObject()) {
			return fFrame.getCallSignature();
		}
	}

	public Variable[] getLocals(Session s) throws NoResponseException, NotSuspendedException, NotConnectedException {
		synchronized (getSyncObject()) {
			return ThreadSafeVariable.wrapArray(getSyncObject(), fFrame.getLocals(ThreadSafeSession.getRaw(s)));
		}
	}

	public Location getLocation() {
		synchronized (getSyncObject()) {
			return ThreadSafeLocation.wrap(getSyncObject(), fFrame.getLocation());
		}
	}

	public Variable getThis(Session s) throws NoResponseException, NotSuspendedException, NotConnectedException {
		synchronized (getSyncObject()) {
			return ThreadSafeVariable.wrap(getSyncObject(), fFrame.getThis(ThreadSafeSession.getRaw(s)));
		}
	}

	public Variable[] getScopeChain(Session s) throws NoResponseException, NotSuspendedException, NotConnectedException {
		synchronized (getSyncObject()) {
			return ThreadSafeVariable.wrapArray(getSyncObject(), fFrame.getScopeChain(ThreadSafeSession.getRaw(s)));
		}
	}
}
