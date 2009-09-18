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

import javax.servlet.jsp.tagext.Tag;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

/**
 * @author Edwin Smith
 */
public abstract class BootstrapTag implements Tag
{
    protected final Tag tag;
    private Tag parent;

    public static ClassLoader bootstrap = BootstrapClassLoader.getClassLoader();

    protected BootstrapTag()
    {
        try
        {
            tag = (Tag) bootstrap.loadClass(getClass().getName()).newInstance();
        }
        catch (ClassNotFoundException e)
        {
            throw new NoClassDefFoundError(e.getMessage());
        }
        catch (InstantiationException e)
        {
            throw new InstantiationError(e.getMessage());
        }
        catch (IllegalAccessException e)
        {
            throw new IllegalAccessError(e.getMessage());
        }
    }

    public int doEndTag() throws JspException
    {
        Thread thread = Thread.currentThread();
        ClassLoader oldLoader = thread.getContextClassLoader();
        thread.setContextClassLoader(bootstrap);
        try
        {
            return tag.doEndTag();
        }
        finally
        {
            thread.setContextClassLoader(oldLoader);
        }
    }

    public int doStartTag() throws JspException
    {
        Thread thread = Thread.currentThread();
        ClassLoader oldLoader = thread.getContextClassLoader();
        thread.setContextClassLoader(bootstrap);
        try
        {
            return tag.doStartTag();
        }
        finally
        {
            thread.setContextClassLoader(oldLoader);
        }
    }

    public void release()
    {
        Thread thread = Thread.currentThread();
        ClassLoader oldLoader = thread.getContextClassLoader();
        thread.setContextClassLoader(bootstrap);
        try
        {
            tag.release();
        }
        finally
        {
            thread.setContextClassLoader(oldLoader);
        }
    }

    public void setPageContext(PageContext pageContext)
    {
        Thread thread = Thread.currentThread();
        ClassLoader oldLoader = thread.getContextClassLoader();
        thread.setContextClassLoader(bootstrap);
        try
        {
            tag.setPageContext(pageContext);
        }
        finally
        {
            thread.setContextClassLoader(oldLoader);
        }
    }

    public Tag getParent()
    {
        return parent;
    }

    public void setParent(Tag parent)
    {
        Thread thread = Thread.currentThread();
        ClassLoader oldLoader = thread.getContextClassLoader();
        thread.setContextClassLoader(bootstrap);
        try
        {
            this.parent = parent;
            if (parent instanceof BootstrapTag)
            {
                BootstrapTag bparent = (BootstrapTag) parent;
                tag.setParent(bparent.tag);
            }
            else
            {
                tag.setParent(parent);
            }
        }
        finally
        {
            thread.setContextClassLoader(oldLoader);
        }
    }
}
