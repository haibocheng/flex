////////////////////////////////////////////////////////////////////////////////
//
//  ADOBE SYSTEMS INCORPORATED
//  Copyright 2008 Adobe Systems Incorporated
//  All Rights Reserved.
//
//  NOTICE: Adobe permits you to use, modify, and distribute this file
//  in accordance with the terms of the license agreement accompanying it.
//
////////////////////////////////////////////////////////////////////////////////
package flex.webtier.util;

import java.io.*;
import java.util.*;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.exception.MethodInvocationException;
import org.apache.velocity.exception.ParseErrorException;
import org.apache.velocity.exception.ResourceNotFoundException;

import flex.webtier.server.j2ee.html.DetectionSettings;
import flex.webtier.server.j2ee.html.EventsRenderer;
import flex.webtier.server.j2ee.MxmlContext;
import flex.webtier.server.j2ee.URLEncoder;
import flex2.compiler.util.VelocityManager;

public class RendererUtil
{
    private static final String TEMPLATE_PATH = "flex/webtier/util/";
    private MxmlContext context;
    
    public RendererUtil(MxmlContext context)
    {
        this.context = context;
    }
    
    public String getTemplateName()
    {
        if (context.getDetectionSettings().enabled())
        {
            return context.getDetectionSettings().useExpressInstall() ? "express.vm" : "client.vm";
        }
        
        return "nodetection.vm";        
    }
    
    // Common attributes between templates using tagsJS and templates using swfobject.
    private Map<String, Object> populateTemplateMap()
    {
        DetectionSettings detectionSettings = context.getDetectionSettings();

        Map<String, Object> map = new HashMap<String, Object>();
        map.put("application", getSwfId());

        map.put("historyCss", context.getRequest().getContextPath() + "/history/history.css");
        map.put("historyJS", context.getRequest().getContextPath() + "/history/history.js");
        map.put("historyEnabled", "" + context.getHistorySettings().enabled());

        map.put("bgcolor", context.getBackgroundColor());
        map.put("height", context.getHeight());
        map.put("width", context.getWidth());
        map.put("swf", getSwfWebPath());

        map.put("playerProductInstall", context.getRequest().getContextPath() + "/history/playerProductInstall.swf");

        map.put("version_major", "" + detectionSettings.getMajorVersion());
        map.put("version_minor", "" + detectionSettings.getMinorVersion());
        map.put("version_revision", "" + detectionSettings.getVersionRevision());
                
        map.put("alternateContent", context.isSecureRequest() ? detectionSettings.getAlternateContentInclude() : detectionSettings.getSecureAlternateContentInclude());

        if (context.hasWarnings() && context.showAllWarnings())
        {
            StringWriter writer = new StringWriter();
            EventsRenderer eventsRenderer = new EventsRenderer();
            try
            {
                eventsRenderer.renderEvents(context, writer);
            }
            catch (IOException e)
            {
            }
            map.put("events", writer.toString());
        }
        else
        {
            map.put("events", "");
        }
        
        return map;        
    }
 
    // JSP pages still use the old tagsJS.  There can be multiple tags per page.
    // swfobject requires some stuff in the <head> for each swf.
    public Map<String, Object> getTemplateMap()
    {
        Map<String, Object> map = populateTemplateMap();

        map.put("tagsJS", context.getRequest().getContextPath() + "/history/AC_OETags.js");

        map.put("swfId", getSwfId());
        map.put("swfName", getSwfWebName());

        map.put("flashvars", getFlashVars());
        
        // The history includes and some of the initial JS variables are only
        // written when the first tag on the page is written so that it's
        // not duplicated if there are multiple tags/swfs on the page.
        map.put("firstTime", "" + context.getFirstOnPage());
        
        if (Trace.mxml)
        {
            for (String k : map.keySet())
            {
                Trace.trace("template=" + getTemplateName() + " key=" + k + " value=" + map.get(k));
            }
        }

        return map;
    }


