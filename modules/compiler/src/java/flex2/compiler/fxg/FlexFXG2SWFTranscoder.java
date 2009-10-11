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

package flex2.compiler.fxg;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.adobe.fxg.dom.FXGNode;
import com.adobe.fxg.swf.FXG2SWFTranscoder;
import com.adobe.internal.fxg.dom.CDATANode;
import com.adobe.internal.fxg.dom.GraphicContentNode;
import com.adobe.internal.fxg.dom.GroupNode;
import com.adobe.internal.fxg.dom.MaskableNode;
import com.adobe.internal.fxg.dom.MaskingNode;
import com.adobe.internal.fxg.dom.RichTextNode;
import com.adobe.internal.fxg.dom.TextGraphicNode;
import com.adobe.internal.fxg.dom.TextNode;
import com.adobe.internal.fxg.dom.richtext.BRNode;
import com.adobe.internal.fxg.dom.richtext.DivNode;
import com.adobe.internal.fxg.dom.richtext.ImgNode;
import com.adobe.internal.fxg.dom.richtext.LinkNode;
import com.adobe.internal.fxg.dom.richtext.TextLayoutFormatNode;
import com.adobe.internal.fxg.dom.richtext.ParagraphNode;
import com.adobe.internal.fxg.dom.richtext.SpanNode;
import com.adobe.internal.fxg.dom.richtext.TCYNode;
import com.adobe.internal.fxg.dom.richtext.TabNode;
import com.adobe.internal.fxg.dom.types.BlendMode;
import com.adobe.internal.fxg.dom.types.MaskType;

import flash.swf.tags.DefineSprite;
import flash.swf.tags.DefineTag;
import flash.swf.tags.PlaceObject;
import flash.util.StringUtils;
import flex2.compiler.mxml.lang.StandardDefs;
import flex2.compiler.mxml.reflect.Property;
import flex2.compiler.mxml.reflect.Type;
import flex2.compiler.mxml.reflect.TypeTable;

/**
 * This implementation generates ActionScript classes to draw TextGraphic nodes
 * programmatically using instances of the spark.components.RichText
 * ActionScript class. To maintain a link between the DefineSprite tree and
 * RichText wrapper classes, symbol classes will be created for each sprite that
 * needs to instantiate a child RichText instance.
 * 
 * @author Kaushal Kantawala
 * @author Peter Farland
 */
public class FlexFXG2SWFTranscoder extends FXG2SWFTranscoder
{
    private FXGSymbolClass graphicClass;
    private String packageName;

    private TypeTable typeTable;
    private Type divType;
    private Type imgType;
    private Type linkType;
    private Type richTextType; // Note: TextGraphic and RichText use same type
    private Type paragraphType;
    private Type spanType;
    private Type tabType;
    private Type tcyType;
    private Type textLayoutFormatType;

    /**
     * Construct a Flex specific FXG to SWF tag transcoder.
     * 
     * @param typeTable A TypeTable is used to look up type information while
     * generating ActionScript source for a TextGraphic section of an FXG
     * document. 
     */
    public FlexFXG2SWFTranscoder(TypeTable typeTable)
    {
        super();
        this.typeTable = typeTable;
        if (typeTable != null)
        {
            textLayoutFormatType = typeTable.getType(StandardDefs.CLASS_TEXT_LAYOUT_FORMAT);
            divType = typeTable.getType(StandardDefs.CLASS_TEXT_DIV);
            linkType = typeTable.getType(StandardDefs.CLASS_TEXT_LINK);
            imgType = typeTable.getType(StandardDefs.CLASS_TEXT_IMG);
            richTextType = typeTable.getType(StandardDefs.CLASS_TEXT_RICHTEXT);
            paragraphType = typeTable.getType(StandardDefs.CLASS_TEXT_PARAGRAPH);
            spanType = typeTable.getType(StandardDefs.CLASS_TEXT_SPAN);
            tabType = typeTable.getType(StandardDefs.CLASS_TEXT_TAB);
            tcyType = typeTable.getType(StandardDefs.CLASS_TEXT_TCY);
        }
    }

