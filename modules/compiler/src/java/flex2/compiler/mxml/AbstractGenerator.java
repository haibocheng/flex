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

package flex2.compiler.mxml;

import flex2.compiler.as3.AbstractSyntaxTreeUtil;
import flex2.compiler.as3.reflect.NodeMagic;
import flex2.compiler.mxml.lang.StandardDefs;
import flex2.compiler.mxml.rep.Script;
import flex2.compiler.util.CompilerMessage;
import flex2.compiler.util.ThreadLocalToolkit;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import macromedia.asc.parser.*;
import macromedia.asc.util.Context;

/**
 * @author Paul Reilly
 */
public abstract class AbstractGenerator
{
    // intern all identifier constants
    protected static final String BINDABLE = "Bindable".intern();
    protected static final String MX_INTERNAL = StandardDefs.NAMESPACE_MX_INTERNAL_LOCALNAME;

    protected final StandardDefs standardDefs;
    protected Context context;
    protected NodeFactory nodeFactory;
    protected HashSet<String> configNamespaces;
    protected ProgramNode programNode;
    protected boolean generateDocComments;

    protected AbstractGenerator(StandardDefs defs)
    {
        this.standardDefs = defs;
    }
    
    protected StatementListNode generateMetaData(StatementListNode programStatementList,
                                                 List<Script> scripts)
    {
        StatementListNode result = programStatementList;
        Iterator<Script> scriptIterator = scripts.iterator();

        while (scriptIterator.hasNext())
        {
            Script script = scriptIterator.next();
            String text = script.getText();
            int xmlLineNumber = script.getXmlLineNumber();
            List<Node> list = AbstractSyntaxTreeUtil.parse(context, configNamespaces, text,
                                                           xmlLineNumber, generateDocComments);
            Iterator<Node> nodeIterator = list.iterator();

            while (nodeIterator.hasNext())
            {
                Node node = nodeIterator.next();

                if (node instanceof MetaDataNode)
                {
                    result = nodeFactory.statementList(result, node);
                }
                else
                {
                    CompilerMessage m = new NodeMagic.OnlyMetadataIsAllowed();
                    m.setPath(getPath());
                    m.setLine(context.input.getLnNum(node.pos()) + script.getXmlLineNumber() - 1);
                    ThreadLocalToolkit.log(m);                    
                }
            }
        }

        return result;
    }

    protected AttributeListNode generateMxInternalAttribute()
    {
        MemberExpressionNode mxInternalGetterSelector =
            AbstractSyntaxTreeUtil.generateResolvedGetterSelector(nodeFactory, standardDefs.getCorePackage(), MX_INTERNAL);
        ListNode list = nodeFactory.list(null, mxInternalGetterSelector);
        return nodeFactory.attributeList(list, null);
    }

    protected StatementListNode generateScripts(StatementListNode statementList, List<Script> scripts)
    {
        StatementListNode result = statementList;
        Iterator<Script> iterator = scripts.iterator();

        while (iterator.hasNext())
        {
            Script script = iterator.next();
            String text = script.getText();
            int xmlLineNumber = script.getXmlLineNumber();
            List<Node> list = AbstractSyntaxTreeUtil.parse(context, configNamespaces, text,
                                                           xmlLineNumber, generateDocComments);
            // Don't use NodeFactory.statementList() here, because it
            // handles IncludeDirectives by inlining them and if we
            // use it here, they will get inlined a second time.
            result.items.addAll(list);
        }

        return result;
    }

    Context getContext()
    {
        return context;
    }

    abstract String getPath();

    public Object getSyntaxTree()
    {
        return programNode;
    }
}
