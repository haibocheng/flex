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

package flex2.compiler;

import flash.swf.tags.DefineTag;
import flash.swf.tags.DefineFont;
import flex2.compiler.io.VirtualFile;

import java.util.*;
import java.util.Map.Entry;

/**
 * @author Clement Wong
 */
public final class Assets
{
	private Map<String, AssetInfo> assets;

	public void add(String className, AssetInfo assetInfo)
	{
		if (assets == null)
		{
			assets = new HashMap<String, AssetInfo>(4);
		}

		assets.put(className, assetInfo);
	}

	// FIXME - this is cheating, not sure what the best thing to do here is.
	// Used by CompilerSwcContext.
	public void add(String className, DefineTag tag)
	{
		if (assets == null)
		{
			assets = new HashMap<String, AssetInfo>(4);
		}

		assets.put(className, new AssetInfo(tag));
	}

	public void addAll(Assets ass)
	{
		if (ass.assets == null)
		{
			return;
		}

		if (assets == null)
		{
			assets = new HashMap<String, AssetInfo>(4);
		}

		assets.putAll(ass.assets);
	}

	public int count()
	{
		return assets == null ? 0 : assets.size();
	}

	public boolean contains(String className)
	{
		return assets == null ? false : assets.containsKey(className);
	}

	public AssetInfo get(String className)
	{
		return assets == null ? null : assets.get(className);
	}

	/**
	 * This is used by the webtier compiler.
	 */
	public Iterator<Map.Entry<String, AssetInfo>> iterator()
	{
		return assets == null ? EMPTY_ITERATOR : assets.entrySet().iterator();
	}

	public boolean isUpdated()
	{
		boolean result = false;

		if (assets != null)
		{
			for (AssetInfo assetInfo : assets.values())
            {
				VirtualFile path = assetInfo.getPath();

				// If the path is null, it's probably a system font
				// that doesn't get resolved by us, so just assume it
				// hasn't changed.
				if ((path != null) && (assetInfo.getCreationTime() != path.getLastModified()))
				{
					result = true;
				}
			}
		}

		return result;
	}

	public List<DefineFont> getFonts()
	{
		LinkedList<DefineFont> fonts = new LinkedList<DefineFont>();

		if (assets != null)
		{
			for (AssetInfo assetInfo : assets.values())
            {
				DefineTag defineTag = assetInfo.getDefineTag();

				if (defineTag instanceof DefineFont)
				{
					fonts.add((DefineFont)defineTag);
				}
			}
		}

		return fonts;
	}

	public boolean exists(String name)
	{
		return assets != null && assets.containsValue(name);
	}
	
	public int size()
	{
		return assets == null ? 0 : assets.size();
	}
	
	private static final Iterator<Entry<String, AssetInfo>> EMPTY_ITERATOR = new Iterator<Entry<String, AssetInfo>>()
	{
		public boolean hasNext()
		{
			return false;
		}

		public Entry<String, AssetInfo> next()
		{
			return null;
		}

		public void remove()
		{
		}
	};
}
