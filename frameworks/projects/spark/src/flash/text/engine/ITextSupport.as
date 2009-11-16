////////////////////////////////////////////////////////////////////////////////
//
//  ADOBE SYSTEMS INCORPORATED
//  Copyright 2009 Adobe Systems Incorporated
//  All Rights Reserved.
//
//  NOTICE: Adobe permits you to use, modify, and distribute this file
//  in accordance with the terms of the license agreement accompanying it.
//
////////////////////////////////////////////////////////////////////////////////

package flash.text.engine
{

/**
 *  The ITextSupport interface is implemented by objects
 *  that wish to implement inline IME, or contain text which should be
 *  searchable on the web, or are accessible. 
 * 
 *  @playerversion Flash 10.1
 *  @langversion 3.0
 */
public interface ITextSupport
{
	/**
	 *  Indicates whether the component implementing ITextSupport
	 *  supports reconversion (has editable text).
	 *
	 *  @playerversion Flash 10.1
	 *  @langversion 3.0
	 */
	function get canReconvert():Boolean;

	/** 
	 *  The zero-based character index value
	 *  of the first character in the current selection.
	 *  Components which wish to support inline IME
	 *  or Accessibility should override this method.
	 *
	 *  @return the index of the character at the anchor end
	 *  of the selection, or <code>-1</code> if no text is selected.
	 * 
	 *  @playerversion Flash 10.1
	 *  @langversion 3.0
	 */
	function get selectionAnchorIndex():int;

	/** 
	 *  The zero-based character index value
	 *  of the last character in the current selection.
	 *  Components which wish to support inline IME
	 *  or Accessibility should override this method.
	 *
	 *  @return the index of the character at the active end
	 *  of the selection, or <code>-1</code> if no text is selected.
	 * 
	 *  @playerversion Flash 10.1
	 *  @langversion 3.0
	 */
	function get selectionActiveIndex():int;
	
	/** 
	 *  Sets the range of selected text
	 *  in a component implementing ITextSupport.
	 *  If either of the arguments is out of bounds
	 *  the selection should not be changed.
	 *  Components which wish to support inline IME
	 *  should override this method.
	 * 
	 *  @param anchorIndex The zero-based index value
	 *  of the character at the anchor end of the selection
	 *
	 *  @param activeIndex The zero-based index value
	 *  of the character at the active end of the selection.
	 * 
	 *  @playerversion Flash 10.1
	 *  @langversion 3.0
	 */
	function selectRange(anchorIndex:int, activeIndex:int):void;
	
	/** 
	 *  Gets the specified range of text
	 *  from a component implementing ITextSupport.
	 *  To retrieve all text in the component, do not specify
	 *  values for <code>startIndex</code> and <code>endIndex</code>.
	 *  Components which wish to support inline IME
	 *  or web searchability should override this method.
	 *  Components overriding this method should ensure
	 *  that the default values of <code>-1</code> 
	 *  for <code>startIndex</code> and <code>endIndex</code> are supported.
	 * 
	 *  @param startIndex Optional; an integer that specifies
	 *  the starting location of the range of text to be retrieved.
	 *
	 *  @param endIndex Optional; an integer that specifies
	 *  the ending location of the range of text to be retrieved.
	 * 
	 *  @return The requested text, or <code>null</code>
	 *  if no text is available in the requested range
	 *  or if either or both of the indexes are invalid.
	 *  The same value should be returned 
	 *  independent of whether <code>startIndex</code>
	 *  is greater or less than <code>endIndex</code>.
	 * 
	 *  @playerversion Flash 10.1
	 *  @langversion 3.0
	 */
	function getTextInRange(startIndex:int = -1, endIndex:int = -1):String;
}

}
