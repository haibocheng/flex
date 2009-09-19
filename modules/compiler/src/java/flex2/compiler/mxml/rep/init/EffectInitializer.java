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
import flex2.compiler.mxml.reflect.Effect;
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

/**
 * Note: need member for type because of classref/instance modality. See processing in Model and Builder
 */
public class EffectInitializer extends NamedInitializer
{
    protected final Effect effect;
    protected final Type type;

    public EffectInitializer(Effect effect, Object value, Type type, int line, StandardDefs defs)
    {
        super(value, line, defs);
        this.effect = effect;
        this.type = type;
    }

    public String getName()
    {
        return effect.getName();
    }

    public Type getLValueType()
    {
        return type;
    }

    public String getEventName()
    {
        return effect.getEvent();
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
