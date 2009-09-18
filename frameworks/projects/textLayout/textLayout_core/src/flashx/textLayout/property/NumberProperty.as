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
	/** A property description with a Number as its value. @private */
	public class NumberProperty extends Property
	{
		private var _minValue:Number;
		private var _maxValue:Number;
		
		public function NumberProperty(nameValue:String, defaultValue:Number, inherited:Boolean, category:String, minValue:Number, maxValue:Number)
		{
			super(nameValue, defaultValue, inherited, category);
			_minValue = minValue;
			_maxValue = maxValue;
		}
		
		public function get minValue():Number
		{ return _minValue; }
		public function get maxValue():Number
		{ return _maxValue; } 
		
		/** @private */
		public override function setHelper(currVal:*,newObject:*):*
		{ 
			CONFIG::debug { assert(newObject !== null,"Property.setHelper - null passed in did you mean undefined?"); }
			if (newObject === undefined || newObject == FormatValue.INHERIT)
				return newObject;

			var newVal:Number = Number(newObject);
			// Nan --> return the current value
			if (isNaN(newVal))
				return currVal;
			if (checkLowerLimit() && newVal < _minValue)
				return _minValue;
			if (checkUpperLimit() && newVal > _maxValue)
				return _maxValue;
			return newVal;
		}
		
		/** @private */
		public override function hash(val:Object, seed:uint):uint
		{ 
			if (val == FormatValue.INHERIT)
				return UintProperty.doHash(inheritHashValue, seed);
			return NumberProperty.doHash(val as Number, seed);
		}
		
		/** @private */
		tlf_internal static function doHash(num:Number, seed:uint):uint
		{ 
			//return stringHash(num.toString(), seed);
			
			var trunc:uint = uint(num);
			var hash:uint = UintProperty.doHash(trunc, seed);
			if (trunc != num)
			{
				var fraction:uint = (uint)((num - trunc) * 10000000000);
				hash =  UintProperty.doHash(fraction, hash);
			}
			
			return hash; 
		}
		
		/** @private */
		public override function valueFromString(str:String):*
		{ 
			if (str == null)
				return undefined;
			if (str == FormatValue.INHERIT)
				return str;				
			var newVal:Number = Number(str); 
			// if not a number return null
			return isNaN(newVal) ? null : newVal;
		}
	}
}
