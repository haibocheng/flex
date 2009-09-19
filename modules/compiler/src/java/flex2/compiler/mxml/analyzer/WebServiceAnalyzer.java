////////////////////////////////////////////////////////////////////////////////
//
//  ADOBE SYSTEMS INCORPORATED
//  Copyright 2004-2006 Adobe Systems Incorporated
//  All Rights Reserved.
//
//  NOTICE: Adobe permits you to use, modify, and distribute this file
//  in accordance with the terms of the license agreement accompanying it.
//
////////////////////////////////////////////////////////////////////////////////

package flex2.compiler.mxml.analyzer;

import flex2.compiler.CompilationUnit;
import flex2.compiler.mxml.MxmlConfiguration;
import flex2.compiler.mxml.dom.AnalyzerAdapter;
import flex2.compiler.mxml.dom.Node;
import flex2.compiler.mxml.dom.OperationNode;
import flex2.compiler.mxml.dom.RequestNode;
import flex2.compiler.mxml.rep.MxmlDocument;
import flex2.compiler.util.CompilerMessage;

/**
 * @author Clement Wong
 */
public class WebServiceAnalyzer extends AnalyzerAdapter
{
    private MxmlDocument document;

	public WebServiceAnalyzer(CompilationUnit unit, MxmlConfiguration mxmlConfiguration, MxmlDocument document)
	{
		super(unit, mxmlConfiguration);
		this.document = document;
	}

	public void analyze(OperationNode node)
	{
		if (node.getAttributeValue("name") == null)
		{
			log(node, new OperationRequiresName());
		}
		super.analyze(node);
	}

    public void analyze(RequestNode node)
    {
		int attrCount = node.getAttributeCount();
		if (attrCount > 1 ||
				(attrCount == 1 && !((node.getAttributeNames().next())).getLocalPart().equals("format")))
        {
	        log(node, new RequestRequiresFormat());
        }
        super.analyze(node);
    }

    public void analyze(Node node)
    {
        super.analyze(node);
    }

    protected int getDocumentVersion()
    {
        return document.getVersion();
    }

    protected String getLanguageNamespace()
    {
        return document.getLanguageNamespace();
    }

	// error messages

	public static class OperationRequiresName extends CompilerMessage.CompilerError
	{
		private static final long serialVersionUID = -2365576843611189649L;

        public OperationRequiresName()
		{
			super();
		}
	}

	public static class RequestRequiresFormat extends CompilerMessage.CompilerError
	{
		private static final long serialVersionUID = 2836590913630566556L;

        public RequestRequiresFormat()
		{
			super();
		}
	}
}
