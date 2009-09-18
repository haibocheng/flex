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

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.EmptyStackException;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Properties;
import java.util.Stack;

/**
 * A utility class that contains static convenience methods for working
 * with properties.
 *
 * @author Karl Moss
 * @date 08Nov1999
 */
public class PropertiesUtil {
    /*
     * The begin marker for variable properties
     */
    public static final String BEGIN_VARIABLE_MARKER = "{";

    /*
     * The end marker for variable properties
     */
    public static final String END_VARIABLE_MARKER = "}";

    /**
     * System-level property variables must start with this marker
     */
    public static final String SYSTEM_PROPERTY_KEY = "System.";

    /*
     * <p>Generates a new Properties object containing all of the
     * keys/values from the original, except that any keys
     * containing the given parent key will be trimmed not to
     * include the parent. Consider the following properties:</p>
     * <dir>
     * name=JRun
     * logging.class=allaire.jrun.logging.LoggingService
     * logging.description=JRun Logging Service
     * loggingEntry.name=Logging Entry
     * session.class=allaire.jrun.session.JRunSessionService
     * session.description=JRun Session Manager
     * </dir>
     * <p>This method will return the following properties
     * when called with a parent of "logging":</p>
     * <dir>
     * class=allaire.jrun.logging.LoggingService
     * description=JRun Logging Service
     * loggingEntry.name=Logging Entry
     * session.class=allaire.jrun.session.JRunSessionService
     * session.description=JRun Session Manager
     * </dir>
     * <p>Note that a parent key of "logging" will cause keys
     * that start with "logging." to be trimmed</p>
     * @param props The original property list. This list will
     * not be modified
     * @param parentKey The parent key to trim
     * @return The new property list
     */
    public static OrderedProperties trimParent(Properties props, String parentKey) {
        return trimParent2(props, parentKey);
    }

    /*
     * <p>Generates a new Properties object containing all of the
     * keys/values from the original, except that any keys
     * containing the given parent key will be trimmed not to
     * include the parent. Consider the following properties:</p>
     * <dir>
     * name=JRun
     * logging.class=allaire.jrun.logging.LoggingService
     * logging.description=JRun Logging Service
     * loggingEntry.name=Logging Entry
     * session.class=allaire.jrun.session.JRunSessionService
     * session.description=JRun Session Manager
     * </dir>
     * <p>This method will return the following properties
     * when called with a parent of "logging":</p>
     * <dir>
     * class=allaire.jrun.logging.LoggingService
     * description=JRun Logging Service
     * loggingEntry.name=Logging Entry
     * session.class=allaire.jrun.session.JRunSessionService
     * session.description=JRun Session Manager
     * </dir>
     * <p>Note that a parent key of "logging" will cause keys
     * that start with "logging." to be trimmed</p>
     * @param props The original property list. This list will
     * not be modified
     * @param parentKey The parent key to trim
     * @return The new property list
     */
    public static OrderedProperties trimParent(OrderedProperties props, String parentKey) {
        return trimParent2(props, parentKey);
    }

    private static OrderedProperties trimParent2(Object props, String parentKey) {
        // If no parent key was given, or there is no list, return the original list
        if ((parentKey == null) ||
                (parentKey.length() == 0) ||
                (props == null)) {
            if (props instanceof Properties) {
                OrderedProperties op = new OrderedProperties();
                Properties p = (Properties) props;

                Enumeration e = p.keys();
                while (e.hasMoreElements()) {
                    String key = (String) e.nextElement();
                    op.put(key, p.getProperty(key));
                }
                return op;
            }
            else {
                return (OrderedProperties) props;
            }
        }

        OrderedProperties newProps = new OrderedProperties();

        // Add the '.' to the parent key
        parentKey += ".";

        // Save the length of the parent key
        int keyLen = parentKey.length();

        // Loop through the given properties. If the property key starts
        // with the parent key, strip the parent key portion off.
        Enumeration e = null;
        Properties pSource = null;
        OrderedProperties opSource = null;
        if (props instanceof Properties) {
            pSource = (Properties) props;
            e = pSource.keys();
        }
        else {
            opSource = (OrderedProperties) props;
            e = opSource.keys();
        }
        while (e.hasMoreElements()) {
            String key = (String) e.nextElement();

            // Make sure no top-level properties are carried over
            if (key.indexOf(".") < 0) {
                continue;
            }

            String value;
            if (pSource != null) {
                value = pSource.getProperty(key);
            }
            else {
                value = opSource.getProperty(key);
            }
            if (key.startsWith(parentKey)) {
                newProps.put(key.substring(keyLen), value);
            }
            else {
                newProps.put(key, value);
            }
        }
        return newProps;
    }

