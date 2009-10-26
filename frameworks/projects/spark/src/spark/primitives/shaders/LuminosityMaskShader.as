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

package spark.primitives.shaders
{
import flash.display.Shader;

/**
 *  The MaskAdjustShader class returns a shader that has one input, and allows 
 *  you to take in graphics, and output them in the correct colors for native 
 *  Flash masks. This acts as a converter for AIM transparency masks to Flash's 
 *  native mask.  
 * 
 *  Due to the way that flash implements alpha style masks, the sprite that is 
 *  to be masked will be in isolated mode, as this type of a mask requires the 
 *  parent's cacheAsBitmap setting to be enabled.
 * 
 *  This shader has a mode variable that can be set. There is a convenience mode 
 *  field you can set to do this.
 * 
 *  mode 0 = luminosityClip on, luminosityInvert off
 *  mode 1 = luminosityClip on, luminosityInvert on
 *  mode 2 = luminosityClip off, luminosityInvert off
 *  mode 3 = luminosityClip off, luminosityInvert on
 * 
 *  @langversion 3.0
 *  @playerversion Flash 10
 *  @playerversion AIR 1.5
 *  @productversion Flex 4
 */
public class LuminosityMaskShader extends Shader
{
    [Embed(source="LuminosityMaskFilter.pbj", mimeType="application/octet-stream")]
    private static var ShaderClass:Class;

	/**
	 *  Constructor. 
	 *  
	 *  @langversion 3.0
	 *  @playerversion Flash 10
	 *  @playerversion AIR 1.5
	 *  @productversion Flex 4
	 */
    public function LuminosityMaskShader()
    {
        super(new ShaderClass());
    }

    /**
     *  A convenience field that takes into account whether luminosityClip and/or
     *  luminosityInvert are on or off. 
     * 
     *  mode 0 = luminosityClip on, luminosityInvert off
 	 *  mode 1 = luminosityClip on, luminosityInvert on
 	 *  mode 2 = luminosityClip off, luminosityInvert off
 	 *  mode 3 = luminosityClip off, luminosityInvert on
	 * 
	 *  @langversion 3.0
 	 *  @playerversion Flash 10
 	 *  @playerversion AIR 1.5
 	 *  @productversion Flex 4
     */
    public function get mode():int
    {
        return this.data.mode.value;
    }

    public function set mode(v:int):void
    {
        this.data.mode.value=[v];
    }
}
}