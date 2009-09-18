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

package com.adobe.internal.fxg.dom;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.adobe.fxg.FXGConstants.*;

import com.adobe.fxg.FXGException;
import com.adobe.fxg.dom.FXGNode;
import com.adobe.fxg.util.FXGLog;
import com.adobe.fxg.util.FXGLogger;
import com.adobe.internal.fxg.dom.richtext.AbstractRichTextNode;
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
import com.adobe.internal.fxg.dom.types.AlignmentBaseline;
import com.adobe.internal.fxg.dom.types.BaselineOffset;
import com.adobe.internal.fxg.dom.types.BaselineShift;
import com.adobe.internal.fxg.dom.types.BlockProgression;
import com.adobe.internal.fxg.dom.types.BreakOpportunity;
import com.adobe.internal.fxg.dom.types.ColorWithEnum;
import com.adobe.internal.fxg.dom.types.DigitCase;
import com.adobe.internal.fxg.dom.types.DigitWidth;
import com.adobe.internal.fxg.dom.types.Direction;
import com.adobe.internal.fxg.dom.types.DominantBaseline;
import com.adobe.internal.fxg.dom.types.FontStyle;
import com.adobe.internal.fxg.dom.types.FontWeight;
import com.adobe.internal.fxg.dom.types.JustificationRule;
import com.adobe.internal.fxg.dom.types.JustificationStyle;
import com.adobe.internal.fxg.dom.types.Kerning;
import com.adobe.internal.fxg.dom.types.LeadingModel;
import com.adobe.internal.fxg.dom.types.LigatureLevel;
import com.adobe.internal.fxg.dom.types.LineBreak;
import com.adobe.internal.fxg.dom.types.NumberAuto;
import com.adobe.internal.fxg.dom.types.NumberInherit;
import com.adobe.internal.fxg.dom.types.TextAlign;
import com.adobe.internal.fxg.dom.types.TextDecoration;
import com.adobe.internal.fxg.dom.types.TextJustify;
import com.adobe.internal.fxg.dom.types.TextRotation;
import com.adobe.internal.fxg.dom.types.TypographicCase;
import com.adobe.internal.fxg.dom.types.VerticalAlign;
import com.adobe.internal.fxg.dom.types.WhiteSpaceCollapse;
import com.adobe.internal.fxg.dom.types.BaselineOffset.BaselineOffsetAsEnum;
import com.adobe.internal.fxg.dom.types.BaselineShift.BaselineShiftAsEnum;
import com.adobe.internal.fxg.dom.types.ColorWithEnum.ColorEnum;
import com.adobe.internal.fxg.dom.types.NumberAuto.NumberAutoAsEnum;
import com.adobe.internal.fxg.dom.types.NumberInherit.NumberInheritAsEnum;
import com.adobe.internal.fxg.swf.TextHelper;

/**
 * Represents a &lt;RichText&gt; element of an FXG Document.
 *
 * @since 2.0
 * @author Min Plunkett
 */
public class RichTextNode extends GraphicContentNode implements TextNode
{
    protected static final double FONTSIZE_MIN_INCLUSIVE = 1.0;
    protected static final double FONTSIZE_MAX_INCLUSIVE = 720.0;
    
    //--------------------------------------------------------------------------
    //
    // Attributes
    //
    //--------------------------------------------------------------------------

    public double width = 0.0;
    public double height = 0.0;

    // Text Flow Attributes
    public BlockProgression blockProgression = BlockProgression.TB;
    public NumberInherit paddingLeft = NumberInherit.newInstance(0.0);
    public NumberInherit paddingRight = NumberInherit.newInstance(0.0);
    public NumberInherit paddingTop = NumberInherit.newInstance(0.0);
    public NumberInherit paddingBottom = NumberInherit.newInstance(0.0);
    public LineBreak lineBreak = LineBreak.TOFIT;
    public NumberInherit columnGap = NumberInherit.newInstance(20.0);
    public NumberAuto columnCount = NumberAuto.newInstance(NumberAutoAsEnum.AUTO);
    public NumberAuto columnWidth = NumberAuto.newInstance(NumberAutoAsEnum.AUTO);
    public BaselineOffset firstBaselineOffset = BaselineOffset.newInstance(BaselineOffsetAsEnum.AUTO);
    public VerticalAlign verticalAlign = VerticalAlign.TOP;

    // Paragraph Attributes
    public TextAlign textAlign = TextAlign.START;
    public TextAlign textAlignLast = TextAlign.START;
    public double textIndent = 0.0;
    public NumberInherit paragraphStartIndent = NumberInherit.newInstance(0.0);
    public NumberInherit paragraphEndIndent = NumberInherit.newInstance(0.0);
    public NumberInherit paragraphSpaceBefore = NumberInherit.newInstance(0.0);
    public NumberInherit paragraphSpaceAfter = NumberInherit.newInstance(0.0);
    public Direction direction = Direction.LTR;
    public JustificationRule justificationRule = JustificationRule.AUTO;
    public JustificationStyle justificationStyle = JustificationStyle.PRIORITIZELEASTADJUSTMENT;
    public TextJustify textJustify = TextJustify.INTERWORD;
    public LeadingModel leadingModel = LeadingModel.AUTO;
    public String tabStops = "";
    
    // Text Leaf Attributes
    public String format = "";
    public String fontFamily = "Arial";
    public double fontSize = 12.0;
    public FontStyle fontStyle = FontStyle.NORMAL;
    public FontWeight fontWeight = FontWeight.NORMAL;
    public Kerning kerning = Kerning.AUTO;
    public double lineHeight = 120.0;
    public TextDecoration textDecoration = TextDecoration.NONE;
    public boolean lineThrough = false;
    public int color = AbstractFXGNode.COLOR_BLACK;
    public double textAlpha = 1.0;
    public WhiteSpaceCollapse whiteSpaceCollapse = WhiteSpaceCollapse.COLLAPSE;
    public NumberInherit backgroundAlpha = NumberInherit.newInstance(1.0);;
    public ColorWithEnum backgroundColor = ColorWithEnum.newInstance(ColorEnum.TRANSPARENT);
    public BaselineShift baselineShift = BaselineShift.newInstance(0.0);
    public BreakOpportunity breakOpportunity = BreakOpportunity.AUTO;
    public DigitCase digitCase = DigitCase.DEFAULT;
    public DigitWidth digitWidth = DigitWidth.DEFAULT;
    public DominantBaseline dominantBaseline = DominantBaseline.AUTO;
    public AlignmentBaseline alignmentBaseline = AlignmentBaseline.USEDOMINANTBASELINE;
    public LigatureLevel ligatureLevel = LigatureLevel.COMMON;
    public String locale = "en";
    public TypographicCase typographicCase = TypographicCase.DEFAULT;
    public double trackingLeft = 0.0;
    public double trackingRight = 0.0;
    public TextRotation textRotation = TextRotation.AUTO;
    
