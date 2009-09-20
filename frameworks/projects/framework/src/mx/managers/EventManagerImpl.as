// =================================================================
/*
 *  Copyright (c) 2009 Style http://www.flexinstyle.org/
 *  Lance Pollard
 *  http://www.viatropos.com
 *  lancejpollard at gmail dot com
 *  
 *  Permission is hereby granted, free of charge, to any person
 *  obtaining a copy of this software and associated documentation
 *  files (the "Software"), to deal in the Software without
 *  restriction, including without limitation the rights to use,
 *  copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the
 *  Software is furnished to do so, subject to the following
 *  conditions:
 * 
 *  The above copyright notice and this permission notice shall be
 *  included in all copies or substantial portions of the Software.
 * 
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 *  EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES
 *  OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 *  NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
 *  HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 *  WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 *  FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
 *  OTHER DEALINGS IN THE SOFTWARE.
 */
// =================================================================

package mx.managers
{
	import flash.events.Event;
	import flash.events.IEventDispatcher;
	import flash.utils.Dictionary;
		
	/**
	 *	EventManagerImpl
	 *	
	 *	API_CHANGE
	 */
	public class EventManagerImpl
	{
		private var registeredCaptures:Dictionary = new Dictionary(false);
	
		/**
		 *	EventManagerImpl Constructor
		 */
		public function EventManagerImpl()
		{
			super();
		}
		
		public function addEventListener(type:String, listener:Function,
			useCapture:Boolean = false, priority:int = 0,
			useWeakReference:Boolean = false):void
		{
			if (useCapture && !registeredCaptures[type])
				registeredCaptures[type] = true;
		}
		
		public function removeEventListener(type:String, listener:Function,
			useCapture:Boolean = false):void
		{
			if (registeredCaptures[type])
				delete registeredCaptures[type];
		}
		
		public function capturable(type:String):Boolean
		{
			return registeredCaptures[type] != null;
		}
	}
}