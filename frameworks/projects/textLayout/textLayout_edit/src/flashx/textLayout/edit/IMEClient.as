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
package flashx.textLayout.edit {
	import flash.errors.IOError;
	import flash.geom.Point;
	import flash.geom.Rectangle;
	import flash.system.IME;
	import flash.text.engine.TextLine;
	import flash.text.ime.CompositionAttributeRange;
	import flash.text.ime.IIMEClient;
	
	import flashx.textLayout.debug.assert;
	import flashx.textLayout.elements.FlowLeafElement;
	import flashx.textLayout.elements.TextRange;
	import flashx.textLayout.formats.BlockProgression;
	import flashx.textLayout.formats.IMEStatus;
	import flashx.textLayout.operations.ApplyElementUserStyleOperation;
	import flashx.textLayout.operations.InsertTextOperation;
	import flashx.textLayout.tlf_internal;
	import flashx.textLayout.utils.GeometryUtil;
	import flashx.undo.IOperation;
	
	use namespace tlf_internal;

	internal class IMEClient implements IIMEClient
	{
		private var _editManager:EditManager;

		/** Maintain position of text we've inserted while in the middle of processing IME. */
		private var _imeAnchorPosition:int;		// start of IME text
		private var _imeLength:int;				// length of IME text
		CONFIG::debug { 
			private var _imeOperation:IOperation; 	// IME in-progress edits - used for debugging to confirm that operation we're undoing is the one we did via IME
		}

		public function IMEClient(editManager:EditManager)
		{
			_editManager = editManager;
			_imeAnchorPosition = _editManager.absoluteStart;
		}
		
		/** @private
		 * Handler function called when the selection has been changed.
		 * @playerversion Flash 10
		 * @playerversion AIR 1.5
 	 	 * @langversion 3.0
		 */
		public function selectionChanged():void
		{	
			// If we change the selection to something outside the session, abort the 
			// session. If we just moved the selection within the session, we tell the IME about the changes.
			if (_editManager.absoluteStart > _imeAnchorPosition + _imeLength || _editManager.absoluteEnd < _imeAnchorPosition)
			{
				//trace("selection changed to out of IME session");
				compositionAbandoned();
			}
			else 
			{
				// This code doesn't with current version of Argo, but should work in future
				//trace("selection changed within IME session");
			//	var imeCompositionSelectionChanged:Function = IME["compositionSelectionChanged"];
			//	if (IME["compositionSelectionChanged"] !== undefined)
			// 		imeCompositionSelectionChanged();
			}
		}

		private function doIMEClauseOperation(selState:SelectionState, clause:int):void
		{
 		    var leaf:FlowLeafElement = _editManager.textFlow.findLeaf(selState.absoluteStart);;
		    var leafAbsoluteStart:int = leaf.getAbsoluteStart();
			_editManager.doOperation(new ApplyElementUserStyleOperation(selState, leaf, IMEStatus.IME_CLAUSE, clause.toString(), selState.absoluteStart - leafAbsoluteStart, selState.absoluteEnd - leafAbsoluteStart));
		}
		
		private function doIMEStatusOperation(selState:SelectionState, attrRange:CompositionAttributeRange):void
		{
			var imeStatus:String;
			
			// Get the IME status from the converted & selected flags
			if (attrRange == null)
				imeStatus = IMEStatus.DEAD_KEY_INPUT_STATE;
    		else if (!attrRange.converted)
    		{
    			if(!attrRange.selected)
    				imeStatus = IMEStatus.NOT_SELECTED_RAW;
    			else
    				imeStatus = IMEStatus.SELECTED_RAW;
    		}
    		else
    		{
    			if (!attrRange.selected)
    				imeStatus = IMEStatus.NOT_SELECTED_CONVERTED;
    			else
    				imeStatus = IMEStatus.SELECTED_CONVERTED;
    		}


			// refind since the previous operation changed the spans
    		var leaf:FlowLeafElement = _editManager.textFlow.findLeaf(selState.absoluteStart);
			CONFIG::debug { assert(	leaf != null, "found null FlowLeafELement at" + (selState.absoluteStart).toString()); }						    		
    		var leafAbsoluteStart:int = leaf.getAbsoluteStart();

			_editManager.doOperation(new ApplyElementUserStyleOperation(selState, leaf, IMEStatus.IME_STATUS, imeStatus, selState.absoluteStart - leafAbsoluteStart, selState.absoluteEnd - leafAbsoluteStart));
		}
		
		private function doIMEUpdateOperation(text:String, attributes:Vector.<CompositionAttributeRange>):void
		{
		    var imeValue:String;
		    
			// Currently we're replacing the entire string each time, might be we could use the compositionStartIndex, endIndex &
			// only update what changed.
	    	var selState:SelectionState = new SelectionState(_editManager.textFlow, _imeAnchorPosition, _imeAnchorPosition + _imeLength);
				
			_editManager.beginCompositeOperation();

			var insertOp:InsertTextOperation = new InsertTextOperation(selState, text);
	    	_imeLength = text.length;
			_editManager.doOperation(insertOp);
			
			if (attributes && attributes.length > 0)
			{
				var attrLen:int = attributes.length;
				for (var i:int = 0; i < attrLen; i++)
				{
					var attrRange:CompositionAttributeRange = attributes[i];
    				var clauseSelState:SelectionState = new SelectionState(_editManager.textFlow, _imeAnchorPosition + attrRange.relativeStart, _imeAnchorPosition + attrRange.relativeEnd);

					doIMEClauseOperation(clauseSelState, i);
					doIMEStatusOperation(clauseSelState, attrRange);
				}
			}
			else // composing accented characters
			{	
				doIMEClauseOperation(selState, 0);
				doIMEStatusOperation(selState, null);
			}

			_editManager.endCompositeOperation();
		}
		
		// IME-related functions
		public function updateComposition(text:String, attributes:Vector.<CompositionAttributeRange>, compositionStartIndex:int, compositionEndIndex:int):void
	    {
			// Undo the previous interim ime operation, if there is one. Otherwise let the update IME operation handle the delete.
			// Doing it via undo keeps the undo stack in sync.
			if (_imeLength > 0 && _editManager.undoManager != null && _editManager.undoManager.peekUndo() != null)
			{
				CONFIG::debug { assert(_editManager.undoManager.peekUndo() == _imeOperation, "Unexpected operation in undo stack at end of IME update"); }
				_editManager.undo();
				_imeLength = 0; // prevent double deletion
			}
			doIMEUpdateOperation(text, attributes);
			CONFIG::debug {  _imeOperation = _editManager.undoManager ? _editManager.undoManager.peekUndo() : null; }
	    }
	    
	    public function confirmComposition(text:String = null, preserveSelection:Boolean = false):void
		{
			endIMESession();
		}
		
		private function compositionAbandoned():void
		{
			// In Argo we could just do this:
			// IME.compositionAbandoned();
			// but for support in Astro/Squirt where this API is undefined we do this:
			var imeCompositionAbandoned:Function = IME["compositionAbandoned"];
			if (IME["compositionAbandoned"] !== undefined)
				imeCompositionAbandoned();
			endIMESession();
		}
		
		private function endIMESession():void
		{
			// Undo the IME operation. We're going to re-add the text, without all the special attributes, as part of handling
			// the textInput event that comes next.
			if (_imeLength > 0)
			{
				if (_editManager.undoManager != null && _editManager.undoManager.peekUndo() != null)
				{
					//trace("undoing imeOperation at end of IME session");
					CONFIG::debug { assert(_editManager.undoManager.peekUndo() == _imeOperation, "Unexpected operation in undo stack at end of IME session"); }
					_editManager.undo();
					CONFIG::debug { assert(_editManager.undoManager.peekRedo() == _imeOperation, "Unexpected operation in redo stack at end of IME session"); }
					_editManager.undoManager.popRedo();
				}
				else 
				{
					var amtToDelete:int = _imeLength;
				    _imeLength = 0;	// prevent re-entrant delete
					_editManager.deleteText(new SelectionState(_editManager.textFlow, _imeAnchorPosition, _imeAnchorPosition + amtToDelete));
				}
			}

			// Clear IME state - tell EditManager to release IMEClient to finally close session
			_editManager.endIMESession();
		}
		
		public function getTextBounds(startIndex:int, endIndex:int):Rectangle
		{
			var boundsResult:Array = GeometryUtil.getHighlightBounds(new TextRange(_editManager.textFlow, _imeAnchorPosition + startIndex, _imeAnchorPosition + endIndex));
		    var bounds:Rectangle = boundsResult[0].rect; 
		    var textLine:TextLine = boundsResult[0].textLine; 
		    var resultTopLeft:Point = textLine.localToGlobal(bounds.topLeft);
		    var resultBottomRight:Point = textLine.localToGlobal(bounds.bottomRight);
		   // trace("getTextBounds returning", resultTopLeft.x, resultTopLeft.y, resultBottomRight.x - resultTopLeft.x, resultBottomRight.y - resultTopLeft.y);
		    return new Rectangle(resultTopLeft.x, resultTopLeft.y, resultBottomRight.x - resultTopLeft.x, resultBottomRight.y - resultTopLeft.y);
		}
		
		public function get compositionStartIndex():int
		{
			return _imeAnchorPosition;
		}
		
		public function get compositionEndIndex():int
		{
			return _imeAnchorPosition + _imeLength;
		}
		
		public function get verticalTextLayout():Boolean
		{
			return _editManager.textFlow.computedFormat.blockProgression == BlockProgression.RL;
		}
	}
}