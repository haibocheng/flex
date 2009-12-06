////////////////////////////////////////////////////////////////////////////////
//
//  ADOBE SYSTEMS INCORPORATED
//  Copyright 2004-2008 Adobe Systems Incorporated
//  All Rights Reserved.
//
//  NOTICE: Adobe permits you to use, modify, and distribute this file
//  in accordance with the terms of the license agreement accompanying it.
//
////////////////////////////////////////////////////////////////////////////////

package flex2.compiler.mxml.dom;

import java.util.List;

import flex2.compiler.mxml.Element;
import flex2.compiler.mxml.Token;

/**
 * @author Clement Wong
 */
public class Node extends Element
{
	public Node(String uri, String localName)
	{
		this(uri, localName, 0);
	}

	public Node(String uri, String localName, int size)
	{
		super(uri, localName, size);
		index = 0;
	}

	private int index;

	public void setIndex(int index)
	{
		this.index = index;
	}

	public int getIndex()
	{
		return index;
	}

	public void analyze(Analyzer analyzer)
	{
		analyzer.prepare(this);
		analyzer.analyze(this);
	}

	public String toString()
	{
		return image + " " + beginLine;
	}

	@Override
    public void addChild(Token child)
    {
	    if (hasText && !preserveWhitespace)
	    {
	        if (child instanceof CDATANode)
	        {
	            CDATANode cdata = (CDATANode)child;
	            if (cdata.isWhitespace())
	                return;
	        }
	    }

	    super.addChild(child);
    }

    @Override
    public void addChildren(List<Token> children)
    {
        if (hasText && children != null)
        {
            for (Token child : children)
            {
                addChild(child);
            }
            return;
        }

        super.addChildren(children);
    }

    public String comment;

	public boolean hasText;

	public boolean preserveWhitespace;
}
