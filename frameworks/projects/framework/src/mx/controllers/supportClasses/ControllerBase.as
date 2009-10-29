// =================================================================
/*
 *  Copyright (c) 2009 Spark
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

package mx.controllers.supportClasses
{
	import flash.events.Event;
	import flash.events.EventDispatcher;
	import flash.utils.getQualifiedClassName;
	
	import mx.controllers.IController;
	import mx.core.UIComponent;
	import mx.managers.EventManager;
	
	/**
	 *  ControllerBase is a default implementation of IController
	 *	for SkinnableComponent (though the interface is more generic).
	 *	
	 *	In more sophisticated subimplementations, you can use Metadata
	 *	to get/set values on the Component.  This means the controller
	 *	is a space to add (attach) blocks of code to a component at runtime.
	 */
	public class ControllerBase extends EventDispatcher implements IController
	{
		private var _component:UIComponent;
		[Bindable(event="componentChange")]
		/**
		 *  Component we are attaching to
		 */
		public function get component():UIComponent
		{
			return _component;
		}
		public function set component(value:UIComponent):void
		{
			if (_component == value) 
				return;
			_component = value;
			dispatchBindingEvent("componentChange");
		}
		
		private var _name:String;
		[Bindable(event="nameChange")]
		/**
		 *  Name of the controller, defaults to the class name
		 */
		public function get name():String
		{
			if (!_name)
				_name = getQualifiedClassName(this);
			return _name;
		}
		public function set name(value:String):void
		{
			if (_name == value) 
				return;
			_name = value;
			dispatchBindingEvent("nameChange");
		}
		
		public function ControllerBase()
		{
			super();
		}
		
		public function attach(target:Object):void
		{
			component = target as UIComponent;
		}
		
		public function detach(target:Object):void
		{
			component = null;
		}
		
		public function dispatchBindingEvent(type:String):void
		{
			if (hasEventListener(type) || EventManager.capturable(type))
				dispatchEvent(new Event(type));
		}
	}
}