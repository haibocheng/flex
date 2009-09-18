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
package flex.webtier.server.j2ee;

import flex.webtier.services.ServiceFactory;
import flex.webtier.server.j2ee.events.XmlOutputHandler;
import flex.webtier.server.j2ee.html.ErrorRenderer;
import flex.webtier.server.j2ee.html.MxmlRenderer;
import flex.webtier.util.J2EEUtil;
import flex.webtier.util.Trace;

import java.io.File;
import java.io.PrintWriter;
import java.util.Vector;
import javax.servlet.ServletContext;

public class RenderFilter extends MxmlFilter
{
    private long maxage;

    public RenderFilter()
    {
        this.maxage = ServiceFactory.getConfigurator().getServerConfiguration().getCacheConfiguration().getHttpMaximumAge();
    }

    public void invoke(MxmlContext context) throws Throwable
    {
        // todo: it would be extremely helpful if any exceptions were caught and logged by this point
        Vector events = context.getEvents();
        if (isXmlRequest(context))
        {
            if (Trace.mxml)
                Trace.trace("isXmlRequest");
            HttpCache.setCacheHeaders(true, maxage, context.getLastModifiedTime(), context.getResponse());
            context.getResponse().setContentType("text/xml");
            PrintWriter writer = context.getResponseWriter();
            // todo: should these be in MxmlContext?
            String mxmlFileName = context.getRequest().getServletPath();
            // todo: is this the app root or the web root?
            ServletContext servletContext = context.getServletContext();
            String servletPath = context.getRequest().getServletPath();
            String appRoot = new File(J2EEUtil.getRealPath(servletPath, servletContext)).getParent();
            XmlOutputHandler xmlOutputHandler = new XmlOutputHandler(writer, mxmlFileName, appRoot, events, context.getSourceCodeLoader());
            xmlOutputHandler.output();
        }
        else
        {
            if (context.hasErrors())
            {
                if (Trace.mxml)
                    Trace.trace("context.hasErrors()");
                HttpCache.setCacheHeaders(false, 0, -1, context.getResponse());
                context.getResponse().setStatus(500);
                ErrorRenderer errorRenderer = new ErrorRenderer();
                errorRenderer.render(context);
            }
            else
            {
                if (Trace.mxml)
                    Trace.trace("context ok");
                HttpCache.setCacheHeaders(true, maxage, context.getLastModifiedTime(), context.getResponse());
                MxmlRenderer mxmlRenderer = new MxmlRenderer();
                mxmlRenderer.render(context);
            }
        }
    }

    public boolean isObjectRequest(MxmlContext context)
    {
        boolean objects = false;
        if (context.getRequest().getParameter("objectWrapperJs") != null)
        {
            objects = true;
        }

        return objects;
    }

    public boolean isXmlRequest(MxmlContext context)
    {
        boolean errors = false;
        if (context.getRequest().getParameter("errors") != null)
        {
            errors = true;
        }
        return errors;
    }
}
