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

package flex2.compiler.media;

import java.io.IOException;
import java.util.Map;

import com.adobe.fxg.FXGException;
import com.adobe.fxg.FXGParser;
import com.adobe.fxg.FXGParserFactory;
import com.adobe.fxg.dom.FXGNode;
import com.adobe.fxg.swf.FXG2SWFTranscoder;
import com.adobe.fxg.util.FXGResourceResolver;

import flash.swf.tags.DefineSprite;
import flex2.compiler.SymbolTable;
import flex2.compiler.TranscoderException;
import flex2.compiler.common.PathResolver;
import flex2.compiler.fxg.FlexFXG2SWFTranscoder;
import flex2.compiler.fxg.FlexResourceResolver;
import flex2.compiler.io.VirtualFile;
import flex2.compiler.util.MimeMappings;

/**
 * Converts an .fxg document to a DefineSprite and associated generated
 * ActionScript class. 
 * 
 * @author Peter Farland
 */
public class FXGTranscoder extends AbstractTranscoder
{
    private static final String IGNORE_TEXT = "ignoreText";

    public FXGTranscoder()
    {
        super(new String[]{MimeMappings.FXG}, DefineSprite.class, true);
    }

    @Override
    public TranscodingResults doTranscode(PathResolver resolver,
            SymbolTable symbolTable, Map<String, Object> args,
            String className, boolean generateSource)
            throws TranscoderException
    {
        VirtualFile source = resolveSource(resolver, args);
        TranscodingResults results = new TranscodingResults(source);

        // Text nodes are ignored and the remaining graphics nodes are 
        // converted to SWF tags
        DefineSprite sprite = fxgWithoutText(resolver, source);
        results.defineTag = sprite;

        if (generateSource)
            generateSource(results, className, args);

        return results;
    }

    /**
     * Determines whether an Embed attribute is supported for FXG transcoding.
     * 
     * <p>
     * The supported attributes are:
     * <ul>
     * <li><b>ignoreText</b> = "true" or "false"</li>
     * </ul>
     * </p>
     * 
     *  @param name An Embed attribute name.
     */
    @Override
    public boolean isSupportedAttribute(String name)
    {
        return (IGNORE_TEXT.equals(name));
    }

    /**
     * Since the SWF file format does not have a graphics primitive to target
     * FTE based text, this implementation processes graphics nodes in and
     * FXG document and ignores text nodes.
     * 
     * @param source The .fxg file to transcode to a SWF sprite.
     * @return A SWF DefineSprite representing the FXG document.
     * @throws TranscoderException
     */
    private DefineSprite fxgWithoutText(PathResolver resolver, VirtualFile source)
        throws TranscoderException
    {
        FXGNode node = parseFXG(source);
        FXG2SWFTranscoder transcoder = new FlexFXG2SWFTranscoder(null);
        FXGResourceResolver fxgResolver = new FlexResourceResolver(resolver);
        transcoder.setResourceResolver(fxgResolver);
        DefineSprite sprite = (DefineSprite)transcoder.transcode(node);
        return sprite;
    }

    /**
     * Parses a .fxg document into a custom FXG DOM.
     * 
     * @param source The .fxg file to be parsed.
     * @return The root GraphicNode of the DOM.
     * @throws TranscoderException
     */
    private static FXGNode parseFXG(VirtualFile source) throws TranscoderException
    {
        try
        {
            FXGParser parser = FXGParserFactory.createDefaultParser();
            FXGNode node = parser.parse(source.getInputStream());
            return node;
        }
        catch (FXGException ex)
        {
            throw new AbstractTranscoder.ExceptionWhileTranscoding(ex);
        }
        catch (IOException ex)
        {
            throw new AbstractTranscoder.ExceptionWhileTranscoding(ex);
        }
        catch (Throwable ex)
        {
            throw new AbstractTranscoder.ExceptionWhileTranscoding(null);
        }
    }
}
