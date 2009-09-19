////////////////////////////////////////////////////////////////////////////////
//
//  ADOBE SYSTEMS INCORPORATED
//  Copyright 2005-2006 Adobe Systems Incorporated
//  All Rights Reserved.
//
//  NOTICE: Adobe permits you to use, modify, and distribute this file
//  in accordance with the terms of the license agreement accompanying it.
//
////////////////////////////////////////////////////////////////////////////////

package flex2.linker;

import flash.localization.LocalizationManager;
import flex2.compiler.ILocalizableMessage;
import flex2.compiler.util.ThreadLocalToolkit;

/**
 * @author Clement Wong
 */
public class LinkerException extends Exception implements ILocalizableMessage
{
    private static final long serialVersionUID = 8871339015713300337L;
    
    public String path = null;  // normally nothing here
    public String level = ILocalizableMessage.ERROR;

    public String getLevel()
    {
        return level;
    }

    public String getPath()
    {
        return path;
    }

    public void setPath(String path)
    {
        this.path = path;
    }

    public int getLine()
    {
        return -1;
    }

    public void setLine(int line)
    {
    }

    public int getColumn()
    {
        return -1;
    }

    public void setColumn(int column)
    {
    }

	public Exception getExceptionDetail()
	{
		return null;
	}

	public boolean isPathAvailable()
	{
		return true;
	}

	public String getMessage()
	{
		String msg = super.getMessage();
		if (msg != null)
		{
			return msg;
		}
		else
		{
			LocalizationManager l10n = ThreadLocalToolkit.getLocalizationManager();
			if (l10n == null)
			{
				return null;
			}
			else
			{
				return l10n.getLocalizedTextString(this);
			}
		}
	}
	
	public String toString()
	{
		return getMessage();
	}

    public static class DependencyException extends LinkerException
    {
        private static final long serialVersionUID = -7140678608544707241L;
        
        public DependencyException( String symbol )
        {
            this.symbol = symbol;
        }

        public String getSymbol()
        {
            return symbol;
        }
        public String symbol;
    }

    public static class CircularReferenceException extends LinkerException
    {
        private static final long serialVersionUID = 958603762612962980L;
        
        public CircularReferenceException( String script )
        {
            this.script = script;
        }
        public String script;
    }
    public static class DuplicateSymbolException extends DependencyException
    {
        private static final long serialVersionUID = 2252695994941796010L;

        public DuplicateSymbolException( String symbol )
        {
            super( symbol );
        }
    }

    public static class MultipleDefinitionsException extends DuplicateSymbolException
    {
        private static final long serialVersionUID = -3479220112332457147L;
        
        public MultipleDefinitionsException( String symbol, String location1, String location2 )
        {
            super( symbol );
            this.location1 = location1;
            this.location2 = location2;
        }
        public String location1;
        public String location2;

    }
    public static class UndefinedSymbolException extends DependencyException
    {
        private static final long serialVersionUID = 7875240557043445474L;

        public UndefinedSymbolException( String symbol )
        {
            super( symbol );
        }

    }
    public static class PartialExternsException extends DependencyException
    {
        private static final long serialVersionUID = -3867374184480315501L;
        
        public PartialExternsException( String script, String symbol, String external  )
        {
            super( symbol );
            this.script = script;
            this.external = external;
        }
        public String script;
        public String external;
    }



    public static class LinkingFailed extends LinkerException
    {
        private static final long serialVersionUID = 8875781962486035947L;
    }

    public static class UnableToWriteLinkReport extends LinkerException
    {
        private static final long serialVersionUID = 7901515994910704022L;
        
        public UnableToWriteLinkReport( String filename )
        {
            this.level = ILocalizableMessage.WARNING;
            this.filename = filename;
        }
        public String filename;
    }

	public static class UnableToWriteResourceBundleList extends LinkerException
	{
	    private static final long serialVersionUID = -3409185617362768818L;
        
        public UnableToWriteResourceBundleList( String filename )
	    {
	        this.level = ILocalizableMessage.WARNING;
	        this.filename = filename;
	    }
	    public String filename;
	}
}
