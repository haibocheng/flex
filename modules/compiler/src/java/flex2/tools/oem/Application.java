////////////////////////////////////////////////////////////////////////////////
//
//  ADOBE SYSTEMS INCORPORATED
//  Copyright 2006-2007 Adobe Systems Incorporated
//  All Rights Reserved.
//
//  NOTICE: Adobe permits you to use, modify, and distribute this file
//  in accordance with the terms of the license agreement accompanying it.
//
////////////////////////////////////////////////////////////////////////////////

package flex2.tools.oem;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import flex2.compiler.CompilerAPI;
import flex2.compiler.CompilationUnit;
import flex2.compiler.CompilerException;
import flex2.compiler.CompilerSwcContext;
import flex2.compiler.FileSpec;
import flex2.compiler.LicenseException;
import flex2.compiler.ResourceBundlePath;
import flex2.compiler.ResourceContainer;
import flex2.compiler.Source;
import flex2.compiler.SourceList;
import flex2.compiler.SourcePath;
import flex2.compiler.SubCompiler;
import flex2.compiler.SymbolTable;
import flex2.compiler.Transcoder;
import flex2.compiler.common.CompilerConfiguration;
import flex2.compiler.common.FontsConfiguration;
import flex2.compiler.config.ConfigurationException;
import flex2.compiler.extensions.ExtensionManager;
import flex2.compiler.extensions.IApplicationExtension;
import flex2.compiler.i18n.I18nUtils;
import flex2.compiler.io.FileUtil;
import flex2.compiler.io.LocalFile;
import flex2.compiler.io.VirtualFile;
import flex2.compiler.swc.SwcCache;
import flex2.compiler.swc.SwcException;
import flex2.compiler.util.Benchmark;
import flex2.compiler.util.CompilerControl;
import flex2.compiler.util.MimeMappings;
import flex2.compiler.util.NameMappings;
import flex2.compiler.util.PerformanceData;
import flex2.compiler.util.QName;
import flex2.compiler.util.ThreadLocalToolkit;
import flex2.linker.LinkerAPI;
import flex2.linker.ConsoleApplication;
import flex2.linker.FlexMovie;
import flex2.linker.LinkerException;
import flex2.linker.SimpleMovie;
import flex2.tools.Mxmlc;
import flex2.tools.PostLink;
import flex2.tools.PreLink;
import flex2.tools.ToolsConfiguration;
import flex2.tools.WebTierAPI;
import flex2.tools.oem.internal.ApplicationCompilerConfiguration;
import flex2.tools.oem.internal.ApplicationData;
import flex2.tools.oem.internal.OEMConfiguration;
import flex2.tools.oem.internal.OEMReport;
import flex2.tools.oem.internal.OEMUtil;


/**
 * The <code>Application</code> class represents a Flex application. It implements the <code>Builder</code> interface
 * which allows for building the application incrementally. There are many ways to define
 * a Flex application. The most common way is specify the location of the target source file
 * on disk:
 *
 * <pre>
 * Application app = new Application(new File("MyApp.mxml"));
 * </pre>
 *
 * Before the <code>Application</code> object starts the compilation, it must be configured. The most common methods that the client
 * calls are <code>setLogger()</code>, <code>setConfiguration()</code>, and <code>setOutput()</code>.
 *
 * A logger must implement <code>flex2.tools.oem.Logger</code> and use the implementation as the Logger
 * for the compilation. The following is an example <code>Logger</code> implementation:
 *
 * <pre>
 * app.setLogger(new flex2.tools.oem.Logger()
 * {
 *     public void log(Message message, int errorCode, String source)
 *     {
 *         System.out.println(message);
 *     }
 * });
 * </pre>
 *
 * To specify compiler options for the <code>Application</code> object, the client
 * must get a <code>Configuration</code> object populated with default values. Then, the client can set
 * compiler options programmatically.
 *
 * The <code>setOutput()</code> method lets clients specify where the <code>Application</code> object should write
 * the output to. If you call the <code>setOutput()</code> method, the <code>build(boolean)</code> method builds and
 * writes directly to the location specified by the <code>setOutput()</code> method. For example:
 *
 * <pre>
 * app.setOutput(new File("MyApp.swf"));
 * app.build(true);
 * </pre>
 *
 * If you do not call the <code>setOutput()</code> method, the client can use the <code>build(OutputStream, boolean)</code> method
 * which requires the client to provide a buffered output stream. For example:
 *
 * <pre>
 * app.build(new BufferedOutputStream(new FileOutputStream("MyApp.swf")), true);
 * </pre>
 *
 * Before the <code>Application</code> object is thrown away, it is possible to save the compilation
 * data for reuse by using the <code>save(OutputStream)</code> method. Subsequent compilations can use the
 * <code>load(OutputStream)</code> method to get the old data into the <code>Application</code> object.
 *
 * <pre>
 * app.save(new BufferedOutputStream(new FileOutputStream("MyApp.incr")));
 * </pre>
 *
 * When a cache file (such as <code>MyApp.incr</code>) is available from a previous compilation, the client can
 * call the <code>load(OutputStream)</code> method before calling the <code>build(boolean)</code> method. For example:
 *
 * <pre>
 * app.load(new BufferedInputStream(FileInputStream("MyApp.incr")));
 * app.build();
 * </pre>
 *
 * The <code>build(false)</code> and <code>build(OutputStream, false)</code> methods always rebuild the application. If the <code>Application</code>
 * object is new, the first <code>build(true)/build(OutputStream, true)</code> method call performs a full build, which
 * is equivalent to <code>build(false)/build(OutputStream, false)</code>, respectively. After a call to the <code>clean()</code> method,
 * the <code>Application</code> object always performs a full build.
 *
 * <p>
 * The <code>clean()</code> method not only cleans up compilation data in the <code>Application</code> object, but also the output
 * file if the <code>setOutput()</code> method was called.
 *
 * <p>
 * The <code>Application</code> class also supports building applications from a combination of source
 * files from the file system and in-memory, dynamically-generated source objects. The client
 * must use the <code>Application(String, VirtualLocalFile)</code> or <code>Application(String, VirtualLocalFile[])</code> constructors.
 *
 * <p>
 * The <code>Application</code> class can be part of a <code>Project</code>. For more information, see the <code>Project</code> class's description.
 *
 * @see flex2.tools.oem.Configuration
 * @see flex2.tools.oem.Project
 * @version 2.0.1
 * @author Clement Wong
 */
