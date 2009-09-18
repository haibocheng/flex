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
	/** An property description with an enumerated string as its value. @private */
	public class EnumStringProperty extends Property
	{
		private var _range:Object;
		
		public function EnumStringProperty(nameValue:String, defaultValue:String, inherited:Boolean, category:String, ... rest)
		{
			//TODO: implement function
			super(nameValue, defaultValue, inherited, category);
			_range = new Object();
			// rest is the list of possible values
			for (var i:int = 0; i < rest.length; i++)
				_range[rest[i]] = nextEnumHashValue++;
			_range[FormatValue.INHERIT] = nextEnumHashValue++;				
		}
		
		/** Returns object whose properties are the legal enum values */
		public function get range():Object
		{
			return Property.shallowCopy(_range); 
		}
		
		/** @private */
		public override function setHelper(currVal:*,newObject:*):*
		{
			CONFIG::debug { assert(newObject !== null,"Property.setHelper - null passed in did you mean undefined?"); }
			if (newObject === undefined)
				return newObject;
				
			return _range.hasOwnProperty(newObject) ? newObject : currVal;
		}
		
		/** @private */
		public override function hash(val:Object, seed:uint):uint
		{ 
			CONFIG::debug { assert(_range.hasOwnProperty(val), "String " + val + " not among possible values for this EnumStringProperty"); }
			return UintProperty.doHash(_range[val], seed);
		}
		
	}
}
