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

package flash.tools.debugger;

import java.io.File;

/**
 * @author mmorearty
 */
public class AIRLaunchInfo
{
	/**
	 * Full path to the AIR Debug Launcher, <code>adl.exe</code> (Windows) or
	 * <code>adl</code> (Mac/Linux).  This is mandatory.
	 */
	public File airDebugLauncher;

	/**
	 * The directory that has runtime.dll, or <code>null</code> to
	 * use the default.
	 */
	public File airRuntimeDir;

	/**
	 * The filename of the security policy to use, or <code>null</code> to
	 * use the default.
	 */
	public File airSecurityPolicy;

	/**
	 * The directory to specify as the application's content root, or
	 * <code>null</code> to not tell ADL where the content root is, in which
	 * case ADL will use the directory of the application.xml file as the
	 * content root.
	 */
	public File applicationContentRootDir;

	/**
	 * Array of command-line arguments for the user's program. These are
	 * specific to the user's program; they are not processed by AIR itself,
	 * just passed on to the user's app.
	 * <p>
	 * Note, this class has both <code>applicationArgumentsArray</code> and
	 * {@link #applicationArguments}. <code>applicationArgumentsArray</code>
	 * accepts an array of arguments, and passes them down as-is to the
	 * operating system. <code>applicationArguments</code> takes a single
	 * string, splits it into arguments, and passes the result to the operating
	 * system. You can use whichever one is more convenient for you; typically,
	 * one of these would be <code>null</code>. If both are non-
	 * <code>null</code>, then <code>applicationArgumentsArray</code> takes
	 * precedence, and <code>applicationArguments</code> is ignored.
	 */
	public String[] applicationArgumentsArray;

	/**
	 * Command-line arguments for the user's program. These are specific to the
	 * user's program; they are not processed by AIR itself, just passed on to
	 * the user's app.
	 * <p>
	 * Note, see the comment above on {@link #applicationArgumentsArray}.
	 */
	public String applicationArguments;

	/**
	 * The publisher ID to use; passed to adl's "-pubid" option.  If
	 * null, no pubid is passed to adl.
	 */
	public String airPublisherID;
}
