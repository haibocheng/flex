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
	 * The HueShader class returns a shader that is the equivalent to the AIM 
	 * "Hue" blend mode for RGB premultiplied color. 
	 * 
	 * For this to display in Flash the same way that it does in AIM contexts, 
	 * you will need to composite against a transparent background. To do this, 
	 * set the parent display object to layer mode, or specify the opaque 
	 * background property to null, with the cacheAsBitmap property set to true. 
	 */
	public class HueShader extends flash.display.Shader
	{
		[Embed(source="Hue.pbj", mimeType="application/octet-stream")]
        private static var ShaderClass:Class;
		
		/**
		 * Constructor. 
		 */
		public function HueShader()
		{
			super(new ShaderClass());
		}
	}
}
