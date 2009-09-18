////////////////////////////////////////////////////////////////////////////////
//
//  ADOBE SYSTEMS INCORPORATED
//  Copyright 2004-2007 Adobe Systems Incorporated
//  All Rights Reserved.
//
//  NOTICE: Adobe permits you to use, modify, and distribute this file
//  in accordance with the terms of the license agreement accompanying it.
//
////////////////////////////////////////////////////////////////////////////////
package flex.webtier.util;

import flash.localization.LocalizationManager;
import flash.localization.ResourceBundleLocalizer;
import flash.localization.XLRLocalizer;
import flex.webtier.services.ServiceFactory;
import flex.webtier.services.config.ExceptionLogger;
import flex.webtier.services.config.InvalidConfigurationException;
import flex2.compiler.config.ConfigurationException;
import flex2.compiler.util.ThreadLocalToolkit;
import java.util.HashMap;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;

/**
 * Common code for Flex services
 * 
 * @author Brian Deitte
 */
public class ServiceUtil
{
    public static ServiceFactory setupFlexService(ServletContext context, ServletConfig config) throws ServletException
    {
        ServiceFactory serviceImpl = null;
        try
        {
            HashMap properties = new HashMap();
            properties.put("ServletContext", context);
            properties.put("ServletConfig", config);
            serviceImpl = ServiceFactory.getFactory(properties);
            serviceImpl.start();
        }
        catch (InvalidConfigurationException ice)
        {
            if (Trace.error) ice.printStackTrace();
            throw new ServletException(ice.getMessage());
        }
        catch (ConfigurationException ce)
        {
            throw new ServletException(getMessage(ce));
        }
        catch (Throwable t)
        {
            if (Trace.error) t.printStackTrace();
            throw new ServletException(t);
        }
        return serviceImpl;
    }
    
    // configuration messages are logged to the console, not the browser
    public static String getMessage(ConfigurationException e)
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
}
