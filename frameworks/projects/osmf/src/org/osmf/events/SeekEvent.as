/******************************************************
*  
*  Copyright 2009 Adobe Systems Incorporated.  All Rights Reserved.
*  
*****************************************************
*  The contents of this file are subject to the Mozilla Public License
*  Version 1.1 (the "License"); you may not use this file except in
*  compliance with the License. You may obtain a copy of the License at
*  http://www.mozilla.org/MPL/
*   
*  Software distributed under the License is distributed on an "AS IS"
*  basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
*  License for the specific language governing rights and limitations
*  under the License.
*   
*  
*  The Initial Developer of the Original Code is Adobe Systems Incorporated.
*  Portions created by Adobe Systems Incorporated are Copyright (C) 2009 Adobe Systems 
*  Incorporated. All Rights Reserved. 
*  
*****************************************************/
package org.osmf.events
{
	import flash.events.Event;
	
	/**
	 * A SeekEvent is dispatched when the properties of an ISeekable trait change.
	 */	
	public class SeekEvent extends Event
	{
		/**
		 * The SeekEvent.SEEK_BEGIN constant defines the value
		 * of the type property of the event object for a seekBegin
		 * event.
		 * 
		 * @eventType SEEK_BEGIN  
		 *  
		 *  @langversion 3.0
		 *  @playerversion Flash 10
		 *  @playerversion AIR 1.0
		 *  @productversion OSMF 1.0
		 */	
		public static const SEEK_BEGIN:String = "seekBegin";

		/**
		 * The SeekEvent.SEEK_END constant defines the value
		 * of the type property of the event object for a seekEnd
		 * event.
		 * 
		 * @eventType SEEK_END  
		 *  
		 *  @langversion 3.0
		 *  @playerversion Flash 10
		 *  @playerversion AIR 1.0
		 *  @productversion OSMF 1.0
		 */	
		public static const SEEK_END:String = "seekEnd";
		
		/**
		 * 
		 * @param type Event type.
		 * @param bubbles Specifies whether the event can bubble up the display list hierarchy.
 		 * @param cancelable Specifies whether the behavior associated with the event can be prevented. 
		 * @param time The seek's target time, in seconds.
		 *  
		 *  @langversion 3.0
		 *  @playerversion Flash 10
		 *  @playerversion AIR 1.0
		 *  @productversion OSMF 1.0
		 */	
		public function SeekEvent(type:String, bubbles:Boolean=false, cancelable:Boolean=false, time:Number=NaN)
		{
			super(type, bubbles, cancelable);

			_time = time;
		}
		
		/**
		 * The seek's target time in seconds.
		 *  
		 *  @langversion 3.0
		 *  @playerversion Flash 10
		 *  @playerversion AIR 1.0
		 *  @productversion OSMF 1.0
		 */		
		public function get time():Number
		{
			return _time;
		}
		
		/**
		 * @private
		 */
		override public function clone():Event
		{
			return new SeekEvent(type, bubbles, cancelable, _time);
		}
		
		// Internals
		//
		
		private var _time:Number;
	}
}