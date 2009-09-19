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
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import flash.swf.Frame;
import flex2.compiler.AssetInfo;
import flex2.compiler.CompilationUnit;
import flex2.compiler.Source;
import flex2.compiler.common.Configuration;
import flex2.compiler.io.VirtualFile;
import flex2.compiler.swc.SwcScript;
import flex2.compiler.util.Name;
import flex2.compiler.util.QName;
import flex2.compiler.util.QNameList;
import flex2.linker.SimpleMovie;
import flex2.tools.VersionInfo;
import flex2.tools.oem.Message;
import flex2.tools.oem.Report;

/**
 * 
 * @version 2.0.1
 * @author Clement Wong
 */
public class OEMReport implements Report
{
	public OEMReport(List<Source> sources, SimpleMovie movie, Configuration configuration,
					 String configurationReport, List<Message> messages)
	{
		init(sources, movie == null ? null : movie.getExportedUnits(),
			 configuration == null ? null : configuration.getResourceBundles());

		processFrames(movie);
		processMessages(messages);
		
		this.frameCount = movie == null ? 0 : movie.frames.size();
		this.configurationReport = configurationReport;
		
		defaultWidth = configuration != null ? configuration.defaultWidth() : 0;
		defaultHeight = configuration != null ? configuration.defaultHeight() : 0;

		width = 0;
		height = 0;
		widthPercent = 0.0;            
		heightPercent = 0.0;
        
        if (movie != null)
		{
			linkReport = movie.getLinkReport();
			bgColor = movie.bgcolor.color;
			pageTitle = movie.pageTitle;
			
			if (movie.userSpecifiedWidth)
			{
				width = movie.width;
			}
			else if (configuration != null)
            {
                String percent = configuration.widthPercent();
                if (percent != null)
                {
                    percent = percent.trim();
                    // Percent character is expected but allow it to be optional.
                    if (percent.length() >= 1 &&
                        percent.charAt(percent.length() - 1) == '%')
                    {
                        percent = percent.substring(0, percent.length() - 1);
                    }
                    try
                    {
                        widthPercent = Double.parseDouble(percent) / 100;
                    }
                    catch(NumberFormatException ex) {}
                }
            }

			if (movie.userSpecifiedHeight)
			{
				height = movie.height;
			}
			else if (configuration != null)
            {
                String percent = configuration.heightPercent();
                if (percent != null)
                {
                    percent = percent.trim();
                    // Percent character is expected but allow it to be optional.
                    if (percent.length() >= 1 &&
                        percent.charAt(percent.length() - 1) == '%')
                    {
                        percent = percent.substring(0, percent.length() - 1);
                    }
                    try
                    {
                        heightPercent = Double.parseDouble(percent) / 100;
                    }
                    catch(NumberFormatException ex) {}
                }
            }
		}
		else
		{
			linkReport = null;
			bgColor = 0;
			pageTitle = null;
		}
	}
	
	private void init(List<Source> sourceList, List<CompilationUnit> exportedUnits, Set<String> resourceBundles)
	{
		TreeSet<String> sources = new TreeSet<String>();
		TreeSet<String> assets = new TreeSet<String>();
		TreeSet<String> libraries = new TreeSet<String>();
		
		data = new HashMap<String, Data>();
		locations = new HashMap<String, String>();
		
		processSources(sourceList, sources, assets, libraries, data, locations);

		timestamps = new HashMap<String, Long>();
		
		compiler_SourceNames = toArray(sources);
		storeTimestamps(compiler_SourceNames);

		compiler_AssetNames = toArray(assets);
		storeTimestamps(compiler_AssetNames);

		compiler_LibraryNames = toArray(libraries);
		storeTimestamps(compiler_LibraryNames);
		
		resourceBundleNames = toArray(resourceBundles);
		
		sources.clear();
		assets.clear();
		libraries.clear();
		
		processCompilationUnits(exportedUnits, sources, assets, libraries);
			
		linker_SourceNames = toArray(sources);
		storeTimestamps(linker_SourceNames);

		linker_AssetNames = toArray(assets);
		storeTimestamps(linker_AssetNames);

		linker_LibraryNames = toArray(libraries);		
		storeTimestamps(linker_LibraryNames);
	}
	
