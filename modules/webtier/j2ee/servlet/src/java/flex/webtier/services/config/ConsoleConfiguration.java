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
package flex.webtier.services.config;

import flex2.compiler.config.ConfigurationValue;

public class ConsoleConfiguration
{
    private boolean enable;

    public void cfgEnable(ConfigurationValue cfgval, boolean enable)
    {
        this.enable = enable;
    }

    public void setEnable(boolean enable)
    {
        this.enable = enable;
    }

    public boolean isEnable()
    {
        return enable;
    }
}
