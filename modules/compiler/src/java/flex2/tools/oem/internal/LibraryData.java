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

import java.util.Map;
import java.util.Set;

import flex2.compiler.Source;
import flex2.compiler.io.VirtualFile;
import flex2.compiler.swc.SwcComponent;

/**
 * @version 2.0.1
 * @author Clement Wong
 *
 */
public class LibraryData extends ApplicationData
{
	public Set<SwcComponent> nsComponents;
    public Map<String, Source> classes;
    public Set<VirtualFile> fileSet;
	public Map<String, VirtualFile> rbFiles, swcArchiveFiles, cssArchiveFiles, l10nArchiveFiles;
}
