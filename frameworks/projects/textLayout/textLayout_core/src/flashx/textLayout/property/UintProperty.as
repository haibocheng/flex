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
package flashx.textLayout.property
{
	import flashx.textLayout.debug.assert;
	import flashx.textLayout.formats.FormatValue;
	import flashx.textLayout.tlf_internal;
		
	use namespace tlf_internal;

	[ExcludeClass]
	/** A property description with an unsigned integer as its value.  Typically used for color. @private */
	public class UintProperty extends Property
	{
		public function UintProperty(nameValue:String, defaultValue:uint, inherited:Boolean, category:String)
		{
			super(nameValue, defaultValue, inherited, category);
		}
		
		/** @private */
		public override function setHelper(currVal:*,newObject:*):*
		{ 
			CONFIG::debug { assert(newObject !== null,"Property.setHelper - null passed in did you mean undefined?"); }
			if (newObject === undefined || newObject == FormatValue.INHERIT)
				return newObject;

			var newVal:uint = newObject is String ? valueFromString(String(newObject)) as uint : uint(newObject);
				
			return newVal;
		}
		
		// Normally, we could just cast a string to a uint. However, the casting technique only works for
		// normal numbers and numbers preceded by "0x". We can encounter numbers of the form "#ffffffff"
		// which is an alternate form of "0xffffffff".
		/** @private */
		public override function valueFromString(str:String):*
		{
			if (str == null)
				return undefined;
			if (str == FormatValue.INHERIT)
				return str;
			// Allow for the convention #ffffffff to work as a unsigned hexadecimal number. (String
			// class supports 0xffffffff format)
			// Color values are typically written in the #ffffffff format.
			if (str.substr(0, 1) == "#")
				str = "0x" + str.substr(1, str.length-1);
			if (str.toLowerCase().substr(0, 2) == "0x")
				return uint(str);
			return 0;
		}
		
		/** @private */
		public override function toXMLString(val:Object):String
		{
			return val == FormatValue.INHERIT ? String(val) : "0x" + val.toString(16);
		}
		
		/** @private */
		public override function hash(val:Object, seed:uint):uint
		{ 
			if (val == FormatValue.INHERIT)
				return UintProperty.doHash(inheritHashValue, seed);
			return doHash(val as uint, seed);
		}
		
		/** @private */
		tlf_internal static function doHash(val:uint, seed:uint):uint
		{ 
			return ((seed << 5) ^ (seed >> 27)) ^ val;
		}
	}
}
