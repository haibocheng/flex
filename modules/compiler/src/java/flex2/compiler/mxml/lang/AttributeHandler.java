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

package flex2.compiler.mxml.lang;

import flex2.compiler.mxml.reflect.Type;
import flex2.compiler.mxml.dom.Node;
import flex2.compiler.util.QName;

/**
 * Attribute-specific wrapper around DeclarationHandler
 */
public abstract class AttributeHandler extends DeclarationHandler
{
	protected Type type;
	protected String text;
	protected int line;

	/**
	 *
	 */
	public void invoke(Node node, Type type, QName qname)
	{
		//	String msg = "AttributeHandler[" + node.image + "/" + node.beginLine + ":" + type.getName() + "].invoke('" + qname + "'): ";
        this.type = type;
		this.text = (String)node.getAttributeValue(qname);
		this.line = node.getLineNumber(qname);

		String namespace = qname.getNamespace();
		String localPart = qname.getLocalPart();

		if (isSpecial(namespace, localPart))
		{
			//	System.out.println(msg + "special()");
			special(type, namespace, localPart);
		}
		else if (namespace.length() != 0)
		{
            //  System.out.println(msg + "qualifiedAttribute()");
		    qualifiedAttribute(node, type, namespace, localPart);
		}
		else
		{
			//	System.out.println(msg + "super.invoke()");
			invoke(type, namespace, localPart);
		}
	}

	/**
     * From Flex 4, tools may annotate elements with qualified attributes
     * in their other namespaces and should be considered private and
     * thus ignored.
     * 
     * @param namespace The namespace of the attribute.
     * @param localPart The name of the attribute.
     */
	protected abstract void qualifiedAttribute(Node node, Type type, String namespace, String localPart);

	/**
	 *
	 */
	protected abstract boolean isSpecial(String namespace, String localPart);

	/**
	 *
	 */
	protected abstract void special(Type type, String namespace, String localPart);

    /**
     * attribute fails to resolve due to unknown namespace 
     */
    protected abstract void unknownNamespace(String namespace, String localPart);
}