    // LinkFormats property
    public LinkNormalFormatNode linkNormalFormat = null;
    public LinkHoverFormatNode linkHoverFormat = null;
    public LinkActiveFormatNode linkActiveFormat = null;    
    
    // List of Format property
    public Map<String, FormatNode> formatNodeMap = null;

    private boolean contiguous = false;
    
    //--------------------------------------------------------------------------
    //
    // Text Node Attribute Helpers
    //
    //--------------------------------------------------------------------------

    /**
     * The attributes set on this node.
     */
    protected Map<String, String> textAttributes;

    /**
     * @return A Map recording the attribute names and values set on this
     * text node.
     */
    public Map<String, String> getTextAttributes()
    {
        return textAttributes;
    }

    /**
     * This node's child text nodes.
     */
    protected List<TextNode> content;

    /**
     * @return The List of child nodes of this text node. 
     */
    public List<TextNode> getTextChildren()
    {
        return content;
    }

    /**
     * This node's child property nodes.
     */
    protected List<TextNode> properties;

    /**
     * @return The List of child property nodes of this text node.
     */
    public List<TextNode> getTextProperties()
    {
        return properties;
    }

    /**
     * Remember that an attribute was set on this node.
     * 
     * @param name - the unqualified attribute name.
     * @param value - the attribute value.
     */
    protected void rememberAttribute(String name, String value)
    {
        if (textAttributes == null)
            textAttributes = new HashMap<String, String>(4);

        textAttributes.put(name, value);
    }

    /**
     * &lt;RichText&gt; content allows child &lt;p&gt;, &lt;span&gt; and
     * &lt;br /&gt; tags, as well as character data (text content).
     * 
     * @param child - a child FXG node to be added to this node.
     * @throws FXGException if the child is not supported by this node.
     */
    public void addContentChild(FXGNode child)
    {
        if (child instanceof ParagraphNode
                || child instanceof DivNode
                || child instanceof SpanNode
                || child instanceof BRNode
                || child instanceof TabNode
                || child instanceof TCYNode
                || child instanceof LinkNode
                || child instanceof ImgNode
                || child instanceof CDATANode)
        {
            if (child instanceof LinkNode && (((LinkNode)child).href == null))
            {
                // Exception: Missing href attribute in <a> element.
                throw new FXGException(getStartLine(), getStartColumn(), "MissingHref");                
            }   
            
            if (content == null)
            {
                content = new ArrayList<TextNode>();
                contiguous = true;
            }
            
            if (!contiguous)
            {
            	throw new FXGException(child.getStartLine(), child.getStartColumn(), "InvalidRichTextContent");            	
            }

            content.add((TextNode)child);
        }
        
        if (child instanceof AbstractRichTextNode)
            ((AbstractRichTextNode)child).setParent(this);       
    }
    
    //--------------------------------------------------------------------------
    //
    // FXGNode Implementation
    //
    //--------------------------------------------------------------------------

    /**
     * This method is invoked for only non-content children.
     * 
     * @param child - a child FXG node to be added to this node.
     * @throws FXGException if the child is not supported by this node.
     */
    @Override
    public void addChild(FXGNode child)
    {
        if (child instanceof LinkNormalFormatNode)
        {
            if (linkNormalFormat == null)
            {
                linkNormalFormat = (LinkNormalFormatNode)child;
                addProperty(linkNormalFormat);
            }
            else
            {
                // Exception: Multiple LinkFormat elements are not allowed.
                throw new FXGException(getStartLine(), getStartColumn(), "MultipleLinkFormatElements");
            }
        }
        else if (child instanceof LinkHoverFormatNode)
        {
            if (linkHoverFormat == null)
            {
                linkHoverFormat = (LinkHoverFormatNode)child;
                addProperty(linkHoverFormat);
            }
            else
            {
                // Exception: Multiple LinkFormat elements are not allowed.
                throw new FXGException(getStartLine(), getStartColumn(), "MultipleLinkFormatElements");
            }
        }
        else if (child instanceof LinkActiveFormatNode)
        {
            if (linkActiveFormat == null)
            {
                linkActiveFormat = (LinkActiveFormatNode)child;
                addProperty(linkActiveFormat);
            }
            else
            {
                // Exception: Multiple LinkFormat elements are not allowed. 
                throw new FXGException(getStartLine(), getStartColumn(), "MultipleLinkFormatElements");
            }
        }
        else if (child instanceof FormatNode)
        {
        	addFormatNode((FormatNode)child);
        }
        else if (child instanceof CDATANode)
        {
            if(TextHelper.ignorableWhitespace(((CDATANode)child).content))
            {
            	/**
            	 * Ignorable white spaces don't break content contiguous 
            	 * rule and should be ignored.
            	 */
            	return;
            }
            else
            {
            	throw new FXGException(child.getStartLine(), child.getStartColumn(), "InvalidRichTextContent");
            }
        }
        else 
        {
            super.addChild(child);
            contiguous = false;
            return;
        }
        
        if (child instanceof AbstractRichTextNode)
        	((AbstractRichTextNode)child).setParent(this);        
    }

    /**
     * A RichText node can also have special child property nodes that represent
     * complex property values that cannot be set via a simple attribute.
     */
    protected void addProperty(TextNode node)
    {
        if (properties == null)
            properties = new ArrayList<TextNode>(5);

        properties.add(node);
    }

    /**
     * Add FormatNode property.
     * If there are more than one FormatNode with the same name, the last 
     * instance wins. Log a Warning.
     * If a FormatNode is missing "name" attribute, ignore it. Log a warning.
     * @param node - the FormatNode
     */
    protected void addFormatNode(FormatNode node)
    {
    	if (formatNodeMap == null)
    		formatNodeMap = new HashMap<String, FormatNode>(2);
    	
    	String nodeName = node.name;
    	if (nodeName == null || nodeName.trim().equals(""))
    	{
    		/**
    		 * If a FormatNode is missing "name" attribute, ignore it. 
    		 * Log a warning.
    		 */
    	    FXGLog.getLogger().log(FXGLogger.WARN, "IgnoredFormatNode", null, getDocumentName(), startLine, startColumn);
    		return;
    	}
    		
    	if (formatNodeMap.containsKey(nodeName))
    	{
    		/**
    		 * If there are more than one FormatNode with the same name,
    		 * the last instance wins. Log a Warning.
    		 */
    	    FXGLog.getLogger().log(FXGLogger.WARN, "DuplicateFormatNode", null, getDocumentName(), startLine, startColumn);
    	    /**
    	     * Remove the previous node from properties list.
    	     */
    	    properties.remove(formatNodeMap.get(nodeName));
    	    formatNodeMap.remove(nodeName);
    	}
    		
    	formatNodeMap.put(nodeName, node);
    	addProperty(node);
    }

