////////////////////////////////////////////////////////////////////////////////
//
//  ADOBE SYSTEMS INCORPORATED
//  Copyright 2003-2007 Adobe Systems Incorporated
//  All Rights Reserved.
//
//  NOTICE: Adobe permits you to use, modify, and distribute this file
//  in accordance with the terms of the license agreement accompanying it.
//
////////////////////////////////////////////////////////////////////////////////
package flex.webtier.services.config;

import flex2.compiler.config.ConfigurationValue;
import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * @author cmurphy
 */
public class CacheConfiguration 
{
    private int contentSize = 10;
    private int jspSourceCacheSize = 20;
    private long httpMaximumAge = 1;
    private int fileWatcherInterval = 1;

    /**
     * @return number of entries in the content cache
     */
    public int getContentSize() 
    {
        return contentSize;
    }

    /**
     * set the max number of entries in the content cache
     * @param size number of entries
     */
    public void cfgContentSize(ConfigurationValue cv, int size)
    {
        this.contentSize = size;
    }

    /**
     * @return number of entries in the jsp source cache
     */
    public int getJspSourceCacheSize()
    {
        return jspSourceCacheSize;
    }

    /**
     * set the number of entries in the jsp source cache
     * @param cv not used
     * @param size number of entries
     */
    public void cfgJspSourceCacheSize(ConfigurationValue cv, int size)
    {
        this.jspSourceCacheSize = size;
    }

    public long getHttpMaximumAge() 
    {
        return httpMaximumAge;
    }

    public void cfgHttpMaximumAge(ConfigurationValue cv, long httpMaximumAge)
    {
        this.httpMaximumAge = httpMaximumAge;
    }

    public int getFileWatcherInterval() 
    {
        return fileWatcherInterval;
    }

    public void cfgFileWatcherInterval(ConfigurationValue cv, int fileWatcherInterval)
    {
        this.fileWatcherInterval = fileWatcherInterval;
    }

    public String toString() 
    {
        StringWriter sw = new StringWriter();
        PrintWriter debugWriter = new PrintWriter(sw);

        debugWriter.println("CacheConfiguration: size = " + contentSize);
        debugWriter.println("CacheConfiguration: httpMaximumAge = " + httpMaximumAge);
        debugWriter.println("CacheConfiguration: fileWatcherInterval = " + fileWatcherInterval);

        return sw.toString();
    }
}
