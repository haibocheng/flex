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
import flex2.compiler.util.NameFormatter;
import flex2.compiler.as3.binding.ClassInfo;
import flex2.compiler.as3.binding.TypeAnalyzer;
import flex2.compiler.as3.reflect.NodeMagic;
import flex2.compiler.mxml.lang.StandardDefs;
import macromedia.asc.parser.ClassDefinitionNode;
import macromedia.asc.parser.DocCommentNode;
import macromedia.asc.parser.MetaDataNode;
import macromedia.asc.parser.Node;
import macromedia.asc.parser.NodeFactory;
import macromedia.asc.parser.StatementListNode;
import macromedia.asc.parser.TypeExpressionNode;
import macromedia.asc.semantics.Value;
import macromedia.asc.util.Context;
import flash.swf.tools.as3.EvaluatorAdapter;
import flex2.compiler.SymbolTable;

class HostComponentEvaluator extends EvaluatorAdapter
{
    private CompilationUnit unit;
    private SymbolTable symbolTable;
    private static final String SKINHOSTCOMPONENT = "hostComponent".intern();
    private static final String BINDABLE = "Bindable".intern();
    
    HostComponentEvaluator(CompilationUnit unit, SymbolTable symbolTable)
    {
        this.unit = unit;
        this.symbolTable = symbolTable;
    }

    public Value evaluate(Context context, MetaDataNode node)
    {
        if (StandardDefs.MD_HOSTCOMPONENT.equals(node.id) && NodeMagic.isClassDefinition(node))
        {
            processHostComponentMetaData(context, node);
        }

        return null;
    }

    /**
     * Generate a strongly typed variable 'hostComponent' on the current 
     * class instance with type specified by the HostComponent metadata.
     */
    private void processHostComponentMetaData(Context cx, MetaDataNode node) {
        if (node.count() == 1)
        {
            Node def = node.def;
            if (def instanceof ClassDefinitionNode) 
            {
                unit.expressions.add(NameFormatter.toMultiName(node.getValue(0)));
                ClassDefinitionNode classDef = (ClassDefinitionNode) def;
                if (!classDeclaresIdentifier(cx, classDef, SKINHOSTCOMPONENT)) 
                {
                    NodeFactory nf = cx.getNodeFactory();
                    
                    MetaDataNode bindingMetaData = AbstractSyntaxTreeUtil.generateMetaData(nf, BINDABLE);
                    bindingMetaData.id = BINDABLE;
                    StatementListNode statementList = nf.statementList(classDef.statements, bindingMetaData);
                    
                    int listSize = node.def.metaData.items.size();
                    // if the HostComponent metadata node has more than one items. 
                    // then look for the associated comment and stick it to the variable. 
                    if(listSize > 1 ) 
                    {
                        for(int ix =0; ix < listSize; ix++ )
                        {
                            // check if the node is of type MetaDataNode.
                            Node tempMeta = node.def.metaData.items.get(ix);
                            if(tempMeta instanceof MetaDataNode)
                            {
                                MetaDataNode tempMetaData = (MetaDataNode)tempMeta; 
                                if("HostComponent".equals( tempMetaData.id ) && ( ix < listSize - 1 ) )
                                {
                                    // if the node has the comment, it would be the next one. 
                                    Node temp = node.def.metaData.items.get(ix  + 1);
                                    
                                    // if the last one is a DocCommentnode, we can run it through the evaluator.
                                    if(temp instanceof DocCommentNode)
                                    {
                                        DocCommentNode tempDoc  = ((DocCommentNode)temp);
                                        
                                        // we can not access the metadata node directly because it doesn't have public access and it is buried deep into the tree. 
                                        // this is required so that we can access the comment easily.
                                        macromedia.asc.parser.MetaDataEvaluator evaluator = new macromedia.asc.parser.MetaDataEvaluator();
                                        evaluator.evaluate(cx, tempDoc);
                                        
                                        // if evaluator has not null comment. 
                                        if(evaluator.doccomments != null && evaluator.doccomments.size() != 0)
                                        {
                                            String comment = evaluator.doccomments.get(0).id;
                                            
                                            // if comment is present then create a DocCommentNode for the hostComponent variable 
                                            if(comment != null ) 
                                            {
                                                DocCommentNode hostComponentComment = AbstractSyntaxTreeUtil.generateDocComment(nf, comment.intern());
                                                
                                                if (hostComponentComment != null)
                                                {
                                                    statementList = nf.statementList(statementList, hostComponentComment);
                                                }                                                
                                            } 
                                        }
                                        
                                        break; // if we got here we already got the comment. now lets short circuit.
                                    }                                    
                                }
                            }
                        }
                    }
                    
                    TypeExpressionNode typeExpression = AbstractSyntaxTreeUtil.generateTypeExpression(nf, node.getValue(0), true);
                    Node variableDefinition = AbstractSyntaxTreeUtil.generatePublicVariable(cx, typeExpression, SKINHOSTCOMPONENT);
                    
                    classDef.statements = nf.statementList(statementList, variableDefinition);
                }
            }
        }
    }

    /**
     * Returns true if the class definition has previously declared a symbol (function or variable) with
     * the identifier provided.
     */
    private boolean classDeclaresIdentifier(Context cx, ClassDefinitionNode classDef, String identifier)
    {
        TypeAnalyzer typeAnalyzer = symbolTable.getTypeAnalyzer();
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

}
