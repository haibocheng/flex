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
package flex.webtier.server.j2ee.html;

import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

import flex.webtier.server.j2ee.MxmlContext;
import flex.webtier.util.RendererUtil;

public class MxmlRenderer
{
    public void render(MxmlContext context) throws Exception
    {
        context.getResponse().setContentType("text/html;charset=UTF-8");
        
        RendererUtil util = new RendererUtil(context);
        RendererUtil.writeTemplate("index.template.vm", util.getSwfObjectTemplateMap(), context.getResponseWriter());
    }
}
