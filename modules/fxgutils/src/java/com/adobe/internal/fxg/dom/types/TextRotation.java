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
 * The TextRotation class.
 * 
 * <pre>
 *   0 = auto
 *   1 = rotate0
 *   2 = rotate90
 *   3 = rotate180
 *   4 = rotate270
 * </pre>
 * 
 * @author Min Plunkett
 */
public enum TextRotation
{
    /**
     * The enum representing an 'auto' TextRotation.
     */
    AUTO,

    /**
     * The enum representing an 'rotate0' TextRotation.
     */    
    ROTATE_0,
    
    /**
     * The enum representing an 'rotate90' TextRotation.
     */    
    ROTATE_90,
    
    /**
     * The enum representing an 'rotate180' TextRotation.
     */    
    ROTATE_180,
    
    /**
     * The enum representing an 'rotate270' TextRotation.
     */    
    ROTATE_270;
}
