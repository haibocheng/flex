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

import flash.localization.LocalizationManager;
import flash.localization.ResourceBundleLocalizer;
import flash.localization.XLRLocalizer;
import flex.webtier.services.ServiceFactory;
import flex2.compiler.config.ConfigurationBuffer;
import flex2.compiler.config.FileConfigurator;
import flex2.compiler.config.SystemPropertyConfigurator;
import flex2.compiler.config.ConfigurationException;
import flex2.compiler.io.VirtualFile;
import flex2.compiler.util.ThreadLocalToolkit;
import flex.webtier.server.j2ee.ServletPathResolver;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.ServletContext;

/*
 * This class is the main configuration interface:
 * obtain a Configurator through ServiceFactory.getConfigurator()
 *
 * @author cmurphy
 *
 *
 * Slightly repurposed in the new configuration scheme.  Holds onto the default server
 * configuration object, and provides the associated ConfigurationBuffer for building
 * per-request configuration objects.  This is admittedly a bit odd.  --rg
 *
 */
public class Configurator
{
    private long configurationVersion;
    private ServletPathResolver resolver;
    private ServletContext servletContext;
    private String flexDir;
    private String fileName;
    private static ArrayList warnings = new ArrayList();

    // configuration used by the webtier infrastructure
    private ServerConfiguration serverConfig;

    // configuration used for generating the mxml compiler configuration
    private ConfigurationBuffer defaultBuf;
    private ServerConfiguration defaultConfig;

    public void init(String flexDir, String fileName, long version, ServletContext servletContext) throws Exception
    {
        this.flexDir = flexDir;
        this.fileName = fileName;
        this.configurationVersion = version;
        this.servletContext = servletContext;
        serverConfig = loadServerConfiguration();
    }

    /**
     * store warnings found when parsing the configuration
     * todo - new config stuff just throws, doesn't gen warnings.  figure out strategy for this.
     */
    public static void storeWarning(String msg)
    {
        warnings.add(msg);
    }

    /**
     * log warnings.  This isn't done right away because the right logging settings aren't set up right away
     */
    public void logStoredWarnings()
    {
        for (Iterator iterator = warnings.iterator(); iterator.hasNext();)
        {
            String msg = (String) iterator.next();
            ServiceFactory.getLogger().logWarning(msg);
        }
        warnings.clear();
    }

    public long getVersion()
    {
        return configurationVersion;
    }

    public ServerConfiguration getServerConfiguration()
    {
        return serverConfig;
    }

    public ServerConfiguration loadServerConfiguration() throws ConfigurationException
    {
        ConfigurationBuffer serverBuffer = new ConfigurationBuffer(ServerConfiguration.class);
        serverBuffer.setToken("flexlib", flexDir);
        ServerDefaultsConfigurator.loadDefaults(serverBuffer, servletContext);

        // todo - move these hard-coded strings into the actual config object?
        SystemPropertyConfigurator.load(serverBuffer, "flex");
        FileConfigurator.load(serverBuffer, fileName, "flex-webtier-config");

        ServerConfiguration config = new ServerConfiguration();

        // the config path resolver root is the parent directory of the config file
        resolver = new ServletPathResolver(servletContext);
        File configFile = new File(fileName);
        resolver.setRoot(configFile.getParent());
        config.getFlexConfigConfiguration().setConfigPathResolver(resolver);

        serverBuffer.commit(config);

        // reset defaults used for compiler override generation
        defaultBuf = serverBuffer;
        defaultConfig = config;

        return config;
    }

    // configuration messages are logged to the console, not the browser
    public String getMessage(ConfigurationException e)
    {
        LocalizationManager lm = ThreadLocalToolkit.getLocalizationManager();

        // set up for localizing messages
        LocalizationManager localizationManager = new LocalizationManager();
        localizationManager.addLocalizer(new XLRLocalizer());
        localizationManager.addLocalizer(new ResourceBundleLocalizer());
        ThreadLocalToolkit.setLocalizationManager(localizationManager);   

        String message = new ExceptionLogger().getMessage(e);

        ThreadLocalToolkit.setLocalizationManager(lm);
        
        return message;
    }

