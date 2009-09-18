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

import java.io.BufferedReader;
import java.io.Serializable;
import java.io.StringReader;
import java.util.Date;
import java.util.Properties;

import flash.util.ExceptionUtil;
import flex.webtier.util.PropertiesUtil;

/**
 * A LogEvent represents a single log message.
 *
 * @author Karl Moss
 * @since 09Apr2001
 */
public class LogEvent implements Serializable
{
    static final long serialVersionUID = 5826658332327361868L;

    // Log event types. Note that several event types can be applied
	// by and-ing together
	public static final int LOG_ERROR   = 0x01;
	public static final int LOG_WARNING = 0x02;
	public static final int LOG_INFO    = 0x04;
	public static final int LOG_DEBUG   = 0x08;
	public static final int LOG_METRICS = 0x10;
    public static final int LOG_USER    = 0x20;

	// The default log format
	public static final String DEFAULT_FORMAT = "{server.date} {log.level} {log.message}{log.exception}";

	protected static String lineSeparator;
	
	private int type;
	private String message;
	private Throwable throwable;
	private Properties props;
	private String format;
	private Date logTime;
	private Throwable logStack;
	
	static
	{
	    // Get the line separator
	    if (lineSeparator == null) {
	        lineSeparator = System.getProperty("line.separator");
	        if (lineSeparator == null) {
	            lineSeparator = "\n";
	        }
	    }
	}
	
	/**
	 * Construct a new LogEvent
	 * @param type The event type
	 * @param msg The message
	 */
	public LogEvent(int type, String msg)
	{
		this(type, msg, null);
	}
	
	/**
	 * Construct a new LogEvent
	 * @param type The event type
	 * @param msg The message
	 * @param t The exception, or null if none
	 */
	public LogEvent(int type, String msg, Throwable t)
	{
		this(type, msg, t, null);
	}
	
	/**
	 * Construct a new LogEvent
	 * @param type The event type
	 * @param msg The message
	 * @param t The exception, or null if none
	 * @param props Additional properties for the log event (such as metrics)
	 */
	public LogEvent(int type, String msg, Throwable t, Properties props)
	{
		this.type = type;
		this.message = msg;
		this.throwable = t;
		this.props = props;
		this.logTime = new Date();
		this.logStack = new Exception();
	}

	/**
	 * Gets the log event type. This method can be used to get custom
	 * event types, but the individual is{type} method should be used
	 * for standard event types
	 * @return The log type
	 */
	public int getType()
	{
		return type;
	}
	
	/**
	 * Determines if this message is an error message
	 * @return true if an error message
	 */
	public boolean isError()
	{
		return (type & LOG_ERROR) != 0;
	}
	
	/**
	 * Determines if this message is an warning message
	 * @return true if a warning message
	 */
	public boolean isWarning()
	{
		return (type & LOG_WARNING) != 0;
	}
	
	/**
	 * Determines if this message is an informational message
	 * @return true if an informational message
	 */
	public boolean isInfo()
	{
		return (type & LOG_INFO) != 0;
	}
	
	/**
	 * Determines if this message is a debug message
	 * @return true if a debug message
	 */
	public boolean isDebug()
	{
		return (type & LOG_DEBUG) != 0;
	}
	
	/**
	 * Determines if this message is a metrics message
	 * @return true if a metrics message
	 */
	public boolean isMetrics()
	{
		return (type & LOG_METRICS) != 0;
	}

    /**
	 * Determines if this message is a user message
	 * @return true if a user message
	 */
	public boolean isUser()
	{
		return (type & LOG_USER) != 0;
	}

	/**
	 * Gets the log message
	 * @return The message to log
	 */
	public String getMessage()
	{
		return message;
	}
	
	/**
	 * Gets the formatted message.
	 * @param f The format to apply, or null to use the default
	 */
	public String getFormattedMessage(String f)
	{
		if (f == null) 
		{
			f = this.format;
			
			if (f == null) 
			{
				// If there is still no format defined, use our default
				f = FileLogEventHandler.expandServerDate(DEFAULT_FORMAT);
			}
		}
		
		// Get all of the variables for the log event
		Properties props = getVariables(f);
		
		String msg = PropertiesUtil.expandDynamicVariables(f, getLogTime(), props);
		return msg;
	}
	
	protected String getExceptionString(Throwable t)
	{
		return ExceptionUtil.exceptionToString(t);
	}
	
