////////////////////////////////////////////////////////////////////////////////
//
//  ADOBE SYSTEMS INCORPORATED
//  Copyright 2007-2008 Adobe Systems Incorporated
//  All Rights Reserved.
//
//  NOTICE: Adobe permits you to use, modify, and distribute this file
//  in accordance with the terms of the license agreement accompanying it.
//
////////////////////////////////////////////////////////////////////////////////
package flex.webtier.util;

import flex.webtier.server.j2ee.IncrementalCompileFilter;
import flex.webtier.services.ServiceFactory;
import flex2.compiler.util.ThreadLocalToolkit;

public class ServletUtils {

	public static void clearThreadLocals(){
        ThreadLocalToolkit.setBenchmark(null);
        ThreadLocalToolkit.setCompilerControl(null);
        ThreadLocalToolkit.setLocalizationManager(null);            
        ThreadLocalToolkit.setMimeMappings(null);            
        ThreadLocalToolkit.setProgressMeter(null);            
        ThreadLocalToolkit.resetResolvedPaths();            
        if (IncrementalCompileFilter.targetThreadLocal!=null)
        	IncrementalCompileFilter.targetThreadLocal.set(null);            
        PathResolver.setThreadLocalPathResolver(null);
        ThreadLocalToolkit.setLocalizationManager(null);
        ThreadLocalToolkit.setLogger(null);
        ThreadLocalToolkit.setPathResolver(null);
	}
	
}
