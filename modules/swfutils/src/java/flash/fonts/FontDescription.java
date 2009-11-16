////////////////////////////////////////////////////////////////////////////////
//
// ADOBE SYSTEMS INCORPORATED
// Copyright 2003-2007 Adobe Systems Incorporated
// All Rights Reserved.
//
// NOTICE: Adobe permits you to use, modify, and distribute this file
// in accordance with the terms of the license agreement accompanying it.
//
////////////////////////////////////////////////////////////////////////////////

package flash.fonts;

/**
 * A FontDescription provides FontManagers a context that describes how to
 * locate a font for embedding, the font style characteristics and any options
 * that may control how it is to be embedded.  
 */
public class FontDescription
{
    /**
     * The name to use to register the font with the SWF.
     */
    public String alias;

    /**
     * The source of the font information, typically a URL pointing to a font
     * file.
     * 
     * The source may alternatively be just a String representing the font
     * family name of a font installed locally on the operating system.
     */
    public Object source;

    /**
     * The font style, represented as an int.
     * 
     * Plain is 0, Bold is 1, Italic is 2, and Bold+Italic is 3.
     */
    public int style;

    /**
     * The Unicode characters to include in the DefineFont, or pass null to
     * include all available characters.
     */
    public String unicodeRanges;

    /**
     * Controls whether advanced anti-aliasing information should be included
     * (if it is available).
     */
    public boolean advancedAntiAliasing;

    /**
     * Controls whether the font should be embedded using compact font format
     * (if supported).
     */
    public boolean compactFontFormat;

    /**
     * Tests whether another FontDescription describes the same font.
     * 
     * Note that the alias is not considered in the comparison.
     * 
     * @param value Another FontDescription instance to test for equality.
     * @return
     */
    public boolean equals(Object value)
    {
        if (this == value)
        {
            return true;
        }
        else if (value != null && value instanceof FontDescription)
        {
            FontDescription other = (FontDescription)value;

            if (style != other.style)
                return false;
            
            if (compactFontFormat != other.compactFontFormat)
                return false;

            if (advancedAntiAliasing != other.advancedAntiAliasing)
                return false;

            if (unicodeRanges == null && other.unicodeRanges != null)
                return false;

            if (source == null && other.source != null)
                return false;

            if (unicodeRanges != null && !unicodeRanges.equals(other.unicodeRanges))
                return false;

            if (source != null && !source.equals(other.source))
                return false;

            return true;
        }

        return false;
    }

    /**
     * Computes a hash code for this FontDescription instance. Note that the
     * alias is not considered in calculating a hash code.
     * 
     * @return a hash code based on all fields used to describe the font. 
     */
    public int hashCode()
    {
        int result = style;

        if (source != null)
            result ^= source.hashCode();

        if (unicodeRanges != null)
            result ^= unicodeRanges.hashCode();

        return result;
    }
}
