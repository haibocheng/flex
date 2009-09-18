/*************************************************************************
 * 
 * ADOBE CONFIDENTIAL
 * __________________
 * 
 *  [2002] - [2007] Adobe Systems Incorporated 
 *  All Rights Reserved.
 * 
 * NOTICE:  All information contained herein is, and remains
 * the property of Adobe Systems Incorporated and its suppliers,
 * if any.  The intellectual and technical concepts contained
 * herein are proprietary to Adobe Systems Incorporated
 * and its suppliers and may be covered by U.S. and Foreign Patents,
 * patents in process, and are protected by trade secret or copyright law.
 * Dissemination of this information or reproduction of this material
 * is strictly forbidden unless prior written permission is obtained
 * from Adobe Systems Incorporated.
 */
package coldfusion.bootstrap;

import javax.servlet.jsp.tagext.Tag;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

/**
 * A base class for local JSP Tag implementations that need to delegate calls
 * to a concrete JSP Tag implementation loaded by ColdFusion's bootstrap
 * classloader.
 * 
 * @author Edwin Smith
 */
public abstract class BootstrapTag implements Tag
{
    protected final Tag tag;
    private Tag parent;

    public static ClassLoader bootstrap = BootstrapClassLoader.instance();

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
