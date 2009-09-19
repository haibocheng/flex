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

package flex2.tools.oem.internal;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import flash.util.FileUtils;
import flex2.compiler.CompilerAPI;
import flex2.compiler.common.Configuration;
import flex2.compiler.common.ConfigurationPathResolver;
import flex2.compiler.config.ConfigurationBuffer;
import flex2.compiler.config.ConfigurationException;
import flex2.compiler.config.ConfigurationInfo;
import flex2.compiler.config.ConfigurationValue;
import flex2.compiler.i18n.I18nUtils;
import flex2.compiler.io.VirtualFile;
import flex2.tools.ToolsConfiguration;

/**
 * @version 2.0.1
 * @author Clement Wong
 */
public class ApplicationCompilerConfiguration extends ToolsConfiguration
{
	public static Map<String, String> getAliases()
    {
        Map<String, String> map = new HashMap<String, String>();
	    map.putAll(Configuration.getAliases());
	    map.remove("o");
		return map;
    }
	
	public void validate(ConfigurationBuffer cfgbuf) throws ConfigurationException
	{
        super.validate( cfgbuf );

        String targetFile = getTargetFile();

		if (targetFile == null)
		{
		    throw new ConfigurationException.MustSpecifyTarget( null, null, -1);
		}
	}

    private VirtualFile getVirtualFile(String file, ConfigurationValue cfgval)
    {
    	try
    	{
    		return ConfigurationPathResolver.getVirtualFile( file, configResolver, cfgval );
    	}
    	catch (ConfigurationException ex)
    	{
    		return null;
    	}
    }

	//
	// 'file-specs' option
	//

	// list of filespecs, default var for command line
	private List<String> fileSpecs = new ArrayList<String>();

	public List<String> getFileSpecs()
	{
		return fileSpecs;
	}

	public void cfgFileSpecs(ConfigurationValue cv, List<String> args) throws ConfigurationException
	{
		this.fileSpecs.addAll( args );
	}

    public static ConfigurationInfo getFileSpecsInfo()
    {
        return new ConfigurationInfo( -1, new String[] { "path-element" } )
        {
            public boolean allowMultiple()
            {
                return true;
            }

            public boolean isHidden()
            {
            	return true;
            }
        };
    }

    private String resourceModulePath;

    public String getOutput()
    {
        return getTargetFile();
    }

	public String getTargetFile()
	{
        // If there are no target files but there are included resource bundles then
        // this is a resource module so generate a target file name.
        if (fileSpecs.size() == 0 && getIncludeResourceBundles().size() > 0)
        {
            if (resourceModulePath == null)
            {
                resourceModulePath = I18nUtils.getGeneratedResourceModule(this).getPath();
            }
            
            return resourceModulePath;
        }
        
        return (fileSpecs.size() > 0) ? (String) fileSpecs.get( fileSpecs.size() - 1 ) : null;
	}

    //
	// 'generate-link-report' option
	//
	
	private boolean generateLinkReport;
	
	public boolean generateLinkReport()
	{
		return generateLinkReport;
	}
	
	public void keepLinkReport(boolean b)
	{
		generateLinkReport = b;
	}

    //
    // 'include-resource-bundles' option
    //
    
    private List resourceBundles = new LinkedList();

    public List getIncludeResourceBundles()
    {
        return resourceBundles;
    }

    public void cfgIncludeResourceBundles(ConfigurationValue val, List includeResourceBundles)
    {
        resourceBundles.addAll(toQNameString(includeResourceBundles));
    }

    public static ConfigurationInfo getIncludeResourceBundlesInfo()
    {
        return new ConfigurationInfo( -1, new String[] { "bundle" } )
        {
            public boolean allowMultiple()
            {
                return true;
            }
        };
    }
    
	//
	// 'load-config' option
	//

	private VirtualFile configFile;

	public VirtualFile getLoadConfig()
	{
		return configFile;
	}

	// dummy, ignored - pulled out of the buffer
	public void cfgLoadConfig(ConfigurationValue cv, String filename) throws ConfigurationException
	{
		// C: resolve the flex-config.xml path to a VirtualFile so incremental compilation can detect timestamp change.
		configFile = ConfigurationPathResolver.getVirtualFile(filename,
		                                                      configResolver,
		                                                      cv);
	}

	public static ConfigurationInfo getLoadConfigInfo()
    {
        return new ConfigurationInfo( 1, "filename" )
        {
            public boolean allowMultiple()
            {
                return true;
            }
        };
    }

    //
	// 'projector' option
	//
	
    private VirtualFile projector;

    public VirtualFile getProjector()
    {
        return projector;
    }

	public void cfgProjector( ConfigurationValue cfgval, String path )
	{
		projector = getVirtualFile(path, cfgval);
	}

    public static ConfigurationInfo getProjectorInfo()
    {
        return new ConfigurationInfo()
        {
            public boolean isHidden()
            {
                return true;
            }
        };
    }
}

