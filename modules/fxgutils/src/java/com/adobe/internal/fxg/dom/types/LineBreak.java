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
 * The LineBreak enumeration determines how line wrapping occurs when rendering
 * text.
 * 
 * The enumeration order is not significant to the SWF specification, but
 * simply matches the order specified for FXG.
 * 
 * <pre>
 *   0 = toFit
 *   1 = explicit
 *   2 = inherit
 * </pre>
 * 
 * @author Peter Farland
 */
public enum LineBreak
{
    /**
     * The enum representing a 'toFit' line break type.
     */
    TOFIT,

    /**
     * The enum representing an 'explicit' line break type.
     */
    EXPLICIT,
    
    /**
     * The enum representing an 'inherit' line break type.
     */
    INHERIT;
}
