////////////////////////////////////////////////////////////////////////////////
//
//  ADOBE SYSTEMS INCORPORATED
//  Copyright 2009 Adobe Systems Incorporated
//  All Rights Reserved.
//
//  NOTICE: Adobe permits you to use, modify, and distribute this file
//  in accordance with the terms of the license agreement accompanying it.
//
////////////////////////////////////////////////////////////////////////////////

package flex2.compiler.fxg;

import com.adobe.fxg.util.FXGLogger;
import com.adobe.internal.fxg.util.AbstractLogger;

import flex2.compiler.Logger;
import flex2.compiler.util.ThreadLocalToolkit;

/**
 * An adapter to bridge the FXGUtils and Flex compiler logging systems.
 * 
 * @author Peter Farland
 */
public class FlexLoggerAdapter extends AbstractLogger
{
    public FlexLoggerAdapter(int level)
    {
        super(level);
    }

    public void log(int level, Object message, Throwable t, String location, int line, int column)
    {
        Logger delegateLogger = ThreadLocalToolkit.getLogger();
        if (delegateLogger != null)
        {
            String messageString = message != null ? message.toString() : null;
            if (level == FXGLogger.ERROR)
            {
                delegateLogger.logError(location, line, column, messageString);
            }
            else if (level == FXGLogger.WARN)
            {
                delegateLogger.logWarning(location, line, column, messageString);
            }
            else if (level == FXGLogger.INFO)
            {
                delegateLogger.logInfo(location, line, column, messageString);
            }
            else if (level == FXGLogger.DEBUG)
            {
                delegateLogger.logDebug(location, line, column, messageString);
            }
        }
    }
} 
