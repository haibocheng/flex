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

import com.adobe.fxg.FXGException;
import com.adobe.fxg.dom.FXGNode;
import com.adobe.internal.fxg.dom.types.Direction;
import com.adobe.internal.fxg.dom.types.JustificationRule;
import com.adobe.internal.fxg.dom.types.JustificationStyle;
import com.adobe.internal.fxg.dom.types.LeadingModel;
import com.adobe.internal.fxg.dom.types.NumberInherit;
import com.adobe.internal.fxg.dom.types.TextAlign;
import com.adobe.internal.fxg.dom.types.TextJustify;
import com.adobe.internal.fxg.dom.types.NumberInherit.NumberInheritAsEnum;

/**
 * An base class that represents a paragraph text.
 * 
 * @since 2.0
 * @author Min Plunkett
 */
public abstract class AbstractRichParagraphNode extends AbstractRichTextLeafNode
{
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
        else
        {
            super.setAttribute(name, value);
            return;
        }
        
        // Remember attribute was set on this node.
        rememberAttribute(name, value);        
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
}
