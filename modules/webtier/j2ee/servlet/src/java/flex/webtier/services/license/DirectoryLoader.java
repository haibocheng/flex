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
package flex.webtier.services.license;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

public class DirectoryLoader implements LicenseLoader
{
    protected String flexdir;

    private static boolean isWindows = System.getProperty("os.name").toUpperCase().startsWith("WINDOWS");

    // location of the flex assets dir within the the web application
    // usually /WEB-INF/flex or <installdir>/lib
    public DirectoryLoader(String flexdir)
    {
        this.flexdir = flexdir;
    }

    private String getHiddenFileName()
    {
        String hiddenFile;
        if (isWindows)
        {
            hiddenFile = "c:\\WINDOWS\\system32\\Macromed\\rkya7143.bin";
        }
        else
        {
            hiddenFile = "/etc/.rkya7143.bin";
        }

        return hiddenFile;
    }

    protected String getPropertiesFileName() throws IOException
    {
        return flexdir + File.separator + LICENSE_FILE_NAME;
    }

    public Properties loadHiddenProperties()
    {
        Properties hiddenProperties = new Properties();

        FileInputStream in = null;

        try
        {
            in = new FileInputStream(getHiddenFileName());
            hiddenProperties.load(in);

        }
        catch (java.io.FileNotFoundException io)
        {
            // Do nothing
        }
        catch (Exception e)
        {
            // Do nothing
        }
        finally
        {
            if (in != null)
            {
                try { in.close(); } catch (IOException ioe) {}
            }
        }

        return hiddenProperties;
    }

    public Properties loadProperties() throws FileNotFoundException, Exception
    {
        Properties properties = new Properties();

        FileInputStream in = null;

        try
        {
            in = new FileInputStream(getPropertiesFileName());
            properties.load(in);
        }
        finally
        {
            if (in != null)
            {
                try { in.close(); } catch (IOException ioe) {}
            }
        }

        return properties;
    }

    public void storeHiddenProperties(Properties hidden) throws Exception
    {
        FileOutputStream out = null;

        try
        {
            out = new FileOutputStream(getHiddenFileName());
            hidden.store(out, null);
        }
        finally
        {
            if (out != null)
            {
                try { out.close(); } catch (IOException e) {}
            }
        }
    }

    public void storeProperties(Properties props) throws Exception
    {
        FileOutputStream out = null;

        try
        {
            out = new FileOutputStream(getPropertiesFileName());
            props.store(out, null);
        }
        finally
        {
            if (out != null)
            {
                try {
                    out.close();
                }
                catch (IOException ex)
                {
                    // ignore
                }
            }
        }
    }
}
