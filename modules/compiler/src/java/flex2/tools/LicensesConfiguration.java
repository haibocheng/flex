////////////////////////////////////////////////////////////////////////////////
//
//  ADOBE SYSTEMS INCORPORATED
//  Copyright 2006-2007 Adobe Systems Incorporated
//  All Rights Reserved.
//
//  NOTICE: Adobe permits you to use, modify, and distribute this file
//  in accordance with the terms of the license agreement accompanying it.
//
////////////////////////////////////////////////////////////////////////////////

package flex2.tools;

import flex2.compiler.config.ConfigurationException;
import flex2.compiler.config.ConfigurationValue;
import flex2.compiler.config.ConfigurationInfo;
import java.util.HashMap;
import java.util.Map;

/**
 * A sub-configuration of ToolsConfiguration.
 *
 * @see flex2.tools.ToolsConfiguration
 * @author Paul Reilly
 */
public class LicensesConfiguration
{
    //
	//  'license' option
	//
	
	private Map<String, String> licenseMap;

    public Map<String, String> getLicenseMap()
    {
        return licenseMap;
    }
    
   public void setLicenseMap(Map<String, String> m)
    {
    	licenseMap = m;
    }
    
    public void cfgLicense( ConfigurationValue cfgval, String product, String serialNumber)
        throws ConfigurationException
    {
        if (licenseMap == null)
        {
            licenseMap = new HashMap<String, String>();
        }

        licenseMap.put(product, serialNumber);
    }

    public static ConfigurationInfo getLicenseInfo()
    {
        return new ConfigurationInfo( new String[] {"product", "serial-number"} )
        {
            public boolean allowMultiple()
            {
                return true;
            }
            
            public boolean isDisplayed()
            {
            	return false;
            }
        };
    }
}
