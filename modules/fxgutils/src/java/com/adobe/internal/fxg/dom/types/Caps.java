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
 * The Caps enumeration represents the type of line cap to use when painting
 * strokes.
 * 
 * The enumeration order is significant and matches the SWF specification for
 * the StartCapStyle and EndCapStyle properties of the LINESTYLE2 structure.
 * 
 * <pre>
 *   0 = round
 *   1 = none
 *   2 = square
 * </pre>
 * 
 * @author Peter Farland
 */
public enum Caps
{
    /**
     * The enum representing a 'round' cap type.
     */
    ROUND,

    /**
     * The enum representing a 'none' cap type. No caps are drawn for the ends
     * of a stroke.
     */
    NONE,

    /**
     * The enum representing a 'square' cap type.
     */
    SQUARE;
}
