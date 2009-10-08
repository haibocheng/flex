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
import flex2.compiler.SymbolTable;
import flex2.compiler.abc.AbcClass;
import flex2.compiler.abc.MetaData;
import flex2.compiler.abc.Method;
import flex2.compiler.abc.Variable;
import flex2.compiler.as3.binding.ClassInfo;
import flex2.compiler.as3.binding.TypeAnalyzer;
import flex2.compiler.as3.reflect.NodeMagic;
import flex2.compiler.as3.reflect.TypeTable;
import flex2.compiler.util.CompilerMessage;
import flex2.compiler.util.NameFormatter;
import flex2.compiler.util.QName;
import flex2.compiler.util.ThreadLocalToolkit;
import java.util.List;
import macromedia.asc.parser.ClassDefinitionNode;
import macromedia.asc.parser.DefinitionNode;
import macromedia.asc.parser.DocCommentNode;
import macromedia.asc.parser.FunctionDefinitionNode;
import macromedia.asc.parser.MetaDataNode;
import macromedia.asc.parser.Node;
import macromedia.asc.parser.NodeFactory;
import macromedia.asc.parser.StatementListNode;
import macromedia.asc.parser.TypeExpressionNode;
import macromedia.asc.parser.VariableDefinitionNode;
import macromedia.asc.util.Context;

public final class HostComponentExtension implements Extension
{
    private static final String SKINHOSTCOMPONENT = "hostComponent".intern();
    private static final String BINDABLE = "Bindable".intern();
    private static final String[] PUBLIC_NAMESPACE = new String[] {SymbolTable.publicNamespace};

