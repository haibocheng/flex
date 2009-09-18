// //////////////////////////////////////////////////////////////////////////////
//
// ADOBE SYSTEMS INCORPORATED
// Copyright 2009 Adobe Systems Incorporated
// All Rights Reserved.
//
// NOTICE: Adobe permits you to use, modify, and distribute this file
// in accordance with the terms of the license agreement accompanying it.
//
// //////////////////////////////////////////////////////////////////////////////

package com.adobe.internal.fxg.sax;

import java.util.Map;
import java.util.Set;

import com.adobe.fxg.FXGVersion;
import com.adobe.fxg.dom.FXGNode;

/**
 * A FXGVersionHandler defines interfaces to encapsulate FXG version specific
 * information. It allows the scanner to handle different versions of fxg files
 * by swapping different FXGVersionHandlers at runtime depending on the fxg
 * version of the input file.
 * 
 * @author Sujata Das
 */
public interface FXGVersionHandler
{
    /**
     * @return the FXGVersion of the FXGVersionHandler
     */
    FXGVersion getVersion();

    /**
     * @param URI - namespace for the elements
     * @return a Set<String> of the elements that are registered to be skipped
     *         by the scanner
     */
    Set<String> getSkippedElements(String URI);

    /**
     * @param URI
     * @return a Map<String, Class<? extends FXGNode>> that maps element names
     *         to Class that handles the element.
     */
    Map<String, Class<? extends FXGNode>> getElementNodes(String URI);

    /**
     * Registers names of elements that are to be skipped by the scanner
     * 
     * @param URI - namespace for the elements
     * @param skippedElements - Set of Strings that specify elements names that
     *        are to be scanned by scanner
     */
    void registerSkippedElements(String URI, Set<String> skippedElements);

    /**
     * Registers mapping for the scanner to process elements and Classes that
     * handle the elements
     * 
     * @param URI - namespace for the elements
     * @param elementNodes - a Map containing mapping from elements names to
     *        Classes that handle the elements.
     */
    void registerElementNodes(String URI,
            Map<String, Class<? extends FXGNode>> elementNodes);

}
