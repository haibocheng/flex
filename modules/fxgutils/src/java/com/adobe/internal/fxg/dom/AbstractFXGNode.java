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

import static com.adobe.fxg.FXGConstants.*;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.adobe.fxg.FXGException;
import com.adobe.fxg.FXGVersion;
import com.adobe.fxg.dom.FXGNode;
import com.adobe.fxg.util.FXGLog;
import com.adobe.fxg.util.FXGLogger;
import com.adobe.internal.fxg.dom.types.InterpolationMethod;
import com.adobe.internal.fxg.dom.types.MaskType;
import com.adobe.internal.fxg.dom.types.FillMode;
import com.adobe.internal.fxg.dom.types.SpreadMethod;

/**
 * A helper class that serves as the base implementation of FXGNode. Subclasses
 * can delegate to this class to handle unknown attributes or children.
 * 
 * @author Peter Farland
 * @author Sujata Das
 */
public abstract class AbstractFXGNode implements FXGNode
{
    protected static final double ALPHA_MIN_INCLUSIVE = 0.0;
    protected static final double ALPHA_MAX_INCLUSIVE = 1.0;
    protected static final int COLOR_BLACK = 0xFF000000;
    protected static final int COLOR_WHITE = 0xFFFFFFFF;
    protected static final int COLOR_RED = 0xFFFF0000;
    protected static final int GRADIENT_ENTRIES_MAX_INCLUSIVE = 16;
    protected static Pattern idPattern = Pattern.compile ("[a-zA-Z_][a-zA-Z_0-9]*");
    
    protected FXGNode documentNode;
    protected String uri;
    protected int startLine;
    protected int startColumn;
    protected int endLine;
    protected int endColumn;

    public static final double EPSILON = 0.00001;

    //--------------------------------------------------------------------------
    //
    // FXGNode Implementation
    //
    //--------------------------------------------------------------------------

    /**
     * Adds an FXG child node to this node.
     * 
     * @param child - a child FXG node to be added to this node.
     * @throws FXGException if the child is not supported by this node.
     */
    public void addChild(FXGNode child)
    {
    	//Exception:Child node {0} is not supported by node {1}.
        if (child == null) 
        {
            throw new FXGException("InvalidChildNode",   null, getNodeName());
        }
        else
        {
            throw new FXGException(child.getStartLine(), child.getStartColumn(), "InvalidChildNode",  child.getNodeName(), getNodeName());            
        }
    }

    /**
     * Sets an FXG attribute on this FXG node.
     * 
     * @param name - the unqualified attribute name
     * @param value - the attribute value
     * @param defaultValue - the default attribute value
     * @throws FXGException if the attribute name is not supported by this node.
     */
    public void setAttribute(String name, String value)
    {
    	if (isVersionGreaterThanCompiler())
        {
            // Warning: Minor version of this FXG file is greater than minor
            // version supported by this compiler. Log a warning for an unknown
            // attribute or an attribute with values out of range.
    	    FXGLog.getLogger().log(FXGLogger.WARN, "UnknownNodeAttribute", null, getDocumentName(), startLine, startColumn);
        }
        else
        {
            // Exception:Attribute {0} not supported by node {1}.
            throw new FXGException(getStartLine(), getStartColumn(), "InvalidNodeAttribute", name, getNodeName());
        }
    }

    /**
     * @return The root node of the FXG document.
     */
    public FXGNode getDocumentNode()
    {
        return documentNode;
    }

    /**
     * Establishes the root node of the FXG document containing this node.
     * @param root - the root node of the FXG document.
     */
    public void setDocumentNode(FXGNode root)
    {
        documentNode = root;
    }

    /**
     * return the namespace URI of this node.
     */
    public String getNodeURI()
    {
        return uri;
    }

    /**
     * @param uri - the namespace URI of this node.
     */
    public void setNodeURI(String uri)
    {
        this.uri = uri;
    }

    /**
     * @return the line on which the node declaration started.
     */
    public int getStartLine()
    {
        return startLine;
    }

    /**
     * @param line - the line on which the node declaration started.
     */
    public void setStartLine(int line)
    {
        startLine = line;
    }

