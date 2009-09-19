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

package flex2.compiler.mxml.dom;

import java.util.HashSet;
import java.util.Set;

import flex2.compiler.util.QName;

/**
 * <Private> element in the FXG or MXML namespace.
 * 
 * A container for design-time private data. Not available at runtime.
 * 
 * @author dloverin
 *
 */
public class PrivateNode extends Node
{
    public static final Set<QName> attributes;

    static
    {
        attributes = new HashSet<QName>();
    }
    
	PrivateNode(String uri, String localName, int size)
	{
		super(uri, localName, size);
	}

	
	public void analyze(Analyzer analyzer) {
		analyzer.prepare(this);
		analyzer.analyze(this);
	}

}
