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
package flex.webtier.util;

/**
 *
 * @author Brian Deitte
 */
public class ServerUtil
{
    public static boolean isDotNet()
    {
        return System.getProperty("flex.platform.CLR") != null;
    }

    public static String getFlexConfig()
    {
        if (isDotNet())
            return "Flex.config";
        else
            return "flex-config.xml";
    }

}
