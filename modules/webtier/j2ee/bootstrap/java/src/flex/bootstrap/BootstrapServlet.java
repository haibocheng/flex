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

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.ServletConfig;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;

/**
 * Servlet to load the real servlet using the BootstrapClassLoader.  Also creates a wrapped ServletConfig
 * and ServletRequest if needed
 *
 * @author Brian Deitte
 */
public class BootstrapServlet implements Servlet
{
    private Servlet servlet;
    private ServletConfig config;

    private static final HashMap classNameMap = new HashMap();
    static
    {
        classNameMap.put("flex2.server.j2ee.MxmlServlet", "flex.webtier.server.j2ee.MxmlServlet");
        classNameMap.put("flex2.server.j2ee.SwfServlet", "flex.webtier.server.j2ee.SwfServlet");
        classNameMap.put("flex.server.j2ee.ForbiddenServlet", "flex.webtier.server.j2ee.ForbiddenServlet");
        classNameMap.put("flex.server.j2ee.filemanager.FileManagerServlet", "flex.webtier.server.j2ee.filemanager.FileManagerServlet");
    }

    public void init(ServletConfig c) throws ServletException
    {
        this.config = J2EEWrapper.getServletConfig(c);
        String servletClass = config.getInitParameter("servlet.class");
        String supportLegacy = config.getInitParameter("supportLegacyClassNames");

        // Translate the name of Flex 2.0 webtier servlet classes into
        // the equivalent classes for Flex 2.5 (and later).
        if (servletClass != null && supportLegacyClassNames(supportLegacy))
        {
            servletClass = servletClass.trim();
            Iterator it = classNameMap.keySet().iterator();
            while (it.hasNext())
            {
                String oldClass = (String)it.next();
                String newClass = (String)classNameMap.get(oldClass);
                if (servletClass.equals(oldClass))
                {
                    servletClass = newClass;
                }
            }
        }

        // Detect the underlying appserver to use the appServer specific properties files for loading specific classes ourselves or via the
        // System ClassLoader
        try
        {
            ClassLoader cl = Thread.currentThread().getContextClassLoader();

            try
            {
                ClassLoader bootstrap = BootstrapClassLoader.getClassLoader(config.getServletContext());
                BootstrapClassLoader.incrementReferences();
                Thread.currentThread().setContextClassLoader(bootstrap);

                servlet = (Servlet) bootstrap.loadClass(servletClass).newInstance();
                servlet.init(getServletConfig());
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

    public void service(ServletRequest req, ServletResponse resp) throws ServletException, IOException
    {
        req = J2EEWrapper.getRequest(req);
        ClassLoader cl = Thread.currentThread().getContextClassLoader();

        try
        {
            ClassLoader bootstrap = BootstrapClassLoader.getClassLoader(config.getServletContext());
            Thread.currentThread().setContextClassLoader(bootstrap);
            servlet.service(req, resp);
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
            ClassLoader bootstrap = BootstrapClassLoader.getClassLoader(config.getServletContext());
            Thread.currentThread().setContextClassLoader(bootstrap);
            servlet.destroy();
            if (bootstrap instanceof BootstrapClassLoader)
            	((BootstrapClassLoader)bootstrap).clear();     
        }
        finally
        {
            Thread.currentThread().setContextClassLoader(cl);
        }
    }

    public ServletConfig getServletConfig()
    {
        return config;
    }

    public String getServletInfo()
    {
        return "Bootstrap servlet " + (config == null ? "" : " for " + config.getServletName());
    }

    private static boolean supportLegacyClassNames(String token)
    {
        boolean support = true;

        if (token != null && !Boolean.getBoolean(token))
            support = false;

        return support;
    }
}
