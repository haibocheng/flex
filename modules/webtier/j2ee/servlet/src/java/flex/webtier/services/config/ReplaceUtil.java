////////////////////////////////////////////////////////////////////////////////
//
//  ADOBE SYSTEMS INCORPORATED
//  Copyright 2004-2007 Adobe Systems Incorporated
//  All Rights Reserved.
//
//  NOTICE: Adobe permits you to use, modify, and distribute this file
//  in accordance with the terms of the license agreement accompanying it.
//
////////////////////////////////////////////////////////////////////////////////
package flex.webtier.services.config;

import flash.util.StringUtils;
import flex.webtier.compiler.ConfigConstants;

/**
 * Methods for replacing {context.root} in config files
 * @author Brian Deitte
 */
public class ReplaceUtil
{
    /**
     * replace {context.root}
     */
    public static String replaceContextRoot(String url, String contextRoot)
    {
        int i = url.indexOf(ConfigConstants.TOKEN_CONTEXT_ROOT);
        if (i != -1)
        {
            String contextSub = contextRoot;
            if (i != 0 && url.charAt(i -1) == '/' && contextSub.length() != 0)
            {
                // get a setup like localhost/{context.root} to work.  We check
                // for a previous '/' and don't create a never-wanted two slashes in a row
                contextSub = contextRoot.substring(1);
            }
            url = StringUtils.substitute(url, ConfigConstants.TOKEN_CONTEXT_ROOT, contextSub);
        }
        return url;
    }
}
