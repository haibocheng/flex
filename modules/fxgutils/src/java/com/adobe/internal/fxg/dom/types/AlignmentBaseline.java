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
 * The AlignmentBaseline class.
 * 
 * <pre>
 *   0 = useDominantBaseline
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
public enum AlignmentBaseline
{
    /**
     * The enum representing an 'useDominantBaseline' AlignmentBaseline.
     */
    USEDOMINANTBASELINE,

    /**
     * The enum representing an 'roman' AlignmentBaseline.
     */    
    ROMAN,
    
    /**
     * The enum representing an 'ascent' AlignmentBaseline.
     */
    ASCENT, 
    
    /**
     * The enum representing an 'descent' AlignmentBaseline.
     */
    DESCENT,  
    
    /**
     * The enum representing an 'ideographicTop' AlignmentBaseline.
     */
    IDEOGRAPHICTOP,  
    
    /**
     * The enum representing an 'ideographicCenter' AlignmentBaseline.
     */
    IDEOGRAPHICCENTER,      
    
    /**
     * The enum representing an 'ideographicBottom' AlignmentBaseline.
     */
    IDEOGRAPHICBOTTOM;       
    
}
