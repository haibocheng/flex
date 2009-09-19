////////////////////////////////////////////////////////////////////////////////
//
//  ADOBE SYSTEMS INCORPORATED
//  Copyright 2005-2007 Adobe Systems Incorporated
//  All Rights Reserved.
//
//  NOTICE: Adobe permits you to use, modify, and distribute this file
//  in accordance with the terms of the license agreement accompanying it.
//
////////////////////////////////////////////////////////////////////////////////
package flex2.compiler.mxml.reflect;

/**
 * Base interface used to mark state-specific model properties.
 */
public interface Stateful
{
    public void setStateName(String name);
    
    public String getStateName();
    
    public boolean isStateSpecific();
}
