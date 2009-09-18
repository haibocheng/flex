////////////////////////////////////////////////////////////////////////////////
//
//  ADOBE SYSTEMS INCORPORATED
//  Copyright 2005-2007 Adobe Systems Incorporated
//  All Rights Reserved.
//
//  NOTICE: Adobe permits you to use, modify, and distribute this file
//  in accordance with the terms of the license agreement accompanying it.
//
////////////////////////////////////////////////////////////////////////////////
package flex.webtier.services.config;

import flex.webtier.util.PathResolver;
import flex.webtier.util.ServletPathResolver;
import flex2.compiler.config.ConfigurationException;
import flex2.compiler.config.ConfigurationValue;
import java.io.File;


public class FileConfiguration
{
    private boolean enable;
    private String fileName;
    private String fileMaximumSize = "200KB";
    private int fileMaximumBackups = 3;

    public void cfgEnable(ConfigurationValue cfgval, boolean enable)
    {
        this.enable = enable;
    }

    public void setEnable(boolean enable)
    {
        this.enable = enable;
    }

    public boolean isEnable()
    {
        return enable;
    }

    public void cfgFileName(ConfigurationValue cv, String fileName) throws ConfigurationException
    {
        PathResolver originalPathResolver = PathResolver.getThreadLocalPathResolver();
        try
        {
            if (originalPathResolver instanceof ServletPathResolver)
            {
                PathResolver.setThreadLocalPathResolver(new LogFilePathResolver((ServletPathResolver)originalPathResolver));
            }

            File f = PathResolver.getThreadLocalPathResolver().resolveFile(fileName, false);

            // throw ConfigurationException only if file logging is enabled
            if (f == null && enable)
            {
                throw new ConfigurationException( "Log file " + fileName + " does not exist!", cv.getSource(), cv.getLine() );
            }

            this.fileName = f.getAbsolutePath();
        }
        finally
        {
            PathResolver.setThreadLocalPathResolver(originalPathResolver);
        }
    }

    public void setFileName(String fileName)
    {
        this.fileName = fileName;
    }

    public String getFileName()
    {
        return fileName;
    }

    public void cfgMaximumSize(ConfigurationValue cv, String fileMaximumSize) throws ConfigurationException
    {
        boolean useDefaultFileMaxSize = false;
        
        if (enable)
        {
            String fms = fileMaximumSize.toLowerCase();                       
            if (fms == null)
            {
                String msg = "maximum-size does not have a value in " + 
                    cv.getSource() + " at line " + cv.getLine() + 
                    ". Using the default "+this.fileMaximumSize;
                Configurator.storeWarning(msg);
                useDefaultFileMaxSize = true;                
            }
            
            if (fms.endsWith("kb") || fms.endsWith("mb")) 
            {
                fms = fms.substring(0, fms.length() - 2);
            }
            
            try
            {             
                int value = Integer.parseInt(fms);
                if (value < 1)
                {
                    String msg = "maximum-size does not have a positive "
                        + "integer " + "value in " + cv.getSource() + 
                        " at line " + cv.getLine() + ". Using the default "+ 
                        this.fileMaximumSize;                    
                    Configurator.storeWarning(msg);
                    useDefaultFileMaxSize = true;
                }
            }
            catch (Exception ex)
            {
                String msg = "maximum-size does not have an integer " +
                    "value in " + cv.getSource() + " at line " + cv.getLine() + 
                    ". Using the default "+this.fileMaximumSize;
                Configurator.storeWarning(msg);
                useDefaultFileMaxSize = true;
            }
          
        }   
        if (!useDefaultFileMaxSize)
        {
            this.fileMaximumSize = fileMaximumSize;
        }
    }

    public String getFileMaximumSize()
    {
        return fileMaximumSize;
    }

    public void cfgMaximumBackups(ConfigurationValue cv, int fileMaximumBackups)
    {
        this.fileMaximumBackups = fileMaximumBackups;
    }

    public int getFileMaximumBackups()
    {
        return fileMaximumBackups;
    }
}