    /**
     * @return - the column on which the node declaration started.
     */
    public int getStartColumn()
    {
        return startColumn;
    }

    /**
     * @param column - the line on which the node declaration started.
     */
    public void setStartColumn(int column)
    {
        startColumn = column;
    }

    /**
     * @return the line on which the node declaration ended.
     */
    public int getEndLine()
    {
        return endLine;
    }

    /**
     * @param line - the line on which the node declaration ended.
     */
    public void setEndLine(int line)
    {
        endLine = line;
    }

    /**
     * @return - the column on which the node declaration ended.
     */
    public int getEndColumn()
    {
        return endColumn;
    }

    /**
     * @param - column - the column on which the node declaration ended.
     */
    public void setEndColumn(int column)
    {
        endColumn = column;
    }
    
    //--------------------------------------------------------------------------
    //
    // Helper Methods
    //
    //--------------------------------------------------------------------------

    /**
     * @return - true if version of the FXG file is greater than the compiler
     * version. false otherwise.
     */
    public boolean isVersionGreaterThanCompiler()
    {
        return ((GraphicNode)this.documentNode).isVersionGreaterThanCompiler();
    }

    /**
     * 
     * @returns the file version
     */
    public FXGVersion getFileVersion()
    {
        return ((GraphicNode)this.documentNode).getVersion();
    }
    /**
     * @return - the name of the FXG file being processed.
     */
    protected String getDocumentName()
    {
        return ((GraphicNode)this.getDocumentNode()).getDocumentName();
    }
    
    /**
     * Convert an FXG String value to a boolean.
     * 
     * @param value - the FXG String value.
     * @return true for the String 'true' (case insensitive), otherwise false.
     */
    protected boolean parseBoolean(String value)
    {
        return Boolean.parseBoolean(value);
    }

    /**
     * Convert an FXG hexadecimal color String to an int. The
     * format must be a '#' character followed by six hexadecimal characters,
     * i.e. '#RRGGBB'.
     * 
     * @param value - an FXG a hexadecimal color String.
     * @param defaultValue - the default RGB color as an int; if the encountered
     *  minor version is later than the supported minor version and the 
     *  attribute value is out-of-range, the default value is returned.
     * @return an RGB color represented as an int.
     * @throws FXGException if the String did not represent a valid color value. 
     */
    protected int parseRGB(String value, int defaultValue)
    {
        value = value.trim();

        if (value.charAt(0) == '#')
        {
            value = value.substring(1);
        }
        // Unofficially allow ActionScript-styled hexadecimal syntax to assist
        // sharing MXML graphics media from Flex. 
        else if (value.startsWith("0x"))
        {
            value = value.substring(2);
        }
        // else Unofficially allow the '#' character to be omitted.

        int a = 255;
        int r = 0;
        int g = 0;
        int b = 0;

        if (value.length() == 6)
        {
            r = Integer.parseInt(value.substring(0, 2), 16) & 0xFF;
            g = Integer.parseInt(value.substring(2, 4), 16) & 0xFF;
            b = Integer.parseInt(value.substring(4, 6), 16) & 0xFF;
        }
        else
        {
            if (isVersionGreaterThanCompiler())
            {
                // Warning: Minor version of this FXG file is greater than minor
                // version supported by this compiler. Use default value if an
                // attribute value is out of range.
                FXGLog.getLogger().log(FXGLogger.WARN, "DefaultColorValue", null, getDocumentName(), startLine, startColumn);
                return defaultValue;
            }
            else
            {
                // Exception:Malformed color value - must be of the form
                // '#RRGGBB'
                throw new FXGException(getStartLine(), getStartColumn(), "InvalidColorValue");
            }
        }

        return  (a << 24) | (r << 16) | (g << 8) | b;
    }
    
