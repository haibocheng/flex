////////////////////////////////////////////////////////////////////////////////
//
//  ADOBE SYSTEMS INCORPORATED
//  Copyright 2005-2007 Adobe Systems Incorporated
//  All Rights Reserved.
//
//  NOTICE: Adobe permits you to use, modify, and distribute this file
//  in accordance with the terms of the license agreement accompanying it.
//
////////////////////////////////////////////////////////////////////////////////

package flex2.compiler.as3.binding;

import flash.swf.tools.as3.EvaluatorAdapter;
import flex2.compiler.CompilationUnit;
import flex2.compiler.SymbolTable;
import flex2.compiler.abc.Attributes;
import flex2.compiler.abc.AbcClass;
import flex2.compiler.abc.MetaData;
import flex2.compiler.abc.Method;
import flex2.compiler.abc.Variable;
import flex2.compiler.as3.reflect.NodeMagic;
import flex2.compiler.as3.reflect.TypeTable;
import flex2.compiler.mxml.lang.StandardDefs;
import flex2.compiler.mxml.rep.BindingExpression;
import flex2.compiler.mxml.rep.Model;
import flex2.compiler.util.*;
import macromedia.asc.parser.*;
import macromedia.asc.semantics.ReferenceValue;
import macromedia.asc.semantics.Value;
import macromedia.asc.semantics.VariableSlot;
import macromedia.asc.util.Context;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

/**
 * This class is a rough equivalent of Flex 1.5's flex.compiler.WatcherVisitor.
 *
 * @author Paul Reilly
 * @author Matt Chotin
 */
public class DataBindingFirstPassEvaluator extends EvaluatorAdapter
{
    private static final String BINDABLE = "Bindable";
    private static final String CHANGE_EVENT = "ChangeEvent";
    private static final String NON_COMMITTING_CHANGE_EVENT = "NonCommittingChangeEvent";
    private static final String EVENT = "event";
    private static final String STYLE = "style";

    private boolean showBindingWarnings;

    private TypeTable typeTable;
    private Stack<String> srcTypeStack;
    private Stack<ArgumentListNode> argumentListStack;
    private Set<ArgumentListNode> skipInitSet;
    private Set<ArgumentListNode> resetSet;
    private MemberExpressionNode xmlMember = null;
    private boolean insideRepeaterExpression = false;
    private boolean insideXMLExpression = false;
    private boolean insideBindingsSetupFunction = false;
    private Stack<CallExpressionNode> callExpressionStack;
    private boolean insideGetExpression = false;
    private boolean insideArrayExpression = false;
    private LinkedList<Watcher> watcherList;
    private ClassDefinitionNode currentClassDefinition;
    private BindingExpression currentBindingExpression;
    private int bindingId = 0;
    /**
     * This is used for incrementing the watcher id count.
     */
    private int currentWatcherId = 0;
    private String bindingFunctionName;
    private List<BindingExpression> bindingExpressions;
    private Set<ClassDefinitionNode> evaluatedClasses;

    private DataBindingInfo dataBindingInfo;
    private List<DataBindingInfo> dataBindingInfoList;
    private boolean makeSecondPass = false;
    private StandardDefs standardDefs;

    public DataBindingFirstPassEvaluator(CompilationUnit unit, TypeTable typeTable, boolean showBindingWarnings)
    {
        this.typeTable = typeTable;
        argumentListStack = new Stack<ArgumentListNode>();
        skipInitSet = new HashSet<ArgumentListNode>();
        resetSet = new HashSet<ArgumentListNode>();
        srcTypeStack = new Stack<String>();
        callExpressionStack = new Stack<CallExpressionNode>();
        standardDefs = unit.getStandardDefs();

        @SuppressWarnings("unchecked")
        List<BindingExpression> tmpBindingExpressions = (List<BindingExpression>) unit.getContext().getAttribute(flex2.compiler.CompilerContext.BINDING_EXPRESSIONS);
        bindingExpressions = tmpBindingExpressions;

        evaluatedClasses = new HashSet<ClassDefinitionNode>();
        dataBindingInfoList = new ArrayList<DataBindingInfo>();
        this.showBindingWarnings = showBindingWarnings;

        setLocalizationManager(ThreadLocalToolkit.getLocalizationManager());
    }

    private boolean addBindables(Watcher watcher, List bindables)
    {
        boolean addedBindable = false;

        if (bindables != null)
        {
            Iterator bindablesIterator = bindables.iterator();

            while ( bindablesIterator.hasNext() )
            {
                MetaData metaData = (MetaData) bindablesIterator.next();

                if ("true".equals(metaData.getValue(STYLE)))
                {
                    assert watcher instanceof FunctionReturnWatcher : watcher.getClass().getName();
                    ((FunctionReturnWatcher) watcher).setStyleWatcher(true);
                }
                else
                {
                    String event = getEventName(metaData);

                    if (event != null)
                    {
                        watcher.addChangeEvent(event);
                    }
                    else
                    {
                        watcher.addChangeEvent(StandardDefs.MDPARAM_PROPERTY_CHANGE);
                    }
                }

                addedBindable = true;
            }
        }

        return addedBindable;
    }

    private void checkForStaticProperty(Attributes attributes, Watcher watcher, String srcTypeName)
    {
        if ((attributes != null) && attributes.hasStatic() && (watcher instanceof PropertyWatcher))
        {
            ((PropertyWatcher) watcher).setStaticProperty(true);
            watcher.setClassName(srcTypeName);
        }
    }

