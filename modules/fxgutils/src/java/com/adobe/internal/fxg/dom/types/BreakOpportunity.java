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
 * The BreakOpportunity class.
 * 
 * <pre>
 *   0 = auto
 *   1 = any
 *   2 = none
 *   3 = all
 * </pre>
 * 
 * @author Min Plunkett
 */
public enum BreakOpportunity
{
    /**
     * The enum representing an 'auto' BreakOpportunity.
     */
    AUTO,

    /**
     * The enum representing an 'any' BreakOpportunity.
     */    
    ANY,
    
    /**
     * The enum representing an 'none' BreakOpportunity.
     */
    NONE,

    /**
     * The enum representing an 'all' BreakOpportunity.
     */
    ALL;    
}
