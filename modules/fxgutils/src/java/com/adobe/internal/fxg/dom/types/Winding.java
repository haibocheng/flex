////////////////////////////////////////////////////////////////////////////////
//
//  ADOBE SYSTEMS INCORPORATED
//  Copyright 2008 Adobe Systems Incorporated
//  All Rights Reserved.
//
//  NOTICE: Adobe permits you to use, modify, and distribute this file
//  in accordance with the terms of the license agreement accompanying it.
//
////////////////////////////////////////////////////////////////////////////////

package com.adobe.internal.fxg.dom.types;

/**
 * The Winding enumeration specifies which rule to use when filling in shapes
 * that have overlapping or intersecting segments.
 * 
 * The enumeration order is significant and matches the SWF specification for
 * the UsesFillWindingRule property of the DefineShape4 tag.
 * 
 *   0 = Even Odd
 *   1 = Non Zero
 * 
 * @author Peter Farland
 */
public enum Winding
{
   /**
    * The enum representing an 'evenOdd' winding.
    */
   EVEN_ODD,

   /**
    * The enum representing a 'nonZero' winding.
    */
   NON_ZERO;
}
