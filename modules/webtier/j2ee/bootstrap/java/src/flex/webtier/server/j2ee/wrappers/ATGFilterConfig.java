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

import javax.servlet.ServletContext;
import javax.servlet.FilterConfig;
import java.util.Enumeration;

/**
 * Wraps a FileConfig and uses the wrapped method for all but getServletContext()
 *
 * @author Brian Deitte
 */
public class ATGFilterConfig implements FilterConfig
{
    private FilterConfig wrappedConfig;
    private ServletContext context;

    public ATGFilterConfig(FilterConfig config)
    {
        this.wrappedConfig = config;
        this.context = new ATGServletContext(config.getServletContext());
    }

    public String getFilterName()
    {
        return wrappedConfig.getFilterName();
    }

    public ServletContext getServletContext()
    {
        return context;
    }

    public String getInitParameter(String s)
    {
        return wrappedConfig.getInitParameter(s);
    }

    public Enumeration getInitParameterNames()
    {
        return wrappedConfig.getInitParameterNames();
    }
}
