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
 * The BevelType enumeration determines where on an object a bevel should be
 * placed.
 * 
 * The enumeration order is not significant to the SWF specification, but
 * simply matches the order specified for FXG.
 * 
 * <pre>
 *   0 = inner
 *   1 = outer
 *   2 = full
 * </pre>
 * 
 * @author Peter Farland
 */
public enum BevelType
{
    /**
     * The enum representing an 'inner' bevel type.
     */
    INNER,

    /**
     * The enum representing an 'outer' bevel type.
     */
    OUTER,

    /**
     * The enum representing a 'full' bevel type.
     */
    FULL;
}