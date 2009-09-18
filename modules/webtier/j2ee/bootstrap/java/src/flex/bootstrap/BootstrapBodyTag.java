////////////////////////////////////////////////////////////////////////////////
//
//  ADOBE SYSTEMS INCORPORATED
//  Copyright 2003-2007 Adobe Systems Incorporated
//  All Rights Reserved.
//
//  NOTICE: Adobe permits you to use, modify, and distribute this file
//  in accordance with the terms of the license agreement accompanying it.
//
////////////////////////////////////////////////////////////////////////////////
package flex.bootstrap;

import javax.servlet.jsp.tagext.BodyTag;
import javax.servlet.jsp.tagext.BodyContent;
import javax.servlet.jsp.JspException;

/**
 * @author Edwin Smith
 */
public abstract class BootstrapBodyTag extends BootstrapTag implements BodyTag
{
    BodyTag bodyTag = (BodyTag) tag;

    public void doInitBody() throws JspException
    {
        Thread thread = Thread.currentThread();
        ClassLoader oldLoader = thread.getContextClassLoader();
        thread.setContextClassLoader(bootstrap);
        try
        {
            bodyTag.doInitBody();
        }
        finally
        {
            thread.setContextClassLoader(oldLoader);
        }
    }

    public void setBodyContent(BodyContent bodyContent)
    {
        Thread thread = Thread.currentThread();
        ClassLoader oldLoader = thread.getContextClassLoader();
        thread.setContextClassLoader(bootstrap);
        try
        {
            bodyTag.setBodyContent(bodyContent);
        }
        finally
        {
            thread.setContextClassLoader(oldLoader);
        }
    }

    public int doAfterBody() throws JspException
    {
        Thread thread = Thread.currentThread();
        ClassLoader oldLoader = thread.getContextClassLoader();
        thread.setContextClassLoader(bootstrap);
        try
        {
            return bodyTag.doAfterBody();
        }
        finally
        {
            thread.setContextClassLoader(oldLoader);
        }
    }
}
