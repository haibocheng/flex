////////////////////////////////////////////////////////////////////////////////
//
//  ADOBE SYSTEMS INCORPORATED
//  Copyright 2005-2008 Adobe Systems Incorporated
//  All Rights Reserved.
//
//  NOTICE: Adobe permits you to use, modify, and distribute this file
//  in accordance with the terms of the license agreement accompanying it.
//
////////////////////////////////////////////////////////////////////////////////
package flex.webtier.server.j2ee.wrappers;

import flex.webtier.util.J2EEUtil;

import javax.servlet.ServletContext;
import javax.servlet.RequestDispatcher;
import javax.servlet.Servlet;
import javax.servlet.ServletException;
import java.util.Enumeration;
import java.util.Set;
import java.net.MalformedURLException;
import java.net.URL;
import java.io.InputStream;

/**
 * Wraps a ServletContext and uses the wrapped method for all but getRealPath 
 *
 * @author Brian Deitte
 */
public class ATGServletContext implements ServletContext
{
    private ServletContext context;

    public ATGServletContext(ServletContext context) {
        this.context = context;
    }

	public String getContextPath()
	{
		return context.getContextPath();
	}
	
    public void setContext(ServletContext context) {
        this.context = context;
    }

    public ServletContext getContext() {
        return context;
    }

    public Object getAttribute(String name) {
        return context.getAttribute(name);
    }

    public Enumeration getAttributeNames() {
        return context.getAttributeNames();
    }

    public ServletContext getContext(String url) {
        return context.getContext(url);
    }

    public String getInitParameter(String name) {
        return context.getInitParameter(name);
    }

    public Enumeration getInitParameterNames() {
        return context.getInitParameterNames();
    }

    public int getMajorVersion() {
        return context.getMajorVersion();
    }

    public String getMimeType(String type) {
        return context.getMimeType(type);
    }

    public int getMinorVersion() {
        return context.getMinorVersion();
    }

    public RequestDispatcher getNamedDispatcher(String name) {
        return context.getNamedDispatcher(name);
    }

    public String getRealPath(String url) {
        return J2EEUtil.getRealPath(url, context);
    }

    public RequestDispatcher getRequestDispatcher(String url) {
        return context.getRequestDispatcher(url);
    }

    public URL getResource(String path) throws MalformedURLException {
        return context.getResource(path);
    }

    public InputStream getResourceAsStream(String path) {
        return context.getResourceAsStream(path);
    }

    public Set getResourcePaths(String path) {
        return context.getResourcePaths(path);
    }

    public String getServerInfo() {
        return context.getServerInfo();
    }

    /**
     * @deprecated
     */
    public Servlet getServlet(String name) throws ServletException {
        return context.getServlet(name);
    }

    public String getServletContextName() {
        return context.getServletContextName();
    }

    /**
     * @deprecated
     */
    public Enumeration getServletNames() {
        return context.getServletNames();
    }

    /**
     * @deprecated
     */
    public Enumeration getServlets() {
        return context.getServlets();
    }

    /**
     * @deprecated
     */
    public void log(Exception exception, String msg) {
        context.log(exception, msg);
    }

    public void log(String msg) {
        context.log(msg);
    }

    public void log(String msg, Throwable throwable) {
        context.log(msg, throwable);
    }

    public void removeAttribute(String name) {
        context.removeAttribute(name);
    }

    public void setAttribute(String name, Object value) {
        context.setAttribute(name, value);
    }
}
