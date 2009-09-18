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

public class JavaConfiguratorHelper implements ConfiguratorHelper
{
    private static String SCHEMA_FILE_NAME = "flex-config.xsd";

    public String getSchemaFileName()
    {
        return SCHEMA_FILE_NAME;
    }

    public boolean isSupportedElement(String element)
    {
        boolean supported = true;
        
        // the following elements are not supported in the Java version
        // these should have been caught by a validating parser
        if (element.equals("event"))
        {
            supported = false;
        }
        else if (element.equals("absolute-expiration-minutes"))
        {
            supported = false;
        }
        else if (element.equals("sliding-expiration-minutes"))
        {
            supported = false;
        }                       
        
        return supported;
    }
}
