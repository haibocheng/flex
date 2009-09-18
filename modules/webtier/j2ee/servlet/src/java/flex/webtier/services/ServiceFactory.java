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

import java.util.HashMap;

import flex.webtier.server.j2ee.cache.CacheHelper;
import flex.webtier.services.config.Configurator;
import flex.webtier.services.extensions.ExtensionManager;
import flex.webtier.services.license.LicenseService;
import flex.webtier.services.license.LicenseServiceImpl;
import flex.webtier.services.logging.Logger;
import flex.webtier.util.ServerUtil;
import flex.webtier.util.Trace;

public abstract class ServiceFactory {

    private static Configurator configurator;
    private static boolean mxmlSwfExtension = true;
    private static ExtensionManager extensionManager;
    private static LicenseService licenseService;
    private static Logger logger;
    private static CacheHelper cacheHelper;

    protected static ServiceFactory factoryImpl;

    public static final String FLEX_LICENSE = "flexbuilder3";

    /** Initializes the ServiceFactory with the specified implementation. **/
    public static ServiceFactory getFactory(HashMap properties) throws Exception
    {
        String serviceClassName = "flex.webtier.oem.OEMServiceImpl";

        try
        {
            if (factoryImpl == null)
            {
                try
                {
                    factoryImpl = (ServiceFactory)Class.forName(serviceClassName).newInstance();
                }
                catch (ClassNotFoundException cnfe)
                {
                    // no oem services use Flex native
                    if (ServerUtil.isDotNet())
                    {
			// no longer supported so this will throw an exception below
                        serviceClassName = "flex.webtier.services.DotNetServiceImpl";

                    }
                    else
                    {
                        serviceClassName = "flex.webtier.services.J2EEServiceImpl";
                    }
                    factoryImpl = (ServiceFactory)Class.forName(serviceClassName).newInstance();

                }
                factoryImpl.init(properties);
            }
        }
        catch (Exception e)
        {
            if (Trace.error)
            {
                e.printStackTrace();
            }
            throw new Exception("Failed to locate ServiceFactory: " + serviceClassName + "; message = " + e.getMessage());
        }

        return factoryImpl;
    }

    public void init(HashMap properties) {}

    public abstract void start() throws Exception;
    public abstract void stop();

    public static final Configurator getConfigurator()
    {
        return configurator;
    }
    
    // Determines whether .swf extension is used rather than usual mxml.swf
    public static final boolean isMxmlSwfExtension()
    {
        return mxmlSwfExtension;
    }
    
    public static final LicenseService getLicenseService()
    {
       return licenseService;
    }

    public static final Logger getLogger()
    {
        return logger;
    }

    //all the set properties
    //starts here

    public static final void setConfigurator(Configurator c)
    {
        configurator = c;
    }
    
    // Determines whether .swf extension is used rather than usual mxml.swf
    public static final void setMxmlSwfExtension(boolean b)
    {
        mxmlSwfExtension = b;   
    }
    
    public static final void setLogger(Logger l)
    {
        logger = l;
    }

    public static final void setLicenseService(LicenseServiceImpl l)
    {
        licenseService = l;
    }

    public static final ExtensionManager getExtensionManager()
    {
        return extensionManager;
    }

    public static final void setExtensionManager(ExtensionManager em)
    {
        extensionManager = em;
    }

    public static final CacheHelper getCacheHelper()
    {
        return cacheHelper;
    }

    public static final void setCacheHelper(CacheHelper ch)
    {
        cacheHelper = ch;
    }
    
	/**
	 * Clear out static variables to avoid memory leaks
	 */
	public static void destroy()
	{
	    configurator=null;
	    extensionManager=null;
	    licenseService=null;
	    logger = null;
	    cacheHelper = null;  
	    factoryImpl = null;        
	}    
}