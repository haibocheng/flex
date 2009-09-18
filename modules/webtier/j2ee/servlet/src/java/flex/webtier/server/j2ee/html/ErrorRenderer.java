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

/** This class is responsible for writing a HTML response when the compile generated errors
 * or exceptions.
 */
public class ErrorRenderer
{
    EventsRenderer eventsRenderer = new EventsRenderer();

    public void render(MxmlContext context) throws Exception
    {
        context.getResponse().setContentType("text/html;charset=UTF-8");

        StringWriter writer = new StringWriter();
        eventsRenderer.renderEvents(context, writer);

        Map<String, Object> map = new HashMap<String, Object>();
        map.put("events", writer.toString());
        
        RendererUtil.writeTemplate("error.vm", map, context.getResponseWriter());       
    }
}