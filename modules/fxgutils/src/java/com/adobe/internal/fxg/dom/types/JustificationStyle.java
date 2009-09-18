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
 * The JustificationStyle class.
 * 
 * <pre>
 *   0 = auto
 *   1 = prioritizeLeastAdjustment
 *   2 = pushInKinsoku
 *   3 = pushOutOnly
 * </pre>
 * 
 * @author Min Plunkett
 */
public enum JustificationStyle
{
    /**
     * The enum representing an 'auto' JustificationStyle.
     */
    AUTO,

    /**
     * The enum representing an 'prioritizeLeastAdjustment' JustificationStyle.
     */    
    PRIORITIZELEASTADJUSTMENT,

    /**
     * The enum representing an 'pushInKinsoku' JustificationStyle.
     */    
    PUSHINKINSOKU,
    
    /**
     * The enum representing an 'pushOutOnly' JustificationStyle.
     */    
    PUSHOUTONLY;
}