	private void storeTimestamps(String[] a)
	{
		for (int i = 0, len = a == null ? 0 : a.length; i < len; i++)
		{
			if (!timestamps.containsKey(a[i]))
			{
				timestamps.put(a[i], new Long(new File(a[i]).lastModified()));
			}
		}
	}
	
	private String[] compiler_SourceNames, compiler_AssetNames, compiler_LibraryNames;
	private String[] linker_SourceNames, linker_AssetNames, linker_LibraryNames;
	private String[] resourceBundleNames;
	private Map<String, Data> data;
    private Map<String, String> locations;
    private Map<String, Long> timestamps;
	
	private int frameCount;
	private int bgColor, defaultWidth, defaultHeight, width, height;
	private String pageTitle;
	private double widthPercent, heightPercent;
	
	private String linkReport, configurationReport;
	private Message[] messages;
	
	private String[][] assetNames, definitionNames;
	
	public boolean contentUpdated()
	{
		for (Iterator<String> i = timestamps.keySet().iterator(); i.hasNext(); )
		{
			String path = i.next();
			Long ts = timestamps.get(path);
			File f = new File(path);
			
			if (!f.exists() || f.lastModified() != ts.longValue())
			{
				return true;
			}
		}
		return false;
	}
	
	public String[] getSourceNames(Object report)
	{
		return (COMPILER.equals(report)) ? compiler_SourceNames : (LINKER.equals(report)) ? linker_SourceNames : null;
	}

	public String[] getAssetNames(int frame)
	{
		return assetNames[frame - 1];
	}
	
	public String[] getAssetNames(Object report)
	{
		return (COMPILER.equals(report)) ? compiler_AssetNames : (LINKER.equals(report)) ? linker_AssetNames : null;
	}

	public String[] getLibraryNames(Object report)
	{
		return (COMPILER.equals(report)) ? compiler_LibraryNames : (LINKER.equals(report)) ? linker_LibraryNames : null;
	}

	public String[] getResourceBundleNames()
	{
		return resourceBundleNames;
	}
	
	public String[] getDefinitionNames(int frame)
	{
		return definitionNames[frame - 1];
	}
	
	public String[] getDefinitionNames(String sourceName)
	{
		Data d = data.get(sourceName);
		return d == null ? null : d.definitions;
	}
	
	public String getLocation(String definition)
	{
		return locations.get(definition);
	}
	
	public String[] getDependencies(String definition)
	{
		String location = getLocation(definition);
		
		if (location != null)
		{
			Data d = data.get(location);
			return d == null ? null : d.dependencies;
		}
		else
		{
			return null;
		}
	}

	public String[] getPrerequisites(String definition)
	{
		String location = getLocation(definition);
		
		if (location != null)
		{
			Data d = data.get(location);
			return d == null ? null : d.prerequisites;
		}
		else
		{
			return null;
		}
	}
	
	public long writeLinkReport(Writer out) throws IOException
	{
		long size = 0;
		
		if (linkReport != null)
		{
			out.write(linkReport);
			out.flush();
			size = linkReport.length();
		}
		
		return size;
	}

	public long writeConfigurationReport(Writer out) throws IOException
	{
		long size = 0;
		
		if (configurationReport != null)
		{
			out.write(configurationReport);
			out.flush();
			size = configurationReport.length();
		}
		
		return size;
	}
	
	public int getBackgroundColor()
	{
		return bgColor;
	}
	
	public String getPageTitle()
	{
		return pageTitle;
	}
	
	public int getDefaultWidth()
	{
		return defaultWidth;
	}
	
	public int getDefaultHeight()
	{
		return defaultHeight;
	}
	
	public int getWidth()
	{
		return width;
	}
	
	public int getHeight()
	{
		return height;
	}
	
	public double getWidthPercent()
	{
		return widthPercent;
	}
	
	public double getHeightPercent()
	{
		return heightPercent;
	}
	
	public String getCompilerVersion()
	{
		return VersionInfo.buildMessage();
	}

	public Message[] getMessages()
	{
		return messages;
	}
	
