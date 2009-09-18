////////////////////////////////////////////////////////////////////////////////
//
//  ADOBE SYSTEMS INCORPORATED
//  Copyright 2003-2007 Adobe Systems Incorporated
//  All Rights Reserved.
//
//  NOTICE: Adobe permits you to use, modify, and distribute this file
//  in accordance with the terms of the license agreement accompanying it.
//
////////////////////////////////////////////////////////////////////////////////
package flex.webtier.services.config;

import flex.webtier.util.ChainedException;

public class InvalidConfigurationException extends ChainedException
{
    static final long serialVersionUID = 6177484126610959258L;

    public InvalidConfigurationException(Throwable cause)
    {
        super(cause);
    }

    public InvalidConfigurationException(String message, Throwable cause)
    {
        super(message, cause);
    }

    public InvalidConfigurationException(String message)
    {
        super(message);
    }
}