    /*
     * <p>Generates a new Properties object containing all of the
     * children for a given parent. Consider the following
     * properties:</p>
     * <dir>
     * logging.type.debug=true
     * logging.type.warning=true
     * logging.type.error=true
     * logging.type.info=false
     * logging.class=allaire.jrun.logging.LoggingService
     * logging.description=JRun Logging Service
     * logging.typeEntry.name=Logging Entry
     * </dir>
     * <p>This method will return the following properties when
     * called with a parent of "logging.type":</p>
     * <dir>
     * debug=true
     * warning=true
     * error=true
     * info=false
     * </dir>
     * <p>Note that a parent key of "logging.type" will cause keys
     * that start with "logging.type." to be trimmed and returned</p>
     * @param props The original property list. This list will
     * not be modified
     * @param parentKey The parent key to trim
     * @return The new property list
     */
    public static OrderedProperties getChildren(Properties props, String parentKey) {
        return getChildren2(props, parentKey);
    }

    /*
     * <p>Generates a new Properties object containing all of the
     * children for a given parent. Consider the following
     * properties:</p>
     * <dir>
     * logging.type.debug=true
     * logging.type.warning=true
     * logging.type.error=true
     * logging.type.info=false
     * logging.class=allaire.jrun.logging.LoggingService
     * logging.description=JRun Logging Service
     * logging.typeEntry.name=Logging Entry
     * </dir>
     * <p>This method will return the following properties when
     * called with a parent of "logging.type":</p>
     * <dir>
     * debug=true
     * warning=true
     * error=true
     * info=false
     * </dir>
     * <p>Note that a parent key of "logging.type" will cause keys
     * that start with "logging.type." to be trimmed and returned</p>
     * @param props The original ordered property list. This list will
     * not be modified
     * @param parentKey The parent key to trim
     * @return The new property list
     */
    public static OrderedProperties getChildren(OrderedProperties props, String parentKey) {
        return getChildren2(props, parentKey);
    }

    private static OrderedProperties getChildren2(Object props, String parentKey) {
        // If no parent key was given, or there is no list, return the original list
        if ((parentKey == null) ||
                (parentKey.length() == 0) ||
                (props == null)) {
            if (props instanceof Properties) {
                OrderedProperties op = new OrderedProperties();
                Properties p = (Properties) props;

                Enumeration e = p.keys();
                while (e.hasMoreElements()) {
                    String key = (String) e.nextElement();
                    op.put(key, p.getProperty(key));
                }
                return op;
            }
            else {
                return (OrderedProperties) props;
            }
        }

        OrderedProperties newProps = new OrderedProperties();

        // Add the '.' to the parent key
        parentKey += ".";

        // Save the length of the parent key
        int keyLen = parentKey.length();

        // Loop through the given properties. If the property key starts
        // with the parent key, strip the parent key portion off.
        Enumeration e = null;
        Properties pSource = null;
        OrderedProperties opSource = null;
        if (props instanceof Properties) {
            pSource = (Properties) props;
            e = pSource.keys();
        }
        else {
            opSource = (OrderedProperties) props;
            e = opSource.keys();
        }

        while (e.hasMoreElements()) {
            String key = (String) e.nextElement();
            if (key.startsWith(parentKey)) {
                String value;
                if (pSource != null) {
                    value = pSource.getProperty(key);
                }
                else {
                    value = opSource.getProperty(key);
                }

                newProps.put(key.substring(keyLen), value);
            }
        }
        return newProps;
    }

