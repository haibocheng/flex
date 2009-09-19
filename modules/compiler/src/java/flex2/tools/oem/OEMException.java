////////////////////////////////////////////////////////////////////////////////
//
//  ADOBE SYSTEMS INCORPORATED
//  Copyright 2007 Adobe Systems Incorporated
//  All Rights Reserved.
//
//  NOTICE: Adobe permits you to use, modify, and distribute this file
//  in accordance with the terms of the license agreement accompanying it.
//
////////////////////////////////////////////////////////////////////////////////

package flex2.tools.oem;

import flex2.compiler.ILocalizableMessage;

public class OEMException extends Exception implements ILocalizableMessage
{
    private static final long serialVersionUID = -6282943339729427885L;
    public String path = null;  // normally nothing here
    public String level = ILocalizableMessage.ERROR;

    public Exception getExceptionDetail()
    {
        return null;
    }

    public boolean isPathAvailable()
    {
        return true;
    }

    public void setColumn(int column)
    {

    }

    public void setLine(int line)
    {

    }

    public void setPath(String path)
    {
        this.path = path;
    }

    public int getColumn()
    {
        return 0;
    }

    public String getLevel()
    {
        return level;
    }

    public int getLine()
    {
        return 0;
    }

    public String getPath()
    {
        return path;
    }

    /**
     *  The specified libraries form a circular dependency. 
     *
     */
    public static class CircularLibraryDependencyException extends OEMException
    {
        private static final long serialVersionUID = -1128789848162235759L;
        private String cause;
        private String circularDependency;
       
        public CircularLibraryDependencyException(String cause, String circularDependency)
        { 
            this.cause = cause;
            this.circularDependency = circularDependency;
        }

        @Override
        public String getMessage()
        {
            return cause;
        }

        public String getCircularDependency()
        {
            return circularDependency;
        }

        
    }
}
