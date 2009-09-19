////////////////////////////////////////////////////////////////////////////////
//
//  ADOBE SYSTEMS INCORPORATED
//  Copyright 2005-2006 Adobe Systems Incorporated
//  All Rights Reserved.
//
//  NOTICE: Adobe permits you to use, modify, and distribute this file
//  in accordance with the terms of the license agreement accompanying it.
//
////////////////////////////////////////////////////////////////////////////////

package flex2.compiler.media;

import flash.swf.tags.DefineBitsLossless;
import flash.swf.tags.DefineSprite;
import flash.swf.builder.tags.DefineBitsLosslessBuilder;
import flash.graphics.images.LosslessImage;
import flex2.compiler.io.VirtualFile;
import flex2.compiler.util.MimeMappings;
import flex2.compiler.TranscoderException;

import java.util.Map;

/**
 * @author Roger Gonzalez
 */
public class LosslessImageTranscoder extends ImageTranscoder
{
    public LosslessImageTranscoder()
    {
        super(new String[]{MimeMappings.GIF, MimeMappings.PNG}, DefineSprite.class, true);
    }

    public ImageInfo getImage(VirtualFile sourceFile, Map args)
            throws TranscoderException
    {
        LosslessImage image = null;

        ImageInfo info = new ImageInfo(); 
        try
        {
            image = new LosslessImage(sourceFile.getName(),
                                      sourceFile.getInputStream(),
                                      sourceFile.getLastModified());
            DefineBitsLossless defineBits = DefineBitsLosslessBuilder.build(image.getPixels(), image.getWidth(), image.getHeight());
            info.defineBits = defineBits;
            info.width = image.getWidth();
            info.height = image.getHeight();
            return info;
        }
        catch (Exception ex)
        {
            throw new ExceptionWhileTranscoding( ex );
        }
    }
}
