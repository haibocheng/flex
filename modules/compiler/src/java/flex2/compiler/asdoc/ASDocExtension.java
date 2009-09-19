// //////////////////////////////////////////////////////////////////////////////
//
// ADOBE SYSTEMS INCORPORATED
// Copyright 2006-2009 Adobe Systems Incorporated
// All Rights Reserved.
//
// NOTICE: Adobe permits you to use, modify, and distribute this file
// in accordance with the terms of the license agreement accompanying it.
//
// //////////////////////////////////////////////////////////////////////////////

package flex2.compiler.asdoc;

import flex2.compiler.abc.AbcClass;
import flex2.compiler.as3.Extension;
import flex2.compiler.as3.reflect.TypeTable;
import flex2.compiler.CompilationUnit;
import flex2.compiler.CompilerContext;
import flex2.compiler.util.Name;
import flex2.compiler.util.NameFormatter;
import flex2.compiler.util.QName;
import macromedia.asc.parser.ProgramNode;
import macromedia.asc.parser.MetaDataEvaluator;
import macromedia.asc.parser.MetaDataNode;

import macromedia.asc.util.Context;
import macromedia.asc.util.ObjectList;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.FileOutputStream;
import java.io.File;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import flash.util.Trace;

/**
 * Compiler extension that creates the ASDoc xml file
 * 
 * @author Brian Deitte
 */
public class ASDocExtension implements Extension
{
    private static String EXCLUDE_CLASS = "ExcludeClass";

    private StringBuilder out;
    private String xml;
    private List excludeClasses;
    private Set includeOnly;
    private Set packages;

    private ClassTable tab; // new

    public ASDocExtension(List excludeClasses, Set includeOnly, Set packages, boolean restoreBuiltinClasses)
    {
        this.excludeClasses = excludeClasses;
        this.includeOnly = includeOnly;
        this.packages = packages;

        tab = new ClassTable(restoreBuiltinClasses); // new
    }

    public void finish(boolean restoreBuiltinClasses)
    {
        /*
         * This part shouldn't be exposed...the default should be a TopLevel
         * passed in.
         */
        DocCommentGenerator g = new TopLevelGenerator(); // new
        g.generate(tab); // new
        xml = g.toString(); // new
        g = null; // new
        
        if(restoreBuiltinClasses && xml != null)
        {
            xml = xml.replaceAll("_ASDoc2", "");
            xml = xml.replaceAll("_ASDoc", "");
            xml = xml.replaceAll("Infinity_Neg_Inf", "-Infinity");
        }
    }

    public void saveFile(File file)
    {
        BufferedOutputStream outputStream = null;
        try
        {
            outputStream = new BufferedOutputStream(new FileOutputStream(file));
            outputStream.write(xml.getBytes("UTF-8"));
            outputStream.flush();
        }
        catch (IOException ex)
        {
            if (Trace.error)
                ex.printStackTrace();

            throw new RuntimeException("Could not save " + file + ": " + ex);
        }
        finally
        {
            if (Trace.asdoc)
                System.out.println("Wrote doc file: " + file);

            if (outputStream != null)
            {
                try
                {
                    outputStream.close();
                }
                catch (IOException ex)
                {
                }
            }
        }
    }

    public String getXML()
    {
        return xml;
    }

    public void parse1(CompilationUnit unit, TypeTable typeTable)
    {
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
        // this code is similar to code in asc. We don't go through the main asc
        // path, though,
        // and have multiple compilation passes, so we have to have our own
        // version of this code
        CompilerContext flexCx = unit.getContext();
        // Don't do the HashMap lookup for the context. access strongly typed
        // variable for the ASC Context from CompilerContext
        Context cx = flexCx.getAscContext();
        ProgramNode node = (ProgramNode)unit.getSyntaxTree();

        // stop processing if unit.topLevelDefinitions.first() is null
        if (unit.topLevelDefinitions.first() == null)
        {
            return;
        }

        String className = NameFormatter.toDot(unit.topLevelDefinitions.first());
        boolean exclude = false;
        if (includeOnly != null && !includeOnly.contains(className))
        {
            exclude = true;
        }
        else if (excludeClasses.contains(className))
        {
            excludeClasses.remove(className);
            exclude = true;
        }
        // check the metadata for ExcludeClass. Like Flex Builder, ASDoc uses
        // this compiler metadata to
        // determine which classes should not be visible to the user
        else if (unit.metadata != null)
        {

            for (Iterator iterator = unit.metadata.iterator(); iterator.hasNext();)
            {
                MetaDataNode metaDataNode = (MetaDataNode)iterator.next();
                if (EXCLUDE_CLASS.equals(metaDataNode.id))
                {
                    exclude = true;
                    break;
                }
            }
        }

        // the inheritance needs to be processed in a predictable order.. 
        Set<QName> inheritance = new TreeSet<QName>(new ComparatorImpl());
        
        for (Name name : unit.inheritance)
        {
            if (name instanceof QName)
            {
                inheritance.add((QName) name);
            }
        }

        boolean flag = false;
        if (!exclude && !unit.getSource().isInternal())
        {
            if (Trace.asdoc)
                System.out.println("Generating XML for " + unit.getSource().getName());

            flag = false;
        }
        else
        {
            if (Trace.asdoc)
                System.out.println("Skipping generating XML for " + unit.getSource().getName());

            flag = true;
        }
        
        if (packages.size() != 0)
        {
            String n = unit.topLevelDefinitions.first().getNamespace();
            if (n != null)
            {
                packages.remove(n);
            }
        }

        cx.pushScope(node.frame);

        MetaDataEvaluator printer = new MetaDataEvaluator();
        node.evaluate(cx, printer);

        ObjectList comments = printer.doccomments;

        AbcClass abcClass = typeTable.getClass(unit.topLevelDefinitions.first().toString());
        tab.addComments(unit.topLevelDefinitions.first(), comments, inheritance, flag, cx, abcClass);

        cx.popScope();        
    }
}

class ComparatorImpl implements Comparator<QName>
{
    public int compare(QName first, QName second)
    {
        return first.toString().compareTo(second.toString());
    }
}
