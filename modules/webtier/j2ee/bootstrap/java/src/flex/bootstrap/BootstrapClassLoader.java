////////////////////////////////////////////////////////////////////////////////
//
//  ADOBE SYSTEMS INCORPORATED
//  Copyright 2003-2008 Adobe Systems Incorporated
//  All Rights Reserved.
//
//  NOTICE: Adobe permits you to use, modify, and distribute this file
//  in accordance with the terms of the license agreement accompanying it.
//
////////////////////////////////////////////////////////////////////////////////
package flex.bootstrap;

import flex.webtier.util.J2EEUtil;

import javax.servlet.ServletContext;
import java.net.URLClassLoader;
import java.net.URL;
import java.net.MalformedURLException;
import java.io.IOException;
import java.io.InputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.HashMap;

/**
 * The bootstrap classloader is used to load classes in the order needed by Royale.  It makes sure that Royale
 * jars are used as needed, a problem which WAS4 and other appservers don't solve by themselves.
 * The code is mainly taken from ColdFusion.
 *
 * @author Brian Deitte
 */
public class BootstrapClassLoader extends URLClassLoader
{
    private String[] deferToSuper;
    private String[] exceptionList;
    private Map classCache = new HashMap();
    private static BootstrapClassLoader instance;
    private static String BOOTSTRAP_PROPERTIES_FILENAME = "flex-bootstrap.properties";
    // keep track of the number of servlets using the bootstrap class loader
    // this will let us know when the last time stop is being called so that
    // shared resources may be discarded
    private static int references;
    
    public static synchronized ClassLoader getClassLoader()
    {
        return instance;
    }

    public static synchronized ClassLoader getClassLoader(ServletContext context)
    {
        if (instance == null)
        {
            J2EEUtil.setServerInfo(context.getServerInfo());

            Properties props = getBootstrapProperties(context);

            URL[] classpathURLs;
            String classpath = context.getInitParameter("flex.class.path");
            if (classpath == null)
            {
                throw new RuntimeException("init-parameter 'flex.class.path' was not found");
            }
            StringTokenizer st = new StringTokenizer(classpath, ",");
            List cpList = new ArrayList();
            while (st.hasMoreTokens())
            {
                String token = st.nextToken();
                //check for the resource in the webapp first
                String path = J2EEUtil.getRealPath(token, context);

                File file = null;
                if ((path == null) && (J2EEUtil.getRealPath("/", context) == null))
                {
                    // cmurphy - technically, the bootstrap classloader could rely on absolute paths
                    // in this case but the remainder of the application (accessing mxml, as files)
                    // can't work without support for getRealPath so write the error here
                    System.err.println("BootstrapClassLoader: the application server doesn't return a valid path for ServletContext.getRealPath(); try expanding the war file before deploying.");
                    continue;
                }
                else if (path == null)
                {
                    //maybe it's an absolute path, i.e. c:\\myapplibs\\stuff.jar
                    file = new File(token);
                }
                else
                {
                    file = new File(path);
                    if (!file.exists())
                    {
                        //maybe it's an absolute path, i.e. c:\\myapplibs\\stuff.jar
                        file = new File(token);
                    }
                }

                if (file.exists())
                {
                    try
                    {
                        // make sure we have the canonical one
                        file = file.getCanonicalFile();
                    }
                    catch (IOException e) // NOWARN - ignore empty catch statement
                    {
                    }

                    // add jars in a dir
                    if (file.isDirectory())
                    {
                        File[] libFiles = file.listFiles();
                        for (int i = 0; i < libFiles.length; i++)
                        {
                            String fileName = libFiles[i].toString();
                            if (fileName.endsWith(".jar") || fileName.endsWith(".zip"))
                            {
                                // add the jar or dir
                                try
                                {
                                    cpList.add(libFiles[i].toURL());
                                }
                                catch (MalformedURLException murl)
                                {
                                    murl.printStackTrace();
                                }
                            }
                        }
                    }

                    // add the jar or dir
                    try
                    {
                        cpList.add(file.toURL());
                    }
                    catch (MalformedURLException murl)
                    {
                        murl.printStackTrace();
                    }
                }
                else
                {
                    System.err.println("BootstrapClassLoader: ignoring " + token);
                }
            }

            classpathURLs = new URL[cpList.size()];
            cpList.toArray(classpathURLs);

            String[] deferToSuper = null;
            String[] exceptionList = null;
            if (props.getProperty("loadByAppServer") != null)
                deferToSuper = split(props.getProperty("loadByAppServer"), ",");
            if (props.getProperty("exceptions") != null)
                exceptionList = split(props.getProperty("exceptions"), ",");

            instance = new BootstrapClassLoader(classpathURLs, BootstrapClassLoader.class.getClassLoader(),
                    deferToSuper, exceptionList);
        }
        return instance;
    }