    /**
     * @return The unqualified name of a RichText node, without tag markup.
     */
    public String getNodeName()
    {
        return FXG_RICHTEXT_ELEMENT;
    }

    /**
     * Sets an FXG attribute on this RichText node.
     * 
     * In addition to the attributes supported by all graphic content nodes,
     * RichText supports the following attributes.
     * 
     * <p>
     * <ul>
     * <li><b>width</b> (Number): The width of the text box to render text
     * in.</li>
     * <li><b>height</b> (Number): The height of the text box to render text
     * in.</li>
     * <li><b>paddingLeft</b> (Number): Inset from left edge to content area.
     * Units in pixels, defaults to 0.</li>
     * <li><b>paddingRight</b> (Number): Inset from right edge to content area.
     * Units in pixels, defaults to 0.</li>
     * <li><b>paddingTop</b> (Number): Inset from top edge to content area.
     * Units in pixels, defaults to 0.</li>
     * <li><b>paddingBottom</b> (Number): Inset from bottom edge to content
     * area. Units in pixels, defaults to 0.</li>
     * </ul>
     * </p>
     * @param name - the unqualified attribute name.
     * @param value - the attribute value.
     */
    @Override
    public void setAttribute(String name,  String value)
    {
        if (FXG_WIDTH_ATTRIBUTE.equals(name))
        {
            width = parseDouble(value);
        }
        else if (FXG_HEIGHT_ATTRIBUTE.equals(name))
        {
            height = parseDouble(value);
        }
        else if (FXG_BLOCKPROGRESSION_ATTRIBUTE.equals(name))
        {
            blockProgression = getBlockProgression(this, value);
        }
        else if (FXG_PADDINGLEFT_ATTRIBUTE.equals(name))
        {
            paddingLeft = getNumberInherit(this, value);
        }
        else if (FXG_PADDINGRIGHT_ATTRIBUTE.equals(name))
        {
            paddingRight = getNumberInherit(this, value);
        }
        else if (FXG_PADDINGTOP_ATTRIBUTE.equals(name))
        {
            paddingTop = getNumberInherit(this, value);
        }
        else if (FXG_PADDINGBOTTOM_ATTRIBUTE.equals(name))
        {
            paddingBottom = getNumberInherit(this, value);
        }
        else if (FXG_LINEBREAK_ATTRIBUTE.equals(name))
        {
            lineBreak = getLineBreak(this, value);
        }        
        else if (FXG_COLUMNGAP_ATTRIBUTE.equals(name))
        {
            columnGap = getNumberInherit(this, value);
        }
        else if (FXG_COLUMNCOUNT_ATTRIBUTE.equals(name))
        {
            columnCount = getNumberAutoInt(this, value);
        }
        else if (FXG_COLUMNWIDTH_ATTRIBUTE.equals(name))
        {
            columnWidth = getNumberAutoDbl(this, value);
        }
        else if (FXG_FIRSTBASELINEOFFSET_ATTRIBUTE.equals(name))
        {
            firstBaselineOffset = getFirstBaselineOffset(this, value);
        }
        else if (FXG_VERTICALALIGN_ATTRIBUTE.equals(name))
        {
            verticalAlign = getVerticalAlign(this, value);
        } 
        else if (FXG_TEXTALIGN_ATTRIBUTE.equals(name))
        {
            textAlign = getTextAlign(this, value);
        }
        else if (FXG_TEXTALIGNLAST_ATTRIBUTE.equals(name))
        {
            textAlignLast = getTextAlign(this, value);
        }
        else if (FXG_TEXTINDENT_ATTRIBUTE.equals(name))
        {
            textIndent = parseDouble(value);
        }
        else if (FXG_PARAGRAPHSTARTINDENT_ATTRIBUTE.equals(name))
        {
            paragraphStartIndent = getNumberInherit(this, value);
        }
        else if (FXG_PARAGRAPHENDINDENT_ATTRIBUTE.equals(name))
        {
            paragraphEndIndent = getNumberInherit(this, value);
        }
        else if (FXG_PARAGRAPHSPACEBEFORE_ATTRIBUTE.equals(name))
        {
            paragraphSpaceBefore = getNumberInherit(this, value);
        }
        else if (FXG_PARAGRAPHSPACEAFTER_ATTRIBUTE.equals(name))
        {
            paragraphSpaceAfter = getNumberInherit(this, value);
        }
        else if (FXG_DIRECTION_ATTRIBUTE.equals(name))
        {
            direction = getDirection(this, value);
        }
        else if (FXG_JUSTIFICATIONRULE_ATTRIBUTE.equals(name))
        {
            justificationRule = getJustificationRule(this, value);
        }
        else if (FXG_JUSTIFICATIONSTYLE_ATTRIBUTE.equals(name))
        {
            justificationStyle = getJustificationStyle(this, value);
        }
        else if (FXG_TEXTJUSTIFY_ATTRIBUTE.equals(name))
        {
            textJustify = getTextJustify(this, value);
        }
        else if (FXG_LEADINGMODEL_ATTRIBUTE.equals(name))
        {
            leadingModel = getLeadingModel(this, value);
        }        
        else if (FXG_TABSTOPS_ATTRIBUTE.equals(name))
        {
            tabStops = value;
        } 
        else if (FXG_FORMAT_ATTRIBUTE.equals(name))
        {
            format = value;
        }
        else if (FXG_FONTFAMILY_ATTRIBUTE.equals(name))
        {
            fontFamily = value;
        }
        else if (FXG_FONTSIZE_ATTRIBUTE.equals(name))
        {
            fontSize = parseDouble(value, FONTSIZE_MIN_INCLUSIVE, FONTSIZE_MAX_INCLUSIVE, fontSize);
        }
        else if (FXG_FONTSTYLE_ATTRIBUTE.equals(name))
        {
            fontStyle = getFontStyle(this, value);
        }
        else if (FXG_FONTWEIGHT_ATTRIBUTE.equals(name))
        {
            fontWeight = getFontWeight(this, value);
        }
        else if (FXG_KERNING_ATTRIBUTE.equals(name))
        {
            kerning = getKerning(this, value);
        }        
        else if (FXG_LINEHEIGHT_ATTRIBUTE.equals(name))
        {
            lineHeight = parsePercent(value);
        }
        else if (FXG_TEXTDECORATION_ATTRIBUTE.equals(name))
        {
            textDecoration = getTextDecoration(this, value);
        }
        else if ( FXG_LINETHROUGH_ATTRIBUTE.equals(name))
        {
            lineThrough = parseBoolean(value);
        }                   
        else if (FXG_COLOR_ATTRIBUTE.equals(name))
        {
            color = parseRGB(value, color);
        }
        else if (FXG_TEXTALPHA_ATTRIBUTE.equals(name))
        {
            textAlpha = parseDouble(value, ALPHA_MIN_INCLUSIVE, ALPHA_MAX_INCLUSIVE, textAlpha);
        }
        else if (FXG_WHITESPACECOLLAPSE_ATTRIBUTE.equals(name))
        {
            whiteSpaceCollapse = getWhiteSpaceCollapse(this, value);
        }
        else if (FXG_BACKGROUNDALPHA_ATTRIBUTE.equals(name))
        {
        	backgroundAlpha = getAlphaInherit(this, value, ALPHA_MIN_INCLUSIVE, ALPHA_MAX_INCLUSIVE, backgroundAlpha.getNumberInheritAsDbl());
        }
        else if (FXG_BACKGROUNDCOLOR_ATTRIBUTE.equals(name))
        {
            backgroundColor = getColorWithEnum(this, value);
        }
        else if (FXG_BASELINESHIFT_ATTRIBUTE.equals(name))
        {
            baselineShift = getBaselineShift(this, value);
        }
        else if (FXG_BREAKOPPORTUNITY_ATTRIBUTE.equals(name))
        {
            breakOpportunity = getBreakOpportunity(this, value);
        }
        else if (FXG_DIGITCASE_ATTRIBUTE.equals(name))
        {
            digitCase = getDigitCase(this, value);
        }
        else if (FXG_DIGITWIDTH_ATTRIBUTE.equals(name))
        {
            digitWidth = getDigitWidth(this, value);
        }
        else if (FXG_DOMINANTBASELINE_ATTRIBUTE.equals(name))
        {
            dominantBaseline = getDominantBaseline(this, value);
        }
        else if (FXG_ALIGNMENTBASELINE_ATTRIBUTE.equals(name))
        {
            alignmentBaseline = getAlignmentBaseline(this, value);
        }
        else if (FXG_LIGATURELEVEL_ATTRIBUTE.equals(name))
        {
            ligatureLevel = getLigatureLevel(this, value);
        }
        else if (FXG_LOCALE_ATTRIBUTE.equals(name))
        {
            locale = value;
        }
        else if (FXG_TYPOGRAPHICCASE_ATTRIBUTE.equals(name))
        {
            typographicCase = getTypographicCase(this, value);
        }        
        else if (FXG_TRACKINGLEFT_ATTRIBUTE.equals(name))
        {
            trackingLeft = parsePercent(value);
        }
        else if (FXG_TRACKINGRIGHT_ATTRIBUTE.equals(name))
        {
            trackingRight = parsePercent(value);
        } 
        else if (FXG_TEXTROTATION_ATTRIBUTE.equals(name))
        {
        	textRotation = getTextRotation(this, value);
        }
        else if (FXG_ID_ATTRIBUTE.equals(name))
        {
            //id = value;
        }        
        else
        {
        	super.setAttribute(name, value);
        }

        // Remember that this attribute was set on this node.
        rememberAttribute(name, value);
    }

