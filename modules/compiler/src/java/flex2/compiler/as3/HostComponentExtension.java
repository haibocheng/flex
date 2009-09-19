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

package flex2.compiler.as3;

import flex2.compiler.CompilationUnit;
import flex2.compiler.CompilerContext;
import flex2.compiler.as3.reflect.TypeTable;
import flex2.compiler.util.ThreadLocalToolkit;
import macromedia.asc.parser.Node;
import macromedia.asc.util.Context;

public final class HostComponentExtension implements Extension
{
    public void parse1(CompilationUnit unit, TypeTable typeTable)
    {
    	// We transform [HostComponent] metadata into a public hostComponent data member in
    	// parse1 because we need the parse1 Binding extension to render our new hostComponent
    	// data member 'Bindable'.  If we were to do this work in parse2 however, we could detect
    	// previously declared instances of a 'hostComponent' symbol within parent classes, which we
    	// cannot do reliably in parse1. TODO: Find a means of detecting previously declared
    	// [HostComponent] or hostComponent data members so as not to collide.
        if (unit.hasHostComponentMD)
        {
            HostComponentEvaluator hcEvaluator = new HostComponentEvaluator(unit, typeTable.getSymbolTable());
            hcEvaluator.setLocalizationManager(ThreadLocalToolkit.getLocalizationManager());
            Node node = (Node) unit.getSyntaxTree();
            CompilerContext context = unit.getContext();
            Context cx = (Context) context.getAscContext();
            node.evaluate(cx, hcEvaluator);
        }
    }

    public void parse2(CompilationUnit unit, TypeTable typeTable)
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
