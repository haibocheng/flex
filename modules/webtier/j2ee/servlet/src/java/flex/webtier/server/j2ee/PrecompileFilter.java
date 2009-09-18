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
package flex.webtier.server.j2ee;

import flex.webtier.util.J2EEUtil;
import flex.webtier.server.j2ee.cache.CacheHelper;
import flex.webtier.server.j2ee.cache.CacheKeyUtils;
import flex.webtier.util.Trace;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;

public class PrecompileFilter extends MxmlFilter
{
    private String swfExt;

    public PrecompileFilter(String swfExt)
    {
        this.swfExt = swfExt;
    }

    public void invoke(MxmlContext context) throws Throwable
    {
        String path = context.getRequest().getServletPath();
        path = path.replace('\\', '/');
        path = PlatformFileUtils.normalize(path);
        String fileName = J2EEUtil.getRealPath(path, context.getServletContext());
        
        if (fileName != null)
        {
            try
            {
                PathValidator.checkFileExists(fileName, context.getRequest());

                byte[] swfBuffer = toSwf(fileName, context);
                if (swfBuffer.length > 0)
                {
                    context.setSwfBuffer(swfBuffer);
                }
            }
            catch (Throwable t)
            {
                throw t;
            }

            if (next != null)
            {
                next.invoke(context);
            }
        }
        else
        {
            throw new FileNotFoundException("File not found: " + context.getRequest().getServletPath());
        }
    }

    private byte[] toSwf(String filename, MxmlContext context) throws Throwable
    {
        CacheHelper ch = CacheHelper.getInstance(context.getServletContext());
        String cacheKey = CacheKeyUtils.getUrlData(context.getRequest()).cacheKey;

        ch.lockCache();

        try
        {
            SWFInfo swfInfo = ch.getFromCache(cacheKey);

            if (swfInfo != null)
            {
                if (Trace.cache)
                {
                    Trace.trace("[cache] swfinfo in content cache up-to-date; cache key = " + cacheKey);
                }
            }
            else
            {
                if (Trace.cache)
                {
                    Trace.trace("[cache] reload swf; cache key = " + cacheKey);
                }

                swfInfo = readSwfFromDisk(context);
                HashMap dependencies = new HashMap();
                dependencies.put(filename, new Long(swfInfo.getLastModified()));
                ch.addToCache(cacheKey, swfInfo, dependencies);
            }

            context.setLastModifiedTime(swfInfo.getLastModified());
            return swfInfo.getSwfBuffer();
        }
        catch (Throwable t)
        {
            ch.unlockCache(cacheKey);
            ch.removeFromCache(cacheKey);
            throw t;
        }
    }

    public SWFInfo readSwfFromDisk(MxmlContext context) throws IOException
    {
        SWFInfo swfInfo = null;

        String path = context.getRequest().getServletPath();

        try
        {
            path = path.replace('\\', '/');
            path = PlatformFileUtils.normalize(path);
            String fileName = J2EEUtil.getRealPath(path, context.getServletContext());
            
            File swfFile = null;

            //By this time we should have a valid file path that must end with a SWF extension
            if (fileName.endsWith(swfExt))
            {
                swfFile = new File(fileName);
            }

            if (swfFile != null)
            {
                byte[] data = new byte[(int)swfFile.length()];
                DataInputStream in = new DataInputStream(new FileInputStream(swfFile));
                try
                {
                    in.readFully(data);
                }
                finally
                {
                    in.close();
                }

                long lastModified = swfFile.lastModified();
                lastModified -= (lastModified % 1000);
                swfInfo = new SWFInfo(fileName, context.getServletContext(), context.getRequest(), lastModified);
                swfInfo.setSwfBuffer(data);
            }
            else
            {
                throw new FileNotFoundException("Unable to find " + path);
            }
        }
        catch (FileNotFoundException fnfe)
        {
            throw new FileNotFoundException("Unable to find " + path);
        }

        return swfInfo;
    }
}
