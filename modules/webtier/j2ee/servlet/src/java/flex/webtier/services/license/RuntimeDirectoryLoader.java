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
package flex.webtier.services.license;

import flex.webtier.util.PathResolver;

import java.io.File;
import java.io.IOException;

public class RuntimeDirectoryLoader extends DirectoryLoader {

    public RuntimeDirectoryLoader(String flexdir)
    {
        super(flexdir);
    }

    protected String getPropertiesFileName() throws IOException
    {
        String licenseFile;

        PathResolver pathResolver = PathResolver.getThreadLocalPathResolver();
        assert (pathResolver != null) : ("path resolver must be set before calling this function");

        Object location = pathResolver.resolveLocation(flexdir);

        if (location instanceof File)
        {
            licenseFile = new File((File) location, LICENSE_FILE_NAME).getCanonicalPath();
        }
        else
        {
            licenseFile = new File(".", LICENSE_FILE_NAME).getCanonicalPath();
        }

        return licenseFile;
    }

}