    /**
     * Convert an FXG String value to a BlockProgression enumeration.
     * 
     * @param value - the FXG String value.
     * @return the matching BlockProgression enum.
     * @throws FXGException if the String did not match a known
     * BlockProgression enum.
     */
    public static BlockProgression getBlockProgression(FXGNode node, String value)
    {
        if (FXG_BLOCKPROGRESSION_TB_VALUE.equals(value))
            return BlockProgression.TB;
        else if (FXG_BLOCKPROGRESSION_RL_VALUE.equals(value))
            return BlockProgression.RL;
        else
            //Exception: Unknown block progression: {0}
            throw new FXGException(node.getStartLine(), node.getStartColumn(), "UnknownBlockProgression", value);
    }     
    
    /**
     * Convert an FXG String value to a LineBreak enumeration.
     * 
     * @param value - the FXG String value.
     * @return the matching LineBreak enum.
     * @throws FXGException if the String did not match a known
     * LineBreak enum.
     */
    public static LineBreak getLineBreak(FXGNode node, String value)
    {
        if (FXG_LINEBREAK_TOFIT_VALUE.equals(value))
        {
            return LineBreak.TOFIT;
        }
        else if (FXG_LINEBREAK_EXPLICIT_VALUE.equals(value))
        {
            return LineBreak.EXPLICIT;
        }
        else if (FXG_INHERIT_VALUE.equals(value))
        {
            return LineBreak.INHERIT;
        }
        else
        {
            //Exception: Unknown line break: {0}
            throw new FXGException(node.getStartLine(), node.getStartColumn(), "UnknownLineBreak", value);
        }
    }
    
    /**
     * Convert an FXG String value to a v enumeration.
     * 
     * @param value - the FXG String value.
     * @return the matching BaselineOffset rule.
     * @throws FXGException if the String did not match a known
     * BaselineOffset rule.
     */
    private BaselineOffset getFirstBaselineOffset(FXGNode node, String value)
    {
        if (FXG_BASELINEOFFSET_AUTO_VALUE.equals(value))
        {
            return BaselineOffset.newInstance(BaselineOffsetAsEnum.AUTO);
        }
        else if (FXG_BASELINEOFFSET_ASCENT_VALUE.equals(value))
        {
            return BaselineOffset.newInstance(BaselineOffsetAsEnum.ASCENT);
        }
        else if (FXG_BASELINEOFFSET_LINEHEIGHT_VALUE.equals(value))
        {
            return BaselineOffset.newInstance(BaselineOffsetAsEnum.LINEHEIGHT);
        }
        else
        {
        	try
        	{
        		return BaselineOffset.newInstance(parseDouble(value));
        	}
        	catch(NumberFormatException e)
        	{
	            //Exception: Unknown baseline offset: {0}
	            throw new FXGException(node.getStartLine(), node.getStartColumn(), "UnknownBaselineOffset", value);
        	}
        }
    }
    
