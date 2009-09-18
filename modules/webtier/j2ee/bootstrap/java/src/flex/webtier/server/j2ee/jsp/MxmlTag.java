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
package flex.webtier.server.j2ee.jsp;

import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.tagext.BodyTag;

/**
 * @author Edwin Smith
 * @author Cathy Reilly
 */
public interface MxmlTag extends BodyTag
{
    void setSource(String source);
    void setFlashVar(Object name, Object value) throws JspTagException;
    void setId(String id);
    void setHeight(String height) throws JspTagException;
    void setWidth(String width) throws JspTagException;
    void setUseHistoryManagement(String useHistoryManagement) throws JspTagException;
    void setUsePlayerDetection(String usePlayerDetection) throws JspTagException;
    void setUseExpressInstall(String useExpressInstall) throws JspTagException;
    void setAlternateContentPage(String alternateContentPage) throws JspTagException;
}