    /**
     * Traverses an FXG DOM to generate a SWF graphics primitives, with the
     * root DefineSprite associated with a generated ActionScript symbol class.
     * 
     * @param node The root node of an FXG DOM.
     * @param className The class name of the generated symbol class.
     * @return an FXGSymbolClass context that includes generated sources and
     * associated DefineSprites.
     */
    public FXGSymbolClass transcode(FXGNode node, String packageName, String className)
    {
        this.packageName = packageName;

        if (node instanceof FlexGraphicNode)
        {
            FlexGraphicNode graphicNode = (FlexGraphicNode)node;

            graphicClass = new FXGSymbolClass();
            graphicClass.setPackageName(packageName);
            graphicClass.setClassName(className);

            DefineSprite sprite = (DefineSprite)transcode(graphicNode);
            graphicClass.setSymbol(sprite);

            // Create a new sprite class to map to this Graphic's DefineSprite
            StringBuilder buf = new StringBuilder(512);
            buf.append("package ").append(packageName).append("\n");
            buf.append("{\n\n");
            buf.append("import spark.core.SpriteVisualElement;\n\n");
            buf.append("public class ").append(className).append(" extends SpriteVisualElement\n");
            buf.append("{\n");
            buf.append("    public function ").append(className).append("()\n");
            buf.append("    {\n");
            buf.append("        super();\n");

            if (!Double.isNaN(graphicNode.viewWidth))
                buf.append("        viewWidth = ").append(graphicNode.viewWidth).append(";\n");

            if (!Double.isNaN(graphicNode.viewHeight))
                buf.append("        viewHeight = ").append(graphicNode.viewHeight).append(";\n");

            if (graphicNode.getMaskType() == MaskType.ALPHA && graphicNode.mask != null)
            {
                int maskIndex = graphicNode.mask.getMaskIndex();
                buf.append("        this.cacheAsBitmap = true;\n");
                buf.append("        this.mask = this.getChildAt(").append(maskIndex).append(");\n");
            }

            buf.append("    }\n"); // End constructor
			buf.append("}\n"); // End class
	        buf.append("}\n"); // End package

            graphicClass.setGeneratedSource(buf.toString());

            return graphicClass;
        }

        return null;
    }

    @Override
    public FXG2SWFTranscoder newInstance()
    {
        FlexFXG2SWFTranscoder graphics = new FlexFXG2SWFTranscoder(typeTable);
        graphics.packageName = packageName;
        graphics.graphicClass = graphicClass;
        graphics.definitions = definitions;
        return graphics;
    }

    //--------------------------------------------------------------------------
    //
    //  Alpha Mask Code Generation
    //
    //--------------------------------------------------------------------------

    @Override
    protected PlaceObject mask(MaskableNode node, DefineSprite parentSprite)
    {
        MaskingNode mask = node.getMask();
        PlaceObject po3 = super.mask(node, parentSprite); 

        if (mask != null)
        {
            MaskType maskType = node.getMaskType(); 
            if (maskType == MaskType.ALPHA)
            {
                // Record the display list position that the mask was placed.
                // The ActionScript display list is 0 based, but SWF the depth
                // counter starts at 1, so we subtract 1.
                int maskIndex = parentSprite.depthCounter - 1;
                mask.setMaskIndex(maskIndex);
            }
        }

        return po3;
    }

    @Override
    protected PlaceObject graphicContentNode(GraphicContentNode node)
    {
        PlaceObject po3 = super.graphicContentNode(node);

        if (hasAdvancedGraphics(node))
        {
            po3 = advancedGraphics(node, po3);
        }

        return po3;
    }

    @Override
    protected PlaceObject group(GroupNode node)
    {
        PlaceObject po3 = super.group(node);

        if (hasAdvancedGraphics(node))
        {
            po3 = advancedGraphics(node, po3);
        }

        return po3;
    }

