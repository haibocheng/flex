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
package flashx.textLayout.elements
{
	import flashx.textLayout.tlf_internal;
	use namespace tlf_internal;
	
	/** Configuration that applies too all TextFlow objects
	 */
	public class GlobalConfiguration 
	{
		/** Specifies the callback used for font mapping
		* The callback takes a <code>flash.text.engine.FontDescription</code> object and updates it as needed.
		* 
		* After setting a new font mapping callback, or changing the behavior of the exisiting font mapping callback, 
		* the client must explicitly call <code>flashx.textLayout.elements.TextFlow.invalidateAllFormats</code> for each impacted text flow.
		* This ensures that whenever a leaf element in the text flow is recomposed next, the FontDescription applied to it is recalculated and the the callback is invoked. 
		* 
		* @see flash.text.engine.FontDescription FontDescription
		* @see flashx.textLayout.elements.TextFlow.invalidateAllFormats invalidateAllFormats
		* 
		* @playerversion Flash 10
		* @playerversion AIR 1.5
		* @langversion 3.0
		*/
		public static function get fontMapper():Function
		{ 
			return _fontMapper; 
		}
		public static function set fontMapper(val:Function):void
		{
			_fontMapper = val;
			FlowLeafElement.clearElementFormatsCache(); // clear out possibly stale mappings between computed TextLayoutFormats and ElementFormats
		}
		
		private static var _fontMapper:Function
	}
}
