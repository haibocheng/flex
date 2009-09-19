////////////////////////////////////////////////////////////////////////////////
//
//  ADOBE SYSTEMS INCORPORATED
//  Copyright 2007-2009 Adobe Systems Incorporated
//  All Rights Reserved.
//
//  NOTICE: Adobe permits you to use, modify, and distribute this file
//  in accordance with the terms of the license agreement accompanying it.
//
////////////////////////////////////////////////////////////////////////////////

package flex2.tools.oem;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import macromedia.asc.util.ContextStatics;

import flash.localization.LocalizationManager;
import flash.swf.Frame;
import flash.swf.Movie;
import flash.swf.MovieDecoder;
import flash.swf.MovieEncoder;
import flash.swf.TagDecoder;
import flash.swf.TagEncoder;
import flash.swf.tags.DefineTag;
import flex2.compiler.CompilerSwcContext;
import flex2.compiler.io.LocalFile;
import flex2.compiler.io.VirtualFile;
import flex2.compiler.swc.SwcCache;
import flex2.compiler.util.MimeMappings;
import flex2.compiler.util.NameMappings;
import flex2.compiler.util.OrderedProperties;
import flex2.compiler.util.QName;
import flex2.compiler.util.SwcDependencyInfo;
import flex2.compiler.util.SwcDependencyUtil;
import flex2.compiler.util.SwcExternalScriptInfo;
import flex2.compiler.util.ThreadLocalToolkit;
import flex2.compiler.util.Benchmark.MemoryUsage;
import flex2.compiler.util.graph.Vertex;
import flex2.tools.oem.OEMException.CircularLibraryDependencyException;
import flex2.tools.oem.internal.OEMUtil;

/**
 * 
 * @author Clement Wong
 * @version 3.0
 */
public class Toolkit
{
	/**
	 * 
	 * @param application
	 * @return
	 */
	public static ApplicationInfo getApplicationInfo(File application)
	{
		InputStream in = null;
		ApplicationInfo info = null;
		
		try
		{
			in = new BufferedInputStream(new FileInputStream(application));
			
			Movie movie = new Movie();
			new TagDecoder(in).parse(new MovieDecoder(movie));
			
			info = new ApplicationInfoImpl(movie);
		}
		catch (IOException ex)
		{
			ex.printStackTrace();
		}
		finally
		{
			try { if (in != null) in.close(); } catch (IOException ex) {}
		}

		return info;
	}

	/**
	 * 
	 * @param library
	 * @return
	 */
	public static LibraryInfo getLibraryInfo(File library)
	{
		return getLibraryInfo(new File[] { library });
	}
	
	/**
	 * 
	 * @param libraries
	 * @return
	 */
	public static LibraryInfo getLibraryInfo(File[] libraries)
	{
		return getLibraryInfo(libraries, false);
	}

	/**
	 * 
	 * @param libraries
	 * @param includeBytecodes
	 * @return
	 */
	public static LibraryInfo getLibraryInfo(File[] libraries, boolean includeBytecodes)
	{
		LibraryInfo info = null;

        try
        {
        	OEMUtil.init(OEMUtil.getLogger(null, new ArrayList<Message>()), new MimeMappings(), null, null, null);
        	
            CompilerSwcContext swcContext = new CompilerSwcContext();
            SwcCache cache = new SwcCache();

            swcContext.load(toVirtualFiles(libraries),
	        				new NameMappings(),
	        				".properties",
	        				cache);
            
            info = new LibraryInfoImpl(swcContext, includeBytecodes);
            
            swcContext.close();
        }
        catch (Throwable t)
        {
        	t.printStackTrace();
        }
        finally
        {
        	OEMUtil.clean();
        }

		return info;
	}
	
	/**
	 * Converts a list of File(s) into a list of VirtualFile(s).
	 * The VirtualFile implementation is flex2.compiler.io.LocalFile.
	 * 
	 * @param files
	 * @return
	 */
	private static VirtualFile[] toVirtualFiles(File[] files)
	{
		if (files == null) return null;
		
		List<VirtualFile> vFiles = new ArrayList<VirtualFile>(files.length);
		for (int i = 0; i < files.length; i++)
		{
		    if (files[i] != null)
		        vFiles.add(new LocalFile(files[i]));
		}
		
		return vFiles.toArray(new VirtualFile[vFiles.size()]);
	}

	/**
	 * Creates a <code>java.util.Properties</code> object from an <code>UTF-8</code> encoded input stream.
	 * 
	 * @param in <code>java.io.InputStream</code>
	 * @return an instance of <code>java.util.Properties</code>;
	 * 						  <code>null</code> if <code>IOException</code> occurs.
	 */
	public static Properties loadProperties(InputStream in)
	{
		return loadProperties(in, "UTF-8");
	}
	
	/**
	 * Creates a <code>java.util.Properties</code> object from an <code>UTF-8</code> encoded .properties file.
	 * 
	 * @param f an <code>UTF-8</code> encoded .properties file
	 * @return an instance of <code>java.util.Properties</code>;
	 * 						  <code>null</code> if the file doesn't exist or if <code>IOException</code> occurs.
	 */
	public static Properties loadProperties(File f)
	{
		return loadProperties(f, "UTF-8");
	}
	
