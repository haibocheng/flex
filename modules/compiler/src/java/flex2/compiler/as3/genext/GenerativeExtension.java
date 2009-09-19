////////////////////////////////////////////////////////////////////////////////
//
//  ADOBE SYSTEMS INCORPORATED
//  Copyright 2005-2006 Adobe Systems Incorporated
//  All Rights Reserved.
//
//  NOTICE: Adobe permits you to use, modify, and distribute this file
//  in accordance with the terms of the license agreement accompanying it.
//
////////////////////////////////////////////////////////////////////////////////

package flex2.compiler.as3.genext;

import flex2.compiler.as3.Extension;
import flex2.compiler.as3.binding.ClassInfo;
import flex2.compiler.as3.binding.TypeAnalyzer;
import flex2.compiler.as3.reflect.TypeTable;
import flex2.compiler.CompilationUnit;
import java.util.Iterator;
import java.util.Map.Entry;
import macromedia.asc.parser.Evaluator;
import macromedia.asc.parser.Node;
import macromedia.asc.util.Context;

public abstract class GenerativeExtension implements Extension
{
    public static final String IEVENT_DISPATCHER = "IEventDispatcher";

    protected String generatedOutputDirectory;
    protected boolean generateAbstractSyntaxTree;
    protected boolean processComments;

    /**
     *
     */
    public GenerativeExtension(String generatedOutputDirectory, boolean generateAbstractSyntaxTree, boolean processComments)
    {
        this.generatedOutputDirectory = generatedOutputDirectory;
        this.generateAbstractSyntaxTree = generateAbstractSyntaxTree;
        this.processComments = processComments;
    }

    /**
     *
     */
    protected abstract void addInheritance(CompilationUnit unit);

    /**
     *
     */
    protected abstract GenerativeFirstPassEvaluator getFirstPassEvaluator(CompilationUnit unit,
                                                                          TypeTable typeTable);

	/**
	 *
	 */
	protected abstract String getFirstPassEvaluatorKey();

    /**
     *
     */
    protected abstract Evaluator getSecondPassEvaluator(CompilationUnit unit, 
                                                        TypeAnalyzer typeAnalyzer,
                                                        GenerativeFirstPassEvaluator firstPassEvaluator);

    /**
     *
     */
    public void parse1(CompilationUnit unit, TypeTable typeTable)
    {
        Node node = (Node) unit.getSyntaxTree();
        Context cx = unit.getContext().getAscContext();
        GenerativeFirstPassEvaluator firstPassEvaluator = getFirstPassEvaluator(unit, typeTable);

        node.evaluate(cx, firstPassEvaluator);

        if ( firstPassEvaluator.makeSecondPass() )
        {
            addInheritance(unit);

            unit.getContext().setAttribute(getFirstPassEvaluatorKey(), firstPassEvaluator);
        }
    }

    public void parse2(CompilationUnit unit, TypeTable typeTable)
    {
        GenerativeFirstPassEvaluator firstPassEvaluator =
            (GenerativeFirstPassEvaluator) unit.getContext().removeAttribute(getFirstPassEvaluatorKey());

        if (firstPassEvaluator != null)
        {
            Node node = (Node) unit.getSyntaxTree();
            Context cx = unit.getContext().getAscContext();
            TypeAnalyzer typeAnalyzer = typeTable.getSymbolTable().getTypeAnalyzer();

            node.evaluate(cx, typeAnalyzer);

            Iterator iterator = firstPassEvaluator.getClassMap().entrySet().iterator();

            while ( iterator.hasNext() )
            {
                Entry entry = (Entry) iterator.next();
                String className = (String) entry.getKey();
                GenerativeClassInfo generativeClassInfo = (GenerativeClassInfo) entry.getValue();
                ClassInfo classInfo = typeAnalyzer.getClassInfo(className);
                generativeClassInfo.setClassInfo(classInfo);
            }

            Evaluator secondPassEvaluator = getSecondPassEvaluator(unit, typeAnalyzer, firstPassEvaluator);

            node.evaluate(cx, secondPassEvaluator);
        }
    }

    /**
     *
     */
    public void analyze1(CompilationUnit unit, TypeTable typeTable)
    {
    }

    /**
     *
     */
    public void analyze2(CompilationUnit unit, TypeTable typeTable)
    {
    }

    /**
     *
     */
    public void analyze3(CompilationUnit unit, TypeTable typeTable)
    {
    }

    /**
     *
     */
    public void analyze4(CompilationUnit unit, TypeTable typeTable)
    {
    }

    /**
     *
     */
    public void generate(CompilationUnit unit, TypeTable typeTable)
    {
    }
}
