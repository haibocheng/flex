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
	
	/**
	 *	EventManager
	 *	
	 *	This class adds the functionality to see if we need to dispatch an event,
	 *	more thoroughly
	 *	
	 *	API_CHANGE
	 */
	public class EventManager
	{
		
		private static var _instance:EventManagerImpl;
		/**
		 *  Singleton
		 */
		public static function get instance():EventManagerImpl
		{
			if (_instance == null)
				_instance = new EventManagerImpl();
			return _instance;
		}
	
		/**
		 *	EventManager Constructor
		 */
		public function EventManager()
		{
			super();
		}
		
		public static function addEventListener(type:String, listener:Function,
			useCapture:Boolean = false, priority:int = 0,
			useWeakReference:Boolean = false):void
		{
			instance.addEventListener(type, listener, useCapture, priority, useWeakReference);
		}
		
		public static function removeEventListener(type:String, listener:Function,
			useCapture:Boolean = false):void
		{
			instance.removeEventListener(type, listener, useCapture);
		}
		
		public static function capturable(type:String):Boolean
		{
			return instance.capturable(type);
		}
	}
}