////////////////////////////////////////////////////////////////////////////////
//
//  ADOBE SYSTEMS INCORPORATED
//  Copyright 2005-2008 Adobe Systems Incorporated
//  All Rights Reserved.
//
//  NOTICE: Adobe permits you to use, modify, and distribute this file
//  in accordance with the terms of the license agreement accompanying it.
//
////////////////////////////////////////////////////////////////////////////////
package flex.webtier.server.j2ee;

import flex.webtier.server.j2ee.cache.CacheHelper;

import java.util.HashMap;
import java.util.Map;
import java.util.Vector;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.ServletContext;

public class SWFInfo
{
    private long lastModified;
    private byte[] swfBuffer;
    private HashMap dependencies;
    private String height;
    private String width;
    private String pageTitle;
    private String backgroundColor;
    private Vector compileEvents;
    private Target target;
    private Map licenseMap;
    
    public SWFInfo(String mxmlTarget, ServletContext context, HttpServletRequest request)
    {
        CacheHelper ch = CacheHelper.getInstance(context);
        // this handles the persistence across server restarts case
        // or, the case where the swfinfo has been kicked out of the cache
        long modTime = ch.getLastModified(mxmlTarget, request);
        if (modTime == -1)
        {
            long currentTime = System.currentTimeMillis();
            // http dates are 1 second granularity
            currentTime -= currentTime % 1000;
            lastModified = currentTime;
        }
        else
        {
            lastModified = modTime;
        }
    }

    public SWFInfo(String mxmlTarget, ServletContext context, HttpServletRequest request, long lastModified)
    {
        CacheHelper ch = CacheHelper.getInstance(context);
        // this handles the persistence across server restarts case
        // or, the case where the swfinfo has been kicked out of the cache
        long modTime = ch.getLastModified(mxmlTarget, request);
        if (modTime == -1)
        {
            this.lastModified = lastModified;
        }
        else
        {
            this.lastModified = modTime;
        }
    }

    public long getLastModified()
    {
        assert lastModified != 0 : "lastModified must be set before using";
        return lastModified;
    }

    public byte[] getSwfBuffer()
    {
        assert swfBuffer != null : "swfBuffer must be set before using";
        return swfBuffer;
    }

    public void setSwfBuffer(byte[] bos)
    {
        assert bos != null;
        swfBuffer = bos;
    }

    public void setDependencies(HashMap dependencies)
    {
        this.dependencies = dependencies;
    }

    public HashMap getDependencies()
    {
        assert dependencies != null : "dependencies must be set before using";
        return dependencies;
    }

    public void setHeight(String height)
    {
        this.height = height;
    }

    public String getHeight()
    {
        assert height != null : "height must be set before using";
        return this.height;
    }

    public void setWidth(String width)
    {
        this.width = width;
    }

    public String getWidth()
    {
        assert width != null : "width must be set before using";
        return this.width;
    }

    public void setPageTitle(String pageTitle)
    {
        this.pageTitle = pageTitle;
    }

    public String getPageTitle()
    {
        assert pageTitle != null : "pageTitle must be set before using";
        return pageTitle;
    }
    
    public void setBackgroundColor(String backgroundColor)
    {        
        this.backgroundColor = backgroundColor;   
    }

    public String getBackgroundColor()
    {
        return backgroundColor;
    }        

    public void setTarget(Target target)
    {
        this.target = target;
    }

    public Target getTarget()
    {
        return target;
    }

    public void setCompileEvents(Vector compileEvents)
    {
        this.compileEvents = compileEvents;
    }

    public Vector getCompileEvents()
    {
        assert compileEvents != null : "compileEvents must be set before using";
        return compileEvents;
    }

    /**
     * If doing incremental compiles, a full compile should be done if the 
     * license map changes due to a <licenses> change in the config file since 
     * the license could impact the watermark on any charts in the swf.
     * @param licenseMap - the map used to generate the swf
     */
    public void setLicenseMap(Map licenseMap)
    {
        this.licenseMap = licenseMap;
    }

    public Map getLicenseMap()
    {
       return licenseMap;
    }  
}
