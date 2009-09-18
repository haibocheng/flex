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

import com.opensymphony.oscache.base.CacheEntry;
import com.opensymphony.oscache.base.NeedsRefreshException;
import com.opensymphony.oscache.general.GeneralCacheAdministrator;
import flex.webtier.util.J2EEUtil;
import flex.webtier.server.j2ee.RequestContext;
import flex.webtier.server.j2ee.SWFInfo;
import flex.webtier.services.ServiceFactory;
import flex.webtier.services.config.CacheConfiguration;
import flex.webtier.services.config.Configurator;
import flex.webtier.services.config.DebuggingConfiguration;
import flex.webtier.services.config.ServerConfiguration;
import flex.webtier.util.HttpConstants;
import flex.webtier.util.Trace;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.ServletContext;

public class CacheHelper
{
    private PageStateCache pages;

    private String dependencyFile;
    private boolean incremental;
    private boolean processRecompile;
    private boolean productionMode;
    private long configurationVersion;

    private static CacheHelper cacheHelper;
    public GeneralCacheAdministrator cache;
    private ResourceManager resourceManager;

    public static CacheHelper getInstance(ServletContext context)
    {
        if (cacheHelper == null)
        {
            cacheHelper = new CacheHelper(context);
        }

        return cacheHelper;
    }

    private CacheHelper(ServletContext context)
    {
        Configurator configurator = ServiceFactory.getConfigurator();
        configurationVersion = configurator.getVersion();
        ServerConfiguration defaultConfiguration = configurator.getServerConfiguration();
        productionMode = defaultConfiguration.isProductionMode();
        incremental = defaultConfiguration.isIncrementalCompile();

        DebuggingConfiguration debuggingConfiguration = defaultConfiguration.getDebuggingConfiguration();
        processRecompile = debuggingConfiguration.processDebugQueryParams() && !productionMode;
        if (Trace.cache)
        {
            Trace.trace("[cache] configuration: production mode = " + productionMode);
            Trace.trace("[cache] configuration: " + defaultConfiguration.getCacheConfiguration().toString());
        }

        // initialize oscache
        initCache();

        // initialize page state cache and reload persistent dependencies
        ResourceManager.checkInterval = defaultConfiguration.getCacheConfiguration().getFileWatcherInterval() * 1000;
        resourceManager = new ResourceManager();
        pages = new PageStateCache(25, 500);
        dependencyFile = J2EEUtil.getRealPath("/WEB-INF/flex/cache.dep", context);
        loadDependencies();
    }

    public void stop()
    {
    	storeDependencies();
    	cacheHelper = null;
    	cache.flushAll();
    	cache.destroy();
    }

    public void initCache()
    {
        Configurator configurator = ServiceFactory.getConfigurator();
        CacheConfiguration cacheConfiguration = configurator.getServerConfiguration().getCacheConfiguration();
        Properties cacheProperties = new Properties();
        cacheProperties.put("cache.capacity", Integer.toString(cacheConfiguration.getContentSize()));
        cacheProperties.put("cache.blocking", "true");
        cache = new GeneralCacheAdministrator(cacheProperties);
    }

    public boolean forceRecompile(HttpServletRequest request)
    {
        boolean recompile = false;

        if (processRecompile)
        {
            String param = request.getParameter("recompile");
            if ((param != null) && ("true".equals(param.toLowerCase())))
            {
                recompile = true;
            }
        }

        return recompile;
    }

    public void addToCache(String key, SWFInfo swfInfo, HashMap dependencies)
    {
        CacheEntryRefreshPolicy policy = new CacheEntryRefreshPolicy();
        policy.init(this, cache.getCache(), key, incremental);
        pages.put(key, new PageState(key, dependencies, swfInfo.getLastModified(), resourceManager));
        cache.putInCache(key, swfInfo, policy);
    }

    public void removeFromCache(String key)
    {
        if (Trace.cache)
        {
            Trace.trace("[cache] remove cache entry; key = " + key);
        }

        remove(key);
        pages.remove(key);
    }

    public void lockCache()
    {
         // with oscache, the cache is automatically blocked with the NeedsRefreshException
    }

    public void unlockCache(String key)
    {
        cache.cancelUpdate(key);
    }

    public SWFInfo getFromCache(String key)
    {
        // cmurphy - fyi this method blocks until its refreshed
        try
        {
            SWFInfo swf = (SWFInfo)cache.getFromCache(key, CacheEntry.INDEFINITE_EXPIRY);
            return swf;
        }
        catch (NeedsRefreshException nre)
        {
            return null;
        }
    }

    public long getLastModified(String key, HttpServletRequest request)
    {
        long lastModified = -1;

        PageState pageState = (PageState) pages.get(key);

        // if the page state is unavailable, return true.
        // if the pageState's last modified is not the same as the given time, return true.
        if (pageState != null)
        {
            if ((!productionMode) && (!pageState.isUpToDate()))
            {
                // remove the out-of-date item
                pages.remove(key);
            }
            else
            {
                lastModified = pageState.getLastModified();
            }
        }

        return lastModified;
    }