    private boolean hasAdvancedGraphics(MaskableNode node)
    {
        if (node.getMask() != null &&
            (node.getMaskType() == MaskType.ALPHA ||
             node.getMaskType() == MaskType.LUMINOSITY))
        {
            return true;
        }
        else if (node instanceof GraphicContentNode)
        {
            GraphicContentNode graphicNode = (GraphicContentNode)node;
            if (graphicNode.blendMode.needsPixelBenderSupport())
                return true;
        }

        return false;
    }

    private PlaceObject advancedGraphics(GraphicContentNode node, PlaceObject po3)
    {
        String className = createUniqueClassName(graphicClass.getClassName() + "_Maskee");

        if (po3 != null)
        {
            DefineTag symbol = po3.ref;

            // Create a new SymbolClass to map to this mask's
            // DefineSprite (see below)  
            FXGSymbolClass symbolClass = new FXGSymbolClass();
            symbolClass.setPackageName(packageName);
            symbolClass.setClassName(className);

            // Then record this SymbolClass with the top-level graphic
            // SymbolClass so that it will be associated with the FXG asset.
            graphicClass.addAdditionalSymbolClass(symbolClass);

            StringBuilder buf = new StringBuilder(512);
            buf.append("package ").append(packageName).append("\n");
            buf.append("{\n\n");

            // Determine Base Class
            String baseClassName = null;
            if (node instanceof GroupNode)
            {
                buf.append("import flash.display.Sprite;\n");
                baseClassName = "Sprite";
            }
            else
            {
                buf.append("import flash.display.Shape;\n");
                baseClassName = "Shape";
            }

            // Advanced BlendModes
            String blendModeImport = generateAdvancedBlendModeImport(node.blendMode);
            if (blendModeImport != null)
                buf.append(blendModeImport);
            String blendModeShader = generateAdvancedBlendMode(node.blendMode);

            buf.append("public class ").append(className).append(" extends ").append(baseClassName).append("\n");
            buf.append("{\n");
            buf.append("    public function ").append(className).append("()\n");
            buf.append("    {\n");
            buf.append("        super();\n");
            buf.append("        this.cacheAsBitmap = true;\n");

            // Alpha Masks
            MaskingNode maskNode = node.getMask();
            if (maskNode != null)
            {
                int maskIndex = maskNode.getMaskIndex();
                if (node instanceof GroupNode)
                    buf.append("        this.mask = this.getChildAt(").append(maskIndex).append(");\n");
                else
                    buf.append("        this.mask = this.parent.getChildAt(").append(maskIndex).append(");\n");
            }

            // BlendMode Shader
            if (blendModeShader != null)
                buf.append(blendModeShader);

            buf.append("    }\n");
            buf.append("}\n"); // End class
            buf.append("}\n"); // End package

            symbolClass.setGeneratedSource(buf.toString());
            symbolClass.setSymbol(symbol);
        }

        return po3;
    }

    private String generateAdvancedBlendModeImport(BlendMode blendMode)
    {
        if (blendMode == BlendMode.COLOR)
            return "import spark.primitives.shaders.ColorShader;\n\n";
        else if (blendMode == BlendMode.COLORBURN)
            return "import spark.primitives.shaders.ColorBurnShader;\n\n";
        else if (blendMode == BlendMode.COLORDODGE)
            return "import spark.primitives.shaders.ColorDodgeShader;\n\n";
        else if (blendMode == BlendMode.EXCLUSION)
            return "import spark.primitives.shaders.ExclusionShader;\n\n";
        else if (blendMode == BlendMode.HUE)
            return "import spark.primitives.shaders.HueShader;\n\n";
        else if (blendMode == BlendMode.LUMINOSITY)
            return "import spark.primitives.shaders.LuminosityShader;\n\n";
        else if (blendMode == BlendMode.SATURATION)
            return "import spark.primitives.shaders.SaturationShader;\n\n";
        else if (blendMode == BlendMode.SOFTLIGHT)
            return"import spark.primitives.shaders.SoftLightShader;\n\n";
        else
            return null;
    }

