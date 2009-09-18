////////////////////////////////////////////////////////////////////////////////
//
//  ADOBE SYSTEMS INCORPORATED
//  Copyright 2004-2008 Adobe Systems Incorporated
//  All Rights Reserved.
//
//  NOTICE: Adobe permits you to use, modify, and distribute this file
//  in accordance with the terms of the license agreement accompanying it.
//
////////////////////////////////////////////////////////////////////////////////
package flex.webtier.services;

import java.beans.Introspector;
import java.io.File;
import java.util.HashMap;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;

import flash.localization.LocalizationManager;
import flash.localization.ResourceBundleLocalizer;
import flash.localization.XLRLocalizer;
import flash.util.Trace;
import flex.webtier.server.j2ee.cache.CacheHelper;
import flex.webtier.services.config.Configurator;
import flex.webtier.services.config.InvalidConfigurationException;
import flex.webtier.services.config.LoggingConfiguration;
import flex.webtier.services.extensions.ExtensionManager;
import flex.webtier.services.license.LicenseServiceImpl;
import flex.webtier.services.license.RuntimeDirectoryLoader;
import flex.webtier.services.logging.Logger;
import flex.webtier.services.logging.LoggerService;
import flex.webtier.services.logging.WebtierIntegratedLogger;
import flex.webtier.util.PathResolver;
import flex.webtier.util.ServletPathResolver;
import flex2.compiler.common.LocalFilePathResolver;
import flex2.compiler.util.ThreadLocalToolkit;
import flex2.compiler.util.URLPathResolver;

public class J2EEServiceImpl extends ServiceFactory
{
    protected static J2EEServiceImpl services;

    protected static int NOT_STARTED = 0;
    protected static int STARTED = 1;
    protected static int FAILED = 2;
    protected static int STOPPED = 3;

    protected static int status = NOT_STARTED;

    public ServletContext context;
    public ServletConfig config;

    // keep track of the number of times start was called
    // this will let you know when the last time stop is being called
    private static int references;

    private Logger logger;
    private CacheHelper cacheHelper;

    public void init(HashMap properties)
    {
        assert (properties.get("ServletContext") != null) : ("ServletContext must be specified.");
        assert (properties.get("ServletContext") instanceof ServletContext) : ("ServletContext must be specified.");

        this.context = (ServletContext)properties.get("ServletContext");
        this.config = (ServletConfig)properties.get("ServletConfig");
    }

    public synchronized void start() throws Exception
    {
        try
        {
            references++;
            if (status == NOT_STARTED)
            {
                // set up a System.out/System.err logger for use before configuration options are known
                ServiceFactory.setLogger(LoggerService.createDefaultLogger()); 
                
                setupToolkit();
        
                try
                {
                    setupConfigurator();
                    setupMxmlSwfExtension();
                    setupLogger();
                    ServiceFactory.getConfigurator().logStoredWarnings();
                    setupLicenseService();
                    setupExtensionManager();
                    setupCacheHelper();
                }
                finally
                {
                    PathResolver.setThreadLocalPathResolver(null);
                    resetToolkit();
                }
                status = STARTED;
            }
            else if (status == FAILED)
            {
                if (Trace.error)
                {                    
                    Trace.trace("Invalid Configuration: see previous failures");
                    Thread.dumpStack();
                }
                throw new InvalidConfigurationException("Invalid Configuration: see previous failures.");
            }
        }
        catch (Exception e)
        {
            status = FAILED;
            if (Trace.error)
            {
                e.printStackTrace();
            }
            throw e;
        }
    }

    public synchronized void stop()
    {
        assert (references > 0) : ("References are being tracked incorrectly or services were stopped prematurely.");
        assert (status != STOPPED) : ("Services were stopped prematurely.");

        --references;
        if ((status == STARTED) && (references <= 0))
        {
            stopLogger();
            stopCacheHelper();
            ServiceFactory.destroy();            
            Introspector.flushCaches();
            status = STOPPED;
        }
    }
   
    
    public void setupConfigurator() throws Exception
    {
        Configurator configurator = new Configurator();
        String flexDir = PathResolver.getThreadLocalPathResolver().getFlexReadPath();
        String fileName = getConfigurationFilename();
        long version = new File(fileName).lastModified();
        configurator.init(flexDir, fileName, version, context);
        ServiceFactory.setConfigurator(configurator);
    }

    private String getConfigurationFilename() throws Exception
    {
        PathResolver resolver = PathResolver.getThreadLocalPathResolver();

        String configPath = null;

        if (config != null)
        {
            String p = config.getInitParameter("webtier.configuration.file");
            if (p != null)
            {
                configPath = p.trim();
            }
        }

        if (configPath == null)
        {
            configPath = resolver.getFlexReadPath() + File.separator + "flex-webtier-config.xml";
        }

        
        File config = resolver.resolveFile(configPath, false);
        if (config == null)
        {
            throw new InvalidConfigurationException("configuration file, " + configPath + ", not found");
        }
        else
        {
            return config.getCanonicalPath();
        }
    }
    
