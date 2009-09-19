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

import flex2.compiler.mxml.dom.Analyzer;
import flex2.compiler.mxml.dom.Node;

import flex2.compiler.util.QName;

import java.util.Set;
import java.util.HashSet;

/**
 * <Definition> tag for a <Library>
 * Has one attribute 'name' as an identifier.
 * At most one child may be specified (enforced downstream).
 * 
 * @author Peter Farland
 */
public class DefinitionNode extends Node
{
    public static final String DEFINITION_NAME_ATTR = "name";
    
    public static final Set<QName> attributes;
    static
    {
        attributes = new HashSet<QName>();
        attributes.add(new QName("", DEFINITION_NAME_ATTR));
    }

    private QName name;

    public DefinitionNode(String uri, String localName, int size)
    {
        super(uri, localName, size);
    }

    public void analyze(Analyzer analyzer)
    {
        analyzer.prepare(this);
        analyzer.analyze(this);
    }

    public void setName(QName name)
    {
        assert this.name == null;
        this.name = name;
    }

    public QName getName()
    {
        assert this.name != null;
        return name;
    }
}
