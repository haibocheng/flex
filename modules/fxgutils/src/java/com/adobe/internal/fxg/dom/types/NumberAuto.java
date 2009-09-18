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
 * The NumberAuto class. Underline value can be either a double or 
 * a NumberAutoAsEnum enum.
 * 
 * <pre>
 *   0 = auto
 *   1 = inherit
 * </pre>
 * 
 * @author Min Plunkett
 */
public class NumberAuto
{
    private double numberAutoAsDbl = 0.0;
    private int numberAutoAsInt;
    private NumberAutoAsEnum numberAutoAsEnum = null;
    private Type dataType;
    
    
    /* The NumberAutoAsEnum class.
    * 
    * <pre>
    *   0 = auto
    *   1 = inherit
    * </pre>
    * 
    * @author Min Plunkett
    */
    public enum NumberAutoAsEnum
    {
        /**
         * The enum representing an 'auto' NumberAuto.
         */
        AUTO,
        
        /**
         * The enum representing an 'inherit' NumberAuto.
         */
        INHERIT;
    }
    
    /* The Type class.
     * 
     * <pre>
     *   0 = enum
     *   1 = double
     *   2 = integer
     * </pre>
     * 
     * @author Min Plunkett
     */
     public enum Type
     {
         /**
          * The enum representing an 'enum' data type.
          */
         ENUM,
         
         /**
          * The enum representing an 'double' data type.
          */
         DOUBLE,

         /**
          * The enum representing an 'integer' data type.
          */
         INTEGER;
     }
    
    private NumberAuto()
    {    
    }
    
    /**
     * Create a new instance of NumberAuto with value set as an enum.
     * @param numberAutoAsEnum - NumberAuto value set as enum.
     * @return a new instance of NumberAuto.
     */
    public static NumberAuto newInstance(NumberAutoAsEnum numberAutoAsEnum)
    {
        NumberAuto numberAuto = new NumberAuto();
        numberAuto.numberAutoAsEnum = numberAutoAsEnum;
        numberAuto.dataType = Type.ENUM;
        return numberAuto;
    }
    
    /**
     * Create a new instance of NumberAuto with value set as a double.
     * @param numberAutoAsDbl - NumberAuto value set as double.
     * @return a new instance of NumberAuto.
     */
    public static NumberAuto newInstance(double numberAutoAsDbl)
    {
        NumberAuto numberAuto = new NumberAuto();
        numberAuto.numberAutoAsDbl = numberAutoAsDbl;
        numberAuto.dataType = Type.DOUBLE;
        return numberAuto;
    }  
    
    /**
     * Create a new instance of NumberAuto with value set as a integer.
     * @param numberAutoAsInt - NumberAuto value set as integer.
     * @return a new instance of NumberAuto.
     */
    public static NumberAuto newInstance(int numberAutoAsInt)
    {
        NumberAuto numberAuto = new NumberAuto();
        numberAuto.numberAutoAsInt = numberAutoAsInt;
        numberAuto.dataType = Type.INTEGER;
        return numberAuto;
    }  
    
    public Type getType()
    {
        return dataType;
    }
    
    public NumberAutoAsEnum getNumberAutoAsEnum()
    {
        return this.numberAutoAsEnum;
    }
    
    public double getNumberAutoAsDbl()
    {
        return this.numberAutoAsDbl;
    }
    
    public int getNumberAutoAsInt()
    {
        return this.numberAutoAsInt;
    }
}
