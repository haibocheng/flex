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
package flex.webtier.services;

import flex.webtier.util.Trace;

import java.util.Properties;

public class VersionInfo
{
    public static byte FLEX_MAJOR_VERSION = 4;
    public static byte FLEX_MINOR_VERSION = 0;

    //Cache this info as it should not change during the time class is loaded
    static String BUILD_MESSAGE;
    static String BUILD_NUMBER_STRING;
    static long BUILD_NUMBER;

    public static String buildMessage()
    {
        if (BUILD_MESSAGE == null)
        {
            try
            {
                //Ensure we've parsed build info
                getBuild();

                if (BUILD_NUMBER_STRING == null || BUILD_NUMBER_STRING == "")
                {
                    BUILD_MESSAGE = "Adobe Flex Web Tier Compiler Build: development";
                }
                else
                {
                    BUILD_MESSAGE = "Adobe Flex Web Tier Compiler Build: " + BUILD_NUMBER_STRING;
                }
            }
            catch (Throwable t)
            {
                BUILD_MESSAGE = "Adobe Flex Web Tier Compiler Build: information unavailable";
            }
        }

        return BUILD_MESSAGE;
    }

    public static long getBuildAsLong()
    {
        if (BUILD_NUMBER == 0)
        {
            getBuild();

            if (BUILD_NUMBER_STRING != null && !BUILD_NUMBER_STRING.equals(""))
            {
                try
                {
                    BUILD_NUMBER = Long.parseLong(BUILD_NUMBER_STRING);
                }
                catch (NumberFormatException nfe)
                {
                    // ignore, just return 0
                }
            }
        }

        return BUILD_NUMBER;
    }

    public static String getBuild()
    {
        if (BUILD_NUMBER_STRING == null)
        {
            BUILD_NUMBER_STRING = "";

            try
            {
                Properties p = new Properties();
                p.load(VersionInfo.class.getResourceAsStream("version.properties"));                
                String build = p.getProperty("build");
                if ((build != null) && (! build.equals("")))
                {
                    BUILD_NUMBER_STRING = build;
                }
            }
            catch (Throwable t)
            {
                if (Trace.error)
                {
                    t.printStackTrace();
                }
            }
        }

        return BUILD_NUMBER_STRING;
    }
}
