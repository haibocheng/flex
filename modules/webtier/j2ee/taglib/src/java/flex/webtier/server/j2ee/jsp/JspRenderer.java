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
package flex.webtier.server.j2ee.jsp;

import flex.webtier.server.j2ee.MxmlContext;
import flex.webtier.util.RendererUtil;
//import flex.webtier.util.VelocityManager;

public class JspRenderer
{
    public JspRenderer(MxmlContext context)
    {
        // can't do this earlier or it conflicts with the cached values for
        // width/height
        if (context.getJspHeight() != null)
        {
            context.setHeight(context.getJspHeight());
        }
        if (context.getJspWidth() != null)
        {
            context.setWidth(context.getJspWidth());
        }

    }

    public void render(MxmlContext context) throws Exception
    {
        RendererUtil util = new RendererUtil(context);
        RendererUtil.writeTemplate(util.getTemplateName(), util.getTemplateMap(), context.getResponseWriter());
    }
}
