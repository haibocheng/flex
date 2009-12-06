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
import java.util.Map;

import static com.adobe.fxg.FXGConstants.*;

import com.adobe.fxg.FXGException;
import com.adobe.fxg.FXGVersion;
import com.adobe.fxg.dom.FXGNode;
import com.adobe.internal.fxg.dom.types.MaskType;
import com.adobe.internal.fxg.dom.types.ScalingGrid;

/**
 * Represents the root &lt;Graphic&gt; element of an FXG Document.
 * 
 * @author Peter Farland
 * @author Sujata Das
 */
public class GraphicNode extends AbstractFXGNode implements MaskableNode
{
    private String documentName = null;
    private FXGVersion version = null; // The version of FXG being processed.
    public Map<String, Class<? extends FXGNode>> reservedNodes;
    	
    //--------------------------------------------------------------------------
    //
    // Attributes
    //
    //--------------------------------------------------------------------------
	
	public double scaleGridLeft = 0.0;
    public double scaleGridTop = 0.0;
    public double scaleGridRight = 0.0;
    public double scaleGridBottom = 0.0;

    public double viewWidth = Double.NaN;
    public double viewHeight = Double.NaN;
    public MaskType maskType = MaskType.CLIP;

    protected boolean luminosityInvert=false;
    protected boolean luminosityClip=false;

    //Flag indicating whether the FXG version is newer than the compiler version.
    private boolean isVersionGreaterThanCompiler = false;

    //--------------------------------------------------------------------------
    //
    // Children
    //
    //--------------------------------------------------------------------------

    public List<GraphicContentNode> children;
    public LibraryNode library;
    public MaskingNode mask;
    
    /**
     * @return - true if version of the FXG file is greater than the compiler/FXGVersionHandler
     * version. false otherwise.
     */
    public boolean isVersionGreaterThanCompiler()
    {
        return isVersionGreaterThanCompiler;
    }
    
    /**
     * sets isVersionGreaterThanCompiler
     * @param versionGreaterThanCompiler
     */
    public void setVersionGreaterThanCompiler(boolean versionGreaterThanCompiler)
    {
        isVersionGreaterThanCompiler = versionGreaterThanCompiler;
    }
    
    /**
     * @return - the name of the FXG file being processed.
     */
    public String getDocumentName()
    {
        return documentName;
    }
    
    /**
     * Set the name of the FXG file being processed.
     * 
     * @param document name
     */
    public void setDocumentName(String documentName)
    {
        this.documentName = documentName;
    }

    /**
     * @return - version as FXGVersion.
     */
    public FXGVersion getVersion()
    {
        return version;
    }
    
    /**
     * Set the reserved nodes HashMap. Those XML element names are reserved and 
     * cannot be used as the definition name for a library element.
     * @param reserved nodes HashMap
     */
    public void setReservedNodes(Map<String, Class<? extends FXGNode>> reservedNodes)
    {
        this.reservedNodes = reservedNodes;
    }
    
    //--------------------------------------------------------------------------
    //
    // FXGNode Implementation
    //
    //--------------------------------------------------------------------------

    /**
     * Adds an FXG child node to this Graphic node. Supported child nodes
     * include graphic content nodes (e.g. Group, BitmapGraphic, Ellipse, Line,
     * Path, Rect, TextGraphic), control nodes (e.g. Library, Private), or
     * property nodes (e.g. mask).
     * 
     * @param child - a child FXG node to be added to this node.
     * @throws FXGException if the child is not supported by this node.
     */
    public void addChild(FXGNode child)
    {
        if (child instanceof MaskPropertyNode)
        {
        	/**
        	 * According to FXG 2.0 spec., <mask> must be before any graphical element.
        	 */
        	if (children != null)
        	{
                throw new FXGException(child.getStartLine(), child.getStartColumn(), "InvalidMaskElement");
        	}	
        	if (mask == null)
        	{
        		mask = ((MaskPropertyNode)child).mask;
        	}
        	else
        	{
                throw new FXGException(child.getStartLine(), child.getStartColumn(), "MultipleMaskElements");
        	}           
        }
        else if (child instanceof LibraryNode)
        {   
        	/**
        	 * According to FXG 2.0 spec., <Library> must be before <mask> and any graphical element.
        	 */
        	if (mask != null || children != null)
        	{
                throw new FXGException(child.getStartLine(), child.getStartColumn(), "InvalidLibraryElement");
        	}	
            if (library == null)
            {
                library = (LibraryNode)child;
            }
            else
            {
                throw new FXGException(child.getStartLine(), child.getStartColumn(), "MultipleLibraryElements");
            }
        }
        else if (child instanceof GraphicContentNode)
        {
            if (children == null)
                children = new ArrayList<GraphicContentNode>();

            if (child instanceof GroupNode)
            {
                GroupNode group = (GroupNode)child;

                if (insideScaleGrid)
                {
                    group.setInsideScaleGrid(true);
                }
            }


            children.add((GraphicContentNode)child);
        }
        else
        {
            super.addChild(child);
        }
    }