public class Application implements Builder
{
    static
    {
        // find all the compiler temp files.
        File[] list = null;
        try
        {
            File tempDir = File.createTempFile("Flex2_", "").getParentFile();
            list = tempDir.listFiles(new FilenameFilter()
            {
                public boolean accept(File dir, String name)
                {
                    return name.startsWith("Flex2_");
                }
            });
        }
        catch (Throwable e)
        {
        }

        // get rid of compiler temp files.
        for (int i = 0, len = list == null ? 0 : list.length; i < len; i++)
        {
            try { list[i].delete(); } catch (Throwable t) {}
        }

        // use the protection domain to find the location of flex-compiler-oem.jar.
        URL url = Application.class.getProtectionDomain().getCodeSource().getLocation();
        try
        {
            File f = new File(new URI(url.toExternalForm()));
            if (f.getAbsolutePath().endsWith("flex-compiler-oem.jar"))
            {
                // use the location of flex-compiler-oem.jar to set application.home
                // assume that the jar file is in <application.home>/lib/flex-compiler-oem.jar
                String applicationHome = f.getParentFile().getParent();
                System.setProperty("application.home", applicationHome);
            }
        }
        catch (URISyntaxException ex)
        {
        }
        catch (IllegalArgumentException ex)
        {
        }
    }

    /**
     * Constructor.
     *
     * @param file The target source file.
     * @throws FileNotFoundException Thrown when the specified source file does not exist.
     */
    public Application(File file) throws FileNotFoundException
    {
        this(file, null);
    }

    /**
     * Constructor.
     *
     * @param file The target source file.
     * @param cache A reference to a LibraryCache object. After building this Application
     * object the cache may be saved and used to compile another Application object
     * that uses the same library path.
     * @throws FileNotFoundException Thrown when the specified source file does not exist.
     * @since 3.0
     */
    public Application(File file, LibraryCache cache) throws FileNotFoundException
    {
        if (file.exists())
        {
            init(new VirtualFile[] { new LocalFile(FileUtil.getCanonicalFile(file)) });
        }
        else
        {
            throw new FileNotFoundException(FileUtil.getCanonicalPath(file));
        }

        librarySwcCache = cache;
    }

    /**
     * Constructor.
     *
     * @param file An in-memory source object.
     */
    public Application(VirtualLocalFile file)
    {
        init(new VirtualFile[] { file });
    }

    /**
     * Constructor.
     *
     * @param files An array of in-memory source objects. The last element in the array is the target source object.
     */
    public Application(VirtualLocalFile[] files)
    {
        init(files);
    }

    /**
     * Constructor.  Use to build resource modules which don't have a target
     * source file.
     *
     */
    public Application()
    {
         init(new VirtualFile[0]);
    }

    /**
     *
     * @param files
     */
    private void init(VirtualFile[] files)
    {
        this.files = new ArrayList(files.length);
        for (int i = 0, length = files.length; i < length; i++)
        {
            this.files.add(files[i]);
        }
        oemConfiguration = null;
        logger = null;
        output = null;
        mimeMappings = new MimeMappings();
        meter = null;
        resolver = null;
        cc = new CompilerControl();
        isGeneratedTargetFile = false;

        data = null;
        cacheName = null;
        configurationReport = null;
        messages = new ArrayList<Message>();
    }

    private List<VirtualFile> files;
    private OEMConfiguration oemConfiguration;
    private Logger logger;
    private File output;
    private MimeMappings mimeMappings;
    private ProgressMeter meter;
    protected PathResolver resolver;
    private CompilerControl cc;
    private LibraryCache librarySwcCache;
    private boolean isGeneratedTargetFile;

    // clean() would null out the following variables.
    private ApplicationData data;
    private String cacheName, configurationReport;
    private List<Message> messages;
    private HashMap<String, PerformanceData[]> compilerBenchmarks;
    private Benchmark benchmark;

    /**
     * @inheritDoc
     */
    public void setConfiguration(Configuration configuration)
    {
        oemConfiguration = (OEMConfiguration) configuration;
    }

    /**
     * @inheritDoc
     */
    public Configuration getDefaultConfiguration()
    {
        return getDefaultConfiguration(false);
    }

    /**
     *
     * @param processDefaults
     * @return
     */
    private Configuration getDefaultConfiguration(boolean processDefaults)
    {
        return OEMUtil.getApplicationConfiguration(constructCommandLine(null), false,
                                                   OEMUtil.getLogger(logger, messages), resolver,
                                                   mimeMappings, processDefaults);
    }

    /**
     * @inheritDoc
     */
    public Map<String, PerformanceData[]> getCompilerBenchmarks()
    {
        return compilerBenchmarks;
    }

    /**
     * @inheritDoc
     */
    public Benchmark getBenchmark()
    {
        return benchmark;
    }

    /**
     * @inheritDoc
     */
    public Configuration getConfiguration()
    {
        return oemConfiguration;
    }

