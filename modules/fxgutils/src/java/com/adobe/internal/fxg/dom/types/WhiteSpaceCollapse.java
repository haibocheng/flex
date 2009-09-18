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

package com.adobe.internal.fxg.dom.types;

/**
 * The WhiteSpaceCollapse enumeration determines how whitespace is handled
 * in text formatting.
 * 
 * Collapse converts line feeds, new lines, and tabs to spaces and collapses
 * adjacent spaces to one. Leading and trailing whitespace is trimmed. Preserve
 * passes whitespace through unchanged.
 * 
 * <pre>
 *   0 = preserve
 *   1 = collapse
 * </pre>
 * 
 * @author Peter Farland
 */
public enum WhiteSpaceCollapse
{
    /**
     * The enum representing 'preserve' whitespace.
     */
    PRESERVE,

    /**
     * The enum representing 'collapse' whitespace.
     */
    COLLAPSE;
}
