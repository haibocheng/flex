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
 * The Watch object represents a single watchpoint within a Session
 * A watchpoint is a mechanism by which execution of the Player
 * can be halted when a particular variable is accessed.  The 
 * access type can be one of read, write or read/write.
 * @since Version 2
 */
public interface Watch
{
	/**
	 * Value id of the value whose member is being watched.
	 * For example if the watch is placed on 'a.b.c' then the id
	 * will be that of the value 'a.b'.  Session.getVariable()
	 * can be used to obtain the variable.  This combined with
	 * the memberName() forms the unique identifier for the Watch.
	 */
	public long getValueId();

	/**
	 * Name of variable member that is being watched.  
	 */
	public String getMemberName();

	/**
	 * The kind of watch placed on the variable being watched.
	 */
    public int getKind();
}