    public void parse1(CompilationUnit unit, TypeTable typeTable)
    {
    	// We transform [HostComponent] metadata into a public hostComponent data member in
    	// parse1 because we need the parse1 Binding extension to render our new hostComponent
    	// data member 'Bindable'.  If we were to do this work in parse2 however, we could detect
    	// previously declared instances of a 'hostComponent' symbol within parent classes, which we
    	// cannot do reliably in parse1. TODO: Find a means of detecting previously declared
    	// [HostComponent] or hostComponent data members so as not to collide.
        if (unit.hostComponentMetaData != null)
        {
            CompilerContext context = unit.getContext();
            Context cx = (Context) context.getAscContext();
            TypeAnalyzer typeAnalyzer = typeTable.getSymbolTable().getTypeAnalyzer();
            generateHostComponentVariable(cx, unit, typeAnalyzer);
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
        if (unit.hostComponentMetaData != null)
        {
            CompilerContext context = unit.getContext();
            Context cx = (Context) context.getAscContext();
            validateRequiredSkinPartsAndStates(cx, unit.hostComponentMetaData, typeTable);
        }
    }

    /**
     * Generate a strongly typed variable 'hostComponent' on the current
     * class instance with type specified by the HostComponent metadata.
     */
    private void generateHostComponentVariable(Context cx, CompilationUnit unit, TypeAnalyzer typeAnalyzer)
    {
        MetaDataNode node = unit.hostComponentMetaData;

        if (node.count() == 1)
        {
            Node def = node.def;

            if (def instanceof ClassDefinitionNode)
            {
                unit.expressions.add(NameFormatter.toMultiName(node.getValue(0)));
                ClassDefinitionNode classDef = (ClassDefinitionNode) def;

                if (!classDeclaresIdentifier(cx, classDef, typeAnalyzer, SKINHOSTCOMPONENT))
                {
                    NodeFactory nodeFactory = cx.getNodeFactory();
                    MetaDataNode bindingMetaData = AbstractSyntaxTreeUtil.generateMetaData(nodeFactory, BINDABLE);
                    bindingMetaData.id = BINDABLE;
                    StatementListNode statementList = nodeFactory.statementList(classDef.statements, bindingMetaData);

                    int listSize = node.def.metaData.items.size();
                    // if the HostComponent metadata node has more than one items.
                    // then look for the associated comment and stick it to the variable.
                    if (listSize > 1)
                    {
                        for (int ix = 0; ix < listSize; ix++)
                        {
                            // check if the node is of type MetaDataNode.
                            Node tempMeta = node.def.metaData.items.get(ix);

                            if (tempMeta instanceof MetaDataNode)
                            {
                                MetaDataNode tempMetaData = (MetaDataNode) tempMeta;

                                if ("HostComponent".equals(tempMetaData.id) && (ix < listSize - 1))
                                {
                                    // if the node has the comment, it would be the next one.
                                    Node temp = node.def.metaData.items.get(ix + 1);

                                    // if the last one is a DocCommentnode, we can run it through the evaluator.
                                    if (temp instanceof DocCommentNode)
                                    {
                                        DocCommentNode tempDoc = ((DocCommentNode)temp);

                                        // we can not access the metadata node directly because it doesn't
                                        // have public access and it is buried deep into the tree.  this is
                                        // required so that we can access the comment easily.
                                        macromedia.asc.parser.MetaDataEvaluator evaluator =
                                            new macromedia.asc.parser.MetaDataEvaluator();
                                        evaluator.evaluate(cx, tempDoc);

                                        // if evaluator has not null comment.
                                        if (evaluator.doccomments != null && evaluator.doccomments.size() != 0)
                                        {
                                            String comment = evaluator.doccomments.get(0).id;

                                            // if comment is present then create a DocCommentNode for the hostComponent variable
                                            if (comment != null)
                                            {
                                                DocCommentNode hostComponentComment =
                                                    AbstractSyntaxTreeUtil.generateDocComment(nodeFactory, comment.intern());

                                                if (hostComponentComment != null)
                                                {
                                                    statementList = nodeFactory.statementList(statementList, hostComponentComment);
                                                }
                                            }
                                        }

                                        break; // if we got here we already got the comment. now lets short circuit.
                                    }
                                }
                            }
                        }
                    }

                    TypeExpressionNode typeExpression = AbstractSyntaxTreeUtil.generateTypeExpression(nodeFactory, node.getValue(0), true);
                    Node variableDefinition = AbstractSyntaxTreeUtil.generatePublicVariable(cx, typeExpression, SKINHOSTCOMPONENT);

                    classDef.statements = nodeFactory.statementList(statementList, variableDefinition);
                }
            }
        }
    }

    /**
     * Returns true if the class definition has previously declared a symbol (function or variable) with
     * the identifier provided.
     */
    private boolean classDeclaresIdentifier(Context cx, ClassDefinitionNode classDef,
                                            TypeAnalyzer typeAnalyzer, String identifier)
    {
        String className = NodeMagic.getClassName(classDef);

        typeAnalyzer.evaluate(cx, classDef);

        ClassInfo classInfo = typeAnalyzer.getClassInfo(className);
        if (classInfo != null && (
            classInfo.definesVariable(identifier) ||
            classInfo.definesFunction(identifier, true) ||
            classInfo.definesGetter(identifier, true) ||
            classInfo.definesSetter(identifier, true)))
        {
            return true;
        }
        return false;
    }

    private void validateRequiredSkinParts(AbcClass hostComponentClass, AbcClass skinClass,
                                           Context cx, int position, TypeTable typeTable)
    {
        QName[] variableNames = hostComponentClass.getVariableNames();

        if (variableNames != null)
        {
            for (int i = 0, length = variableNames.length; i < length; i++)
            {
                Variable variable = hostComponentClass.getVariable(new String[] {variableNames[i].getNamespace()},
                                                                   variableNames[i].getLocalPart(), false);

                List<MetaData> skinPartsMetaDataList = variable.getMetaData("SkinPart");

                if (skinPartsMetaDataList != null)
                {
                    validateRequiredSkinParts(skinPartsMetaDataList, variableNames[i].getLocalPart(),
                                              skinClass, cx, position);
                }
            }
        }

        QName[] getterNames = hostComponentClass.getGetterNames();

        if (getterNames != null)
        {
            for (int i = 0, length = getterNames.length; i < length; i++)
            {
                Method getter = hostComponentClass.getGetter(new String[] {getterNames[i].getNamespace()},
                                                             getterNames[i].getLocalPart(), false);

                List<MetaData> skinPartsMetaDataList = getter.getMetaData("SkinPart");

                if (skinPartsMetaDataList != null)
                {
                    validateRequiredSkinParts(skinPartsMetaDataList, getterNames[i].getLocalPart(),
                                              skinClass, cx, position);
                }
            }
        }

        // Validate up the inheritance chain
        String superTypeName = hostComponentClass.getSuperTypeName();

        if (superTypeName != null)
        {
            AbcClass superType = typeTable.getClass(superTypeName);

            if (superType != null)
            {
                validateRequiredSkinParts(superType, skinClass, cx, position, typeTable);
            }
        }
    }

    private void validateRequiredSkinParts(List<MetaData> skinPartsMetaDataList, String skinPartName,
                                           AbcClass skinClass, Context cx, int position)
    {
        for (MetaData skinPartsMetaData : skinPartsMetaDataList)
        {
            String required = skinPartsMetaData.getValue("required");

            if ("true".equals(required))
            {
                if ((skinClass.getVariable(PUBLIC_NAMESPACE, skinPartName, true) == null) &&
                    (skinClass.getGetter(PUBLIC_NAMESPACE, skinPartName, true) == null))
                {
                    cx.localizedError2(cx.input.origin, position, new MissingSkinPart(skinPartName));
                }
            }
        }
    }

    private void validateRequiredSkinPartsAndStates(Context cx, MetaDataNode metaData, TypeTable typeTable)
    {
        String hostComponentClassName = metaData.getValue(0);
        AbcClass hostComponentClass = typeTable.getClass(NameFormatter.toColon(hostComponentClassName));

        if (hostComponentClass == null)
        {
            cx.localizedError2(cx.input.origin, metaData.pos(),
                               new HostComponentClassNotFound(hostComponentClassName));
        }
        else if (metaData.def != null)
        {
            String skinClassName = NodeMagic.getClassName((ClassDefinitionNode) metaData.def);
            AbcClass skinClass = typeTable.getClass(skinClassName);

            validateRequiredSkinParts(hostComponentClass, skinClass, cx, metaData.pos(), typeTable);
            validateRequiredSkinStates(hostComponentClass, skinClass, cx, metaData.pos());
        }
    }

    private void validateRequiredSkinStates(AbcClass hostComponentClass, AbcClass skinClass,
                                            Context cx, int position)
    {
        List<MetaData> skinStatesMetaDataList = hostComponentClass.getMetaData("SkinState", true);
        List<MetaData> statesMetaDataList = skinClass.getMetaData("States", true);

        if (skinStatesMetaDataList != null)
        {
            for (MetaData skinStatesMetaData : skinStatesMetaDataList)
            {
                String skinStateName = skinStatesMetaData.getValue(0);
                boolean isFound = false;

                if (statesMetaDataList != null)
                {
                    foundIt:
                    for (MetaData statesMetaData : statesMetaDataList)
                    {
                        for (int i = 0, count = statesMetaData.count(); i < count; i++)
                        {
                            String state = statesMetaData.getValue(i);

                            if (skinStateName.equals(state))
                            {
                                isFound = true;
                                break foundIt;
                            }
                        }
                    }
                }

                if (!isFound)
                {
                    cx.localizedError2(cx.input.origin, position, new MissingSkinState(skinStateName));
                }
            }
        }
    }

    public static class HostComponentClassNotFound extends CompilerMessage.CompilerError
    {
        private static final long serialVersionUID = 5290330001936137678L;

        public String className;

        public HostComponentClassNotFound(String className)
        {
            this.className = className;
        }
    }

    public static class MissingSkinPart extends CompilerMessage.CompilerError
    {
        private static final long serialVersionUID = 5290330001936137667L;

        public String skinPartName;

        public MissingSkinPart(String skinPartName)
        {
            this.skinPartName = skinPartName;
        }
    }

    public static class MissingSkinState extends CompilerMessage.CompilerError
    {
        private static final long serialVersionUID = 5290330001936137669L;

        public String skinStateName;

        public MissingSkinState(String skinStateName)
        {
            this.skinStateName = skinStateName;
        }
    }
}
