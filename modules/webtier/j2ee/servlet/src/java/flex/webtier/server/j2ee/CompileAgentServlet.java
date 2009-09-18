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
package flex.webtier.server.j2ee;

import flex.webtier.util.PathResolver;
import flex.webtier.server.j2ee.events.FileSourceCodeLoader;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletContext;

public class CompileAgentServlet extends HttpServlet
{
    static final long serialVersionUID = -4927576014934325954L;
    private CompileAgent compileAgent;

    public void init(ServletConfig servletConfig) throws ServletException
    {
        super.init(servletConfig);
        compileAgent = new CompileAgent();
    }

    /**
     * this method is a bridge between the legacy RequestContext which generates various
     * names (swf name, source name, swf id, swf web path) from request
     */
    protected void setupMxmlContextKeys(ServletContext servletContext, MxmlContext context, HttpServletRequest request)
    {
        compileAgent.setupMxmlContextKeys(servletContext, context, request);        
    }

    protected void setupCompileEventLogger(MxmlContext context, HttpServletRequest request)
    {
        compileAgent.setupCompileEventLogger(context, request);
    }

    protected void setupSourceCodeLoader(MxmlContext context)
    {
        PathResolver pathResolver = PathResolver.getThreadLocalPathResolver();
        FileSourceCodeLoader cl = new FileSourceCodeLoader(pathResolver);
        context.setSourceCodeLoader(cl);
    }
}
