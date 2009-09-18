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
 * The NumberPercentAuto class. Underline value can be either a double or 
 * a NumberPercentAutoAsEnum enum.
 * 
 * <pre>
 *   0 = auto
 * </pre>
 * 
 * @author Min Plunkett
 */
public class NumberPercentAuto
{
    private double numberPercentAutoAsDbl = 0.0;
    private NumberPercentAutoAsEnum numberPercentAutoAsEnum = null;
    
    
    /* The NumberPercentAutoAsEnum class.
    * 
    * <pre>
    *   0 = auto
    * </pre>
    * 
    * @author Min Plunkett
    */
    public enum NumberPercentAutoAsEnum
    {
        /**
         * The enum representing an 'auto' NumberPercentAuto.
         */
        AUTO;
    }
    
    private NumberPercentAuto()
    {    
    }
    
    /**
     * Create a new instance of NumberPercentAuto with value set as an enum.
     * @param NumberPercentAutoAsEnum - NumberPercentAuto value set as enum.
     * @return a new instance of NumberPercentAuto.
     */
    public static NumberPercentAuto newInstance(NumberPercentAutoAsEnum numberPercentAutoAsEnum)
    {
        NumberPercentAuto numberPercentAuto = new NumberPercentAuto();
        numberPercentAuto.numberPercentAutoAsEnum = numberPercentAutoAsEnum;
        return numberPercentAuto;
    }
    
    /**
     * Create a new instance of NumberPercentAuto with value set as a double.
     * @param NumberPercentAutoAsDbl - NumberPercentAuto value set as double.
     * @return a new instance of NumberPercentAuto.
     */
    public static NumberPercentAuto newInstance(double numberPercentAutoAsDbl)
    {
        NumberPercentAuto numberPercentAuto = new NumberPercentAuto();
        numberPercentAuto.numberPercentAutoAsDbl = numberPercentAutoAsDbl;
        return numberPercentAuto;
    }  
    
    public boolean isNumberPercentAutoAsEnum()
    {
    	if (numberPercentAutoAsEnum != null)
    		return true;
    	else
    		return false;
    }
    
    public NumberPercentAutoAsEnum getNumberPercentAutoAsEnum()
    {
        return this.numberPercentAutoAsEnum;
    }
    
    public double getNumberPercentAutoAsDbl()
    {
        return this.numberPercentAutoAsDbl;
    }
}
