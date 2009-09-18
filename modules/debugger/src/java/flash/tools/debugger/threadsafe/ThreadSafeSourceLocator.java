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

import java.io.InputStream;

import flash.tools.debugger.SourceLocator;

/**
 * @author Mike Morearty
 */
public class ThreadSafeSourceLocator extends ThreadSafeDebuggerObject implements SourceLocator
{
	private SourceLocator fSourceLocator;
	
	/**
	 * @param syncObj
	 */
	public ThreadSafeSourceLocator(Object syncObj, SourceLocator sourceLocator)
	{
		super(syncObj);
		fSourceLocator = sourceLocator;
	}

	/**
	 * Wraps a SourceLocator inside a ThreadSafeSourceLocator.  If the passed-in SourceLocator
	 * is null, then this function returns null.
	 */
	public static ThreadSafeSourceLocator wrap(Object syncObj, SourceLocator sourceLocator) {
		if (sourceLocator != null)
			return new ThreadSafeSourceLocator(syncObj, sourceLocator);
		else
			return null;
	}

	/* (non-Javadoc)
	 * @see flash.tools.debugger.SourceLocator#locateSource(java.lang.String, java.lang.String, java.lang.String)
	 */
	public InputStream locateSource(String arg0, String arg1, String arg2)
	{
		synchronized (getSyncObject()) {
			return fSourceLocator.locateSource(arg0, arg1, arg2);
		}
	}

	/* (non-Javadoc)
	 * @see flash.tools.debugger.SourceLocator#getChangeCount()
	 */
	public int getChangeCount()
	{
		synchronized (getSyncObject()) {
			return fSourceLocator.getChangeCount();
		}
	}
}
