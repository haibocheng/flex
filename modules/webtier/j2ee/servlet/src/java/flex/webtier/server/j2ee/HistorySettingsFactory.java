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
package flex.webtier.server.j2ee;

import flex.webtier.compiler.ConfigConstants;
import flex.webtier.services.ServiceFactory;
import flex.webtier.services.config.ServerConfiguration;
import flex.webtier.server.j2ee.html.HistorySettings;

public class HistorySettingsFactory implements ConfigConstants
{
    private static HistorySettings hs;

    public static HistorySettings getInstance(String contextRoot)
    {
        if (hs == null)
        {
            hs = HistorySettingsFactory.setupDefaultHistorySettings(contextRoot);
        }

        return hs;
    }

    protected static HistorySettings setupDefaultHistorySettings(String contextRoot)
    {
        HistorySettings defaultHistorySettings = new HistorySettings();
        ServerConfiguration configurator = ServiceFactory.getConfigurator().getServerConfiguration();
        defaultHistorySettings.setEnabled(configurator.useHistoryManagement());

        return defaultHistorySettings;
    }
}
