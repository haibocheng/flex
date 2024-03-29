/*****************************************************
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
package org.osmf.net
{
	import flash.events.NetStatusEvent;
	import flash.net.NetStream;
	
	import org.osmf.events.LoadEvent;
	import org.osmf.traits.DownloadableTrait;

	[ExcludeClass]
	
	/**
	 * @private
	 * 
	 * This class extends DownloadableTrait to provide access to the bytesLoaded and bytesTotal properties
	 * of NetStream.
	 */
	public class NetStreamDownloadableTrait extends DownloadableTrait
	{
		/**
		 * Constructor
		 * 
		 * @param netStream The NetStream object to be used for the retrieval of bytesLoaded and bytesTotal values
		 *  
		 *  
		 *  @langversion 3.0
		 *  @playerversion Flash 10
		 *  @playerversion AIR 1.0
		 *  @productversion OSMF 1.0
		 */
		public function NetStreamDownloadableTrait(netStream:NetStream)
		{
			super(NaN, NaN);
			
			_netStream = netStream;
			if (isNaN(_netStream.bytesTotal) || _netStream.bytesTotal <= 0)
			{
				_netStream.addEventListener(NetStatusEvent.NET_STATUS, onNetStatus);
			}
		}
		
		/**
		 * @inheritDoc
		 *  
		 *  @langversion 3.0
		 *  @playerversion Flash 10
		 *  @playerversion AIR 1.0
		 *  @productversion OSMF 1.0
		 */
		override public function get bytesLoaded():Number
		{
			return _netStream.bytesLoaded;
		}
		
		/**
		 * @inheritDoc
		 *  
		 *  @langversion 3.0
		 *  @playerversion Flash 10
		 *  @playerversion AIR 1.0
		 *  @productversion OSMF 1.0
		 */
		override public function get bytesTotal():Number
		{
			return _netStream.bytesTotal;
		}	
		
		//
		// Internals
		//
		
		private function onNetStatus(event:NetStatusEvent):void
		{
			if (!isNaN(_netStream.bytesTotal) && _netStream.bytesTotal > 0)
			{
				dispatchEvent
					( new LoadEvent
						( LoadEvent.BYTES_TOTAL_CHANGE
						, false
						, false
						, null
						, _netStream.bytesTotal
						)
					);
				_netStream.removeEventListener(NetStatusEvent.NET_STATUS, onNetStatus);
			}
		}
		
		private var _netStream:NetStream;	
	}
}