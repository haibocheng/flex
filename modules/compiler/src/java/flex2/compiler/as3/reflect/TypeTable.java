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
import flex2.compiler.util.QName;
import flex2.compiler.util.QNameList;
import macromedia.asc.parser.*;
import macromedia.asc.semantics.ObjectValue;
import macromedia.asc.semantics.ParameterizedName;
import macromedia.asc.semantics.ReferenceValue;
import macromedia.asc.semantics.TypeInfo;
import macromedia.asc.util.ObjectList;

import java.util.*;

/**
 * @author Clement Wong
 */
public class TypeTable
{
	public TypeTable(SymbolTable symbolTable)
	{
		this.symbolTable = symbolTable;
	}

	private SymbolTable symbolTable;

	public AbcClass getClass(String className)
	{
		return symbolTable.getClass(className);
	}

	// C: TypeTable should not expose SymbolTable, if possible.
    public SymbolTable getSymbolTable()
    {
        return symbolTable;
    }

	public static String convertName(ReferenceValue typeref)
	{
		ObjectValue ns = typeref.namespaces.first();
		if( ns != null && ns.name.length() > 0 )
		{
			StringBuilder value = new StringBuilder(ns.name.length() + 1 + typeref.name.length());
		    value.append(ns.name).append(':').append(typeref.name);
			return value.toString();
		}
		else
		{
			return typeref.name;
		}
	}

	public static String convertName(String name)
	{
		// C: Warning: This doesn't handle the asc debug name full syntax...

		/*
		int dollarSign = -1, colon = -1;
		String pkg_name = null, def_name = null;

		if ((colon = name.indexOf(':', dollarSign + 1)) > 0)
		{
			def_name = name.substring(colon + 1);

			if (dollarSign == -1)
			{
				pkg_name = name.substring(0, colon);
			}
		}
		else
		{
			def_name = name;
		}

		return (pkg_name == null) ? def_name : pkg_name + ":" + def_name;
		*/

		return name.intern();
	}

	public final Map<String, AbcClass> createClasses(ObjectList clsdefs, QNameList toplevelDefinitions)
	{
		Map<String, AbcClass> classes = new HashMap<String, AbcClass>();
		for (int i = 0, size = clsdefs.size(); i < size; i++)
		{
			ClassDefinitionNode clsdef = (ClassDefinitionNode) clsdefs.get(i);
			macromedia.asc.semantics.QName qName = clsdef.cframe.builder.classname;

			if (toplevelDefinitions.contains(qName.ns.name, qName.name))
			{
				createClass(clsdef, classes);
			}
		}
		return classes;
	}

	final void createClass(ClassDefinitionNode clsdef, Map<String, AbcClass> classes)
	{
		AbcClass cls = new As3Class(clsdef, this);
		classes.put(cls.getName(), cls);
	}

	static void createMethod(FunctionDefinitionNode f, Map<QName, flex2.compiler.abc.Method> methods, As3Class declaringClass)
	{
		Method method = new Method(f, declaringClass);

        if (methods.put(method.getQName(), method) != null)
		{
			// ThreadLocalToolkit.logWarning("duplicate method... " + method.getName() + " in " + declaringClass.getName());
		}
	}

	static void createVariable(VariableDefinitionNode var, Map<QName, flex2.compiler.abc.Variable> variables, As3Class declaringClass)
	{
		List<flex2.compiler.abc.MetaData> meta = createMetaData(var);

		// for (Node n : var.list.items)
		for (int k = 0, len = var.list.items.size(); k < len; k++)
		{
			VariableBindingNode binding = (VariableBindingNode) var.list.items.get(k);
			Variable variable = new Variable(var.attrs, binding, meta, declaringClass);
            variables.put(variable.getQName(), variable);
		}
	}

    static void createParameters(ParameterListNode params, String[] names, String[] typeNames,
                                 String[] elementTypeNames, boolean[] hasDefault)
    {
        for (int i = 0, size = params.size(); i < size; i++)
        {
            ParameterNode item = params.items.get(i);

            // name
            names[i] = item.ref != null ? item.ref.name : "";

            // has default
            hasDefault[i] = item.init != null;

            // type name - could get from item.typeref?
            if (params.types != null && i < params.types.size())
            {
                TypeInfo paramType = params.types.get(i);
                macromedia.asc.semantics.QName name = paramType.getName();
                typeNames[i] = convertName(name.toString());

                if (name instanceof ParameterizedName)
                {
                    ParameterizedName parameterizedName = (ParameterizedName) name;
                    elementTypeNames[i] = TypeTable.convertName(parameterizedName.type_params.first().toString());
                }
            }
            else
            {
                ParameterNode param = params.items.get(i);
                if (param != null && param.typeref != null)
                {
                    typeNames[i] = convertName(param.typeref);

                    if (param.typeref.type_params != null)
                    {
                        elementTypeNames[i] = TypeTable.convertName(param.typeref.type_params.first());
                    }
                }
                else if (param != null)
                {
                    typeNames[i] = SymbolTable.NOTYPE;
                }
                else
                {
                    assert false : "Expected ParameterNode...";
                }
            }
        }
    }

	static List<flex2.compiler.abc.MetaData> createMetaData(DefinitionNode def)
	{
		List<flex2.compiler.abc.MetaData> list = null;

		for (int i = 0, size = def.metaData != null ? def.metaData.items.size() : 0; i < size; i++)
		{
			MetaDataNode n = (MetaDataNode) def.metaData.items.get(i);
			if (list == null)
			{
				list = new ArrayList<flex2.compiler.abc.MetaData>();
			}
			list.add(new MetaData(n));
		}

		return list;
	}

    static String getNamespace(flex2.compiler.abc.Attributes attributes)
    {
        String result = QName.DEFAULT_NAMESPACE;

        if (attributes != null)
        {
            // For "internal", ASC uses the package name, but we want
            // to be able to lookup using "internal".
            if (attributes.hasInternal())
            {
                result = NodeMagic.INTERNAL;
            }
            else
            {
                Iterator iterator = attributes.getNamespaces();

                if ((iterator != null) && iterator.hasNext())
                {
                    result = (String) iterator.next();
                }
            }
        }

        return result;
    }
}
