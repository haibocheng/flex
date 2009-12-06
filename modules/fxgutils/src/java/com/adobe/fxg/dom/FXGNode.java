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

package com.adobe.fxg.dom;

/**
 * Implementations of FXGNode represent a node in the DOM of an FXG document.
 * 
 * @author Peter Farland
 */
public interface FXGNode
{
    /**
     * @return The XML element name of this node.
     */
    String getNodeName();

    /**
     * @return The namespace URI of this node.
     */
    String getNodeURI();

    /**
     * Adds an FXG child node to this node.
     * 
     * @param child - a child FXG node to be added to this node.
     */
    void addChild(FXGNode child);

    /**
     * @return The root node of the FXG document.
     */
    FXGNode getDocumentNode();

    /**
     * Establishes the root node of the FXG document containing this node.
     * @param root - the root node of the FXG document.
     */
    void setDocumentNode(FXGNode root);
    
    /**
     * Sets an FXG attribute on this node.
     * 
     * @param name - the unqualified attribute name
     * @param value - the attribute value
     */
    void setAttribute(String name, String value);


    //--------------------------------------------------------------------------
    //
    //  Document Line Information (Used in Error Reporting)
    //
    //--------------------------------------------------------------------------

    /**
     * @return the line on which the node declaration started.
     */
    int getStartLine();

    /**
     * @param line - the line on which the node declaration started.
     */
    void setStartLine(int line);

    /**
     * @return - the column on which the node declaration started.
     */
    int getStartColumn();

    /**
     * @param column - the line on which the node declaration started.
     */
    void setStartColumn(int column);

    /**
     * @return the line on which the node declaration ended.
     */
    int getEndLine();

    /**
     * @param line - the line on which the node declaration ended.
     */
    void setEndLine(int line);

    /**
     * @return - the column on which the node declaration ended.
     */
    int getEndColumn();

    /**
     * @param - column - the column on which the node declaration ended.
     */
    void setEndColumn(int column);
}
