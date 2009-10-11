package flash.text.ime
{

import flash.geom.Rectangle;

//
// IIMEClient
//

/**
* Interface for IME clients.  If your .swf application has a text engine that calls flash.text.engine directly, you
* need to implement this interface to support editing text inline using an IME. If your .swf uses TextField objects, 
* this interface is not used. TextLayoutFramework uses this interface to support the IME, so clients using TLF do not
* to implement this interface.
*
* 
* @see flash.text.ime.CompositionAttributeRange
* 
* @playerversion Flash 10.1
* @playerversion AIR 1.5
* @langversion 3.0
*/
public interface IIMEClient
{
	/**
	* Callback for updating the contents of the inline editing session.
	* This gets called whenever the text being edited with the IME has changed
	* and its contents are used by the client to redraw the entire inline edit session.
	* 
	* @playerversion Flash 10.1
	* @langversion 3.0
	* 
	* @param text  contains the text of the inline edit session from the IME
	* @param attributes  contains an array of composition clauses with adornment info 
	* @param relativeSelectionStart  start of the inline session relative to the start of the text object
	* @param relativeSelectionEnd  end of the inline session relative to the start of the text object
	*/
	function updateComposition(text:String, attributes:Vector.<CompositionAttributeRange>,
								compositionStartIndex:int, compositionEndIndex:int):void;
								
	/**
	* Use this callback to end the inline editing session and confirm the text.
	* 
	* @playerversion Flash 10.1
	* @langversion 3.0
	* 
	* @param text  the final state of the text in the inline session (the text that got confirmed).
	* @param preserveSelection  when true, you should not reset the current selection to the end of the confirmed text.
	*/
	function confirmComposition(text:String = null, preserveSelection:Boolean = false):void;

	/**
	* This callback is used by the IME to query the bounding box of the text being edited with the IME
	* It is used to place the candidate window and set the mouse cursor when over the inline session
	* 
	* @playerversion Flash 10.1
	* @langversion 3.0
	* 
	* @param startIndex an integer that specifies the starting location of the range of text for which you need to measure the bounding box.
	* @param endIndex Optional; an integer that specifies the ending location of the range of text for which you need to measure the bounding box.
	*
	* @return  the bounding box of the specified range of text, or <code>null</code> if either or both of the indexes are invalid.
	* The same value should be returned independant of whether <code>startIndex</code> is greater or less than <code>endIndex</code>.
	*/
	function getTextBounds(startIndex:int, endIndex:int):Rectangle;

	/** 
	 * The zero-based character index value of the start of the current edit session text (i.e.
	 * all text in the inline session that is still not yet confirmed to the document).
	 *
	 * @return the index of the first character of the composition, or <code>-1</code> if there is no active composition.
	 * 
	 * @playerversion Flash 10.1
	 * @langversion 3.0
	 */
	function get compositionStartIndex():int;

	/** 
	 * The zero-based character index value of the end of the current edit session text (i.e.
	 * all text in the inline session that is still not yet confirmed to the document).
	 *
	 * @return the index of the last character of the composition, or <code>-1</code> if there is no active composition.
	 * 
	 * @playerversion Flash 10.1
	 * @langversion 3.0
	 */
	function get compositionEndIndex():int;

	/** 
	 * Indicates whether the text in the component is vertical or not.  This will affect the positioning
	 * of the candidate window (beside vertical text, below horizontal text).
	 *
	 * @return <code>true</code> if the text is vertical, otherwise false.
	 * 
	 * @playerversion Flash 10.1
	 * @langversion 3.0
	 */
    function get verticalTextLayout():Boolean;
} // end of class
} // end of package