    /**
     * TODO this exactly replicates BindableFirstPassEvaluator.getEventName()'s logic on MetaDataNode. Find a way to factor
     */
    private String getEventName(MetaData metaData)
    {
        String eventName = metaData.getValue(EVENT);
        if (eventName != null)
        {
            //    [Bindable( ... event="<eventname>" ... )]
            return eventName;
        }
        else if (metaData.count() == 1)
        {
            // 1. Currently, ASC builds MetaDataNodes in such a way that [Foo] and [Foo("Foo")] both result in
            // node.count() == 1 and node.getValue(0).equals("Foo"). Soooooo, best not to have an event named Bindable!
            String param = metaData.getValue(0);
            if (!param.equals(metaData.getID()))
            {
                //    [Bindable("<eventname>")]
                return param;
            }
        }
        return null;
    }

    private boolean addChangeEvents(Watcher watcher, List changeEvents)
    {
        boolean addedChangeEvent = false;

        if (changeEvents != null)
        {
            Iterator changeEventIterator = changeEvents.iterator();

            while ( changeEventIterator.hasNext() )
            {
                MetaData metaData = (MetaData) changeEventIterator.next();
                String event = metaData.getValue(0);
                if (event != null)
                {
                    watcher.addChangeEvent(event);
                    addedChangeEvent = true;
                }
            }
        }

        return addedChangeEvent;
    }

    private boolean addNonCommittingChangeEvents(Watcher watcher, List changeEvents)
    {
        boolean addedChangeEvent = false;

        if (changeEvents != null)
        {
            Iterator changeEventIterator = changeEvents.iterator();

            while ( changeEventIterator.hasNext() )
            {
                MetaData metaData = (MetaData) changeEventIterator.next();
                String event = metaData.getValue(0);
                if (event != null)
                {
                    watcher.addChangeEvent(event, false);
                    addedChangeEvent = true;
                }
            }
        }

        return addedChangeEvent;
    }

    public Value evaluate(Context context, ArgumentListNode node)
    {
        if (insideBindingsSetupFunction)
        {
            for (int i = 0, size = node.items.size(); i < size; i++)
            {
                Node argument = node.items.get(i);

                LinkedList<Watcher> tempWatcherList = watcherList;

                argumentListStack.push(node);
                srcTypeStack.push( srcTypeStack.firstElement() );

                if (!skipInitSet.remove(node))
                {
                    watcherList = new LinkedList<Watcher>();
                }

                argument.evaluate(context, this);

                if (resetSet.remove(node))
                {
                    watcherList = tempWatcherList;
                }

                srcTypeStack.pop();
                argumentListStack.pop();
            }
        }

        return null;
    }

    public Value evaluate(Context context, BinaryExpressionNode node)
    {
        if (node.lhs != null)
        {
            node.lhs.evaluate(context, this);
        }

        if (insideBindingsSetupFunction && (node.op != Tokens.AS_TOKEN))
        {
            watcherList = new LinkedList<Watcher>();
        }

        if (node.rhs != null)
        {
            node.rhs.evaluate(context, this);
        }

        if (insideBindingsSetupFunction && (node.op != Tokens.AS_TOKEN))
        {
            watcherList = new LinkedList<Watcher>();
        }

        return null;
    }

    public Value evaluate(Context context, CallExpressionNode node)
    {
        if (insideBindingsSetupFunction && (node.expr != null))
        {
            if (insideRepeaterExpression &&
                (node.expr instanceof IdentifierNode) &&
                ((IdentifierNode) node.expr).name.equals("getItemAt"))
            {
                skipInitSet.add((ArgumentListNode) node.args);
                node.args.evaluate(context, this);
            }
            else
            {
                argumentListStack.push(node.args);

                if (node.expr instanceof IdentifierNode)
                {
                    IdentifierNode identifier = (IdentifierNode) node.expr;

                    // If expr.name is the same as the ref.type.name.name, then this seems to
                    // be a cast.  There is no need to setup a watcher for a cast, so skip
                    // evaluating expr.
                    if ((identifier.ref != null) &&
                        (identifier.ref.getType(context) != null) &&
                        !identifier.name.equals(identifier.ref.getType(context).getName().name))
                    {
                        callExpressionStack.push(node);
                        node.expr.evaluate(context, this);
                        callExpressionStack.pop();
                        resetSet.add(node.args);
                    }
                    else
                    {
                        skipInitSet.add((ArgumentListNode) node.args);
                    }
                }
                else
                {
                    assert false : "Unexpected CallExpressionNode.expr type: " + node.expr.getClass().getName();
                }

                if (node.args != null)
                {
                    srcTypeStack.push( srcTypeStack.firstElement() );
                    node.args.evaluate(context, this);
                    srcTypeStack.pop();
                }

                argumentListStack.pop();
            }
        }

        return null;
    }

    public Value evaluate(Context context, ClassDefinitionNode node)
    {
        if (!evaluatedClasses.contains(node))
        {
            currentClassDefinition = node;
            String className = node.name.name;
            String convertedClassName = "_" + className.replace('.', '_').replace(':', '_');
            bindingFunctionName = convertedClassName + "_bindingsSetup";

            String fullyQualifiedClassName = node.cframe.name.toString();
            srcTypeStack.push( TypeTable.convertName(fullyQualifiedClassName) );
            if (node.fexprs != null)
            {
                for (int i = 0, size = node.fexprs.size(); i < size; i++)
                {
                    FunctionCommonNode functionCommon = node.fexprs.get(i);
                    functionCommon.evaluate(context, this);
                }
            }
            srcTypeStack.pop();

            if (dataBindingInfo != null)
            {
                dataBindingInfo.setBindingExpressions(bindingExpressions);
                dataBindingInfo.setClassName(fullyQualifiedClassName);
                dataBindingInfoList.add(dataBindingInfo);
                dataBindingInfo = null;
            }

            evaluatedClasses.add(node);
            currentClassDefinition = null;
        }

        return null;
    }

