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
package flashx.textLayout.compose
{	
	import flash.text.engine.TextLine;
	import flash.utils.Dictionary;
	
	import flashx.textLayout.debug.assert;
	import flashx.textLayout.elements.Configuration;
	import flashx.textLayout.tlf_internal;
	
	use namespace tlf_internal;
	
	/** 
	 * The TextLineRecycler class provides support for recycling of TextLines.  Some player versions support a recreateTextLine.  Passing TextLines
	 * to the recycler makes them available for reuse.  This improves Player performance.
	 *
	 * @playerversion Flash 10
	 * @playerversion AIR 1.5
	 * @langversion 3.0
	 */ 
	public class TextLineRecycler
	{
		static private var _textLineRecyclerEnabled:Boolean = Configuration.playerEnablesArgoFeatures;
		
		/** Controls if the TLF recycler enabled.   It can only be enabled in 10.1 or later players.
		 * @playerversion Flash 10
		 * @playerversion AIR 1.5
		 * @langversion 3.0
		 */
		static public function get textLineRecyclerEnabled():Boolean
		{ return _textLineRecyclerEnabled; }
		static public function set textLineRecyclerEnabled(value:Boolean):void
		{ _textLineRecyclerEnabled = value ? Configuration.playerEnablesArgoFeatures : false; }
		
		// manage a cache of TextLine's that can be reused
		// This version uses a dictionary that holds the TextLines as weak references
		static private var reusableLineCache:Dictionary = new Dictionary(true);
		
		/**
		 * Add a TextLine to the pool for reuse. TextLines for reuse should have null userData and null parent. 
		 * @playerversion Flash 10
		 * @playerversion AIR 1.5
		 * @langversion 3.0
		 */

		static public function addLineForReuse(textLine:TextLine):void
		{
			CONFIG::debug { assert(textLine.parent == null && textLine.userData == null && (textLine.validity == "invalid"||textLine.validity == "static"),"textLine not ready for reuse"); }
			if (_textLineRecyclerEnabled)
			{
				CONFIG::debug 
				{
					for each (var line:TextLine in reusableLineCache)
					{
						 assert(line != textLine,"READDING LINE TO CACHE");
					}
				}

				reusableLineCache[textLine] = null;
			}
		} 
		
		/**
		 * Return a TextLine from the pool for reuse. 
		 * @playerversion Flash 10
		 * @playerversion AIR 1.5
		 * @langversion 3.0
		 */

		static public function getLineForReuse():TextLine
		{
			if (_textLineRecyclerEnabled)
			{
				for (var obj:Object in reusableLineCache)
				{
					// remove from the cache
					delete reusableLineCache[obj];
					return obj as TextLine;
				}
			}
			return null;
		}
		
		/** @private empty the reusableLineCache */
		static tlf_internal function emptyReusableLineCache():void
		{
			reusableLineCache = new Dictionary(true);
		}
	}
}
