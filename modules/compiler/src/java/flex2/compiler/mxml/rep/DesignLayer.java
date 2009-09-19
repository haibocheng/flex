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

package flex2.compiler.mxml.rep;

import flex2.compiler.mxml.reflect.Type;

/**
 * This class represents a DesignLayer instance.
 */
public class DesignLayer extends Model
{
    public DesignLayer(MxmlDocument document, Type type, int line)
    {
        super(document, type, line);
    }
    
    public DesignLayer(MxmlDocument document, Type type, DesignLayer parent, int line)
    {
        super(document, type, (Model) parent, line);
    }
}