    /**
     * @inheritDoc
     */
    public void setLogger(Logger logger)
    {
        this.logger = logger;
    }

    /**
     * @inheritDoc
     */
    public Logger getLogger()
    {
        return logger;
    }

    /**
     * Sets the location of the compiler's output. This method is necessary if you call the <code>build(boolean)</code> method.
     * If you use the <code>build(OutputStream, boolean)</code> method, in which an output stream
     * is provided, there is no need to use this method.
     *
     * @param output An instance of the <code>java.io.File</code> class.
     */
    public void setOutput(File output)
    {
        this.output = output;
    }

    /**
     * Gets the output destination. This method returns <code>null</code> if the <code>setOutput()</code> method was not called.
     *
     * @return An instance of the <code>java.io.File</code> class, or <code>null</code> if you did not call the <code>setOutput()</code> method.
     */
    public File getOutput()
    {
        return output;
    }

    /**
     * @inheritDoc
     */
    public void setSupportedFileExtensions(String mimeType, String[] extensions)
    {
        mimeMappings.set(mimeType, extensions);
    }

    /**
     * @inheritDoc
     */
    public void setProgressMeter(ProgressMeter meter)
    {
        this.meter = meter;
    }

    /**
     * @inheritDoc
     * @since 3.0
     */
    public void setPathResolver(PathResolver resolver)
    {
        this.resolver = resolver;
    }

    /**
     * @inheritDoc
     */
    // IMPORTANT: If you make changes here, you probably want to mirror them in Library.build()
    public long build(boolean incremental) throws IOException
    {
        if (output != null)
        {
            InputStream tempIn = null;
            ByteArrayOutputStream tempOut = null;
            OutputStream out = null;
            long size = 0;

            //TODO PERFORMANCE: A lot of unnecessary recopying and buffering here
            try
            {
                int result = compile(incremental);

                if (result == SKIP || result == LINK || result == OK)
                {
                    // write to a temp buffer...
                    tempOut = new ByteArrayOutputStream();
                    size = (result == OK || result == LINK) ? link(tempOut) : encode(tempOut);
                    tempOut.flush();

                    if (size > 0)
                    {
                        tempIn = new ByteArrayInputStream(tempOut.toByteArray());
                        out = new BufferedOutputStream(new FileOutputStream(output));
                        FileUtil.streamOutput(tempIn, out);
                    }
                }

                return size;
            }
            finally
            {
                if (tempIn != null) { try { tempIn.close(); } catch (Exception ex) {} }
                if (tempOut != null) { try { tempOut.close(); } catch (Exception ex) {} }
                if (out != null) { try { out.close(); } catch (Exception ex) {} }

                if ((benchmark != null) && benchmark.hasStarted(Benchmark.POSTCOMPILE))
                {
                    benchmark.stopTime(Benchmark.POSTCOMPILE, false);
                }

                runExtensions();  
                
                clean(false, false, false, true, false, true);
            }
        }
        else
        {
            return 0;
        }
    }

    private void runExtensions()
    {
        if (oemConfiguration != null)
        {
            Set<IApplicationExtension> extensions = ExtensionManager.getApplicationExtensions(oemConfiguration.getExtensions());

            for ( IApplicationExtension extension : extensions )
            {
                if (ThreadLocalToolkit.errorCount() == 0)
                {
                    extension.run( (Configuration) oemConfiguration.clone() );
                }
            }
        }
    }

    /**
     * @inheritDoc
     */
    public long build(OutputStream out, boolean incremental) throws IOException
    {
        try
        {
            int result = compile(incremental);
            if (result == OK || result == LINK)
            {
                runExtensions();
                return link(out);
            }
            else if (result == SKIP)
            {
                runExtensions();
                return encode(out);
            }
            else
            {
                return 0;
            }
        }
        finally
        {
            if ((benchmark != null) && benchmark.hasStarted(Benchmark.POSTCOMPILE))
            {
                benchmark.stopTime(Benchmark.POSTCOMPILE, false);
            }
            
            clean(false, false, false, true, false, true);
        }
    }

    /**
     * @inheritDoc
     */
    public void stop()
    {
        cc.stop();
    }

    /**
     * @inheritDoc
     */
    public void clean()
    {
        clean(true, true, true, true, true, true);
    }

    /**
     * @inheritDoc
     */
    public void load(InputStream in) throws IOException
    {
        cacheName = OEMUtil.load(in, cacheName);
        clean(true, false, false);
    }

    /**
     * @inheritDoc
     */
    public long save(OutputStream out) throws IOException
    {
        return OEMUtil.save(out, cacheName, data);
    }

    /**
     * @inheritDoc
     */
    public Report getReport()
    {
        OEMUtil.setupLocalizationManager();
        return new OEMReport(data == null ? null : data.sources,
                             data == null ? null : data.movie,
                             data == null ? null : data.configuration,
                             configurationReport,
                             messages);
    }

    private void setupFontManager(OEMConfiguration localOEMConfiguration)
    {
        if (localOEMConfiguration != null && data != null)
        {
            FontsConfiguration fontsConfiguration = localOEMConfiguration.configuration.getCompilerConfiguration().getFontsConfiguration();
            fontsConfiguration.setTopLevelManager(data.fontManager);
        }
    }