    /**
     * Convert an FXG String value to a VerticalAlign enumeration.
     * 
     * @param value - the FXG String value.
     * @return the matching VerticalAlign rule.
     * @throws FXGException if the String did not match a known
     * VerticalAlign rule.
     */
    public static VerticalAlign getVerticalAlign(FXGNode node, String value)
    {
        if (FXG_VERTICALALIGN_TOP_VALUE.equals(value))
            return VerticalAlign.TOP;
        else if (FXG_VERTICALALIGN_BOTTOM_VALUE.equals(value))
            return VerticalAlign.BOTTOM;
        else if (FXG_VERTICALALIGN_MIDDLE_VALUE.equals(value))
            return VerticalAlign.MIDDLE;
        else if (FXG_VERTICALALIGN_JUSTIFY_VALUE.equals(value))
            return VerticalAlign.JUSTIFY;
        else if (FXG_INHERIT_VALUE.equals(value))
            return VerticalAlign.INHERIT;
        else
            //Exception: Unknown vertical align: {0}
            throw new FXGException(node.getStartLine(), node.getStartColumn(), "UnknownVerticalAlign", value);
    }
    
    /**
     * Convert an FXG String value to a NumberAuto object.
     * 
     * @param value - the FXG String value.
     * @return the matching NumberAuto rule.
     * @throws FXGException if the String did not match a known
     * NumberAuto rule.
     */
    private NumberAuto getNumberAutoDbl(FXGNode node, String value)
    {
        try
        {
            return NumberAuto.newInstance(parseDouble(value));            
        }catch(NumberFormatException e)
        {
            if (FXG_NUMBERAUTO_AUTO_VALUE.equals(value))
                return NumberAuto.newInstance(NumberAutoAsEnum.AUTO);
            else if (FXG_INHERIT_VALUE.equals(value))
            	return NumberAuto.newInstance(NumberAutoAsEnum.INHERIT);
            else
                //Exception: Unknown number auto: {0}
                throw new FXGException(node.getStartLine(), node.getStartColumn(), "UnknownNumberAuto", value);            
        }
    }
    
    /**
     * Convert an FXG String value to a NumberAuto object.
     * 
     * @param value - the FXG String value.
     * @return the matching NumberAuto rule.
     * @throws FXGException if the String did not match a known
     * NumberAuto rule.
     */
    private NumberAuto getNumberAutoInt(FXGNode node, String value)
    {
        try
        {
            return NumberAuto.newInstance(parseInt(value));            
        }catch(NumberFormatException e)
        {
            if (FXG_NUMBERAUTO_AUTO_VALUE.equals(value))
                return NumberAuto.newInstance(NumberAutoAsEnum.AUTO);
            else if (FXG_INHERIT_VALUE.equals(value))
            	return NumberAuto.newInstance(NumberAutoAsEnum.INHERIT);
            else
                //Exception: Unknown number auto: {0}
                throw new FXGException(node.getStartLine(), node.getStartColumn(), "UnknownNumberAuto", value);            
        }
    }
    
    /**
     * Convert an FXG String value to a TextAlign enumeration.
     * 
     * @param value - the FXG String value.
     * @return the matching TextAlign rule.
     * @throws FXGException if the String did not match a known
     * TextAlign rule.
     */
    public static TextAlign getTextAlign(FXGNode node, String value)
    {
        if (FXG_TEXTALIGN_START_VALUE.equals(value))
            return TextAlign.START;
        else if (FXG_TEXTALIGN_END_VALUE.equals(value))
            return TextAlign.END;
        else if (FXG_TEXTALIGN_LEFT_VALUE.equals(value))
            return TextAlign.LEFT;
        else if (FXG_TEXTALIGN_CENTER_VALUE.equals(value))
            return TextAlign.CENTER;
        else if (FXG_TEXTALIGN_RIGHT_VALUE.equals(value))
            return TextAlign.RIGHT;
        else if (FXG_TEXTALIGN_JUSTIFY_VALUE.equals(value))
            return TextAlign.JUSTIFY;
        else
            //Exception: Unknown text align: {0}
            throw new FXGException(node.getStartLine(), node.getStartColumn(), "UnknownTextAlign", value);
    }
    
    /**
     * Convert an FXG String value to a Direction enumeration.
     * 
     * @param value - the FXG String value.
     * @return the matching Direction rule.
     * @throws FXGException if the String did not match a known
     * Direction rule.
     */
    public static Direction getDirection(FXGNode node, String value)
    {
        if (FXG_DIRECTION_LTR_VALUE.equals(value))
            return Direction.LTR;
        else if (FXG_DIRECTION_RTL_VALUE.equals(value))
            return Direction.RTL;
        else
            //Exception: Unknown direction: {0}
            throw new FXGException(node.getStartLine(), node.getStartColumn(), "UnknownDirection", value);
    }

    /**
     * Convert an FXG String value to a JustificationRule enumeration.
     * 
     * @param value - the FXG String value.
     * @return the matching JustificationRule rule.
     * @throws FXGException if the String did not match a known
     * JustificationRule rule.
     */
    public static JustificationRule getJustificationRule(FXGNode node, String value)
    {
        if (FXG_JUSTIFICATIONRULE_AUTO_VALUE.equals(value))
            return JustificationRule.AUTO;
        else if (FXG_JUSTIFICATIONRULE_SPACE_VALUE.equals(value))
            return JustificationRule.SPACE;
        else if (FXG_JUSTIFICATIONRULE_EASTASIAN_VALUE.equals(value))
            return JustificationRule.EASTASIAN;
        else
            //Exception: Unknown justification rule: {0}
            throw new FXGException(node.getStartLine(), node.getStartColumn(), "UnknownJustificationRule", value);
    }
    
    /**
     * Convert an FXG String value to a JustificationStyle enumeration.
     * 
     * @param value - the FXG String value.
     * @return the matching JustificationStyle rule.
     * @throws FXGException if the String did not match a known
     * JustificationStyle rule.
     */
    public static JustificationStyle getJustificationStyle(FXGNode node, String value)
    {
        if (FXG_JUSTIFICATIONSTYLE_AUTO_VALUE.equals(value))
            return JustificationStyle.AUTO;
        else if (FXG_JUSTIFICATIONSTYLE_AUTO_VALUE.equals(value))
            return JustificationStyle.PRIORITIZELEASTADJUSTMENT;
        else if (FXG_JUSTIFICATIONSTYLE_PUSHINKINSOKU_VALUE.equals(value))
            return JustificationStyle.PUSHINKINSOKU;
        else if (FXG_JUSTIFICATIONSTYLE_PUSHOUTONLY_VALUE.equals(value))
            return JustificationStyle.PUSHOUTONLY;
        else
            //Exception: Unknown justification style: {0}
            throw new FXGException(node.getStartLine(), node.getStartColumn(), "UnknownJustificationStyle", value);
    }
    
