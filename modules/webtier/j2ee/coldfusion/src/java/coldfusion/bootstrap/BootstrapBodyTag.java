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

import javax.servlet.jsp.tagext.BodyTag;
import javax.servlet.jsp.tagext.BodyContent;
import javax.servlet.jsp.JspException;

/**
 * A base class for local JSP BodyTag implementations that need to delegate
 * calls to a concrete JSP BodyTag implementation loaded by ColdFusion's
 * bootstrap classloader.
 * 
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
