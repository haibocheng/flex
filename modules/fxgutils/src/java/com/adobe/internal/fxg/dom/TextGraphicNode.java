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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.adobe.fxg.FXGConstants.*;

import com.adobe.fxg.FXGException;
import com.adobe.fxg.dom.FXGNode;
import com.adobe.internal.fxg.dom.text.BRNode;
import com.adobe.internal.fxg.dom.CDATANode;
import com.adobe.internal.fxg.dom.text.ParagraphNode;
import com.adobe.internal.fxg.dom.text.SpanNode;
import com.adobe.internal.fxg.dom.text.AbstractCharacterTextNode;
import com.adobe.internal.fxg.dom.types.Kerning;
import com.adobe.internal.fxg.dom.types.LineBreak;
import com.adobe.internal.fxg.dom.types.WhiteSpaceCollapse;
import com.adobe.internal.fxg.swf.TextHelper;

/**
 * @author Peter Farland
 */
public class TextGraphicNode extends GraphicContentNode implements TextNode
{
    //--------------------------------------------------------------------------
    //
    // Attributes
    //
    //--------------------------------------------------------------------------

    // Text Attributes
    public double width = 0.0;
    public double height = 0.0;
    public double paddingLeft = 0.0;
    public double paddingRight = 0.0;
    public double paddingBottom = 0.0;
    public double paddingTop = 0.0;

    // Character Attributes
    public String fontFamily = "Times New Roman";
    public double fontSize = 12.0;
    public String fontStyle = "normal";
    public String fontWeight = "normal";
    public double lineHeight = 120.0;
    public String textDecoration = "none";
    public WhiteSpaceCollapse whiteSpaceCollapse = WhiteSpaceCollapse.PRESERVE;
    public LineBreak lineBreak = LineBreak.TOFIT;
    public boolean lineThrough = false;
    public double tracking = 0.0;
    public Kerning kerning = Kerning.AUTO;
    public double textAlpha = 1.0;
    public int color = COLOR_BLACK;

    // Paragraph Attributes
    public String textAlign = "left";
    public String textAlignLast = "left";
    public double textIndent = 0.0;
    public double marginLeft = 0.0;
    public double marginRight = 0.0;
    public double marginTop = 0.0;
    public double marginBottom = 0.0;
    public String direction = "ltr";
    public String blockProgression = "tb";
    
    private boolean contiguous = false;

    //--------------------------------------------------------------------------
    //
    // Text Node Attribute Helpers
    //
    //--------------------------------------------------------------------------
    /**
     * The attributes set on this node.
     */
    protected Map<String, String> textAttributes;

    /**
     * @return A Map recording the attribute names and values set on this
     * text node.
     */
    public Map<String, String> getTextAttributes()
    {
        return textAttributes;
    }

    /**
     * This nodes child text nodes.
     */
    protected List<TextNode> content;

    /**
     * @return The List of child nodes of this text node. 
     */
    public List<TextNode> getTextChildren()
    {
        return content;
    }

    /**
     * @return The List of child property nodes of this text node.
     */
    public List<TextNode> getTextProperties()
    {
        return null;
    }

    /**
     * Remember that an attribute was set on this node.
     * 
     * @param name - the unqualified attribute name.
     * @param value - the attribute value.
     */
    protected void rememberAttribute(String name, String value)
    {
        if (textAttributes == null)
            textAttributes = new HashMap<String, String>(4);

        textAttributes.put(name, value);
    }

    /**
     * &lt;TextGraphic&gt; content allows child &lt;p&gt;, &lt;span&gt; and
     * &lt;br /&gt; tags, as well as character data (text content).
     * 
     * @param child - a child FXG node to be added to this node.
     * @throws FXGException if the child is not supported by this node.
     */
    public void addContentChild(FXGNode child)
    {
        if (child instanceof ParagraphNode
                || child instanceof BRNode
                || child instanceof SpanNode
                || child instanceof CDATANode)
        {
            if (content == null)
            {
                content = new ArrayList<TextNode>();
                contiguous = true;
            }
            
            if (!contiguous)
            {
            	throw new FXGException(child.getStartLine(), child.getStartColumn(), "InvalidRichTextContent");            	
            }

            content.add((TextNode)child);
        }
    }

    
    //--------------------------------------------------------------------------
    //
    // FXGNode Implementation
    //
    //--------------------------------------------------------------------------

    /**
     * This method is invoked for only non-content children.
     * 
     * @param child - a child FXG node to be added to this node.
     * @throws FXGException if the child is not supported by this node.
     */
    @Override
    public void addChild(FXGNode child)
    {
    	if (child instanceof CDATANode)
        {
            if(TextHelper.ignorableWhitespace(((CDATANode)child).content))
            {
            	/**
            	 * Ignorable white spaces don't break content contiguous 
            	 * rule and should be ignored.
            	 */
            	return;
            }
            else
            {
            	throw new FXGException(child.getStartLine(), child.getStartColumn(), "InvalidTextGraphicContent");
            }
        }
        else 
        {
            super.addChild(child);
            contiguous = false;
        }
    }

    /**
     * @return The unqualified name of a TextGraphic node, without tag markup.
     */
    public String getNodeName()
    {
        return FXG_TEXTGRAPHIC_ELEMENT;
    }

