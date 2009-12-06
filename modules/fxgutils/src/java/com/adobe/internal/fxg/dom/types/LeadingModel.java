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
 * The LeadingModel class.
 * 
 * <pre>
 *   0 = auto
 *   1 = romanUp
 *   2 = ideographicTopUp
 *   3 = ascentDescentUp
 *   4 = ideographicTopDown
 *   5 = ideographicCenterDown
 *   6 = approximateTextField
 * </pre>
 * 
 * @author Min Plunkett
 */
public enum LeadingModel
{
    /**
     * The enum representing an 'auto' LeadingModel.
     */
    AUTO,

    /**
     * The enum representing an 'romanUp' LeadingModel.
     */    
    ROMANUP,
    
    /**
     * The enum representing an 'ideographicTopUp' LeadingModel.
     */
    IDEOGRAPHICTOPUP,  
    
    /**
     * The enum representing an 'ideographicCenterUp' LeadingModel.
     */
    IDEOGRAPHICCENTERUP,      
    
    /**
     * The enum representing an 'ascentDescentUp' LeadingModel.
     */
    ASCENTDESCENTUP,
    
    /**
     * The enum representing an 'ideographicTopDown' LeadingModel.
     */
    IDEOGRAPHICTOPDOWN,  
    
    /**
     * The enum representing an 'ideographicCenterDown' LeadingModel.
     */
    IDEOGRAPHICCENTERDOWN,
    
    /**
     * The enum representing an 'approximateTextField' LeadingModel.
     */
    APPROXIMATETEXTFIELD;
}
