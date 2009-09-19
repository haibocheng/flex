////////////////////////////////////////////////////////////////////////////////
//
//  ADOBE SYSTEMS INCORPORATED
//  Copyright 2005 Adobe Systems Incorporated
//  All Rights Reserved.
//
//  NOTICE: Adobe permits you to use, modify, and distribute this file
//  in accordance with the terms of the license agreement accompanying it.
//
////////////////////////////////////////////////////////////////////////////////

package flex2.compiler.mxml.rep.init;

import flex2.compiler.mxml.reflect.Type;
import macromedia.asc.parser.ExpressionStatementNode;
import macromedia.asc.parser.Node;
import macromedia.asc.parser.NodeFactory;
import macromedia.asc.parser.StatementListNode;
import macromedia.asc.util.Context;

import java.util.HashSet;
import java.util.Iterator;

/**
 *
 */
public interface Initializer
{
	/**
	 *
	 */
	int getLineRef();

	/**
	 *
	 */
	boolean isBinding();

	/**
	 *
	 */
	Type getLValueType();

	/**
	 *
	 */
	String getValueExpr();

	/**
	 *
	 */
	Node generateValueExpr(NodeFactory nodeFactory);

	/**
	 *
	 */
	String getAssignExpr(String lvalueBase);

	/**
	 *
	 */
	StatementListNode generateAssignExpr(NodeFactory nodeFactory,
                                         StatementListNode statementList,
                                         Node lvalueBase);

	/**
	 *
	 */
	boolean hasDefinition();
	
	/**
	 *
	 */
	boolean isStateSpecific();

	/**
	 *
	 */
	Iterator getDefinitionsIterator();

	/**
	 *
	 */
    StatementListNode generateDefinitions(Context context, HashSet<String> configNamespaces,
                                          StatementListNode statementList, boolean generateDocComments);
}
