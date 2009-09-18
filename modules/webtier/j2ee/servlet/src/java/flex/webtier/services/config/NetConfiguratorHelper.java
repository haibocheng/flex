////////////////////////////////////////////////////////////////////////////////
//
//  ADOBE SYSTEMS INCORPORATED
//  Copyright 2004-2007 Adobe Systems Incorporated
//  All Rights Reserved.
//
//  NOTICE: Adobe permits you to use, modify, and distribute this file
//  in accordance with the terms of the license agreement accompanying it.
//
////////////////////////////////////////////////////////////////////////////////
package flex.webtier.services.config;

public class NetConfiguratorHelper implements ConfiguratorHelper
{
    private static String SCHEMA_FILE_NAME = "flex-net-config.xsd";

    public String getSchemaFileName()
    {
        return SCHEMA_FILE_NAME;
    }

    public boolean isSupportedElement(String element)
    {
        boolean supported = true;
        
        // the following elements are not supported in the .NET version
        // these should have been caught by a validating parser
        if (element.equals("console"))
        {
            supported = false;
        }
        else if (element.equals("content-size"))
        {
            supported = false;
        }
        else if (element.equals("mxml-size"))                    
        {
            supported = false;
        }
        else if (element.equals("file-watcher-interval"))
        {
            supported = false;
        }

        return supported;        
    }
}