    private String generateAdvancedBlendMode(BlendMode blendMode)
    {
        if (blendMode == BlendMode.COLOR)
            return "        this.blendShader = new ColorShader();\n";
        else if (blendMode == BlendMode.COLORBURN)
            return "        this.blendShader = new ColorBurnShader();\n"; 
        else if (blendMode == BlendMode.COLORDODGE)
            return "        this.blendShader = new ColorDodgeShader();\n"; 
        else if (blendMode == BlendMode.EXCLUSION)
            return "        this.blendShader = new ExclusionShader();\n"; 
        else if (blendMode == BlendMode.HUE)
            return "        this.blendShader = new HueShader();\n"; 
        else if (blendMode == BlendMode.LUMINOSITY)
            return "        this.blendShader = new LuminosityShader();\n"; 
        else if (blendMode == BlendMode.SATURATION)
            return "        this.blendShader = new SaturationShader();\n"; 
        else if (blendMode == BlendMode.SOFTLIGHT)
            return "        this.blendShader = new SoftLightShader();\n";
        else
            return null;
    }

    //--------------------------------------------------------------------------
    //
    //  TextGraphic and RichText Code Generation
    //
    //--------------------------------------------------------------------------
    
    @Override
    protected PlaceObject richtext(RichTextNode node)
    {
        return flexText(node);
    }

    @Override
    protected PlaceObject text(TextGraphicNode node)
    {
        return flexText(node);
    }

    /**
     * Generates Flex specific ActionScript for FXG 1.0 &lt;TextGraphic&gt; or
     * FXG 2.0 &lt;RichText&gt; nodes.
     * @param node either a TextGraphicNode or RichTextNode.
     * @return PlaceObject 
     */
    private PlaceObject flexText(GraphicContentNode node)
    {
        if (node instanceof TextNode)
        {
            TextNode textNode = ((TextNode)node);

            // Create a new SymbolClass to map to this TextGraphic's
            // DefineSprite (see below)  
            String className = createUniqueClassName(graphicClass.getClassName() + "_Text");

            FXGSymbolClass spriteSymbolClass = new FXGSymbolClass();
            spriteSymbolClass.setPackageName(packageName);
            spriteSymbolClass.setClassName(className);

            // Then record this SymbolClass with the top-level graphic
            // SymbolClass so that it will be associated with the FXG asset.
            graphicClass.addAdditionalSymbolClass(spriteSymbolClass);

            // Create a DefineSprite to hold this TextGraphic
            DefineSprite textSprite = createDefineSprite(className);
            PlaceObject po3 = placeObject(textSprite, node.createGraphicContext());
            spriteStack.push(textSprite);

            StringBuilder buf = new StringBuilder(1024);
            buf.append("package ").append(packageName).append("\n");
            buf.append("{\n\n");
            buf.append("import flashx.textLayout.elements.*;\n");
            buf.append("import flashx.textLayout.formats.TextLayoutFormat;\n");
            buf.append("import spark.components.RichText;\n");
            buf.append("import spark.core.SpriteVisualElement;\n\n");

            // Advanced BlendModes
            String blendModeImport = generateAdvancedBlendModeImport(node.blendMode);
            if (blendModeImport != null)
                buf.append(blendModeImport);
            String blendModeShader = generateAdvancedBlendMode(node.blendMode);

            buf.append("public class ").append(className).append(" extends SpriteVisualElement\n");
            buf.append("{\n");
            buf.append("    public function ").append(className).append("()\n");
            buf.append("    {\n");
            buf.append("        super();\n");

            // Alpha Masks
            MaskingNode maskNode = node.getMask();
            if (maskNode != null)
            {
                int maskIndex = maskNode.getMaskIndex();
                buf.append("        this.mask = this.parent.getChildAt(").append(maskIndex).append(");\n");
            }

            // BlendMode Shader
            if (blendModeShader != null)
                buf.append(blendModeShader);

            // Generate Text
            buf.append("        createText();\n");
            buf.append("    }\n");
            buf.append("\n");
            buf.append("    private function createText():void\n");
            buf.append("    {\n");
            StringBuilder textSource = generateRichText(textNode);
            buf.append(textSource);
            buf.append("    }\n");
            buf.append("}\n"); // End class
            buf.append("}\n"); // End package

            spriteSymbolClass.setGeneratedSource(buf.toString());
            spriteSymbolClass.setSymbol(textSprite);

            spriteStack.pop();
            return po3;
        }

        return null;
    }

