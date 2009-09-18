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
 * The SpreadMethod enumeration determines how linear gradients control the
 * colors for points that lie outside of the gradient vector.
 * 
 * The enumeration order is significant and matches the SWF specification for
 * the SpreadMode property of the GRADIENT structure.
 * 
 * <pre>
 *   0 = Pad Mode
 *   1 = Reflect Mode
 *   2 = Repeat Mode
 * </pre>
 * 
 * @author Peter Farland
 */
public enum SpreadMethod
{
    /**
     * The enum representing a 'pad' spread method.
     */
    PAD,

    /**
     * The enum representing a 'reflect' spread method.
     */
    REFLECT,

    /**
     * The enum representing a 'repeat' spread method.
     */
    REPEAT;
}