	/**
	 * Creates a <code>java.util.Properties</code> object from an <code>UTF-8</code> encoded .properties file.
	 * 
	 * @param f an <code>UTF-8</code> encoded .properties file
	 * @param encoding character encoding
	 * @return an instance of <code>java.util.Properties</code>;
	 * 						  <code>null</code> if the file doesn't exist or if <code>IOException</code> occurs.
	 */
	public static Properties loadProperties(File f, String encoding)
	{
		if (f != null && f.isFile())
		{
			try
			{
				return loadProperties(new FileInputStream(f), encoding);
			}
			catch (IOException ex)
			{
				return null;
			}
		}
		else
		{
			return null;
		}
	}
	
	private static Properties loadProperties(InputStream in, String encoding)
	{
		if (in != null)
		{
			try
			{
				OrderedProperties p = new OrderedProperties();
				p.load(new BufferedReader(new InputStreamReader(in, encoding)));
				return p;
			}
			catch (IOException ex)
			{
				return null;
			}
		}
		else
		{
			return null;
		}
	}
	
	/**
	 * Optimizes a SWF. This operation performs the following:
	 * 
	 * <pre>
	 * 1. remove debug tags and opcodes
	 * 2. merge abc bytecodes
	 * 3. peephole optimization
	 * 4. remove unwanted metadata
	 * </pre>
	 * 
	 * @param in a SWF input stream
	 * @param out a SWF output stream
	 * @return the number of bytes written to the output stream; <code>0</code> if the optimization fails.
	 */
	public static long optimize(InputStream in, OutputStream out)
	{
		try
		{
			return flex2.tools.WebTierAPI.optimize(in, out);
		}
		catch (IOException ex)
		{
			return 0;
		}
	}
	
	/**
	 * Optimizes the library SWF. This operation performs the following:
	 * 
	 * <pre>
	 * 1. remove debug tags and opcodes
	 * 2. merge abc bytecodes
	 * 3. peephole optimization
	 * 4. remove unwanted metadata, but preserve the metadata specified in the Library object
	 * </pre>
	 * 
	 * This operation returns an optimized version of the library SWF. The SWF in the library
	 * remains unchanged.
	 * 
	 * @param in a SWF input stream
	 * @param out a SWF output stream
	 * @return the number of bytes written to the output stream; <code>0</code> if the optimization fails.
	 */
	public static long optimize(Library lib, OutputStream out)
	{
		if (lib == null || lib.data == null || lib.data.movie == null) return 0;
		
		try
		{
			TagEncoder handler = new TagEncoder();
			MovieEncoder encoder = new MovieEncoder(handler);
			encoder.export(lib.data.movie);
            
            //TODO PERFORMANCE: A lot of unnecessary recopying here
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			handler.writeTo(baos);

			return flex2.tools.WebTierAPI.optimize(new ByteArrayInputStream(baos.toByteArray()),
											out,
											lib.data.configuration);
		}
		catch (IOException ex)
		{
			return 0;
		}
	}
	
	/**
	 * 
	 *
	 */
	public static void printMemoryUsage()
	{
	    MemoryUsage mem = new flex2.compiler.util.Benchmark().getMemoryUsageInBytes();
	    long mbHeapUsed = (mem.heap / 1048576);
		long mbNonHeapUsed = (mem.nonHeap / 1048576);
		System.out.println("Heap: " + mbHeapUsed + " Non-Heap: " + mbNonHeapUsed);
	}