    public Value evaluate(Context context, ConditionalExpressionNode node)
    {
        if (node.condition != null)
        {
            node.condition.evaluate(context, this);
        }

        if (insideBindingsSetupFunction)
        {
            watcherList = new LinkedList<Watcher>();
        }

        if (node.thenexpr != null)
        {
            node.thenexpr.evaluate(context, this);
        }

        if (insideBindingsSetupFunction)
        {
            watcherList = new LinkedList<Watcher>();
        }

        if (node.elseexpr != null)
        {
            node.elseexpr.evaluate(context, this);
        }

        if (insideBindingsSetupFunction)
        {
            watcherList = new LinkedList<Watcher>();
        }

        return null;
    }

    public Value evaluate(Context context, FunctionCommonNode functionCommon)
    {
        if (functionCommon.identifier != null)
        {
            if (functionCommon.identifier.name.equals(bindingFunctionName))
            {
                insideBindingsSetupFunction = true;
                dataBindingInfo = new DataBindingInfo( NodeMagic.getImports(currentClassDefinition.imported_names) );

                for (int i = 0; i < functionCommon.body.items.size(); i++)
                {
                    Node item1 = functionCommon.body.items.get(i);

                    // Skip over the DocCommentNodes, which are generated when asdoc is
                    // enabled, VariableDefinitionNodes, and ReturnStatementNodes.
                    if (item1 instanceof ExpressionStatementNode)
                    {
                        ExpressionStatementNode expressionStatement1 = (ExpressionStatementNode) item1;
                        ListNode list1 = (ListNode) expressionStatement1.expr;
                        MemberExpressionNode memberExpression1 = (MemberExpressionNode) list1.items.get(0);
                        SetExpressionNode setExpression = (SetExpressionNode) memberExpression1.selector;
                        Node item2 = setExpression.args.items.get(0);

                        // Skip over LiteralArrayNodes, like the right hand side of the
                        // following:
                        //
                        //   var result:Array = [];
                        //
                        if (item2 instanceof MemberExpressionNode)
                        {
                            MemberExpressionNode memberExpression2 = (MemberExpressionNode) item2;

                            if (memberExpression2.selector instanceof CallExpressionNode)
                            {
                                CallExpressionNode callExpression = (CallExpressionNode) memberExpression2.selector;
                                Node arg1 = callExpression.args.items.get(1);
                                currentBindingExpression = bindingExpressions.get(bindingId++);
                                watcherList = new LinkedList<Watcher>();

                                if (arg1 instanceof FunctionCommonNode)
                                {
                                    FunctionCommonNode srcFunctionCommon = (FunctionCommonNode) arg1;
                                    TypeExpressionNode typeExpression = (TypeExpressionNode) srcFunctionCommon.signature.result;
                                    List<Node> items = srcFunctionCommon.body.items;
                                    int size = items.size();

                                    // For a destination of type String, we generate something like:
                                    //
                                    //   var result:* = (name);
                                    //   return (result == undefined ? null : String(result));
                                    //
                                    // so we want to ignore the last two ReturnStatementNodes (yes,
                                    // there are two) and analyze the node before them.  Otherwise,
                                    // we generate something like:
                                    //
                                    //   return (name);
                                    //
                                    // so we want to ignore only the last ReturnStatementNode.
                                    //
                                    if ((typeExpression != null) && isArrayOrString(typeExpression))
                                    {
                                        ExpressionStatementNode expressionStatement3 = (ExpressionStatementNode) items.get(size - 3);
                                        ListNode list2 = (ListNode) expressionStatement3.expr;
                                        MemberExpressionNode memberExpression4 = (MemberExpressionNode) list2.items.get(0);
                                        SetExpressionNode setExpression3 = (SetExpressionNode) memberExpression4.selector;
                                        setExpression3.args.items.get(0).evaluate(context, this);
                                    }
                                    else
                                    {
                                        ReturnStatementNode returnStatement = (ReturnStatementNode) items.get(size - 2);
                                        returnStatement.expr.evaluate(context, this);
                                    }
                                }
                                else if (arg1 instanceof LiteralNullNode)
                                {
                                    LiteralStringNode literalString = (LiteralStringNode) callExpression.args.items.get(4);
                                    String name = literalString.value;
                                    Watcher watcher = watchExpressionStringAsProperty(name);
                                    MultiName multiName = new MultiName(SymbolTable.publicNamespace, name);
                                    findEvents(context, name, multiName, arg1.pos(), watcher);
                                }
                                else
                                {
                                    assert false : arg1.getClass().getName();
                                }

                                insideXMLExpression = false;
                                xmlMember = null;
                            }
                        }
                    }
                }

                insideBindingsSetupFunction = false;
                makeSecondPass = true;
            }
        }

        return null;
    }

    public Value evaluate(Context context, GetExpressionNode node)
    {
        if (node.expr != null)
        {
            insideGetExpression = true;

            if (node.expr instanceof ArgumentListNode)
            {
                ArgumentListNode argumentList = (ArgumentListNode) node.expr;

                if (!insideRepeaterExpression)
                {
                    if (!insideXMLExpression && showBindingWarnings)
                    {
                        context.localizedWarning2(node.pos(), new UnableToDetectSquareBracketChanges());
                    }

                    if (!insideXMLExpression)
                    {
                        argumentListStack.push(argumentList);
                        watchExpressionArray();
                        argumentListStack.pop();
                    }

                    resetSet.add(argumentList);
                    node.expr.evaluate(context, this);
                }
            }
            else
            {
                node.expr.evaluate(context, this);
            }

            insideGetExpression = false;
        }
        return null;
    }

