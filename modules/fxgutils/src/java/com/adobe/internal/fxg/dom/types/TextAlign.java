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
 * The TextAlign class.
 * 
 * <pre>
 *   0 = start
 *   1 = end
 *   2 = left
 *   3 = center
 *   4 = right
 *   5 = justify
 * </pre>
 * 
 * @author Min Plunkett
 */
public enum TextAlign
{
    /**
     * The enum representing an 'start' TextAlign.
     */
    START,

    /**
     * The enum representing an 'end' TextAlign.
     */    
    END,
    
    /**
     * The enum representing an 'left' TextAlign.
     */
    LEFT,

    /**
     * The enum representing an 'center' TextAlign.
     */
    CENTER,   
    
    /**
     * The enum representing an 'right' TextAlign.
     */
    RIGHT, 
    
    /**
     * The enum representing an 'justify' TextAlign.
     */
    JUSTIFY;
}
