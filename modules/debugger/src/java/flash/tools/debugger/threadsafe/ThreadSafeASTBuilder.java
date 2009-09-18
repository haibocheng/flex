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
import java.io.Reader;
import java.text.ParseException;

import flash.tools.debugger.expression.IASTBuilder;
import flash.tools.debugger.expression.ValueExp;

/**
 * @author Mike Morearty
 */
public class ThreadSafeASTBuilder extends ThreadSafeDebuggerObject implements IASTBuilder
{
	private final IASTBuilder m_astBuilder;

	/**
	 * @param syncObj
	 */
	public ThreadSafeASTBuilder(Object syncObj, IASTBuilder astBuilder)
	{
		super(syncObj);
		m_astBuilder = astBuilder;
	}

	/**
	 * Wraps an IASTBuilder inside a ThreadSafeASTBuilder. If the passed-in
	 * IASTBuilder is null, then this function returns null.
	 */
	public static ThreadSafeASTBuilder wrap(Object syncObj, IASTBuilder astBuilder) {
		if (astBuilder != null)
			return new ThreadSafeASTBuilder(syncObj, astBuilder);
		else
			return null;
	}

	/*
	 * @see flash.tools.debugger.expression.IASTBuilder#parse(java.io.Reader)
	 */
	public ValueExp parse(Reader in) throws IOException, ParseException
	{
		synchronized (getSyncObject()) {
			return ThreadSafeValueExp.wrap(getSyncObject(), m_astBuilder.parse(in));
		}
	}

}
