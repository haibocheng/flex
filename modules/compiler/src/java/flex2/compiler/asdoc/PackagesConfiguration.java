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

package flex2.compiler.asdoc;

import flex2.compiler.config.ConfigurationValue;
import flex2.compiler.config.ConfigurationInfo;

import java.util.List;
import java.util.ArrayList;
import java.util.Set;
import java.util.HashSet;

/**
 * @author Brian Deitte
 */
public class PackagesConfiguration
{
	//
	// 'packages.package' option
	//
	
	private List<PackageInfo> packages = new ArrayList<PackageInfo>();
	private Set<String> packageNames = new HashSet<String>();

	public List<PackageInfo> getPackages()
	{
		return packages;
	}

	public Set<String> getPackageNames()
	{
		return packageNames;
	}
	
	public void cfgPackage(ConfigurationValue cfgval, String name, String desc)
	{
		packages.add(new PackageInfo(name, desc));
		packageNames.add(name);
	}

	public static ConfigurationInfo getPackageInfo()
	{
	    return new ConfigurationInfo( )
	    {
	        public boolean allowMultiple()
	        {
	            return true;
	        }
	    };
	}
}
