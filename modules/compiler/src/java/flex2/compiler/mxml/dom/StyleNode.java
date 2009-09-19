////////////////////////////////////////////////////////////////////////////////
//
//  ADOBE SYSTEMS INCORPORATED
//  Copyright 2004-2006 Adobe Systems Incorporated
//  All Rights Reserved.
//
//  NOTICE: Adobe permits you to use, modify, and distribute this file
//  in accordance with the terms of the license agreement accompanying it.
//
////////////////////////////////////////////////////////////////////////////////

package flex2.compiler.mxml.dom;

import flash.css.StyleSheet;
import flex2.compiler.util.QName;

import java.util.HashSet;
import java.util.Set;

/**
 * @author Clement Wong
 */
public class StyleNode extends Node
{
	public static final Set<QName> attributes;

	static
	{
		attributes = new HashSet<QName>();
		attributes.add(new QName("", "source"));
	}

	private StyleSheet styleSheet;

	StyleNode(String uri, String localName, int size)
	{
		super(uri, localName, size);
	}

	public void analyze(Analyzer analyzer)
	{
		analyzer.prepare(this);
		analyzer.analyze(this);
	}

    public StyleSheet getStyleSheet()
    {
        return styleSheet;
    }

    public void setStyleSheet(StyleSheet styleSheet)
    {
        this.styleSheet = styleSheet;
    }
}