    /*
     * Returns true if the given value is "true"
     * @param value The property value
     * @return true if the property is "true"
     */
    public static boolean isTrue(String value) {
        boolean rc = false;
        if ((value != null) &&
                value.equals("true")) {
            rc = true;
        }
        return rc;
    }

    /**
     * Expands any dynamic variables (such as {date}) for the
     * given key
     * @param value The property value with dynamic variables
     * @param date The timestamp to use for date variables, or null not to use dates
     * @param variableProps Properties to use for variables
     * @return The new value
     */
    public static String expandDynamicVariables(String value,
                                                Date date, Properties variableProps) {
        if (value == null) {
            return null;
        }

        String lastValue = "";

        // Loop until the value doesn't change any more. This is
        // to accomodate variables that are replaced with a
        // nested variable
        while (!value.equals(lastValue)) {
            lastValue = value;

            // Find the first variable marker
            int beginPos = value.indexOf(BEGIN_VARIABLE_MARKER);

            // Loop while there are more variable markers
            while (beginPos >= 0) {

                // Find the associated end marker
                int endPos = value.indexOf(END_VARIABLE_MARKER, beginPos);

                // If there is no end marker, exit
                if (endPos < 0) {
                    break;
                }

                // Get the variable name
                String varName = value.substring(beginPos + 1, endPos);

                String varValue = null;

                // Check the properties first in case the caller wants to override
                // any of the other dynamic variables (such as thread.name)
                if (variableProps != null) {
                    varValue = variableProps.getProperty(varName);
                }

                // If the value was not found, get the value of the dynamic
                // variable from the date/thread
                if (varValue == null) {
                    varValue = getVariableValue(varName, date);
                }

                if (varValue == null) {
                    // Look for system-level properties
                    if (varName.startsWith(SYSTEM_PROPERTY_KEY)) {
                        Properties sysProps = System.getProperties();
                        if (sysProps != null) {
                            varValue = sysProps.getProperty(varName.substring(SYSTEM_PROPERTY_KEY.length()));
                        }
                    }
                }

                // Perform a quick check to make sure we're not going to get into
                // a recursive loop. If the value for the property contains the
                // property name that we just searched for, don't use it.
                if (varValue != null) {
                    if (varValue.indexOf(BEGIN_VARIABLE_MARKER + varName + END_VARIABLE_MARKER) >= 0) {
                        varValue = null;
                    }
                }

                // Only replace the variable if a value was found
                if (varValue != null) {
                    StringBuffer sb = new StringBuffer();
                    if (beginPos > 0) {
                        sb.append(value.substring(0, beginPos));
                    }
                    sb.append(varValue);
                    if (endPos < value.length()) {
                        sb.append(value.substring(endPos + 1));
                    }
                    value = sb.toString();
                }
                beginPos = value.indexOf(BEGIN_VARIABLE_MARKER, beginPos + 1);
            }
        }
        return value;
    }

    // A Hasthable of Stacks keyed by the SimpleDateFormat specification. We'll use
    // this Hashtable as a cache so we don't have to keep creating SDF's
    protected static Hashtable sdfCache = new Hashtable();

    /**
     * <p>Gets a SimpleDateFormat for the given format. SimpleDateFormat objects
     * are cached for performance and locking reasons
     * @param format The format
     * @return The SimpleDateFormat object
     */
    protected static SimpleDateFormat getPooledSDF(String format) {
        SimpleDateFormat sdf = null;

        Stack stack = (Stack) sdfCache.get(format);
        if (stack != null) {
            try {
                sdf = (SimpleDateFormat) stack.pop();
            } catch (EmptyStackException ese) {
                // Ignore - create below
            }
        }

        // Either this is the first time we've requested this format or all
        // of the objects are being used; create a new one
        if (sdf == null) {
            sdf = new SimpleDateFormat(format);
        }
        return sdf;
    }

