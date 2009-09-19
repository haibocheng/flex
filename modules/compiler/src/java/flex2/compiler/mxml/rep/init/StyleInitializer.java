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

import flex2.compiler.mxml.gen.TextGen;
import flex2.compiler.mxml.lang.StandardDefs;
import flex2.compiler.mxml.reflect.Style;
import flex2.compiler.mxml.reflect.Type;
import macromedia.asc.parser.ArgumentListNode;
import macromedia.asc.parser.CallExpressionNode;
import macromedia.asc.parser.ExpressionStatementNode;
import macromedia.asc.parser.IdentifierNode;
import macromedia.asc.parser.ListNode;
import macromedia.asc.parser.LiteralStringNode;
import macromedia.asc.parser.MemberExpressionNode;
import macromedia.asc.parser.Node;
import macromedia.asc.parser.NodeFactory;
import macromedia.asc.parser.StatementListNode;

public class StyleInitializer extends NamedInitializer
{
	protected final Style style;

	public StyleInitializer(Style style, Object value, int line, StandardDefs standardDefs)
	{
		super(value, line, standardDefs);
		this.style = style;
	}

	public Style getStyle()
	{
		return style;
	}

	public Type getLValueType()
	{
		return style.getType();
	}

	public String getName()
	{
		return style.getName();
	}

	public String getAssignExpr(String lvalueBase)
	{
		return lvalueBase + ".setStyle(" + TextGen.quoteWord(getName()) + ", " + getValueExpr() + ")";
	}

    // intern all identifier constants
    private static final String SET_STYLE = "setStyle".intern();

	public StatementListNode generateAssignExpr(NodeFactory nodeFactory,
                                                StatementListNode statementList,
                                                Node lvalueBase)
    {
        IdentifierNode identifier = nodeFactory.identifier(SET_STYLE, false);
        LiteralStringNode literalString = nodeFactory.literalString(getName());
        ArgumentListNode argumentList = nodeFactory.argumentList(null, literalString);
        argumentList = nodeFactory.argumentList(argumentList, generateValueExpr(nodeFactory));
        CallExpressionNode callExpression =
            (CallExpressionNode) nodeFactory.callExpression(identifier, argumentList);
        callExpression.setRValue(false);
        MemberExpressionNode memberExpression = nodeFactory.memberExpression(lvalueBase, callExpression);
        ListNode list = nodeFactory.list(null, memberExpression);
        ExpressionStatementNode expressionStatement = nodeFactory.expressionStatement(list);
        return nodeFactory.statementList(statementList, expressionStatement);
    }
}
