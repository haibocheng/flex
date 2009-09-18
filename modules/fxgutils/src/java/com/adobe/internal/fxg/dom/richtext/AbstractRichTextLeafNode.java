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
import com.adobe.internal.fxg.dom.AbstractFXGNode;
import com.adobe.internal.fxg.dom.types.AlignmentBaseline;
import com.adobe.internal.fxg.dom.types.BaselineShift;
import com.adobe.internal.fxg.dom.types.BreakOpportunity;
import com.adobe.internal.fxg.dom.types.ColorWithEnum;
import com.adobe.internal.fxg.dom.types.DigitCase;
import com.adobe.internal.fxg.dom.types.DigitWidth;
import com.adobe.internal.fxg.dom.types.DominantBaseline;
import com.adobe.internal.fxg.dom.types.FontStyle;
import com.adobe.internal.fxg.dom.types.FontWeight;
import com.adobe.internal.fxg.dom.types.Kerning;
import com.adobe.internal.fxg.dom.types.LigatureLevel;
import com.adobe.internal.fxg.dom.types.NumberInherit;
import com.adobe.internal.fxg.dom.types.TextDecoration;
import com.adobe.internal.fxg.dom.types.TextRotation;
import com.adobe.internal.fxg.dom.types.TypographicCase;
import com.adobe.internal.fxg.dom.types.WhiteSpaceCollapse;
import com.adobe.internal.fxg.dom.types.BaselineShift.BaselineShiftAsEnum;
import com.adobe.internal.fxg.dom.types.ColorWithEnum.ColorEnum;
import com.adobe.internal.fxg.dom.types.NumberInherit.NumberInheritAsEnum;

/**
 * A base text left node class that have character formatting.
 * 
 * @since 2.0
 * @author Min Plunkett
 */
public abstract class AbstractRichTextLeafNode extends AbstractRichTextNode
{
    protected static final double FONTSIZE_MIN_INCLUSIVE = 1.0;
    protected static final double FONTSIZE_MAX_INCLUSIVE = 720.0;

    //--------------------------------------------------------------------------
    //
    // Attributes
    //
    //--------------------------------------------------------------------------

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
    
    /**
     * This implementation processes text leaf attributes that are common to
     * &lt;RichText&gt;, &lt;p&gt;, and &lt;span&gt;.
     * <p>
     * The right hand side of an ActionScript assignment is generated for
     * each property based on the expected type of the attribute.
     * </p>
     * <p>
     * Text leaf attributes include:
     * <ul>
     * <li><b>fontFamily</b> (String): The font family name used to render the
     * text. Default value is Times New Roman (Times on Mac OS X).</li>
     * <li><b>fontSize</b> (Number): The size of the glyphs that is used to
     * render the text, specified in point sizes. Default is 12. Minimum 1
     * point. Maximum 500 points.</li>
     * <li><b>fontStyle</b> (String): [normal, italic] The style of the glyphs
     * that is used to render the text. Legal values are 'normal' and 'italic'.
     * Default is normal.</li>
     * <li><b>fontWeight</b> (String): [normal, bold] The boldness or lightness
     * of the glyphs that is used to render the text. Default is normal.</li>
     * <li><b>lineHeight</b> (Percent | Number): The leading, or the distance
     * from the previous line's baseline to this one, in points. Default is
     * 120%. Minimum value for percent or number is 0.</li>
     * <li><b>tracking</b> (Percent): Space added to the advance after each
     * character, as a percentage of the current point size. Percentages can be
     * negative, to bring characters closer together. Default is 0.</li>
     * <li><b>textDecoration</b> (String): [none, underline]: The decoration to
     * apply to the text. Default is none.</li>
     * <li><b>lineThrough</b> (Boolean): true if text has strikethrough applied,
     * false otherwise. Default is false.</li>
     * <li><b>color</b> (Color): The color of the text. Default is 0x000000.</li>
     * <li><b>textAlpha</b> (alpha): The alpha value applied to the text.
     * Default is 1.0.</li>
     * <li><b>whiteSpaceCollapse</b> (String): [preserve, collapse] This is an
     * enumerated value. A value of "collapse" converts line feeds, newlines,
     * and tabs to spaces and collapses adjacent spaces to one. Leading and
     * trailing whitespace is trimmed. A value of "preserve" passes whitespace
     * through unchanged.</li>
     * <li><b>kerning</b> (String): [on, off, auto] If on, pair kerns are
     * honored. If off, there is no font-based kerning applied. If auto,
     * kerning is applied to all characters except Kanji, Hiragana or Katakana.
     * The default is auto.</li>
     * </ul>
     * </p>
     * @param name - the unqualified attribute name
     * @param value - the attribute value
     */
    @Override
    public void setAttribute(String name, String value)
    {
        if (FXG_FORMAT_ATTRIBUTE.equals(name))
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
        else
        {
            super.setAttribute(name, value);
            return;
        }
        
        // Remember attribute was set on this node.
        rememberAttribute(name, value);
    }

    //--------------------------------------------------------------------------
    //
    // Helper Methods
    //
    //--------------------------------------------------------------------------

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