    public Value evaluate(Context context, IdentifierNode node)
    {
        if (insideBindingsSetupFunction && !node.name.equals("instanceIndices"))
        {
            watchExpression(context, node, new MultiName(SymbolTable.VISIBILITY_NAMESPACES, node.name));
        }

        return null;
    }

    public Value evaluate(Context context, InvokeNode node)
    {
        if (insideBindingsSetupFunction && insideXMLExpression)
        {
            // We get here when the data binding destination is something like:
            //
            //   xdata.(@id=='123456').@timestamp
            //
            // where xdata is an E4X expression.  The IdentifierNode for "id" is in
            // node.args and we want the XMLWatcher for "id" to be added as a child of the
            // PropertyWatcher for "xdata", so we signal to evaluate(Context,
            // ArgumentListNode) to skip initializing the watcherList.
            skipInitSet.add(node.args);
        }

        super.evaluate(context, node);

        return null;
    }

    public Value evaluate(Context context, LiteralNumberNode node)
    {
        if (insideBindingsSetupFunction && insideGetExpression)
        {
            watchExpressionArray();
        }

        return null;
    }

    public Value evaluate(Context context, WithStatementNode node)
    {
        if (node.expr != null)
        {
            node.expr.evaluate(context, this);
        }

        LinkedList<Watcher> savedWatcherList = watcherList;
        boolean normalWarningMode = showBindingWarnings;
        if (insideBindingsSetupFunction && !insideArrayExpression)
        {
            if (xmlMember != null)
            {
                Watcher watcher = watchExpressionStringAsXML(xmlMember.ref.name);
                if (watcher != null)
                {
                    String name = xmlMember.ref.name;
                    MultiName multiName = new MultiName(SymbolTable.VISIBILITY_NAMESPACES, name);
                    findEvents(context, name, multiName, xmlMember.pos(), watcher);
                }
                xmlMember = null;
            }
            if (insideXMLExpression)
            {
                watcherList = new LinkedList<Watcher>();
                showBindingWarnings = false;     // inside an e4x selector is the freakin' wild west..
            }
        }

        if (node.statement != null)
        {
            node.statement.evaluate(context, this);
        }
        if (insideBindingsSetupFunction && !insideArrayExpression && insideXMLExpression)
        {
            watcherList = savedWatcherList;
            showBindingWarnings = normalWarningMode;
        }

        return null;
    }

    public Value evaluate(Context context, MemberExpressionNode node)
    {
        if (insideBindingsSetupFunction && !insideRepeaterExpression && isRepeaterBase(node.base))
        {
            insideRepeaterExpression = true;
            node.base.evaluate(context, this);
            // Since currentItem wasn't cast, we have no way to know the type.
            srcTypeStack.push(null);
            node.selector.evaluate(context, this);
            srcTypeStack.pop();
            insideRepeaterExpression = false;
        }
        else if (insideBindingsSetupFunction && isRepeaterIndicesBase(node.base))
        {
            setupRepeaterWatchers(node);
        }
        else
        {
            int pushed = 0;
            boolean oldArrayExpression = insideArrayExpression;
            insideArrayExpression = node.isIndexedMemberExpression();

            if (node.base != null)
            {
                node.base.evaluate(context, this);

                if (insideBindingsSetupFunction && (!insideArrayExpression || insideXMLExpression))
                {
                    ReferenceValue ref = getRef(node);
                    pushSrcType(context, ref, node.base);
                    pushed++;
                }
            }

            // Figure out if this member expression is a static reference.
            // If it is, then don't bother evaluating any further, because
            // we can't watch statics for changes.
            boolean staticReference = false;

            if (insideBindingsSetupFunction)
            {
                ReferenceValue ref = node.ref;
                if (ref != null)
                {
                    if (isStaticReference(node.selector, ref))
                    {
                        staticReference = true;
                        srcTypeStack.push( TypeTable.convertName( ref.slot.getObjectValue().toString() ) );
                        pushed++;
                    }

                    String type = ref.getType(context).getName().toString();

                    if (type.equals("XML") || type.equals("XMLList"))
                    {
                        xmlMember = node;
                        insideXMLExpression = true;
                    }
                }
            }

            if ((node.selector != null) && !staticReference)
            {
                node.selector.evaluate(context, this);
            }

            insideArrayExpression = oldArrayExpression;

            for (int i = 0; i < pushed; i++)
            {
                srcTypeStack.pop();
            }
        }

        return null;
    }

    public Value evaluate(Context context, MetaDataNode node)
    {
        return null;
    }

    public Value evaluate(Context context, QualifiedIdentifierNode node)
    {
        if (insideBindingsSetupFunction)
        {
            QName qName = NodeMagic.getQName(node);
            Map<Integer, String> namespaces = currentBindingExpression.getNamespaces();

            if ((namespaces != null) && namespaces.values().contains(qName.getNamespace()))
            {
                MultiName multiName = new MultiName(SymbolTable.VISIBILITY_NAMESPACES, qName.getLocalPart());
                watchExpression(context, node, multiName);
            }
            else
            {
                watchExpression(context, node, qName);
            }
        }

        return null;
    }