    /**
     * Generates a unique class name by appending a random number to the given
     * base name.
     * @param baseName the base of the generated class name 
     * @return the unique class name
     */
    private String createUniqueClassName(String baseName)
    {
        int r = random.nextInt();
        String suffix = Integer.toString(r);
        if (suffix.charAt(0) == '-')
        {
            suffix = suffix.replace('-', '_');
        }
        return baseName + suffix;
    }

    //--------------------------------------------------------------------------
    //
    // Methods for ActionScript Generation  
    //
    //--------------------------------------------------------------------------

    /**
     * Generates ActionScript code to initialize a new instance of
     * RichText for a given FXG TextGraphic node, its attributes, and any
     * child nodes.
     * 
     * @param node The TextGraphic node to process.
     * @param textGraphicCounter Used to generate a unique variable name for
     * each instance of RichText.
     * 
     * @return Returns the code generation buffer as a StringBuilder.
     */
    private StringBuilder generateRichText(TextNode textNode)
    {
        // Generate ActionScript equivalent of tag markup...
        StringBuilder sb = new StringBuilder();

        Variables context = new Variables();
        context.setVar(richTextType, NodeType.RICHTEXT);
        String elementVar = context.elementVar;

        generateTextVariable(sb, textNode, context);

        sb.append("        addChild(").append(elementVar).append(");\r\n");
		
        sb.append("        ").append(elementVar).append(".regenerateStyleCache(true);\r\n");
        sb.append("        ").append(elementVar).append(".stylesInitialized();\r\n");
        sb.append("        ").append(elementVar).append(".validateProperties();\r\n");
        sb.append("        ").append(elementVar).append(".validateSize();\r\n");
        sb.append("        ").append(elementVar).append(".setLayoutBoundsSize(NaN, NaN);\r\n");
        sb.append("        ").append(elementVar).append(".validateDisplayList();\r\n");

        return sb;
    }

    /**
     * Generates ActionScript to push a child onto an Array:
     * <pre>
     * someContent.push(someChildElement);
     * </pre>
     */
    private void generatePushChild(StringBuilder sb, String contentVar, String childElementVar)
    {
        sb.append("        ").append(contentVar).append(".push(").append(childElementVar).append(");\r\n");
    }

    /**
     * Generates an ActionScript property assignment:
     * <pre>
     * someElement.mxmlChildren = someContent;
     * </pre>
     * @param sb
     * @param elementVar
     * @param propertyVar
     * @param valueVar
     */
    private void generateAssignment(StringBuilder sb, String elementVar, String propertyVar, String valueVar)
    {
        sb.append("        ").append(elementVar).append(".").append(propertyVar).append(" = ").append(valueVar).append(";\r\n");
    }

