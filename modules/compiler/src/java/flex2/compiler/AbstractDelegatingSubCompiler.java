////////////////////////////////////////////////////////////////////////////////
//
//  ADOBE SYSTEMS INCORPORATED
//  Copyright 2008 Adobe Systems Incorporated
//  All Rights Reserved.
//
//  NOTICE: Adobe permits you to use, modify, and distribute this file
//  in accordance with the terms of the license agreement accompanying it.
//
////////////////////////////////////////////////////////////////////////////////

package flex2.compiler;

import flex2.compiler.mxml.MxmlLogAdapter;
import flex2.compiler.util.DualModeLineNumberMap;
import flex2.compiler.util.LineNumberMap;
import flex2.compiler.util.ThreadLocalToolkit;

/**
 * Utility class be used for sub-compilers that delegate to another
 * sub-compiler.
 */
public abstract class AbstractDelegatingSubCompiler extends AbstractSubCompiler
{
    protected static final String DELEGATE_UNIT = "DelegateUnit";
    protected static final String LINE_NUMBER_MAP = "LineNumberMap";

    protected AbstractSubCompiler delegateSubCompiler;

    public Source preprocess(Source source)
    {
        return source;
    }

    public void parse2(CompilationUnit unit, SymbolTable symbolTable)
    {
        CompilationUnit ascUnit = (CompilationUnit) unit.getContext().getAttribute(DELEGATE_UNIT);
        Source.transferInheritance(unit, ascUnit);

        Logger original = ThreadLocalToolkit.getLogger();
        LineNumberMap map = (LineNumberMap) unit.getContext().getAttribute(LINE_NUMBER_MAP);
        Logger adapter = new MxmlLogAdapter(original, map);

        ThreadLocalToolkit.setLogger(adapter);
        delegateSubCompiler.parse2(ascUnit, symbolTable);
        ThreadLocalToolkit.setLogger(original);

        Source.transferAssets(ascUnit, unit);
        Source.transferGeneratedSources(ascUnit, unit);
    }

    /**
     * Analyze... The implementation must:
     *
     * 1. register type info to SymbolTable
     */
    public void analyze1(CompilationUnit unit, SymbolTable symbolTable)
    {
        CompilationUnit ascUnit = (CompilationUnit) unit.getContext().getAttribute(DELEGATE_UNIT);

        Logger original = ThreadLocalToolkit.getLogger();
        LineNumberMap map = (LineNumberMap) unit.getContext().getAttribute(LINE_NUMBER_MAP);
        Logger adapter = new MxmlLogAdapter(original, map);

        ThreadLocalToolkit.setLogger(adapter);
        delegateSubCompiler.analyze1(ascUnit, symbolTable);
        ThreadLocalToolkit.setLogger(original);

        Source.transferTypeInfo(ascUnit, unit);
        Source.transferNamespaces(ascUnit, unit);
    }

    public void analyze2(CompilationUnit unit, SymbolTable symbolTable)
    {
        CompilationUnit ascUnit = (CompilationUnit) unit.getContext().getAttribute(DELEGATE_UNIT);
        Source.transferDependencies(unit, ascUnit);

        Logger original = ThreadLocalToolkit.getLogger();
        LineNumberMap map = (LineNumberMap) unit.getContext().getAttribute(LINE_NUMBER_MAP);
        Logger adapter = new MxmlLogAdapter(original, map);

        ThreadLocalToolkit.setLogger(adapter);
        delegateSubCompiler.analyze2(ascUnit, symbolTable);
        ThreadLocalToolkit.setLogger(original);

        Source.transferDependencies(ascUnit, unit);
    }

    public void analyze3(CompilationUnit unit, SymbolTable symbolTable)
    {
        CompilationUnit ascUnit = (CompilationUnit) unit.getContext().getAttribute(DELEGATE_UNIT);
        Source.transferDependencies(unit, ascUnit);

        Logger original = ThreadLocalToolkit.getLogger();
        LineNumberMap map = (LineNumberMap) unit.getContext().getAttribute(LINE_NUMBER_MAP);
        Logger adapter = new MxmlLogAdapter(original, map);

        ThreadLocalToolkit.setLogger(adapter);
        delegateSubCompiler.analyze3(ascUnit, symbolTable);
        ThreadLocalToolkit.setLogger(original);
    }

    public void analyze4(CompilationUnit unit, SymbolTable symbolTable)
    {
        CompilationUnit ascUnit = (CompilationUnit) unit.getContext().getAttribute(DELEGATE_UNIT);

        Logger original = ThreadLocalToolkit.getLogger();
        LineNumberMap map = (LineNumberMap) unit.getContext().getAttribute(LINE_NUMBER_MAP);
        MxmlLogAdapter adapter = new MxmlLogAdapter(original, map);

        ThreadLocalToolkit.setLogger(adapter);
        delegateSubCompiler.analyze4(ascUnit, symbolTable);

        if (ThreadLocalToolkit.errorCount() > 0)
        {
            return;
        }

        ThreadLocalToolkit.setLogger(original);

        Source.transferExpressions(ascUnit, unit);
        Source.transferMetaData(ascUnit, unit);
        Source.transferLoaderClassBase(ascUnit, unit);
        Source.transferClassTable(ascUnit, unit);
        Source.transferStyles(ascUnit, unit);

    }

    /**
     * Generate ABC
     */
    public void generate(CompilationUnit unit, SymbolTable symbolTable)
    {
        CompilationUnit ascUnit = (CompilationUnit) unit.getContext().getAttribute(DELEGATE_UNIT);

        Logger original = ThreadLocalToolkit.getLogger();

        DualModeLineNumberMap map = (DualModeLineNumberMap) unit.getContext().getAttribute(LINE_NUMBER_MAP);
        if (map != null)
            map.flushTemp();    //  flush all compile-error-only line number mappings

        Logger adapter = new MxmlLogAdapter(original, map);

        ThreadLocalToolkit.setLogger(adapter);
        delegateSubCompiler.generate(ascUnit, symbolTable);

        if (ThreadLocalToolkit.errorCount() > 0)
        {
            return;
        }

        Source.transferGeneratedSources(ascUnit, unit);
        Source.transferBytecodes(ascUnit, unit);
    }

    /**
     * Postprocess... could be invoked multiple times.
     */
    public void postprocess(CompilationUnit unit, SymbolTable symbolTable)
    {
        
    }
}
