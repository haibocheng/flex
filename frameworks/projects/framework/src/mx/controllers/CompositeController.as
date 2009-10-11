// =================================================================
/*
 *  Copyright (c) 2009
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

package mx.controllers
{
	import mx.controllers.IController;
	import mx.controllers.supportClasses.ControllerBase;
	import mx.core.UIComponent;
	
	[DefaultProperty("controllers")]
	/**
	 *	CompositeController gives you multiple controllers
	 */
	public class CompositeController extends ControllerBase
	{
		
		private var _controllers:Array;
		[ArrayElementType("mx.controllers.IController")]
		[Bindable(event="controllersChange")]
		/**
		 *  Array of controllers
		 */
		public function get controllers():Array
		{
			return _controllers;
		}
		public function set controllers(value:Array):void
		{
			if (_controllers == value) 
				return;
			detach(_controllers)
			_controllers = value;
			attach(_controllers);
			dispatchBindingEvent("controllersChange");
		}
	
		/**
		 *	CompositeController Constructor
		 */
		public function CompositeController()
		{
			super();
		}
		
		override public function attach(target:Object):void
		{
			if (target is UIComponent)
				super.attach(target);
			if (!(target is UIComponent) || !_controllers)
				return;
			var i:int = 0, n:int = _controllers.length;
			for (i; i < n; i++)
			{
				_controllers[i].attach(component);
			}
		}
		
		override public function detach(target:Object):void
		{
			if (!_controllers)
				return;
			var i:int = 0, n:int = _controllers.length;
			for (i; i < n; i++)
			{
				_controllers[i].detach(component);
			}
			if (target is UIComponent)
				super.detach(target);
		}
	}
}