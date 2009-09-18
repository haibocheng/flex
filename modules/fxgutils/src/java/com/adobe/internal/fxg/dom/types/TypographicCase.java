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
 * The TypographicCase class.
 * 
 * <pre>
 *   0 = default
 *   1 = capsToSmallCaps
 *   2 = uppercase
 *   3 = lowercase
 *   4 = lowercaseToSmallCaps
 * </pre>
 * 
 * @author Min Plunkett
 */
public enum TypographicCase
{
    /**
     * The enum representing an 'default' TypographicCase.
     */
    DEFAULT,

    /**
     * The enum representing an 'capsToSmallCaps' TypographicCase.
     */    
    CAPSTOSMALLCAPS,
    
    /**
     * The enum representing an 'uppercase' TypographicCase.
     */
    UPPERCASE,

    /**
     * The enum representing an 'lowercase' TypographicCase.
     */
    LOWERCASE,   
    
    /**
     * The enum representing an 'lowercaseToSmallCaps' TypographicCase.
     */
    LOWERCASETOSMALLCAPS;
}
