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
import flex2.compiler.mxml.dom.ArgumentsNode;
import flex2.compiler.mxml.dom.MethodNode;
import flex2.compiler.mxml.rep.MxmlDocument;
import flex2.compiler.util.CompilerMessage;

/**
 * @author Clement Wong
 */
public class RemoteObjectAnalyzer extends AnalyzerAdapter
{
    private MxmlDocument document;

    public RemoteObjectAnalyzer(CompilationUnit unit, MxmlConfiguration mxmlConfiguration, MxmlDocument document)
    {
        super(unit, mxmlConfiguration);
        this.document = document;
	}

	public void analyze(MethodNode node)
	{
		if (node.getAttributeValue("name") == null)
		{
			log(node, new MethodRequiresName());
		}
		super.analyze(node);
	}

    public void analyze(ArgumentsNode node)
    {
        if (node.getAttributeCount() > 0)
        {
	        log(node, new ArgumentsNoAttributes());
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

	public static class MethodRequiresName extends CompilerMessage.CompilerError
	{
		private static final long serialVersionUID = -1993485855814744142L;

        public MethodRequiresName()
		{
			super();
		}
	}

	public static class ArgumentsNoAttributes extends CompilerMessage.CompilerError
	{
		private static final long serialVersionUID = -1790969989441708094L;

        public ArgumentsNoAttributes()
		{
			super();
		}
	}
}
