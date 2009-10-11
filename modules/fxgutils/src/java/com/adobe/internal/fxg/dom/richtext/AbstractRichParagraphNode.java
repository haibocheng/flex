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

package com.adobe.internal.fxg.dom.richtext;

import static com.adobe.fxg.FXGConstants.*;

import com.adobe.internal.fxg.dom.types.Direction;
import com.adobe.internal.fxg.dom.types.JustificationRule;
import com.adobe.internal.fxg.dom.types.JustificationStyle;
import com.adobe.internal.fxg.dom.types.LeadingModel;
import com.adobe.internal.fxg.dom.types.TextAlign;
import com.adobe.internal.fxg.dom.types.TextJustify;


/**
 * An base class that represents a paragraph text.
 * 
 * @since 2.0
 * @author Min Plunkett
 */
public abstract class AbstractRichParagraphNode extends AbstractRichTextLeafNode
{
    protected static final double PARAGRAPH_INDENT_MIN_INCLUSIVE = 0.0;
    protected static final double PARAGRAPH_INDENT_MAX_INCLUSIVE = 1000.00;    
    protected static final double PARAGRAPH_SPACE_MIN_INCLUSIVE = 0.0;
    protected static final double PARAGRAPH_SPACE_MAX_INCLUSIVE = 1000.00;    
    protected static final double TEXTINDENT_MIN_INCLUSIVE = -1000.0;
    protected static final double TEXTINDENT_MAX_INCLUSIVE = 1000.0; 

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
    
    /**
     * This implementation processes paragraph attributes that are relevant to
     * the &lt;p&gt; tag, as well as delegates to the parent class to process
     * text leaft attributes that are also relevant to the &lt;p&gt; tag.
     * 
     * <p>
     * Paragraph attributes include:
     * <ul>
     * <li><b>textAlign</b> (String): [left, center, right, justify]  The
     * alignment of the text relative to the text box edges. Default is left.</li>
     * <li><b>textAlignLast</b> (String): [left, center, right, justify]: The
     * alignment of the last line of the paragraph, applies if textAlign is
     * justify. Default is left.</li>
     * <li><b>textIndent</b> (Number): The indentation of the first line of
     * text in a paragraph. The indent is relative to the left margin.
     * Measured in pixels. Default is 0. Can be negative.</li>
     * <li><b>marginLeft</b> (Number): The indentation applied to the left edge.
     * Measured in pixels. Default is 0.</li>
     * <li><b>marginRight</b> (Number): The indentation applied to the right
     * edge. Measured in pixels. Default is 0.</li>
     * <li><b>marginTop</b> (Number): This is the "space before" the paragraph.
     * Default is 0. Minimum is 0.</li>
     * <li><b>marginBottom</b> (Number): This is the "spaceAfter" the paragraph.
     * Default is 0. Minimum is 0.</li>
     * <li><b>direction</b> (String): [ltr, rtl] Controls the dominant writing
     * direction for the paragraphs (left-to-right or right-to-left), Default
     * is ltr.</li>
     * <li><b>blockProgression</b> (String): [tb, rl] Controls the direction in which
     * lines are stacked.</li>
     * </ul>
     * </p>
     * 
     * @param name the attribute name
     * @param value the attribute value
     * @see AbstractTextNode#setAttribute(String, String)
     */
    @Override
    public void setAttribute(String name, String value)
    {
        if (FXG_TEXTALIGN_ATTRIBUTE.equals(name))
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
        else
        {
            super.setAttribute(name, value);
            return;
        }
        
        // Remember attribute was set on this node.
        rememberAttribute(name, value);        
    }    
}
