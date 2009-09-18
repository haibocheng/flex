////////////////////////////////////////////////////////////////////////////////
//
//  ADOBE SYSTEMS INCORPORATED
//  Copyright 2009 Adobe Systems Incorporated
//  All Rights Reserved.
//
//  NOTICE: Adobe permits you to use, modify, and distribute this file
//  in accordance with the terms of the license agreement accompanying it.
//
////////////////////////////////////////////////////////////////////////////////

package flash.tools.debugger.concrete;

import java.io.File;

import flash.tools.debugger.Browser;
import flash.tools.debugger.Player;

/**
 * @author Mike Morearty
 */
public class AIRPlayer implements Player
{
	File m_adl;

	/**
	 * @param adl
	 *            The path to adl (Mac/Linux) or adl.exe (Windows); may be null
	 */
	public AIRPlayer(File adl)
	{
		m_adl = adl;
	}

	/*
	 * @see flash.tools.debugger.Player#getType()
	 */
	public int getType()
	{
		return AIR;
	}

	/*
	 * @see flash.tools.debugger.Player#getPath()
	 */
	public File getPath()
	{
		return m_adl;
	}

	/*
	 * @see flash.tools.debugger.Player#getBrowser()
	 */
	public Browser getBrowser()
	{
		return null;
	}
}
