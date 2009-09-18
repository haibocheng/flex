////////////////////////////////////////////////////////////////////////////////
//
//  ADOBE SYSTEMS INCORPORATED
//  Copyright 2003-2006 Adobe Systems Incorporated
//  All Rights Reserved.
//
//  NOTICE: Adobe permits you to use, modify, and distribute this file
//  in accordance with the terms of the license agreement accompanying it.
//
////////////////////////////////////////////////////////////////////////////////

package flash.tools.debugger.events;

import flash.tools.debugger.Value;

/**
 * Signals that a user exception has been thrown.
 */
public class ExceptionFault extends FaultEvent
{
	public final static String name = "exception"; //$NON-NLS-1$
	private final boolean m_willExceptionBeCaught;
	private final Value m_thrownValue;

	public ExceptionFault(String message, boolean willExceptionBeCaught, Value thrownValue)
	{
		super(message);
		m_willExceptionBeCaught = willExceptionBeCaught;
		m_thrownValue = thrownValue;
	}

	public String name()
	{
		return name;
	}

	/**
	 * Returns true if there is a "catch" block that is going to catch
	 * this exception, false if not.
	 */
	public boolean willExceptionBeCaught()
	{
		return m_willExceptionBeCaught;
	}

	/**
	 * The value that was thrown; may be null, because there are times when we
	 * cannot determine the value that was thrown.
	 */
	public Value getThrownValue()
	{
		return m_thrownValue;
	}
}
