////////////////////////////////////////////////////////////////////////////////
//
//  ADOBE SYSTEMS INCORPORATED
//  Copyright 2005-2006 Adobe Systems Incorporated
//  All Rights Reserved.
//
//  NOTICE: Adobe permits you to use, modify, and distribute this file
//  in accordance with the terms of the license agreement accompanying it.
//
////////////////////////////////////////////////////////////////////////////////

package flex2.compiler.mxml.rep.init;

import macromedia.asc.parser.ExpressionStatementNode;
import macromedia.asc.parser.Node;
import macromedia.asc.parser.NodeFactory;
import macromedia.asc.parser.StatementListNode;
import flex2.compiler.as3.AbstractSyntaxTreeUtil;
import flex2.compiler.mxml.gen.TextGen;
import flex2.compiler.mxml.lang.StandardDefs;
import flex2.compiler.mxml.lang.TextParser;

/**
 * initializer for a named lvalue - e.g. property or style, but not array element or visual child
 */
public abstract class NamedInitializer extends ValueInitializer
{
	NamedInitializer(Object value, int line, StandardDefs defs)
	{
		super(value, line, defs);
	}

	/**
	 *
	 */
	public abstract String getName();

	/**
	 *
	 */
	public String getAssignExpr(String lvalueBase)
	{
		String name = getName();
		
		String lvalue = TextParser.isValidIdentifier(name) ?
				lvalueBase + '.' + name :
				lvalueBase + "[" + TextGen.quoteWord(name) + "]";

		return lvalue + " = " + getValueExpr();
	}

	public StatementListNode generateAssignExpr(NodeFactory nodeFactory,
                                                StatementListNode statementList,
                                                Node lvalueBase)
    {
		String name = getName();

		if (TextParser.isValidIdentifier(name))
        {
            ExpressionStatementNode expressionStatement =
                AbstractSyntaxTreeUtil.generateAssignment(nodeFactory, lvalueBase, name,
                                                          generateValueExpr(nodeFactory));
            return nodeFactory.statementList(statementList, expressionStatement);
        }
        else
        {
            assert false;
            return null;
        }
    }
}