    public boolean needsRecompile(String key, long ifModifiedSince)
    {
        boolean needsRecompile = true;

        PageState pageState = (PageState) pages.get(key);

        // if the page state is unavailable, return true.
        // if the pageState's last modified is not the same as the given time, return true.
        if (pageState != null)
        {
            if ((!productionMode) && (!pageState.isUpToDate()))
            {
                pages.remove(key);
                needsRecompile = true;
            }
            else if (pageState.getLastModified() == ifModifiedSince)
            {
                needsRecompile = false;
            }
        }

        return needsRecompile;
    }

    public static boolean isBrowserUpToDate(HttpServletRequest request, ServletContext context, String cacheKey)
    {
        boolean uptodate = false;

        long modified = request.getDateHeader(HttpConstants.IF_MODIFIED_SINCE);

        if (Trace.cache)
        {
            Trace.trace("[cache] Checking cache key " + cacheKey);
        }

        CacheHelper ch = CacheHelper.getInstance(context);

        String dependencyKey = new RequestContext(context, request).getDependencyKey();

        if (!ch.needsRecompile(dependencyKey, modified))
        {
            uptodate = true;
            if (Trace.cache)
            {
                Trace.trace("[cache] Browser cache up-to-date, key = " + cacheKey + "; modified = " + modified);
            }

            uptodate = true;
        }
        else
        {
            if (Trace.cache)
            {
                Trace.trace("[cache] Browser cache is out-of-date, key = " + cacheKey + "; modified = " + modified);
            }
        }

        return uptodate;
    }

    /**
     * Load the persistent page state information and initialize
     * the file watcher service.
     */
    private void loadDependencies()
    {

        ObjectInputStream ois = null;

        try
        {
            ois = new ObjectInputStream(new FileInputStream(dependencyFile));
            long lastConfiguration  = ois.readLong();
            if (lastConfiguration != configurationVersion)
            {
                if (Trace.cache)
                {
                    Trace.trace("[cache] invalidating dependencies because configuration changed");
                }
            }

            Map previousLicenses = (Map)ois.readObject();
            Map currentLicenses = ServiceFactory.getLicenseService().getLicenseMap();

            if ((previousLicenses.size() != currentLicenses.size()) || ((!previousLicenses.equals(currentLicenses))))
            {
                if (Trace.cache)
                {
                    Trace.trace("[cache] invalidating dependencies because license changed");
                }
            }
            else
            {
                Map map = (Map) ois.readObject();

                if (Trace.cache)
                {
                    Trace.trace("[cache] load dependencies from " + dependencyFile);
                    for (Iterator i = map.keySet().iterator(); i.hasNext(); )
                    {
                        String key = (String)i.next();
                        Trace.trace(key + " = " + map.get(key));
                    }
                }

                // initialize the file watcher with existing PageState data, unless the pages are out of date
                for (Iterator pageIter = map.keySet().iterator(); pageIter.hasNext();)
                {
                    String pageKey = (String)pageIter.next();
                    PageState pageState = (PageState) map.get(pageKey);
                    // pageState.init(fileWatcherService);
                    // C: mxmlSize may be larger/smaller than what's in cache.dep. We essentially
                    //    reset the LRU status after restart, which I think it's okay 'cos we
                    //    never rely on the LRU status for anything anyway...
                    pageState.setResourceManager(resourceManager);
                    if (pageState.isUpToDate())
                    {
                        pages.put(pageKey, pageState);
                    }
                    else
                    {
                        if (Trace.cache)
                        {
                            Trace.trace("[cache] " + pageKey + " is out of date");
                        }
                    }
                }
            }
        }
        catch (FileNotFoundException e)
        {
            ServiceFactory.getLogger().logDebug("[cache] No dependency information found in "+e.getMessage());
        }
        catch (Exception e)
        {
            if (Trace.error)
            {
                e.printStackTrace();
            }
            ServiceFactory.getLogger().logInfo("[cache] No existing dependency information.");
        }
        finally
        {
            if (ois != null)
            {
                try
                {
                    ois.close();
                }
                catch (IOException ioe)
                {
                    // ignore
                }
            }
        }
    }

    /**
     * Persist the page state information.
     */
    private void storeDependencies()
    {

        ObjectOutputStream oos = null;

        try
        {
            oos = new ObjectOutputStream(new FileOutputStream(dependencyFile));
            oos.writeLong(configurationVersion);
            oos.writeObject(ServiceFactory.getLicenseService().getLicenseMap());
            // C: write a HashMap, not LRUCache, even though it's serializable.
            oos.writeObject(pages.getPageStates());
        }
        catch (Exception e)
        {
            ServiceFactory.getLogger().logWarning("[cache] Dependency information could not be stored in " + dependencyFile);
        }
        finally
        {
            if (oos != null)
            {
                try
                {
                    oos.close();
                }
                catch (IOException e)
                {
                    // ignore
                }
            }
        }
    }

    public void remove(String key)
    {
        if (cache != null)
        {
            cache.removeEntry(key);
        }
    }

    public void flushAll()
    {
        if (cache != null)
        {
            cache.flushAll();
        }
    }
}
