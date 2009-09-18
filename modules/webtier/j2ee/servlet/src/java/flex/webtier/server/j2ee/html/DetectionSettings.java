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
package flex.webtier.server.j2ee.html;

public class DetectionSettings
{
    private boolean enabled;
    private boolean expressInstall;

    private int majorVersion;
    private int minorVersion;
    private int versionRevision;

    private String alternateContentPage;
    private String alternateContentInclude;

    private String secureAlternateContentPage;
    private String secureAlternateContentInclude;

    private String jsUri;
    private String swfUri;

    public boolean enabled()
    {
        return enabled;
    }

    public void setEnabled(boolean enabled)
    {
        this.enabled = enabled;
    }

    public boolean useExpressInstall()
    {
        return expressInstall;
    }

    public void setUseExpressInstall(boolean expressInstall)
    {
        this.expressInstall = expressInstall;
    }

    public int getMajorVersion()
    {
        return majorVersion;
    }

    public void setMajorVersion(int majorVersion)
    {
        this.majorVersion = majorVersion;
    }

    public int getMinorVersion()
    {
        return minorVersion;
    }

    public void setMinorVersion(int minorVersion)
    {
        this.minorVersion = minorVersion;
    }

    public int getVersionRevision()
    {
        return versionRevision;
    }

    public void setVersionRevision(int versionRevision)
    {
        this.versionRevision = versionRevision;
    }

    public String getAlternateContentPage()
    {
        return alternateContentPage;
    }

    public void setAlternateContentPage(String alternateContentPage)
    {
        this.alternateContentPage = alternateContentPage;
    }

    public String getAlternateContentInclude()
    {
        return alternateContentInclude;
    }

    public void setAlternateContentInclude(String alternateContentInclude)
    {
        this.alternateContentInclude = alternateContentInclude;
    }

    public String getSecureAlternateContentPage()
    {
        return secureAlternateContentPage;
    }

    public void setSecureAlternateContentPage(String secureAlternateContentPage)
    {
        this.secureAlternateContentPage = secureAlternateContentPage;
    }

    public String getSecureAlternateContentInclude()
    {
        return secureAlternateContentInclude;
    }

    public void setSecureAlternateContentInclude(String secureAlternateContentInclude)
    {
        this.secureAlternateContentInclude = secureAlternateContentInclude;
    }


    public Object clone()
    {
        DetectionSettings ds = new DetectionSettings();
        ds.enabled = this.enabled;
        ds.expressInstall = this.expressInstall;
        ds.majorVersion = this.majorVersion;
        ds.minorVersion = this.minorVersion;
        ds.alternateContentPage = this.alternateContentPage;
        ds.alternateContentInclude = this.alternateContentInclude;
        ds.secureAlternateContentPage = this.secureAlternateContentPage;
        ds.secureAlternateContentInclude = this.secureAlternateContentInclude;
        ds.jsUri = this.jsUri;
        ds.swfUri = this.swfUri;
        return ds;
    }
}
