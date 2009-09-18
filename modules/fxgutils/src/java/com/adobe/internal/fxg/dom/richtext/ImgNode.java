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
import com.adobe.internal.fxg.dom.types.NumberPercentAuto;
import com.adobe.internal.fxg.dom.types.NumberPercentAuto.NumberPercentAutoAsEnum;

/**
 * Represents a &lt;p /&gt; FXG image node.
 * 
 * @since 2.0
 * @author Min Plunkett
 */
public class ImgNode extends AbstractRichTextNode
{
    //--------------------------------------------------------------------------
    //
    // Attributes
    //
    //--------------------------------------------------------------------------

    // Image attributes
    public NumberPercentAuto width = NumberPercentAuto.newInstance(NumberPercentAutoAsEnum.AUTO);
    public NumberPercentAuto height = NumberPercentAuto.newInstance(NumberPercentAutoAsEnum.AUTO);
    public String source = "";
        
    //--------------------------------------------------------------------------
    //
    // FXGNode Implementation
    //
    //--------------------------------------------------------------------------

    public String getNodeName()
    {
        return FXG_IMG_ELEMENT;
    }

    /**
     * This implementation processes image attributes that are relevant to
     * the &lt;p&gt; tag, as well as delegates to the parent class to process
     * character attributes that are also relevant to the &lt;p&gt; tag.
     *  
     * @param name the attribute name
     * @param value the attribute value
     * @see AbstractRichTextNode#setAttribute(String, String)
     */
    @Override
    public void setAttribute(String name, String value)
    {
        if (FXG_WIDTH_ATTRIBUTE.equals(name))
        {
            width = getNumberPercentAuto(this, value);
        }
        else if(FXG_HEIGHT_ATTRIBUTE.equals(name))
        {
            height = getNumberPercentAuto(this, value);
        }
        else if(FXG_SOURCE_ATTRIBUTE.equals(name))
        {
            source = value;
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
     * 
     * @param node - the FXG node.
	 * @param value - the FXG String value.
	 * 
     */
    private NumberPercentAuto getNumberPercentAuto(FXGNode node, String value)
    {
    	try
    	{
    		double valueDbl = parsePercent(value);
    		return NumberPercentAuto.newInstance(valueDbl);
    	}
    	catch (NumberFormatException e)
    	{
    		if (FXG_NUMBERPERCENAUTO_AUTO_VALUE.equals(value))
    		{
    			return NumberPercentAuto.newInstance(NumberPercentAutoAsEnum.AUTO);
            }
            else
            {
                //Exception: Unknown number percent auto: {0}
                throw new FXGException(node.getStartLine(), node.getStartColumn(), "UnknownNumberPercentAuto", value);            
            }
    	}
    }
}
