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

package flex2.compiler;

import flex2.compiler.io.VirtualFile;
import flex2.compiler.util.CompilerMessage;
import flex2.compiler.util.ThreadLocalToolkit;

import java.io.File;
import java.util.*;

/**
 * A list of files, which are not required to follow the single public definition rule.
 * This is used by compc's include-sources option and by Flex Builder.  It's similar to
 * ASC's include option.
 *
 * @author Clement Wong
 */
public final class FileSpec
{
	public FileSpec(List<VirtualFile> files, String[] mimeTypes)
		throws CompilerException
	{
        this(files, mimeTypes, true);
	}

    public FileSpec(List<VirtualFile> files, String[] mimeTypes, boolean lastIsRoot)
	    throws CompilerException
    {
        VirtualFile[] vfiles = new VirtualFile[files.size()];
        files.toArray(vfiles);
        init(vfiles, mimeTypes, lastIsRoot);
    }

	private void init(VirtualFile[] files, String[] mimeTypes, boolean lastIsRoot)
		throws CompilerException
	{
		this.mimeTypes = mimeTypes;

		sources = new LinkedHashMap<String, Source>(files.length);

		for (int i = 0, length = files.length; i < length; i++)
		{
			if (isSupported(files[i]))
			{
				String name = files[i].getName();
				String shortName = name.substring(name.lastIndexOf(File.separator) + 1, name.lastIndexOf('.'));
				// C: Source.relativePath = "". Strictly speaking, Source in FileSpec shouldn't have the notion of
				//    relative paths.
				Source s = new Source(files[i], "", shortName, this, false, (i == length - 1) && lastIsRoot);
				sources.put(name, s);
			}
			else
			{
				UnsupportedFileType err = new UnsupportedFileType(files[i].getName());
				ThreadLocalToolkit.log(err);
				throw err;
			}
		}
	}

	private Map<String, Source> sources;
	private String[] mimeTypes;

	public List<Source> retrieveSources()
	{
		List<Source> sources = new ArrayList<Source>(this.sources.size());

		for (Iterator<String> i = this.sources.keySet().iterator(); i.hasNext();)
		{
			String name = i.next();
			Source s = this.sources.get(name);
			CompilationUnit u = (s != null) ? s.getCompilationUnit() : null;

			if (s != null && !s.exists())
			{
				// C: This is a FileSpec. If the source doesn't exist, the compiler should get a warning...
				s = null;
			}
			else if ((u != null && !u.isDone()) || (s != null && s.isUpdated()))
			{
				// s.removeCompilationUnit();
			}
			else if (u != null)
			{
				s = s.copy();
				assert s != null;
			}

			if (s != null)
			{
				sources.add(s);
			}
		}

		return sources;
	}

	private boolean isSupported(VirtualFile file)
	{
		for (int i = 0, length = mimeTypes.length; i < length; i++)
		{
			if (mimeTypes[i].equals(file.getMimeType()))
			{
				return true;
			}
		}

		return false;
	}

	String[] getMimeTypes()
	{
		return mimeTypes;
	}

	Collection<Source> sources()
	{
		return sources.values();
	}

	boolean isUpdated()
	{
		for (Iterator<String> i = this.sources.keySet().iterator(); i.hasNext();)
		{
			String name = i.next();
			Source s = this.sources.get(name);
			CompilationUnit u = (s != null) ? s.getCompilationUnit() : null;

			if ((s != null) &&
                !s.isRoot() &&
			    (!s.exists() ||
                 s.isUpdated() ||
                 s.hasError() ||
                 (u != null &&
                  (!u.isDone() ||
                   (u.hasAssets() &&
                    u.getAssets().isUpdated())))))
			{
				return true;
			}
		}

		return false;
	}

	// error messages

	public static class UnsupportedFileType extends CompilerMessage.CompilerError
	{
		private static final long serialVersionUID = -149187184530224369L;

        public UnsupportedFileType(String name)
		{
			super();
			this.name = name;
		}

		public final String name;
	}
}
