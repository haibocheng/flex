////////////////////////////////////////////////////////////////////////////////
//
//  ADOBE SYSTEMS INCORPORATED
//  Copyright 2004-2006 Adobe Systems Incorporated
//  All Rights Reserved.
//
//  NOTICE: Adobe permits you to use, modify, and distribute this file
//  in accordance with the terms of the license agreement accompanying it.
//
////////////////////////////////////////////////////////////////////////////////

package flash.tools.debugger;

/**
 * The SwfInfo object contains information relating to
 * a particular swf file that was loaded by the Player.
 * Each SWF file contains a list of actionscript source
 * files from which execution is performed.
 * 
 * It is important to note 2 or more SWF files may contain
 * multiple copies of the same source code.  From the 
 * Player's perspective and the API perspective these
 * copies are unique and it is up to the user of the 
 * API to detect these 'duplicate' files and either
 * filter them from the user and/or present an
 * appropriate disambiguous representation of 
 * the file names.  Also internally they are treated
 * as two distinct files and thus breakpoints 
 * will most likely need to be set on both files
 * independently.
 */
public interface SwfInfo
{
	/**
	 * The full path of the SWF. If the SWF does not have a path (e.g. if it was
	 * loaded via Loader.loadBytes()), then this returns "<unnamed-N>", where
	 * "N" is the value returned by <code>getUnnamedIndex()</code>.
	 */
	public String getPath();

	/**
	 * The URL for the SWF. Includes any options at the end of the URL. For
	 * example ?debug=true. If the SWF does not have a URL (e.g. if it was
	 * loaded via Loader.loadBytes()), then tihs returns "unnamed:N", where "N"
	 * is the value returned by <code>getUnnamedIndex()</code>.
	 */
	public String getUrl();

	/**
	 * Returns 0 for swfs that have a name, or a 1-based index for swfs that
	 * don't have a name.
	 * <p>
	 * Most swfs have a name; for those swfs, <code>getPath()</code> and
	 * <code>getUrl()</code> will return the path and URL of the swf. But there
	 * are some swfs for which no name is available, such as those that were
	 * loaded via <code>Loader.loadBytes()</code>. This includes framework RSLs
	 * with digests.
	 * <p>
	 * For swfs for which no name is available, <code>getUnnamedIndex()</code>
	 * returns a 1-based index to uniquely identify this child swf, and
	 * <code>getPath()</code> and <code>getUrl()</code> also return special
	 * values.
	 * 
	 * @see #getPath()
	 * @see #getUrl()
	 */
	public int getUnnamedIndex();

	/**
	 * The size of this SWF in bytes
	 */
	public int getSwfSize();

	/**
	 * The size of the debug SWD file, if any
	 * This may also be zero if the SWD load is in progress
	 * @throws InProgressException if the SWD has not yet been loaded
	 */
	public int getSwdSize(Session s) throws InProgressException;

	/**
	 * Indication that this SWF, which was previously loaded into
	 * the Player, is now currently unloaded.  All breakpoints
	 * set on any of the files contained within this SWF will
	 * be inactive.  These breakpoints will still exist in the 
	 * list returned by Session.getBreakpointList()
	 */
	public boolean isUnloaded();
	
	/**
	 * Indicates whether the contents of the SWF file
	 * have been completely processed.
	 * Completely processed means that calls to getSwdSize
	 * and other calls that may throw an InProgressException will
	 * not throw this exception.  Additionally the function
	 * and offset related calls within SourceFile will return
	 * non-null values once this call returns true.
	 * @since Version 2
	 */
	public boolean isProcessingComplete();

	/**
	 * Number of source files in this SWF.
	 * May be zero if no debug 
	 * @throws InProgressException if the SWD has not yet been loaded
	 */
	public int getSourceCount(Session s) throws InProgressException;

	/**
	 * List of source files that are contained within 
	 * this SWF.
	 * @throws InProgressException if the SWD has not yet been loaded
	 * @since Version 2
	 */
	public SourceFile[] getSourceList(Session s) throws InProgressException;

	/**
	 * Returns true if the given source file is contained 
	 * within this SWF. 
	 * @since Version 2
	 */
	public boolean containsSource(SourceFile f);
}
