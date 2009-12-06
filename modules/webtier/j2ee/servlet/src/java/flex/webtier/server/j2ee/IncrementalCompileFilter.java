////////////////////////////////////////////////////////////////////////////////
//
//  ADOBE SYSTEMS INCORPORATED
//  Copyright 2006-2008 Adobe Systems Incorporated
//  All Rights Reserved.
//
//  NOTICE: Adobe permits you to use, modify, and distribute this file
//  in accordance with the terms of the license agreement accompanying it.
//
////////////////////////////////////////////////////////////////////////////////
package flex.webtier.server.j2ee;

import flash.swf.Movie;
import flash.util.FileUtils;
import flex.webtier.services.ServiceFactory;
import flex.webtier.services.config.ServerConfiguration;
import flex.webtier.util.Trace;
import flex2.compiler.CompilerSwcContext;
import flex2.compiler.FileSpec;
import flex2.compiler.ResourceBundlePath;
import flex2.compiler.ResourceContainer;
import flex2.compiler.SourceList;
import flex2.compiler.SourcePath;
import flex2.compiler.SymbolTable;
import flex2.compiler.common.Configuration;
import flex2.compiler.common.CompilerConfiguration;
import flex2.compiler.i18n.I18nUtils;
import flex2.compiler.io.FileUtil;
import flex2.compiler.io.LocalFile;
import flex2.compiler.io.VirtualFile;
import flex2.compiler.swc.SwcCache;
import flex2.compiler.util.NameMappings;
import flex2.compiler.util.ThreadLocalToolkit;
import flex2.tools.PreLink;
import flex2.tools.PostLink;
import java.io.ByteArrayOutputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class IncrementalCompileFilter extends BaseCompileFilter
{
    public static ThreadLocal targetThreadLocal = new ThreadLocal();

    public IncrementalCompileFilter(String swfExt)
    {
        super(swfExt);
    }

    protected SWFInfo compileMxml(SWFInfo swfInfo, String filename, MxmlContext context, HashMap dependencies) throws Exception
    {
        File appPath = FileUtil.openFile(filename);
        if (appPath != null)
        {
            appPath = appPath.getCanonicalFile();
        }

        Target t = (Target)targetThreadLocal.get();

        // create request-specific configuration
        ServerConfiguration configuration = ServiceFactory.getConfigurator().generateConfiguration(context.getRequest(), appPath, dependencies);
        Configuration flexConfig = configuration.getFlexConfigConfiguration();
        CompilerConfiguration compilerConfig = flexConfig.getCompilerConfiguration();

        if (compilerConfig.keepGeneratedActionScript())
        {
            File gendir = new File(FileUtils.addPathComponents(appPath.getParent(), "generated", File.separatorChar));
            gendir.mkdirs();
            compilerConfig.setGeneratedDirectory(gendir.getCanonicalPath());
        }

        Map licenseMap = ServiceFactory.getLicenseService().getConsolidatedLicenseMap(flexConfig);

        flex2.compiler.CompilerAPI.runBenchmark();
        flex2.compiler.CompilerAPI.setupHeadless(flexConfig);

        try
        {
            // Can only do an incremental compile if the license map hasn't changed.
            if (t != null && licenseMap.equals(swfInfo.getLicenseMap()))
            {
                if (Trace.cache)
                {
                    Trace.trace("refresh incremental compile: " + filename);
                }
                t = refreshCompile(t, appPath, configuration, licenseMap);
            }
            else
            {
                t = new Target();

                if (Trace.cache)
                {
                    Trace.trace("full incremental compile: " + filename);
                }
                t = fullCompile(t, appPath, configuration, licenseMap);
            }

            swfInfo.setTarget(t);
            swfInfo.setLicenseMap(licenseMap);
            
            getDependenciesFromCompilationUnits(t.units, dependencies);
            getDependenciesFromConfiguration(configuration, dependencies);

            // after you have the list of dependencies, you don't need these anymore
            t.units = null;

            setHtmlWrapperAttributes(t.movie, context);

            ByteArrayOutputStream bos = encodeSwf(t.movie);
            if (bos != null)
            {
                // save swf buffer
                swfInfo.setSwfBuffer(bos.toByteArray());

                // save swf buffer to disk
                if (configuration.getDebuggingConfiguration().keepGeneratedSwfs())
                {
                    keepGenerated(bos, new File(appPath.getParent()), appPath);
                }
            }
        }
        catch (Exception e)
        {
            swfInfo.setTarget(null);
            throw e;
        }
        finally
        {
            flex2.compiler.CompilerAPI.disableBenchmark();
            targetThreadLocal.set(null);
        }

        return swfInfo;
    }

    protected Target fullCompile(Target t, File appPath, ServerConfiguration configuration, Map licenseMap) throws Exception
    {
        try
        {
            Configuration flexConfig = configuration.getFlexConfigConfiguration();
            CompilerConfiguration compilerConfig = flexConfig.getCompilerConfiguration();

            t.configuration = flexConfig;

            VirtualFile appPathVirtual = new LocalFile(appPath);

            NameMappings mappings = flex2.compiler.CompilerAPI.getNameMappings(flexConfig);
            flex2.compiler.Transcoder[] transcoders = flex2.tools.WebTierAPI.getTranscoders(flexConfig);
            flex2.compiler.SubCompiler[] compilers = flex2.tools.WebTierAPI.getCompilers(compilerConfig, mappings, transcoders);

            // create a FileSpec... can reuse based on appPath, debug settings, etc...
            t.fileSpec = new FileSpec(Collections.EMPTY_LIST, flex2.tools.WebTierAPI.getFileSpecMimeTypes());

            // create a SourcePath...
            VirtualFile[] asClasspath = compilerConfig.getSourcePath();
            List virtualFileList = new ArrayList();
            virtualFileList.add(appPathVirtual);
            t.sourceList = new SourceList(virtualFileList, asClasspath, appPathVirtual, flex2.tools.WebTierAPI.getSourcePathMimeTypes());
            t.sourcePath = new SourcePath(asClasspath, appPathVirtual, flex2.tools.WebTierAPI.getSourcePathMimeTypes(), compilerConfig.allowSourcePathOverlap());

            // create a ResourceContainer...
            t.resources = new ResourceContainer();
            t.bundlePath = new ResourceBundlePath(compilerConfig, appPathVirtual);

            ThreadLocalToolkit.getBenchmark().benchmark("Initial setup:");

            t.swcCache = new SwcCache();
            CompilerSwcContext swcContext = new CompilerSwcContext(true);
            swcContext.load(compilerConfig.getLibraryPath(),
                            compilerConfig.getExternalLibraryPath(),
                            compilerConfig.getThemeFiles(),
                            compilerConfig.getIncludeLibraries(),
                            mappings,
                            I18nUtils.getTranslationFormat(compilerConfig),
                            t.swcCache);
            ThreadLocalToolkit.getBenchmark().benchmark("Loaded " + swcContext.getNumberLoaded() + " SWCs:");
            flexConfig.addExterns(swcContext.getExterns());
            flexConfig.addIncludes(swcContext.getIncludes());
            compilerConfig.addThemeCssFiles(swcContext.getThemeStyleSheets());
            t.checksum = configuration.getChecksum_ts() + swcContext.checksum();

            SymbolTable symbolTable = new SymbolTable(t.configuration);
            
            t.perCompileData = symbolTable.perCompileData;

            List units = flex2.compiler.CompilerAPI.compile(t.fileSpec, t.sourceList, t.sourcePath, t.resources, t.bundlePath,
                                                    swcContext, symbolTable, mappings, flexConfig, compilers,
                                                    new PreLink(), licenseMap);

            t.movie = flex2.linker.LinkerAPI.link(units, new PostLink(t.configuration), t.configuration);
            t.units = units;
            
            t.sourcePath.clearCache();
            t.bundlePath.clearCache();
            t.resources.refresh();

            return t;
        }
        catch (Exception e)
        {
            throw e;
        }
        finally
        {
            ThreadLocalToolkit.getBenchmark().totalTime();
            ThreadLocalToolkit.getBenchmark().peakMemoryUsage(true);
        }
    }

    private Target refreshCompile(Target t, File appPath, ServerConfiguration configuration, Map licenseMap) throws Exception
    {
        try
        {
            Configuration flexConfig = configuration.getFlexConfigConfiguration();
            CompilerConfiguration compilerConfig = flexConfig.getCompilerConfiguration();

            t.sourcePath.clearCache();
            t.bundlePath.clearCache();
            t.resources.refresh();

            NameMappings mappings = flex2.compiler.CompilerAPI.getNameMappings(flexConfig);
            flex2.compiler.Transcoder[] transcoders = flex2.tools.WebTierAPI.getTranscoders(flexConfig);
            flex2.compiler.SubCompiler[] compilers = flex2.tools.WebTierAPI.getCompilers(compilerConfig, mappings, transcoders);

            ThreadLocalToolkit.getBenchmark().benchmark("Initial setup:");

            CompilerSwcContext swcContext = new CompilerSwcContext(true);
            swcContext.load(compilerConfig.getLibraryPath(),
                            compilerConfig.getExternalLibraryPath(),
                            compilerConfig.getThemeFiles(),
                            compilerConfig.getIncludeLibraries(),
                            mappings,
                            I18nUtils.getTranslationFormat(compilerConfig),
                            t.swcCache);
            ThreadLocalToolkit.getBenchmark().benchmark("Loaded " + swcContext.getNumberLoaded() + " SWCs:");
            flexConfig.addExterns(swcContext.getExterns());
            flexConfig.addIncludes(swcContext.getIncludes());
            compilerConfig.addThemeCssFiles(swcContext.getThemeStyleSheets());

            boolean recompile = false;
            int newChecksum = configuration.getChecksum_ts() + swcContext.checksum();
            if (newChecksum != t.checksum)
            {
                ThreadLocalToolkit.logInfo("Detected configuration changes. Recompile...");
                t.checksum = newChecksum;
                t.resources = new ResourceContainer();
                recompile = true;                
            }
            
            // Flex 3 Version of this API
            if (flex2.compiler.CompilerAPI.validateCompilationUnits(t.fileSpec, t.sourceList, t.sourcePath, t.bundlePath, t.resources, swcContext, t.perCompileData, recompile, flexConfig) > 0)
            {
                t.configuration = flexConfig;
                SymbolTable symbolTable = new SymbolTable(t.configuration, t.perCompileData);

                List units = flex2.compiler.CompilerAPI.compile(t.fileSpec, t.sourceList, t.sourcePath, t.resources,
                                                        t.bundlePath, swcContext, symbolTable, mappings, t.configuration, compilers,
                                                        new PreLink(), licenseMap);

                assert(units != null) : "recompile = " + recompile + "; please log a bug.";

                // until we've uncovered the intermittent edge cases where the above assert code occur
                // allow for recovery by doing a fullCompile
                if (units == null)
                {
                    return fullCompile(new Target(), appPath, configuration, licenseMap);
                }

                t.movie = flex2.linker.LinkerAPI.link(units, new PostLink(t.configuration), t.configuration);
                t.units = units;
                
                t.sourcePath.clearCache();
                t.bundlePath.clearCache();
                t.resources.refresh();
            }
            else
            {
                assert(false) : "Units should have been compiled but weren't identified. Please log a bug.";
                return fullCompile(new Target(), appPath, configuration, licenseMap);
            }

            return t;

        }
        finally
        {
            ThreadLocalToolkit.getBenchmark().totalTime();
            ThreadLocalToolkit.getBenchmark().peakMemoryUsage(true);
        }
    }

    protected ByteArrayOutputStream encodeSwf(Movie movie)
    {
        ByteArrayOutputStream bos = null;
        OutputStream swfOut = null;
        try
        {
            bos = new ByteArrayOutputStream();
            swfOut = new BufferedOutputStream(bos);
            // C: encode movie...
            flex2.compiler.CompilerAPI.encode(movie, swfOut);
            swfOut.flush();
        }
        catch (IOException ioe)
        {
            if (swfOut != null) { try { swfOut.close(); } catch (Exception e) {} }
        }
        return bos;
    }

}

