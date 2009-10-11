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

package com.adobe.internal.fxg.dom;

import java.util.ArrayList;
import java.util.List;

import static com.adobe.fxg.FXGConstants.*;

import com.adobe.fxg.FXGException;
import com.adobe.fxg.FXGVersion;
import com.adobe.fxg.dom.FXGNode;
import com.adobe.fxg.util.FXGLog;
import com.adobe.fxg.util.FXGLogger;
import com.adobe.internal.fxg.dom.transforms.ColorTransformNode;
import com.adobe.internal.fxg.dom.transforms.MatrixNode;
import com.adobe.internal.fxg.dom.types.BlendMode;
import com.adobe.internal.fxg.dom.types.MaskType;
import com.adobe.internal.fxg.types.FXGMatrix;

/**
 * Base class for all nodes that present graphic content or represent groups
 * of graphic content. Children inherit parent context information for
 * transforms, blend modes and masks.
 * 
 * @author Peter Farland
 */
public abstract class GraphicContentNode extends AbstractFXGNode
        implements MaskableNode
{
    //--------------------------------------------------------------------------
    //
    // Attributes
    //
    //--------------------------------------------------------------------------

    //------------
    // id
    //------------

    protected String id = "undefined";

    /**
     * An id attribute provides a well defined name to a text node.
     */
    public String getId()
    {
        return id;
    }

    /**
     * Sets the node id.
     * @param value - the node id as a String.
     */
    public void setId(String value)
    {
        id = value;
    }

    public boolean visible = true;

    public double x = 0.0;
    public double y = 0.0;
    public double scaleX = 1.0;
    public double scaleY = 1.0;
    public double rotation = 0.0;
    public double alpha = 1.0;
    public BlendMode blendMode = BlendMode.AUTO;
    public MaskType maskType = MaskType.CLIP;
    public boolean luminosityClip = false;
    public boolean luminosityInvert = false;

    protected boolean translateSet;
    protected boolean scaleSet;
    protected boolean rotationSet;
    protected boolean alphaSet;
    protected boolean maskTypeSet;
    
    //is part of mask
    public boolean isPartofMask = false;

    //--------------------------------------------------------------------------
    //
    // Children
    //
    //--------------------------------------------------------------------------

    public List<FilterNode> filters;
    public MaskingNode mask;
    public MatrixNode matrix;
    public ColorTransformNode colorTransform;

    //--------------------------------------------------------------------------
    //
    // FXGNode Implementation
    //
    //--------------------------------------------------------------------------

    /**
     * Adds an FXG child node to this node.
     * <p>
     * Graphic content nodes support child property nodes &lt;filter&gt;,
     * &lt;mask&gt;, &lt;matrix&gt;, or &lt;colorTransform&gt;.
     * </p>
     * 
     * @param child - a child FXG node to be added to this node.
     * @throws FXGException if the child is not supported by this node.
     */
    @Override
    public void addChild(FXGNode child)
    {
        if (child instanceof FilterNode)
        {
            if (filters == null)
                filters = new ArrayList<FilterNode>();

            filters.add((FilterNode)child);
        }
        else if (child instanceof MaskPropertyNode)
        {
            mask = ((MaskPropertyNode)child).mask;
            if (mask instanceof GraphicContentNode)
            {
                ((GraphicContentNode)mask).setParentGraphicContext(createGraphicContext());
            }
        }
        else if (child instanceof MatrixNode)
        {
            if (translateSet || scaleSet || rotationSet)
            	//Exception:Cannot supply a matrix child if transformation attributes were provided
                throw new FXGException(child.getStartLine(), child.getStartColumn(), "InvalidChildMatrixNode");

            matrix = (MatrixNode)child;
        }
        else if (child instanceof ColorTransformNode)
        {
            if (alphaSet)
            	//Exception:Cannot supply a colorTransform child if alpha attribute was provided.
                throw new FXGException(child.getStartLine(), child.getStartColumn(), "InvalidChildColorTransformNode");

            colorTransform = (ColorTransformNode)child;
        }
        else
        {
            super.addChild(child);
        }
    }

    /**
     * Sets an FXG attribute on this FXG node.
     * <p>
     * Graphic content nodes support the following attributes:
     * <ul>
     * <li><b>rotation</b> (ASDegrees): Defaults to 0.</li>
     * <li><b>scaleX</b> (Number): Defaults to 1.</li>
     * <li><b>scaleY</b> (Number): Defaults to 1.</li>
     * <li><b>x</b> (Number): The horizontal placement of the left edge of the
     * text box, relative to the parent grouping element. Defaults to 0.</li>
     * <li><b>y</b> (Number): The vertical placement of the top edge of the
     * text box, relative to the parent grouping element. Defaults to 0.</li>
     * <li><b>blendMode</b> (String): [normal, add, alpha, darken, difference,
     * erase, hardlight, invert, layer, lighten, multiply, normal, subtract,
     * screen, overlay, auto, colordodge, colorburn, exclusion, softlight, 
     * hue, saturation, color, luminosity] Defaults to auto.</li>
     * <li><b>alpha</b> (ASAlpha): Defaults to 1.</li>
     * <li><b>maskType</b> (String):[clip, alpha]: Defaults to clip.</li>
     * <li><b>visible</b> (Boolean): Whether or not the text box is visible.
     * Defaults to true.</li>
     * </ul>
     * </p>
     * <p>
     * Graphic content nodes also support an id attribute.
     * </p> 
     * 
     * @param name - the unqualified attribute name
     * @param value - the attribute value
     * @throws FXGException if the attribute name is not supported by this node.
     */
    @Override
    public void setAttribute(String name, String value)
    {
        if (FXG_X_ATTRIBUTE.equals(name))
        {
            x = parseDouble(value);
            translateSet = true;
        }
        else if (FXG_Y_ATTRIBUTE.equals(name))
        {
            y = parseDouble(value);
            translateSet = true;
        }
        else if (FXG_ROTATION_ATTRIBUTE.equals(name))
        {
            rotation = parseDouble(value);
            rotationSet = true;
        }
        else if (FXG_SCALEX_ATTRIBUTE.equals(name))
        {
            scaleX = parseDouble(value);
            scaleSet = true;
        }
        else if (FXG_SCALEY_ATTRIBUTE.equals(name))
        {
            scaleY = parseDouble(value);
            scaleSet = true;
        }
        else if (FXG_ALPHA_ATTRIBUTE.equals(name))
        {
            alpha = parseDouble(value, ALPHA_MIN_INCLUSIVE, ALPHA_MAX_INCLUSIVE, alpha);
            alphaSet = true;
        }
        else if (FXG_BLENDMODE_ATTRIBUTE.equals(name))
        {
            blendMode = parseBlendMode(value, blendMode);
        }
        else if (FXG_VISIBLE_ATTRIBUTE.equals(name))
        {
            visible = parseBoolean(value);
        }
        else if (FXG_ID_ATTRIBUTE.equals(name))
        {
            id = value;
        }
        else if (FXG_MASKTYPE_ATTRIBUTE.equals(name))
        {
            maskType = parseMaskType(value, maskType);
            maskTypeSet = true;
        }
        else if (getFileVersion().equalTo(FXGVersion.v1_0))
        {
            // Rest of the attributes are not supported by FXG 1.0
            // Exception:Attribute {0} not supported by node {1}.
            throw new FXGException(getStartLine(), getStartColumn(), "InvalidNodeAttribute", name, getNodeName());
        }
        else if (FXG_LUMINOSITYCLIP_ATTRIBUTE.equals(name))
        {
            luminosityClip = parseBoolean(value);
        }
        else if (FXG_LUMINOSITYINVERT_ATTRIBUTE.equals(name))
        {
            luminosityInvert =  parseBoolean(value);            
        }
        else
        {
            super.setAttribute(name, value);
        }
    }

    
    //--------------------------------------------------------------------------
    //
    // MaskableNode Implementation
    //
    //--------------------------------------------------------------------------

    public MaskingNode getMask()
    {
        return mask;
    }

    public MaskType getMaskType()
    {
        return maskType;
    }
    
    public boolean getLuminosityClip()
    {
        return luminosityClip;
    }
    
    public boolean getLuminosityInvert()
    {
        return luminosityInvert;
    }
    
    //--------------------------------------------------------------------------
    //
    // Helper Methods
    //
    //--------------------------------------------------------------------------

    private GraphicContext parentGraphicContext;

    public GraphicContext createGraphicContext()
    {
        GraphicContext graphicContext = new GraphicContext();

        if (parentGraphicContext != null)
            graphicContext.scalingGrid = parentGraphicContext.scalingGrid;

        FXGMatrix transform = graphicContext.getTransform(); 
        if (matrix != null)
        {
            FXGMatrix t = new FXGMatrix(matrix);
            transform.concat(t);
        }
        else
        { 

            if (scaleSet)
                transform.scale(scaleX, scaleY);

            if (rotationSet)
                transform.rotate(rotation);

            if (translateSet)
                transform.translate(x, y);

        }

        if (colorTransform != null)
        {
            graphicContext.colorTransform = colorTransform;
        }
        else if (alphaSet)
        {
            if (graphicContext.colorTransform == null)
                graphicContext.colorTransform = new ColorTransformNode();

            graphicContext.colorTransform.alphaMultiplier = alpha;
        }

        graphicContext.blendMode = blendMode;

        if (filters != null)
            graphicContext.addFilters(filters);

        if (maskTypeSet)
            graphicContext.maskType = maskType;
        else if (parentGraphicContext != null)
            graphicContext.maskType = parentGraphicContext.maskType;

        return graphicContext;
    }

    public void setParentGraphicContext(GraphicContext context)
    {
        parentGraphicContext = context;
    }

    /**
     * Convert an FXG String value to a BlendMode enumeration.
     * 
     * @param value - the FXG String value
     * @return the matching BlendMode
     * @throws FXGException if the String did not match a known
     * BlendMode.
     */
    protected BlendMode parseBlendMode(String value, BlendMode defMode)
    {
        FXGVersion fileVersion = ((GraphicNode)(this.getDocumentNode())).getVersion();
        
        if (FXG_BLENDMODE_ADD_VALUE.equals(value))
        {
            return BlendMode.ADD;
        }
        else if (FXG_BLENDMODE_ALPHA_VALUE.equals(value))
        {
            return BlendMode.ALPHA;
        }
        else if (FXG_BLENDMODE_DARKEN_VALUE.equals(value))
        {
            return BlendMode.DARKEN;
        }
        else if (FXG_BLENDMODE_DIFFERENCE_VALUE.equals(value))
        {
            return BlendMode.DIFFERENCE;
        }
        else if (FXG_BLENDMODE_ERASE_VALUE.equals(value))
        {
            return BlendMode.ERASE;
        }
        else if (FXG_BLENDMODE_HARDLIGHT_VALUE.equals(value))
        {
            return BlendMode.HARDLIGHT;
        }
        else if (FXG_BLENDMODE_INVERT_VALUE.equals(value))
        {
            return BlendMode.INVERT;
        }
        else if (FXG_BLENDMODE_LAYER_VALUE.equals(value))
        {
            return BlendMode.LAYER;
        }
        else if (FXG_BLENDMODE_LIGHTEN_VALUE.equals(value))
        {
            return BlendMode.LIGHTEN;
        }
        else if (FXG_BLENDMODE_MULTIPLY_VALUE.equals(value))
        {
            return BlendMode.MULTIPLY;
        }
        else if (FXG_BLENDMODE_NORMAL_VALUE.equals(value))
        {
            return BlendMode.NORMAL;
        }
        else if (FXG_BLENDMODE_OVERLAY_VALUE.equals(value))
        {
            return BlendMode.OVERLAY;
        }
        else if (FXG_BLENDMODE_SCREEN_VALUE.equals(value))
        {
            return BlendMode.SCREEN;
        }
        else if (FXG_BLENDMODE_SUBTRACT_VALUE.equals(value))
        {
            return BlendMode.SUBTRACT;
        }
        else if (fileVersion.equalTo(FXGVersion.v1_0))
        {
            // Rest of the blend modes are unknown for FXG 1.0
            //Exception:Unknown blend mode: {0}.
            throw new FXGException(getStartLine(), getStartColumn(), "UnknownBlendMode", value);
        }
        else if (FXG_BLENDMODE_COLORDOGE_VALUE.equals(value))
        {
            return BlendMode.COLORDODGE;
        }
        else if (FXG_BLENDMODE_COLORBURN_VALUE.equals(value))
        {
            return BlendMode.COLORBURN;
        }
        else if (FXG_BLENDMODE_EXCLUSION_VALUE.equals(value))
        {
            return BlendMode.EXCLUSION;
        }
        else if (FXG_BLENDMODE_SOFTLIGHT_VALUE.equals(value))
        {
            return BlendMode.SOFTLIGHT;
        }
        else if (FXG_BLENDMODE_HUE_VALUE.equals(value))
        {
            return BlendMode.HUE;
        }
        else if (FXG_BLENDMODE_SATURATION_VALUE.equals(value))
        {
            return BlendMode.SATURATION;
        }
        else if (FXG_BLENDMODE_COLOR_VALUE.equals(value))
        {
            return BlendMode.COLOR;
        }
        else if (FXG_BLENDMODE_LUMINOSITY_VALUE.equals(value))
        {
            return BlendMode.LUMINOSITY;
        }
        else if (FXG_BLENDMODE_AUTO_VALUE.equals(value))
        {
            return BlendMode.AUTO;
        }
        else
        {
            if (isVersionGreaterThanCompiler())
            {
                // Warning: Minor version of this FXG file is greater than minor
                // version supported by this compiler. Log a warning for an unknown
                // blend mode.
                FXGLog.getLogger().log(FXGLogger.WARN, "UnknownBlendMode", null, getDocumentName(), startLine, startColumn);
            }
            else
            {
              //Exception:Unknown blend mode: {0} for FXGVersion 2.0.
                throw new FXGException(getStartLine(), getStartColumn(), "UnknownBlendMode", value);
            }
        }
            

        return defMode;
    }
    
 
    /**
     * Convert discreet transform attributes to child matrix. This allows
     *  concatenation of another matrix.
     */
    public void convertTransformAttrToMatrix()
    {
        try
        {
            MatrixNode matrixNode = MatrixNode.class.newInstance();
            // Convert discreet transform attributes to FXGMatrix.
            FXGMatrix matrix = FXGMatrix.convertToMatrix(scaleX, scaleY, rotation, x, y);
            // Set matrix attributes to FXGMatrix values.
            matrix.setMatrixNodeValue(matrixNode);
            // Reset all discreet transform attributes since matrix 
            // and discreet transform attributes cannot coexist.
            resetTransformAttr();
            // Add child matrix to the node.
            this.addChild(matrixNode);
        }
        catch (Throwable t)
        {
            throw new FXGException(mask.getStartLine(), mask.getStartColumn(), "InvalidChildMatrixNode", t);
        }        

    }
    
    /**
     * Reset discreet transform attributes to their default value. This allows
     *  child matrix can be set instead.
     */
    private void resetTransformAttr()
    {
        x = 0.0;
        y = 0.0;
        scaleX = 1.0;
        scaleY = 1.0;
        rotation = 0.0;
        translateSet = false;
        scaleSet = false;
        rotationSet = false;        
    }
}
