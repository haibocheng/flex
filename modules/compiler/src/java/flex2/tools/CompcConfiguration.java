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

package flex2.tools;

import flex2.compiler.common.Configuration;
import flex2.compiler.common.ConfigurationPathResolver;
import flex2.compiler.config.ConfigurationBuffer;
import flex2.compiler.config.ConfigurationException;
import flex2.compiler.config.ConfigurationInfo;
import flex2.compiler.config.ConfigurationValue;
import flex2.compiler.config.FileConfigurator;
import flex2.compiler.io.VirtualFile;
import flex2.compiler.util.ThreadLocalToolkit;
import flash.util.FileUtils;

import java.io.File;
import java.io.PrintWriter;
import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * compc specific configuration.
 *
 * @author Brian Deitte
 */
public class CompcConfiguration extends ToolsConfiguration
{
    // Hack to get around not being able to override the static method
    // getOutputInfo() in subclasses.
    protected static boolean outputRequired = true;

	public static Map<String, String> getAliases()
    {
        // FIXME: would make more sense to have these as part of ConfigurationInfo
        Map<String, String> map = new HashMap<String, String>();
        map.put( "o", "output" );
        map.put( "ic", "include-classes" );
        map.put( "in", "include-namespaces" );
        map.put( "is", "include-sources" );
        map.put( "if", "include-file" );
		map.put( "ir", "include-resource-bundles" );
	    map.putAll(Configuration.getAliases());
		return map;
    }

    public void validate( ConfigurationBuffer cfgbuf ) throws ConfigurationException
    {
    	super.validate( cfgbuf );
    	
        validateSwcInputs();
    }

    protected void validateSwcInputs() throws ConfigurationException
    {
        if (getIncludeSources().isEmpty() && 
            getClasses().isEmpty() &&
            getNamespaces().isEmpty() &&
            ((getCompilerConfiguration().getIncludeLibraries() == null) || 
             (getCompilerConfiguration().getIncludeLibraries().length == 0)) &&
            getFiles().isEmpty() && 
            getIncludeResourceBundles().isEmpty())
        {
            throw new ConfigurationException.NoSwcInputs( null, null, -1 );
        }
    }

    //
    // 'directory' option
    //
    
    private boolean isDirectory;

    public boolean isDirectory()
    {
        return isDirectory;
    }

    public void cfgDirectory(ConfigurationValue val, boolean directory)
    {
        this.isDirectory = directory;
    }

	//
	// 'help' option
	//
	
    // dummy, just a trigger for help text
    public void cfgHelp(ConfigurationValue cv, String[] keywords)
    {
        // intercepted upstream in order to allow help text to be printed even when args are otherwise bad
    }
    
    public static ConfigurationInfo getHelpInfo()
    {
        return new ConfigurationInfo( -1, "keyword" )
        {
            public boolean isGreedy()
			{
				return true;
			}

            public boolean isDisplayed()
			{
				return false;
			}
        };
    }

    //
	// 'include-classes' option
	//
	
    private List<String> classes = new LinkedList<String>();

    public List<String> getClasses()
    {
        return classes;
    }

    public void cfgIncludeClasses(ConfigurationValue cv, List<String> args) throws ConfigurationException
    {
        classes.addAll( toQNameString(args) );
    }

    public static ConfigurationInfo getIncludeClassesInfo()
    {
        return new ConfigurationInfo( -1, new String[] { "class" } )
        {
            public boolean allowMultiple()
            {
                return true;
            }
        };
    }

	//
	// 'include-file' option
	//
	
    private Map<String, VirtualFile> files = new HashMap<String, VirtualFile>();

    public Map<String, VirtualFile> getFiles()
    {
        return files;
    }

    public void addFiles(Map<String, VirtualFile> f)
    {
        files.putAll(f);
    }

    public void cfgIncludeFile( ConfigurationValue cfgval, String name, String path)
            throws ConfigurationException
    {
        if (files.containsKey(name))
        {
            throw new ConfigurationException.RedundantFile(name, cfgval.getVar(), cfgval.getSource(), cfgval.getLine() );
        }
        VirtualFile f = ConfigurationPathResolver.getVirtualFile( path, configResolver, cfgval );
        files.put(name, f);
    }

    public static ConfigurationInfo getIncludeFileInfo()
    {
        return new ConfigurationInfo( new String[] {"name", "path"} )
        {
            public boolean isPath()
            {
                return true;
            }

            public boolean allowMultiple()
			{
				return true;
			}
        };
    }

	//
	// 'include-stylesheet' option
	//
	
    private Map<String, VirtualFile> stylesheets = new HashMap<String, VirtualFile>();

    public Map<String, VirtualFile> getStylesheets()
    {
        return stylesheets;
    }

    public void addStylesheets(Map<String, VirtualFile> f)
    {
        stylesheets.putAll(f);
    }

    public void cfgIncludeStylesheet( ConfigurationValue cfgval, String name, String path)
            throws ConfigurationException
    {
        if (stylesheets.containsKey(name))
        {
            throw new ConfigurationException.RedundantFile(name, cfgval.getVar(), cfgval.getSource(), cfgval.getLine() );
        }
        VirtualFile f = ConfigurationPathResolver.getVirtualFile( path, configResolver, cfgval );
        stylesheets.put(name, f);
    }