    /**
     * @return The unqualified name of a Graphic node, without tag markup.
     */
    public String getNodeName()
    {
        return FXG_GRAPHIC_ELEMENT;
    }

    /**
     * Sets an FXG attribute on this Graphic node.
     * 
     * @param name - the unqualified attribute name
     * @param value - the attribute value
     * @throws FXGException if the attribute is not supported by this node.
     */
    public void setAttribute(String name, String value)
    {
        if (FXG_SCALEGRIDLEFT_ATTRIBUTE.equals(name))
        {
            scaleGridLeft = DOMParserHelper.parseDouble(this, value, name);
            insideScaleGrid = true;
        }
        else if (FXG_SCALEGRIDTOP_ATTRIBUTE.equals(name))
        {
            scaleGridTop = DOMParserHelper.parseDouble(this, value, name);
            insideScaleGrid = true;
        }
        else if (FXG_SCALEGRIDRIGHT_ATTRIBUTE.equals(name))
        {
            scaleGridRight = DOMParserHelper.parseDouble(this, value, name);
            insideScaleGrid = true;
        }
        else if (FXG_SCALEGRIDBOTTOM_ATTRIBUTE.equals(name))
        {
            scaleGridBottom = DOMParserHelper.parseDouble(this, value, name);
            insideScaleGrid = true;
        }
        else if (FXG_VIEWWIDTH_ATTRIBUTE.equals(name))
        {
            viewWidth = DOMParserHelper.parseDouble(this, value, name);
        }
        else if (FXG_VIEWHEIGHT_ATTRIBUTE.equals(name))
        {
            viewHeight = DOMParserHelper.parseDouble(this, value, name);
        }
        else if (FXG_VERSION_ATTRIBUTE.equals(name))
        {
            try
            {
                version = FXGVersion.newInstance(DOMParserHelper.parseDouble(this, value, name));
            }
            catch (FXGException e)
            {
                throw new FXGException("InvalidVersionNumber", e);
            }
        }
        else if (FXG_MASKTYPE_ATTRIBUTE.equals(name))
        {
            maskType = DOMParserHelper.parseMaskType(this, value, maskType);
        }
        else if ((version != null) && (version.equalTo(FXGVersion.v1_0)))
        {
            // Rest of the attributes are not supported by FXG 1.0
            // Exception:Attribute {0} not supported by node {1}. 
            throw new FXGException(getStartLine(), getStartColumn(), "InvalidNodeAttribute", name, getNodeName());
        }
        else if (FXG_LUMINOSITYINVERT_ATTRIBUTE.equals(name))
        {
            luminosityInvert = DOMParserHelper.parseBoolean(this, value, name);
        }        
        else if (FXG_LUMINOSITYCLIP_ATTRIBUTE.equals(name))
        {
            luminosityClip = DOMParserHelper.parseBoolean(this, value, name); 
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
    // Other Methods
    //
    //--------------------------------------------------------------------------

    public PlaceObjectNode getDefinitionInstance(String name)
    {
        PlaceObjectNode instance = null;

        if (library != null)
        {
            DefinitionNode definition = library.getDefinition(name);
            if (definition != null)
            {
                instance = new PlaceObjectNode();
                instance.definition = definition;
            }
        }

        return instance;
    }

    public ScalingGrid getScalingGrid()
    {
        ScalingGrid scalingGrid = null;

        if (definesScaleGrid())
        {
            scalingGrid = new ScalingGrid();
            scalingGrid.scaleGridLeft = scaleGridLeft;
            scalingGrid.scaleGridTop = scaleGridTop;
            scalingGrid.scaleGridRight = scaleGridRight;
            scalingGrid.scaleGridBottom = scaleGridBottom;
        }

        return scalingGrid;
    }

    public boolean definesScaleGrid()
    {
        return definesScaleGrid;
    }

    public boolean isInsideScaleGrid()
    {
        return insideScaleGrid;
    }

    private boolean definesScaleGrid;
    private boolean insideScaleGrid;
}
