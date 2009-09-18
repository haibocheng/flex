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

package flash.tools.debugger;

import java.io.File;
import java.io.IOException;

/**
 * Miscellaneous callbacks from the DJAPI to the debugger which is using it.
 * 
 * @author mmorearty
 */
public interface IDebuggerCallbacks
{
	/**
	 * Tells the debugger to recompute the values which will be returned by
	 * getHttpExe() and getPlayerExe().
	 * 
	 * This does NOT need to be called before the first call to either of
	 * those functions.  The intent of this function is to allow the debugger
	 * to cache any expensive calculations, but still allow for the possibility
	 * of recalculating the values from time to time (e.g. when a new launch
	 * is going to happen).
	 */
	public void recomputeExeLocations();

	/**
	 * Returns the executable of the browser to launch for http: URLs, or
	 * <code>null</code> if not known.
	 */
	public File getHttpExe();
	
	/**
	 * Returns the executable for the standalone Flash player, or <code>null</code>
	 * if not known.
	 */
	public File getPlayerExe();

	/**
	 * Returns a name such as "firefox" or "Web browser", the name of the
	 * browser, useful for error messages. Never returns <code>null</code>.
	 */
	public String getHttpExeName();

	/**
	 * Returns a name such as "SAFlashPlayer.exe" or "gflashplayer" or "Flash
	 * player", the name of the standalone player, useful for error messages.
	 * Never returns <code>null</code>.
	 */
	public String getPlayerExeName();

	/**
	 * Launches a debug target.  The arguments are the same as those of
	 * Runtime.exec().
	 */
	public Process launchDebugTarget(String[] cmd) throws IOException;

	/**
	 * Query the Windows registry.
	 * 
	 * @param key
	 *            The registry key, in a format suitable for the REG.EXE
	 *            program. You must use full key names such as
	 *            HKEY_LOCAL_MACHINE rather the shorter abbreviations such as
	 *            HKLM.
	 * @param value
	 *            The value within that key, or null for the unnamed ("empty")
	 *            value
	 * @return the value stored at the location, or null if key or value was not
	 *         found
	 * @throws IOException
	 *             indicates the registry query failed -- warning, this can
	 *             really happen! Some implementations of this function don't
	 *             work on Windows 2000. So, this function should not be counted
	 *             on too heavily -- you should have a backup plan.
	 */
	public String queryWindowsRegistry(String key, String value) throws IOException;

	/**
	 * Returns the version number of an application. For example, Firefox 3.5.4
	 * would return new int[] { 3, 5 }.
	 * <p>
	 * As of this writing, the only thing this is used for is to determine, on
	 * Windows, whether the user is running IE 8; if he is, we need to pass the
	 * "-noframemerging" command-line argument. It is generally okay to just
	 * return <code>null</code> from this function; a robust implementation is
	 * not required.
	 * 
	 * @param application
	 *            the application whose version number is desired. On Windows,
	 *            this will typically be a path to a .exe file. On Mac, it may
	 *            point to a .app directory such as "/Applications/Safari.app",
	 *            or it may point to the underlying binary, such as
	 *            "/Applications/Safari.app/Contents/MacOS/Safari".
	 * @return an array of two integers if the version can be determined, or
	 *         null if it cannot be determined. The first integer is the major
	 *         version number, and the second integer is the minor version
	 *         number. More detailed information cannot be provided, because
	 *         this function needs to be cross- platform, and the format of
	 *         version information tends to vary widely from one platform to
	 *         another.
	 * @throws IOException
	 *             e.g. for file not found, etc.
	 */
	public int[] getAppVersion(File application) throws IOException;
}
