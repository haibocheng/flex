////////////////////////////////////////////////////////////////////////////////
//
//  ADOBE SYSTEMS INCORPORATED
//  Copyright 2003-2006 Adobe Systems Incorporated
//  All Rights Reserved.
//
//  NOTICE: Adobe permits you to use, modify, and distribute this file
//  in accordance with the terms of the license agreement accompanying it.
//
////////////////////////////////////////////////////////////////////////////////

package flash.swf.builder.tags;

import flash.swf.tags.DefineBitsLossless;
import flash.swf.TagValues;

/**
 * @author Paul Reilly
 * @author Peter Farland
 */
public class DefineBitsLosslessBuilder
{
	private DefineBitsLosslessBuilder()
	{
	}

	public static DefineBitsLossless build(int[] pixels, int width, int height)
	{
		DefineBitsLossless defineBitsLossless = new DefineBitsLossless(TagValues.stagDefineBitsLossless2);
		defineBitsLossless.format = DefineBitsLossless.FORMAT_24_BIT_RGB;
		defineBitsLossless.width = width;
		defineBitsLossless.height = height;
		defineBitsLossless.data = new byte[pixels.length * 4];

		for (int i = 0; i < pixels.length; i++)
		{
			int offset = i * 4;
			int alpha = (pixels[i] >> 24) & 0xFF;
			defineBitsLossless.data[offset] = (byte)alpha;

			// [preilly] Ignore the other components if alpha is transparent.  This seems
			// to be a bug in the player.  Additionally, premultiply the alpha and the
			// colors, because the player expects this.
			if (defineBitsLossless.data[offset] != 0)
			{
				int red = (pixels[i] >> 16) & 0xFF;
				defineBitsLossless.data[offset + 1] = (byte)((red * alpha) / 255);
				int green = (pixels[i] >> 8) & 0xFF;
				defineBitsLossless.data[offset + 2] = (byte)((green * alpha) / 255);
				int blue = pixels[i] & 0xFF;
				defineBitsLossless.data[offset + 3] = (byte)((blue * alpha) / 255);
			}
		}

		return defineBitsLossless;
	}
}
