////////////////////////////////////////////////////////////////////////////////
//
//  ADOBE SYSTEMS INCORPORATED
//  Copyright 2009 Adobe Systems Incorporated
//  All Rights Reserved.
//
//  NOTICE: Adobe permits you to use, modify, and distribute this file
//  in accordance with the terms of the license agreement accompanying it.
//
////////////////////////////////////////////////////////////////////////////////

package com.adobe.internal.fxg.dom.types;

/**
 * The VerticalAlign class.
 * 
 * <pre>
 *   0 = top
 *   1 = middle
 *   2 = bottom
 *   3 = justify
 *   4 = inherit
 * </pre>
 * 
 * @author Min Plunkett
 */
public enum VerticalAlign
{
    /**
     * The enum representing an 'top' VerticalAlign.
     */
    TOP,

    /**
     * The enum representing an 'middle' VerticalAlign.
     */    
    MIDDLE,

    /**
     * The enum representing an 'bottom' VerticalAlign.
     */    
    BOTTOM,
    
    /**
     * The enum representing an 'justify' VerticalAlign.
     */    
    JUSTIFY,
    
    /**
     * The enum representing an 'inherit' VerticalAlign.
     */    
    INHERIT;
}