    /**
     * Convert an FXG String value to a TextJustify enumeration.
     * 
     * @param value - the FXG String value.
     * @return the matching TextJustify rule.
     * @throws FXGException if the String did not match a known
     * TextJustify rule.
     */
    public static TextJustify getTextJustify(FXGNode node, String value)
    {
        if (FXG_TEXTJUSTIFY_INTERWORD_VALUE.equals(value))
            return TextJustify.INTERWORD;
        else if (FXG_TEXTJUSTIFY_DISTRIBUTE_VALUE.equals(value))
            return TextJustify.DISTRIBUTE;
        else
            //Exception: Unknown text justify: {0}
            throw new FXGException(node.getStartLine(), node.getStartColumn(), "UnknownTextJustify", value);
    }    
    
    /**
     * Convert an FXG String value to a LeadingModel enumeration.
     * 
     * @param value - the FXG String value.
     * @return the matching LeadingModel rule.
     * @throws FXGException if the String did not match a known
     * LeadingModel rule.
     */
    public static LeadingModel getLeadingModel(FXGNode node, String value)
    {
        if (FXG_LEADINGMODEL_AUTO_VALUE.equals(value))
            return LeadingModel.AUTO;
        else if (FXG_LEADINGMODEL_ROMANUP_VALUE.equals(value))
            return LeadingModel.ROMANUP;
        else if (FXG_LEADINGMODEL_IDEOGRAPHICTOPUP_VALUE.equals(value))
            return LeadingModel.IDEOGRAPHICTOPUP;
        else if (FXG_LEADINGMODEL_IDEOGRAPHICCENTERUP_VALUE.equals(value))
            return LeadingModel.IDEOGRAPHICCENTERUP;
        else if (FXG_LEADINGMODEL_ASCENTDESCENTUP_VALUE.equals(value))
            return LeadingModel.ASCENTDESCENTUP;
        else if (FXG_LEADINGMODEL_IDEOGRAPHICTOPDOWN_VALUE.equals(value))
            return LeadingModel.IDEOGRAPHICTOPDOWN;
        else if (FXG_LEADINGMODEL_IDEOGRAPHICCENTERDOWN_VALUE.equals(value))
            return LeadingModel.IDEOGRAPHICCENTERDOWN;
        else
            //Exception: Unknown leading model: {0}
            throw new FXGException(node.getStartLine(), node.getStartColumn(), "UnknownLeadingModel", value);
    }    
    
    /**
     * Convert an FXG String value to a NumberInherit enumeration.
     * 
     * @param value - the FXG String value.
     * @return the matching NumberInherit rule.
     * @throws FXGException if the String did not match a known
     * NumberInherit rule.
     */
    private NumberInherit getNumberInherit(FXGNode node, String value)
    {
        try
        {
            return NumberInherit.newInstance(parseDouble(value));            
        }catch(NumberFormatException e)
        {
            if (FXG_INHERIT_VALUE.equals(value))
                return NumberInherit.newInstance(NumberInheritAsEnum.INHERIT);
            else
                //Exception: Unknown number inherit: {0}
                throw new FXGException(node.getStartLine(), node.getStartColumn(), "UnknownNumberInherit", value);            
        }
    }
    
    /**
     * Convert an FXG String value to a NumberInherit object.
     * 
     * @param value - the FXG String value.
     * @return the matching NumberInherit rule.
     * @throws FXGException if the String did not match a known
     * NumberInherit rule.
     */
    private ColorWithEnum getColorWithEnum(FXGNode node, String value)        
    {
        if (FXG_COLORWITHENUM_TRANSPARENT_VALUE.equals(value))
        {
            return ColorWithEnum.newInstance(ColorEnum.TRANSPARENT);
        }
        else if (FXG_INHERIT_VALUE.equals(value))
        {
            return ColorWithEnum.newInstance(ColorEnum.INHERIT);
        }
        else
        {
            return ColorWithEnum.newInstance(parseRGB(value));           
        }
    }

    /**
     * Convert an FXG String value to a FontStyle enumeration.
     * 
     * @param value - the FXG String value.
     * @return the matching FontStyle value.
     * @throws FXGException if the String did not match a known
     * FontStyle value.
     */
    public static FontStyle getFontStyle(FXGNode node, String value)
    {
        if (FXG_FONTSTYLE_NORMAL_VALUE.equals(value))
            return FontStyle.NORMAL;
        else if (FXG_FONTSTYLE_ITALIC_VALUE.equals(value))
            return FontStyle.ITALIC;
        else
            //Exception: Unknown font style: {0}
            throw new FXGException(node.getStartLine(), node.getStartColumn(), "UnknownFontStyle", value);
    }
    
    /**
     * Convert an FXG String value to a FontWeight enumeration.
     * 
     * @param value - the FXG String value.
     * @return the matching FontWeight value.
     * @throws FXGException if the String did not match a known
     * FontWeight value.
     */
    public static FontWeight getFontWeight(FXGNode node, String value)
    {
        if (FXG_FONTWEIGHT_NORMAL_VALUE.equals(value))
            return FontWeight.NORMAL;
        else if (FXG_FONTWEIGHT_BOLD_VALUE.equals(value))
            return FontWeight.BOLD;
        else
            //Exception: Unknown font weight: {0}
            throw new FXGException(node.getStartLine(), node.getStartColumn(), "UnknownFontWeight", value);
    }
    
    /**
     * Convert an FXG String value to a TextDecoration enumeration.
     * 
     * @param value - the FXG String value.
     * @return the matching TextDecoration value.
     * @throws FXGException if the String did not match a known
     * TextDecoration value.
     */
    public static TextDecoration getTextDecoration(FXGNode node, String value)
    {
        if (FXG_TEXTDECORATION_NONE_VALUE.equals(value))
            return TextDecoration.NONE;
        else if (FXG_TEXTDECORATION_UNDERLINE_VALUE.equals(value))
            return TextDecoration.UNDERLINE;
        else
            //Exception: Unknown text decoration: {0}
            throw new FXGException(node.getStartLine(), node.getStartColumn(), "UnknownTextDecoration", value);
    }
    
