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
 * The ResizeMode class.
 * 
 * <pre>
 *   0 = noScale
 *   1 = repeat
 *   2 = scale
 * </pre>
 * 
 * @author Min Plunkett
 */
public enum ResizeMode
{
    /**
     * The enum representing an 'noScale' ResizeMode.
     */
    NOSCALE,

    /**
     * The enum representing an 'repeat' ResizeMode.
     */    
    REPEAT,
    
    /**
     * The enum representing an 'scale' ResizeMode.
     */    
    SCALE;
}