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
 * The MaskType enumeration controls how a mask layer will behave with respect
 * to the target graphical layers.
 * 
 * The enumeration order is not significant to the SWF specification, but
 * simply matches the order specified for FXG.
 * 
 * <pre>
 *   0 = clip
 *   1 = alpha
 *   2 = luminosity
 * </pre>
 * 
 * 
 * 
 * @author Peter Farland
 * @author Sujata Das
 */
public enum MaskType
{
    /**
     * The enum representing a 'clip' mask type.
     */
    CLIP,

    /**
     * The enum representing an 'alpha' mask type.
     */
    ALPHA,
    
    /**
     * The enum representing an 'luminosity' mask type.
     */
    LUMINOSITY;
    
    
}
