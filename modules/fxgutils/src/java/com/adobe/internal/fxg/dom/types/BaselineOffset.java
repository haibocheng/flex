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
 * The BaselineOffset class. Underline value can be either a double or 
 * a BaselineOffsetAsEnum enum.
 * 
 * <pre>
 *   0 = auto
 *   1 = ascent
 *   2 = lineHeight 
 * </pre>
 * 
 * @author Min Plunkett
 */
public class BaselineOffset
{
    private double baselineOffsetAsDbl = 0.0;
    private BaselineOffsetAsEnum baselineOffsetAsEnum = null;
    
    /* The BaselineOffsetAsEnum class.
    * 
    * <pre>
    *   0 = auto
    *   1 = ascent
    *   2 = lineHeight
    * </pre>
    * 
    * @author Min Plunkett
    */
    public enum BaselineOffsetAsEnum
    {
        /**
         * The enum representing an 'auto' BaselineOffset.
         */
    	AUTO,

        /**
         * The enum representing an 'ascent' BaselineOffset.
         */
        ASCENT,
        
        /**
         * The enum representing an 'lineHeight' BaselineOffset.
         */
        LINEHEIGHT
    }
    
    private BaselineOffset()
    {    
    }
    
    /**
     * Create a new instance of BaselineOffset with value set as an enum.
     * @param baselineOffsetAsEnum - BaselineOffset value set as enum.
     * @return a new instance of BaselineOffset.
     */
    public static BaselineOffset newInstance(BaselineOffsetAsEnum baselineOffsetAsEnum)
    {
        BaselineOffset baselineOffset = new BaselineOffset();
        baselineOffset.baselineOffsetAsEnum = baselineOffsetAsEnum;
        return baselineOffset;
    }
    
    /**
     * Create a new instance of BaselineOffset with value set as a double.
     * @param baselineOffsetAsDbl - BaselineOffset value set as double.
     * @return a new instance of BaselineOffset.
     */
    public static BaselineOffset newInstance(double baselineOffsetAsDbl)
    {
        BaselineOffset baselineOffset = new BaselineOffset();
        baselineOffset.baselineOffsetAsDbl = baselineOffsetAsDbl;
        return baselineOffset;
    }  
    
    public boolean isBaselineOffsetAsEnum()
    {
        if (this.baselineOffsetAsEnum != null)
            return true;
        else
            return false;
    }
    
    public BaselineOffsetAsEnum getBaselineOffsetAsEnum()
    {
        return this.baselineOffsetAsEnum;
    }
    
    public double getBaselineOffsetAsDbl()
    {
        return this.baselineOffsetAsDbl;
    }
}
