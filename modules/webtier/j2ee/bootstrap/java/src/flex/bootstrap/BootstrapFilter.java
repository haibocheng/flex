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
package flex.bootstrap;

import flex.webtier.server.j2ee.wrappers.J2EEWrapper;

import javax.servlet.Filter;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.FilterChain;
import java.io.IOException;

/**
 * Filter to load the real filter using the BootstrapClassLoader.  Also creates a wrapped FilterConfig and
 * HttpServletRequest if needed
 *
 * @author Brian Deitte
 */
public class BootstrapFilter implements Filter
{
    private Filter filter;

    public void init(FilterConfig filterConfig) throws ServletException
    {
         filterConfig = J2EEWrapper.getFilterConfig(filterConfig);

        String filterClass = filterConfig.getInitParameter("filter.class");

        // Detect the underlying appserver to use the appServer specific properties files for loading specific classes ourselves or via the
        // System ClassLoader
        try
        {
            ClassLoader cl = Thread.currentThread().getContextClassLoader();

            try
            {
                ClassLoader bootstrap = BootstrapClassLoader.getClassLoader(filterConfig.getServletContext());
                Thread.currentThread().setContextClassLoader(bootstrap);
                filter = (Filter) bootstrap.loadClass(filterClass).newInstance();
                filter.init(filterConfig);
            }
            finally
            {
                Thread.currentThread().setContextClassLoader(cl);
            }
        }
        catch (ServletException e)
        {
            throw e;
        }
        catch (Throwable e)
        {
            throw new ServletException(e);
        }
    }

    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
            throws IOException, ServletException
    {
        ClassLoader cl = Thread.currentThread().getContextClassLoader();

        try
        {
            ClassLoader bootstrap = BootstrapClassLoader.getClassLoader();
            Thread.currentThread().setContextClassLoader(bootstrap);

            servletRequest = J2EEWrapper.getRequest(servletRequest);

            filter.doFilter(servletRequest, servletResponse, filterChain);
        }                                                                                     
        finally
        {
            Thread.currentThread().setContextClassLoader(cl);
        }
    }

    public void destroy()
    {
        ClassLoader cl = Thread.currentThread().getContextClassLoader();

        try
        {
            ClassLoader bootstrap = BootstrapClassLoader.getClassLoader();
            Thread.currentThread().setContextClassLoader(bootstrap);
            filter.destroy();
        }
        finally
        {
            Thread.currentThread().setContextClassLoader(cl);
        }
    }

}
