////////////////////////////////////////////////////////////////////////////////
//
//  ADOBE SYSTEMS INCORPORATED
//  Copyright 2006-2007 Adobe Systems Incorporated
//  All Rights Reserved.
//
//  NOTICE: Adobe permits you to use, modify, and distribute this file
//  in accordance with the terms of the license agreement accompanying it.
//
////////////////////////////////////////////////////////////////////////////////
package flex.webtier.server.j2ee.jsp;

import flex.webtier.services.ServiceFactory;
import flex.webtier.server.j2ee.MxmlFilter;
import flex.webtier.server.j2ee.MxmlContext;
import flex.webtier.server.j2ee.html.ErrorRenderer;

public class JspRenderFilter extends MxmlFilter
{
    private long maxage;

    public JspRenderFilter()
    {
        this.maxage = ServiceFactory.getConfigurator().getServerConfiguration().getCacheConfiguration().getHttpMaximumAge();
    }

    public void invoke(MxmlContext context) throws Throwable
    {
        if (context.hasErrors())
        {
            context.getResponse().setStatus(500);
            ErrorRenderer errorRenderer = new ErrorRenderer();
            errorRenderer.render(context);
        }
        else
        {
            JspRenderer jspRenderer = new JspRenderer(context);
            jspRenderer.render(context);
        }
    }
}