    private void setupMxmlSwfExtension()
    {
        if (config != null)
        {
            String p = config.getInitParameter("mxml.swf.extension");
            if (p != null && p.equalsIgnoreCase("false"))
                ServiceFactory.setMxmlSwfExtension(false);
        }
    }
    protected void setupExtensionManager()
    {
        ServiceFactory.setExtensionManager(new ExtensionManager());
    }

    public void setupLicenseService() throws Exception
    {
        String flexDir = PathResolver.getThreadLocalPathResolver().getFlexReadPath();
        LicenseServiceImpl licenseService = new LicenseServiceImpl(new RuntimeDirectoryLoader(flexDir));
        licenseService.init();
        ServiceFactory.setLicenseService(licenseService);
    }

    public void setupLogger() throws Exception
    {
        LoggingConfiguration config = ServiceFactory.getConfigurator().getServerConfiguration().getLoggingConfiguration();
/*
FIXME - probably register a file resolver such that the logger config's cfgFile() setter can do this itself.
FIXME - doing this from outside is a bit messy with the new config system.
        if (config.isFileEnable())
        {
            String filename = config.getFileName();
            String resolved = resolveLogFileName(filename);
            if (resolved == null)
            {
                config.setFileEnable(false);
                ServiceFactory.getLogger().logError("File logging disabled: invalid log filename specified. Log filename must be an absolute path or must start with '/' and specify a virtual path which can be resolved by the getRealPath method of the ServletContext.");
            }
            else
            {
                config.setFileName(resolved);
            }
        }
        */

        logger = new LoggerService();
        logger.initializeLogger(config);
        ((LoggerService)logger).start();
        ServiceFactory.setLogger(logger);

        if (logger.isDebugEnabled())
        {
            logger.logDebug("configuration service started.");
            logger.logDebug("logger services started.");
        }
    }

    public void stopLogger()
    {
        ((LoggerService)logger).stop();
    }

    /*
    private String resolveLogFileName(String filename)
    {
        String resolved = null;
        boolean isWindows = File.separator.equals("\\");

        if (filename != null)
        {
            if (isWindows)
            {
                if (filename.startsWith("/"))
                {
                    resolved = J2EEUtil.getRealPath(filename, context);
                }
                else if (new File(filename).isAbsolute())
                {
                    resolved = filename;
                }
            }
            else
            {
                File testFile = new File(filename);
                if (testFile.isAbsolute())
                {
                    // try to find a parent directory which exists
                    // this gives some clue as to whether
                    while (testFile != null && !testFile.exists())
                    {
                        testFile = testFile.getParentFile();
                    }

                    if (testFile.toString().equals("/"))
                    {
                        resolved = J2EEUtil.getRealPath(filename, context);
                    }
                    else
                    {
                        resolved = filename;
                    }
                }
            }
        }

        return resolved;
    }
    */

    public void setupCacheHelper()
    {
        cacheHelper = CacheHelper.getInstance(context);
        ServiceFactory.setCacheHelper(cacheHelper);
    }

    public void stopCacheHelper()
    {
        cacheHelper.stop();
    }
    
    
    /** setup the toolkit which is a link with the Flex compiler
     */
    private void setupToolkit()
    {
        // setup path resolvers
        PathResolver.setThreadLocalPathResolver(new ServletPathResolver(context));
        flex2.compiler.common.PathResolver resolver = new flex2.compiler.common.PathResolver();
        resolver.addSinglePathResolver(new flex.webtier.server.j2ee.ServletPathResolver(context));
        resolver.addSinglePathResolver(LocalFilePathResolver.getSingleton());
        resolver.addSinglePathResolver(URLPathResolver.getSingleton());
        ThreadLocalToolkit.setPathResolver(resolver);

        // set up for localizing messages
        LocalizationManager localizationManager = new LocalizationManager();
        localizationManager.addLocalizer(new XLRLocalizer());
        localizationManager.addLocalizer(new ResourceBundleLocalizer());
        ThreadLocalToolkit.setLocalizationManager(localizationManager);         
        
        // setup the logger, during server start up all messages are logged to the webtier logger
        ThreadLocalToolkit.setLogger(new WebtierIntegratedLogger());
    }
       
    private void resetToolkit()
    {                        
        ThreadLocalToolkit.setLogger(null);
        ThreadLocalToolkit.setLocalizationManager(null);
        ThreadLocalToolkit.setPathResolver(null);
    }

}
