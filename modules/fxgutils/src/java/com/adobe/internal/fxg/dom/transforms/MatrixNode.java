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

package com.adobe.internal.fxg.dom.transforms;

import static com.adobe.fxg.FXGConstants.*;

/**
 * @author Peter Farland
 */
public class MatrixNode extends AbstractTransformNode
{
    //--------------------------------------------------------------------------
    //
    // Attributes
    //
    //--------------------------------------------------------------------------

    public double a = 1.0;
    public double b = 0.0;
    public double c = 0.0;
    public double d = 1.0;
    public double tx = 0.0;
    public double ty = 0.0;

    //--------------------------------------------------------------------------
    //
    // FXGNode Implementation
    //
    //--------------------------------------------------------------------------

    /**
     * @return The unqualified name of a Matrix node, without tag markup.
     */
    public String getNodeName()
    {
        return FXG_MATRIX_ELEMENT;
    }

    @Override
    public void setAttribute(String name, String value)
    {
        if (FXG_A_ATTRIBUTE.equals(name))
            a = parseDouble(value);
        else if (FXG_B_ATTRIBUTE.equals(name))
            b = parseDouble(value);
        else if (FXG_C_ATTRIBUTE.equals(name))
            c = parseDouble(value);
        else if (FXG_D_ATTRIBUTE.equals(name))
            d = parseDouble(value);
        else if (FXG_TX_ATTRIBUTE.equals(name))
            tx = parseDouble(value);
        else if (FXG_TY_ATTRIBUTE.equals(name))
            ty = parseDouble(value);
    }

}
