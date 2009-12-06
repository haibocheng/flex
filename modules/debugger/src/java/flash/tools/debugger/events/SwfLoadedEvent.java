////////////////////////////////////////////////////////////////////////////////
//
//  ADOBE SYSTEMS INCORPORATED
//  Copyright 2003-2006 Adobe Systems Incorporated
//  All Rights Reserved.
//
//  NOTICE: Adobe permits you to use, modify, and distribute this file
//  in accordance with the terms of the license agreement accompanying it.
//
////////////////////////////////////////////////////////////////////////////////

package flash.tools.debugger.events;

import flash.tools.debugger.SwfInfo;

/**
 * This event is fired when the player has completed the loading of 
 * the specified SWF.
 */
public class SwfLoadedEvent extends DebugEvent
{
	/** unique identifier for the SWF */
	public long id;				

	/** index of swf in Session.getSwfs() array */
	public int index;

	/**
	 * Full path name for SWF. If <code>unnamedIndex</code> is nonzero, then
	 * this is "<unnamed-N>", where N is the value of <code>unnamedIndex</code>.
	 */
	public String path;

	/** size of the loaded SWF in bytes */
	public long swfSize;

	/**
	 * URL of the loaded SWF. If <code>unnamedIndex</code> is nonzero, then this
	 * is "unnamed:N", where N is the value of <code>unnamedIndex</code>.
	 */
	public String url;

	/** port number related to the URL */
	public long port;

	/** name of host in which the SWF was loaded */
	public String host;

	/**
	 * @see SwfInfo#getUnnamedIndex()
	 */
	public int unnamedIndex;

	public SwfLoadedEvent(long id, int index, String path, String url,
			int unnamedIndex, String host, long port, long swfSize)
	{
		this.id = id;
		this.index = index;
		this.path = path;
		this.url = url;
		this.unnamedIndex = unnamedIndex;
		this.host = host;
		this.port = port;
		this.swfSize = swfSize;
	}
}
