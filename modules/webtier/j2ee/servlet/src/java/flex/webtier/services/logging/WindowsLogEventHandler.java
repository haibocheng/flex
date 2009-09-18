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
package flex.webtier.services.logging;

/*
 * This class serves as an integration point for Flex.NET.
 * It has no used in Flex for Java.
 */
public class WindowsLogEventHandler extends LogEventHandler
{
    public WindowsLogEventHandler(String logName) {}
    
    public boolean logEvent(LogEvent event)
    {
        return true;
    }
}