    /**
     * @param configuration
     * @return true, unless a CompilerException occurs.
     */
    private boolean setupSourceContainers(OEMConfiguration localOEMConfiguration)
    {
        ToolsConfiguration configuration = localOEMConfiguration.configuration;
        CompilerConfiguration compilerConfig = configuration.getCompilerConfiguration();
        VirtualFile[] asClasspath = compilerConfig.getSourcePath();
        boolean result = false;

        try
        {
            // If there are no files that means this is a resource module and this
            // is the first time compiling the module.  When the
            // ApplicationCompilerConfiguration was generated the validate() would
            // have failed if there were no source files and no included resource
            // bundles.  See ApplicationCompilerConfiguration.getTargetFile() to
            // see how the resource module is initially generated.
            if (files.size() == 0)
            {
                ApplicationCompilerConfiguration acc = (ApplicationCompilerConfiguration)localOEMConfiguration.configuration;
                files.add(CompilerAPI.getVirtualFile(acc.getTargetFile(), true));
                isGeneratedTargetFile = true;
            }
            else if (isGeneratedTargetFile)
            {
                // The resource module file has already been generated but we need to
                // regenerate it to do a fresh compile.  This file is impacted if
                // either the locales or bundleNames have changed.
                I18nUtils.regenerateResourceModule((ApplicationCompilerConfiguration)localOEMConfiguration.configuration);
            }

            VirtualFile targetFile = (VirtualFile) files.get(files.size() - 1);

            WebTierAPI.checkSupportedTargetMimeType(targetFile);

            // create a FileSpec...
            data.fileSpec = new FileSpec(Collections.<VirtualFile>emptyList(), WebTierAPI.getFileSpecMimeTypes());

            // create a SourceList
            data.sourceList = new SourceList(files, asClasspath, targetFile, WebTierAPI.getSourcePathMimeTypes());

            // create a SourcePath...
            data.sourcePath = new SourcePath(asClasspath, targetFile, WebTierAPI.getSourcePathMimeTypes(),
                                             compilerConfig.allowSourcePathOverlap());

            // create a ResourceContainer...
            data.resources = new ResourceContainer();

            // create a ResourceBundlePath...
            data.bundlePath = new ResourceBundlePath(compilerConfig, targetFile);

            // clear these...
            if (data.sources != null) data.sources.clear();
            if (data.units != null) data.units.clear();
            if (data.swcDefSignatureChecksums != null) data.swcDefSignatureChecksums.clear();
            if (data.swcFileChecksums != null) data.swcFileChecksums.clear();

            result = true;
        }
        catch (CompilerException ex)
        {
            assert ThreadLocalToolkit.errorCount() > 0;
        }
        catch (ConfigurationException e)
        {
            ThreadLocalToolkit.logInfo(e.getMessage());
        }

        return result;
    }

    /**
     * @param configuration
     * @return true, unless an IOException occurs and the source containers can't be setup.
     */
    private boolean loadCompilationUnits(OEMConfiguration localOEMConfiguration, CompilerSwcContext swcContext, int[] checksums)
    {
        ToolsConfiguration configuration = localOEMConfiguration.configuration;

        if (data.cacheName == null) // note: NOT (cacheName == null)
        {
            return true;
        }

        RandomAccessFile cacheFile = null;

        try
        {
            cacheFile = new RandomAccessFile(data.cacheName, "r");
            CompilerAPI.loadCompilationUnits(configuration, data.fileSpec, data.sourceList, data.sourcePath,
                                             data.resources, data.bundlePath, data.sources = new ArrayList<Source>(),
                                             data.units = new ArrayList<CompilationUnit>(), checksums,
                                             data.swcDefSignatureChecksums = new HashMap<QName, Long>(),
                                             data.swcFileChecksums = new HashMap<String, Long>(),
                                             cacheFile, data.cacheName, data.fontManager);
            /*for (int i = 0, size = data.sources.size(); i < size; i++)
            {
                Object obj = data.sources.get(i);
                if (obj instanceof String)
                {
                    String name = (String) obj;
                    Source s = swcContext.getSource(name);
                    data.sources.set(i, s);
                    data.units.set(i, s != null ? s.getCompilationUnit() : null);
                }
            }
            */
        }
        catch (FileNotFoundException ex)
        {
            ThreadLocalToolkit.logInfo(ex.getMessage());
        }
        catch (IOException ex)
        {
            ThreadLocalToolkit.logInfo(ex.getMessage());

            if (!setupSourceContainers(localOEMConfiguration))
            {
                return false;
            }
        }
        finally
        {
            if (cacheFile != null) try { cacheFile.close(); } catch (IOException ex) {}
        }

        return true;
    }