	public int getFrameCount()
	{
		return frameCount;
	}
	
	private void processCompilationUnits(List<CompilationUnit> units, TreeSet<String> sources, TreeSet<String> assets, TreeSet<String> libraries)
	{
		for (int i = 0, length = units == null ? 0 : units.size(); i < length; i++)
		{
			CompilationUnit u = units.get(i);
			Source s = (u == null) ? null : u.getSource();
			
			if (s == null)
			{
				continue;
			}
			
			if (s.isFileSpecOwner() || s.isSourceListOwner() || s.isSourcePathOwner() || s.isResourceBundlePathOwner())
			{
				sources.add(s.getName());
				
				for (Iterator j = s.getFileIncludes(); j.hasNext(); )
				{
					VirtualFile f = (VirtualFile) j.next();
					sources.add(f.getName());
				}

				if (u.hasAssets())
                {
                    for (Iterator j = u.getAssets().iterator(); j.hasNext(); )
                    {
                        Map.Entry e = (Map.Entry) j.next();
                        AssetInfo assetInfo = (AssetInfo) e.getValue();
                        VirtualFile path = assetInfo.getPath();
                        if (path != null)
                        {
                            assets.add(path.getName());
                        }
                    }
                }
			}
			else if (s.isSwcScriptOwner())
			{
				String location = ((SwcScript) s.getOwner()).getLibrary().getSwcLocation(); 
				libraries.add(location);
			}
		}
	}
	
	private void processSources(List<Source> sourceList, TreeSet<String> sources, TreeSet<String> assets, TreeSet<String> libraries,
								Map<String, Data> data, Map<String, String> locations)
	{
		for (int i = 0, length = sourceList == null ? 0 : sourceList.size(); i < length; i++)
		{
			Source s = sourceList.get(i);
			CompilationUnit u = (s == null) ? null : s.getCompilationUnit();
			
			if (s == null)
			{
				continue;
			}
			
			if (s.isFileSpecOwner() || s.isSourceListOwner() || s.isSourcePathOwner() || s.isResourceBundlePathOwner())
			{
				sources.add(s.getName());
				
				for (Iterator j = s.getFileIncludes(); j.hasNext(); )
				{
					VirtualFile f = (VirtualFile) j.next();
					sources.add(f.getName());
				}

				if (u.hasAssets())
                {
                    for (Iterator j = u.getAssets().iterator(); j.hasNext(); )
                    {
                        Map.Entry e = (Map.Entry) j.next();
                        AssetInfo assetInfo = (AssetInfo) e.getValue();
                        VirtualFile path = assetInfo.getPath();
                        if (path != null)
                        {
                            assets.add(assetInfo.getPath().getName());
                        }
                    }
                }

				if (locations != null)
				{
					for (int j = 0, size = u.topLevelDefinitions.size(); j < size; j++)
					{
						locations.put(u.topLevelDefinitions.get(j).toString(), s.getName());
					}
				}
			}
			else if (s.isSwcScriptOwner())
			{
				String location = ((SwcScript) s.getOwner()).getLibrary().getSwcLocation(); 
				libraries.add(location);
				
				if (locations != null)
				{
					for (int j = 0, size = u.topLevelDefinitions.size(); j < size; j++)
					{
						locations.put(u.topLevelDefinitions.get(j).toString(), location);
					}
				}
			}
		}

		for (int i = 0, length = sourceList == null ? 0 : sourceList.size(); i < length; i++)
		{
			Source s = sourceList.get(i);
			CompilationUnit u = (s == null) ? null : s.getCompilationUnit();
			
			if (s == null)
			{
				continue;
			}
			
			if (s.isFileSpecOwner() || s.isSourceListOwner() || s.isSourcePathOwner() || s.isResourceBundlePathOwner())
			{
				Data d = new Data();
				d.definitions = toArray(u.topLevelDefinitions);
				d.prerequisites = toArray(u.inheritance, null, locations);
                Set<Name> nameSet = new HashSet<Name>();
                nameSet.addAll(u.namespaces);
                nameSet.addAll(u.types);
                nameSet.addAll(u.expressions);
				d.dependencies = toArray(nameSet, new Set[] { u.extraClasses, u.resourceBundleHistory }, locations);
					
				data.put(s.getName(), d);
			}
		}
	}

