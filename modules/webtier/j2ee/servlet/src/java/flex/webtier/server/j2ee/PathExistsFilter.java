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
package flex.webtier.server.j2ee;

import flex.webtier.server.j2ee.events.CompileEvent;

public class PathExistsFilter extends MxmlFilter
{
    public void invoke(MxmlContext context) throws Throwable
    {
        String mxmlFileName = context.getSourceFileName();

        if (context.getSourceFileName() != null)
        {
            try
            {
                PathValidator.checkFileExists(mxmlFileName, context.getRequest());
            }
            catch (Throwable t)
            {
                context.addEvent(new CompileEvent(t));
            }

            if (next != null)
            {
                next.invoke(context);
            }
        }
        else
        {
            assert false : "Be sure to set MxmlContext.sourceFileName before invoking this filter.";
        }
    }
}
