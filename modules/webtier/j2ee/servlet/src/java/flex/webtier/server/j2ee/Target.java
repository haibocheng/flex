////////////////////////////////////////////////////////////////////////////////
//
//  ADOBE SYSTEMS INCORPORATED
//  Copyright 2006-2008 Adobe Systems Incorporated
//  All Rights Reserved.
//
//  NOTICE: Adobe permits you to use, modify, and distribute this file
//  in accordance with the terms of the license agreement accompanying it.
//
////////////////////////////////////////////////////////////////////////////////
package flex.webtier.server.j2ee;

import flash.swf.Movie;
import flex2.compiler.FileSpec;
import flex2.compiler.ResourceBundlePath;
import flex2.compiler.ResourceContainer;
import flex2.compiler.SourceList;
import flex2.compiler.SourcePath;
import flex2.compiler.common.Configuration;
import flex2.compiler.swc.SwcCache;
import java.util.List;
import macromedia.asc.util.ContextStatics;

/**
 * @author Clement Wong
 */
public class Target
{
    public int checksum;
    public FileSpec fileSpec;
    public SourceList sourceList;
    public SourcePath sourcePath;
    public ResourceContainer resources;
    public ResourceBundlePath bundlePath;
    public List units;
    public Configuration configuration;
    public ContextStatics perCompileData;
    public SwcCache swcCache;
    public Movie movie;
}
