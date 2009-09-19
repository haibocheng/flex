////////////////////////////////////////////////////////////////////////////////
//
//  ADOBE SYSTEMS INCORPORATED
//  Copyright 2005-2006 Adobe Systems Incorporated
//  All Rights Reserved.
//
//  NOTICE: Adobe permits you to use, modify, and distribute this file
//  in accordance with the terms of the license agreement accompanying it.
//
////////////////////////////////////////////////////////////////////////////////

package flex2.compiler.mxml.dom;

import flex2.compiler.util.NameMappings;
import flex2.compiler.util.QName;
import flex2.compiler.mxml.dom.DesignLayerNode;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.List;

/**
 * @author Clement Wong
 */
public class DocumentNode extends Node
{
	private String outerDocumentClassName;
    private String languageNamespace;
    private int version;

	/**
	 * Qualified node mappings to classes locally defined for the document.
	 */
	private NameMappings localClassMappings;
	
	/**
	 * Collection of declared DesignLayer instances that wouldn't otherwise
	 * be associated with any layer children. These eventually will become
	 * top level declarations.
	 */
	public List<DesignLayerNode> layerDeclarationNodes = 
		new ArrayList<DesignLayerNode>();

	public static final Set<QName> attributes;

	static
	{
		attributes = new HashSet<QName>();
	}

	public DocumentNode(String uri, String localName)
	{
		this(uri, localName, 0);
	}

	public DocumentNode(String uri, String localName, int size)
	{
		super(uri, localName, size);
	}

	public String getOuterDocumentClassName()
	{
		return outerDocumentClassName;
	}

	public boolean isInlineComponent()
	{
		return outerDocumentClassName != null;
	}

	public static DocumentNode inlineDocumentNode(String uri, String localName, String outerDocumentClassName)
	{
		DocumentNode node = new DocumentNode(uri, localName);
		node.setOuterDocumentClassName(outerDocumentClassName);
		return node;
	}

	private void setOuterDocumentClassName(String outerDocumentClassName)
	{
		this.outerDocumentClassName = outerDocumentClassName;
	}

    /**
     * Looks to see whether the Source has a local class mapping for a
     * qualified tag name.
     *
     * @param namespace The tag namespace URI.
     * @param localPart The tag name.
     * @return The Class name, or null if a mapping was not found.
     */
    public String getLocalClass(String namespace, String localPart)
    {
        if (localClassMappings != null)
        {
            return localClassMappings.lookupClassName(namespace, localPart);
        }

        return null;
    }

    /**
     * Maps a qualified tag name to a Class name for the local Source.
     * 
     * @param namespace The tag namespace URI.
     * @param localPart The tag name.
     * @param className The name of the local Class.
     */
    public void addLocalClass(String namespace, String localPart, String className)
    {
        if (localClassMappings == null)
            localClassMappings = new NameMappings();

        localClassMappings.addClass(namespace, localPart, className);
    }

    /**
     * @return The collection of local class mappings for this DocumentNode.
     */
    public NameMappings getLocalClassMappings()
    {
        return localClassMappings;
    }

    /**
     * Update the collection of local class mappings for this document node.
     * This can be useful for the creation of synthetic DocumentNodes for
     * inline components and Library Definitions.
     * 
     * @param mappings The local class mappings for this DocumentNode
     */
    public void setLocalClassMappings(NameMappings mappings)
    {
        localClassMappings = mappings;
    }

    /**
     * The language namespace for this document.
     *
     * @return String the URI representing the language namespace.
     */
    public String getLanguageNamespace()
    {
        return languageNamespace;
    }

    /**
     * Updates the language namespace for this document.
     *
     * @param namespace
     */
    public void setLanguageNamespace(String namespace)
    {
        languageNamespace = namespace;
    }

    /**
     * Reports the MXML language version based on the language namespace
     * found on the document Node. This version instructs the compiler
     * as to which rules are in effect and is an integer to allow for relative
     * comparison.
     */
    public int getVersion()
    {
        return version;
    }

    /**
     * Sets the MXML language version.
     */
    public void setVersion(int version)
    {
        this.version = version;
    }
}