    /**
     * Split a string using the separator string.
     */
    private static String[] split(String line, String sep)
    {
        if (sep == null)
        {
            sep = ",";
        }
        StringTokenizer st = new StringTokenizer(line, sep, false);

        int number = st.countTokens();
        ArrayList L = new ArrayList(number);

        for (int i = 0; i < number; i++)
        {
            L.add(i, st.nextToken().trim());
        }
        return (String[]) L.toArray(new String[]{});
    }

    private static Properties getBootstrapProperties(ServletContext context)
    {
        // try to find the override properties file
        String propFileName = BOOTSTRAP_PROPERTIES_FILENAME;
        Properties props = null;
        InputStream propsFileStream = null;
        try
        {
            //this is ok, since property files are in bootstrap.jar.
            propsFileStream = BootstrapClassLoader.class.getClassLoader().getResourceAsStream(propFileName);
            if (propsFileStream != null)
            {
                props = new Properties();
                props.load(propsFileStream);
            }
        }
        catch (Exception e) // NOWARN - ignore empty catch statement
        {
        }
        finally
        {
            try
            {
                if (propsFileStream != null) propsFileStream.close();
            }
            catch(IOException ioe) // NOWARN - ignore empty catch statement
            {
            }
        }
        
        if (props == null)
        {
            props = new Properties();
            //figure out what type of app server we're running on.
            String appServerName = getAppServerName(context.getServerInfo());

            propFileName = appServerName + ".properties";

            try
            {
                //this is ok, since property files are in bootstrap.jar.
                propsFileStream = BootstrapClassLoader.class.getClassLoader().getResourceAsStream(propFileName);
                props.load(propsFileStream);
            }
            catch (Exception e)
            {
                e.printStackTrace();
                System.err.println("Properties file not found: " + propFileName);
            }
            finally
            {
                try
                {
                    if (propsFileStream != null) propsFileStream.close();
                }
                catch(IOException ioe) // NOWARN - ignore empty catch statement
                {
                }
            }
        }
            
        return props;
    }

    private static String getAppServerName(String serverinfo)
    {
        String appServerName;
        if (serverinfo.indexOf("JRun") != -1)
        {
            appServerName="jrun";
        }
        else if (serverinfo.indexOf("WebSphere") != -1)
        {
            appServerName="websphere";
        }
        else if (serverinfo.indexOf("WebLogic") != -1)
        {
	        appServerName="weblogic";
	    }
        else if (serverinfo.indexOf("Interstage") != -1)
        {
            appServerName="interstage";
        }
        else if (serverinfo.indexOf("Hitachi") != -1)
        {
            appServerName="hitachi";
        }            
        else if ((serverinfo.indexOf("Tomcat/5") != -1) && (serverinfo.indexOf("Tomcat/5.0") == -1))
        {            
            // all Tomcat servers over 5.0
            appServerName="tomcat";
        }
        else if ((serverinfo.indexOf("Apache Tomcat/") != -1))
        {
            // Apache Tomcat 6+
            appServerName = "tomcat";
        }
        else
        {
            appServerName="default";
        }
        return appServerName;
    }

