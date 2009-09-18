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
 * The Kerning enumeration determines whether font based kerning of character
 * pairs is used when rendering text.
 * 
 * The enumeration order is not significant to the SWF specification, but
 * simply matches the order specified for FXG.
 * 
 * <pre>
 *   0 = on
 *   1 = off
 *   2 = auto
 * </pre>
 * 
 * @author Peter Farland
 */
public enum Kerning
{
    /**
     * The enum representing an 'on' kerning type.
     */
    ON,

    /**
     * The enum representing an 'off' kerning type.
     */
    OFF,

    /**
     * The enum representing an 'auto' kerning type.
     */
    AUTO;
}
