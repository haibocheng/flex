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
	/** 
	 * <p>The ISWFContext interface is used to wraps methods that a client may wish to call in the context of a different SWF.
	 * The main usage is for calling the FTE line creation methods.
	 * There are basically two reasons why an application might want to use 
	 * this interface to control the line creation. First,
	 * if the application has a SWF that contains a font, and you want to 
	 * use that font from a different SWF, you can reuse the
	 * font if the TextLine was created from the same SWF that has the font. 
	 * Second, you can get a faster recompose time by reusing 
	 * existing TextLines. TLF does that internally, and when it is reusing 
	 * it will call recreateTextLine instead of createTextLine.
	 * Your application may have additional TextLines that it knows can be reused. 
	 * If so, in you may trap calls to createTextLine,
	 * and call TextBlock.recreateTextLine with the line to be reused instead 
	 * of TextBlock.createTextLine.</p>
	 *
	 * @includeExample examples\ITextLineCreator_ClassExample.as -noswf
	 * 
	 * @playerversion Flash 10
	 * @playerversion AIR 1.5
	 * @langversion 3.0
	 * 
	 * @playerversion Flash 10
	 * @playerversion AIR 1.5
	 * @langversion 3.0
	 */
	public interface ISWFContext
	{
	    /**
	     *  A way to call a method in a client controlled context.
	     *
	     *  @param fn The function or method to call
	     *  @param thisArg The this pointer for the function
	     *  @param argArray The arguments for the function
	     *  @param returns If true, the function returns a value
	     *
	     *  @return Whatever the function returns, if anything.
	     *
	     *  @see Function.apply
	
	     *  @langversion 3.0
	     *  @playerversion Flash 10
	     *  @playerversion AIR 1.5
	     *  @productversion Flex 3
	     */
	    function callInContext(fn:Function, thisArg:Object, argArray:Array, returns:Boolean=true):*;
	}
}
