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
 * This class represents a Reparent instance.
 */
public class Reparent extends Model
{
    public Reparent(MxmlDocument document, Type type, int line)
    {
        super(document, type, line);
    }
    
    public Reparent(MxmlDocument document, Type type, Model parent, int line)
    {
        super(document, type, parent, line);
    }

    public Reparent(MxmlDocument document, Type type, Model parent, String target, int line)
    {
        super(document, type, parent, line);
        setId(target, true);
        setStateSpecific(true);
    }

    public Object getTarget()
    {
        return getId();
    }

    public void setTarget(String value)
    {
        setId(value,true);
    }
}
