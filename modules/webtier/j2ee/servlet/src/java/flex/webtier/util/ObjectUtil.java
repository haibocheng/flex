////////////////////////////////////////////////////////////////////////////////
//
//  ADOBE SYSTEMS INCORPORATED
//  Copyright 2004-2008 Adobe Systems Incorporated
//  All Rights Reserved.
//
//  NOTICE: Adobe permits you to use, modify, and distribute this file
//  in accordance with the terms of the license agreement accompanying it.
//
////////////////////////////////////////////////////////////////////////////////
package flex.webtier.util;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Hashtable;

public class ObjectUtil {
    // Map of primitive class name and its associated Class object
    private static Hashtable primitiveClasses_;

    static {
        primitiveClasses_ = new Hashtable();
        primitiveClasses_.put(Character.TYPE.getName(), Character.TYPE);
        primitiveClasses_.put(Boolean.TYPE.getName(), Boolean.TYPE);
        primitiveClasses_.put(Byte.TYPE.getName(), Byte.TYPE);
        primitiveClasses_.put(Integer.TYPE.getName(), Integer.TYPE);
        primitiveClasses_.put(Long.TYPE.getName(), Long.TYPE);
        primitiveClasses_.put(Short.TYPE.getName(), Short.TYPE);
        primitiveClasses_.put(Float.TYPE.getName(), Float.TYPE);
        primitiveClasses_.put(Double.TYPE.getName(), Double.TYPE);
    }

    /**
     * find a field that has the given name
     */
    public static Field findField(Object instance, String name) {
        try {
            return instance.getClass().getField(name);
        } catch (NoSuchFieldException e) {
            return null;
        }
    }

    /**
     * find a method that has the given name, and that will accept the given
     * parameters.  If more than one such matching method exists, return the
     * first one we find (arbitrary)
     */
    public static Method findMethod(Object instance, String name, Class[] params) {
        try {
            return instance.getClass().getMethod(name, params);
        } catch (NoSuchMethodException e) {
            return null;
        }
    }

    /**
     * get the value of a field
     */
    public static Object getValue(Object instance, String fieldName)
            throws IllegalAccessException, InvocationTargetException {
        Field f = findField(instance, fieldName);
        if (f != null)
            return f.get(instance);
        //use get method
        fieldName = StringUtils.upperCaseFirstInitial(fieldName);
        Method get = ObjectUtil.findMethod(instance, "get".concat(fieldName), new Class[0]);
        if (get == null) {
            // check for boolean property
            get = ObjectUtil.findMethod(instance, "is".concat(fieldName), new Class[0]);
            if (get == null) {
                // no method or field was found.
                return null;
            }
        }

        return get.invoke(instance, (Object) null);
    }

    public static void setValue(Object instance, Object value, Field field)
            throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        setValue(instance, field.getName(), field.getType(), value);
    }

    /**
     * get the value of a field
     */
    public static void setValue(Object instance, String fieldName, Class fieldClass, Object value)
            throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        Field f = findField(instance, fieldName);
        if (f != null)
            f.set(instance, value);
        else {
            Class[] params = new Class[1];
            params[0] = fieldClass;
            //use set method
            fieldName = StringUtils.upperCaseFirstInitial(fieldName);
            Method set = ObjectUtil.findMethod(instance, "set".concat(fieldName), params);
            if (set == null) {
                set = ObjectUtil.findMethod(instance, "set".concat(fieldName), params);
                if (set == null)
                // no method or field was found.
                    throw new NoSuchMethodException("set".concat(fieldName) + " with args " + params[0].getName());
            }
            Object[] args = { value };
            set.invoke(instance, args);
        }
    }

    /**
     * perform variable substitution in the message string, using {}
     * to delimit variable names.  The variables are resolved as properties
     * on the instance, so subclasses can implement the
     * public fields or get-methods.
     */
    public static String replaceVars(Object instance, String s) {
        if (s == null)
            return null;

        StringBuffer out = new StringBuffer(2 * s.length());

        int i = 0;
        final int len = s.length();

        do {
            int j = s.indexOf('{', i);
            if (j == -1) {
                j = len;
                out.append(s.substring(i, j));
                break;
            }

            if (j > i) {
                out.append(s.substring(i, j));
            }

            i = s.indexOf('}', j);
            if (i == -1) {
                i = len;
                out.append(s.substring(j, i));
                break;
            }

            String name = s.substring(j + 1, i);
            Object value = null;
            try {
                value = ObjectUtil.getValue(instance, name);
            } catch (IllegalAccessException ex) {
                // ignore -- treat like we can't find the variable.
                ex.printStackTrace();
            } catch (InvocationTargetException ex) {
                // ignore -- treat like we can't find the variable.
                ex.printStackTrace();
            }

            if (value != null) {
                out.append(value);
            }
            else {
                out.append('{').append(name).append('}');
            }

            i++;
        } while (i < len);

        return out.toString();
    }

    public static String getClassName(Object instance) {
        String packageName = instance.getClass().getName();
        int index = packageName.lastIndexOf('.');
        if (index >= 0)
            return packageName.substring(index + 1);
        else
            return packageName;
    }
}