    private void findEvents(Context context, String name, MultiName multiName, int pos, Watcher watcher)
    {
        String srcTypeName = null;

        if (watcher.isPartOfAnonObjectGraph())
        {
            srcTypeName = standardDefs.CLASS_OBJECTPROXY;
        }
        else if (! srcTypeStack.empty())
        {
            srcTypeName = srcTypeStack.peek();
        }

        Watcher parentWatcher = watcher.getParent();

        if ((parentWatcher != null) && parentWatcher.isOperation())
        {
            watcher.addChangeEvent("resultForBinding");
        }
        else if (srcTypeName != null)
        {
            AbcClass watchedClass = typeTable.getClass(srcTypeName);

            if (watchedClass != null)
            {
                if ( watchedClass.isSubclassOf(standardDefs.CLASS_OBJECTPROXY) )
                {
                    watcher.setPartOfAnonObjectGraph(true);
                }

                List metaData = watchedClass.getMetaData(BINDABLE, true);
                boolean foundEvents = addBindables(watcher, metaData);
                boolean foundSource = false;

                Variable variable = watchedClass.getVariable(multiName.getNamespace(), multiName.getLocalPart(), true);

                if (variable != null)
                {
                    metaData = variable.getMetaData(BINDABLE);
                    foundEvents = addBindables(watcher, metaData) || foundEvents;
                    metaData = variable.getMetaData(CHANGE_EVENT);
                    foundEvents = addChangeEvents(watcher, metaData) || foundEvents;
                    metaData = variable.getMetaData(NON_COMMITTING_CHANGE_EVENT);
                    foundEvents = addNonCommittingChangeEvents(watcher, metaData) || foundEvents;

                    Attributes attributes = variable.getAttributes();

                    // Object has a public static const variable names "length", which is
                    // some legacy compatibility crap left over from EMCA script 262, so
                    // we ignore it.
                    if ((attributes != null) && attributes.hasConst() &&
                        !(multiName.getLocalPart().equals("length") &&
                          variable.getDeclaringClass().getName().equals(SymbolTable.OBJECT)))
                    {
                        // We didn't really find any events, but we want
                        // to follow the same code path below as if we did.
                        foundEvents = true;
                        //    TODO will this ever be something besides a PropertyWatcher?
                        if (watcher instanceof PropertyWatcher)
                        {
                            ((PropertyWatcher) watcher).suppress();
                        }
                    }

                    // See comment above.
                    if ((attributes != null) &&
                        !(multiName.getLocalPart().equals("length") &&
                          variable.getDeclaringClass().getName().equals(SymbolTable.OBJECT)))
                    {
                        checkForStaticProperty(attributes, watcher, srcTypeName);
                    }

                    foundSource = true;
                }

                if (!foundEvents)
                {
                    Method getter = watchedClass.getGetter(multiName.getNamespace(), multiName.getLocalPart(), true);

                    if (getter != null)
                    {
                        metaData = getter.getMetaData(BINDABLE);
                        foundEvents = addBindables(watcher, metaData) || foundEvents;
                        metaData = getter.getMetaData(CHANGE_EVENT);
                        foundEvents = addChangeEvents(watcher, metaData) || foundEvents;
                        metaData = getter.getMetaData(NON_COMMITTING_CHANGE_EVENT);
                        foundEvents = addNonCommittingChangeEvents( watcher, metaData) || foundEvents;

                        Attributes attributes = getter.getAttributes();
                        checkForStaticProperty(attributes, watcher, srcTypeName);

                        foundSource = true;
                    }

                    Method setter = watchedClass.getSetter(multiName.getNamespace(), multiName.getLocalPart(), true);

                    if (setter != null)
                    {
                        metaData = setter.getMetaData(BINDABLE);
                        foundEvents = addBindables(watcher, metaData) || foundEvents;
                        metaData = setter.getMetaData(CHANGE_EVENT);
                        foundEvents = addChangeEvents(watcher, metaData) || foundEvents;
                        metaData = setter.getMetaData(NON_COMMITTING_CHANGE_EVENT);
                        foundEvents = addNonCommittingChangeEvents(watcher, metaData) || foundEvents;

                        Attributes attributes = setter.getAttributes();
                        checkForStaticProperty(attributes, watcher, srcTypeName);

                        foundSource = true;
                    }
                    else
                    {
                        if (getter != null)
                        {
                            //    getters without setters are de facto const, use same bypass as above for const vars
                            foundEvents = true;
                        }
                    }
                }

                if (!foundSource)
                {
                    Method function = watchedClass.getMethod(multiName.getNamespace(), multiName.getLocalPart(), true);

                    if (function != null)
                    {
                        metaData = function.getMetaData(BINDABLE);
                        foundEvents = 
                        foundEvents = addBindables(watcher, metaData) || foundEvents;
                        metaData = function.getMetaData(CHANGE_EVENT);
                        foundEvents = addChangeEvents(watcher, metaData) || foundEvents;
                        metaData = function.getMetaData(NON_COMMITTING_CHANGE_EVENT);
                        foundEvents = addNonCommittingChangeEvents(watcher, metaData) || foundEvents;
                        foundSource = true;

                        if (!foundEvents && callExpressionStack.isEmpty())
                        {
                            foundEvents = true;
                            //    TODO will this ever be something besides a PropertyWatcher?
                            if (watcher instanceof PropertyWatcher)
                            {
                                ((PropertyWatcher)watcher).suppress();
                            }
                        }
                    }
                }

                if ((!foundSource) && watchedClass.isSubclassOf(standardDefs.CLASS_ABSTRACTSERVICE))
                {
                    watcher.setOperation(true);
                }
                else if (!foundEvents &&
                         !(watcher instanceof FunctionReturnWatcher) &&
                         !(watcher instanceof XMLWatcher) &&
                         !watcher.isOperation())
                {
                    /***
                     * NOTE: when we've failed to find change events for properties of untyped or Object-typed parents, we go
                     * ahead and generate code to create a runtime PropertyWatcher with no change events specified. The lack
                     * of change events tells the runtime PW to introspect RTTI to discover change events associated with the
                     * actual type of the actual value being assigned to the property.
                     * OTOH for strongly-typed properties, we still require change events to be reachable at compile time.
                     */
                    if (!(watchedClass.getName().equals(SymbolTable.OBJECT) || watchedClass.getName().equals(SymbolTable.NOTYPE)))
                    {
                        //    TODO do we still want this to be configurable?
                        if (showBindingWarnings)
                        {
                            context.localizedWarning2(pos, new UnableToDetectChanges(name));
                        }

                        //    TODO will this ever be something besides a PropertyWatcher?
                        if (watcher instanceof PropertyWatcher)
                        {
                            ((PropertyWatcher)watcher).suppress();
                        }
                    }
                }
            }
        }
    }