    /**
     * Compiles the <code>Application</code> object. This method does not link the <code>Application</code>.
     *
     * @param incremental If <code>true</code>, build incrementally; if <code>false</code>, rebuild.
     * @return  {@link Builder#OK} if this method call resulted in compilation of some/all parts of the application;
     *          {@link Builder#LINK} if this method call did not compile anything in the application but advise the caller to link again;
     *          {@link Builder#SKIP} if this method call did not compile anything in the application;
     *          {@link Builder#FAIL} if this method call encountered errors during compilation.
     */
    protected int compile(boolean incremental)
    {
        messages.clear();

        // if there is no configuration, use the default... but don't populate this.configuration.
        OEMConfiguration tempOEMConfiguration;
        if (oemConfiguration == null)
        {
            tempOEMConfiguration = (OEMConfiguration) getDefaultConfiguration(true);
        }
        else
        {
            tempOEMConfiguration = OEMUtil.getApplicationConfiguration(constructCommandLine(oemConfiguration),
                                                                       oemConfiguration.keepLinkReport(),
                                                                       OEMUtil.getLogger(logger, messages),
                                                                       resolver, mimeMappings);
        }

        // if c is null, which indicates problems, this method will return.
        if (tempOEMConfiguration == null)
        {
            clean(false, false, false);
            return FAIL;
        }
        else if (oemConfiguration != null && oemConfiguration.keepConfigurationReport())
        {
            configurationReport = OEMUtil.formatConfigurationBuffer(tempOEMConfiguration.cfgbuf);
        }

        setupFontManager(tempOEMConfiguration);

        if (oemConfiguration != null)
        {
            oemConfiguration.cfgbuf = tempOEMConfiguration.cfgbuf;
        }

        if (tempOEMConfiguration.configuration.benchmark())
        {
            benchmark = CompilerAPI.runBenchmark();
            benchmark.setTimeFilter(tempOEMConfiguration.configuration.getBenchmarkTimeFilter());
            benchmark.startTime(Benchmark.PRECOMPILE);
        }
        else
        {
            CompilerAPI.disableBenchmark();
        }

        // initialize some ThreadLocal variables...
        cc.run();
        OEMUtil.init(OEMUtil.getLogger(logger, messages), mimeMappings, meter, resolver, cc);

        Map licenseMap = OEMUtil.getLicenseMap(tempOEMConfiguration.configuration);

        if (data == null || !incremental)
        {
            String compilationType = (cacheName != null) ? "inactive" : "full";

            if (benchmark != null)
            {
                benchmark.benchmark2("Starting " + compilationType + " compile for " + getOutput(), true);
            }

            int returnValue = recompile(false, licenseMap, tempOEMConfiguration);

            if (benchmark != null)
            {
                benchmark.benchmark2("Ending " + compilationType + " compile for " + getOutput(), true);
            }

            clean(returnValue != OK, false, false);
            return returnValue;
        }

        CompilerAPI.setupHeadless(tempOEMConfiguration.configuration);

        CompilerConfiguration compilerConfig = tempOEMConfiguration.configuration.getCompilerConfiguration();
        NameMappings mappings = CompilerAPI.getNameMappings(tempOEMConfiguration.configuration);

        Transcoder[] transcoders = WebTierAPI.getTranscoders(tempOEMConfiguration.configuration);
        SubCompiler[] compilers = WebTierAPI.getCompilers(compilerConfig, mappings, transcoders);

        CompilerSwcContext swcContext = new CompilerSwcContext(true);
        try
        {
            swcContext.load( compilerConfig.getLibraryPath(),
                             flex2.compiler.common.Configuration.getAllExcludedLibraries(compilerConfig, tempOEMConfiguration.configuration),
                             compilerConfig.getThemeFiles(),
                             compilerConfig.getIncludeLibraries(),
                             mappings,
                             I18nUtils.getTranslationFormat(compilerConfig),
                             data.swcCache );
        }
        catch (SwcException ex)
        {
            clean(false, false, false);
            return FAIL;
        }

        // save the generated cache if the caller provided a librarySwcCache.
        if (librarySwcCache != null &&
            librarySwcCache.getSwcCache() != data.swcCache)
        {
            librarySwcCache.setSwcCache(data.swcCache);
        }

        data.includes = new HashSet<String>(swcContext.getIncludes());
        data.excludes = new HashSet<String>(swcContext.getExterns());
        tempOEMConfiguration.configuration.addExterns( swcContext.getExterns() );
        tempOEMConfiguration.configuration.addIncludes( swcContext.getIncludes() );
        tempOEMConfiguration.configuration.getCompilerConfiguration().addThemeCssFiles( swcContext.getThemeStyleSheets() );

        // recompile or incrementally compile...
        if (OEMUtil.isRecompilationNeeded(data, swcContext, tempOEMConfiguration))
        {
            data.resources = new ResourceContainer();

            if (benchmark != null)
            {
                benchmark.benchmark2("Starting full compile for " + getOutput(), true);
            }

            clean(true, false, false, true, false, false);
            int returnValue = recompile(true, licenseMap, tempOEMConfiguration);

            if (benchmark != null)
            {
                benchmark.benchmark2("Ending full compile for " + getOutput(), true);
            }

            clean(returnValue != OK, false, false);
            return returnValue;
        }

        if (benchmark != null)
        {
            // We aren't really starting the compile here, but it's
            // the earliest that we know that it's going to be an
            // active compilation.
            benchmark.benchmark2("Starting active compile for " + getOutput(), true);
        }

        data.sourcePath.clearCache();
        data.bundlePath.clearCache();
        data.resources.refresh();

        // validate CompilationUnits
        final int count = CompilerAPI.validateCompilationUnits(data.fileSpec, data.sourceList, data.sourcePath, data.bundlePath,
                                                               data.resources, swcContext, data.perCompileData, false, tempOEMConfiguration.configuration);

        if ((count > 0) || (data.swcChecksum != swcContext.checksum()))
        {
            data.configuration = tempOEMConfiguration.configuration;
            data.linkChecksum = tempOEMConfiguration.cfgbuf.link_checksum_ts();
            data.swcChecksum = swcContext.checksum();

            // create a symbol table
            SymbolTable symbolTable = new SymbolTable(data.perCompileData);

            data.sources = new ArrayList<Source>();
            data.units = compile(compilers, swcContext, symbolTable, mappings, licenseMap, data.sources, tempOEMConfiguration);

            boolean forcedToStop = CompilerAPI.forcedToStop();
            if (data.units == null || forcedToStop)
            {
                data.sources = null;
            }

            if (benchmark != null)
            {
                benchmark.benchmark2("Ending active compile for " + getOutput(), true);
            }

            clean(false, false, false);

            return (data.units != null && !forcedToStop) ? OK : FAIL;
        }
        else
        {
            if (benchmark != null)
            {
                benchmark.stopTime(Benchmark.PRECOMPILE, false);
                benchmark.startTime(Benchmark.POSTCOMPILE);
            }

            int retVal = SKIP;
            if (data != null)
            {
                CompilerAPI.displayWarnings(data.units);
                if (data.linkChecksum != tempOEMConfiguration.cfgbuf.link_checksum_ts())
                {
                    retVal = LINK;
                }
            }
            else
            {
                retVal = LINK;
            }
            data.linkChecksum = tempOEMConfiguration.cfgbuf.link_checksum_ts();
            data.swcChecksum = swcContext.checksum();

            if (benchmark != null)
            {
                benchmark.benchmark2("Ending active compile for " + getOutput(), true);
            }

            if (CompilerAPI.forcedToStop()) retVal = FAIL;
            if (retVal == LINK)
            {
                clean(false, false, false, false, false, true);
            }
            else
            {
                clean(false, false, false);
            }

            return retVal;
        }
    }

