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
package flex.webtier.services.license;

import java.io.File;
import java.io.FileNotFoundException;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import flex.webtier.util.Trace;
import flex2.compiler.common.Configuration;

/**
 * The Flex License Manager Service. The license manager acts
 * as a proxy to the actual license implementation.
 */
public final class LicenseServiceImpl implements LicenseService
{
    // The current license key
    private String licenseKey = null;

    File hiddenFile = null;

    private boolean betaRelease = false;

    private int betaPeriodDays = 106;
    private int betaStartsYear = 2007;
    private int betaStartsMonth = 3;
    private int betaStartsDay = 1;

    LicenseLoader loader;

    public LicenseServiceImpl(LicenseLoader loader)
    {
        this.loader = loader;
    }

    // If this license changes, the server needs to be restarted for it to
    // take effect.
    private void load()
    {
        try
        {
            Properties props = loader.loadProperties();

            licenseKey = props.getProperty(LicenseLoader.FLEXBUILDER3_LICENSE_KEY_PROPERTY);

            // Trim any excess whitespace and ignore blank strings
            if (licenseKey != null)
            {
                licenseKey = licenseKey.trim();
            }

            if ((licenseKey == null) || (licenseKey.length() == 0))
            {
                licenseKey = null;
            }

            if (Trace.license)
            {
                Trace.trace("LicenseLoader " +
                        LicenseLoader.FLEXBUILDER3_LICENSE_KEY_PROPERTY + 
                        " license key is " + licenseKey);
            }                
        }
        catch (FileNotFoundException ex)
        {
        }
        catch (Exception ex)
        {
        }
    }

    public void init()
    {
        load();

        if (isBetaExpired())
        {
            InvalidLicenseException ile = new InvalidLicenseException();
            ile.setMessage(expiredMessage());
            throw ile;
        }
    }

    public Map getLicenseMap()
    {
        Map licenseMap = new HashMap();

        if (licenseKey != null)
        {
            licenseMap.put(LicenseLoader.FLEXBUILDER3_LICENSE_KEY_PROPERTY, licenseKey);
        }

        return licenseMap;
    }

    // Consolidate license keys from the LicenseLoader (either license.properties 
    // or the hard-coded in OEMLicenseLoader) and the licenses configuration in 
    // flexConfig (usually from flex-config.xml).  The licenses in flexConfig 
    // takes precedence over the licenses from the LicenseLoader.  Unlike the
    // license loaded by the LicenseLoader, the server does not need to be 
    // restarted for changes in the config file to take effect.
    public Map getConsolidatedLicenseMap(Configuration flexConfig)
    {
        String key;
        
        Map licenseMap = getLicenseMap();
        Map cmdLicensesMap = flexConfig.getLicensesConfiguration().getLicenseMap();

        if (cmdLicensesMap != null)
        {
            licenseMap.putAll(cmdLicensesMap);           
            if (Trace.license)
            {
                Trace.trace(LicenseLoader.FLEXBUILDER3_LICENSE_KEY_PROPERTY + 
                        " license key is " + 
                        licenseMap.get(LicenseLoader.FLEXBUILDER3_LICENSE_KEY_PROPERTY));
            }
        }
        
        return licenseMap;
    }

    public boolean isMxmlCompileEnabled()
    {
        return true;
    }

    // No license checking done here.  Let the SDK figure out if the license
    // being used for a request is valid.
    public boolean isValid()
    {
        return true;
    }


    public String getBetaRemainingMessage()
    {
        String message = null;

        if (betaRelease)
        {
            Calendar c = betaExpires();
            message = " (beta period ends " + DateFormat.getDateInstance().format( c.getTime() ) + ")";
        }

        return message;
    }

    private Calendar betaExpires()
    {
        if (!betaRelease)
        {
            return null;
        }
        else
        {
            Calendar expires = Calendar.getInstance();
            expires.clear();
            expires.set(Calendar.HOUR_OF_DAY, 0);
            expires.set(Calendar.MINUTE, 0);
            expires.set(Calendar.SECOND, 0);
            expires.set( Calendar.YEAR, betaStartsYear );
            expires.set( Calendar.MONTH, betaStartsMonth-1 );
            expires.set( Calendar.DAY_OF_MONTH, betaStartsDay );
            expires.add( Calendar.DATE, betaPeriodDays );

            return expires;
        }
    }

    public boolean isBetaExpired()
    {
        if (!betaRelease)
        {
            return false;
        }
        else
        {
            Calendar now = Calendar.getInstance();
            Calendar expires = betaExpires();

            return now.after( expires );
        }
    }


    // currently not used - because no license, expired license reverts to developer license functionality
    // this would be used for during the pre-release period
    public String expiredMessage()
    {
        String message = null;

        if (isBetaExpired())
        {
            message = "Adobe Flex Web Tier Compiler: beta license expired!";
        }

        return message;
    }

}