    /**
     * Generates ActionScript to initialize a variable for a given text node,
     * populates any specified attributes as properties or styles, and
     * recursively processes any text child nodes.
     * 
     * @param sb - the generated ActionScript buffer 
     * @param textNode - the current text node
     * @param context - the current generated ActionScript variable context 
     */
    private void generateTextVariable(StringBuilder sb, TextNode textNode,
            Variables context)
    {
        Map<String, String> attributes = textNode.getTextAttributes();
        List<TextNode> children = textNode.getTextChildren();

        String parentVar = context.elementVar;
        String contentVar = context.contentVar;
        String parentClass = context.elementClass;
        String parentChildrenVar = context.elementChildrenVar;
        Type type = context.type;

        if (!context.varDeclared)
        {
            // var someElement:SomeElement = new SomeElement();
            sb.append("        var ").append(parentVar).append(":").append(parentClass).append(" = new ").append(parentClass).append("();\r\n");

            // var someContent:Array = [];
            if (contentVar != null)
                sb.append("        var ").append(contentVar).append(":Array = [];\r\n");
        }
        else
        {
            // someElement = new SomeElement();
            sb.append("        ").append(parentVar).append(" = new ").append(parentClass).append("();\r\n");

            // someContent = [];
            if (contentVar != null)
                sb.append("        ").append(contentVar).append(" = [];\r\n");
        }

        // Attributes
        generateAttributes(type, attributes, sb, parentVar);

        // Properties
        generateProperties(sb, parentVar, textNode.getTextProperties(), context);

        // Child Nodes
        if (children != null && children.size() > 0)
        {
            Iterator<TextNode> iter = children.iterator();
            while (iter.hasNext())
            {
                String elementVar = null;
                TextNode child = iter.next();

                // FXG 2.0
                if (child instanceof RichTextNode)
                {
                    context.setVar(richTextType, NodeType.RICHTEXT);
                    elementVar = context.elementVar;
                    generateTextVariable(sb, child, context);
                    generatePushChild(sb, contentVar, elementVar);
                }
                else if (child instanceof ParagraphNode)
                {
                    context.setVar(paragraphType, NodeType.PARAGRAPH);
                    elementVar = context.elementVar;
                    generateTextVariable(sb, child, context);
                    generatePushChild(sb, contentVar, elementVar);
                }
                else if (child instanceof SpanNode)
                {
                    context.setVar(spanType, NodeType.SPAN);
                    elementVar = context.elementVar;
                    generateTextVariable(sb, child, context);
                    generatePushChild(sb, contentVar, elementVar);
                }
                else if (child instanceof DivNode)
                {
                    context.setVar(divType, NodeType.DIV);
                    elementVar = context.elementVar;
                    generateTextVariable(sb, child, context);
                    generatePushChild(sb, contentVar, elementVar);
                }
                else if (child instanceof CDATANode)
                {
                    // someContent.push("some text");
                    String text = formatString(((CDATANode)child).content);
                    sb.append("        ").append(contentVar).append(".push(").append(text).append(");\r\n");
                }
                else if (child instanceof BRNode)
                {
                    // someContent.push(new BreakElement());
                    sb.append("        ").append(contentVar).append(".push(new BreakElement());\r\n");
                }
                else if (child instanceof ImgNode)
                {
                    context.setVar(imgType, NodeType.IMG);
                    elementVar = context.elementVar;
                    generateTextVariable(sb, child, context);
                    generatePushChild(sb, contentVar, elementVar);
                }
                else if (child instanceof LinkNode)
                {
                    context.setVar(linkType, NodeType.LINK);
                    elementVar = context.elementVar;
                    generateTextVariable(sb, child, context);
                    generatePushChild(sb, contentVar, elementVar);
                }
                else if (child instanceof TabNode)
                {
                    context.setVar(tabType, NodeType.TAB);
                    elementVar = context.elementVar;
                    generateTextVariable(sb, child, context);
                    generatePushChild(sb, contentVar, elementVar);
                }
                else if (child instanceof TCYNode)
                {
                    context.setVar(tcyType, NodeType.TCY);
                    elementVar = context.elementVar;
                    generateTextVariable(sb, child, context);
                    generatePushChild(sb, contentVar, elementVar);
                }
                // FXG 1.0
                else if (child instanceof TextGraphicNode)
                {
                    context.setVar(richTextType, NodeType.RICHTEXT);
                    elementVar = context.elementVar;
                    generateTextVariable(sb, child, context);
                    generatePushChild(sb, contentVar, elementVar);
                }
                else if (child instanceof com.adobe.internal.fxg.dom.text.ParagraphNode)
                {
                    context.setVar(paragraphType, NodeType.PARAGRAPH);
                    elementVar = context.elementVar;
                    generateTextVariable(sb, child, context);
                    generatePushChild(sb, contentVar, elementVar);
                }
                else if (child instanceof com.adobe.internal.fxg.dom.text.SpanNode)
                {
                    context.setVar(spanType, NodeType.SPAN);
                    elementVar = context.elementVar;
                    generateTextVariable(sb, child, context);
                    generatePushChild(sb, contentVar, elementVar);
                }
                else if (child instanceof com.adobe.internal.fxg.dom.text.BRNode)
                {
                    // e.g. someContent.push(new BreakElement());
                    sb.append("        ").append(contentVar).append(".push(new BreakElement());\r\n");
                }
                else
                {
                    // TODO: Error: Unknown Text Tag
                }
            }
        }

        // e.g. someElement.mxmlChildren = someContent;
        if (parentChildrenVar != null && contentVar != null)
            generateAssignment(sb, parentVar, parentChildrenVar, contentVar);
    }

