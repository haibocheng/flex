////////////////////////////////////////////////////////////////////////////////
//
//  ADOBE SYSTEMS INCORPORATED
//  Copyright 2005-2009 Adobe Systems Incorporated
//  All Rights Reserved.
//
//  NOTICE: Adobe permits you to use, modify, and distribute this file
//  in accordance with the terms of the license agreement accompanying it.
//
////////////////////////////////////////////////////////////////////////////////

package flex2.compiler.media;

import flash.graphics.images.LosslessImage;
import flash.swf.builder.tags.DefineBitsLosslessBuilder;
import flash.swf.tags.DefineBitsJPEG3;
import flash.swf.tags.DefineBitsLossless;
import flash.swf.tags.DefineSprite;
import flex2.compiler.TranscoderException;
import flex2.compiler.io.VirtualFile;
import flex2.compiler.util.MimeMappings;

import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.DirectColorModel;
import java.io.ByteArrayOutputStream;
import java.util.Map;
import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageTypeSpecifier;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.MemoryCacheImageOutputStream;

/**
 * Transcodes lossless images, GIF and PNG.  Compression with quality
 * is supported.  For compression, we convert the image to a JPEG.
 *
 * @author Roger Gonzalez
 * @author Joa Ebert
 * @author Paul Reilly
 */
public class LosslessImageTranscoder extends ImageTranscoder
{
    public static final String COMPRESSION = "compression";
    public static final String QUALITY = "quality";

    public LosslessImageTranscoder()
    {
        super(new String[]{MimeMappings.GIF, MimeMappings.PNG}, DefineSprite.class, true);
    }

    public boolean isSupportedAttribute(String attr)
    {
        return (COMPRESSION.equals(attr) || 
                QUALITY.equals(attr) || 
                super.isSupportedAttribute(attr));
    }

    public ImageInfo getImage(VirtualFile sourceFile, Map<String, Object> args)
        throws TranscoderException
    {
        ImageInfo info = new ImageInfo();

        try
        {
            LosslessImage image = new LosslessImage(sourceFile.getName(),
                                                    sourceFile.getInputStream(),
                                                    sourceFile.getLastModified());
        
            // If compression is true, use JPEG compression.  Otherwise, use the lossless format.
            if (args.containsKey(COMPRESSION) && Boolean.parseBoolean((String) args.get(COMPRESSION)))
            {
                // We use DefineBitsJPEG3, because it supports an alpha channel
                DefineBitsJPEG3 defineBits = new DefineBitsJPEG3();
                int imageSize = image.getWidth() * image.getHeight();
                byte[] alphaData = new byte[imageSize];
                int[] pixels = image.getPixels();
                BufferedImage bufferedImage = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_ARGB);

                bufferedImage.setRGB(0, 0, image.getWidth(), image.getHeight(), pixels, 0, image.getWidth());

                for (int i = 0; i < imageSize; ++i)
                {
                    alphaData[i] = (byte) ((pixels[i] >> 24) & 0xff);
                }

                float quality = 1.0f;

                if (args.containsKey(QUALITY))
                {
                    try
                    {
                        String qualityString = (String) args.get(QUALITY);
                        double qualityPercentage = Double.parseDouble(qualityString);

                        // quality must be a number between 0 and 100.
                        if (qualityPercentage < 0 || qualityPercentage > 100)
                        {
                            throw new InvalidQuality();
                        }

                        quality = (float)(qualityPercentage / 100.0);
                    }
                    catch (NumberFormatException numberFormatException)
                    {
                        throw new InvalidQuality();
                    }
                }

                defineBits.data = bufferedImageToJPEG(bufferedImage, quality);
                defineBits.alphaDataOffset = alphaData.length;
                defineBits.alphaData = alphaData;

                info.defineBits = defineBits;
            }
            else
            {
                // quality doesn't make sense without compression.
                if (args.containsKey(QUALITY))
                {
                    throw new QualityRequiresCompression();
                }

                DefineBitsLossless defineBits = DefineBitsLosslessBuilder.build(image.getPixels(), image.getWidth(), image.getHeight());
                info.defineBits = defineBits;
            }

            info.width = image.getWidth();
            info.height = image.getHeight();
        }
        catch (TranscoderException transcoderException)
        {
            throw transcoderException;
        }
        catch (Exception exception)
        {
            throw new ExceptionWhileTranscoding(exception);
        }

        return info;
    }

    private static byte[] bufferedImageToJPEG(BufferedImage bufferedImage, float quality)
        throws Exception
    {
        ImageWriter writer = ImageIO.getImageWritersByFormatName("jpeg").next();
        ImageWriteParam writeParam = writer.getDefaultWriteParam();
        ColorModel colorModel = new DirectColorModel(24, 0x00ff0000, 0x0000ff00, 0x000000ff);
        ImageTypeSpecifier imageTypeSpecifier =
            new ImageTypeSpecifier(colorModel, colorModel.createCompatibleSampleModel(1, 1)/*ignored*/);
        writeParam.setDestinationType(imageTypeSpecifier);
        writeParam.setSourceBands(new int[] {0, 1, 2});
        writeParam.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
        writeParam.setCompressionQuality(quality);

        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        writer.setOutput(new MemoryCacheImageOutputStream(buffer));

        IIOImage ioImage = new IIOImage(bufferedImage, null, null);

        writer.write(null, ioImage, writeParam);
        writer.dispose();

        return buffer.toByteArray();
    }

    public class InvalidQuality extends TranscoderException
    {
        private static final long serialVersionUID = 6347969168361169993L;

        public InvalidQuality()
        {
        }
    }

    public class QualityRequiresCompression extends TranscoderException
    {
        private static final long serialVersionUID = 6347969168361169994L;

        public QualityRequiresCompression()
        {
        }
    }
}
