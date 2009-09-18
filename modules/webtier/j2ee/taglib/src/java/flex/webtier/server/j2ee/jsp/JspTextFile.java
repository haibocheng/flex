////////////////////////////////////////////////////////////////////////////////
//
//  ADOBE SYSTEMS INCORPORATED
//  Copyright 2006-2007 Adobe Systems Incorporated
//  All Rights Reserved.
//
//  NOTICE: Adobe permits you to use, modify, and distribute this file
//  in accordance with the terms of the license agreement accompanying it.
//
////////////////////////////////////////////////////////////////////////////////
package flex.webtier.server.j2ee.jsp;

import flash.util.FileUtils;
import flash.util.Trace;
import flex2.compiler.io.FileUtil;
import flex2.compiler.io.LocalFile;
import flex2.compiler.io.TextFile;
import flex2.compiler.io.VirtualFile;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

public class JspTextFile extends TextFile
{    
    private InputStream inputStream;
    private String parentFileName;
	
	public JspTextFile(String text, String name, String nameForReporting, String parent, String mimeType)
	{
        super(text, name, nameForReporting, parent, mimeType, System.currentTimeMillis());

        this.parentFileName = parent;

        try 
        {
            this.inputStream = new ByteArrayInputStream(text.getBytes("UTF-8"));
        } 
        catch (UnsupportedEncodingException e)
        {
        }        
    }

    /**
	 * Return input stream...
	 */
	public InputStream getInputStream() throws IOException
	{
		return inputStream;
	}
	
    public VirtualFile resolve(String relativeStr)
    {
	
        File relativeFile = null;

		if (parentFileName != null)
		{
			relativeFile = FileUtil.openFile(new File(parentFileName), relativeStr);
		}

		VirtualFile result = null;

		if (relativeFile != null && FileUtils.exists(relativeFile))
		{
			result = new LocalFile(relativeFile);
		}

		if ((result != null) && Trace.pathResolver)
		{
			Trace.trace("JspTextFile.resolve: resolved " + relativeStr + " to " + result.getName());
		}

		return result;	
    }		
}