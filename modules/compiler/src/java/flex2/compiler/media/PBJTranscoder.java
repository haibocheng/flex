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

import java.util.ArrayList;
import java.util.Map;

import flex2.compiler.SymbolTable;
import flex2.compiler.TranscoderException;
import flex2.compiler.common.PathResolver;
import flex2.compiler.io.VirtualFile;
import flex2.compiler.util.MimeMappings;

/**
 * Transcodes a compiled PBJ shader file to an ActionScript class.
 * 
 * @author Peter Farland
 */
public class PBJTranscoder extends AbstractTranscoder
{
    private DataTranscoder dataTranscoder = new DataTranscoder();

    public PBJTranscoder()
    {
        super(new String[]{MimeMappings.PBJ}, null, true);
    }

    @Override
    public TranscodingResults doTranscode(PathResolver context,
            SymbolTable symbolTable, Map<String, Object> args,
            String className, boolean generateSource)
            throws TranscoderException
    {
        VirtualFile source = resolveSource(context, args);

        // Create ByteArray subclass
        String byteArrayClassName = className + "ByteArray";
        args.put(NEWNAME, byteArrayClassName);
        TranscodingResults byteArrayResults = dataTranscoder.doTranscode(context,
                symbolTable, args, byteArrayClassName, generateSource);
        byteArrayResults.className = byteArrayClassName;

        // Create Shader subclass
        TranscodingResults shaderResults = new TranscodingResults(source);
        shaderResults.className = className;
        if (generateSource)
            shaderResults.generatedCode = generateSource(className, byteArrayClassName);

        // Associate ByteArray subclass asset with Shader subclass asset.
        shaderResults.additionalAssets = new ArrayList<TranscodingResults>();
        shaderResults.additionalAssets.add(byteArrayResults);

        return shaderResults;
    }

    @Override
    public boolean isSupportedAttribute(String attr)
    {
        return false;
    }

    /**
     * 
     * <pre>
     * package
     * {
     * 
     * import flash.display.Shader;
     * import flash.utils.ByteArray;
     * import mx.core.IFlexAsset;
     * 
     * public class MyShaderAsset extends Shader implements IFlexAsset
     * {
     *     public function MyShaderAsset()
     *     {
     *         super(null);
     *         byteCode = new MyShaderAssetByteArray();
     *     }
     * }
     * }
     * </pre>
     * 
     * @param className
     * @param results
     */
    private String generateSource(String className, String byteArrayClassName)
    {
        StringBuilder sb = new StringBuilder();
        sb.append("package\n");
        sb.append("{\n");
        sb.append("import flash.display.Shader\n");
        sb.append("import flash.utils.ByteArray\n");
        sb.append("import mx.core.IFlexAsset\n\n");
        sb.append("public class ").append(className).append(" extends Shader implements IFlexAsset\n");
        sb.append("{\n");
        sb.append("\tpublic function ").append(className).append("()\n");
        sb.append("\t{\n");
        sb.append("\t\tsuper();\n");
        sb.append("\t\tbyteCode = new " + byteArrayClassName + "();\n");
        sb.append("\t}\n");
        sb.append("}\n");
        sb.append("}\n");
        return sb.toString();
    }
}