    /**
     * Generates ActionScript code for child property nodes that represent
     * complex property values. It also generates the property assignment 
     * statement.
     * 
     * @param sb - the ActionScript code generation buffer.
     * @param parentVar - the parent variable that declares the properties
     * @param properties - the text property nodes 
     * @param context - the current context for generating variables in
     * ActionScript code 
     */
    private void generateProperties(StringBuilder sb, String parentVar,
            Map<String, TextNode> properties, Variables context)
    {
        if (properties != null)
        {
            for (Map.Entry<String, TextNode> entry : properties.entrySet())
            {
                String propertyName = entry.getKey();
                TextNode node = entry.getValue();

                if (node instanceof TextLayoutFormatNode)
                {
                    context.setVar(textLayoutFormatType, NodeType.TEXT_LAYOUT_FORMAT);
                    generateTextVariable(sb, node, context);
                    generateAssignment(sb, parentVar, propertyName, context.elementVar);
                }
            }
        }
    }

    /**
     * Converts the attributes specified on an FXG node into ActionScript
     * property initializers.
     * 
     * @param type The ActionScript type for this node.
     * @param attributes The Map of attributes specified on this node.
     * @param sb The code generation buffer.
     * @param variableName The ActionScript variable name representing the
     * instance of this node.
     */
    private void generateAttributes(Type type, Map<String, String> attributes,
            StringBuilder sb, String variableName)
    {
        if (attributes != null)
        {
            for (Map.Entry<String, String> entry : attributes.entrySet())
            {
                String attribName = entry.getKey();
                String attribValue = entry.getValue();
                String thisAttrib = null;

                Property property = type.getProperty(attribName);
                if (property != null)
                {
                    Type propertyType = property.getType();
                    if (propertyType.isAssignableTo(typeTable.stringType)
                        || propertyType == typeTable.objectType
                        || propertyType == typeTable.noType) 
                    {
                        thisAttrib = attribName + " = \"" + attribValue + "\"";
                    }
                    else
                    {
                         thisAttrib = attribName + " = " + attribValue;
                    }
                }
                else if (type.getStyle(attribName) != null)
                {
                    thisAttrib = "setStyle(\"" + attribName + "\", \"" + attribValue + "\")";
                }

                if (thisAttrib != null)
                    sb.append("        " + variableName + '.' + thisAttrib + ";\r\n");
            }
        }
    }

    /**
     * Quotes a String and escapes any special characters so that it can be
     * used as a literal ActionScript value.
     * 
     * @param content the raw String
     * @return a Quoted String suitable for use as an ActionScript value. 
     */
    private String formatString(String content)
    {
        if (content != null)
            return StringUtils.formatString(content);

        return content;
    }

    //--------------------------------------------------------------------------
    //
    // Helper Classes for ActionScript Source Code Generation
    //
    //--------------------------------------------------------------------------

    /**
     * Text node type enumeration.
     */
    private static enum NodeType
    {
        DIV,
        FORMAT,
        IMG,
        LINK,
        PARAGRAPH,
        RICHTEXT,
        SPAN,
        TAB,
        TCY,
        TEXT_LAYOUT_FORMAT
    }

