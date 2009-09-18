////////////////////////////////////////////////////////////////////////////////
//
//  ADOBE SYSTEMS INCORPORATED
//  Copyright 2005-2008 Adobe Systems Incorporated
//  All Rights Reserved.
//
//  NOTICE: Adobe permits you to use, modify, and distribute this file
//  in accordance with the terms of the license agreement accompanying it.
//
////////////////////////////////////////////////////////////////////////////////

package flex.util;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.NotSerializableException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.runtime.RuntimeInstance;
import org.apache.velocity.runtime.RuntimeServices;
import org.apache.velocity.runtime.parser.node.SimpleNode;

public class SerializedTemplateFactory 
{
	public static Template load(String resourcePath) throws IOException, ClassNotFoundException
	{
		// assumption, this class and templates are found from same classloader, should be cool for now
		InputStream is = SerializedTemplateFactory.class.getClassLoader().getResourceAsStream(resourcePath);
		Object data = new ObjectInputStream(new BufferedInputStream(is)).readObject();		
		is.close();
		Template t = new Template();
		t.setData(data);
		return t;
	}
	
	/**
	 * Usage: template.vm 
	 * writes template.ser (canned velocity parse tree)
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception
	{
		Velocity.init();
		
		for(int i=0,j=args.length; i<j; i++)
		{
			String serName = args[i] + "s";
	        Template template = Velocity.getTemplate(args[i]);
	
	        try {
		        FileOutputStream fos = new FileOutputStream(serName);
		        ObjectOutputStream ooo = new ObjectOutputStream(fos);
		        Object data = template.getData();
		        ooo.writeObject(data);
		        ooo.close();
		        fos.close();
	        } catch(NotSerializableException nse) {
	        	System.err.println("Make sure you are using the special velocity in flex/sdk/lib.");
	        	throw nse;
	        }
		}
	}
}
