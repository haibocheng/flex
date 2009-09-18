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
 * The DigitCase class.
 * 
 * <pre>
 *   0 = default
 *   1 = lining
 *   2 = oldStyle
 * </pre>
 * 
 * @author Min Plunkett
 */
public enum DigitCase
{
    /**
     * The enum representing an 'default' DigitCase.
     */
    DEFAULT,

    /**
     * The enum representing an 'lining' DigitCase.
     */    
    LINING,
    
    /**
     * The enum representing an 'oldStyle' DigitCase.
     */
    OLDSTYLE;
}
