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

import flex.webtier.server.j2ee.cache.CacheKeyUtils;
import flex.webtier.services.ServiceFactory;
import flex.webtier.services.config.ServerConfiguration;
import flex2.compiler.util.ThreadLocalToolkit;
import flex.webtier.server.j2ee.events.CompileEventLogger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.ServletContext;

public class CompileAgent
{
    public void setupMxmlContextKeys(ServletContext servletContext, MxmlContext context, HttpServletRequest request)
    {
        RequestContext requestContext = new RequestContext(servletContext, request);
        context.setDependencyKey(requestContext.getDependencyKey());
        context.setCacheKey(CacheKeyUtils.getUrlData(context.getRequest()).cacheKey);
        context.setSourceFileName(requestContext.getSourceFileName());
        context.setSwfUri(requestContext.getSwfUri());
        context.setFlashVars(requestContext.getFlashVars());
        context.setObjectUri(requestContext.getObjectUri());
    }

    public CompileEventLogger setupCompileEventLogger(MxmlContext context, HttpServletRequest request)
    {
        ServerConfiguration configuration = ServiceFactory.getConfigurator().getServerConfiguration();
        boolean productionMode = configuration.isProductionMode();
        boolean processDebugQueryParams = configuration.getDebuggingConfiguration().processDebugQueryParams();

        String param = request.getParameter("showAllWarnings");
        boolean showAllWarnings = false;
        if ((!productionMode) && (processDebugQueryParams))
        {
            if ((param != null) && ("true".equalsIgnoreCase(param)))
            {
                showAllWarnings = true;
            }
            else if ((param != null) && ("false".equalsIgnoreCase(param)))
            {
                showAllWarnings = false;
            }
            else
            {
                showAllWarnings = configuration.getDebuggingConfiguration().showAllWarnings();
            }
        }

        context.setShowAllWarnings(showAllWarnings);
        CompileEventLogger logger = new CompileEventLogger(context);
        ThreadLocalToolkit.setLogger(logger);

        return logger;
    }
}