    /**
     * Convert an FXG String value to a Kerning enumeration.
     * 
     * @param value - the FXG String value.
     * @return the matching Kerning value.
     * @throws FXGException if the String did not match a known
     * Kerning value.
     */
    public static Kerning getKerning(FXGNode node, String value)
    {
        if (FXG_KERNING_AUTO_VALUE.equals(value))
            return Kerning.AUTO;
        else if (FXG_KERNING_ON_VALUE.equals(value))
            return Kerning.ON;
        else if (FXG_KERNING_OFF_VALUE.equals(value))
            return Kerning.OFF;
        else
            //Exception: Unknown kerning: {0}
            throw new FXGException(node.getStartLine(), node.getStartColumn(), "UnknownKerning", value);
    }

    /**
     * Convert an FXG String value to a WhiteSpaceCollapse enumeration.
     * 
     * @param value - the FXG String value.
     * @return the matching WhiteSpaceCollapse rule.
     * @throws FXGException if the String did not match a known
     * WhiteSpaceCollapse rule.
     */
    public static WhiteSpaceCollapse getWhiteSpaceCollapse(FXGNode node, String value)
    {
        if (FXG_WHITESPACE_PRESERVE_VALUE.equals(value))
            return WhiteSpaceCollapse.PRESERVE;
        else if (FXG_WHITESPACE_COLLAPSE_VALUE.equals(value))
            return WhiteSpaceCollapse.COLLAPSE;
        else
            //Exception: Unknown white space collapse: {0}
            throw new FXGException(node.getStartLine(), node.getStartColumn(), "UnknownWhiteSpaceCollapse", value);
    }

    /**
     * Convert an FXG String value to a BreakOpportunity enumeration.
     * 
     * @param value - the FXG String value.
     * @return the matching BreakOpportunity rule.
     * @throws FXGException if the String did not match a known
     * BreakOpportunity rule.
     */
    public static BreakOpportunity getBreakOpportunity(FXGNode node, String value)
    {
        if (FXG_BREAKOPPORTUNITY_AUTO_VALUE.equals(value))
            return BreakOpportunity.AUTO;
        else if (FXG_BREAKOPPORTUNITY_ANY_VALUE.equals(value))
            return BreakOpportunity.ANY;
        else if (FXG_BREAKOPPORTUNITY_NONE_VALUE.equals(value))
            return BreakOpportunity.NONE;
        else if (FXG_BREAKOPPORTUNITY_ALL_VALUE.equals(value))
            return BreakOpportunity.ALL;
        else
            //Exception: Unknown break opportunity: {0}
            throw new FXGException(node.getStartLine(), node.getStartColumn(), "UnknownBreakOpportunity", value);
    }
    
    /**
     * Convert an FXG String value to a DigitCase enumeration.
     * 
     * @param value - the FXG String value.
     * @return the matching DigitCase rule.
     * @throws FXGException if the String did not match a known
     * DigitCase rule.
     */
    public static DigitCase getDigitCase(FXGNode node, String value)
    {
        if (FXG_DIGITCASE_DEFAULT_VALUE.equals(value))
            return DigitCase.DEFAULT;
        else if (FXG_DIGITCASE_LINING_VALUE.equals(value))
            return DigitCase.LINING;
        else if (FXG_DIGITCASE_OLDSTYLE_VALUE.equals(value))
            return DigitCase.OLDSTYLE;
        else
            //Exception: Unknown digit case: {0}
            throw new FXGException(node.getStartLine(), node.getStartColumn(), "UnknownDigitCase", value);
    }
    
    /**
     * Convert an FXG String value to a DigitWidth enumeration.
     * 
     * @param value - the FXG String value.
     * @return the matching DigitWidth rule.
     * @throws FXGException if the String did not match a known
     * DigitWidth rule.
     */
    public static DigitWidth getDigitWidth(FXGNode node, String value)
    {
        if (FXG_DIGITWIDTH_DEFAULT_VALUE.equals(value))
            return DigitWidth.DEFAULT;
        else if (FXG_DIGITWIDTH_PROPORTIONAL_VALUE.equals(value))
            return DigitWidth.PROPORTIONAL;
        else if (FXG_DIGITWIDTH_TABULAR_VALUE.equals(value))
            return DigitWidth.TABULAR;
        else
            //Exception: Unknown digit width: {0}
            throw new FXGException(node.getStartLine(), node.getStartColumn(), "UnknownDigitWidth", value);
    }

    /**
     * Convert an FXG String value to a DominantBaseline enumeration.
     * 
     * @param value - the FXG String value.
     * @return the matching DominantBaseline rule.
     * @throws FXGException if the String did not match a known
     * DominantBaseline rule.
     */
    public static DominantBaseline getDominantBaseline(FXGNode node, String value)
    {
        if (FXG_DOMINANTBASELINE_AUTO_VALUE.equals(value))
            return DominantBaseline.AUTO;
        else if (FXG_DOMINANTBASELINE_ROMAN_VALUE.equals(value))
            return DominantBaseline.ROMAN;
        else if (FXG_DOMINANTBASELINE_ASCENT_VALUE.equals(value))
            return DominantBaseline.ASCENT;
        else if (FXG_DOMINANTBASELINE_DESCENT_VALUE.equals(value))
            return DominantBaseline.DESCENT;
        else if (FXG_DOMINANTBASELINE_IDEOGRAPHICTOP_VALUE.equals(value))
            return DominantBaseline.IDEOGRAPHICTOP;
        else if (FXG_DOMINANTBASELINE_IDEOGRAPHICCENTER_VALUE.equals(value))
            return DominantBaseline.IDEOGRAPHICCENTER;
        else if (FXG_DOMINANTBASELINE_IDEOGRAPHICBOTTOM_VALUE.equals(value))
            return DominantBaseline.IDEOGRAPHICBOTTOM;
        else
            //Exception: Unknown dominant baseline: {0}
            throw new FXGException(node.getStartLine(), node.getStartColumn(), "UnknownDominantBaseline", value);
    }
    