    /**
     * Convert an FXG hexadecimal color String to an int. The
     * format must be a '#' character followed by six hexadecimal characters,
     * i.e. '#RRGGBB'.
     * 
     * @param value - an FXG a hexadecimal color String.
     * @param defaultValue - the default RGB color as an int; if the encountered
     *  minor version is later than the supported minor version and the 
     *  attribute value is out-of-range, the default value is returned.
     * @return an RGB color represented as an int.
     * @throws FXGException if the String did not represent a valid color value. 
     */
    protected int parseRGB(String value)
    {
        value = value.trim();

        if (value.charAt(0) == '#')
        {
            value = value.substring(1);
        }
        // Unofficially allow ActionScript-styled hexadecimal syntax to assist
        // sharing MXML graphics media from Flex. 
        else if (value.startsWith("0x"))
        {
            value = value.substring(2);
        }
        // else Unofficially allow the '#' character to be omitted.

        int a = 255;
        int r = 0;
        int g = 0;
        int b = 0;

        if (value.length() == 6)
        {
            r = Integer.parseInt(value.substring(0, 2), 16) & 0xFF;
            g = Integer.parseInt(value.substring(2, 4), 16) & 0xFF;
            b = Integer.parseInt(value.substring(4, 6), 16) & 0xFF;
        }
        else
        {
            // Exception:Malformed color value - must be of the form
            // '#RRGGBB'
            throw new FXGException(getStartLine(), getStartColumn(), "InvalidColorValue");
        }

        return  (a << 24) | (r << 16) | (g << 8) | b;
    }

    /**
     * Convert an FXG String value to a double.
     * 
     * @param value - the FXG String value.
     * @return the double precision floating point value represented by the
     * String.
     * @throws NumberFormatException if the String did not represent a double.
     */
    protected double parseDouble(String value)
    {
        return Double.parseDouble(value);
    }

    /**
     * Convert an FXG String value to a double after taking care of the % sign.
     * 
     * @param value - the FXG String value.
    * @return the double precision floating point value represented by the
     * String.
     * @throws NumberFormatException if the String did not represent a double.
     */
    protected double parsePercent(String value)
    {
        value = value.trim();

        if (value.charAt(value.length()-1) == '%')
        {
            value = value.substring(0, value.length()-1);
            value = value.trim();
        }

        return Double.parseDouble(value);
    }
    
    /**
     * Convert an FXG String value to a float.
     * 
     * @param value - the FXG String value.
     * @return the floating point value represented by the String.
     * @throws NumberFormatException if the String did not represent a double.
     */
    protected float parseFloat(String value)
    {
        return Float.parseFloat(value);
    }

    /**
     * Convert an FXG String value to a double and check that the result is
     * within the specified range (inclusive).
     * 
     * @param value - the FXG String value.
     * @param min - the smallest double value that the result must be greater
     * or equal to.
     * @param min - the largest double value that the result must be smaller
     * than or equal to.
     * @param defaultValue - the default double value; if the encountered minor 
     * version is later than the supported minor version and the attribute value
     *  is out-of-range, the default value is returned.
     * @return the double precision floating point value represented by the
     * String.
     * @throws NumberFormatException if the String did not represent a double.
     * @throws FXGException if the result did not lie within the specified
     * range.
     */
    protected double parseDouble(String value, double min, double max, double defaultValue)
    {
        double d = Double.parseDouble(value);
        if (d >= min && d <= max)
        {
            return d;
        }

        if (isVersionGreaterThanCompiler())
        {
            // Warning: Minor version of this FXG file is greater than minor
            // version supported by this compiler. Use default value if an
            // attribute value is out of range.
            FXGLog.getLogger().log(FXGLogger.WARN, "DefaultAttributeValue", null, getDocumentName(), startLine, startColumn);
            return defaultValue;
        }
        else
        {
            // Exception:Numeric value {0} must be greater than or equal to {1}
            // and less than or equal to {2}.
            throw new FXGException(getStartLine(), getStartColumn(), "OutOfRangeValue", value, min, max);
        }
    }
    /**
     * Convert an FXG String value to an int and check that the result is
     * within the specified range (inclusive).
     * 
     * @param value - the FXG String value.
     * @param min - the smallest int value that the result must be greater
     * or equal to.
     * @param min - the largest int value that the result must be smaller
     * than or equal to.
     * @param defaultValue - the default int value; if the encountered minor 
     * version is later than the supported minor version and the attribute value
     *  is out-of-range, the default value is returned.
     * @return the integer value represented by the String.
     * @throws NumberFormatException if the String did not represent an integer.
     * @throws FXGException if the result did not lie within the specified
     * range.
     */
    protected int parseInt(String value, int min, int max, int defaultValue)
    {
        int i = Integer.parseInt(value);
        if (i >= min && i <= max)
        {
            return i;
        }

    	if (isVersionGreaterThanCompiler())
        {
            // Warning: Minor version of this FXG file is greater than minor
            // version supported by this compiler. Use default value if an
            // attribute value is out of range.
    	    FXGLog.getLogger().log(FXGLogger.WARN, "DefaultAttributeValue", null, getDocumentName(), startLine, startColumn);
            return defaultValue;
        }
        else
        {
            // Exception:Numeric value {0} must be greater than or equal to {1}
            // and less than or equal to {2}.
            throw new FXGException(getStartLine(), getStartColumn(), "OutOfRangeValue", value, min, max);
        }
    }
    
