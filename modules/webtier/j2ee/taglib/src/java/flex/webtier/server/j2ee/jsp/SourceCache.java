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
package flex.webtier.server.j2ee.jsp;

import flash.util.LRUCache;
import flex.webtier.services.ServiceFactory;
import flex.webtier.services.config.CacheConfiguration;
import flex.webtier.services.config.Configurator;
import flex.webtier.util.Hex;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import javax.servlet.ServletContext;


/**
 * @author Edwin Smith
 * @author Cathy Reilly
 */
public class SourceCache extends LRUCache implements Serializable
{
    private static final String ATTRNAME = SourceCache.class.getClassLoader().hashCode() + SourceCache.class.getName();

    public SourceCache(int initialSize, int maxSize)
    {
        super(initialSize, maxSize);
    }

    public SourceCacheEntry getEntry(String key)
    {
        return (SourceCacheEntry) get(key);
    }

    public void addEntry(SourceCacheEntry entry)
    {
        put(entry.sourceKey, entry);
    }

    public static synchronized SourceCache getInstance(ServletContext servletContext)
    {
        SourceCache sourceCache = (SourceCache) servletContext.getAttribute(ATTRNAME);
        if (sourceCache == null)
        {
            Configurator config = ServiceFactory.getConfigurator();
            CacheConfiguration cacheConfig = config.getServerConfiguration().getCacheConfiguration();
            int maxSize = cacheConfig.getJspSourceCacheSize();
            sourceCache = new SourceCache(maxSize/4, maxSize);
            servletContext.setAttribute(ATTRNAME, sourceCache);
        }
        return sourceCache;
    }

    public static String sourceToKey(String sourceCode)
    {
        try
        {
            return md5Algorithm(sourceCode);
        }
        catch (UnsupportedEncodingException uee)
        {
            // don't expect to get here but use the old algorithm if you do
            return hashCodeAlgorithm(sourceCode);
        }
        catch (NoSuchAlgorithmException nsae)
        {
            // don't expect to get here but use the old algorithm if you do
            return hashCodeAlgorithm(sourceCode);
        }
    }

    private static String md5Algorithm(String sourceCode) throws NoSuchAlgorithmException, UnsupportedEncodingException
    {
        MessageDigest ctx = MessageDigest.getInstance("MD5");
        byte[] ba = ctx.digest(sourceCode.getBytes("UTF-8"));
        Hex.Encoder encoder = new Hex.Encoder(ba.length * 2);
        encoder.encode(ba);
        String encoded = encoder.drain();
        return encoded;
    }

    private static String hashCodeAlgorithm(String sourceCode)
    {
        int i = sourceCode.hashCode();
        return String.valueOf(i < 0 ? ~i : i);
    }

    public static String uriToKey(String uri)
    {
        int i = uri.lastIndexOf('/');
        int j = uri.indexOf('.', i);
        return uri.substring(i+1, j);
    }
}
