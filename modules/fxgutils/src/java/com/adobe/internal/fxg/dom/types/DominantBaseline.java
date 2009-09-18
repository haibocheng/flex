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
 * The DominantBaseline class.
 * 
 * <pre>
 *   0 = auto
 *   1 = roman
 *   2 = ascent
 *   3 = descent
 *   4 = ideographicTop
 *   5 = ideographicCenter
 *   6 = ideographicBottom
 * </pre>
 * 
 * @author Min Plunkett
 */
public enum DominantBaseline
{
    /**
     * The enum representing an 'auto' DominantBaseline.
     */
    AUTO,

    /**
     * The enum representing an 'roman' DominantBaseline.
     */    
    ROMAN,
    
    /**
     * The enum representing an 'ascent' DominantBaseline.
     */
    ASCENT, 
    
    /**
     * The enum representing an 'descent' DominantBaseline.
     */
    DESCENT,  
    
    /**
     * The enum representing an 'ideographicTop' DominantBaseline.
     */
    IDEOGRAPHICTOP,  
    
    /**
     * The enum representing an 'ideographicCenter' DominantBaseline.
     */
    IDEOGRAPHICCENTER,      
    
    /**
     * The enum representing an 'ideographicBottom' DominantBaseline.
     */
    IDEOGRAPHICBOTTOM;       
    
}
