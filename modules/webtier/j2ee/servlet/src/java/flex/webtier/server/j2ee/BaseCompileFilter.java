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

import flash.swf.Movie;
import flex.webtier.server.j2ee.cache.CacheHelper;
import flex.webtier.services.config.ServerConfiguration;
import flex.webtier.util.Trace;
import flex2.compiler.AssetInfo;
import flex2.compiler.CompilationUnit;
import flex2.compiler.CompilerException;
import flex2.compiler.io.FileUtil;
import flex2.compiler.io.InMemoryFile;
import flex2.compiler.io.VirtualFile;
import flex2.compiler.swc.SwcFile;
import flex2.linker.LinkerException;
import flex.webtier.server.j2ee.events.CompileEvent;

import java.net.MalformedURLException;
import java.net.URL;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public abstract class BaseCompileFilter extends MxmlFilter
{
    protected String swfExt;

    public BaseCompileFilter(String swfExt)
    {
        this.swfExt = swfExt;
    }

    public void invoke(MxmlContext context) throws Throwable
    {
        if (!context.hasErrors())
        {
            try
            {
                byte[] swfBuffer = mxmlToSwf(context.getSourceFileName(), context);
                if (swfBuffer.length > 0)
                {
                    context.setSwfBuffer(swfBuffer);
                }
            }
            catch (LinkerException le)
            {
                if (!context.hasErrors())
                {
                    context.addEvent(new CompileEvent(le));
                }
            }
            catch (CompilerException ce)
            {
                // if the errors haven't been logged, then throw exception
                // otherwise, continue and display the errors collected
                if (!context.hasErrors())
                {
                    context.addEvent(new CompileEvent(ce));
                }
            }
            catch (Throwable t)
            {
                context.addEvent(new CompileEvent(t));
            }
        }

        if (next != null)
        {
            next.invoke(context);
        }
    }

    protected abstract SWFInfo compileMxml(SWFInfo swfInfo, String filename, MxmlContext context, HashMap dependencies) throws Exception;

    private boolean isJspFile(String name)
    {
        boolean isJspFile = false;
        String path = name.replace('\\', '/');
        try
        {
            String base = path.substring(path.lastIndexOf('/')+1);
            int idx = base.indexOf('.');
            // remove initial jspXXXXX.swf
            if (idx >= 3)
            {
                base = base.substring(3, idx);

                if (base.length() == 32)
                {
                    // now that the initial checks are complete, assume it's a jsp until we figure out otherwise
                    isJspFile = true;
                    for (int i = 0; i < 32 && isJspFile; i ++)
                    {
                        if (!((Character.isDigit(base.charAt(i))) ||
                        (base.charAt(i) == 'A') ||
                        (base.charAt(i) == 'B') ||
                        (base.charAt(i) == 'C') ||
                        (base.charAt(i) == 'D') ||
                        (base.charAt(i) == 'E') ||
                        (base.charAt(i) == 'F')))
                        {
                            isJspFile = false;
                        }
                    }
                }
            }
        }
        catch (Throwable t)
        {
            // ignore, it's not a jsp file
            t.printStackTrace();
        }
        return isJspFile;
    }

    protected void getDependenciesFromCompilationUnits(List units, HashMap dependencies)
    {
        // getting dependencies
        for (int i = 0, size = units.size(); i < size; i++)
        {
            CompilationUnit u = (CompilationUnit) units.get(i);
            String name = u.getSource().getRawLocation();
            long lastmod = u.getSource().getRawLastModified();
            if (lastmod != -1)
            {
                if (!isJspFile(name))
                {
                    lastmod -= lastmod % 1000;
                    dependencies.put(name, new Long(lastmod));
                    if (Trace.cache)
                    {
                        Trace.trace("[cache] name = " + name + "; lastmod = " + lastmod);
                    }
                }
                else
                {
                    if (Trace.cache)
                    {
                        Trace.trace("[cache] skipping jsp file " + name);
                    }
                }
            }
            else
            {
                if (Trace.cache)
                {
                    Trace.trace("[cache] skipping file " + name);
                }
            }

            Iterator iter = u.getSource().getFileIncludes();
            while (iter.hasNext())
            {
                try
                {
                    VirtualFile includeVirtual = ((VirtualFile)iter.next());
                    String includeName = new URL(includeVirtual.getURL()).getFile();

                    if (includeVirtual instanceof InMemoryFile)
                    {
                        if (Trace.cache)
                        {
                            Trace.trace("[cache] skipping include file " + includeName);
                        }
                    }
                    else if (includeVirtual instanceof SwcFile)
                    {
                        // Dependency is on SwcArchive file not individual SwcFile.
                        // The dependencies map would need to be restructured to have
                        // dependencies on the SwcFile since the map does not contain enough
                        // information to recreate the virtual file to check on the mod time
                        // if the file is swapped out of the resource cache.
                        SwcFile swcFile = (SwcFile) includeVirtual;
                        String swcPath = SwcFile.getSwcLocation(swcFile.getName());
                        if (swcPath != null && swcPath.length() > 0)
                        {
                            if (!dependencies.containsKey(swcPath))
                            {
                                long includeMod = new File(swcPath).lastModified();
                                includeMod -= includeMod % 1000;
                                dependencies.put(swcPath, new Long(includeMod));
                                if (Trace.cache)
                                {
                                    Trace.trace("[cache] includes = " + swcPath + "; lastmod = " + includeMod);
                                }
                            }
                        }                                                       
                    }
                    else
                    {
                        if (!includeName.equals(""))
                        {
                            long includeMod = includeVirtual.getLastModified();
                            includeMod -= includeMod % 1000;
                            dependencies.put(includeVirtual.getName(), new Long(includeMod));
                            if (Trace.cache)
                            {
                                Trace.trace("[cache] includes = " + includeVirtual.getName() + "; lastmod = " + includeMod);
                            }
                        }
                    }
                }
                catch (MalformedURLException mue)
                {
                    // ignore
                }
            }

            iter = u.getAssets().iterator();
            while (iter.hasNext())
            {
                try
                {
                    Map.Entry asset = (Map.Entry)iter.next();
                    AssetInfo assetInfo = (AssetInfo)asset.getValue();
                    VirtualFile assetVirtual = (VirtualFile)assetInfo.getPath();
                    if (assetVirtual != null)
                    {
                        String assetName = new URL(assetVirtual.getURL()).getFile();
    
                        if (assetVirtual instanceof InMemoryFile)
                        {
                            if (Trace.cache)
                            {
                                Trace.trace("[cache] skipping asset file " + assetName);
                            }
                        }
                        else if (assetVirtual instanceof SwcFile)
                        {
                            // See note above on includeVirtual instanceof SwcFile
                            SwcFile swcFile = (SwcFile) assetVirtual;
                            String swcPath = SwcFile.getSwcLocation(swcFile.getName());
                            if (swcPath != null && swcPath.length() > 0)
                            {
                                if (!dependencies.containsKey(swcPath))
                                {
                                    long includeMod = new File(swcPath).lastModified();
                                    includeMod -= includeMod % 1000;
                                    dependencies.put(swcPath, new Long(includeMod));
                                    if (Trace.cache)
                                    {
                                        Trace.trace("[cache] assets = " + swcPath + "; lastmod = " + includeMod);
                                    }
                                }
                            }                                                       
                        }
                        else
                        {
                            if (!assetName.equals(""))
                            {
                                long includeMod = assetVirtual.getLastModified();
                                includeMod -= includeMod % 1000;
                                dependencies.put(assetVirtual.getName(), new Long(includeMod));
                                if (Trace.cache)
                                {
                                    Trace.trace("[cache] assets = " + assetVirtual.getName() + "; lastmod = " + includeMod);
                                }
                            }
                        }
                    }
                }
                catch (MalformedURLException mue)
                {
                    // ignore
                }
            }
        }
    }

    protected void getDependenciesFromConfiguration(ServerConfiguration configuration, HashMap dependencies)
    {
        Map configs = configuration.getFlexConfigConfiguration().getCompilerConfiguration().getServicesDependencies().getConfigPaths();
        Iterator iter = configs.entrySet().iterator();
        while (iter.hasNext())
        {
            Map.Entry entry = (Map.Entry)iter.next();

            long modified = ((Long)entry.getValue()).longValue();
            modified -= modified % 1000;
            dependencies.put(entry.getKey(), new Long(modified));

            if (Trace.cache)
            {
                Trace.trace("[cache] name = " + entry.getKey() + "; lastmod = " + modified);
            }
        }
    }

    protected void keepGenerated(ByteArrayOutputStream swf, File parentPath, File appPath)
    {
        assert parentPath != null;
        assert appPath != null;

        String name = appPath.getName();
        name = name.substring(0, name.lastIndexOf('.'));

        if (swf != null)
        {
            FileOutputStream fos = null;
            try
            {
                fos = new FileOutputStream(FileUtil.openFile(parentPath, name + swfExt));
                swf.writeTo(fos);
            }
            catch (IOException ioe)
            {
                if (fos != null) { try { fos.close(); } catch (Exception e) {} }
            }
        }
    }

    /* This method should return the swf from the cache if it's available.
     * Otherwise, it should recompile the swf and then return it.
     *
     */
    protected byte[] mxmlToSwf(String filename, MxmlContext context) throws Throwable
    {
        CacheHelper ch = CacheHelper.getInstance(context.getServletContext());
        String cacheKey = context.getCacheKey();

        ch.lockCache();

        try
        {
            SWFInfo swfInfo = ch.getFromCache(cacheKey);
            if (swfInfo != null)
            {
                copySwfInfoToContext(context, swfInfo);
                if (Trace.cache)
                {
                    Trace.trace("[cache] swfinfo in content cache up-to-date; cache key = " + cacheKey);
                }
            }

            if (swfInfo == null)
            {
                if (Trace.cache)
                {
                    Trace.trace("[cache] recompile swf; cache key = " + cacheKey);
                }
                context.setLogSwfEvents(true);
                swfInfo = new SWFInfo(context.getDependencyKey(), context.getServletContext(), context.getRequest());
                HashMap dependencies = new HashMap();

                compileMxml(swfInfo, filename, context, dependencies);
                copyContextToSwfInfo(context, swfInfo);
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

    protected void setHtmlWrapperAttributes(Movie movie, MxmlContext context)
    {
        if (movie.userSpecifiedHeight)
        {
            context.setHeight(new Integer(movie.height).toString());
        }
        else if (movie.heightPercent != null)
        {
            context.setHeight(movie.heightPercent);
        }
        else
        {
            context.setHeight("100%");
        }

        if (movie.userSpecifiedWidth)
        {
            context.setWidth(new Integer(movie.width).toString());
        }
        else if (movie.widthPercent != null)
        {
            context.setWidth(movie.widthPercent);
        }
        else
        {
            context.setWidth("100%");
        }

        if (movie.pageTitle != null)
        {
            context.setPageTitle(movie.pageTitle);
        }
        
        if (movie.bgcolor != null)
        {
            context.setBackgroundColor(movie.bgcolor.color);
        }
    }

    protected void copyContextToSwfInfo(MxmlContext context, SWFInfo swfInfo)
    {
        swfInfo.setPageTitle(context.getPageTitle());
        swfInfo.setWidth(context.getWidth());
        swfInfo.setHeight(context.getHeight());
        swfInfo.setBackgroundColor(context.getBackgroundColor());
        swfInfo.setCompileEvents(context.getEvents());
    }

    protected void copySwfInfoToContext(MxmlContext context, SWFInfo swfInfo)
    {
        context.setPageTitle(swfInfo.getPageTitle());
        context.setWidth(swfInfo.getWidth());
        context.setHeight(swfInfo.getHeight());
        context.setBackgroundColor(swfInfo.getBackgroundColor());
        context.setEvents(swfInfo.getCompileEvents());
    }
}
