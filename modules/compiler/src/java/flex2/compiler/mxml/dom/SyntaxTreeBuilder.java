////////////////////////////////////////////////////////////////////////////////
//
//  ADOBE SYSTEMS INCORPORATED
//  Copyright 2004-2007 Adobe Systems Incorporated
//  All Rights Reserved.
//
//  NOTICE: Adobe permits you to use, modify, and distribute this file
//  in accordance with the terms of the license agreement accompanying it.
//
////////////////////////////////////////////////////////////////////////////////

package flex2.compiler.mxml.dom;

import flex2.compiler.mxml.MxmlVisitor;
import flex2.compiler.mxml.Token;
import flex2.compiler.mxml.dom.DefinitionNode;
import flex2.compiler.mxml.dom.LibraryNode;

import java.util.List;

/**
 * @author Clement Wong
 */
public class SyntaxTreeBuilder implements MxmlVisitor
{
	public void parseApplication(Token t, List<Token> components)
	{
		Node app = (Node) t;
		app.addChildren(components);
	}

	public void parseComponent(Token t, List<Token> components)
	{
		Node comp = (Node) t;
		comp.addChildren(components);
	}

	public void parseStyle(Token t, Token text)
	{
		StyleNode style = (StyleNode) t;

		if (text != null)
		{
			style.addChild(text);
		}
	}

	public void parseScript(Token t, Token text)
	{
		ScriptNode script = (ScriptNode) t;

		if (text != null)
		{
			script.addChild(text);
		}
	}

	public void parseMetaData(Token t, Token text)
	{
		MetaDataNode metadata = (MetaDataNode) t;

		if (text != null)
		{
			metadata.addChild(text);
		}
	}

	public void parseModel(Token t, List<Token> objects)
	{
		ModelNode model = (ModelNode) t;

		if (objects != null)
		{
			model.addChildren(objects);
		}
	}

	public void parseXML(Token t, List<Token> objects)
	{
		XMLNode xml = (XMLNode) t;

		if (objects != null)
		{
			xml.addChildren(objects);
		}
	}

    public void parseXMLList(Token t, List<Token> objects)
    {
        XMLListNode xmlList = (XMLListNode) t;

        if (objects != null)
        {
            xmlList.addChildren(objects);
        }
    }

	public void parseArray(Token t, List<Token> elements)
	{
		ArrayNode a = (ArrayNode) t;

		if (elements != null)
		{
			a.addChildren(elements);
		}
	}

	public void parseVector(Token t, List<Token> elements)
	{
		VectorNode a = (VectorNode) t;

		if (elements != null)
		{
			a.addChildren(elements);
		}
	}

	public void parseBinding(Token t)
	{
	}

	public void parseAnonymousObject(Token t, List<Token> objects)
	{
		Node obj = (Node) t;

		if (objects != null)
		{
			obj.addChildren(objects);
		}
	}

	public void parseWebService(Token t, List<Token> children)
	{
		WebServiceNode webService = (WebServiceNode) t;

		if (children != null)
		{
			webService.addChildren(children);
		}
	}

	public void parseHTTPService(Token t, List<Token> children)
	{
		HTTPServiceNode httpService = (HTTPServiceNode) t;

		if (children != null)
		{
			httpService.addChildren(children);
		}
	}

	public void parseRemoteObject(Token t, List<Token> children)
	{
		RemoteObjectNode remoteObject = (RemoteObjectNode) t;

		if (children != null)
		{
			remoteObject.addChildren(children);
		}
	}

	public void parseOperation(Token t, List<Token> children)
	{
		OperationNode operation = (OperationNode) t;

		if (children != null)
		{
			operation.addChildren(children);
		}
	}

	public void parseRequest(Token t, List<Token> children)
	{
		Node request = (Node) t;

		if (children != null)
		{
			request.addChildren(children);
		}
	}

	public void parseMethod(Token t, List<Token> children)
	{
		MethodNode method = (MethodNode) t;

		if (children != null)
		{
			method.addChildren(children);
		}
	}

	public void parseArguments(Token t, List<Token> children)
	{
		ArgumentsNode arguments = (ArgumentsNode) t;

		if (children != null)
		{
			arguments.addChildren(children);
		}
	}

	public void parseString(Token t, Token data)
	{
		StringNode str = (StringNode) t;

		if (data != null)
		{
			str.addChild(data);
		}
	}

	public void parseNumber(Token t, Token data)
	{
		NumberNode num = (NumberNode) t;

		if (data != null)
		{
			num.addChild(data);
		}
	}

    public void parseInt(Token t, Token data)
    {
        IntNode num = (IntNode) t;

        if (data != null)
        {
            num.addChild(data);
        }
    }

    public void parseUInt(Token t, Token data)
    {
        UIntNode num = (UIntNode) t;

        if (data != null)
        {
            num.addChild(data);
        }
    }

    public void parseBoolean(Token t, Token data)
	{
		BooleanNode bool = (BooleanNode) t;

		if (data != null)
		{
			bool.addChild(data);
		}
	}

	public void parseClass(Token t, Token data)
	{
		ClassNode cls = (ClassNode) t;

		if (data != null)
		{
			cls.addChild(data);
		}
	}

	public void parseFunction(Token t, Token data)
	{
		FunctionNode func = (FunctionNode) t;

		if (data != null)
		{
			func.addChild(data);
		}
	}

	public void parseInlineComponent(Token t, Token child)
	{
		InlineComponentNode inlineComponent = (InlineComponentNode) t;

		if (child != null)
		{
			inlineComponent.addChild(child);
		}
	}

	public void parseDeclarations(Token t, List declarations)
	{
	    DeclarationsNode declarationsNode = (DeclarationsNode) t;

		if (declarations != null)
		{
		    declarationsNode.addChildren(declarations);
		}
	}
	
	public void parseReparent(Token t)
    {
    }
	
	public void parseState(Token t, List overrides)
    {
		StateNode stateNode = (StateNode) t;

		if (stateNode != null)
		{
		    stateNode.addChildren(overrides);
		}
    }
	
	/*
	 * FXG Tags
	 */

	public void parseLibrary(Token t, List children)
	{
		LibraryNode library = (LibraryNode) t;

		if (children != null)
		{
			library.addChildren(children);
		}
	}

	public void parseDefinition(Token t, Token child)
	{
		DefinitionNode def = (DefinitionNode) t;

		if (child != null)
		{
			def.addChild(child);
		}
	}

	public void parseDesignLayer(Token t, List<Token> children)
	{
		DesignLayerNode designNode = (DesignLayerNode) t;

		if (children != null)
		{
			designNode.addChildren(children);
		}
	}
}