    /**
     * @param fullRecompile if true a full recompile is needed, do not attempted to use cache file.
     *
     * @return  {@link Builder#OK} if this method call resulted in compilation of some/all parts of the application;
     *          {@link Builder#LINK} if this method call did not compile anything in the application but advise the caller to link again;
     *          {@link Builder#SKIP} if this method call did not compile anything in the application;
     *          {@link Builder#FAIL} if this method call encountered errors during compilation.
     */
    private int recompile(boolean fullRecompile, Map licenseMap, OEMConfiguration localOEMConfiguration)
    {
        data = new ApplicationData();
        data.configuration = localOEMConfiguration.configuration;
        data.cacheName = cacheName;

        CompilerAPI.setupHeadless(localOEMConfiguration.configuration);

        CompilerConfiguration compilerConfig = localOEMConfiguration.configuration.getCompilerConfiguration();
        NameMappings mappings = CompilerAPI.getNameMappings(localOEMConfiguration.configuration);
        data.fontManager = compilerConfig.getFontsConfiguration().getTopLevelManager();

        if (output != null)
        {
            OEMUtil.setGeneratedDirectory(compilerConfig, output);
        }

        Transcoder[] transcoders = WebTierAPI.getTranscoders(localOEMConfiguration.configuration);
        SubCompiler[] compilers = WebTierAPI.getCompilers(compilerConfig, mappings, transcoders);

        // NOT in compile
        if (!setupSourceContainers(localOEMConfiguration))
        {
            clean(true, false, false);
            return FAIL;
        }

        // load SWCs
        if (librarySwcCache != null)
        {
            data.swcCache = librarySwcCache.getSwcCache();
            data.perCompileData = librarySwcCache.getContextStatics();

            if (data.perCompileData != null)
            {
                data.perCompileData.clearUserDefined();
            }
        }

        if (data.swcCache == null)
        {
            data.swcCache = new SwcCache();
        }

        CompilerSwcContext swcContext = new CompilerSwcContext(true);
        try
        {
            swcContext.load( compilerConfig.getLibraryPath(),
                             flex2.compiler.common.Configuration.getAllExcludedLibraries(compilerConfig, localOEMConfiguration.configuration),
                             compilerConfig.getThemeFiles(),
                             compilerConfig.getIncludeLibraries(),
                             mappings,
                             I18nUtils.getTranslationFormat(compilerConfig),
                             data.swcCache );
        }
        catch (SwcException ex)
        {
            clean(false, false, false);
            return FAIL;
        }

        // save the generated swcCache if the class has a librarySwcCache.
        if (librarySwcCache != null &&
            librarySwcCache.getSwcCache() != data.swcCache)
        {
            librarySwcCache.setSwcCache(data.swcCache);
        }

        data.includes = new HashSet<String>(swcContext.getIncludes());
        data.excludes = new HashSet<String>(swcContext.getExterns());
        localOEMConfiguration.configuration.addExterns( swcContext.getExterns() );
        localOEMConfiguration.configuration.addIncludes( swcContext.getIncludes() );
        localOEMConfiguration.configuration.getCompilerConfiguration().addThemeCssFiles( swcContext.getThemeStyleSheets() );

        data.cmdChecksum = localOEMConfiguration.cfgbuf.checksum_ts(); // OEMUtil.calculateChecksum(data, swcContext, c);
        data.linkChecksum = localOEMConfiguration.cfgbuf.link_checksum_ts();
        data.swcChecksum = swcContext.checksum();
        int[] checksums = new int[] { 0, data.cmdChecksum, data.linkChecksum, data.swcChecksum };

        // C: must do loadCompilationUnits() after checksum calculation...
        if (!fullRecompile)
        {
            if (!loadCompilationUnits(localOEMConfiguration, swcContext, checksums))
            {
                clean(true, false, false);
                return FAIL;
            }

            data.checksum = checksums[0];
            if (data.units != null &&
                data.units.size() > 0 &&
                OEMUtil.isRecompilationNeeded(data, swcContext, localOEMConfiguration))
            {
                if (!setupSourceContainers(localOEMConfiguration))
                {
                    clean(true, false, false);
                    return FAIL;
                }
            }
        }

        // validate CompilationUnits...
        int count = CompilerAPI.validateCompilationUnits(data.fileSpec, data.sourceList,
                                                         data.sourcePath, data.bundlePath,
                                                         data.resources, swcContext,
                                                         data.perCompileData, false,
                                                         localOEMConfiguration.configuration);

        /*
        if (data.cmdChecksum == checksums[1] &&
            data.linkChecksum == checksums[2] &&
            data.swcChecksum == checksums[3] &&
            data.sources != null &&
            data.units != null &&
            count == 0)
        {
            clean(false, false, false);
            return OK;
        }
        else
        {
            data.sources = null;
            data.units = null;
        }
        */

        SymbolTable symbolTable;

        if (data.perCompileData != null)
        {
            symbolTable = new SymbolTable(data.perCompileData);
        }
        else
        {
            symbolTable = SymbolTable.newSymbolTable(localOEMConfiguration.configuration);
            data.perCompileData = symbolTable.perCompileData;

            if (librarySwcCache != null)
            {
                librarySwcCache.setContextStatics(data.perCompileData);
            }
        }

        data.sources = new ArrayList<Source>();
        data.units = compile(compilers, swcContext, symbolTable, mappings, licenseMap,
                             data.sources, localOEMConfiguration);

        // need to update the checksum here since doing a compile could add some
        // some signature checksums and change it.
        data.checksum = OEMUtil.calculateChecksum(data, swcContext, localOEMConfiguration);

        boolean forcedToStop = CompilerAPI.forcedToStop();

        return (data == null || data.units == null || forcedToStop) ? FAIL : OK;
    }

