package com.adobe.internal.fxg.dom.filters;

import static com.adobe.fxg.FXGConstants.*;

/**
 * @author Peter Farland
 */
public class DropShadowFilterNode extends AbstractFilterNode
{
    //--------------------------------------------------------------------------
    //
    // Attributes
    //
    //--------------------------------------------------------------------------

    public double alpha = 1.0;
    public double angle = 45.0;
    public double blurX = 4.0;
    public double blurY = 4.0;
    public int color = COLOR_BLACK;
    public double distance = 4.0;
    public boolean hideObject = false;
    public boolean inner = false;
    public boolean knockout = false;
    public int quality = 1;
    public double strength = 1.0;

    //--------------------------------------------------------------------------
    //
    // FXGNode Implementation
    //
    //--------------------------------------------------------------------------

    /**
     * @return The unqualified name of a DropShadowFilter node, without tag
     * markup.
     */
    public String getNodeName()
    {
        return FXG_DROPSHADOWFILTER_ELEMENT;
    }

    @Override
    public void setAttribute(String name, String value)
    {
        if (FXG_ALPHA_ATTRIBUTE.equals(name))
            alpha = parseDouble(value, ALPHA_MIN_INCLUSIVE, ALPHA_MAX_INCLUSIVE, alpha);
        else if (FXG_ANGLE_ATTRIBUTE.equals(name))
            angle = parseDouble(value);
        else if (FXG_BLURX_ATTRIBUTE.equals(name))
            blurX = parseDouble(value);
        else if (FXG_BLURY_ATTRIBUTE.equals(name))
            blurY = parseDouble(value);
        else if (FXG_COLOR_ATTRIBUTE.equals(name))
            color = parseRGB(value, color);
        else if (FXG_DISTANCE_ATTRIBUTE.equals(name))
            distance = parseDouble(value);
        else if (FXG_HIDEOBJECT_ATTRIBUTE.equals(name))
            hideObject = parseBoolean(value);
        else if (FXG_INNER_ATTRIBUTE.equals(name))
            inner = parseBoolean(value);
        else if (FXG_KNOCKOUT_ATTRIBUTE.equals(name))
            knockout = parseBoolean(value);
        else if (FXG_QUALITY_ATTRIBUTE.equals(name))
            quality = parseInt(value, QUALITY_MIN_INCLUSIVE, QUALITY_MAX_INCLUSIVE, quality);
        else if (FXG_STRENGTH_ATTRIBUTE.equals(name))
            strength = parseDouble(value);
        else
            super.setAttribute(name, value);
    }
}