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

import flash.tools.debugger.PlayerDebugException;
import flash.tools.debugger.expression.Context;
import flash.tools.debugger.expression.NoSuchVariableException;
import flash.tools.debugger.expression.PlayerFaultException;
import flash.tools.debugger.expression.ValueExp;

/**
 * Thread-safe wrapper for flash.tools.debugger.expression.ValueExp
 * @author Mike Morearty
 */
public class ThreadSafeValueExp extends ThreadSafeDebuggerObject implements ValueExp
{
	private final ValueExp m_valueExp;

	public ThreadSafeValueExp(Object syncObj, ValueExp valueExp)
	{
		super(syncObj);
		m_valueExp = valueExp;
	}

	/**
	 * Wraps a ValueExp inside a ThreadSafeValueExp. If the passed-in
	 * ValueExp is null, then this function returns null.
	 */
	public static ThreadSafeValueExp wrap(Object syncObj, ValueExp valueExp) {
		if (valueExp != null)
			return new ThreadSafeValueExp(syncObj, valueExp);
		else
			return null;
	}

	public Object evaluate(Context context) throws NumberFormatException, NoSuchVariableException, PlayerFaultException, PlayerDebugException
	{
		synchronized (getSyncObject()) {
			return m_valueExp.evaluate(context);
		}
	}

	public boolean containsAssignment()
	{
		synchronized (getSyncObject()) {
			return m_valueExp.containsAssignment();
		}
	}

	public boolean isLookupMembers()
	{
		synchronized (getSyncObject()) {
			return m_valueExp.isLookupMembers();
		}
	}
}
