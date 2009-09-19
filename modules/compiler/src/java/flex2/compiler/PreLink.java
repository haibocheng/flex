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

package flex2.compiler;

import flex2.compiler.common.Configuration;
import flex2.compiler.util.NameMappings;
import java.util.List;

/**
 * @author Clement Wong
 */
public interface PreLink
{
	void run(List<Source> sources,
	         List<CompilationUnit> units,
	         FileSpec fileSpec,
	         SourceList sourceList,
	         SourcePath sourcePath,
	         ResourceBundlePath bundlePath,
	         ResourceContainer resources,
	         SymbolTable symbolTable,
             CompilerSwcContext swcContext,
             NameMappings nameMappings,
	         Configuration configuration);

    void postRun(List<Source> sources,
                 List<CompilationUnit> units,
                 ResourceContainer resources,
                 CompilerSwcContext swcContext,
                 Configuration configuration);
}
