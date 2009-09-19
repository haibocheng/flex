// //////////////////////////////////////////////////////////////////////////////
//
// ADOBE SYSTEMS INCORPORATED
// Copyright 2005-2008 Adobe Systems Incorporated
// All Rights Reserved.
//
// NOTICE: Adobe permits you to use, modify, and distribute this file
// in accordance with the terms of the license agreement accompanying it.
//
// //////////////////////////////////////////////////////////////////////////////

package flex2.compiler.mxml.builder;

import flex2.compiler.CompilationUnit;
import flex2.compiler.mxml.MxmlConfiguration;
import flex2.compiler.mxml.dom.InlineComponentNode;
import flex2.compiler.mxml.lang.StandardDefs;
import flex2.compiler.mxml.reflect.TypeTable;
import flex2.compiler.mxml.rep.Model;
import flex2.compiler.mxml.rep.MxmlDocument;
import flex2.compiler.util.MxmlCommentUtil;
import flex2.compiler.util.NameFormatter;
import flex2.compiler.util.QName;

/**
 * @author Paul Reilly
 */
class InlineComponentBuilder extends AbstractBuilder
{
    InlineComponentBuilder(CompilationUnit unit, TypeTable typeTable,
            MxmlConfiguration mxmlConfiguration, MxmlDocument document,
            boolean topLevel)
    {
        super(unit, typeTable, mxmlConfiguration, document);
        this.topLevel = topLevel;
    }

    protected boolean topLevel;
    Model rvalue;

    public void analyze(InlineComponentNode node)
    {
        QName classQName = node.getClassQName();

        rvalue = factoryFromClass(NameFormatter.toDot(classQName), node.beginLine);

        String id = (String)getLanguageAttributeValue(node, StandardDefs.PROP_ID);
        if (id != null || topLevel)
        {
            // if node has a comment then transfer it to the model.
            if (node.comment != null)
            {
                // if generate ast is false, lets not scan the tokens here
                // because they will be scanned later in asc scanner.
                // we will go the velocity template route
                if (!mxmlConfiguration.getGenerateAbstractSyntaxTree())
                {
                    rvalue.comment = node.comment;
                }
                else
                {
                    rvalue.comment = MxmlCommentUtil.commentToXmlComment(node.comment);
                }
            }

            registerModel(id, rvalue, topLevel);
        }
    }

    public Model getRValue()
    {
        return rvalue;
    }
}
