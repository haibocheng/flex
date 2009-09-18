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
 * The FillMode class.
 * 
 * <pre>
 *   0 = clip
 *   1 = repeat
 *   2 = scale
 * </pre>
 * 
 * @author Min Plunkett
 * @author Sujata Das
 */
public enum FillMode
{
    /**
     * The enum representing an 'clip' FillMode.
     */
    CLIP,

    /**
     * The enum representing an 'repeat' FillMode.
     */    
    REPEAT,
    
    /**
     * The enum representing an 'scale' FillMode.
     */    
    SCALE;
}