    /**
     * Convert an FXG String value to a integer.
     * 
     * @param value - the FXG String value.
     * @return the integer value represented by the String.
     * @throws NumberFormatException if the String did not represent a integer.
     */
    protected double parseInt(String value)
    {
        return Integer.parseInt(value);
    }

    /**
     * Convert an FXG String value to an InterpolationMethod enumeration.
     * 
     * @param value - the FXG String value
     * @param defaultValue - the FXG InterpolationMethod default value
     * @return the matching InterpolationMethod; if the encountered minor 
     * version is later than the supported minor version and the attribute value
     *  is out-of-range, the default value is returned.
     * @throws FXGException if the String did not match a known
     * InterpolationMethod.
     */
    protected InterpolationMethod parseInterpolationMethod(String value, InterpolationMethod defaultValue)
    {
        if (FXG_INTERPOLATION_RGB_VALUE.equals(value))
        {
            return InterpolationMethod.RGB;
        }
        else if (FXG_INTERPOLATION_LINEARRGB_VALUE.equals(value))
        {
            return InterpolationMethod.LINEAR_RGB;
        }
        else
        {
            if (isVersionGreaterThanCompiler())
            {
                // Warning: Minor version of this FXG file is greater than minor
                // version supported by this compiler. Use default value if an
                // attribute value is out of range.
                FXGLog.getLogger().log(FXGLogger.WARN, "DefaultAttributeValue", null, getDocumentName(), startLine, startColumn);
                return defaultValue;
            }
            else
            {
                // Exception:Unknown interpolation method {0}.
                throw new FXGException(getStartLine(), getStartColumn(), "UnknownInterpolationMethod", value);
            }
        }
    }

    /**
     * Convert an FXG String value to a MaskType enumeration.
     * 
     * @param value - the FXG String value
     * @param defaultValue - the FXG MaskType default value; if the encountered 
     * minor version is later than the supported minor version and the attribute
     *  value is out-of-range, the default value is returned.
     * @return the matching MaskType
     * @throws FXGException if the String did not match a known
     * MaskType.
     */
    protected MaskType parseMaskType(String value, MaskType defaultValue)
    {
        if (FXG_MASK_CLIP_VALUE.equals(value))
        {
            return MaskType.CLIP;
        }
        else if (FXG_MASK_ALPHA_VALUE.equals(value))
        {
            return MaskType.ALPHA;
        }
        else if (getFileVersion().equalTo(FXGVersion.v1_0))
        {
            // FXG 1.0 does not support any more maskTypes
            // Exception:Unknown maskType {0}.
            throw new FXGException(getStartLine(), getStartColumn(), "UnknownMaskType", value);            
        }
        else if (FXG_MASK_LUMINOSITY_VALUE.equals(value))
        {
            return MaskType.LUMINOSITY;
        }
        else
        {
            if (isVersionGreaterThanCompiler())
            {
                // Warning: Minor version of this FXG file is greater than minor
                // version supported by this compiler. Use default value if an
                // attribute value is out of range.
                FXGLog.getLogger().log(FXGLogger.WARN, "DefaultAttributeValue", null, getDocumentName(), startLine, startColumn);
                return defaultValue;
            }
            else
            {
                // Exception:Unknown maskType {0}.
                throw new FXGException(getStartLine(), getStartColumn(), "UnknownMaskType", value);
            }
        }
    }

