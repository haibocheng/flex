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

package mx.utils
{
	import flash.system.ApplicationDomain;
	import flash.utils.*;
	
	/**
	 *	ClassUtil provides helpful methods for accessing class data
	 */
	public class ClassUtil
	{
		/**
		 *  Gets the Class object for a specific value.  The value can be a
		 *	String, a Class, or an Object (DisplayObject, UIComponent, or some random Object).
		 */
		public static function getClass(value:Object):Class
		{
			if (!(value is String || value is Class))
				value = getQualifiedClassName(value);

			var type:Class;
			if (value is Class)
			{
				type = value as Class;
			}
			else
			{
				try
				{
					type = ApplicationDomain.currentDomain.getDefinition(value as String) as Class;
				}
				catch (error:ReferenceError)
				{
					throw new Error("cannot find class " + value);
				}
			}
			return type;
		}
		
		/**
		 *  Returns a list of methods for the class.  Pass in the superclass depth for how many classes
		 *	you want this to return (class tree).  If it's -1, it will return the whole method list
		 */
		public static function getMethods(target:Object, superclassDepth:int = -1, keepGeneratedMethods:Boolean = false):Array {
			var description:XML = DescribeTypeCache.describeType(target).typeDescription;
			var hierarchy:Array = getHierarchy(target);
			var methodList:XMLList = description..method;
			var methodName:String, methods:Array = [], declaredBy:String;
			var methodXML:XML;
			var i:int = 0, n:int = methodList.length();
			for (i; i < n; i++) {
				methodXML = methodList[i];
				declaredBy = methodXML.@declaredBy;
				methodName = methodXML.@name;
				// we break because the xml list is orded hierarchically by class!
				if (superclassDepth != -1 && hierarchy.indexOf(declaredBy) >= superclassDepth) break;
				// ignore methods that start with underscore:
				if (methodName.charAt(0) == "_" && !keepGeneratedMethods) { continue; }
				methods.push(methodName);
			}
			// sort the method list, so there's some kind of order to the report:
			methods.sort(Array.CASEINSENSITIVE);
			return methods;
		}
		
		/**
		 *  Returns an Array of Strings representing the fully qualified name of each class
		 *	in the hierarchy.
		 */
		public static function getHierarchy(target:Object, stopClass:* = null):Array
		{
			if (stopClass && !(stopClass is String))
				stopClass = getFQN(stopClass);

			var hierarchy:Array = [getQualifiedClassName(target)];
			var description:XML = DescribeTypeCache.describeType(target).typeDescription;
			var superclasses:XMLList = description..extendsClass;
			var extendedType:String;
			var i:int = 0, n:int = superclasses.length();
			for (i; i < n; i++)
			{
				extendedType = superclasses[i].@type;
				if (extendedType == stopClass)
					break;
				hierarchy.push(extendedType);
			}
			return hierarchy;
		}
		
		/**
		 *  If you assign something like <code>accessorXML.&#64;type</code>, which is a string, to an Object,
		 *	as in <code>var type:Object = accessorXML.&#64;type</code>, <code>type</code> will be of type "XMLList"!
		 *	Same with passing <code>accessorXML.&#64;type</code> into a method, you must first assign it to a variable.
		 *	To get <code>accessorXML.&#64;type</code> as a String, assign it to a variable of type <code>String</code>:
		 *	<code>var type:String = accessorXML.&#64;type</code>, then pass that to a method.
		 */
		public static function getAccessors(target:Object, superclassDepth:int = -1,
				packages:Array = null, keepGeneratedMethods:Boolean = false):Array
		{
			var description:XML = DescribeTypeCache.describeType(target).typeDescription;
			var hierarchy:Array = getHierarchy(target);
			var accessorList:XMLList = description..accessor;
			var packageName:String, returnValue:Object, type:Object;
			var accessorXML:XML, declaredBy:String;
			var accessorName:String, accessors:Array = [];
			var i:int = 0, n:int = accessorList.length();
			for (i; i < n; i++)
			{
				accessorXML = accessorList[i];
				declaredBy = accessorXML.@declaredBy;
				accessorName = accessorXML.@name;
				packageName = accessorXML.@type;
				type = getType(packageName);
				if (superclassDepth != -1 && hierarchy.indexOf(declaredBy) >= superclassDepth)
					break;
				if (packages != null)
				{
					packageName = getOnlyPackageName(packageName);
					if (packages.indexOf(packageName) == -1)
						continue;
				}
				// ignore accessors that start with underscore:
				if (accessorName.charAt(0) == "_" && !keepGeneratedMethods) 
					continue;
				accessors.push(accessorName);
			}
			// sort the accessor list, so there's some kind of order to the report:
			accessors.sort(Array.CASEINSENSITIVE);
			return accessors;
		}
		
		/**
		 *  @return Returns a package-like string: "mx.controls.Button"
		 */
		public static function getPackageName(target:Object):String
		{
			var fqn:String = target is String ? target as String : getQualifiedClassName(target);
			return fqn.replace("::", ".");
		}
		
		/**
		 *  @return Returns only the package, without the Class: "mx.controls"
		 */
		public static function getOnlyPackageName(target:Object):String
		{
			var fqn:String = target is String ? target as String : getQualifiedClassName(target);
			return fqn.indexOf("::") != -1 ? fqn.slice(0, fqn.indexOf("::")) : fqn;
		}
		
		/**
		 *  @return Retuns the Class name without the package: "Button"
		 */
		public static function getUnqualifiedClassName(target:Object):String
		{
			var fqn:String = target is String ? target as String : getQualifiedClassName(target);
			return fqn.indexOf("::") != -1 ? fqn.substr(fqn.indexOf("::") + 2) : fqn;
		}
		
		/**
		 *  @return Retuns the Superclass name without the package: "Component"
		 */
		public static function getUnqualifiedSuperclassName(target:Object):String
		{
			var fqn:String = target is String ? target as String : getQualifiedSuperClassName(target);
			return fqn.indexOf("::") != -1 ? fqn.substr(fqn.indexOf("::") + 2) : fqn;
		}
	}
}