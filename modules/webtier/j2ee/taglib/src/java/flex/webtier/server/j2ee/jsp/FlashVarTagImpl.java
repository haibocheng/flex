////////////////////////////////////////////////////////////////////////////////
//
//  ADOBE SYSTEMS INCORPORATED
//  Copyright 2006-2007 Adobe Systems Incorporated
//  All Rights Reserved.
//
//  NOTICE: Adobe permits you to use, modify, and distribute this file
//  in accordance with the terms of the license agreement accompanying it.
//
////////////////////////////////////////////////////////////////////////////////
package flex.webtier.server.j2ee.jsp;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.Tag;
import javax.servlet.jsp.tagext.TagSupport;

/**
 * This tag is used to supply additional parameters to FlashTag. These are represented in the emitted HTML
 * as both child PARAM tags to the OBJECT tag and custom attributes to the EMBED tag.
 */
public class FlashVarTagImpl extends TagSupport implements FlashVarTag
{
	private String name = null;
	private Object value = null;

	/**
	 * Sets the name of this parameter.
	 */
	public void setName(String name)
	{
		this.name = name;
	}

	/**
	 * Sets the value of this parameter.
	 */
	public void setValue(Object value)
	{
		this.value = value;
	}

	public int doStartTag() throws JspException
	{
		Tag t = getParent();
		if (t instanceof MxmlTag)
		{
			MxmlTag parent = (MxmlTag) t;
			parent.setFlashVar(name, value);
		}
        else
        {
            throw new JspException("flashvar tag may not appear in this context");
        }

		return SKIP_BODY;
	}

	public int doEndTag() throws JspException
	{
		return EVAL_PAGE;                                                 
	}


	public void release()
	{
		name = null;
		value = null;
		super.release();
	}

}