    /**
     * Convert an FXG String value to a fillMode enumeration.
     * 
     * @param value - the FXG String value.
     * @return the matching fillMode value.
     * @throws FXGException if the String did not match a known
     * fillMode value.
     */
    protected FillMode parseFillMode(String value, FillMode defaultValue)
    {
        if (FXG_FILLMODE_CLIP_VALUE.equals(value))
        {
            return FillMode.CLIP;
        }
        else if (FXG_FILLMODE_REPEAT_VALUE.equals(value))
        {
            return FillMode.REPEAT;
        }
        else if (FXG_FILLMODE_SCALE_VALUE.equals(value))
        {
            return FillMode.SCALE;
        }
        else
        {
            if (isVersionGreaterThanCompiler())
            {
                // Warning: Minor version of this FXG file is greater than minor
                // version supported by this compiler. Use default value if an
                // attribute value is out of range.
                FXGLog.getLogger().log(FXGLogger.WARN, "DefaultAttributeValue", null, getDocumentName(), startLine, startColumn);
                return defaultValue;
            }
            else
            {
                // Exception:Unknown fill mode: {0}.
                throw new FXGException(getStartLine(), getStartColumn(), "UnknownFillMode", value);
            }
        }
            
    }
    
    /**
     * Convert an FXG String value to a SpreadMethod enumeration.
     * 
     * @param value - the FXG String value
     * @param defaultValue - the FXG SpreadMethod default value
     * @return the matching SpreadMethod; if the encountered minor version is 
     * later than the supported minor version and the attribute value is 
     * out-of-range, the default value is returned.
     * @throws FXGException if the String did not match a known
     * SpreadMethod.
     */
    protected SpreadMethod parseSpreadMethod(String value, SpreadMethod defaultValue)
    {
        if (FXG_SPREADMETHOD_PAD_VALUE.equals(value))
        {
            return SpreadMethod.PAD;
        }
        else if (FXG_SPREADMETHOD_REFLECT_VALUE.equals(value))
        {
            return SpreadMethod.REFLECT;
        }
        else if (FXG_SPREADMETHOD_REPEAT_VALUE.equals(value))
        {
            return SpreadMethod.REPEAT;
        }
        else
        {
            if (isVersionGreaterThanCompiler())
            {
                // Warning: Minor version of this FXG file is greater than minor
                // version supported by this compiler. Use default value if an
                // attribute value is out of range.
                FXGLog.getLogger().log(FXGLogger.WARN, "DefaultAttributeValue", null, getDocumentName(), startLine, startColumn);
                return defaultValue;
            }
            else
            {
                // Exception:Unknown spreadMethod {0}.
                throw new FXGException(getStartLine(), getStartColumn(), "UnknownSpreadMethod", value);
            }
        }
    }
    
    /**
     * Convert an FXG String value to a Identifier matching pattern 
     * [a-zA-Z_][a-zA-Z_0-9]*.
     * 
     * @param value - the FXG String value
     * @throws FXGException if the String did not match the pattern.
     */
    protected String parseIdentifier(String value, String defaultValue)
    {
        Matcher m;

        m = idPattern.matcher(value);
        if (m.matches ())
        {
            return value; 
        }
        else
        {
            if (isVersionGreaterThanCompiler())
            {
                // Warning: Minor version of this FXG file is greater than minor
                // version supported by this compiler. Use default value if an
                // attribute value is out of range.
                FXGLog.getLogger().log(FXGLogger.WARN, "DefaultAttributeValue", null, getDocumentName(), startLine, startColumn);
                return defaultValue;
            }
            else
            {
                //Exception: Invalid identifier format: {0}
                throw new FXGException(getStartLine(), getStartColumn(), "InvalidIdentifierFormat", value);
            }
        }
    }
}
