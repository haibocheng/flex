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
package com.adobe.fxg;

import java.io.IOException;
import java.io.InputStream;

import com.adobe.fxg.dom.FXGNode;

/**
 * A FXGParser parses an InputStream for an FXG document and builds a custom
 * DOM. Custom FXGNodes can be registered to represent specific elements and
 * elements can also be marked as skipped prior to parsing .
 * 
 * @author Sujata Das
 */
public interface FXGParser
{
    /**
     * Parses an FXG document InputStream to produce an FXGNode based DOM.
     * 
     * @param stream - InputStream of the document to be parsed.
     * @return the root FXGNode of the DOM
     * @throws FXGException if an exception occurred while parsing.
     * @throws IOException if an exception occurred while reading the stream.
     */
    FXGNode parse(InputStream is) throws FXGException, IOException;

    /**
     * Parses an FXG document InputStream to produce an FXGNode based DOM.
     *
     * @param stream - InputStream of the document to be parsed.
     * @param documentName - the name of the FXG document which can be useful
     * for error reporting.
     * @return the root FXGNode of the DOM
     * @throws FXGException if an exception occurred while parsing.
     * @throws IOException if an exception occurred while reading the stream.
     */
    FXGNode parse(InputStream is, String documentName) throws FXGException, IOException;

    /**
     * Registers a custom FXGNode for a particular type of element encountered 
     * while parsing an FXG document.
     * 
     * This method must be called prior to parsing.
     * 
     * @param version - the FXG version of the FXG element 
     * @param uri - the namespace URI of the FXG element
     * @param localName - the local name of the FXG element
     * @param nodeClass - Class of an FXGNode implementation that will represent
     * an element in the DOM and process its attributes and child nodes during
     * parsing.
     */
    void registerElementNode(double version , String uri,  String localName, Class<? extends FXGNode> nodeClass);

    /**
     * Specifies that a particular element should be skipped while parsing an
     * FXG document. All of the element's attributes and child nodes will be
     * skipped.
     * 
     * Skipped elements must be registered prior to parsing.
     * 
     * @param version - the FXG version of the FXG element 
     * @param uri - the namespace URI of the FXG element to skip
     * @param localName - the name of the FXG element to skip
     */
    void skipElement(double version, String uri, String localName);
}
