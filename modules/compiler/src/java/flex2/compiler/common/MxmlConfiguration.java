////////////////////////////////////////////////////////////////////////////////
//
//  ADOBE SYSTEMS INCORPORATED
//  Copyright 2007 Adobe Systems Incorporated
//  All Rights Reserved.
//
//  NOTICE: Adobe permits you to use, modify, and distribute this file
//  in accordance with the terms of the license agreement accompanying it.
//
////////////////////////////////////////////////////////////////////////////////

package flex2.compiler.common;

import flex2.compiler.config.AdvancedConfigurationInfo;
import flex2.compiler.config.ConfigurationException;
import flex2.compiler.config.ConfigurationValue;
import flex2.compiler.config.ConfigurationInfo;

/**
 * @author Clement Wong
 */
public class MxmlConfiguration
{
	private ConfigurationPathResolver configResolver;

	public void setConfigPathResolver( ConfigurationPathResolver resolver )
	{
	    this.configResolver = resolver;
	}

    //
    // 'compiler.mxml.compatibility-version' option
    //
	
    public static final int CURRENT_VERSION = 0x04000000;
    public static final int VERSION_4_0 = 0x04000000;
    public static final int VERSION_3_0 = 0x03000000;
    public static final int VERSION_2_0_1 = 0x02000001;
    public static final int VERSION_2_0 = 0x02000000;
    private static final int EARLIEST_MAJOR_VERSION = 2;
    private static final int LATEST_MAJOR_VERSION = 4;

	private int major = LATEST_MAJOR_VERSION;
	private int minor;
	private int revision;

	public int getMajorCompatibilityVersion()
	{
		return major;
	}

	public int getMinorCompatibilityVersion()
	{
		return minor;
	}

	public int getRevisionCompatibilityVersion()
	{
		return revision;
	}

	/*
	 * Unlike the framework's FlexVersion.compatibilityVersionString,
	 * this returns null rather than a string like "3.0.0" for the current version.
	 * But if a -compatibility-version was specified, this string will always
	 * be of the form N.N.N. For example, if -compatibility-version=2,
	 * this string is "2.0.0", not "2".
	 */
	public String getCompatibilityVersionString()
	{
		return (major == 0 && minor == 0 && revision == 0) ? null : major + "." + minor + "." + revision;
	}

	/*
	 * This returns an int that can be compared with version constants
	 * such as MxmlConfiguration.VERSION_3_0.
	 */
	public int getCompatibilityVersion()
	{
		int version = (major << 24) + (minor << 16) + revision;
		return version != 0 ? version : CURRENT_VERSION;
	}
	
	public void cfgCompatibilityVersion(ConfigurationValue cv, String version) throws ConfigurationException
	{
		if (version == null)
		{
			return;
		}
		
		String[] results = version.split("\\.");
		
		if (results.length == 0)
		{
			throw new ConfigurationException.BadVersion(version, "compatibility-version");

		}
		
		for (int i = 0; i < results.length; i++)
		{
			int versionNum = 0;
			
			try
			{
				versionNum = Integer.parseInt(results[i]);
			}
			catch (NumberFormatException e)
			{
				throw new ConfigurationException.BadVersion(version, "compatibility-version");				
			}
			
			if (i == 0)
			{
				if (versionNum >= EARLIEST_MAJOR_VERSION && versionNum <= LATEST_MAJOR_VERSION) 
				{
					this.major = versionNum;
				}
				else 
				{
					throw new ConfigurationException.BadVersion(version, "compatibility-version");
				}				
			}
			else 
			{
				if (versionNum >= 0) 
				{
					if (i == 1)
					{
						this.minor = versionNum;						
					}
					else
					{
						this.revision = versionNum;
					}
				}
				else 
				{
					throw new ConfigurationException.BadVersion(version, "compatibility-version");
				}				
			}
		}

		if (major <= 3)
		    qualifiedTypeSelectors = false;
	}

	public static ConfigurationInfo getCompatibilityVersionInfo()
	{
	    return new ConfigurationInfo( new String[] {"version"} );
	}

    //
    // 'qualified-type-selectors' option
    //

    private boolean qualifiedTypeSelectors = true;

    public boolean getQualifiedTypeSelectors()
    {
        if (getCompatibilityVersion() < MxmlConfiguration.VERSION_4_0)
            return false;

        return qualifiedTypeSelectors;
    }

    public void cfgQualifiedTypeSelectors(ConfigurationValue cv, boolean b)
    {
        qualifiedTypeSelectors = b;
    }

    public static ConfigurationInfo getQualifiedTypeSelectorsInfo()
    {
        return new AdvancedConfigurationInfo();
    }
}
