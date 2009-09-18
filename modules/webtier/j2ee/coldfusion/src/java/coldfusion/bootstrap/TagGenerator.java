/*************************************************************************
 * 
 * ADOBE CONFIDENTIAL
 * __________________
 * 
 *  [2002] - [2007] Adobe Systems Incorporated 
 *  All Rights Reserved.
 * 
 * NOTICE:  All information contained herein is, and remains
 * the property of Adobe Systems Incorporated and its suppliers,
 * if any.  The intellectual and technical concepts contained
 * herein are proprietary to Adobe Systems Incorporated
 * and its suppliers and may be covered by U.S. and Foreign Patents,
 * patents in process, and are protected by trade secret or copyright law.
 * Dissemination of this information or reproduction of this material
 * is strictly forbidden unless prior written permission is obtained
 * from Adobe Systems Incorporated.
 */
package coldfusion.bootstrap;

import javax.servlet.jsp.tagext.BodyTag;
import javax.servlet.jsp.tagext.Tag;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.util.HashSet;

/**
 * This utility generates a local JSP Tag implementation that delegates all
 * calls to a concrete JSP Tag implementation loaded by the ColdFusion bootstrap
 * classloader.
 * 
 * @author Edwin Smith
 */
public class TagGenerator
{
    public static void main(String[] args) throws ClassNotFoundException, IOException
    {
        if (args.length < 2)
        {
            System.err.println("usage: TagGenerator destdir class1 class2 ...");
            System.exit(1);
        }
        String destdir = args[0];
        for (int i = 1; i < args.length; i++)
        {
            String className = args[i];
            Class api = Class.forName(className);
            String filename = className.replace('.', '/') + "Impl.java";
            String pkgdir = filename.substring(0, filename.lastIndexOf('/'));
            new File(destdir, pkgdir).mkdirs();
            System.out.println("generating " + filename);
            PrintWriter out = new PrintWriter(new FileWriter(new File(destdir, filename)));
            try
            {
                generateTag(api, out);
                out.flush();
            }
            finally
            {
                out.close();
            }
        }
    }

    private static class MethodKey
    {
        Method method;
        String name;
        Class[] paramTypes;

        public MethodKey(Method method)
        {
            this.method = method;
            this.name = method.getName();
            this.paramTypes = method.getParameterTypes();
        }

        public boolean equals(Object obj)
        {
            if (!(obj instanceof MethodKey))
                return false;
            MethodKey other = (MethodKey) obj;
            if (!name.equals(other.name))
                return false;
            if (paramTypes.length != other.paramTypes.length)
                return false;
            for (int i = 0; i < paramTypes.length; i++)
                if (paramTypes[i] != other.paramTypes[i])
                    return false;
            return true;
        }

        public int hashCode()
        {
            int c = name.hashCode();
            for (int i = 0; i < paramTypes.length; i++)
                c ^= paramTypes[i].hashCode();
            return c;
        }

        public String toString()
        {
            return method.toString();
        }
    }

    private static void generateTag(Class api, PrintWriter out)
    {
        String implName = api.getName() + "Impl";
        String packageName = implName.substring(0, implName.lastIndexOf('.'));
        String className = implName.substring(implName.lastIndexOf('.') + 1);
        HashSet done = new HashSet();

        Class baseImpl;
        Class baseApi;
        if (BodyTag.class.isAssignableFrom(api))
        {
            baseImpl = BootstrapBodyTag.class;
            baseApi = BodyTag.class;
        }
        else
        {
            baseImpl = BootstrapTag.class;
            baseApi = Tag.class;
        }

        out.println("package " + packageName + ";");
        out.println("public class " + className + " extends " + baseImpl.getName());
        out.println("   implements " + api.getName());
        out.println("{");

        // constructor
        String tagName = createTagName(api);
        out.println(api.getName() + " " + tagName + " = (" + api.getName() + ") tag;");

        // delegate methods
        Method[] methods = api.getMethods();
        for (int i = 0; i < methods.length; i++)
        {
            MethodKey key = new MethodKey(methods[i]);
            try
            {
                if (!done.contains(key))
                    baseApi.getMethod(key.name, key.paramTypes);
            }
            catch (NoSuchMethodException e)
            {
                // only generate unique methods that are not in baseApi
                generateMethod(out, methods[i], tagName);
                done.add(key);
            }
        }
        out.println("}");
    }

    private static void generateMethod(PrintWriter out, Method method, String tagName)
    {
        out.print("public ");
        Class returnType = method.getReturnType();
        out.print(returnType.getName());
        out.print(" ");
        out.print(method.getName());
        out.print("(");

        Class[] params = method.getParameterTypes();
        for (int i = 0; i < params.length; i++)
        {
            if (i > 0)
                out.print(", ");
            out.print(params[i].getName());
            out.print(" p");
            out.print(i);
        }

        out.println(")");

        Class[] exceptionTypes = method.getExceptionTypes();
        if (exceptionTypes.length > 0)
        {
            out.print(" throws ");
            for (int i = 0; i < exceptionTypes.length; i++)
            {
                if (i > 0)
                    out.print(", ");
                out.print(exceptionTypes[i].getName());
            }
        }

        out.println("{");

        out.println("  Thread thread = Thread.currentThread();");
        out.println("  ClassLoader oldLoader = thread.getContextClassLoader();");
        out.println("  thread.setContextClassLoader(bootstrap);");
        out.println("  try {");

        out.print("    ");

        if (returnType != Void.TYPE)
            out.print("return ");
        out.print(tagName);
        out.print(".");
        out.print(method.getName());
        out.print("(");
        for (int i = 0; i < params.length; i++)
        {
            if (i > 0)
                out.print(", ");
            out.print("p");
            out.print(i);
        }
        out.println(");");

        out.println("  } finally {");
        out.println("    thread.setContextClassLoader(oldLoader);");
        out.println("  }");
        out.println("}");
    }

    private static String createTagName(Class api)
    {
        String name = api.getName();
        StringBuffer b = new StringBuffer(name);
        b.delete(0, name.lastIndexOf('.') + 1);
        b.setCharAt(0, Character.toLowerCase(b.charAt(0)));
        return b.toString();
    }
}