    /**
     * @param swcContext
     * @param symbolTable
     * @param licenseMap
     * @param sources
     * @param OEMConfig
     * @param isRecompile - true if called as part of a recompile, false if incremental compile.
     *
     * @return a list of CompilationUnit
     */
    private List<CompilationUnit> compile(SubCompiler[] compilers, CompilerSwcContext swcContext,
                                          SymbolTable symbolTable, NameMappings nameMappings,
                                          Map licenseMap, List<Source> sources,
                                          OEMConfiguration OEMConfig)
    {
        List<CompilationUnit> units = null;

        try
        {
            if (benchmark != null)
            {
                for (int i = 0; i < compilers.length; i++)
                {
                    compilers[i].initBenchmarks();
                }
            }

            ApplicationCompilerConfiguration config = (ApplicationCompilerConfiguration) data.configuration;
            VirtualFile projector = config.getProjector();

            if (benchmark != null)
            {
                benchmark.stopTime(Benchmark.PRECOMPILE, false);
            }

            if (projector != null && projector.getName().endsWith("avmplus.exe"))
            {
                units = CompilerAPI.compile(data.fileSpec, data.sourceList, null, data.sourcePath, data.resources, data.bundlePath,
                                                   swcContext, symbolTable, nameMappings, data.configuration, compilers,
                                                   null, licenseMap, sources);
            }
            else
            {
                units = CompilerAPI.compile(data.fileSpec, data.sourceList, null, data.sourcePath, data.resources, data.bundlePath,
                                                   swcContext, symbolTable, nameMappings, data.configuration, compilers,
                                                   new PreLink(), licenseMap, sources);
            }

            if (benchmark != null)
            {
                benchmark.startTime(Benchmark.POSTCOMPILE);
            }

            if ((benchmark != null) && (ThreadLocalToolkit.getLogger() != null))
            {
                if (compilerBenchmarks == null)
                    compilerBenchmarks = new HashMap<String, PerformanceData[]>();

                compilerBenchmarks.clear();

                flex2.compiler.Logger logger = ThreadLocalToolkit.getLogger();
                for (int i = 0; i < compilers.length; i++)
                {
                    SubCompiler compiler = compilers[i];
                    PerformanceData[] times = compiler.getBenchmarks();

                    if (times != null)
                    {
                        compiler.logBenchmarks(logger);
                        String compilerName = compiler.getName();

                        assert(!compilerBenchmarks.containsKey(compilerName));
                        compilerBenchmarks.put(compilerName, times);
                    }

                    // Now check for any embedded compilers and get their phase times.
                    // "synthesize" compiler name by appending _ebm to main compiler name
                    times = compiler.getEmbeddedBenchmarks();
                    if (times != null)
                    {
                        compiler.logBenchmarks(logger);
                        String compilerName = compiler.getName();
                        compilerName += "_emb";

                        assert(!compilerBenchmarks.containsKey(compilerName));
                        compilerBenchmarks.put(compilerName, times);
                    }
                }
            }
        }
        catch (LicenseException ex)
        {
            ThreadLocalToolkit.logError(ex.getMessage());
        }
        catch (CompilerException ex)
        {
            assert ThreadLocalToolkit.errorCount() > 0;
        }
        finally
        {
            data.sourcePath.clearCache();
            data.bundlePath.clearCache();
            data.resources.refresh();

            OEMUtil.saveSwcFileChecksums(swcContext, data, data.configuration);
            OEMUtil.saveSignatureChecksums(units, data, data.configuration);
        }

        return units;
    }

