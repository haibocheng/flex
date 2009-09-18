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

import flash.tools.debugger.NoResponseException;
import flash.tools.debugger.NotConnectedException;
import flash.tools.debugger.NotSuspendedException;
import flash.tools.debugger.Session;
import flash.tools.debugger.Value;
import flash.tools.debugger.Variable;

/**
 * Thread-safe wrapper for flash.tools.debugger.Value
 * @author Mike Morearty
 */
public class ThreadSafeValue extends ThreadSafeDebuggerObject implements Value {

	private Value fVal;

	private ThreadSafeValue(Object syncObj, Value val) {
		super(syncObj);
		fVal = val;
	}

	/**
	 * Wraps a Value inside a ThreadSafeValue.  If the passed-in Value
	 * is null, then this function returns null.
	 */
	public static ThreadSafeValue wrap(Object syncObj, Value val) {
		if (val != null)
			return new ThreadSafeValue(syncObj, val);
		else
			return null;
	}

	/**
	 * Wraps an array of Values inside an array of ThreadSafeValues.
	 */
	public static ThreadSafeValue[] wrapArray(Object syncObj, Value[] values) {
		ThreadSafeValue[] threadSafeValues = new ThreadSafeValue[values.length];
		for (int i=0; i<values.length; ++i) {
			threadSafeValues[i] = wrap(syncObj, values[i]);
		}
		return threadSafeValues;
	}

	/**
	 * Returns the raw Value underlying a ThreadSafeValue.
	 */
	public static Value getRaw(Value v) {
		if (v instanceof ThreadSafeValue)
			return ((ThreadSafeValue)v).fVal;
		else
			return v;
	}

	public static Object getSyncObject(Value v) {
		return ((ThreadSafeValue)v).getSyncObject();
	}
	
	public boolean equals(Object other) {
		if (other instanceof Value)
			return fVal.equals(getRaw((Value)other));
		else
			return false;
	}

	public int getAttributes() {
		synchronized (getSyncObject()) { return fVal.getAttributes(); }
	}

	public String getClassName() {
		synchronized (getSyncObject()) { return fVal.getClassName(); }
	}

	public long getId() {
		synchronized (getSyncObject()) { return fVal.getId(); }
	}

	public int getMemberCount(Session s) throws NotSuspendedException, NoResponseException, NotConnectedException {
		synchronized (getSyncObject()) { return fVal.getMemberCount(ThreadSafeSession.getRaw(s)); }
	}

	public Variable getMemberNamed(Session s, String name) throws NotSuspendedException, NoResponseException, NotConnectedException {
		synchronized (getSyncObject()) {
			return ThreadSafeVariable.wrap(getSyncObject(), fVal.getMemberNamed(ThreadSafeSession.getRaw(s), name));
		}
	}

	public Variable[] getMembers(Session s) throws NotSuspendedException, NoResponseException, NotConnectedException {
		synchronized (getSyncObject()) {
			return ThreadSafeVariable.wrapArray(getSyncObject(), fVal.getMembers(ThreadSafeSession.getRaw(s)));
		}
	}

	public int getType() {
		synchronized (getSyncObject()) { return fVal.getType(); }
	}

	public String getTypeName() {
		synchronized (getSyncObject()) { return fVal.getTypeName(); }
	}

	public Object getValueAsObject() {
		synchronized (getSyncObject()) { return fVal.getValueAsObject(); }
	}

	public String getValueAsString() {
		synchronized (getSyncObject()) { return fVal.getValueAsString(); }
	}

	public boolean isAttributeSet(int variableAttribute) {
		synchronized (getSyncObject()) { return fVal.isAttributeSet(variableAttribute); }
	}

	public String[] getClassHierarchy(boolean allLevels) {
		synchronized (getSyncObject()) { return fVal.getClassHierarchy(allLevels); }
	}

	public String toString() {
		synchronized (getSyncObject()) { return fVal.toString(); }
	}
}
