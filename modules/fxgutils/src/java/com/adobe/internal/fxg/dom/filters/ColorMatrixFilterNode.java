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

package com.adobe.internal.fxg.dom.filters;

import java.util.StringTokenizer;

import com.adobe.fxg.FXGException;
import com.adobe.internal.fxg.dom.DOMParserHelper;

import static com.adobe.fxg.FXGConstants.*;

/**
 * @author Peter Farland
 */
public class ColorMatrixFilterNode extends AbstractFilterNode
{
    //--------------------------------------------------------------------------
    //
    // FXGNode Implementation
    //
    //--------------------------------------------------------------------------
    
    /**
     * A 4 x 5 matrix transformation for RGBA. Matrix is in row major order
     * with each row comprising of srcR, srcG, srcB, srcA, 1. The first five
     * values apply to Red, the next five to Green, and so forth.
     */
    public float[] matrix = new float[] {1,0,0,0,0,
                                         0,1,0,0,0,
                                         0,0,1,0,0,
                                         0,0,0,1,0};

    //--------------------------------------------------------------------------
    //
    // FXGNode Implementation
    //
    //--------------------------------------------------------------------------

    /**
     * @return The unqualified name of a ColorMatrixFilter node, without tag
     * markup.
     */
    public String getNodeName()
    {
        return FXG_COLORMATRIXFILTER_ELEMENT;
    }

    @Override
    public void setAttribute(String name, String value)
    {
        if (FXG_MATRIX_ATTRIBUTE.equals(name))
            matrix = get4x5FloatMatrix(value);
        else
            super.setAttribute(name, value);
    }

    //--------------------------------------------------------------------------
    //
    // Helper Methods
    //
    //--------------------------------------------------------------------------

    /**
     * Convert a comma delimited String of 20 numbers to an array of 20 float
     * values representing a 4 x 5 color transform matrix.
     */
    protected float[] get4x5FloatMatrix(String value)
    {
        byte index = 0;
        float[] result = new float[20];
        StringTokenizer tokenizer = new StringTokenizer(value, ",", false);
        try{
            while (tokenizer.hasMoreTokens() && index < 20)
            {
                String token = tokenizer.nextToken();
                float f = DOMParserHelper.parseFloat(this, token);
                result[index++] = f;
            }
        }
        catch(FXGException e)
        {
            throw new FXGException(getStartLine(), getStartColumn(), "InvalidColorMatrix", value);
        }                
        return result;
    }
}
