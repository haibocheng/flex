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
import com.adobe.internal.fxg.dom.richtext.AbstractRichTextNode;
import com.adobe.internal.fxg.dom.richtext.BRNode;
import com.adobe.internal.fxg.dom.richtext.DivNode;
import com.adobe.internal.fxg.dom.richtext.ImgNode;
import com.adobe.internal.fxg.dom.richtext.LinkNode;
import com.adobe.internal.fxg.dom.richtext.ParagraphNode;
import com.adobe.internal.fxg.dom.richtext.SpanNode;
import com.adobe.internal.fxg.dom.richtext.TCYNode;
import com.adobe.internal.fxg.dom.richtext.TabNode;
import com.adobe.internal.fxg.dom.richtext.TextHelper;
import com.adobe.internal.fxg.dom.richtext.TextLayoutFormatNode;
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
    protected static final double PADDING_MIN_INCLUSIVE = 0.0;
    protected static final double PADDING_MAX_INCLUSIVE = 1000.0;    
    protected static final double BASELINEOFFSET_MIN_INCLUSIVE = 0.0;
    protected static final double BASELINEOFFSET_MAX_INCLUSIVE = 1000.0; 
    protected static final double BASELINESHIFT_MIN_INCLUSIVE = -1000.0;
    protected static final double BASELINESHIFT_MAX_INCLUSIVE = 1000.0; 
    protected static final int COLUMNCOUNT_MIN_INCLUSIVE = 0;
    protected static final int COLUMNCOUNT_MAX_INCLUSIVE = 50; 
    protected static final double COLUMNGAP_MIN_INCLUSIVE = 0.0;
    protected static final double COLUMNGAP_MAX_INCLUSIVE = 1000.0; 
    protected static final double COLUMNWIDTH_MIN_INCLUSIVE = 0.0;
    protected static final double COLUMNWIDTH_MAX_INCLUSIVE = 8000.0; 
    protected static final double LINEHEIGHT_PERCENT_MIN_INCLUSIVE = -1000.0;
    protected static final double LINEHEIGHT_PERCENT_MAX_INCLUSIVE = 1000.0; 
    protected static final double LINEHEIGHT_PIXEL_MIN_INCLUSIVE = -720.0;
    protected static final double LINEHEIGHT_PIXEL_MAX_INCLUSIVE = 720.0; 
    protected static final double PARAGRAPH_INDENT_MIN_INCLUSIVE = 0.0;
    protected static final double PARAGRAPH_INDENT_MAX_INCLUSIVE = 1000.00;    
    protected static final double PARAGRAPH_SPACE_MIN_INCLUSIVE = 0.0;
    protected static final double PARAGRAPH_SPACE_MAX_INCLUSIVE = 1000.00;    
    protected static final double TEXTINDENT_MIN_INCLUSIVE = -1000.0;
    protected static final double TEXTINDENT_MAX_INCLUSIVE = 1000.0; 
    protected static final double TRACKING_MIN_INCLUSIVE = -100.0;
    protected static final double TRACKING_MAX_INCLUSIVE = 100.0;     
    
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
    public double paragraphStartIndent = 0.0;
    public double paragraphEndIndent = 0.0;
    public double paragraphSpaceBefore = 0.0;
    public double paragraphSpaceAfter = 0.0;
    public Direction direction = Direction.LTR;
    public JustificationRule justificationRule = JustificationRule.AUTO;
    public JustificationStyle justificationStyle = JustificationStyle.PRIORITIZELEASTADJUSTMENT;
    public TextJustify textJustify = TextJustify.INTERWORD;
    public LeadingModel leadingModel = LeadingModel.AUTO;
    public String tabStops = "";
    
    // Text Leaf Attributes
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

    // Link format properties
    public TextLayoutFormatNode linkNormalFormat = null;
    public TextLayoutFormatNode linkHoverFormat = null;
    public TextLayoutFormatNode linkActiveFormat = null;    
    
    private boolean contiguous = false;
    
    //--------------------------------------------------------------------------
    //
    // TextNode Helpers
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
    protected Map<String, TextNode> properties;

    /**
     * @return The List of child property nodes of this text node.
     */
    public Map<String, TextNode> getTextProperties()
    {
        return properties;
    }

    /**
     * A RichText node can also have special child property nodes that represent
     * complex property values that cannot be set via a simple attribute.
     */
    public void addTextProperty(String propertyName, TextNode node)
    {
        if (node instanceof TextLayoutFormatNode)
        {
            if (FXG_LINKACTIVEFORMAT_PROPERTY_ELEMENT.equals(propertyName))
            {
                if (linkActiveFormat == null)
                {
                    linkActiveFormat = (TextLayoutFormatNode)node;
                    linkActiveFormat.setParent(this);

                    if (properties == null)
                        properties = new HashMap<String, TextNode>(3);
                    properties.put(propertyName, linkActiveFormat);
                }
                else
                {
                    // Exception: Multiple LinkFormat elements are not allowed.
                    throw new FXGException(getStartLine(), getStartColumn(), "MultipleLinkFormatElements");
                }
            }
            else if (FXG_LINKHOVERFORMAT_PROPERTY_ELEMENT.equals(propertyName))
            {
                if (linkHoverFormat == null)
                {
                    linkHoverFormat = (TextLayoutFormatNode)node;
                    linkHoverFormat.setParent(this);

                    if (properties == null)
                        properties = new HashMap<String, TextNode>(3);
                    properties.put(propertyName, linkHoverFormat);
                }
                else
                {
                    // Exception: Multiple LinkFormat elements are not allowed.
                    throw new FXGException(getStartLine(), getStartColumn(), "MultipleLinkFormatElements");
                }
            }
            else if (FXG_LINKNORMALFORMAT_PROPERTY_ELEMENT.equals(propertyName))
            {
                if (linkNormalFormat == null)
                {
                    linkNormalFormat = (TextLayoutFormatNode)node;
                    linkNormalFormat.setParent(this);

                    if (properties == null)
                        properties = new HashMap<String, TextNode>(3);
                    properties.put(propertyName, linkNormalFormat);
                }
                else
                {
                    // Exception: Multiple LinkFormat elements are not allowed. 
                    throw new FXGException(getStartLine(), getStartColumn(), "MultipleLinkFormatElements");
                }
            }
            else
            {
                // Exception: Unknown LinkFormat element. 
                throw new FXGException(node.getStartLine(), node.getStartColumn(), "UnknownLinkFormat", propertyName);
            }
        }
        else
        {
            addChild(node);
        }
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
        if (child instanceof CDATANode)
        {
            if (TextHelper.ignorableWhitespace(((CDATANode)child).content))
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
            blockProgression = TextHelper.getBlockProgression(this, value);
        }
        else if (FXG_PADDINGLEFT_ATTRIBUTE.equals(name))
        {
            paddingLeft = getNumberInherit(this, value, PADDING_MIN_INCLUSIVE, PADDING_MAX_INCLUSIVE, paddingLeft.getNumberInheritAsDbl());
        }
        else if (FXG_PADDINGRIGHT_ATTRIBUTE.equals(name))
        {
            paddingRight = getNumberInherit(this, value, PADDING_MIN_INCLUSIVE, PADDING_MAX_INCLUSIVE, paddingRight.getNumberInheritAsDbl());
        }
        else if (FXG_PADDINGTOP_ATTRIBUTE.equals(name))
        {
            paddingTop = getNumberInherit(this, value, PADDING_MIN_INCLUSIVE, PADDING_MAX_INCLUSIVE, paddingTop.getNumberInheritAsDbl());
        }
        else if (FXG_PADDINGBOTTOM_ATTRIBUTE.equals(name))
        {
            paddingBottom = getNumberInherit(this, value, PADDING_MIN_INCLUSIVE, PADDING_MAX_INCLUSIVE, paddingBottom.getNumberInheritAsDbl());
        }
        else if (FXG_LINEBREAK_ATTRIBUTE.equals(name))
        {
            lineBreak = TextHelper.getLineBreak(this, value);
        }        
        else if (FXG_COLUMNGAP_ATTRIBUTE.equals(name))
        {
            columnGap = getNumberInherit(this, value, COLUMNGAP_MIN_INCLUSIVE, COLUMNGAP_MAX_INCLUSIVE, columnGap.getNumberInheritAsDbl());
        }
        else if (FXG_COLUMNCOUNT_ATTRIBUTE.equals(name))
        {
            columnCount = getNumberAutoInt(this, value, COLUMNCOUNT_MIN_INCLUSIVE, COLUMNCOUNT_MAX_INCLUSIVE, columnCount.getNumberAutoAsInt());
        }
        else if (FXG_COLUMNWIDTH_ATTRIBUTE.equals(name))
        {
            columnWidth = getNumberAutoDbl(this, value, COLUMNWIDTH_MIN_INCLUSIVE, COLUMNWIDTH_MAX_INCLUSIVE, columnWidth.getNumberAutoAsDbl());
        }
        else if (FXG_FIRSTBASELINEOFFSET_ATTRIBUTE.equals(name))
        {
            firstBaselineOffset = getFirstBaselineOffset(this, value, BASELINEOFFSET_MIN_INCLUSIVE, BASELINEOFFSET_MAX_INCLUSIVE, firstBaselineOffset.getBaselineOffsetAsDbl());
        }
        else if (FXG_VERTICALALIGN_ATTRIBUTE.equals(name))
        {
            verticalAlign = TextHelper.getVerticalAlign(this, value);
        } 
        else if (FXG_TEXTALIGN_ATTRIBUTE.equals(name))
        {
            textAlign = TextHelper.getTextAlign(this, value);
        }
        else if (FXG_TEXTALIGNLAST_ATTRIBUTE.equals(name))
        {
            textAlignLast = TextHelper.getTextAlign(this, value);
        }
        else if (FXG_TEXTINDENT_ATTRIBUTE.equals(name))
        {
            textIndent = parseDouble(value, TEXTINDENT_MIN_INCLUSIVE, TEXTINDENT_MAX_INCLUSIVE, textIndent);
        }
        else if (FXG_PARAGRAPHSTARTINDENT_ATTRIBUTE.equals(name))
        {
            paragraphStartIndent = parseDouble(value, PARAGRAPH_INDENT_MIN_INCLUSIVE, PARAGRAPH_INDENT_MAX_INCLUSIVE, paragraphStartIndent);
        }
        else if (FXG_PARAGRAPHENDINDENT_ATTRIBUTE.equals(name))
        {
            paragraphEndIndent = parseDouble(value, PARAGRAPH_INDENT_MIN_INCLUSIVE, PARAGRAPH_INDENT_MAX_INCLUSIVE, paragraphEndIndent);
        }
        else if (FXG_PARAGRAPHSPACEBEFORE_ATTRIBUTE.equals(name))
        {
            paragraphSpaceBefore = parseDouble(value, PARAGRAPH_SPACE_MIN_INCLUSIVE, PARAGRAPH_SPACE_MAX_INCLUSIVE, paragraphSpaceBefore);
        }
        else if (FXG_PARAGRAPHSPACEAFTER_ATTRIBUTE.equals(name))
        {
            paragraphSpaceAfter = parseDouble(value, PARAGRAPH_SPACE_MIN_INCLUSIVE, PARAGRAPH_SPACE_MAX_INCLUSIVE, paragraphSpaceAfter);
        }
        else if (FXG_DIRECTION_ATTRIBUTE.equals(name))
        {
            direction = TextHelper.getDirection(this, value);
        }
        else if (FXG_JUSTIFICATIONRULE_ATTRIBUTE.equals(name))
        {
            justificationRule = TextHelper.getJustificationRule(this, value);
        }
        else if (FXG_JUSTIFICATIONSTYLE_ATTRIBUTE.equals(name))
        {
            justificationStyle = TextHelper.getJustificationStyle(this, value);
        }
        else if (FXG_TEXTJUSTIFY_ATTRIBUTE.equals(name))
        {
            textJustify = TextHelper.getTextJustify(this, value);
        }
        else if (FXG_LEADINGMODEL_ATTRIBUTE.equals(name))
        {
            leadingModel = TextHelper.getLeadingModel(this, value);
        }        
        else if (FXG_TABSTOPS_ATTRIBUTE.equals(name))
        {
            tabStops = value;
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
            fontStyle = TextHelper.getFontStyle(this, value);
        }
        else if (FXG_FONTWEIGHT_ATTRIBUTE.equals(name))
        {
            fontWeight = TextHelper.getFontWeight(this, value);
        }
        else if (FXG_KERNING_ATTRIBUTE.equals(name))
        {
            kerning = TextHelper.getKerning(this, value);
        }        
        else if (FXG_LINEHEIGHT_ATTRIBUTE.equals(name))
        {
            lineHeight = parseNumberPercentWithSeparateRange(value, 
            		LINEHEIGHT_PERCENT_MIN_INCLUSIVE, LINEHEIGHT_PERCENT_MAX_INCLUSIVE, 
            		LINEHEIGHT_PIXEL_MIN_INCLUSIVE, LINEHEIGHT_PIXEL_MAX_INCLUSIVE, lineHeight); 
        }
        else if (FXG_TEXTDECORATION_ATTRIBUTE.equals(name))
        {
            textDecoration = TextHelper.getTextDecoration(this, value);
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
            whiteSpaceCollapse = TextHelper.getWhiteSpaceCollapse(this, value);
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
            baselineShift = getBaselineShift(this, value, BASELINESHIFT_MIN_INCLUSIVE, BASELINESHIFT_MAX_INCLUSIVE, baselineShift.getBaselineShiftAsDbl());
        }
        else if (FXG_BREAKOPPORTUNITY_ATTRIBUTE.equals(name))
        {
            breakOpportunity = TextHelper.getBreakOpportunity(this, value);
        }
        else if (FXG_DIGITCASE_ATTRIBUTE.equals(name))
        {
            digitCase = TextHelper.getDigitCase(this, value);
        }
        else if (FXG_DIGITWIDTH_ATTRIBUTE.equals(name))
        {
            digitWidth = TextHelper.getDigitWidth(this, value);
        }
        else if (FXG_DOMINANTBASELINE_ATTRIBUTE.equals(name))
        {
            dominantBaseline = TextHelper.getDominantBaseline(this, value);
        }
        else if (FXG_ALIGNMENTBASELINE_ATTRIBUTE.equals(name))
        {
            alignmentBaseline = TextHelper.getAlignmentBaseline(this, value);
        }
        else if (FXG_LIGATURELEVEL_ATTRIBUTE.equals(name))
        {
            ligatureLevel = TextHelper.getLigatureLevel(this, value);
        }
        else if (FXG_LOCALE_ATTRIBUTE.equals(name))
        {
            locale = value;
        }
        else if (FXG_TYPOGRAPHICCASE_ATTRIBUTE.equals(name))
        {
            typographicCase = TextHelper.getTypographicCase(this, value);
        }        
        else if (FXG_TRACKINGLEFT_ATTRIBUTE.equals(name))
        {
            trackingLeft = parseNumberPercent(value, TRACKING_MIN_INCLUSIVE, TRACKING_MAX_INCLUSIVE, trackingLeft);
        }
        else if (FXG_TRACKINGRIGHT_ATTRIBUTE.equals(name))
        {
            trackingRight = parseNumberPercent(value, TRACKING_MIN_INCLUSIVE, TRACKING_MAX_INCLUSIVE, trackingRight);
        } 
        else if (FXG_TEXTROTATION_ATTRIBUTE.equals(name))
        {
        	textRotation = TextHelper.getTextRotation(this, value);
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

    //--------------------------------------------------------------------------
    //
    // Helper Methods
    //
    //--------------------------------------------------------------------------
    
    /**
     * Convert an FXG String value to a BaselineOffset object.
     * 
     * @param value - the FXG String value.
     * @param min - the smallest double value that the result must be greater
     * or equal to.
     * @param max - the largest double value that the result must be smaller
     * than or equal to.
     * @param defaultValue - the default double value; if the encountered minor 
     * version is later than the supported minor version and the attribute value
     *  is out-of-range, the default value is returned.
     * @return the matching BaselineOffset rule.
     * @throws FXGException if the String did not match a known
     * BaselineOffset rule or the value falls out of the specified range 
     * (inclusive).
     */
    private BaselineOffset getFirstBaselineOffset(FXGNode node, String value, double min, double max, double defaultValue)
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
        		return BaselineOffset.newInstance(parseDouble(value, min, max, defaultValue));
        	}
        	catch(NumberFormatException e)
        	{
	            //Exception: Unknown baseline offset: {0}
	            throw new FXGException(node.getStartLine(), node.getStartColumn(), "UnknownBaselineOffset", value);
        	}
        }
    }
    
    /**
     * Convert an FXG String value to a NumberAuto object.
     * 
     * @param value - the FXG String value.
     * @param min - the smallest double value that the result must be greater
     * or equal to.
     * @param max - the largest double value that the result must be smaller
     * than or equal to.
     * @param defaultValue - the default double value; if the encountered minor 
     * version is later than the supported minor version and the attribute value
     *  is out-of-range, the default value is returned.
     * @return the matching NumberAuto rule.
     * @throws FXGException if the String did not match a known
     * NumberAuto rule.
     */
    private NumberAuto getNumberAutoDbl(FXGNode node, String value, double min, double max, double defaultValue)
    {
        try
        {
            return NumberAuto.newInstance(parseDouble(value, min, max, defaultValue));            
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
     * @param min - the smallest int value that the result must be greater
     * or equal to.
     * @param max - the largest int value that the result must be smaller
     * than or equal to.
     * @param defaultValue - the default int value; if the encountered minor 
     * version is later than the supported minor version and the attribute value
     *  is out-of-range, the default value is returned.
     * @return the matching NumberAuto rule.
     * @throws FXGException if the String did not match a known
     * NumberAuto rule.
     */
    private NumberAuto getNumberAutoInt(FXGNode node, String value, int min, int max, int defaultValue)
    {
        try
        {
            return NumberAuto.newInstance(parseInt(value, min, max, defaultValue));            
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
     * Convert an FXG String value to a NumberInherit enumeration.
     * 
     * @param value - the FXG String value.
     * @param min - the smallest double value that the result must be greater
     * or equal to.
     * @param max - the largest double value that the result must be smaller
     * than or equal to.
     * @param defaultValue - the default double value; if the encountered minor 
     * version is later than the supported minor version and the attribute value
     *  is out-of-range, the default value is returned.
     * @return the matching NumberInherit rule.
     * @throws FXGException if the String did not match a known
     * NumberInherit rule or the value falls out of the specified range 
     * (inclusive).
     */
    private NumberInherit getNumberInherit(FXGNode node, String value, double min, double max, double defaultValue)
    {
        try
        {
            return NumberInherit.newInstance(parseDouble(value, min, max, defaultValue));            
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
     * Convert an FXG String value to a BaselineShift enumeration.
     * 
     * @param value - the FXG String value.
     * @param min - the smallest double value that the result must be greater
     * or equal to.
     * @param max - the largest double value that the result must be smaller
     * than or equal to.
     * @param defaultValue - the default double value; if the encountered minor 
     * version is later than the supported minor version and the attribute value
     *  is out-of-range, the default value is returned.
     * @return the matching BaselineShift rule.
     * @throws FXGException if the String did not match a known
     * BaselineShift rule or the value falls out of the specified range 
     * (inclusive).
     */
    private BaselineShift getBaselineShift(FXGNode node, String value, double min, double max, double defaultValue)
    {
        try
        {
        	
            return BaselineShift.newInstance(parseNumberPercent(value, min, max, defaultValue));            
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
}