	/**
	 * Gets the variables for the current log event. These include:
	 * <dir>
	 * log.level - debug, info, warning, error
	 * log.message - the message
	 * log.exception - the exception message (if present)
	 * log.logger.className - the class name (excluding package) where the log event originated
	 * log.logger.fullClassName - the full class name (including package) where the log
	 * event originated
	 * log.logger.methodName - the method name where the log event originated
	 * log.logger.sourceFile - the name of the source file where the log event originated
	 * log.logger.line - the line number where the log event originated
	 * </dir>
	 * @param format The logging format, or null to use the default
	 */
	public Properties getVariables(String format)
	{
		if (format == null) 
		{
			format = FileLogEventHandler.expandServerDate(getFormat());
		}
		
		Properties props = new Properties();

		StringBuffer sb = new StringBuffer();
		setType(sb, isDebug(), "DEBUG");
		setType(sb, isInfo(), "INFO");
		setType(sb, isWarning(), "WARNING");
		setType(sb, isError(), "ERROR");
		setType(sb, isMetrics(), "METRICS");
        setType(sb, isUser(), "USER");
		props.put("log.level", sb.toString());

        // Initialized the log.logger attributes
        props.put("log.logger.className", "");
        props.put("log.logger.fullClassName", "");
        props.put("log.logger.methodName", "");
        props.put("log.logger.sourceFile", "");
        props.put("log.logger.line", "");

		String s = getMessage();
		if (s == null) 
		{
			s = "";
		}
		props.put("log.message", s);
		
		s = "";
		if (getThrowable() != null) 
		{
			s = lineSeparator + getExceptionString(getThrowable());
		}
		props.put("log.exception", s);

		// Rip apart the caller stuff, but only if present in the format
		if (format.indexOf("log.logger.") >= 0) 
		{
			s = getExceptionString(logStack);
			
			// Now read it a line at a time
			String line = null;
			try 
			{
				BufferedReader in = new BufferedReader(new StringReader(s));
				boolean nextOne = false;
				while (true) 
				{
					line = in.readLine();
					if (line == null) 
					{
						break;
					}
					
					// Look for the entry point into the logger. This could
					// appear several times
					if (line.indexOf("jrunx.logger.LoggerService.log") >= 0) 
					{
						nextOne = true;
					}
					else if (nextOne) 
					{
						break;
					}
				}
			}
			catch (Exception ex) 
			{
				// Just reset the line on error
				line = null;
			}

			// This line contains the trace information from the logger.			
			if (line != null) 
			{
				String sourceFile = null;
				String className = null;
				String fullClassName = null;
				String methodName = null;
				String lineNo = null;
				
				int lastSpace = line.lastIndexOf(" ");
				int openParen = line.indexOf("(");
				int closeParen = line.indexOf(")");
				int colon = line.indexOf(":");
				if (lastSpace < openParen) 
				{
					String name = line.substring(lastSpace + 1, openParen);
					
					// Yank the method name
					int n = name.lastIndexOf(".");
					if (n > 0) 
					{
						methodName = name.substring(n + 1);
						fullClassName = name.substring(0, n);
						
						// Now strip out the class name
						n = fullClassName.lastIndexOf(".");
						if (n > 0) 
						{
							className = fullClassName.substring(n + 1);
						}
						else 
						{
							className = fullClassName;
						}
					}
					
				}
				if ((openParen > 0) && (openParen < closeParen)) 
				{
					if (colon > openParen) 
					{
						sourceFile = line.substring(openParen + 1, colon);
						lineNo = line.substring(colon + 1, closeParen);
					}
					else 
					{
						sourceFile = line.substring(openParen + 1, closeParen);
					}
				}
				
				// Stuff into props
				if (className != null) 
				{
					props.put("log.logger.className", className);
				}
				if (fullClassName != null) 
				{
					props.put("log.logger.fullClassName", fullClassName);
				}
				if (methodName != null) 
				{
					props.put("log.logger.methodName", methodName);
				}
				if (sourceFile != null) 
				{
					props.put("log.logger.sourceFile", sourceFile);
				}
				if (lineNo != null) 
				{
					props.put("log.logger.line", lineNo);
				}
			}
		}
		
		return props;
	}
	
	private void setType(StringBuffer sb, boolean set, String desc)
	{
		if (set) 
		{
			if (sb.length() > 0) 
			{
				sb.append(" ");
			}
			sb.append(desc);
		}
	}
	
	/**
	 * Gets the exception associated with this message
	 * @return The exception to log, or null for none
	 */
	public Throwable getThrowable()
	{
		return throwable;
	}
	
	/**
	 * Gets the additional properties associated with this message, such
	 * as metrics information.
	 * @return The properties, or null for none
	 */
	public Properties getProperties()
	{
		return props;
	}
	
	/**
	 * Sets the current format for the event
	 * @param format The format
	 */
	public void setFormat(String format)
	{
		this.format = format;
	}
	
	/**
	 * Gets the current format for the event
	 * @return The format
	 */
	public String getFormat()
	{
	    if (format == null)
		format = DEFAULT_FORMAT;
	    return format;
	}
	
	/**
	 * Gets the time that the message was logged
	 */
	public Date getLogTime()
	{
		return logTime;
	}

	public String toString()
	{
		return getMessage();
	}
}
