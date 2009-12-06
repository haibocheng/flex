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
	
	/** Configuration that applies to all TextFlow objects.
	 */
	public class GlobalSettings 
	{
		/** 
		* Specifies the callback used for font mapping.
		* The callback takes a <code>flash.text.engine.FontDescription</code> object and updates it as needed.
		* 
		* After setting a new font mapping callback, or changing the behavior of the exisiting font mapping callback, 
		* the client must explicitly call <code>flashx.textLayout.elements.TextFlow.invalidateAllFormats</code> for each impacted text flow.
		* This ensures that whenever a leaf element in the text flow is next recomposed, the FontDescription applied to it is recalculated, and the the callback is invoked. 
		* 
		* @see flash.text.engine.FontDescription FontDescription
		* @see flashx.textLayout.elements.TextFlow.invalidateAllFormats invalidateAllFormats
		* 
		* @playerversion Flash 10
		* @playerversion AIR 1.5
		* @langversion 3.0
		*/
		public static function get fontMapperFunction():Function
		{ 
			return _fontMapperFunction; 
		}
		public static function set fontMapperFunction(val:Function):void
		{
			_fontMapperFunction = val;
			FlowLeafElement.clearElementFormatsCache(); // clear out possibly stale mappings between computed TextLayoutFormats and ElementFormats
		}
		
		private static var _fontMapperFunction:Function
		
		/** Controls whether the text will be visible to a search engine indexer. Defaults to <code>true</code>.
		 * 
		 * @playerversion Flash 10
		 * @playerversion AIR 1.5
		 * @langversion 3.0
		 */
		public static function get enableSearch():Boolean
		{
			return _enableSearch;
		}
		public static function set enableSearch(value:Boolean):void
		{
			_enableSearch = value;
		}
		
		private static var _enableSearch:Boolean = true;

		/** Function that takes two parameters, a resource id and an optional array of parameters to substitute into the string.
		 * The string is of form "Content {0} more content {1}".  The parameters are read from the optional array and substituted for the bracketed substrings
		 * TLF provides a default implementation with
		 * default strings.  Clients may replace this function with their own implementation for localization.
		 */
		public static function get getResourceStringFunction():Function
		{ 
			return _getResourceStringFunction; 
		}
		public static function set getResourceStringFunction(val:Function):void
		{
			_getResourceStringFunction = val;
		}
		
		private static var _getResourceStringFunction:Function = defaultGetResourceStringFunction;
		
		/** @private */
		
		private static const resourceDict:Object = 
		{
			missingStringResource:			"No string for resource {0}",
			// core errors
			invalidFlowElementConstruct:	"Attempted construct of invalid FlowElement subclass",
			invalidSplitAtPosition:			"Invalid parameter to splitAtPosition",
			badMXMLChildrenArgument: 		"Bad element of type {0} passed to mxmlChildren",
			badReplaceChildrenIndex: 		"Out of range index to FlowGroupElement.replaceChildren",
			invalidChildType: 				"NewElement not of a type that this can be parent of",
			badRemoveChild: 				"Child to remove not found",
			invalidSplitAtIndex:			"Invalid parameter to splitAtIndex",
			badShallowCopyRange: 			"Bad range in shallowCopy",
			badSurrogatePairCopy: 			"Copying only half of a surrogate pair in SpanElement.shallowCopy",
			invalidReplaceTextPositions:	"Invalid positions passed to SpanElement.replaceText",
			invalidSurrogatePairSplit: 		"Invalid splitting of a surrogate pair",
			badPropertyValue:				"Property {0} value {1} is out of range",
			// selection/editing
			illegalOperation:				"Illegal attempt to execute {0} operation",
			// shared import errors
			unexpectedXMLElementInSpan:		"Unexpected element {0} within a span",
			unexpectedNamespace:			"Unexpected namespace {0}",
			unknownElement: 				"Unknown element {0}",
			unknownAttribute: 				"Attribute {0} not permitted in element {1}",
			// html format import errors
			malformedTag:					"Malformed tag {0}",
			malformedMarkup: 				"Malformed markup {0}",
			// textlayoutformat import errors
			missingTextFlow: 				"No TextFlow to parse",
			expectedExactlyOneTextLayoutFormat: "Expected one and only one TextLayoutFormat in {0}" 	
		};
		
		/** @private */
		tlf_internal static function defaultGetResourceStringFunction(resourceName:String, parameters:Array = null):String
		{
			var value:String = String(resourceDict[resourceName]);
			
			if (value == null)
			{
				value = String(resourceDict["missingStringResource"]);
				parameters = [ resourceName ];
			}
			
			if (parameters)
				value = substitute(value, parameters);
			
			return value;
		}
		
		/** @private */
		tlf_internal static function substitute(str:String, ... rest):String
		{
			if (str == null) 
				return '';
			
			// Replace all of the parameters in the msg string.
			var len:uint = rest.length;
			var args:Array;
			if (len == 1 && rest[0] is Array)
			{
				args = rest[0] as Array;
				len = args.length;
			}
			else
			{
				args = rest;
			}
			
			for (var i:int = 0; i < len; i++)
			{
				str = str.replace(new RegExp("\\{"+i+"\\}", "g"), args[i]);
			}
			
			return str;
		}
	}
}
