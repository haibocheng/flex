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
 * The InterpolationMethod enumeration determines how colors are interpolated
 * across a gradient.
 * 
 * The enumeration order is significant and matches the SWF specification for
 * the InterpolationMode property of the GRADIENT structure.
 * 
 * <pre>
 *   0 = rgb
 *   1 = linearRGB
 * </pre>
 * 
 * @author Peter Farland
 */
public enum InterpolationMethod
{
    /**
     * The enum representing an 'rgb' interpolation type.
     */
    RGB,

    /**
     * The enum representing a 'linearRGB' interpolation type.
     */
    LINEAR_RGB;
}
