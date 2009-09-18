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

import flex.webtier.services.ServiceFactory;
import flex.webtier.server.j2ee.events.CompileEvent;
import flex.webtier.server.j2ee.events.SourceCodeLoader;
import flex.webtier.server.j2ee.html.DetectionSettings;
import flex.webtier.server.j2ee.html.HistorySettings;

import java.io.PrintWriter;
import java.util.Map;
import java.util.Vector;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class MxmlContext
{
    // set up by the MxmlServlet, SwfServlet, or JSP tag
    private HttpServletRequest request;
    private HttpServletResponse response;
    private PrintWriter responseWriter;
    private ServletContext servletContext;
    private long lastModifiedTime;
    private byte[] swfBuffer;
    private byte[] swdBuffer;
    private Vector events = new Vector();
    private boolean hasWarnings = false;
    private boolean hasErrors = false;
    private String height;
    private String width;
    private String pageTitle;
    private String backgroundColor;
    private boolean showAllWarnings;
    private boolean logSwfEvents = false;

    private String appPath;
    private String cacheKey;
    private String dependencyKey;
    private String sourceFileName;
    private String swfUri;
    private String swfId;
    private String objectUri;
    private String parentDir;

    private DetectionSettings detectionSettings;
    private HistorySettings historySettings;
    private Map flashVars;

    private SourceCodeLoader sourceCodeLoader;

    /** specific to jsp tags */
    private boolean firstOnPage;
    private boolean firstDetection;
    private boolean firstHistory;
    private String jspWidth;
    private String jspHeight;

    public void setAppPath(String appPath)
    {
        this.appPath = appPath;
    }

    public String getAppPath()
    {
        return appPath;
    }

    public void setCacheKey(String cacheKey)
    {
        this.cacheKey = cacheKey;
    }

    public String getCacheKey()
    {
        return cacheKey;
    }

    public void setDependencyKey(String dependencyKey)
    {
        this.dependencyKey = dependencyKey;
    }

    public String getDependencyKey()
    {
        return dependencyKey;
    }

    public void setDetectionSettings(DetectionSettings detectionSettings)
    {
        this.detectionSettings = detectionSettings;
    }

    public DetectionSettings getDetectionSettings()
    {
        return detectionSettings;
    }

    /** specific to jsp tags */
    public void setFirstOnPage(boolean firstOnPage)
    {
        this.firstOnPage = firstOnPage;
    }

    public boolean getFirstOnPage()
    {
        return firstOnPage;
    }

    public void setFirstDetection(boolean firstDetection)
    {
        this.firstDetection = firstDetection;
    }

    public boolean getFirstDetection()
    {
        return firstDetection;
    }

    public void setFirstHistory(boolean firstHistory)
    {
        this.firstHistory = firstHistory;
    }

    public boolean getFirstHistory()
    {
        return firstHistory;
    }

    public void setFlashVars(Map<String,String> flashVars)
    {
        this.flashVars = flashVars;
    }

    public Map<String, String> getFlashVars()
    {
         return flashVars;
    }

    public void setHistorySettings(HistorySettings historySettings)
    {
        this.historySettings = historySettings;
    }

    public HistorySettings getHistorySettings()
    {
        return historySettings;
    }

    public void setObjectUri(String objectUri)
    {
        this.objectUri = objectUri;
    }

    public String getObjectUri()
    {
        return objectUri;
    }

    public void setParentDir(String parentDir)
    {
        this.parentDir = parentDir;
    }

    public String getParentDir()
    {
        return parentDir;
    }

    public void setRequest(HttpServletRequest request)
    {
        this.request = request;
    }

    public HttpServletRequest getRequest()
    {
        return request;
    }

    public boolean isSecureRequest()
    {
        return request.isSecure();
    }

    public void setResponse(HttpServletResponse response)
    {
        this.response = response;
    }

    public HttpServletResponse getResponse()
    {
        return response;
    }

    public void setResponseWriter(PrintWriter responseWriter)
    {
        this.responseWriter = responseWriter;
    }

    public PrintWriter getResponseWriter()
    {
        if(responseWriter==null){
            try
            {
                return response.getWriter();
            }
            catch (Throwable t)
            {
                // if problem retrieving writer log warning and return null
                ServiceFactory.getLogger().logWarning("cant get writer from response " + request.getServletPath() + ": " + t.getMessage());
                return null;
            }
        }
        else
            return responseWriter;
    }

    public void setServletContext(ServletContext servletContext)
    {
        this.servletContext = servletContext;
    }

    public ServletContext getServletContext()
    {
        return servletContext;
    }

    public void setLastModifiedTime(long lastModifiedTime)
    {
        this.lastModifiedTime = lastModifiedTime;
    }

    public long getLastModifiedTime()
    {
        return lastModifiedTime;
    }

    public void setSwfBuffer(byte[] swfBuffer)
    {
        this.swfBuffer = swfBuffer;
    }

    public byte[] getSwfBuffer()
    {
        return swfBuffer;
    }

    public void setSwdBuffer(byte[] swdBuffer)
    {
        this.swdBuffer = swdBuffer;
    }

    public byte[] getSwdBuffer()
    {
        return swdBuffer;
    }

    public void setLogSwfEvents(boolean logSwfEvents)
    {
        this.logSwfEvents = logSwfEvents;
    }

    public boolean logSwfEvents()
    {
        return logSwfEvents;
    }

    public void addEvent(CompileEvent event)
    {
        switch (event.eventType)
        {
            case CompileEvent.EVT_WARNING:
            {
                hasWarnings = true;
                break;
            }
            case CompileEvent.EVT_ERROR:
            case CompileEvent.EVT_EXCEPTION:
            {
                hasErrors = true;
                break;
            }
        }

        events.add(event);
    }

    // the events are stored with compiled swf entries to ensure that subsequent
    // users of the swf see the same warnings
    // this method is used when initializing from the cached entry
    // events which are designated as one-time events aren't copied
    public void setEvents(Vector events)
    {
        for (int i = 0; i < events.size(); i++)
        {
            CompileEvent event = (CompileEvent)events.get(i);
            if (!event.isOneTime())
            {
                addEvent((CompileEvent)events.get(i));
            }
        }
    }

    public Vector getEvents()
    {
        return this.events;
    }

    public boolean hasWarnings()
    {
        return hasWarnings;
    }

    public boolean hasErrors()
    {
        return hasErrors;
    }

    public void setHeight(String height)
    {
        this.height = height;
    }

    public String getHeight()
    {
        return height;
    }

    public void setJspHeight(String height)
    {
        this.jspHeight = height;
    }

    public String getJspHeight()
    {
        return jspHeight;
    }

    public void setWidth(String width)
    {
        this.width = width;
    }

    public String getWidth()
    {
        return width;
    }

    public void setJspWidth(String width)
    {
        this.jspWidth = width;
    }

    public String getJspWidth()
    {
        return jspWidth;
    }

    public String getBackgroundColor()
    {
        return backgroundColor;
    }

    public void setBackgroundColor(String backgroundColor)
    {
        this.backgroundColor = backgroundColor;
    }

    public void setBackgroundColor(int bgcolor)
    {
        // convert from movie from to form for wrapper
        String hex = ("000000" + Integer.toHexString(bgcolor));
        hex = hex.substring(hex.length() - 6);
        this.backgroundColor = "#" + hex;
    }

    public void setPageTitle(String pageTitle)
    {
        this.pageTitle = pageTitle;
    }

    public String getPageTitle()
    {
        return pageTitle;
    }

    public boolean showAllWarnings()
    {
        return showAllWarnings;
    }

    public void setShowAllWarnings(boolean showAllWarnings)
    {
        this.showAllWarnings = showAllWarnings;
    }

    public void setSourceCodeLoader(SourceCodeLoader sourceCodeLoader)
    {
        this.sourceCodeLoader = sourceCodeLoader;
    }

    public SourceCodeLoader getSourceCodeLoader()
    {
        return sourceCodeLoader;
    }

    public String getSourceFileName()
    {
        return sourceFileName;
    }

    public void setSourceFileName(String sourceFileName)
    {
        this.sourceFileName = sourceFileName;
    }

    public String getSwfUri()
    {
        return swfUri;
    }

    public void setSwfUri(String swfUri)
    {
        this.swfUri = swfUri;
    }

    /** id/name used in the object/embed tag */
    public String getSwfId()
    {
        return swfId;
    }

    /** id/name used in the object/embed tag */
    public void setSwfId(String swfId)
    {
        this.swfId = swfId;
    }
}
