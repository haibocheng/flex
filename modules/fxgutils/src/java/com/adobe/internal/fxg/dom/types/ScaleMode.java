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
 * The ScaleMode enumeration represents the type of scaling used painting a
 * stroke for a shape that has a transformation matrix.
 * 
 * The enumeration order is not significant to the SWF specification, but
 * simply matches the order specified for FXG.
 * 
 * <pre>
 *   0 = none
 *   1 = vertical
 *   2 = normal
 *   3 = horizontal
 * </pre>
 * 
 * @author Peter Farland
 */
public enum ScaleMode
{
    /**
     * The enum representing a 'none' stroke scale mode.
     */
    NONE,

    /**
     * The enum representing a 'vertical' stroke scale mode.
     */
    VERTICAL,

    /**
     * The enum representing a 'normal' (both horizontal and vertical) stroke
     * scale mode.
     */
    NORMAL,

    /**
     * The enum representing a 'horizontal' stroke scale mode.
     */
    HORIZONTAL;
}
