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
 * A scaling grid is used to calculate the center rectangle that determines 
 * how to apply 9-slice scaling to a graphic.
 * 
 * @author Peter Farland
 */
public class ScalingGrid
{
    public double scaleGridLeft = 0.0;
    public double scaleGridRight = 0.0;
    public double scaleGridTop = 0.0;
    public double scaleGridBottom = 0.0;
}