    /**
     * Convert an FXG String value to a AlignmentBaseline enumeration.
     * 
     * @param value - the FXG String value.
     * @return the matching AlignmentBaseline rule.
     * @throws FXGException if the String did not match a known
     * AlignmentBaseline rule.
     */
    public static AlignmentBaseline getAlignmentBaseline(FXGNode node, String value)
    {
        if (FXG_ALIGNMENTBASELINE_USEDOMINANTBASELINE_VALUE.equals(value))
            return AlignmentBaseline.USEDOMINANTBASELINE;
        else if (FXG_ALIGNMENTBASELINE_ROMAN_VALUE.equals(value))
            return AlignmentBaseline.ROMAN;
        else if (FXG_ALIGNMENTBASELINE_ASCENT_VALUE.equals(value))
            return AlignmentBaseline.ASCENT;
        else if (FXG_ALIGNMENTBASELINE_DESCENT_VALUE.equals(value))
            return AlignmentBaseline.DESCENT;
        else if (FXG_ALIGNMENTBASELINE_IDEOGRAPHICTOP_VALUE.equals(value))
            return AlignmentBaseline.IDEOGRAPHICTOP;
        else if (FXG_ALIGNMENTBASELINE_IDEOGRAPHICCENTER_VALUE.equals(value))
            return AlignmentBaseline.IDEOGRAPHICCENTER;
        else if (FXG_ALIGNMENTBASELINE_IDEOGRAPHICBOTTOM_VALUE.equals(value))
            return AlignmentBaseline.IDEOGRAPHICBOTTOM;
        else
            //Exception: Unknown alignment baseline: {0}
            throw new FXGException(node.getStartLine(), node.getStartColumn(), "UnknownAlignmentBaseline", value);
    }
    
    /**
     * Convert an FXG String value to a LigatureLevel enumeration.
     * 
     * @param value - the FXG String value.
     * @return the matching LigatureLevel rule.
     * @throws FXGException if the String did not match a known
     * LigatureLevel rule.
     */
    public static LigatureLevel getLigatureLevel(FXGNode node, String value)
    {
        if (FXG_LIGATURELEVEL_MINIMUM_VALUE.equals(value))
            return LigatureLevel.MINIMUM;
        else if (FXG_LIGATURELEVEL_COMMON_VALUE.equals(value))
            return LigatureLevel.COMMON;
        else if (FXG_LIGATURELEVEL_UNCOMMON_VALUE.equals(value))
            return LigatureLevel.UNCOMMON;
        else if (FXG_LIGATURELEVEL_EXOTIC_VALUE.equals(value))
            return LigatureLevel.EXOTIC;
        else
            //Exception: Unknown ligature level: {0}
            throw new FXGException(node.getStartLine(), node.getStartColumn(), "UnknownLigatureLevel", value);
    }
    
    
    /**
     * Convert an FXG String value to a TypographicCase enumeration.
     * 
     * @param value - the FXG String value.
     * @return the matching TypographicCase rule.
     * @throws FXGException if the String did not match a known
     * TypographicCase rule.
     */
    public static TypographicCase getTypographicCase(FXGNode node, String value)
    {
        if (FXG_TYPOGRAPHICCASE_DEFAULT_VALUE.equals(value))
            return TypographicCase.DEFAULT;
        else if (FXG_TYPOGRAPHICCASE_CAPSTOSMALLCAPS_VALUE.equals(value))
            return TypographicCase.CAPSTOSMALLCAPS;
        else if (FXG_TYPOGRAPHICCASE_UPPERCASE_VALUE.equals(value))
            return TypographicCase.UPPERCASE;
        else if (FXG_TYPOGRAPHICCASE_LOWERCASE_VALUE.equals(value))
            return TypographicCase.LOWERCASE;
        else if (FXG_TYPOGRAPHICCASE_LOWERCASETOSMALLCAPS_VALUE.equals(value))
            return TypographicCase.LOWERCASETOSMALLCAPS;
        else
            //Exception: Unknown typographic case: {0}
            throw new FXGException(node.getStartLine(), node.getStartColumn(), "UnknownTypographicCase", value);
    }
           
    /**
     * Convert an FXG String value to a BaselineShift enumeration.
     * 
     * @param value - the FXG String value.
     * @return the matching BaselineShift rule.
     * @throws FXGException if the String did not match a known
     * BaselineShift rule.
     */
    private BaselineShift getBaselineShift(FXGNode node, String value)
    {
        try
        {
            return BaselineShift.newInstance(parsePercent(value));            
        }
        catch(NumberFormatException e)
        {
            if (FXG_BASELINESHIFT_SUPERSCRIPT_VALUE.equals(value))
            {
                return BaselineShift.newInstance(BaselineShiftAsEnum.SUPERSCRIPT);
            }
            else if (FXG_BASELINESHIFT_SUBSCRIPT_VALUE.equals(value))
            {
                return BaselineShift.newInstance(BaselineShiftAsEnum.SUBSCRIPT);
            }
            else
            {
                //Exception: Unknown baseline shift: {0}
                throw new FXGException(node.getStartLine(), node.getStartColumn(), "UnknownBaselineShift", value);            
            }
        }
    }
    
    /**
     * Convert an FXG String value to a TextRotation enumeration.
     * 
     * @param value - the FXG String value.
     * @return the matching TextRotation rule.
     * @throws FXGException if the String did not match a known
     * TextRotation rule.
     */
    private TextRotation getTextRotation(FXGNode node, String value)
    {
        if (FXG_TEXTROTATION_AUTO_VALUE.equals(value))
            return TextRotation.AUTO;
        else if (FXG_TEXTROTATION_ROTATE_0_VALUE.equals(value))
            return TextRotation.ROTATE_0;
        else if (FXG_TEXTROTATION_ROTATE_90_VALUE.equals(value))
            return TextRotation.ROTATE_90;
        else if (FXG_TEXTROTATION_ROTATE_180_VALUE.equals(value))
            return TextRotation.ROTATE_180;
        else if (FXG_TEXTROTATION_ROTATE_270_VALUE.equals(value))
            return TextRotation.ROTATE_270;
        else
            //Exception: Unknown text rotation: {0}
            throw new FXGException(node.getStartLine(), node.getStartColumn(), "UnknownTextRotation", value);
    }
    
    /**
     * Convert an FXG String value to a NumberInherit object.
     * 
     * @param value - the FXG String value.
     * @return the matching NumberInherit rule.
     * @throws FXGException if the String did not match a known
     * NumberInherit rule.
     */
    private NumberInherit getAlphaInherit(FXGNode node, String value, double min, double max, double defaultValue)        
    {
        try
        {
            return NumberInherit.newInstance(parseDouble(value, ALPHA_MIN_INCLUSIVE, ALPHA_MAX_INCLUSIVE, defaultValue));           
        }catch(NumberFormatException e)
        {
            if (FXG_INHERIT_VALUE.equals(value))
            {
                return NumberInherit.newInstance(NumberInheritAsEnum.INHERIT);
            }
            else
            {
                //Exception: Unknown number inherit: {0}
                throw new FXGException(node.getStartLine(), node.getStartColumn(), "UnknownNumberInherit", value);            
            }
        }
    }
}