    /**
     * Sets an FXG attribute on this TextGraphic node.
     * 
     * In addition to the attributes supported by all graphic content nodes,
     * TextGraphic supports the following attributes.
     * 
     * <p>
     * <ul>
     * <li><b>width</b> (Number): The width of the text box to render text
     * in.</li>
     * <li><b>height</b> (Number): The height of the text box to render text
     * in.</li>
     * <li><b>paddingLeft</b> (Number): Inset from left edge to content area.
     * Units in pixels, defaults to 0.</li>
     * <li><b>paddingRight</b> (Number): Inset from right edge to content area.
     * Units in pixels, defaults to 0.</li>
     * <li><b>paddingTop</b> (Number): Inset from top edge to content area.
     * Units in pixels, defaults to 0.</li>
     * <li><b>paddingBottom</b> (Number): Inset from bottom edge to content
     * area. Units in pixels, defaults to 0.</li>
     * </ul>
     * </p>
     * @param name - the unqualified attribute name.
     * @param value - the attribute value.
     */
    @Override
    public void setAttribute(String name,  String value)
    {
        
        if (FXG_WIDTH_ATTRIBUTE.equals(name))
        {
            width = parseDouble(value);
        }
        else if (FXG_HEIGHT_ATTRIBUTE.equals(name))
        {
            height = parseDouble(value);
        }
        else if (FXG_PADDINGLEFT_ATTRIBUTE.equals(name))
        {
            paddingLeft = parseDouble(value);
        }
        else if (FXG_PADDINGRIGHT_ATTRIBUTE.equals(name))
        {
            paddingRight = parseDouble(value);
         }
        else if (FXG_PADDINGBOTTOM_ATTRIBUTE.equals(name))
        {
            paddingBottom = parseDouble(value);
        }
        else if (FXG_PADDINGTOP_ATTRIBUTE.equals(name))
        {
            paddingTop = parseDouble(value);
        }
        else if (FXG_FONTFAMILY_ATTRIBUTE.equals(name))
        {
            fontFamily = value;
        }
        else if (FXG_FONTSIZE_ATTRIBUTE.equals(name))
        {
            fontSize = parseDouble(value);
        }
        else if (FXG_FONTSTYLE_ATTRIBUTE.equals(name))
        {
            fontStyle = value;
        }
        else if (FXG_FONTWEIGHT_ATTRIBUTE.equals(name))
        {
            fontWeight = value;
        }
        else if (FXG_LINEHEIGHT_ATTRIBUTE.equals(name))
        {
            lineHeight = parsePercent(value);
        }
        else if (FXG_TEXTDECORATION_ATTRIBUTE.equals(name))
        {
            textDecoration = value;
        }
        else if (FXG_WHITESPACECOLLAPSE_ATTRIBUTE.equals(name))
        {
            whiteSpaceCollapse = AbstractCharacterTextNode.getWhiteSpaceCollapse(this, value);
        }
        else if (FXG_LINEBREAK_ATTRIBUTE.equals(name))
        {
            lineBreak = AbstractCharacterTextNode.getLineBreak(this, value);
        }
        else if (FXG_TRACKING_ATTRIBUTE.equals(name))
        {
            tracking = parsePercent(value);
        }
        else if (FXG_KERNING_ATTRIBUTE.equals(name))
        {
            kerning = AbstractCharacterTextNode.getKerning(this, value);
        }
        else if (FXG_TEXTALPHA_ATTRIBUTE.equals(name))
        {
            textAlpha = parseDouble(value, ALPHA_MIN_INCLUSIVE, ALPHA_MAX_INCLUSIVE, textAlpha);
        }
        else if (FXG_COLOR_ATTRIBUTE.equals(name))
        {
            color = parseRGB(value, color);
        }
        else if (FXG_TEXTALIGN_ATTRIBUTE.equals(name))
        {
            textAlign = value;
        }
        else if (FXG_TEXTALIGNLAST_ATTRIBUTE.equals(name))
        {
            textAlignLast = value;
        }
        else if (FXG_TEXTINDENT_ATTRIBUTE.equals(name))
        {
            textIndent = parseDouble(value);
        }
        else if (FXG_MARGINLEFT_ATTRIBUTE.equals(name))
        {
            marginLeft = parseDouble(value);
        }
        else if (FXG_MARGINRIGHT_ATTRIBUTE.equals(name))
        {
            marginRight = parseDouble(value);
        }
        else if (FXG_MARGINTOP_ATTRIBUTE.equals(name))
        {
            marginTop = parseDouble(value);
        }
        
        else if (FXG_MARGINBOTTOM_ATTRIBUTE.equals(name))
        {
            marginBottom = parseDouble(value);
        }
        else if (FXG_DIRECTION_ATTRIBUTE.equals(name))
        {
            direction = value;
        }
        else if (FXG_BLOCKPROGRESSION_ATTRIBUTE.equals(name))
        {
            blockProgression = value;
        }
        else if (FXG_X_ATTRIBUTE.equals(name))
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
            blendModeSet = true;
        }
        else if (FXG_MASKTYPE_ATTRIBUTE.equals(name))
        {
            maskType = parseMaskType(value, maskType);
            maskTypeSet = true;
        }
        else if (FXG_VISIBLE_ATTRIBUTE.equals(name))
        {
            visible = parseBoolean(value);
        }      
        else if ( FXG_LINETHROUGH_ATTRIBUTE.equals(name))
        {
            lineThrough = parseBoolean(value);
        }        
        else if (FXG_ID_ATTRIBUTE.equals(name))
        {
            //id = value;
        }
        else
        {
        	//Exception:Attribute, {0}, not supported by node: {1}.
            throw new FXGException(getStartLine(), getStartColumn(), "InvalidNodeAttribute", name, getNodeName());
        }
        
        // Remember attribute was set on this node.
        rememberAttribute(name, value);                
    }
}
