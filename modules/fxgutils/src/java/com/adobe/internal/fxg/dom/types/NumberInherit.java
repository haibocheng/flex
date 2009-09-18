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
 * The NumberInherit class. Underline value can be either a double or 
 * a NumberInheritAsEnum enum.
 * 
 * <pre>
 *   0 = inherit
 * </pre>
 * 
 * @author Min Plunkett
 */
public class NumberInherit
{
    private double numberInheritAsDbl = 0.0;
    private NumberInheritAsEnum numberInheritAsEnum = null;
    
    /* The NumberInheritAsEnum class.
    * 
    * <pre>
    *   0 = inherit
    * </pre>
    * 
    * @author Min Plunkett
    */
    public enum NumberInheritAsEnum
    {
        /**
         * The enum representing an 'inherit' NumberInherit.
         */
        INHERIT;
    }
    
    protected NumberInherit()
    {    
    }
    
    /**
     * Create a new instance of NumberInherit with value set as an enum.
     * @param numberInheritAsEnum - NumberInherit value set as enum.
     * @return a new instance of NumberInherit.
     */
    public static NumberInherit newInstance(NumberInheritAsEnum numberInheritAsEnum)
    {
        NumberInherit numberInherit = new NumberInherit();
        numberInherit.numberInheritAsEnum = numberInheritAsEnum;
        return numberInherit;
    }
    
    /**
     * Create a new instance of NumberInherit with value set as a double.
     * @param numberInheritAsDbl - NumberInherit value set as double.
     * @return a new instance of NumberInherit.
     */
    public static NumberInherit newInstance(double numberInheritAsDbl)
    {
        NumberInherit numberInherit = new NumberInherit();
        numberInherit.numberInheritAsDbl = numberInheritAsDbl;
        return numberInherit;
    }  
    
    public boolean isNumberInheritAsEnum()
    {
        if (this.numberInheritAsEnum != null)
            return true;
        else
            return false;
    }
    
    public NumberInheritAsEnum getNumberInheritAsEnum()
    {
        return this.numberInheritAsEnum;
    }
    
    public double getNumberInheritAsDbl()
    {
        return this.numberInheritAsDbl;
    }
}
