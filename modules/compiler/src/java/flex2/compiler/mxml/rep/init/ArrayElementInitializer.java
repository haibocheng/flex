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

import flex2.compiler.mxml.lang.StandardDefs;
import flex2.compiler.mxml.reflect.Type;
import macromedia.asc.parser.ArgumentListNode;
import macromedia.asc.parser.ExpressionStatementNode;
import macromedia.asc.parser.ListNode;
import macromedia.asc.parser.LiteralNumberNode;
import macromedia.asc.parser.MemberExpressionNode;
import macromedia.asc.parser.Node;
import macromedia.asc.parser.NodeFactory;
import macromedia.asc.parser.SetExpressionNode;
import macromedia.asc.parser.StatementListNode;
import macromedia.asc.parser.Tokens;

/**
 *
 */
public class ArrayElementInitializer extends ValueInitializer
{
	final Type type;
	final int index;

	public ArrayElementInitializer(Type type, int index, Object value, int line, StandardDefs defs)
	{
		super(value, line, defs);
		this.type = type;
		this.index = index;
	}

	public Type getLValueType()
	{
		return type;
	}

	public String getAssignExpr(String lvalueBase)
	{
		return lvalueBase + "[" + index + "] = " + getValueExpr();
	}

	public StatementListNode generateAssignExpr(NodeFactory nodeFactory,
                                                StatementListNode statementList,
                                                Node lvalueBase)
    {
        LiteralNumberNode literalNumber = nodeFactory.literalNumber(index);
        ArgumentListNode expr = nodeFactory.argumentList(null, literalNumber);
        ArgumentListNode argumentList = nodeFactory.argumentList(null, generateValueExpr(nodeFactory));
        SetExpressionNode setExpression = nodeFactory.setExpression(expr, argumentList, false);
		setExpression.setMode(Tokens.LEFTBRACKET_TOKEN);
        MemberExpressionNode memberExpression = nodeFactory.memberExpression(lvalueBase, setExpression);
        ListNode list = nodeFactory.list(null, memberExpression);
        ExpressionStatementNode expressionStatement = nodeFactory.expressionStatement(list);
        return nodeFactory.statementList(statementList, expressionStatement);
    }
}
