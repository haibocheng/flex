////////////////////////////////////////////////////////////////////////////////
//
//  ADOBE SYSTEMS INCORPORATED
//  Copyright 2008-2009 Adobe Systems Incorporated
//  All Rights Reserved.
//
//  NOTICE: Adobe permits you to use, modify, and distribute this file
//  in accordance with the terms of the license agreement accompanying it.
//
//////////////////////////////////////////////////////////////////////////////////
package flashx.textLayout.container
{
	import flash.events.Event;
	/** Interface to support TLF managed containers in a security sandbox.
	 *
	 * @playerversion Flash 10
	 * @playerversion AIR 1.5
	 * @langversion 3.0
	 */
	public interface ISandboxSupport
	{
		/** Called to request mouseUpSomewhere and mouseMoveSomewhere events */
		function beginMouseCapture():void;
		/** Called when mouseUpSomewhere and mouseMoveSomewhere events no longer needed */
		function endMouseCapture():void;
		/** Client calls to forward a MouseUpSomewhere*/
		function mouseUpSomewhere(event:Event):void;
		/** Client calls to forward a MouseMoveSomewhere event */
		function mouseMoveSomewhere(event:Event):void;
	}
}
