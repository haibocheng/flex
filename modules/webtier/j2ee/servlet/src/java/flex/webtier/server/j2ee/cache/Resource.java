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
package flex.webtier.server.j2ee.cache;

import java.io.File;

public class Resource
{
    protected String path;
    protected File file;
    private long lastModCheckTime, lastMod=0;

    public Resource(String path, File file)
    {
        this.path = path;
        this.file = file;
    }

    public String getName()
    {
        return path;
    }

    public long getLastModified() throws InvalidResourceException
    {
        if (file == null)
        {
            // try again, maybe it's now available
            file = new File(path);
        }
        
        if ((file != null) && (file.exists()))
        {
            long now = System.currentTimeMillis();

            if(now > lastModCheckTime + 1000)
            {
                lastMod = file.lastModified();
                lastModCheckTime = now;
            }

            return lastMod;
        }
        else
        {
            throw new InvalidResourceException(this.path);
        }
    }
}