    /**
     * <p>Returns a SimpleDateFormat object back to the cache.
     * @param sdf The SimpleDateFormat object
     */
    protected static void returnPooledSDF(SimpleDateFormat sdf) {
        String format = sdf.toPattern();
        Stack stack = (Stack) sdfCache.get(format);
        if (stack == null) {
            // Create a new stack
            stack = new Stack();
            sdfCache.put(format, stack);
        }

        // Put the SDF back
        stack.push(sdf);
    }

    /**
     * <p>Gets the variable value of the given variable name.
     * Valid names are:</p>
     * <dir>
     * date - Today's date in the format YYYYMMDD
     * date &lt;format&gt; - Today's date using the specified format,
     * which is the same as java.text.SimpleDateFormat
     * day - 2 digit day (01-n)
     * month - 2 digit month (01-12)
     * hour - 2 digit hour (00 - 23)
     * year - 4 digit year
     * julian - The julian date
     * thread-name - The current thread name
     * thread-hashcode - - The current thread hashcode
     * thread-id - A unique id for the thread (the hashcode in hex)
     * </dir>
     * @param var The variable name
     * @return The new value, or null for invalid variable
     * @see java.text.SimpleDateFormat
     */
    public static String getVariableValue(String var, Date date) {
        if ((var == null) || (date == null)) {
            return null;
        }

        String s = null;
        if (var.equals("date")) {
            SimpleDateFormat format = getPooledSDF("yyyyMMdd");
            s = format.format(date);
            returnPooledSDF(format);
        }
        else if (var.startsWith("date ")) {
            SimpleDateFormat format = getPooledSDF(var.substring(5));
            s = format.format(date);
            returnPooledSDF(format);
        }
        else if (var.equals("day")) {
            SimpleDateFormat format = getPooledSDF("dd");
            s = format.format(date);
            returnPooledSDF(format);
        }
        else if (var.equals("month")) {
            SimpleDateFormat format = getPooledSDF("MM");
            s = format.format(date);
            returnPooledSDF(format);
        }
        else if (var.equals("year")) {
            SimpleDateFormat format = getPooledSDF("yyyy");
            s = format.format(date);
            returnPooledSDF(format);
        }
        else if (var.equals("hour")) {
            SimpleDateFormat format = getPooledSDF("HH");
            s = format.format(date);
            returnPooledSDF(format);
        }
        else if (var.equals("julian")) {
            s = "" + (date.getTime() / (1000 * 60 * 60 * 24));
        }
        else if (var.equals("thread.name")) {
            s = Thread.currentThread().getName();
        }
        else if (var.equals("thread.hashcode")) {
            s = "" + Thread.currentThread().hashCode();
        }
        else if (var.equals("thread.id")) {
            s = toHex(Thread.currentThread().hashCode());
        }
        return s;
    }


    // Table of hex digits
    private static final char[] hexDigit =
            {'0','1','2','3','4','5','6','7','8','9','A','B','C','D','E','F'};

    /**
     * Converts the given number into a hex string
     * @param n The number to convert
     * @param bytes The number of bytes to convert (1-4)
     * @return The number represented in hex
     */
    public static String toHex(int n, int bytes) {
        // Sanity check the number of bytes
        if ((bytes < 1) || (bytes > 4)) {
            bytes = 4;
        }

        StringBuffer sb = new StringBuffer();

        for (int i = (bytes - 1); i >= 0; i--) {
            sb.append(hexDigit[(n >> ((i * 8) + 4)) & 0x0F]);
            sb.append(hexDigit[(n >> (i * 8)) & 0x0F]);
        }

        return sb.toString();
    }

    /**
     * Converts the given number into a hex string. The number
     * is assumed to be a 4-byte int
     * @param n The number to convert
     * @return The number represented in hex
     */
    public static String toHex(int n) {
        return toHex(n, 4);
    }


}