    // There is only one template for swfobject which can support express
    // install, client and nodetection by altering values of replacement
    // variables.
    public Map<String, Object> getSwfObjectTemplateMap()
    {
        Map<String, Object> map = populateTemplateMap();
        
        // Special character for Velocity.  It is possible to escape it with '\'
        // so it will parse without errors, but it doesn't remove it and then 
        // styles don't work correctly.
        map.put("poundChar", "#");
        
        map.put("title", context.getPageTitle());
        
        map.put("swfobjectJS", context.getRequest().getContextPath() + "/history/swfobject.js");

        // don't detect flash version
        if (!context.getDetectionSettings().enabled())
        {
            map.put("version_major", "0");
            map.put("version_minor", "0");
            map.put("version_revision", "0");
        }
        
        // don't do express install
        if (!context.getDetectionSettings().useExpressInstall())
        {
            map.put("expressInstallSwf", "");           
        }
     
        // any flashvars in name, value pairs
        map.put("flashvars", context.getFlashVars());
                   
        if (Trace.mxml)
        {
            for (String k : map.keySet())
            {
                Trace.trace("key=" + k + " value=" + map.get(k));
            }
        }
        
        return map;
    }

    public static void writeTemplate(String templateName,
            Map<String, Object> map, Writer writer)
            throws ResourceNotFoundException, ParseErrorException,
            MethodInvocationException, Exception
    {
        Template t = VelocityManager.getTemplate(TEMPLATE_PATH + templateName);
        VelocityContext c = VelocityManager.getCodeGenContext();
        for (String key : map.keySet())
        {
            c.put(key, map.get(key));
        }
        t.merge(c, writer);
    }

    private String getSwfWebPath()
    {
        String swfUri = context.getSwfUri();

        // cmurphy - ideally, we could just send 'src' as utf-8 but the player
        // currently can't handle this
        // encode only the filename itself
        String fullUri = swfUri.startsWith("/") ? context.getRequest().getContextPath() + swfUri : swfUri;
        String encodedSwfUri = encodeSrcUrl(fullUri);
        return encodedSwfUri;
    }

    private String getSwfWebName()
    {
        String webPath = getSwfWebPath();
        return webPath.substring(0, webPath.length() - 4);
    }

    private String getSwfId()
    {
        String swfId = context.getSwfId();

        if (swfId == null)
        {
            swfId = context.getSwfUri();

            // remove extra directories
            if (swfId.lastIndexOf("/") != -1)
            {
                swfId = swfId.substring(swfId.lastIndexOf("/") + 1);
            }

            // remove .mxml.swf string
            if (swfId.indexOf(".") != -1)
            {
                swfId = swfId.substring(0, swfId.indexOf("."));
            }
        }

        return swfId;
    }

    private static String encodeSrcUrl(String fullUri)
    {
        try
        {
            int length = fullUri.length();
            StringBuffer buf = new StringBuffer(length);
            for (int i = 0; i < length; i++)
            {
                char c = fullUri.charAt(i);
                if (c <= 127)
                {
                    buf.append(c);
                }
                else
                {
                    byte[] bytes = fullUri.substring(i, i + 1).getBytes("UTF-8");
                    for (int j = 0; j < bytes.length; j++)
                    {
                        int b = bytes[j] & 255;
                        buf.append('%');
                        if (b < 16)
                        {
                            buf.append('0');
                        }
                        buf.append(Integer.toHexString(b));
                    }
                }
            }
            return buf.toString();
        }
        catch (UnsupportedEncodingException e)
        {
            // will not happen because UTF-8 is required on all JVM's
            return fullUri;
        }
    }

    private String getFlashVars()
    {
        StringBuffer sb = new StringBuffer();

        // add additional flash vars specified via jsp tag
        Map additionalFlashVars = context.getFlashVars();
        Set entrySet;
        if (additionalFlashVars != null && (additionalFlashVars.entrySet() != null))
        {
            entrySet = additionalFlashVars.entrySet();
            for (Iterator i = entrySet.iterator(); i != null && i.hasNext();)
            {
                Map.Entry entry = (Map.Entry)i.next();
                String key = (String)entry.getKey();
                String value = (String)entry.getValue();

                if (sb.length() > 0)
                {
                    sb.append("&");
                }
                sb.append(URLEncoder.encode(key));
                if ((value != null) && (!value.equals("")))
                {
                    sb.append("=");
                    sb.append(URLEncoder.encode(value));
                }
            }
        }
        return sb.toString();
    }
}