    /**
     * Provides a context of variables in use for ActionScript source
     * generation of a text node and its children.
     */
    private static class Variables
    {
        private Variable divVar;
        private Variable formatVar;
        private Variable imgVar;
        private Variable linkVar;
        private Variable paragraphVar;
        private Variable richTextVar;
        private Variable spanVar;
        private Variable tabVar;
        private Variable tcyVar;
        private Variable textLayoutFormatVar;

        private Variables()
        {
        }

        private void setVar(Type type, NodeType nodeType)
        {
            this.type = type;
            Variable var = getVar(nodeType);
            if (var != null)
            {
                var.count++;
                if (!var.reusableVar)
                {
                    varDeclared = false;
                    elementVar = var.elementVar + var.count;
                    contentVar = var.contentVar + var.count;
                }
                else
                {
                    varDeclared = var.count > 1;
                    elementVar = var.elementVar;
                    contentVar = var.contentVar;
                }
                elementClass = var.elementClass;
                elementChildrenVar = var.elementChildrenVar;
            }
        }

        private Variable getVar(NodeType nodeType)
        {
            switch (nodeType)
            {
                case DIV:
                {
                    if (divVar == null)
                        divVar = new Variable("DivElement", "divElement", "divContent", "mxmlChildren", false);
                    return divVar; 
                }
                case FORMAT:
                {
                    if (formatVar == null)
                        formatVar = new Variable("TextLayoutFormat", "formatElement", null, null, false);
                    return formatVar;
                }
                case IMG:
                {
                    if (imgVar == null)
                        imgVar = new Variable("InlineGraphicElement", "imgElement", null, null, true);
                    return imgVar;
                }
                case LINK:
                {
                    if (linkVar == null)
                        linkVar = new Variable("LinkElement", "linkElement", "linkContent", "mxmlChildren", true);
                    return linkVar;
                }
                case PARAGRAPH:
                {
                    if (paragraphVar == null)
                        paragraphVar = new Variable("ParagraphElement", "paragraphElement", "paragraphContent", "mxmlChildren", true);
                    return paragraphVar;
                }
                case RICHTEXT:
                {
                    if (richTextVar == null)
                        richTextVar = new Variable("RichText", "textElement", "textContent", "content", true);
                    return richTextVar;
                }
                case SPAN:
                {
                    if (spanVar == null)
                        spanVar = new Variable("SpanElement", "spanElement", "spanContent", "mxmlChildren", true);
                    return spanVar;
                }
                case TAB:
                {
                    if (tabVar == null)
                        tabVar = new Variable("TabElement", "tabElement", null, null, true);
                    return tabVar;
                }
                case TCY:
                {
                    if (tcyVar == null)
                        tcyVar = new Variable("TCYElement", "tcyElement", "tcyContent", "mxmlChildren", true);
                    return tcyVar;
                }
                case TEXT_LAYOUT_FORMAT:
                {
                    if (textLayoutFormatVar == null)
                        textLayoutFormatVar = new Variable("TextLayoutFormat", "tlfElement", null, null, true);
                    return textLayoutFormatVar;
                }
            }

            return null;
        }

        private Type type;
        private boolean varDeclared;
        private String elementClass;
        private String elementVar;
        private String contentVar;
        private String elementChildrenVar;
    }

    /**
     * The context for an individual variable.
     */
    private static class Variable
    {
        private Variable(String elementClass, String elementVar, String contentVar, String elementChildrenVar, boolean reusableVar)
        {
            this.elementClass = elementClass;
            this.elementVar = elementVar;
            this.contentVar = contentVar;
            this.elementChildrenVar = elementChildrenVar;
            this.reusableVar = reusableVar;
        }

        private int count;
        private boolean reusableVar;
        private String elementClass;
        private String elementVar;
        private String contentVar;
        private String elementChildrenVar;
    }
}
