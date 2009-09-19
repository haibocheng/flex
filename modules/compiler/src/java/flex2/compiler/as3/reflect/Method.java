////////////////////////////////////////////////////////////////////////////////
//
//  ADOBE SYSTEMS INCORPORATED
//  Copyright 2005-2007 Adobe Systems Incorporated
//  All Rights Reserved.
//
//  NOTICE: Adobe permits you to use, modify, and distribute this file
//  in accordance with the terms of the license agreement accompanying it.
//
////////////////////////////////////////////////////////////////////////////////

package flex2.compiler.as3.reflect;

import flex2.compiler.SymbolTable;
import flex2.compiler.abc.AbcClass;
import flex2.compiler.abc.MetaData;
import flex2.compiler.util.QName;
import macromedia.asc.parser.FunctionDefinitionNode;
import macromedia.asc.parser.MemberExpressionNode;
import macromedia.asc.parser.ParameterListNode;
import macromedia.asc.semantics.ParameterizedName;
import macromedia.asc.semantics.ReferenceValue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Clement Wong
 */
public final class Method implements flex2.compiler.abc.Method
{
    Method(FunctionDefinitionNode function, As3Class declaringClass)
    {
        extractFunctionTypeInfo(function);

        if (function.attrs != null)
        {
            attributes = new Attributes(function.attrs);
        }

        qName = new QName(TypeTable.getNamespace(attributes), function.fexpr.identifier.name);

        ParameterListNode params = function.fexpr.signature.parameter;
        int size = params != null ? params.items.size() : 0;
        if (size != 0)
        {
            parameterNames = new String[size];
            parameterTypeNames = new String[size];
            parameterElementTypeNames = new String[size];
            parameterHasDefault = new boolean[size];
            TypeTable.createParameters(params, parameterNames, parameterTypeNames,
                                       parameterElementTypeNames, parameterHasDefault);
        }

        /*
        size = function.fexpr.fexprs.size();
        if (size != 0)
        {
            methods = new QNameMap(); // QNameMap<QName, Method>
            getters = new QNameMap(); // QNameMap<QName, Method>
            setters = new QNameMap(); // QNameMap<QName, Method>
            TypeTable.createMethods(function.fexpr.fexprs, methods, getters, setters, internalNamespace, metadata, declaringClass);
        }
        */

        this.metadata = TypeTable.createMetaData(function);

        this.declaringClass = declaringClass;
    }

    private Attributes attributes;
    private QName qName;
    private String returnTypeName = SymbolTable.NOTYPE;
    private String returnElementTypeName;
    private String[] parameterNames;
    private String[] parameterTypeNames;
    private String[] parameterElementTypeNames;
    private boolean[] parameterHasDefault;
    private List<MetaData> metadata;
    private As3Class declaringClass;

    public flex2.compiler.abc.Attributes getAttributes()
    {
        return attributes;
    }

    public QName getQName()
    {
        return qName;
    }

    public String getReturnTypeName()
    {
        return returnTypeName;
    }

    public String getReturnElementTypeName()
    {
        return returnElementTypeName;
    }

    public String[] getParameterNames()
    {
        return parameterNames;
    }

    public String[] getParameterTypeNames()
    {
        return parameterTypeNames;
    }

    public String[] getParameterElementTypeNames()
    {
        return parameterElementTypeNames;
    }

    public boolean[] getParameterHasDefault()
    {
        return parameterHasDefault;
    }

    public List<flex2.compiler.abc.MetaData> getMetaData()
    {
        return metadata != null ? metadata : Collections.<flex2.compiler.abc.MetaData>emptyList();
    }

    public List<flex2.compiler.abc.MetaData> getMetaData(String id)
    {
        if (metadata == null)
        {
            return null;
        }
        else
        {
            List<MetaData> list = null;
            for (int i = 0, length = metadata.size(); i < length; i++)
            {
                if (id.equals((metadata.get(i)).getID()))
                {
                    if (list == null)
                    {
                        list = new ArrayList<MetaData>();
                    }
                    list.add(metadata.get(i));
                }
            }
            return list;
        }
    }

    public AbcClass getDeclaringClass()
    {
        return declaringClass;
    }

    /**
     * Initializes returnTypeName and in the case of Vector,
     * initializes returnElementTypeName.
     */
    private void extractFunctionTypeInfo(FunctionDefinitionNode function)
    {
        if (function.fexpr.signature.type != null)
        {
            macromedia.asc.semantics.QName name = function.fexpr.signature.type.getName();

            returnTypeName = TypeTable.convertName(name.toString());

            if (name instanceof ParameterizedName)
            {
                ParameterizedName parameterizedName = (ParameterizedName) name;
                returnElementTypeName = TypeTable.convertName(parameterizedName.type_params.first().toString());
            }
        }
        else if (function.fexpr.signature.result != null)
        {
            ReferenceValue typeref = ((MemberExpressionNode) function.fexpr.signature.result).ref;

            if (typeref != null)
            {
                returnTypeName = TypeTable.convertName(typeref);

                if (typeref.type_params != null)
                {
                    returnElementTypeName = TypeTable.convertName(typeref.type_params.first());
                }
            }
        }
    }
}
