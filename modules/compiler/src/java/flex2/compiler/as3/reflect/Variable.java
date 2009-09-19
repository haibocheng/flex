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
import macromedia.asc.parser.AttributeListNode;
import macromedia.asc.parser.Tokens;
import macromedia.asc.parser.VariableBindingNode;
import macromedia.asc.semantics.ParameterizedName;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Clement Wong
 */
public final class Variable implements flex2.compiler.abc.Variable
{
	Variable(AttributeListNode attrs, VariableBindingNode binding, List<MetaData> metadata,
			 As3Class declaringClass)
	{
		extractVariableTypeInfo(binding);

		if (attrs != null)
		{
			attributes = new Attributes(attrs);

            // preilly: The AttributeListNode's hasConst is never
            // used, so we do the following to check for const's.
            if (binding.kind == Tokens.CONST_TOKEN)
            {
                attributes.setHasConst();
            }
		}

		qName = new QName(TypeTable.getNamespace(attributes), binding.variable.identifier.name);

		this.metadata = metadata;

		this.declaringClass = declaringClass;
	}

	private Attributes attributes;
	private QName qName;
	private String typeName;
    private String elementTypeName;
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

	public String getTypeName()
	{
		return typeName;
	}

    public String getElementTypeName()
    {
        return elementTypeName;
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
     * Initializes typeName and in the case of Vector, initializes
     * elementTypeName.
     */
	private void extractVariableTypeInfo(VariableBindingNode binding)
	{
		if (binding.variable.identifier.ref != null)
		{
            macromedia.asc.semantics.QName name = binding.variable.identifier.ref.slot.getType().getName();
			typeName = TypeTable.convertName(name.toString());

            if (name instanceof ParameterizedName)
            {
                ParameterizedName parameterizedName = (ParameterizedName) name;
                elementTypeName = TypeTable.convertName(parameterizedName.type_params.first().toString());
            }
		}
		else if (binding.typeref != null)
		{
			typeName = TypeTable.convertName(binding.typeref);

            if (binding.typeref.type_params != null)
            {
                elementTypeName = TypeTable.convertName(binding.typeref.type_params.first());
            }
		}
		else
		{
			typeName = SymbolTable.NOTYPE;
		}
	}
}