    public List<DataBindingInfo> getDataBindingInfoList()
    {
        return dataBindingInfoList;
    }

    private ReferenceValue getRef(MemberExpressionNode memberExpression)
    {
        ReferenceValue ref = null;

        if (memberExpression.base instanceof CallExpressionNode)
        {
            CallExpressionNode base = (CallExpressionNode) memberExpression.base;
            ref = base.ref;
        }
        else if (memberExpression.base instanceof MemberExpressionNode)
        {
            MemberExpressionNode base = (MemberExpressionNode) memberExpression.base;
            ref = base.ref;

            if ((ref == null) || (ref.slot == null))
            {
                ref = getRef(base);
            }
        }
        else if (memberExpression.base instanceof GetExpressionNode)
        {
            GetExpressionNode base = (GetExpressionNode) memberExpression.base;
            ref = base.ref;
        }
        else if (memberExpression.base instanceof ListNode)
        {
            ListNode list = (ListNode) memberExpression.base;
            Node node = list.items.get(0);

            if (node instanceof BinaryExpressionNode)
            {
                BinaryExpressionNode binaryExpression = (BinaryExpressionNode) node;

                if (binaryExpression.op == Tokens.AS_TOKEN)
                {
                    if ((binaryExpression.rhs != null) && (binaryExpression.rhs instanceof MemberExpressionNode))
                    {
                        MemberExpressionNode memberExpressionNode = (MemberExpressionNode) binaryExpression.rhs;
                        ref = memberExpressionNode.ref;
                    }
                }
            }
        }

        return ref;
    }

    private boolean isRepeaterBase(Node node)
    {
        boolean result = false;

        if ((node != null) && (node instanceof MemberExpressionNode))
        {
            MemberExpressionNode memberExpression = (MemberExpressionNode) node;

            if (memberExpression.base != null)
            {
                result = isRepeaterBase(memberExpression.base);
            }
            else if (memberExpression.selector instanceof GetExpressionNode)
            {
                GetExpressionNode getExpression = (GetExpressionNode) memberExpression.selector;

                if (getExpression.expr instanceof IdentifierNode)
                {
                    IdentifierNode identifier = (IdentifierNode) getExpression.expr;

                    if (currentBindingExpression.getRepeaterLevel(identifier.name) >= 0)
                    {
                        result = true;
                    }
                }
            }
        }

        return result;
    }

    private boolean isRepeaterIndicesBase(Node node)
    {
        boolean result = false;

        if ((node != null) && (node instanceof MemberExpressionNode))
        {
            MemberExpressionNode memberExpression = (MemberExpressionNode) node;

            if (memberExpression.base != null)
            {
                result = isRepeaterIndicesBase(memberExpression.base);
            }
            else if (memberExpression.selector instanceof GetExpressionNode)
            {
                GetExpressionNode getExpression = (GetExpressionNode) memberExpression.selector;

                if (getExpression.expr instanceof IdentifierNode)
                {
                    IdentifierNode identifier = (IdentifierNode) getExpression.expr;

                    if (identifier.name.equals("repeaterIndices"))
                    {
                        result = true;
                    }
                }
            }
        }

        return result;
    }

    private boolean isStaticReference(SelectorNode selector, ReferenceValue referenceValue)
    {
        // Note: With a static variable reference, the selector will be a GetExpression
        // and the slot will be a VariableSlot.  With a static function call, the selector
        // will be a CallExpression and the slot will be a MethodSlot.  With a function
        // call that returns an instance of type Class, the selector will be a
        // CallExpression and the slot will be a MethodSlot.  With a cast/type conversion,
        // the selector will be a CallExpression and the slot will be a VariableSlot.
        return (((selector == null) || (selector instanceof GetExpressionNode)) &&
                (referenceValue != null) &&
                (referenceValue.slot != null) &&
                (referenceValue.slot instanceof VariableSlot) &&
                (referenceValue.slot.getType() != null) &&
                (referenceValue.slot.getType().getName().ns.toString().equals("")) &&
                (referenceValue.slot.getType().getName().name.equals("Class")) &&
                (referenceValue.slot.getObjectValue() != null));
    }

    public boolean isArrayOrString(TypeExpressionNode typeExpression)
    {
        boolean result = false;

        if (typeExpression.expr instanceof MemberExpressionNode)
        {
            MemberExpressionNode memberExpression = (MemberExpressionNode) typeExpression.expr;

            if (memberExpression.selector instanceof GetExpressionNode)
            {
                GetExpressionNode getExpression = (GetExpressionNode) memberExpression.selector;

                if (getExpression.expr instanceof IdentifierNode)
                {
                    IdentifierNode identifier = (IdentifierNode) getExpression.expr;

                    if (identifier.name.equals("Array") ||
                        identifier.name.equals("String"))
                    {
                        result = true;
                    }
                }
            }
        }

        return result;
    }

