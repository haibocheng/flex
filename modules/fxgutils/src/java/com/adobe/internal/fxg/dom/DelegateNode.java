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

package com.adobe.internal.fxg.dom;

import com.adobe.fxg.FXGException;
import com.adobe.fxg.dom.FXGNode;

/**
 * A special kind of relationship node that delegates the addition of child
 * nodes to another parent node (instead of adding them to itself). An example
 * of a delegate node is the fill child of a Rect component. In the snippet
 * below, a SolidColor fill is added directly to the Rect - the parent of the
 * fill property node.
 * 
 * <pre>
 * &lt;Rect width="20" height="20"&gt;
 *     &lt;fill&gt;
 *         &lt;SolidColor color="#FFCC00" /&gt;
 *     &lt;/fill&gt;
 * &lt;/Rect&gt;
 * </pre>
 * 
 * @author Peter Farland
 */
public class DelegateNode implements FXGNode
{
    protected String name;
    protected FXGNode delegate;
    protected FXGNode documentNode;
    protected String uri;
    protected int startLine;
    protected int startColumn;
    protected int endLine;
    protected int endColumn;

    public void setName(String name)
    {
        this.name = name;
    }

    public String getNodeName()
    {
        return name;
    }

    public void setDelegate(FXGNode delegate)
    {
        this.delegate = delegate;
    }

    //--------------------------------------------------------------------------
    //
    // FXGNode Implementation
    //
    //--------------------------------------------------------------------------

    /**
     * Adds an FXG child node to the delegate node.
     * 
     * @param child - a child FXG node to be added to the delegate node.
     * @throws FXGException if the child is not supported by the delegate node.
     */
    public void addChild(FXGNode child)
    {
        delegate.addChild(child);
    }

    /**
     * Sets an FXG attribute on the delegate node.
     * 
     * @param name - the unqualified attribute name
     * @param value - the attribute value
     * @throws FXGException if the attribute name is not supported by the
     * delegate node.
     */
    public void setAttribute(String name, String value)
    {
        //Exception: Attribute {0} not supported by node {1}
        throw new FXGException(getStartLine(), getStartColumn(), "InvalidNodeAttribute", name, getNodeName());
    }

    /**
     * @return The root node of the FXG document.
     */
    public FXGNode getDocumentNode()
    {
        return documentNode;
    }

    /**
     * Establishes the root node of the FXG document containing this node.
     * @param root - the root node of the FXG document.
     */
    public void setDocumentNode(FXGNode root)
    {
        documentNode = root;
    }

    /**
     * return the namespace URI of this node.
     */
    public String getNodeURI()
    {
        return uri;
    }

    /**
     * @param uri - the namespace URI of this node.
     */
    public void setNodeURI(String uri)
    {
        this.uri = uri;
    }

    /**
     * @return the line on which the node declaration started.
     */
    public int getStartLine()
    {
        return startLine;
    }

    /**
     * @param line - the line on which the node declaration started.
     */
    public void setStartLine(int line)
    {
        startLine = line;
    }

    /**
     * @return - the column on which the node declaration started.
     */
    public int getStartColumn()
    {
        return startColumn;
    }

    /**
     * @param column - the line on which the node declaration started.
     */
    public void setStartColumn(int column)
    {
        startColumn = column;
    }

    /**
     * @return the line on which the node declaration ended.
     */
    public int getEndLine()
    {
        return endLine;
    }

    /**
     * @param line - the line on which the node declaration ended.
     */
    public void setEndLine(int line)
    {
        endLine = line;
    }

    /**
     * @return - the column on which the node declaration ended.
     */
    public int getEndColumn()
    {
        return endColumn;
    }

    /**
     * @param - column - the column on which the node declaration ended.
     */
    public void setEndColumn(int column)
    {
        endColumn = column;
    }
}
