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
package flex.webtier.services.config;

import flex.messaging.config.ServicesDependencies;
import flex2.compiler.config.ConfigurationValue;
import flex2.compiler.config.ConfigurationException;
import flex2.compiler.config.ConfigurationInfo;

/**
 * @author Roger Gonzalez
 */
public class ServerConfiguration
{
    private boolean productionMode = true;
    private boolean incrementalCompile = true;
    private int checksum_ts;
    private boolean useHistoryManagement = true;
    private String flexConfigFilePath;

    public boolean isProductionMode()
    {
        return productionMode;
    }

    public void cfgProductionMode(ConfigurationValue cv, boolean productionMode)
    {
        this.productionMode = productionMode;
    }

    public static ConfigurationInfo getProductionModeInfo()
    {
        return new ConfigurationInfo()
        {
            public boolean isRequired()
            {
                return true;
            }
        };
    }

    public boolean isIncrementalCompile()
    {
        return incrementalCompile;
    }

    public void cfgIncrementalCompile(ConfigurationValue cv, boolean incrementalCompile)
    {
        this.incrementalCompile = incrementalCompile;
    }

    public boolean useHistoryManagement()
    {
        return useHistoryManagement;
    }

    public void cfgUseHistoryManagement(ConfigurationValue cv, boolean useHistoryManagement)
    {
        this.useHistoryManagement = useHistoryManagement;
    }

    public FlashPlayerConfiguration getFlashPlayerConfiguration()
    {
        return flashPlayerConfiguration;
    }

    public CacheConfiguration getCacheConfiguration()
    {
        return cacheConfiguration;
    }

    public DebuggingConfiguration getDebuggingConfiguration()
    {
        return debuggingConfiguration;
    }

    public LoggingConfiguration getLoggingConfiguration()
    {
        return loggingConfiguration;
    }

    public void setServices(ServicesDependencies servicesDependencies)
    {
        this.mxmlc.getCompilerConfiguration().setServicesDependencies( servicesDependencies );
    }

    // dummy, ignored - pulled out of the buffer
    public void cfgConfig(ConfigurationValue cv, String filename) throws ConfigurationException
    {
    }

    public void cfgFlexConfigFilePath(ConfigurationValue cv, String filename) throws ConfigurationException
    {
        this.flexConfigFilePath = filename;
    }

    public String getFlexConfigFilePath()
    {
        return flexConfigFilePath;
    }

    // This has a stupid name in order to produce the root tag
    // of <flex-config> in a <flex-config src="flex-config.xml" /> tag.
    public flex2.compiler.common.Configuration getFlexConfigConfiguration()
    {
        return mxmlc;
    }

    public void setChecksum_ts(int checksum_ts)
    {
        this.checksum_ts = checksum_ts;
    }

    public int getChecksum_ts()
    {
        return checksum_ts;
    }

    private LoggingConfiguration loggingConfiguration = new LoggingConfiguration();
    private CacheConfiguration cacheConfiguration = new CacheConfiguration();
    private DebuggingConfiguration debuggingConfiguration = new DebuggingConfiguration(this);
    private FlashPlayerConfiguration flashPlayerConfiguration = new FlashPlayerConfiguration();
    private flex2.compiler.common.Configuration mxmlc = new flex2.compiler.common.Configuration();

}
