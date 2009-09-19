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
import flex2.compiler.mxml.dom.RequestNode;
import flex2.compiler.mxml.rep.MxmlDocument;
import flex2.compiler.util.CompilerMessage;

/**
 * @author Clement Wong
 */
public class HTTPServiceAnalyzer extends AnalyzerAdapter
{
    private MxmlDocument document;

	public HTTPServiceAnalyzer(CompilationUnit unit, MxmlConfiguration mxmlConfiguration, MxmlDocument document)
	{
		super(unit, mxmlConfiguration);
		this.document = document;
	}

	public void analyze(RequestNode node)
	{
		if (node.getAttributeCount() > 0)
		{
			log(node, new RequestNoAttributes());
		}
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

	public static class RequestNoAttributes extends CompilerMessage.CompilerError
	{
		private static final long serialVersionUID = -7135962786954334643L;

        public RequestNoAttributes()
		{
			super();
		}
	}
}
