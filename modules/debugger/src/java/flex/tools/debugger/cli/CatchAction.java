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

package flex.tools.debugger.cli;

/**
 * 
 * An object that relates a CLI debugger catchpoint with the
 * actual Catch obtained from the Session
 * 
 * @author Mike Morearty
 */
public class CatchAction
{
	private final int m_id;
	private final String m_typeToCatch;

	/**
	 * @param typeToCatch
	 *            the type, e.g. "ReferenceError" or "com.example.MyError". If
	 *            typeToCatch is <code>null</code>, that means to halt on any
	 *            exception.
	 */
	public CatchAction(String typeToCatch)
	{
		m_typeToCatch = typeToCatch;
		m_id = BreakIdentifier.next();
	}

	public int getId()
	{
		return m_id;
	}

	/**
	 * Returns the type being caught, or <code>null</code> to catch all
	 * exceptions.
	 */
	public String getTypeToCatch()
	{
		return m_typeToCatch;
	}
}
