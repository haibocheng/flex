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

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import java.util.Enumeration;

/**
 * Wraps a ServletConfig and uses the wrapped method for all but getServletContext()
 *
 * @author Brian Deitte
 */
public class ATGServletConfig implements ServletConfig
{
    private ServletConfig wrappedConfig;
    private ServletContext context;

    public ATGServletConfig(ServletConfig config)
    {
        this.wrappedConfig = config;
        this.context = new ATGServletContext(config.getServletContext());
    }

    public String getInitParameter(String name)
    {
        return wrappedConfig.getInitParameter(name);
    }

    public Enumeration getInitParameterNames()
    {
        return wrappedConfig.getInitParameterNames();
    }

    public ServletContext getServletContext()
    {
        return context;
    }

    public String getServletName()
    {
        return wrappedConfig.getServletName();
    }
}
