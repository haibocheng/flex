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
package flex.webtier.server.j2ee.wrappers;

import flex.webtier.util.J2EEUtil;

import javax.servlet.ServletRequest;
import javax.servlet.FilterConfig;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

/**
 * Returns same J2EE object passed in or a wrapped request if J2EEUtil methods need to be used
 * @author Brian Deitte
 */
public class J2EEWrapper
{
    public static ServletRequest getRequest(ServletRequest request)
    {
        if (J2EEUtil.isATG())
        {
            return new ATGHttpServletRequest((HttpServletRequest)request);
        }
        return request;
    }

    public static ServletContext getServletContext(ServletContext context)
    {
        J2EEUtil.setServerInfo(context.getServerInfo());
        if (J2EEUtil.isATG())
        {
            return new ATGServletContext(context);
        }
        return context;
    }

    public static FilterConfig getFilterConfig(FilterConfig config)
    {
        J2EEUtil.setServerInfo(config.getServletContext().getServerInfo());
        if (J2EEUtil.isATG())
        {
            return new ATGFilterConfig(config);
        }
        return config;
    }

    /**
     * returns the same ServletConfig passed in or a wrapped config if J2EEUtil methods need to be used
     */
    public static ServletConfig getServletConfig(ServletConfig config)
    {
        J2EEUtil.setServerInfo(config.getServletContext().getServerInfo());
        if (J2EEUtil.isATG())
        {
            return new ATGServletConfig(config);
        }
        return config;
    }
}