    public boolean makeSecondPass()
    {
        return makeSecondPass;
    }

    private void pushSrcType(Context context, ReferenceValue ref, Node base)
    {
        if ((ref != null) && (ref.slot != null))
        {
            if ((ref.slot.getObjectValue() != null) && (!ref.slot.getObjectValue().toString().equals("")))
            {
                srcTypeStack.push( TypeTable.convertName( ref.slot.getObjectValue().toString() ) );
            }
            else if ((ref.getType(context) != null) && (!ref.getType(context).getName().toString().equals("")))
            {
                srcTypeStack.push( TypeTable.convertName( ref.getType(context).getName().toString() ) );
            }
            else if ((ref.slot.getType() != null) && (!ref.slot.getType().getName().toString().equals("")))
            {
                srcTypeStack.push( TypeTable.convertName( ref.slot.getType().getName().toString() ) );
            }
            else
            {
                srcTypeStack.push(null);
            }
        }
        else if (base instanceof ThisExpressionNode)
        {
            srcTypeStack.push( srcTypeStack.firstElement() );
        }
        else
        {
            srcTypeStack.push(null);
        }
    }

    private void setupRepeaterWatchers(MemberExpressionNode node)
    {
        Watcher repeaterWatcher;

        if (watcherList.isEmpty())
        {
            GetExpressionNode getExpression = (GetExpressionNode) node.selector;
            ArgumentListNode argumentList = (ArgumentListNode) getExpression.expr;
            LiteralNumberNode literalNumber = (LiteralNumberNode) argumentList.items.get(0);
            int level = literalNumber.numericValue.intValue();
            repeaterWatcher = new PropertyWatcher(currentWatcherId++, currentBindingExpression.getRepeaterId(level));
            watcherList.addLast(repeaterWatcher);
        }
        else
        {
            repeaterWatcher = watcherList.getLast();
        }

        PropertyWatcher dataProviderWatcher =
            (PropertyWatcher) repeaterWatcher.getChild("dataProvider");

        if (dataProviderWatcher == null)
        {
            dataProviderWatcher = new PropertyWatcher(currentWatcherId++, "dataProvider");
            repeaterWatcher.addChild(dataProviderWatcher);
        }

        watcherList.addLast(dataProviderWatcher);
        dataProviderWatcher.addBindingExpression(currentBindingExpression);
        RepeaterItemWatcher repeaterItemWatcher = new RepeaterItemWatcher(currentWatcherId++);
        repeaterItemWatcher.addBindingExpression(currentBindingExpression);
        dataProviderWatcher.addChild(repeaterItemWatcher);
        watcherList.addLast(repeaterItemWatcher);

        if (node.selector instanceof GetExpressionNode)
        {
            GetExpressionNode getExpression = (GetExpressionNode) node.selector;

            if (getExpression.expr instanceof ArgumentListNode)
            {
                skipInitSet.add((ArgumentListNode) getExpression.expr);
            }
            else
            {
                assert false : "Unexpected selector for repeaterIndices MemberExpressionNode";
            }
        }
        else
        {
            assert false : "Unexpected selector for repeaterIndices MemberExpressionNode";
        }
    }

    private Watcher watchIdentifier(String name)
    {
        Watcher watcher = null;

        int size = srcTypeStack.size();

        // Skip "Top Level" constants
        if (insideGetExpression &&
            (! ((size == 1) &&
                (name.equals("Infinity") ||
                 name.equals("-Infinity") ||
                 name.equals("NaN") ||
                 name.equals("undefined")))))
        {
            String src = srcTypeStack.peek();

            if ((!watcherList.isEmpty() && (watcherList.getLast() instanceof XMLWatcher)) ||
                ((src != null) && ((src.equals("XML") || src.equals("XMLList")))))
            {
                watcher = watchExpressionStringAsXML(name);
                xmlMember = null;
            }
            else
            {
                watcher = watchExpressionStringAsProperty(name);
            }
        }
        // Skip "Top Level" functions
        else if (!callExpressionStack.isEmpty() &&
                 (! ((size == 1) &&
                     (name.equals("Array") ||
                      name.equals("Boolean") ||
                      name.equals("decodeURI") ||
                      name.equals("decodeURIComponent") ||
                      name.equals("encodeURI") ||
                      name.equals("encodeURIComponent") ||
                      name.equals("escape") ||
                      name.equals("int") ||
                      name.equals("isFinite") ||
                      name.equals("isNaN") ||
                      name.equals("isXMLName") ||
                      name.equals("Number") ||
                      name.equals("Object") ||
                      name.equals("parseFloat") ||
                      name.equals("parseInt") ||
                      name.equals("trace") ||
                      name.equals("uint") ||
                      name.equals("unescape") ||
                      name.equals("XML") ||
                      name.equals("XMLList")))))
        {
            watcher = watchExpressionStringAsFunction(name);
        }

        return watcher;
    }

    private void watchExpression(Context context, IdentifierNode identifier, MultiName multiName)
    {
        String name = multiName.getLocalPart();

        Watcher watcher = watchIdentifier(name);

        if (watcher != null)
        {
            findEvents(context, name, multiName, identifier.pos(), watcher);
        }
    }

    private void watchExpression(Context context, QualifiedIdentifierNode qualifiedIdentifier, QName qName)
    {
        String name = qName.getNamespace() + "::" + qName.getLocalPart();

        Watcher watcher = watchIdentifier(name);

        if (watcher != null)
        {
            MultiName multiName = new MultiName(new String[] {qName.getNamespace()}, qName.getLocalPart());
            findEvents(context, name, multiName, qualifiedIdentifier.pos(), watcher);
        }
    }

