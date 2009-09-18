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
 * The BaselineShift class. Underline value can be either a double or 
 * a BaselineShiftAsEnum enum.
 * 
 * <pre>
 *   0 = superscript
 *   1 = subscript
 * </pre>
 * 
 * @author Min Plunkett
 */
public class BaselineShift
{
    private double baselineShiftAsDbl = 0.0;
    private BaselineShiftAsEnum baselineShiftAsEnum = null;
    
    /* The BaselineShiftAsEnum class.
    * 
    * <pre>
    *   0 = superscript
    *   1 = subscript
    * </pre>
    * 
    * @author Min Plunkett
    */
    public enum BaselineShiftAsEnum
    {
        /**
         * The enum representing an 'superscript' BaselineShift.
         */
        SUPERSCRIPT,

        /**
         * The enum representing an 'subscript' BaselineShift.
         */
        SUBSCRIPT;
    }
    
    private BaselineShift()
    {    
    }
    
    /**
     * Create a new instance of BaselineShift with value set as an enum.
     * @param baselineShiftAsEnum - BaselineShift value set as enum.
     * @return a new instance of BaselineShift.
     */
    public static BaselineShift newInstance(BaselineShiftAsEnum baselineShiftAsEnum)
    {
        BaselineShift baselineShift = new BaselineShift();
        baselineShift.baselineShiftAsEnum = baselineShiftAsEnum;
        return baselineShift;
    }
    
    /**
     * Create a new instance of BaselineShift with value set as a double.
     * @param baselineShiftAsDbl - BaselineShift value set as double.
     * @return a new instance of BaselineShift.
     */
    public static BaselineShift newInstance(double baselineShiftAsDbl)
    {
        BaselineShift baselineShift = new BaselineShift();
        baselineShift.baselineShiftAsDbl = baselineShiftAsDbl;
        return baselineShift;
    }  
    
    public boolean isBaselineShiftAsEnum()
    {
        if (this.baselineShiftAsEnum != null)
            return true;
        else
            return false;
    }
    
    public BaselineShiftAsEnum getBaselineShiftAsEnum()
    {
        return this.baselineShiftAsEnum;
    }
    
    public double getBaselineShiftAsDbl()
    {
        return this.baselineShiftAsDbl;
    }
}
