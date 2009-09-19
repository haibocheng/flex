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

public class LayeredNode extends Node
{
	LayeredNode(String uri, String localName, int size, DesignLayerNode parent)
	{
		super(uri, localName, size);
		layerParent = parent;
	}
	
	private DesignLayerNode layerParent;
	
	public void setLayerParent(DesignLayerNode node)
	{
		layerParent = node;
	}

	public DesignLayerNode getLayerParent()
	{
		return layerParent;
	}
	
	public void analyze(Analyzer analyzer)
	{
		analyzer.prepare(this);
		analyzer.analyze(this);
	}
}
