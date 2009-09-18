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
package flex.webtier.server.j2ee;

import flex.webtier.util.J2EEUtil;
import flex.webtier.services.ServiceFactory;
import flex.webtier.services.config.FlashPlayerConfiguration;
import flex.webtier.server.j2ee.events.LogOutputHandler;
import flex.webtier.server.j2ee.html.ErrorRenderer;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.SocketException;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletContext;

public class SwfRenderFilter extends MxmlFilter
{
    private String swfExt;
    private String swdExt;
    private long maxage;

    public SwfRenderFilter(String swfExt, String swdExt)
    {
        this.swfExt = swfExt;
        this.swdExt = swdExt;
        this.maxage = ServiceFactory.getConfigurator().getServerConfiguration().getCacheConfiguration().getHttpMaximumAge();
    }

    public void invoke(MxmlContext context) throws Throwable
    {
        if (context.hasErrors())
        {
            //String mxmlFileName = context.getRequest().getServletPath();
            //ServletContext servletContext = context.getServletContext();
            //String servletPath = context.getRequest().getServletPath();
            //String appRoot = new File(J2EEUtil.getRealPath(servletPath, servletContext)).getParent();
            context.getResponse().setStatus(500);
            ErrorRenderer errorRenderer = new ErrorRenderer();
            errorRenderer.render(context);
            HttpCache.setCacheHeaders(false, 0, -1, context.getResponse());
        }
        else
        {
            String path = context.getRequest().getServletPath();

            if (context.hasWarnings()  && context.logSwfEvents())
            {
                ServletContext servletContext = context.getServletContext();                
                String appRoot = new File(J2EEUtil.getRealPath(context.getRequest().getServletPath(), servletContext)).getParent();
                LogOutputHandler logOutputHandler = new LogOutputHandler(path, appRoot, context.getEvents(), context.getSourceCodeLoader());
                logOutputHandler.output();
            }

            if (path.endsWith(swfExt))
            {
                sendResponse(context, context.getResponse(), context.getSwfBuffer());
           }
            else if (path.endsWith(swdExt))
            {
                sendResponse(context, context.getResponse(), context.getSwdBuffer());
            }
        }
    }

    private void sendResponse(MxmlContext context, HttpServletResponse response, byte[] data)
    {
        try
        {
            response.setContentType(FlashPlayerConfiguration.CONTENT_TYPE);
            response.setContentLength(data.length);
            HttpCache.setCacheHeaders(true, maxage, context.getLastModifiedTime(), context.getResponse());
            OutputStream out = response.getOutputStream();
            out.write(data);
        }
        catch (SocketException se)
        {
            // ignore
        }
        catch (IOException ioe)
        {
            // ignore
        }
    }
}