	private void processFrames(SimpleMovie movie)
	{
		int count = movie == null ? 0 : movie.frames.size();
		assetNames = new String[count][];
		definitionNames = new String[count][];
		
		for (int i = 0; i < count; i++)
		{
			Frame f = movie.frames.get(i);
			List<CompilationUnit> units = movie.getExportedUnitsByFrame(f);
            List<String> aList = new ArrayList<String>(), dList = new ArrayList<String>();
			for (int j = 0, size = units == null ? 0 : units.size(); j < size; j++)
			{
				CompilationUnit u = units.get(j);
				Source s = u.getSource();
				
                if (u.hasAssets())
                {
                    for (Iterator k = u.getAssets().iterator(); k.hasNext(); )
                    {
                        Map.Entry e = (Map.Entry) k.next();
                        AssetInfo assetInfo = (AssetInfo) e.getValue();
                        VirtualFile path = assetInfo.getPath();
                        if (path != null)
                        {
                            String assetName = path.getName();
                            if (!aList.contains(assetName))
                            {
                                aList.add(assetName);
                            }
                        }
					}
				}

				if (s.isFileSpecOwner() || s.isResourceBundlePathOwner() || s.isSourceListOwner() ||
					s.isSourcePathOwner() || s.isSwcScriptOwner())
				{
					for (Iterator k = u.topLevelDefinitions.iterator(); k.hasNext(); )
					{
						String definitionName = k.next().toString();
						dList.add(definitionName);
					}
				}
			}
			
			if (aList.size() > 0)
			{
				assetNames[i] = new String[aList.size()];
				aList.toArray(assetNames[i]);
			}
			
			if (dList.size() > 0)
			{
				definitionNames[i] = new String[dList.size()];
				dList.toArray(definitionNames[i]);
			}
		}
	}
	
	private void processMessages(List<Message> messages)
	{
		if (messages != null && messages.size() > 0)
		{
			List<Message> filtered = new ArrayList<Message>();
			
			for (int i = 0, length = messages.size(); i < length; i++)
			{
				Message m = messages.get(i);
				if (m != null && !Message.INFO.equals(m.getLevel()))
				{
					filtered.add(m);
				}
			}
			
			messages = filtered;
		}
			
		if (messages != null && messages.size() > 0)
		{
			this.messages = new Message[messages.size()];
			for (int i = 0, length = this.messages.length; i < length; i++)
			{
				this.messages[i] = new GenericMessage(messages.get(i));
			}
		}
		else
		{
			this.messages = null;
		}
	}
	
	private String[] toArray(Set set)
	{
		String[] a = new String[set == null ? 0 : set.size()];
		int j = 0;
		
		if (set != null)
		{
			for (Iterator i = set.iterator(); i.hasNext(); j++)
			{
				a[j] = (String) i.next();
			}
		}
		
		return a.length == 0 ? null : a;
	}

	private String[] toArray(QNameList definitions)
	{
		String[] a = new String[definitions == null ? 0 : definitions.size()];
		
		for (int i = 0; i < a.length; i++)
		{
			a[i] = definitions.get(i).toString();
		}
		
		return a.length == 0 ? null : a;
	}

	private String[] toArray(Set<Name> nameSet, Set[] sets, Map locations)
	{
		TreeSet<String> set = new TreeSet<String>();
		
        for (Name name : nameSet)
		{
					String qName = null;
            if (name instanceof QName && (locations == null || locations.containsKey(qName = name.toString())))
					{
						set.add(qName);
					}
				}

		for (int i = 0, length = sets == null ? 0 : sets.length; i < length; i++)
		{
			if (sets[i] != null)
			{
				for (Iterator j = sets[i].iterator(); j.hasNext(); )
				{
					Object obj = j.next();
					if ((obj instanceof String) && (locations == null || locations.containsKey(obj)))
					{
						set.add((String)obj);
					}
				}
			}
		}

		return toArray(set);
	}

	static class Data
	{
		String[] definitions;
		String[] prerequisites;
		String[] dependencies;
	}
}
