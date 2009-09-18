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
 * The Joints enumeration represents the type of joint to use when painting
 * two connecting segments of a stroke.
 * 
 * The enumeration order is significant and matches the SWF specification for
 * the JoinStyle property of the LINESTYLE2 structure.
 * 
 * <pre>
 *   0 = round
 *   1 = bevel
 *   2 = miter
 * </pre>
 * 
 * @author Peter Farland
 */
public enum Joints
{
    /**
     * The enum representing a 'round' joint type.
     */
    ROUND,

    /**
     * The enum representing a 'bevel' joint type.
     */
    BEVEL,

    /**
     * The enum representing a 'miter' joint type.
     */
    MITER;
}
