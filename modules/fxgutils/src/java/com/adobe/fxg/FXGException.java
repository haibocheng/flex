// //////////////////////////////////////////////////////////////////////////////
//
// ADOBE SYSTEMS INCORPORATED
// Copyright 2008 Adobe Systems Incorporated
// All Rights Reserved.
//
// NOTICE: Adobe permits you to use, modify, and distribute this file
// in accordance with the terms of the license agreement accompanying it.
//
// //////////////////////////////////////////////////////////////////////////////

package com.adobe.fxg;

import java.text.MessageFormat;
import java.util.Enumeration;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * Base type for exceptions encountered while processing FXG.
 */
public class FXGException extends RuntimeException
{
    private static final long serialVersionUID = -7393979231178285695L;
    private static final Locale DEFAULT_LOCALE = Locale.ENGLISH;
    private static final String DEFAULT_RESOURCE_BASE_NAME = "com.adobe.fxg.FXGException";

    private static Locale locale = DEFAULT_LOCALE;
    private static String resourceBaseName = DEFAULT_RESOURCE_BASE_NAME;
    private static ResourceBundle exceptionResourceBundle = null;
    private Object[] arguments;
    private String message;
    private int lineNumber;
    private int columnNumber;

    public FXGException()
    {
        super();
        arguments = null;
        message = null;
        lineNumber = -1;
        columnNumber = -1;
    }

    public FXGException(Throwable cause)
    {
        super(cause);
        this.arguments = null;
        message = null;
        lineNumber = -1;
        columnNumber = -1;
    }

    public FXGException(String message, Throwable cause, Object... arguments)
    {
        super(message, cause);
        this.arguments = arguments;
        message = null;
        lineNumber = -1;
        columnNumber = -1;
    }

    public FXGException(String message, Object... arguments)
    {
        super(message);
        this.arguments = arguments;
        message = null;
        lineNumber = -1;
        columnNumber = -1;
    }

    public FXGException(int lineNumber, int columnNumber)
    {
        super();
        arguments = null;
        message = null;
        this.lineNumber = lineNumber;
        this.columnNumber = columnNumber;
    }

    public FXGException(int lineNumber, int columnNumber, Throwable cause)
    {
        super(cause);
        this.arguments = null;
        message = null;
        this.lineNumber = lineNumber;
        this.columnNumber = columnNumber;
    }

    public FXGException(int lineNumber, int columnNumber, String message, Throwable cause, Object... arguments)
    {
        super(message, cause);
        this.arguments = arguments;
        message = null;
        this.lineNumber = lineNumber;
        this.columnNumber = columnNumber;
    }

    public FXGException(int lineNumber, int columnNumber, String message, Object... arguments)
    {
        super(message);
        this.arguments = arguments;
        message = null;
        this.lineNumber = lineNumber;
        this.columnNumber = columnNumber;
    }

    /**
     * Specifies locale for subsequent getLocalizedMessage() calls if loc passed
     * in is null, DEFAULT_LOCALE is used.
     * 
     * @param loc
     */
    public static void setLocale(Locale loc)
    {
        if (loc == null)
            loc = DEFAULT_LOCALE;
        if (locale.equals(loc))
            return;

        synchronized (FXGException.class)
        {
            locale = loc;
            setResourceBundle();
        }
    }

    /**
     * Specifies resource base name and locale for initializing resource bundle.
     * If baseName is null, DEFAULT_RESOURCE_BASE_NAME is used. if loc in null,
     * DEFAULT_LOCALE is used.
     * 
     * @param baseName
     * @param loc
     */
    public static void setResourceBundle(String baseName, Locale loc)
    {

        if (baseName == null)
            baseName = DEFAULT_RESOURCE_BASE_NAME;
        if (loc == null)
            loc = DEFAULT_LOCALE;
        if (resourceBaseName.equals(baseName) && locale.equals(loc) && (exceptionResourceBundle != null))
            return;

        synchronized (FXGException.class)
        {
            locale = loc;
            resourceBaseName = baseName;
            setResourceBundle();
        }
    }

    @Override
    public String getMessage()
    {
        // check if message is already cached
        if (message != null)
            return message;

        synchronized (FXGException.class)
        {
            setResourceBundle(null, null);
            message = getLocalizedMessage();
            return message;
        }
    }

    public int getLineNumber()
    {
        return lineNumber;
    }
    
    public int getColumnNumber()
    {
        return columnNumber;
    }
    
    @Override
    public String getLocalizedMessage()
    {

        synchronized (FXGException.class)
        {
            if (exceptionResourceBundle == null)
                setResourceBundle();
            String paramMsg = super.getMessage();
            if (exceptionResourceBundle != null)
            {
                Enumeration<String> keys = exceptionResourceBundle.getKeys();
                while (keys.hasMoreElements())
                {
                    String key = keys.nextElement();
                    if (key.equals(paramMsg))
                    {
                        paramMsg = exceptionResourceBundle.getString(super.getMessage());
                    }
                }
            }
            return substituteArguments(paramMsg, arguments);
        }
    }

    /**
     * @param locale - the Locale to use for the message.
     * @return Returns localized error message for the Locale specified.
     */
    public String getLocalizedMessage(Locale locale)
    {
        synchronized (FXGException.class)
        {
            setLocale(locale);
            return getLocalizedMessage();
        }
    }

    // helper function to set resource bundle
    private static void setResourceBundle()
    {
        try
        {
            exceptionResourceBundle = ResourceBundle.getBundle(resourceBaseName, locale);
        }
        catch (MissingResourceException e)
        {
            exceptionResourceBundle = null;
        }
    }

    // helper function to substitute arguments
    private String substituteArguments(String parameterized, Object[] arguments)
    {
        if ((parameterized == null) || (arguments == null))
            return parameterized;
        return MessageFormat.format(parameterized, arguments).trim();
    }
}