	// added for FB code model
    /**
     * Returns a list filled with namespaces that should be automatically
     * opened, based on the current target player, e.g. flash10, AS3.
     * 
     * @param targetPlayerMajorVersion E.g. 9, 10, ...
     * @return List<String> containing the namespaces
     */
    public static List<String> getRequiredUseNamespaces(int targetPlayerMajorVersion)
    {
        return ContextStatics.getRequiredUseNamespaces(targetPlayerMajorVersion);
    }
    

    
    /**
     * Get the dependency order of a given set of libraries.
     * 
     * @param libraries The set of libraries to find the dependency information for. Each
     * File in the list must be a library file or a directory of libraries files.
     * 
     * @return An ordered list of library dependencies. Each String in the 
     * list is the location of a library in the file system. The first library in the list has no
     * dependencies. Each library in the list has at least the same dependencies as its 
     * predecessor and may be dependent on its predecessor as well. 
     */
    public static List<String> getDependencyOrder(File[] libraries) throws CircularLibraryDependencyException
    {
        if (libraries == null)
            return Collections.emptyList();
        
        SwcDependencyInfo info = SwcDependencyUtil.getSwcDependencyInfo(toVirtualFiles(libraries), 
                                                                        null,
                                                                        true);
        Set<Vertex<String, SwcExternalScriptInfo>> cycles = info.detectCycles();
        if (cycles.size() > 0) 
        {
            LocalizationManager i10n = ThreadLocalToolkit.getLocalizationManager();
            if (i10n == null)
            {
                OEMUtil.setupLocalizationManager();
                i10n = ThreadLocalToolkit.getLocalizationManager();
            }
            
            String message = i10n.getLocalizedTextString(new CircularLibraryDependencyException(null, null));
            throw new CircularLibraryDependencyException(message, 
                            SwcDependencyUtil.SetOfVertexToString(cycles));
        }
        
        return info.getSwcDependencyOrder();
    }

    
    /**
     * Get the set of library dependencies of a given library.
     * 
     * @param libraries The set of libraries need to resolve all the dependencies of the targetLibrary. Each
     * File in the list must be a library file or a directory of libraries files.
     * @param targetLibrary The libraries to find dependencies for.
     * @param minimizeDependencySet If false, all of the libraries dependencies are returned. If true, the external script
     * classes are reviewed. If the set of script classes resolved in a libraryA is a subset of the script
     * classes resolved in libraryB, then libraryA will be removed as a dependency of targetLibrary.
     * @return A set of Strings; where each String is the location of a library in the file system. 
     */
    public static Set<String> getLibraryDependencies(File[] libraries, 
            File targetLibrary, 
            boolean minimizeDependencySet) throws CircularLibraryDependencyException 
    {
        if (libraries == null || targetLibrary == null)
            return Collections.emptySet();
        
        SwcDependencyInfo info = SwcDependencyUtil.getSwcDependencyInfo(toVirtualFiles(libraries), 
                                                                        null,
                                                                        minimizeDependencySet);
        Set<Vertex<String, SwcExternalScriptInfo>> cycles = info.detectCycles();
        if (cycles.size() > 0) 
        {
            LocalizationManager i10n = ThreadLocalToolkit.getLocalizationManager();
            if (i10n == null)
            {
                OEMUtil.setupLocalizationManager();
                i10n = ThreadLocalToolkit.getLocalizationManager();
            }
            
            String message = i10n.getLocalizedTextString(new CircularLibraryDependencyException(null, null));
            throw new CircularLibraryDependencyException(message, 
                            SwcDependencyUtil.SetOfVertexToString(cycles));
        }

        VirtualFile virtualLibrary = new LocalFile(targetLibrary);
        return info.getDependencies(virtualLibrary.getName());
    }
    
}


/**
 * 
 *
 */
class ApplicationInfoImpl implements ApplicationInfo
{
    ApplicationInfoImpl(Movie movie)
    {
        version = movie.version;
        
        List frames = movie.frames;
        Set<String> symbols = new TreeSet<String>();
        
        for (int i = 0, size = frames == null ? 0 : frames.size(); i < size; i++)
        {
            Frame f = (Frame) frames.get(i);
            for (Iterator j = f.exportIterator(); j.hasNext(); )
            {
                DefineTag t = (DefineTag) j.next();
                if (t.name != null)
                {
                    symbols.add(t.name);
                }
            }
        }
        
        symbols.toArray(symbolNames = new String[symbols.size()]);
    }
    
    private String[] symbolNames;
    private int version;

    public String[] getSymbolNames()
    {
        return symbolNames;
    }
    
    public int getSWFVersion()
    {
        return version;
    }
}


/**
 * 
 *
 */
class LibraryInfoImpl implements LibraryInfo
{
    LibraryInfoImpl(CompilerSwcContext swcContext, boolean includeBytecodes)
    {
        List<QName> names = new ArrayList<QName>();
        
        for (Iterator i = swcContext.getDefinitionIterator(); i.hasNext(); )
        {
            names.add((QName) i.next());
        }
        
        definitionNames = new String[names.size()];
        
        for (int i = 0; i < definitionNames.length; i++)
        {
            definitionNames[i] = names.get(i).toString();
        }
        
        scripts = new TreeMap<String, Script>();
        
        for (int i = 0; i < definitionNames.length; i++)
        {
            QName def = names.get(i);
            Script s = swcContext.getScript(def, includeBytecodes);
            scripts.put(def.toString(), s);
        }
        
        components = new TreeMap<String, Component>();
        
        for (Iterator i = swcContext.getComponentIterator(); i.hasNext(); )
        {
            Component c = (Component) i.next();
            components.put(c.getClassName(), c);
        }
        
        mappings = swcContext.getNameMappings();
        
        fileNames = new TreeSet<String>(swcContext.getFiles().keySet());
    }
    
    private String[] definitionNames;
    private Map<String, Script> scripts;
    private Map<String, Component> components;
    private NameMappings mappings;
    private Set<String> fileNames;

    public Component getComponent(String namespaceURI, String name)
    {
        return getComponent(mappings.lookupClassName(namespaceURI, name));
    }

    public Component getComponent(String definition)
    {
        return (definition != null) ? components.get(definition) : null;
    }

    public Iterator<Component> getComponents()
    {
        return components.values().iterator();
    }

    public String[] getDefinitionNames()
    {
        return definitionNames;
    }

    public Script getScript(String definition)
    {
        return scripts.get(definition);
    }

    public Iterator<Script> getScripts()
    {
        return scripts.values().iterator();
    }
    
    public Iterator<String> getFiles()
    {
        return fileNames.iterator();
    }
}