    private void watchExpressionArray()
    {
        ArrayElementWatcher watcher = new ArrayElementWatcher(currentWatcherId++,
                                                              currentBindingExpression,
                                                              argumentListStack.peek());

        if (!watcherList.isEmpty())
        {
            Watcher parentWatcher = watcherList.getLast();
            watcher.setParentWatcher(parentWatcher);
            parentWatcher.addChild(watcher);

            if (parentWatcher.isPartOfAnonObjectGraph())
            {
                watcher.setPartOfAnonObjectGraph(true);
            }
        }

        watcherList.addLast(watcher);
    }

    private FunctionReturnWatcher watchExpressionStringAsFunction(String value)
    {
        FunctionReturnWatcher watcher = new FunctionReturnWatcher(currentWatcherId++,
                                                                  currentBindingExpression,
                                                                  value,
                                                                  argumentListStack.peek());

        if (!watcherList.isEmpty())
        {
            Watcher parentWatcher = watcherList.getLast();
            parentWatcher.addChild(watcher);
            watcher.setParentWatcher(parentWatcher);
        }
        else
        {
            //we want to get unique FunctionReturnWatchers in there
            dataBindingInfo.getRootWatchers().put(value + watcher.getId(), watcher);
        }

        String src = srcTypeStack.peek();

        // If the top of srcTypeStack is not the document's class and
        // the watcherList is empty, then we need to set the new
        // watcher's className.
        if ((srcTypeStack.size() > 1) &&
            (src != null) &&
            (srcTypeStack.firstElement() != src) &&
            watcherList.isEmpty())
        {
            watcher.setClassName(src);
        }

        watcherList.addLast(watcher);

        return watcher;
    }

    private XMLWatcher watchExpressionStringAsXML(String value)
    {
        XMLWatcher watcher;

        if (watcherList.isEmpty())
        {
            Map<String, Watcher> rootWatchers = dataBindingInfo.getRootWatchers();

            if (rootWatchers.containsKey(value))
            {
                // See bug 159393 for a test case that gets here.
                return null;
            }
            else
            {
                watcher = new XMLWatcher(currentWatcherId++, value);
                rootWatchers.put(value, watcher);
            }
        }
        else
        {
            Watcher parentWatcher = watcherList.getLast();
            Watcher child = parentWatcher.getChild(value);

            if (child instanceof XMLWatcher)
            {
                watcher = (XMLWatcher) child;
            }
            else
            {
                watcher = new XMLWatcher(currentWatcherId++, value);
                parentWatcher.addChild(watcher);
            }
        }

        String src = srcTypeStack.peek();

        // If the top of srcTypeStack is not the document's class and
        // the watcherList is empty, then we need to set the new
        // watcher's className.
        if ((srcTypeStack.size() > 1) &&
            (src != null) &&
            (srcTypeStack.firstElement() != src) &&
            watcherList.isEmpty())
        {
            watcher.setClassName(src);
        }

        watcherList.addLast(watcher);
        watcher.addBindingExpression(currentBindingExpression);

        return watcher;
    }

    private PropertyWatcher watchExpressionStringAsProperty(String value)
    {
        PropertyWatcher watcher = null;

        if (watcherList.isEmpty())
        {
            watcher = watchRootProperty(value);
        }
        else
        {
            Watcher parentWatcher = watcherList.getLast();
            watcher = parentWatcher.getChild(value);

            if (watcher == null)
            {
                watcher = new PropertyWatcher(currentWatcherId++, value);
                if (parentWatcher.isPartOfAnonObjectGraph())
                {
                    watcher.setPartOfAnonObjectGraph(true);
                }
                else if (!parentWatcher.shouldWriteChildren())
                {
                    watcher.setShouldWriteChildren(false);
                }
                parentWatcher.addChild(watcher);
            }
        }

        watcherList.addLast(watcher);
        watcher.addBindingExpression(currentBindingExpression);

        return watcher;
    }

    private PropertyWatcher watchRootProperty(String propertyName)
    {
        Map<String, Watcher> rootWatchers = dataBindingInfo.getRootWatchers();
        String key = propertyName;
        String srcType = srcTypeStack.peek();
        String className = null;

        // If the top of srcTypeStack is not the document's class,
        // then we need to set the new watcher's className.
        if ((srcTypeStack.size() > 1) &&
            (srcType != null) &&
            (srcTypeStack.firstElement() != srcType))
        {
            className = srcType;
            key = className + "." + propertyName;
        }

        PropertyWatcher result = (PropertyWatcher) rootWatchers.get(key);

        if (result == null)
        {
            Model destination = currentBindingExpression.getDestination();

            if ((destination != null) && (destination.getRepeaterLevel() > 1))
            {
                result = new RepeaterComponentWatcher(currentWatcherId++, propertyName, destination.getRepeaterLevel());
            }
            else
            {
                result = new PropertyWatcher(currentWatcherId++, propertyName);
            }

            if (className != null)
            {
                result.setClassName(className);
            }

            rootWatchers.put(key, result);
        }

        return result;
    }

    /**
     * CompilerMessages
     */
    public class UnableToDetectChanges extends CompilerMessage.CompilerWarning
    {
        private static final long serialVersionUID = -2290221228589394685L;
        public String name;

        public UnableToDetectChanges(String name)
        {
            this.name = name;
        }
    }

    public class UnableToDetectSquareBracketChanges extends CompilerMessage.CompilerWarning
    {

        private static final long serialVersionUID = 5936329878115867103L;
    }
}
