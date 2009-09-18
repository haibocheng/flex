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
import com.adobe.internal.fxg.dom.types.BaselineOffset;
import com.adobe.internal.fxg.dom.types.BlockProgression;
import com.adobe.internal.fxg.dom.types.LineBreak;
import com.adobe.internal.fxg.dom.types.NumberAuto;
import com.adobe.internal.fxg.dom.types.NumberInherit;
import com.adobe.internal.fxg.dom.types.VerticalAlign;
import com.adobe.internal.fxg.dom.types.BaselineOffset.BaselineOffsetAsEnum;
import com.adobe.internal.fxg.dom.types.NumberAuto.NumberAutoAsEnum;
import com.adobe.internal.fxg.dom.types.NumberInherit.NumberInheritAsEnum;

/**
 * An base class that represents a block text.
 * 
 * @since 2.0
 * @author Min Plunkett
 */
public abstract class AbstractRichBlockTextNode extends AbstractRichParagraphNode
{
    //--------------------------------------------------------------------------
    //
    // Attributes
    //
    //--------------------------------------------------------------------------

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
    
    /**
     * This implementation processes text flow extra attributes that are 
     * relevant to the &lt;p&gt; tag, as well as delegates to the parent class 
     * to process text leaf or paragraph attributes that are also relevant to 
     * the &lt;p&gt; tag.
     * 
     * @param name the attribute name
     * @param value the attribute value
     * @see AbstractRichParagraphNode#setAttribute(String, String)
     */
    @Override
    public void setAttribute(String name, String value)
    {
        if (FXG_BLOCKPROGRESSION_ATTRIBUTE.equals(name))
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
        else
        {
            super.setAttribute(name, value);
            return;
        }
        
        // Remember attribute was set on this node.
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
     * Convert an FXG String value to a NumberInherit object.
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
