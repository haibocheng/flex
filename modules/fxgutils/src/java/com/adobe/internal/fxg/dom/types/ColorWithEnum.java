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
 * The ColorWithEnum class. Underline value can be either a double or 
 * a ColorWithEnumAsEnum enum.
 * 
 * <pre>
 *   0 = auto
 *   1 = inherit
 * </pre>
 * 
 * @author Min Plunkett
 */
public class ColorWithEnum
{
    private int colorWithEnumAsInt;
    private ColorEnum colorEnum = null;
    
    
    /* The ColorEnum class.
    * 
    * <pre>
    *   0 = transparent
    *   1 = inherit
    * </pre>
    * 
    * @author Min Plunkett
    */
    public enum ColorEnum
    {
        /**
         * The enum representing an 'transparent' ColorWithEnum.
         */
        TRANSPARENT,
        
        /**
         * The enum representing an 'inherit' ColorWithEnum.
         */
        INHERIT;
    }
    
    private ColorWithEnum()
    {    
    }
    
    /**
     * Create a new instance of ColorWithEnum with value set as an enum.
     * @param colorEnum - ColorWithEnum value set as enum.
     * @return a new instance of ColorWithEnum.
     */
    public static ColorWithEnum newInstance(ColorEnum colorEnum)
    {
        ColorWithEnum colorWithEnum = new ColorWithEnum();
        colorWithEnum.colorEnum = colorEnum;
        return colorWithEnum;
    }
    
    /**
     * Create a new instance of ColorWithEnum with value set as a integer.
     * @param colorWithEnumAsInt - ColorWithEnum value set as integer.
     * @return a new instance of ColorWithEnum.
     */
    public static ColorWithEnum newInstance(int colorWithEnumAsInt)
    {
        ColorWithEnum colorWithEnum = new ColorWithEnum();
        colorWithEnum.colorWithEnumAsInt = colorWithEnumAsInt;
        return colorWithEnum;
    }  
    
    public boolean isColorWithEnumAsEnum()
    {
    	if (colorEnum != null)
    		return true;
    	else
    		return false;
    }
    
    public ColorEnum getColorWithEnumAsEnum()
    {
        return this.colorEnum;
    }
    
    public int getColorWithEnumAsString()
    {
        return this.colorWithEnumAsInt;
    }
}
