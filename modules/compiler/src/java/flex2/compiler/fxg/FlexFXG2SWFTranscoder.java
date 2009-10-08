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
import com.adobe.internal.fxg.dom.richtext.FormatNode;
import com.adobe.internal.fxg.dom.richtext.ImgNode;
import com.adobe.internal.fxg.dom.richtext.LinkActiveFormatNode;
import com.adobe.internal.fxg.dom.richtext.LinkHoverFormatNode;
import com.adobe.internal.fxg.dom.richtext.LinkNode;
import com.adobe.internal.fxg.dom.richtext.LinkNormalFormatNode;
import com.adobe.internal.fxg.dom.richtext.ParagraphNode;
import com.adobe.internal.fxg.dom.richtext.SpanNode;
import com.adobe.internal.fxg.dom.richtext.TCYNode;
import com.adobe.internal.fxg.dom.richtext.TabNode;
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
    private Type formatType;
    private Type imgType;
    private Type linkType;
    private Type linkActiveType;
    private Type linkNormalType;
    private Type linkHoverType;
    private Type richTextType; // Note: TextGraphic and RichText use same type
    private Type paragraphType;
    private Type spanType;
    private Type tabType;
    private Type tcyType;

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
            divType = typeTable.getType(StandardDefs.CLASS_TEXT_DIV);
            formatType = typeTable.getType(StandardDefs.CLASS_TEXT_FORMAT);
            linkType = typeTable.getType(StandardDefs.CLASS_TEXT_LINK);
            imgType = typeTable.getType(StandardDefs.CLASS_TEXT_IMG);
            linkActiveType = typeTable.getType(StandardDefs.CLASS_TEXT_LINK_ACTIVE_FORMAT);
            linkHoverType = typeTable.getType(StandardDefs.CLASS_TEXT_LINK_HOVER_FORMAT);
            linkNormalType = typeTable.getType(StandardDefs.CLASS_TEXT_LINK_NORMAL_FORMAT);
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

            SourceContext context = new SourceContext(packageName, className);
            graphicClass = new FXGSymbolClass();
            graphicClass.setPackageName(context.packageName);
            graphicClass.setClassName(context.className);

            DefineSprite sprite = (DefineSprite)transcode(graphicNode);
            graphicClass.setSymbol(sprite);

            // Create a new sprite class to map to this Graphic's DefineSprite
            StringBuilder buf = new StringBuilder(512);
            buf.append("package ").append(context.packageName).append("\n");
            buf.append("{\n\n");
            buf.append("import spark.core.SpriteVisualElement;\n\n");
            buf.append("public class ").append(context.className).append(" extends SpriteVisualElement\n");
            buf.append("{\n");
            buf.append("    public function ").append(context.className).append("()\n");
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

        if (node.getMaskType() == MaskType.ALPHA && mask != null)
        {
            // Record the display list position that the mask was placed.
            // The ActionScript display list is 0 based, but SWF the depth
            // counter starts at 1, so we subtract 1.
            int maskIndex = parentSprite.depthCounter - 1;
            mask.setMaskIndex(maskIndex);
        }

        return po3;
    }

    @Override
    protected PlaceObject graphicContentNode(GraphicContentNode node)
    {
        if (node.getMaskType() == MaskType.ALPHA && node.mask != null)
        {
            PlaceObject po3 = super.graphicContentNode(node);
            return alphaMaskee(node, po3);
        }

        return super.graphicContentNode(node);
    }

    @Override
    protected PlaceObject group(GroupNode node)
    {
        if (node.getMaskType() == MaskType.ALPHA && node.mask != null)
        {
            PlaceObject po3 = super.group(node);
            return alphaMaskee(node, po3);
        }

        return super.group(node);
    }

    private PlaceObject alphaMaskee(MaskableNode node, PlaceObject po3)
    {
        String className = createUniqueClassName(graphicClass.getClassName() + "_Maskee");

        if (po3 != null)
        {
            int maskIndex = node.getMask().getMaskIndex();
            DefineTag symbol = po3.ref;

            // Create a new SymbolClass to map to this mask's
            // DefineSprite (see below)  
            SourceContext context = new SourceContext(packageName, className);

            FXGSymbolClass symbolClass = new FXGSymbolClass();
            symbolClass.setPackageName(context.packageName);
            symbolClass.setClassName(context.className);

            // Then record this SymbolClass with the top-level graphic
            // SymbolClass so that it will be associated with the FXG asset.
            graphicClass.addAdditionalSymbolClass(symbolClass);

            StringBuilder buf = new StringBuilder(512);
            buf.append("package ").append(context.packageName).append("\n");
            buf.append("{\n\n");
            if (node instanceof GroupNode)
            {
                buf.append("import flash.display.Sprite;\n\n");
                buf.append("public class ").append(context.className).append(" extends Sprite\n");
            }
            else
            {
                buf.append("import flash.display.Shape;\n\n");
                buf.append("public class ").append(context.className).append(" extends Shape\n");
            }
            buf.append("{\n");
            buf.append("    public function ").append(context.className).append("()\n");
            buf.append("    {\n");
            buf.append("        super();\n");
            buf.append("        this.cacheAsBitmap = true;\n");

            if (node instanceof GroupNode)
                buf.append("        this.mask = this.getChildAt(").append(maskIndex).append(");\n");
            else
                buf.append("        this.mask = this.parent.getChildAt(").append(maskIndex).append(");\n");

            buf.append("    }\n");
            buf.append("}\n"); // End class
            buf.append("}\n"); // End package

            symbolClass.setGeneratedSource(buf.toString());
            symbolClass.setSymbol(symbol);
        }

        return po3;
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
            SourceContext context = new SourceContext(packageName, className);

            FXGSymbolClass spriteSymbolClass = new FXGSymbolClass();
            spriteSymbolClass.setPackageName(context.packageName);
            spriteSymbolClass.setClassName(context.className);

            // Then record this SymbolClass with the top-level graphic
            // SymbolClass so that it will be associated with the FXG asset.
            graphicClass.addAdditionalSymbolClass(spriteSymbolClass);

            // Create a DefineSprite to hold this TextGraphic
            DefineSprite textSprite = createDefineSprite(className);
            PlaceObject po3 = placeObject(textSprite, node.createGraphicContext());
            spriteStack.push(textSprite);

            StringBuilder buf = new StringBuilder(1024);
            buf.append("package ").append(context.packageName).append("\n");
            buf.append("{\n\n");
            buf.append("import flashx.textLayout.elements.*;\n");
            buf.append("import flashx.textLayout.formats.TextLayoutFormat;\n");
            buf.append("import spark.components.RichText;\n");
            buf.append("import spark.core.SpriteVisualElement;\n\n");
            buf.append("public class ").append(context.className).append(" extends SpriteVisualElement\n");
            buf.append("{\n");
            buf.append("    public function ").append(context.className).append("()\n");
            buf.append("    {\n");
            buf.append("        super();\n");
            buf.append("        createText();\n");
            buf.append("    }\n\n");
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
            List<TextNode> properties, Variables context)
    {
        if (properties != null)
        {
            for (TextNode node : properties)
            {
                if (node instanceof LinkActiveFormatNode)
                {
                    context.setVar(linkActiveType, NodeType.LINK_ACTIVE_FORMAT);
                    generateTextVariable(sb, node, context);
                    generateAssignment(sb, parentVar, "linkActiveFormat", context.elementVar);
                }
                else if (node instanceof LinkHoverFormatNode)
                {
                    context.setVar(linkHoverType, NodeType.LINK_HOVER_FORMAT);
                    generateTextVariable(sb, node, context);
                    generateAssignment(sb, parentVar, "linkHoverFormat", context.elementVar);
                }
                else if (node instanceof LinkNormalFormatNode)
                {
                    context.setVar(linkNormalType, NodeType.LINK_NORMAL_FORMAT);
                    generateTextVariable(sb, node, context);
                    generateAssignment(sb, parentVar, "linkNormalFormat", context.elementVar);
                }
                else if (node instanceof FormatNode)
                {
                    // <format> is very special as it is a local declaration
                    // rather than a property of RichText so it is not assigned
                    // to the parent.
                    FormatNode formatNode = (FormatNode)node;
                    String name = formatNode.name;
                    if (isValidVariableName(name))
                    {
                        context.setVar(formatType, NodeType.FORMAT);
                        context.elementVar = name;
                        generateTextVariable(sb, formatNode, context);
                    }
                }
            }
        }
    }

    /**
     * Determines whether a String is a valid ActionScript variable name.
     * @param name - the String to test
     * @return true if the value is valid
     */
    private boolean isValidVariableName(String name)
    {
        // FIXME: Enforce ActionScript variable naming rules.
        return (name != null && name.length() > 0);
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
     * Provides the context for a generated ActionScript source.
     */
    private static class SourceContext
    {
        private SourceContext(String packageName, String className)
        {
            this.packageName = packageName;
            this.className = className;
        }

        private final String className;
        private final String packageName;
    }

    /**
     * Text node type enumeration.
     */
    private static enum NodeType
    {
        DIV,
        FORMAT,
        IMG,
        LINK,
        LINK_ACTIVE_FORMAT,
        LINK_HOVER_FORMAT,
        LINK_NORMAL_FORMAT,
        PARAGRAPH,
        RICHTEXT,
        SPAN,
        TAB,
        TCY
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
        private Variable linkActiveFormatVar;
        private Variable linkHoverFormatVar;
        private Variable linkNormalFormatVar;
        private Variable paragraphVar;
        private Variable richTextVar;
        private Variable spanVar;
        private Variable tabVar;
        private Variable tcyVar;

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
                case LINK_ACTIVE_FORMAT:
                {
                    if (linkActiveFormatVar == null)
                        linkActiveFormatVar = new Variable("LinkActiveFormat", "lafElement", null, null, true);
                    return linkActiveFormatVar;
                }
                case LINK_HOVER_FORMAT:
                {
                    if (linkHoverFormatVar == null)
                        linkHoverFormatVar = new Variable("LinkHoverFormat", "lhfElement", null, null, true);
                    return linkHoverFormatVar;
                }
                case LINK_NORMAL_FORMAT:
                {
                    if (linkNormalFormatVar == null)
                        linkNormalFormatVar = new Variable("LinkNormalFormat", "lnfElement", null, null, true);
                    return linkNormalFormatVar;
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
