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

package flash.tools.debugger.expression;

/**
 * An exception raised while evaluating an expression.  This is a bit
 * of a hack -- we need this to extend <code>RuntimeException</code>
 * because the functions in the <code>Evaluator</code> interface don't
 * throw anything, but our <code>DebuggerEvaluator</code> has many
 * places where it needs to bail out.
 * 
 * @author Mike Morearty
 */
public class ExpressionEvaluatorException extends RuntimeException {
	private static final long serialVersionUID = -7005526599250035578L;

	public ExpressionEvaluatorException(String message) {
		super(message);
	}

	public ExpressionEvaluatorException(Throwable cause) {
		super(cause);
	}
}