    public ServerConfiguration generateConfiguration(HttpServletRequest request,
                                                     File appFile,
                                                     Map dependencies)
            throws ConfigurationException
    {
        ServerConfiguration newConfig = new ServerConfiguration();

        try
        {
        // reload to pick up any changes
        loadServerConfiguration();

        // add dependencies - flex-webtier-config.xml
        long modified = new File(fileName).lastModified();
        modified -= modified % 1000;
        dependencies.put(fileName, new Long(modified));
        // add dependencies - flex-config.xml
        String flexConfigFilePath = getServerConfiguration().getFlexConfigFilePath();
        if (flexConfigFilePath != null)
        {
            modified = new File(flexConfigFilePath).lastModified();
            modified -= modified % 1000;
            dependencies.put(flexConfigFilePath, new Long(modified));
        }

        ConfigurationBuffer configBuf = new ConfigurationBuffer(defaultBuf, false);

        if (appFile != null)
        {
            String appPath = appFile.getAbsolutePath();

            int dot = appPath.lastIndexOf( '.' );

            if (dot != -1)
            {
                try
                {
                    ConfigurationBuffer projectBuf = new ConfigurationBuffer( configBuf.getChildConfigClass( "flex-config" ) );

                    String appConfigFile = appPath.substring( 0, dot ) + "-config.xml";
                    VirtualFile projectFile = resolver.resolve( appConfigFile );
                    if (projectFile != null)
                    {
                        InputStream in = projectFile.getInputStream();
                        if (in != null)
                        {
                            // Add to dependency list because we want to rebuild even if the cfg is hosed.
                            long lastmod = projectFile.getLastModified();
                            if (lastmod != -1)
                            {
                                lastmod -= lastmod % 1000;
                                dependencies.put(projectFile.getName(), new Long(lastmod));
                            }

                            FileConfigurator.load(projectBuf, new InputStreamReader(in), projectFile.getName(), projectFile.getParent(), "flex-config");
                            configBuf.mergeChild( "flex-config", projectBuf );
                        }
                    }
                }
                catch (Exception e)
                {
                    ServiceFactory.getLogger().logWarning( e.getMessage() );
                }
            }
        }

        ConfigurationBuffer requestBuf = new ConfigurationBuffer(ServerConfiguration.class);
        new RequestConfigurator().parse(requestBuf, defaultConfig, request);

        configBuf.merge(requestBuf);

        newConfig.getFlexConfigConfiguration().setConfigPathResolver(resolver);

        processProductionMode(configBuf);

        configBuf.commit(newConfig);
        
        calculateServicesChecksum(newConfig, configBuf);
        
        newConfig.setChecksum_ts(configBuf.checksum_ts());
        }
        catch (ConfigurationException ce)
        {
            ConfigurationException c = new ConfigurationException(new ExceptionLogger().getMessage(ce));
            c.setStackTrace(ce.getStackTrace());
            throw c;
        }
        return newConfig;
    }

    public void calculateServicesChecksum(ServerConfiguration config, ConfigurationBuffer cfgbuf)
    {
        Map services = null;
        if (config.getFlexConfigConfiguration().getCompilerConfiguration().getServicesDependencies() != null)
        {
            services = config.getFlexConfigConfiguration().getCompilerConfiguration().getServicesDependencies().getConfigPaths();
        }

        if (services != null)
        {
            for (Iterator iter = services.entrySet().iterator(); iter.hasNext(); )
            {
                Map.Entry entry = (Map.Entry)iter.next();
                cfgbuf.calculateChecksum((String)entry.getKey(), (Long)entry.getValue());
            }
        }
    }

    /**
     * Process production mode overrides in both flex-config.xml and flex-webtier-config.xml
     */
    private void processProductionMode(ConfigurationBuffer configBuf) throws ConfigurationException
    {
        if (defaultConfig.isProductionMode())
        {
            ConfigurationBuffer productionBuf = new ConfigurationBuffer(ServerConfiguration.class);

            // flex-config.xml overrides
            productionBuf.setVar("flex-config.compiler.verbose-stacktraces", "false", "production-mode", -1);
            productionBuf.setVar("flex-config.compiler.show-binding-warnings", "false", "production-mode", -1);
            productionBuf.setVar("flex-config.compiler.show-deprecation-warnings", "false", "production-mode", -1);
            productionBuf.setVar("flex-config.compiler.keep-generated-actionscript", "false", "production-mode", -1);
            productionBuf.setVar("flex-config.compiler.show-actionscript-warnings", "false", "production-mode", -1);
            productionBuf.setVar("flex-config.compiler.strict", "false", "production-mode", -1);
            productionBuf.setVar("flex-config.compiler.debug", "false", "production-mode", -1);

            // flex-webtier-config.xml overrides
            productionBuf.setVar("debugging.process-debug-query-params", "false", "production-mode", -1);
            productionBuf.setVar("debugging.generate-profile-swfs", "false", "production-mode", -1);
            productionBuf.setVar("debugging.keep-generated-swfs", "false", "production-mode", -1);
            productionBuf.setVar("debugging.show-all-warnings", "false", "production-mode", -1);
            productionBuf.setVar("debugging.show-override-warnings", "false", "production-mode", -1);
            productionBuf.setVar("debugging.show-source-in-compiler-errors", "false", "production-mode", -1);
            productionBuf.setVar("debugging.log-compiler-errors", "false", "production-mode", -1);
            productionBuf.setVar("debugging.create-compile-report", "false", "production-mode", -1);

            configBuf.merge(productionBuf);
        }
    }

}
