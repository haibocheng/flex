////////////////////////////////////////////////////////////////////////////////
//
//  ADOBE SYSTEMS INCORPORATED
//  Copyright 2009 Adobe Systems Incorporated
//  All Rights Reserved.
//
//  NOTICE: Adobe permits you to use, modify, and distribute this file
//  in accordance with the terms of the license agreement accompanying it.
//
////////////////////////////////////////////////////////////////////////////////
package flex2.compiler.css;

import flex2.compiler.util.CompilerMessage.CompilerWarning;

public class UnresolvedQualifiedTypeSelector extends CompilerWarning
{
    private static final long serialVersionUID = 459989935353560899L;
    public String selector;
    public String type;

    public UnresolvedQualifiedTypeSelector(String path, int line, String type, String selector)
    {
        this.path = path;
        this.line = line;
        this.type = type;
        this.selector = selector;
    }

}
