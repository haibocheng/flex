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

import flex2.compiler.config.ConfigurationValue;

public class FlashPlayerConfiguration
{
    public static final String CONTENT_TYPE = "application/x-shockwave-flash";

    private boolean usePlayerDetection = true;
    private boolean useExpressInstall = true;

    // Flash Player 10.0.0 - shipping version
    private int requiredMajorVersion = 10;
    private int requiredMinorVersion = 0;
    private int requiredVersionRevision = 0;

    private String alternateContentPage;
    private String alternateContentInclude;
    private String secureAlternateContentPage;
    private String secureAlternateContentInclude;

    // not configurable, used for object/embed tags of history swfs and swfs after detection has occurred
    private String classid = "clsid:D27CDB6E-AE6D-11cf-96B8-444553540000";
    private String codebaseVersion = "#version=10,0,0,0";
    private String activexDownloadUrl = "http://fpdownload.macromedia.com/get/flashplayer/current/swflash.cab";
    private String activexDownloadHttpsUrl = "https://fpdownload.macromedia.com/get/flashplayer/current/swflash.cab";
    private String pluginDownloadUrl = "http://www.adobe.com/go/getflashplayer";
    private String pluginDownloadHttpsUrl = "https://www.adobe.com/go/getflashplayer";

    public boolean usePlayerDetection()
    {
        return usePlayerDetection;
    }

    public void cfgUsePlayerDetection(ConfigurationValue cv, boolean enable)
    {
        this.usePlayerDetection = enable;
    }

    public int requiredMajorVersion()
    {
        return requiredMajorVersion;
    }

    public void cfgRequiredMajorVersion(ConfigurationValue cv, int reqver)
    {
        this.requiredMajorVersion = reqver;
    }

    public int requiredMinorVersion()
    {
        return requiredMinorVersion;
    }

    public void cfgRequiredMinorVersion(ConfigurationValue cv, int requiredMinorVersion)
    {
        this.requiredMinorVersion = requiredMinorVersion;
    }

    public int requiredVersionRevision()
    {
        return requiredVersionRevision;
    }

    public void cfgRequiredVersionRevision(ConfigurationValue cv, int requiredVersionRevision)
    {
        this.requiredVersionRevision = requiredVersionRevision;
    }

    public boolean useExpressInstall()
    {
        return useExpressInstall;
    }

    public void cfgUseExpressInstall(ConfigurationValue cv, boolean useExpressInstall)
    {
        this.useExpressInstall = useExpressInstall;
    }

    public String alternateContentInclude()
    {
        return alternateContentInclude;
    }

    public void cfgAlternateContentInclude(ConfigurationValue cv, String alternateContentInclude)
    {
        this.alternateContentInclude = alternateContentInclude;
    }

    public String secureAlternateContentInclude()
    {
        return secureAlternateContentInclude;
    }

    public void cfgSecureAlternateContentInclude(ConfigurationValue cv, String secureAlternateContentInclude)
    {
        this.secureAlternateContentInclude = secureAlternateContentInclude;
    }

    public String alternateContentPage()
    {
        return alternateContentPage;
    }

    public void cfgAlternateContentPage(ConfigurationValue cv, String alternateContentPage)
    {
        this.alternateContentPage = alternateContentPage;
    }

    public String secureAlternateContentPage()
    {
        return secureAlternateContentPage;
    }

    public void cfgSecureAlternateContentPage(ConfigurationValue cv, String secureAlternateContentPage)
    {
        this.secureAlternateContentPage = secureAlternateContentPage;
    }

    public String getClassid()
    {
        return classid;
    }

    public String getCodebase()
    {
        return activexDownloadUrl;
    }

    public String getCodebaseHttps()
    {
        return activexDownloadHttpsUrl;
    }

    public String getCodebaseVersion()
    {
        return codebaseVersion;
    }

    public String getActivexDownloadUrl()
    {
        return activexDownloadUrl;
    }

    public String getActivexDownloadHttpsUrl()
    {
        return activexDownloadHttpsUrl;
    }

    public String getPluginDownloadHttpsUrl()
    {
        return pluginDownloadHttpsUrl;
    }

    public String getPluginDownloadUrl()
    {
        return pluginDownloadUrl;
    }
}