    BootstrapClassLoader(URL[] classpath, ClassLoader parent, String[] deferToSuper, String[] exceptionList)
    {
        super(classpath, parent);
        this.deferToSuper = deferToSuper;
        this.exceptionList = exceptionList;
    }

    /**
     * isStrInList Is the string 'name' in the list of strings.
     */
    private boolean isStrInList(String name, String[] list)
    {
        boolean retValue = false;
        if (list != null)
        {
            for (int i = 0; i < list.length; i++)
            {
                if (name.startsWith(list[i]))
                {
                    retValue = true;
                    break;
                }
            }
        }
        return retValue;
    }

    /**
     * delegateToSuper if the class name is in the list of includes but not in the
     * list of excludes. 
     */
    private boolean delegateToSuper(String name)
    {
        return (isStrInList(name, deferToSuper) && !isStrInList(name, exceptionList));
    }

    protected Class loadClass(String name, boolean resolve)
            throws ClassNotFoundException
    {
        // First, check if the class has already been loaded by this class loader
        Class c = findLoadedClass(name);
        if (c != null)
        {
            return c;
        }

        c = (Class) classCache.get(name);
        if (c == null)
        {
            synchronized (this)
            {
                c = (Class) classCache.get(name);
                if (c == null)
                {
                    // First Delegate class loading to the Application class loader and
                    // if not found try this class loader.
                    if (delegateToSuper(name))
                    {
                        try
                        {
                            c = super.loadClass(name, resolve);
                            //System.out.println("loaded super class: " + name);
                        }
                        catch (ClassNotFoundException cnfe)
                        {
                            //maybe the parent doesn't have this,
                            // in which case keep going and try to find it
                            // in our ClassLoader.
                            c = findClass(name);
                        }
                    }
                    // First try to load the class using this class loader and if not found
                    // delegate to the parent application class loader.
                    else
                    {
                        try
                        {
                            //find the class in our classpath first, if it exists, use it.
                            c = findClass(name);
                            //System.out.println("loaded class: " + name);
                        }
                        catch (ClassNotFoundException e)
                        {
                            //else use the standard mechanism for loading classes.
                            c = super.loadClass(name, false);
                        }
                    }

                    if (c == null)
                    {
                        throw new ClassNotFoundException();
                    }
                    /*else
                    {
                        System.out.println("loaded class: " + name);
                    }
                    */
                }
                classCache.put(name, c);
            }
        }

        if (resolve)
        {
            resolveClass(c);
        }
        return c;
    }

    public URL getResource(String name)
    {
        URL url = null;
        if (url == null)
        {
            url = findResource(name);
        }
        if (url == null)
        {
            url = getParent().getResource(name);
        }
        return url;
    }
    
    /**
    * This method should be called from the <code>destroy</code> method 
    * of every servlet using this class loader.  If that happens to be the
    * last servlet using the class loader, according to the reference count,
    * then the resources of this class loader are released.  Otherwise, the
    * reference count is decremented.
    */
	public void clear()
	{
		synchronized (BootstrapClassLoader.class)
		{
		     references--;
		     if (references == 0)
		     {
		         deferToSuper = null;
		         exceptionList = null;
		         classCache = null;
		         instance = null;
		     }
		}
	}
	
    /**
    * Increment the reference count of servlets using this class loader.
    * Should be called from the <code>init</code> method of any servlet
    * using this class loader
    */
    public static synchronized void incrementReferences() 
    {
         references++;
    }	
  /*
    public static void main(String[] args) throws Exception
    {
        File f = new File("c:/CfusionMX/lib/macromedia_drivers.jar");
        URL url = f.toURL();

        URL urls[] = new URL[]{url};
        ClassLoader cl = new BootstrapClassLoader(urls, BootstrapClassLoader.class.getClassLoader(), new Properties());

        long startTime = System.currentTimeMillis();
        for (int i=0; i<10000; i++) {
            cl.loadClass("java.lang.Object");
        }
        long endTime = System.currentTimeMillis();
        System.out.println("Total time: " + (endTime-startTime) + "ms");
    }
    */
}


