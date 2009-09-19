////////////////////////////////////////////////////////////////////////////////
//
//  ADOBE SYSTEMS INCORPORATED
//  Copyright 2005-2008 Adobe Systems Incorporated
//  All Rights Reserved.
//
//  NOTICE: Adobe permits you to use, modify, and distribute this file
//  in accordance with the terms of the license agreement accompanying it.
//
////////////////////////////////////////////////////////////////////////////////

// AdobePatentID="B976"

package flex2.compiler.as3;

import flex2.compiler.CompilationUnit;
import flex2.compiler.CompilerContext;
import flex2.compiler.as3.reflect.TypeTable;
import flex2.compiler.util.ThreadLocalToolkit;
import macromedia.asc.parser.Node;
import macromedia.asc.util.Context;

public final class SkinPartExtension implements Extension
{
    public void parse2(CompilationUnit unit, TypeTable typeTable)
    {
        SkinPartEvaluator spEvaluator = new SkinPartEvaluator(typeTable.getSymbolTable());
        spEvaluator.setLocalizationManager(ThreadLocalToolkit.getLocalizationManager());
        Node node = (Node) unit.getSyntaxTree();
        CompilerContext context = unit.getContext();
        Context cx = (Context) context.getAscContext();
        node.evaluate(cx, spEvaluator);
    }

    public void parse1(CompilationUnit unit, TypeTable typeTable)
    {
    }

    public void analyze1(CompilationUnit unit, TypeTable typeTable)
    {
    }

    public void analyze2(CompilationUnit unit, TypeTable typeTable)
    {
    }

    public void analyze3(CompilationUnit unit, TypeTable typeTable)
    {
    }

    public void analyze4(CompilationUnit unit, TypeTable typeTable)
    {
    }

    public void generate(CompilationUnit unit, TypeTable typeTable)
    {
    }
}
