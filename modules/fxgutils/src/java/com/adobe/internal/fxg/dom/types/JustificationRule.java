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
 * The JustificationRule class.
 * 
 * <pre>
 *   0 = auto
 *   1 = space
 *   2 = eastAsian
 * </pre>
 * 
 * @author Min Plunkett
 */
public enum JustificationRule
{
    /**
     * The enum representing an 'auto' JustificationRule.
     */
    AUTO,

    /**
     * The enum representing an 'space' JustificationRule.
     */    
    SPACE,

    /**
     * The enum representing an 'eastAsian' JustificationRule.
     */    
    EASTASIAN;
}
