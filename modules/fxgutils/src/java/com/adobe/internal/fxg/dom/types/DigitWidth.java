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
 * The DigitWidth class.
 * 
 * <pre>
 *   0 = default
 *   1 = proportional
 *   2 = tabular
 * </pre>
 * 
 * @author Min Plunkett
 */
public enum DigitWidth
{
    /**
     * The enum representing an 'default' DigitWidth.
     */
    DEFAULT,

    /**
     * The enum representing an 'proportional' DigitWidth.
     */    
    PROPORTIONAL,
    
    /**
     * The enum representing an 'tabular' DigitWidth.
     */
    TABULAR;
}
