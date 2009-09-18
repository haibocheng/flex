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
 * The BlendMode enumeration determines which blend mode to use when
 * compositing with the background. 
 * 
 * The ordinal for the enumeration is significant for the following BlendModes supported in FXGVersion.v1_0 
 * as it matches the SWF specification for the BlendMode property of the PlaceObject3 tag.
 * 
 * <pre>
 *   0 = normal
 *   1 = normal
 *   2 = layer
 *   3 = multiply
 *   4 = screen
 *   5 = lighten
 *   6 = darken
 *   7 = difference
 *   8 = add
 *   9 = subtract
 *  10 = invert
 *  11 = alpha
 *  12 = erase
 *  13 = overlay
 *  14 = hardlight
 * </pre>
 * 
 * The following Blendmodes were introduced in FXGVersion 2.0. Their implementation needs PixelBurst support.
 * 
 * <pre>
 *    colordodge
 *    colorburn
 *    exclusion
 *    softlight
 *    hue
 *    saturation
 *    color
 *    luminosity
 * </pre>
 * 
 * The following was introduced in FXG 2.0 which acts like blendMode=”layer” except when alpha is 0 or 1, in which case it acts like blendMode=”normal”
 * <pre>
 * 		auto
 * </pre>
 *  
 * @author Peter Farland
 * @author Sujata Das
 */
public enum BlendMode
{
    /**
     * The enum representing the default (unspecified) blend mode.
     */
    NORMAL0(),

    /**
     * The enum representing a 'normal' blend mode.
     */
    NORMAL(),

    /**
     * The enum representing a 'layer' blend mode.
     */
    LAYER(),

    /**
     * The enum representing a 'multiply' blend mode.
     */
    MULTIPLY(),

    /**
     * The enum representing a 'screen' blend mode.
     */
    SCREEN(),

    /**
     * The enum representing a 'lighten' blend mode.
     */
    LIGHTEN(),

    /**
     * The enum representing a 'darken' blend mode.
     */
    DARKEN(),

    /**
     * The enum representing an 'difference' blend mode.
     */
    DIFFERENCE(),

    /**
     * The enum representing a 'add' blend mode.
     */
    ADD(),

    /**
     * The enum representing a 'subtract' blend mode.
     */
    SUBTRACT(),

    /**
     * The enum representing an 'invert' blend mode.
     */
    INVERT(),

    /**
     * The enum representing an 'alpha' blend mode.
     */
    ALPHA(),

    /**
     * The enum representing an 'erase' blend mode.
     */
    ERASE(),

    /**
     * The enum representing a 'overlay' blend mode.
     */
    OVERLAY(),

    /**
     * The enum representing a 'hardlight' blend mode.
     */
    HARDLIGHT(),
    
    /**
     * The enum representing an 'auto' blend mode.
     */
    AUTO(),
    
    /**
     * The enum representing a 'colordodge' blend mode.
     */
    COLORDODGE(true),
    
    /**
     * The enum representing a 'colorburn' blend mode.
     */
    COLORBURN(true),
    
    /**
     * The enum representing a 'exclusion' blend mode.
     */
    EXCLUSION(true),
    
    /**
     * The enum representing a 'softlight' blend mode.
     */
    SOFTLIGHT(true),
    
    /**
     * The enum representing a 'hue' blend mode.
     */
    HUE(true),
    
    /**
     * The enum representing a 'saturation' blend mode.
     */
    SATURATION(true),
    
    /**
     * The enum representing a 'color' blend mode.
     */
    COLOR(true),
    
    /**
     * The enum representing a 'luminosity' blend mode.
     */
    LUMINOSITY(true);
    
    
    BlendMode ()
    {
        needsPixelBenderSupport = false;
    }
    
    BlendMode (boolean pixelBenderBlendMode)
    {
        needsPixelBenderSupport =  pixelBenderBlendMode;
    }
    
    public boolean needsPixelBenderSupport()
    {
        return needsPixelBenderSupport;
    }
    private boolean needsPixelBenderSupport;
    
    
    
}