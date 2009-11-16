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
package flashx.textLayout.operations
{

	import flashx.textLayout.debug.assert;
	import flashx.textLayout.edit.ElementRange;
	import flashx.textLayout.edit.ParaEdit;
	import flashx.textLayout.edit.SelectionState;
	import flashx.textLayout.elements.TextFlow;
	import flashx.textLayout.formats.Category;
	import flashx.textLayout.formats.ITextLayoutFormat;
	import flashx.textLayout.formats.TextLayoutFormat;
	import flashx.textLayout.property.Property;
	import flashx.textLayout.tlf_internal;
	use namespace tlf_internal;
		
	/**
	 * The UndefineFormatOperation class encapsulates a way to undefine formats.
	 * 
	 * <p>An UndefineFormatOperation undefines properties set in the leaf format to the text in the 
	 * specified range (no change is made if the specified range is a single point). 
	 * It undefines properties set in the paragraph format to any paragraphs at least partially within the range 
	 * (or a single paragraph if the range is a single point). 
	 * And it undefines properties set in the container format to any containers at least partially within the range 
	 * (or a single container if the range is a single point).</p>
	 *
	 * @see flashx.textLayout.edit.EditManager
	 * @see flashx.textLayout.events.FlowOperationEvent
	 * 
	 * @includeExample examples\ApplyFormatOperation_example.as -noswf
	 * 
	 * @playerversion Flash 10
	 * @playerversion AIR 1.5
	 * @langversion 3.0 
	 */
	public class UndefineFormatOperation extends FlowTextOperation
	{
		private var applyLeafFormat:ITextLayoutFormat;
		private var applyParagraphFormat:ITextLayoutFormat;
		private var applyContainerFormat:ITextLayoutFormat;

		// helper array of styles to revert
		// each entry has a begIdx, endIdx, ContainerFormat
		private var undoLeafArray:Array;	
		private var undoParagraphArray:Array;	
		private var undoContainerArray:Array;	
		
		/** 
		 * Creates an UndefineFormatOperation object.
		 *
		 *  @param operationState	Defines the text range to which the format is applied.
		 *  @param leafFormat	 The format whose set values indicate properties to undefine to LeafFlowElement objects in the selected range.
		 *  @param paragraphFormat The format whose set values indicate properties to undefine to ParagraphElement objects in the selected range.
		 *  @param containerFormat The format whose set values indicate properties to undefine to ContainerController objects in the selected range.
		 * 
		 * @playerversion Flash 10
		 * @playerversion AIR 1.5
	 	 * @langversion 3.0 
		 */		
		public function UndefineFormatOperation(operationState:SelectionState, leafFormat:ITextLayoutFormat, paragraphFormat:ITextLayoutFormat, containerFormat:ITextLayoutFormat = null)
		{
			super(operationState);
			this.leafFormat = leafFormat;
			this.paragraphFormat = paragraphFormat;
			this.containerFormat = containerFormat;
		}

		/** 
		 * The format properties to undefine on the leaf elements in the range.
		 * 
		 * <p>If the range of this operation is a point, or if <code>leafFormat</code> is <code>null</code>,
		 * then no leaf element formats are changed.</p>
		 * 
		 * @playerversion Flash 10
		 * @playerversion AIR 1.5
	 	 * @langversion 3.0 
		*/
		public function get leafFormat():ITextLayoutFormat
		{
			return applyLeafFormat;
		}
		public function set leafFormat(value:ITextLayoutFormat):void
		{
			applyLeafFormat = value ? new TextLayoutFormat(value) : null;
		}
		
		/** 
		 * The format properties to undefine on the paragraphs in the range.
		 * 
		 * <p>The formats of any paragraphs at least partially within the range are updated. 
		 * If the range of this operation is a point, then a single paragraph is updated.
		 * If <code>paragraphFormat</code> is <code>null</code>, then no paragraph formats are changed.</p>
		 * 
		 * @playerversion Flash 10
		 * @playerversion AIR 1.5
	 	 * @langversion 3.0 
		*/
		public function get paragraphFormat():ITextLayoutFormat
		{
			return applyParagraphFormat;
		}
		public function set paragraphFormat(value:ITextLayoutFormat):void
		{
			applyParagraphFormat = value ? new TextLayoutFormat(value) : null;
		}
		
		/** 
		 * The format properties to undefine on the containers in the range.
		 * 
		 * <p>The formats of any containers at least partially within the range are updated. 
		 * If the range of this operation is a point, then a single container is updated.
		 * If <code>containerFormat</code> is <code>null</code>, then no container formats are changed.</p>
		 * 
		 * @playerversion Flash 10
		 * @playerversion AIR 1.5
	 	 * @langversion 3.0 
		*/
		public function get containerFormat():ITextLayoutFormat
		{
			return applyContainerFormat;
		}
		public function set containerFormat(value:ITextLayoutFormat):void
		{
			applyContainerFormat = value ? new TextLayoutFormat(value) : null;
		}
		
		private function doInternal():void
		{
			// Apply character format
			if (applyLeafFormat && absoluteStart != absoluteEnd)
			{
				var range:ElementRange = ElementRange.createElementRange(textFlow, absoluteStart,absoluteEnd);
				
				var begSel:int = range.absoluteStart;
				var endSel:int = range.absoluteEnd;
				if(endSel == textFlow.textLength - 1)
					++endSel;
					
			//	CONFIG::debug { if (begSel != absoluteStart || endSel != absoluteEnd) trace("found mismatch ApplyFormatOperation"); }
				if (!undoLeafArray)
				{
					undoLeafArray = new Array();
					ParaEdit.cacheStyleInformation(textFlow,begSel,endSel,undoLeafArray);
				}
				ParaEdit.applyTextStyleChange(textFlow,begSel,endSel,null,applyLeafFormat);
			}

			if (applyParagraphFormat)
			{
				if (!undoParagraphArray)
				{
					undoParagraphArray = new Array();
					ParaEdit.cacheParagraphStyleInformation(textFlow,absoluteStart, absoluteEnd,undoParagraphArray);
				}
				ParaEdit.applyParagraphStyleChange(textFlow,absoluteStart, absoluteEnd, null, applyParagraphFormat);
			}
			if (applyContainerFormat)
			{
				if (!undoContainerArray)
				{
					undoContainerArray = new Array();
					ParaEdit.cacheContainerStyleInformation(textFlow,absoluteStart,absoluteEnd,undoContainerArray);
				}
				ParaEdit.applyContainerStyleChange(textFlow,absoluteStart,absoluteEnd, null, applyContainerFormat);
			}
		}
		
		/** @private */
		public override function doOperation():Boolean
		{ 
			doInternal(); 
			return true;
		}	
		
		/** @private */
		public override function undo():SelectionState
		{ 
			var obj:Object;
			
			// Undo character format changes
			for each (obj in undoLeafArray)
				ParaEdit.setTextStyleChange(textFlow,obj.begIdx,obj.endIdx,obj.style);

			// Undo paragraph format changes
			for each (obj in undoParagraphArray)
				ParaEdit.setParagraphStyleChange(textFlow,obj.begIdx,obj.endIdx,obj.attributes);

			// Undo container format changes
			for each (obj in undoContainerArray)
				ParaEdit.setContainerStyleChange(textFlow,obj);

			return originalSelectionState;
		}
		
	}
}
