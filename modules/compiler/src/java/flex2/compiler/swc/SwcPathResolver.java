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

package flex2.compiler.swc;

import flash.util.Trace;
import flex2.compiler.common.SinglePathResolver;
import flex2.compiler.io.VirtualFile;
import flex2.compiler.swc.SwcFile;

import java.util.Map;

/**
 * Resolves files found in a given Map<String, VirtualFile>, where the String is the path.  
 *
 * @author Brian Deitte
 */
public class SwcPathResolver implements SinglePathResolver
{
    private SwcGroup swcGroup;

    public SwcPathResolver(SwcGroup swcGroup)
    {
        this.swcGroup = swcGroup;
    }

    public VirtualFile resolve( String pathStr )
    {
        // Handles the case when pathStr is something like "foo.css".
        VirtualFile virt = swcGroup.getFiles().get(pathStr);

	    if (virt == null)
	    {
            // Handles the case when pathStr is something like "foo.swc$bar.css".
            virt = swcGroup.getFile(pathStr);
	    }

        if ((virt != null) && Trace.pathResolver)
        {
            Trace.trace("SwcPathResolver.resolve: resolved " + pathStr + " to " + virt.getName());
        }

        return virt;
    }
}
