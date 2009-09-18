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
 * The LigatureLevel class.
 * 
 * <pre>
 *   0 = minimum
 *   1 = common
 *   2 = uncommon
 *   3 = exotic
 * </pre>
 * 
 * @author Min Plunkett
 */
public enum LigatureLevel
{
    /**
     * The enum representing an 'minimum' LigatureLevel.
     */
    MINIMUM,

    /**
     * The enum representing an 'common' LigatureLevel.
     */    
    COMMON,
    
    /**
     * The enum representing an 'uncommon' LigatureLevel.
     */
    UNCOMMON,

    /**
     * The enum representing an 'exotic' LigatureLevel.
     */
    EXOTIC;    
}
