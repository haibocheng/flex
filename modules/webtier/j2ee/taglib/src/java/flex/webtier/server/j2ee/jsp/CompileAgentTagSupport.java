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

import flex.webtier.server.j2ee.CompileAgent;
import flex.webtier.server.j2ee.MxmlContext;
import flex.webtier.server.j2ee.events.CompileEventLogger;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.tagext.BodyTagSupport;

public abstract class CompileAgentTagSupport extends BodyTagSupport
{
    private CompileAgent compileAgent;

    public abstract void setSource(String source);

    public CompileAgentTagSupport()
    {
        compileAgent = new CompileAgent();
    }

    protected CompileEventLogger setupCompileEventLogger(MxmlContext context, HttpServletRequest request)
    {
       return compileAgent.setupCompileEventLogger(context, request);
    }
}