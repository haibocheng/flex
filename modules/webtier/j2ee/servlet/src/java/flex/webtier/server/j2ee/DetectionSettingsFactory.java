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

import flash.util.StringUtils;
import flex.webtier.compiler.ConfigConstants;
import flex.webtier.services.ServiceFactory;
import flex.webtier.services.config.FlashPlayerConfiguration;
import flex.webtier.services.config.ServerConfiguration;
import flex.webtier.server.j2ee.html.DetectionSettings;

public class DetectionSettingsFactory implements ConfigConstants
{
    private static DetectionSettings ds;

    public static DetectionSettings getInstance(String contextRoot)
    {
        if (ds == null)
        {
            ds = DetectionSettingsFactory.setupDefaultDetectionSettings(contextRoot);
        }

        return ds;
    }

    protected static DetectionSettings setupDefaultDetectionSettings(String contextRoot)
    {
        DetectionSettings defaultDetectionSettings = new DetectionSettings();

        
        ServerConfiguration configurator = ServiceFactory.getConfigurator().getServerConfiguration();
        FlashPlayerConfiguration config = configurator.getFlashPlayerConfiguration();

        defaultDetectionSettings.setEnabled(config.usePlayerDetection());
        defaultDetectionSettings.setUseExpressInstall(config.useExpressInstall());

        defaultDetectionSettings.setMajorVersion(config.requiredMajorVersion());
        defaultDetectionSettings.setMinorVersion(config.requiredMinorVersion());
        defaultDetectionSettings.setVersionRevision(config.requiredVersionRevision());

        String alternateContentPage = config.alternateContentPage();
        if (alternateContentPage != null)
        {
            alternateContentPage = StringUtils.substitute(alternateContentPage, TOKEN_CONTEXT_ROOT, contextRoot);
        }
        String secureAlternateContentPage = config.secureAlternateContentPage();
        if (secureAlternateContentPage != null)
        {
            secureAlternateContentPage = StringUtils.substitute(secureAlternateContentPage, TOKEN_CONTEXT_ROOT, contextRoot);
        }

        defaultDetectionSettings.setAlternateContentPage(alternateContentPage);
        defaultDetectionSettings.setSecureAlternateContentPage(secureAlternateContentPage);

        defaultDetectionSettings.setAlternateContentInclude(config.alternateContentInclude());
        defaultDetectionSettings.setSecureAlternateContentInclude(config.secureAlternateContentInclude());

        return defaultDetectionSettings;
    }
}