    /**
     * Links the application. This method writes the output to the output stream specified by
     * the client. You should use a buffered output stream for best performance.
     *
     * <p>
     * This method is protected. In most circumstances, the client only needs to call the
     * <code>build()</code> method. Subclasses can call this method so that it links and outputs
     * the application without recompiling.
     *
     * @param out The <code>OutputStream</code>.
     * @return The size of the application, in bytes.
     * @throws IOException Thrown when an I/O error occurs during linking.
     */
    protected long link(OutputStream out) throws IOException
    {
        if (data == null || data.units == null)
        {
            return 0;
        }

        boolean hasChanged = (oemConfiguration == null) ? false : oemConfiguration.hasChanged();
        flex2.compiler.common.Configuration config = null;

        if (hasChanged)
        {
            OEMConfiguration tempOEMConfiguration;
            tempOEMConfiguration = OEMUtil.getLinkerConfiguration(oemConfiguration.getLinkerOptions(),
                                                                  oemConfiguration.keepLinkReport(),
                                                                  OEMUtil.getLogger(logger, messages),
                                                                  mimeMappings, resolver,
                                                                  data.configuration,
                                                                  oemConfiguration.newLinkerOptionsAfterCompile,
                                                                  data.includes, data.excludes);
            if (tempOEMConfiguration == null)
            {
                clean(false, false, false, false, false, true);
                return 0;
            }

            config = tempOEMConfiguration.configuration;
        }
        else
        {
            config = data.configuration;
        }

        long size = 0;

        try
        {
            OEMUtil.init(OEMUtil.getLogger(logger, messages), mimeMappings, meter, resolver, cc);

            ApplicationCompilerConfiguration appConfig = (ApplicationCompilerConfiguration) data.configuration;
            VirtualFile projector = appConfig.getProjector();
            PostLink postLink = null;

            if (config.optimize() && !config.generateDebugTags())
            {
                postLink = new PostLink(config);
            }

            // link
            if (projector != null && projector.getName().endsWith("avmplus.exe"))
            {
                ConsoleApplication temp = data.app;
                data.app = LinkerAPI.linkConsole(data.units, postLink, config);
                size = encodeConsoleProjector(projector, out);
                if (hasChanged && temp != null)
                {
                    data.app = temp;
                }
            }
            else
            {
                SimpleMovie temp = data.movie;
                data.movie = (FlexMovie) LinkerAPI.link(data.units, postLink, config);
                size = (projector == null) ? encode(out) : encodeProjector(projector, out);
                if (hasChanged && temp != null)
                {
                    data.movie = temp;
                }
            }
        }
        catch (LinkerException ex)
        {
            assert ThreadLocalToolkit.errorCount() > 0;
        }

        return size;
    }

    /**
     *
     * @param out
     * @return Number of bytes written to 'out'
     * @throws IOException
     */
    private long encode(OutputStream out) throws IOException
    {
        if (data == null || data.units == null || data.movie == null)
        {
            return 0;
        }

//        if (ThreadLocalToolkit.getBenchmark() != null &&
//            ThreadLocalToolkit.getLocalizationManager() == null)
//        {
//            OEMUtil.init(OEMUtil.getLogger(logger, messages), mimeMappings, meter, resolver, cc);
//        }

        //TODO PERFORMANCE: A lot of unnecessary recopying and buffering here
        // output SWF
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        CompilerAPI.encode(data.movie, baos);
        long size = baos.size();

        baos.writeTo(out);
        out.flush();

        return size;
    }

    /**
     *
     * @param projector
     * @param out
     * @return
     * @throws IOException
     */
    private long encodeProjector(VirtualFile projector, OutputStream out) throws IOException
    {
        if (data == null || data.units == null || data.movie == null)
        {
            return 0;
        }

        // output EXE
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        CompilerAPI.encode(data.movie, baos);
        return Mxmlc.createProjector(projector, baos, out);
    }

    /**
     *
     * @param projector
     * @param out
     * @return
     * @throws IOException
     */
    private long encodeConsoleProjector(VirtualFile projector, OutputStream out) throws IOException
    {
        if (data == null || data.units == null || data.app == null)
        {
            return 0;
        }

        // output EXE
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        CompilerAPI.encode(data.app, baos);
        return Mxmlc.createProjector(projector, baos, out);
    }

    /**
     *
     * @param cleanData
     * @param cleanCache
     * @param cleanOutput
     */
    private void clean(boolean cleanData, boolean cleanCache, boolean cleanOutput)
    {
        clean(cleanData, cleanCache, cleanOutput, true, false, true);
    }

    /**
     *
     * @param cleanData
     * @param cleanCache
     * @param cleanOutput
     * @param cleanConfig
     * @param cleanMessages
     * @param cleanThreadLocals
     */
    private void clean(boolean cleanData, boolean cleanCache, boolean cleanOutput,
                       boolean cleanConfig, boolean cleanMessages, boolean cleanThreadLocals)
    {
        if (cleanThreadLocals)
        {
            OEMUtil.clean();
        }

        if (oemConfiguration != null && cleanConfig)
        {
            oemConfiguration.reset();
        }

        if (cleanData)
        {
            data = null;
            configurationReport = null;
        }

        if (cleanCache)
        {
            if (cacheName != null)
            {
                File dead = FileUtil.openFile(cacheName);
                if (dead != null && dead.exists())
                {
                    dead.delete();
                }
                cacheName = null;
            }
        }

        if (cleanOutput)
        {
            if (output != null && output.exists())
            {
                output.delete();
            }
        }

        if (cleanMessages)
        {
            messages.clear();
        }
    }

    /**
     *
     * @param localOEMConfiguration
     * @return
     */
    private String[] constructCommandLine(OEMConfiguration localOEMConfiguration)
    {
        String[] options = (localOEMConfiguration != null) ? localOEMConfiguration.getCompilerOptions() : new String[0];
        String[] args = new String[options.length + files.size() + 1];
        System.arraycopy(options, 0, args, 0, options.length);
        args[options.length] = "--" + Mxmlc.FILE_SPECS;
        for (int i = 0, size = files.size(); i < size; i++)
        {
            args[options.length + 1 + i] = files.get(i).getName();
        }

        return args;
    }

    /**
     * Get the cache of swcs in the library path. After building this Application
     * object the cache may be saved and used to compile another Application object
     * that uses the same library path.
     *
     * @return The active cache. May be null.
     *
     * @since 3.0
     */
    public LibraryCache getSwcCache()
    {
        return librarySwcCache;
    }

    /**
     * Set the cache for swcs in the library path. After compiling an
     * Application object the cache may be reused to build another Application
     * object that uses the same library path.
     *
     * @param swcCache A reference to an allocated swc cache.
     *
     * @since 3.0
     */
    public void setSwcCache(LibraryCache swcCache)
    {
        this.librarySwcCache = swcCache;
    }
}