    public static ConfigurationInfo getIncludeStylesheetInfo()
    {
        return new ConfigurationInfo( new String[] {"name", "path"} )
        {
            public boolean isPath()
            {
                return true;
            }

            public boolean allowMultiple()
			{
				return true;
			}
        };
    }

	//
	// 'include-lookup-only' option
	//
	
	private boolean includeLookupOnly = false;

	public boolean getIncludeLookupOnly()
	{
		return includeLookupOnly;
	}

	/**
	 * include-lookup-only (hidden)
	 * if true, manifest entries with lookupOnly=true are included in SWC catalog. default is false.
	 * This exists only so that manifests can mention classes that come in from filespec rather than classpath,
	 * e.g. in playerglobal.swc.
	 * TODO could make this a per-namespace setting. Or, if we modify catalog-builder to handle defs from filespecs,
	 * could remove it entirely.
	 */
	public void cfgIncludeLookupOnly(ConfigurationValue val, boolean includeLookupOnly)
	{
		this.includeLookupOnly = includeLookupOnly;
	}

	public static ConfigurationInfo getIncludeLookupOnlyInfo()
	{
		return new ConfigurationInfo()
		{
			public boolean isAdvanced()
			{
				return true;
			}
		};
	}

	//
    // 'include-namespaces' option
    //
    
    private List<String> namespaces = new LinkedList<String>();

    public List<String> getNamespaces()
    {
        return namespaces;
    }

    public void cfgIncludeNamespaces(ConfigurationValue val, List<String> includeNamespaces)
    {
        namespaces.addAll(includeNamespaces);
    }

    public static ConfigurationInfo getIncludeNamespacesInfo()
    {
        return new ConfigurationInfo( -1, new String[] { "uri" } )
        {
            public boolean allowMultiple()
            {
                return true;
            }
        };
    }

    //
    // 'include-resource-bundles' option
    //
    
	private List<String> resourceBundles = new LinkedList<String>();

    public List<String> getIncludeResourceBundles()
	{
		return resourceBundles;
	}

	public void cfgIncludeResourceBundles(ConfigurationValue val, List<String> includeResourceBundles)
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
	// 'include-sources' option
	//
	
    private List<String> sources = new LinkedList<String>();
    
    public void setIncludeSources(List<String> sources) 
    {
        this.sources = sources;
    }
 
    public List<String> getIncludeSources()
    {
        return sources;
    }

    public void cfgIncludeSources(ConfigurationValue cv, List<String> args) throws ConfigurationException
    {
        sources.addAll( args );
    }

    public static ConfigurationInfo getIncludeSourcesInfo()
    {
        return new ConfigurationInfo( -1, new String[] { "path-element" } )
        {
            public boolean allowMultiple()
            {
                return true;
            }

            public boolean isPath()
            {
                return true;
            }
        };
    }

	//
	// 'load-config' option
	//
	
    // dummy, ignored - pulled out of the buffer
    public void cfgLoadConfig(ConfigurationValue cv, String filename) throws ConfigurationException
    {
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
	// 'output' option
	//
	
    public void cfgOutput(ConfigurationValue val, String output) throws ConfigurationException
    {
		if (output != null && (output.startsWith(File.separator) || output.startsWith("/") || FileUtils.isAbsolute(new File(output))))
		{
			this.output = output;
		}
		else if (val.getContext() != null)
        {
            this.output = FileUtils.addPathComponents( val.getContext(), output, File.separatorChar );
        }
        else
        {
            this.output = output;
        }

        if (isDirectory)
        {
            File d = new File( this.output );

            if (d.exists())
            {
                if (!d.isDirectory())
                    throw new ConfigurationException.NotADirectory( this.output, val.getVar(), val.getSource(), val.getLine() );
                else
                {
                    File[] fl = d.listFiles();
                    if ((fl != null) && (fl.length > 0))
                    {
                        throw new ConfigurationException.DirectoryNotEmpty(this.output, val.getVar(), val.getSource(), val.getLine() );
                    }
                }
            }
        }
    }

    public static ConfigurationInfo getOutputInfo()
    {
        return new ConfigurationInfo(1, "filename")
        {
            public String[] getPrerequisites()
            {
                return new String[] {"directory"} ;
            }

            public boolean isRequired()
            {
                return outputRequired;
            }
        };
    }

    public String getTargetFile()
    {
        return null;
    }

    //
    // 'root' option
    //
     
	public void cfgRoot(ConfigurationValue val, String rootStr)
            throws ConfigurationException
    {
        throw new ConfigurationException.ObsoleteVariable( "source-path", val.getVar(),
                                                            val.getSource(), val.getLine() );
    }

    public static ConfigurationInfo getRootInfo()
    {
        return new ConfigurationInfo()
        {
            public boolean isAdvanced()
            {
                return true;
            }

            public boolean isHidden()
            {
                return true;
            }
        };
    }

	//
	// 'version' option
	//
	
    // dummy, just a trigger for version info
    public void cfgVersion(ConfigurationValue cv, boolean dummy)
    {
        // intercepted upstream in order to allow version into to be printed even when required args are missing
    }

    
	//
	// compute-digest option
	//
	
	private boolean computeDigest = true;
	
	public boolean getComputeDigest()
	{
		return computeDigest;
	}
	
	/**
	 * compute-digest option
	 * 
	 * @param cv
	 * @param b
	 */
	public void cfgComputeDigest(ConfigurationValue cv, boolean b)
	{
		computeDigest = b;
	}
}
