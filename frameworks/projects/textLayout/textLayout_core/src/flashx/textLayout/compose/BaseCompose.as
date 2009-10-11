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
	import flash.geom.Rectangle;
	import flash.text.engine.TextBlock;
	import flash.text.engine.TextLine;
	import flash.text.engine.TextLineValidity;
	
	import flashx.textLayout.*;
	import flashx.textLayout.container.ContainerController;
	import flashx.textLayout.debug.Debugging;
	import flashx.textLayout.debug.assert;
	import flashx.textLayout.elements.*;
	import flashx.textLayout.formats.*;
	import flashx.textLayout.utils.LocaleUtil;
	
	use namespace tlf_internal;
	
	
	[ExcludeClass]
	/** @private Common composer base class */
	public class BaseCompose
	{
		protected var _parcelList:IParcelList;
		
		/** List of areas we're composing into, matches the container's bounding box */
		public function get parcelList():IParcelList
		{ return _parcelList; }
		
		/** Element of current location */
		protected var _curElement:FlowLeafElement;		
		/** Absolute start position of _curElement */
		protected var _curElementStart:int;		
		/** Offset from element start to current location */
		protected var _curElementOffset:int;		
		/** ParagraphElement that contains the current location */
		protected var _curParaElement:ParagraphElement;	
		protected var _curParaFormat:ITextLayoutFormat;
		/** Absolute start position of _curParaElement */
		protected var _curParaStart:int;
		/** leading direction for the current line's para (set when line is being composed and committed to _lastLineLeadingModel when line is finalized) */
		private var _curLineLeadingModel:String = "";
		/** leading amount for the current line (set when line is being composed and committed to _lastLineLeading when line is finalized) */
		private var _curLineLeading:Number;
		/** leading direction for the last line's para */
		protected var _lastLineLeadingModel:String = "";
		/** leading amount for the last line */
		protected var _lastLineLeading:Number;
		/** descent of the last line */
		protected var _lastLineDescent:Number;
		/** Amount of spaceAfter added to the previous line */
		protected var _spaceCarried:Number;
		/** BlockProgression - vertical horizontal etc. @see text.formats.BlockProgression */
		protected var _blockProgression:String;
		
		/** Minimum left edge coordinate across all the parcels in a controller */
		private var _controllerLeft:Number;
		/** Minimum top edge across all the parcels in a controller */
		private var _controllerTop:Number;
		/** Maximum right edge coordinate across all the parcels in a controller */
		private var _controllerRight:Number;
		/** Maximum bottom edge coordinate across all the parcels in a controller */
		private var _controllerBottom:Number;
		
		/** Maximum horizontal extension from left/right edge of the parcel.  Alignment width for the parcel. */
		protected var _contentLogicalExtent:Number;
		/** Minimum left edge coordinate across all the parcels in a controller */
		protected var _parcelLeft:Number;
		/** Minimum top edge across all the parcels in a controller */
		protected var _parcelTop:Number;
		/** Maximum right edge coordinate across all the parcels in a controller */
		protected var _parcelRight:Number;
		/** Maximum bottom edge coordinate across all the parcels in a controller */
		protected var _parcelBottom:Number;

		/** owning textFlow of current compose */
		protected var _textFlow:TextFlow;
		private var _releaseLineCreationData:Boolean;
		/** flowComposer of current compose */
		protected var _flowComposer:StandardFlowComposer;
		/** rootElement of current compose */
		protected var _rootElement:ContainerFormattedElement;
		/** position to stop composing at */
		protected var _stopComposePos:int;
		
		// scratch line slugs
		static protected var _candidateLineSlug:Rectangle = new Rectangle();
		static protected var _lineSlug:Rectangle = new Rectangle();
		
		// scratch array for holding lines awaiting alignment
		static private var _alignLines:Array;
		
		/** Parcel we are composing - used for keeping track of when it changes b/c parcelList.parcel may have advanced */
 		protected var _curParcel:Parcel;
 		
 		/** Start position of _curParcel */
 		protected var _curParcelStart:int;
 		
		/** Constructor. */
		public function  BaseCompose()
		{	

		}
		
		protected function createParcelList():IParcelList
		{ return null; }
		protected function releaseParcelList(list:IParcelList):void
		{ }
		
		/** Initialize for a composition that will compose up through the controllerEndIndex, or all the way to the end of the flow
		 * @param composer
		 * @param composeToPosition 	-1 means not specified.  0 means request to compose nothing, >0 specifies a position to force compose to
		 * @param controllerEndIndex	index of the last controller to compose for, or -1 to compose through all controllers
		 */
		protected function  initializeForComposer(composer:IFlowComposer, composeToPosition:int, controllerEndIndex:int):void
		{
			_parcelList = createParcelList();
			_parcelList.notifyOnParcelChange = parcelHasChanged;
			
			_spaceCarried = 0;
			// TODO: just use the rootElement for table cells
			_blockProgression = composer.rootElement.computedFormat.blockProgression;
			// for a non-specified compose position the ParcelList handles the bail out - just set to textLength
			_stopComposePos = composeToPosition >= 0 ? Math.min(_textFlow.textLength,composeToPosition) : _textFlow.textLength;
			
			// this chains through the list - tell it if a "care about" comopseToPosition was specified
			_parcelList.beginCompose(composer, controllerEndIndex, composeToPosition > 0);	
			
			_contentLogicalExtent = 0;
		}
		
		/*
		 * Compose an inline-block element, used for tables or other inline-blocks. The
		 * element has a container associated with it, and the container is going to be placed
		 * after the current paragraph if it fits in the text container.
		 * 
		 * @param composeFrame	the text container we're composing into
		 */
		protected function composeFloat(elem:ContainerFormattedElement,composeFrame:ContainerController):void
		{
			// Should get handled in derived class
			CONFIG::debug { assert(false, "Floats are not supported in ComposeState"); }
		}
		
		/** Called when we are about to compose a line. Handler for derived classes to override default behavior. */
		protected function startLine():void
		{
			// does nothing
		}
		
		/** Called when we are finished composing a line. Handler for derived classes to override default behavior.  */
		protected function endLine():void
		{
			// does nothing
		}		
		
		private function composeBlockElement(elem:FlowGroupElement,absStart:int):Boolean
		{	
			// Compose all the children, until all the containers are filled, or if we're on the last container, we've hit the stop compose text index 
			for (var idx:int = 0; idx < elem.numChildren && (absStart <= _stopComposePos || ! parcelList.atLast()); idx++)
			{
				var child:FlowElement = elem.getChildAt(idx);
				var para:ParagraphElement = child as ParagraphElement;
				if (para)
				{
					var rslt:Boolean = composeParagraphElement(para,absStart);
					// we need to flush each TextBlock - this saves a lot of memory at the cost of performance during editing	
					// note that this is a nop on older players.  only newer players implement flush	
					if (releaseLineCreationData)
						para.releaseLineCreationData();
					if (!rslt)
						return false;	// done
				}
				else if (child.display == FlowElementDisplayType.FLOAT)
				{
					composeFloat(ContainerFormattedElement(child),_parcelList.controller);
					if (_parcelList.atEnd())
						return false;

 					CONFIG::debug { assert(child.getAbsoluteStart() + child.textLength - _parcelList.controller.absoluteStart >= 0, "frame has negative composition"); }
				}
				else 
				{
					if (!composeBlockElement(FlowGroupElement(child),absStart))
						return false;
				}
				absStart += child.textLength;
			}
			return true;
		}
		
		/**
		 * Compose the flow into the text container. Starts at the root element,
		 * and composes elements until either there are no more elements, or the
		 * text container is full. It will compose only the lines which are
		 * marked invalid, so that existing lines that are unchanged are not
		 * recomposed.
		 */
		public function composeTextFlow(textFlow:TextFlow, composeToPosition:int, controllerEndIndex:int):int
		{
			_textFlow = textFlow;
			_releaseLineCreationData = textFlow.configuration.releaseLineCreationData && ParagraphElement.textBlockHasReleaseLineCreationData;
			
			initializeForComposer(textFlow.flowComposer, composeToPosition, controllerEndIndex);

			_flowComposer = _textFlow.flowComposer as StandardFlowComposer;
			_rootElement = textFlow;
			_curElementOffset = 0;
			_curElement = _rootElement.getFirstLeaf();	

            _curElementStart = 0;		// current position in the text (start of current line)
            
            _curParcel = null;
            resetControllerBounds();

				
            parcelHasChanged(_parcelList.currentParcel);		// force start of composition acccounting initialization
				
			composeInternal(_rootElement,0);
			
			for (;;)
			{
				if (parcelList.atEnd())
				{
					parcelHasChanged(null);		// force end of composition accounting for the parcel
					break;
				}				
				parcelList.next();
			}
			
			releaseParcelList(_parcelList);
			
			return _curElementStart + _curElementOffset;		// Return last composed position
		}
		
		private function resetControllerBounds():void
		{
            _controllerLeft = TextLine.MAX_LINE_WIDTH;
			_controllerTop = TextLine.MAX_LINE_WIDTH;
			_controllerRight = -TextLine.MAX_LINE_WIDTH;
			_controllerBottom = -TextLine.MAX_LINE_WIDTH;
		}
				
		/** Release line creation data during this compose */
		protected function get releaseLineCreationData():Boolean
		{ return _releaseLineCreationData; }
		
		// Create new lines through composition. lines, wrap, etc.
		protected function composeInternal(composeRoot:FlowGroupElement,absStart:int):void
		{
			composeBlockElement(composeRoot,absStart);
		}
	
		protected function composeParagraphElement(elem:ParagraphElement,absStart:int):Boolean
		{
			var curLine:TextFlowLine;
			
			_curParaElement  = elem;
			_curParaStart    = absStart;
			CONFIG::debug { assert(_curParaStart == elem.getAbsoluteStart(),"composeParagraphElement: bad start"); }
			_curParaFormat = elem.computedFormat;
			
			_curElement 	 = elem.getFirstLeaf();
			_curElementStart = _curParaStart;
			
			// loop creating lines
			for (;;)
			{
				if (_parcelList.atEnd())
					return false;

				// Allow derived classes to do processing here
				startLine();
				
				// Get the next line
				curLine = composeNextLine();
				if (curLine ==  null)
					return false;

				var alignData:AlignData = calculateTextAlign(curLine, curLine.getTextLine());

				/* {
					for (var idx:int = 0; idx < curLine.textLine.atomCount; idx++)
					{
						trace(idx.toString()+": beginIndex: " + curLine.textLine.getAtomTextBlockBeginIndex(idx)+ " bidiLevel: "+ curLine.textLine.getAtomBidiLevel(idx) + " bounds: " + curLine.textLine.getAtomBounds(idx));
					}
				} */
				
				if ((curLine.spaceBefore != 0 || _spaceCarried != 0) && !_parcelList.isColumnStart())
					_parcelList.addTotalDepth(Math.max(curLine.spaceBefore, _spaceCarried));
				_spaceCarried = 0;
				_parcelList.addTotalDepth(curLine.height);
				_curElementOffset += curLine.textLength;
					// textLength is the first character in the next line
  
 				var textLine:TextLine = curLine.getTextLine();

				var lineWidth:Number; 
				if (_parcelList.explicitLineBreaks)
				{
					var isRTL:Boolean = _curParaElement.computedFormat.direction == Direction.RTL;
					textLine = curLine.getTextLine(true);
					var lastAtom:int = textLine.atomCount - 1;
					// If we're at the end of the paragraph, don't count the terminator
					var endOfParagraph:Boolean = _curElementStart+_curElementOffset == _curParaStart + _curParaElement.textLength;
					if (endOfParagraph && !isRTL)
						--lastAtom;	// can go negative if just the terminator.  in that case use left/top of atom zero
					var bounds:Rectangle = textLine.getAtomBounds(lastAtom >= 0 ? lastAtom : 0);	// get rightmost atom bounds
					lineWidth = (_blockProgression == BlockProgression.TB) 
						? (lastAtom >= 0 ? bounds.right : bounds.left)
						: (lastAtom >= 0 ? bounds.bottom : bounds.top);
					if (isRTL)	// in right to left, get leftmost atom bounds, that has trailing space
					{
						// in RTL strip the width of the paragraph terminator from the front
						bounds = textLine.getAtomBounds(lastAtom != 0 && endOfParagraph ? 1 : 0);						
						lineWidth -= (_blockProgression == BlockProgression.TB) ? bounds.left : bounds.top;
					}
					textLine.flushAtomData();
				}
				else
					lineWidth = textLine.textWidth;
				
				var rightSidePadding:Number =  _curParaFormat.direction == Direction.LTR ? _curParaFormat.paragraphEndIndent : _curParaFormat.paragraphStartIndent;
				var textIndent:Number = 0;
				var rightSideIndent:Number = 0;
				var leftSideIndent:Number = 0;
				if (_curParaFormat.direction == Direction.RTL && (curLine.location & TextFlowLineLocation.FIRST))
				{
					// the textIndent isn't applied on left aligned paragraphs in measured RTL mode
					// need to be careful because leftaligned paragraphs need to be exactly right coming out of this routine
					if (alignData && (_blockProgression == BlockProgression.TB && !curLine.controller.measureWidth || _blockProgression == BlockProgression.RL && !curLine.controller.measureHeight))
						rightSideIndent = _curParaFormat.textIndent;
				}
				var leftSidePadding:Number =  _curParaFormat.direction == Direction.LTR ? _curParaFormat.paragraphStartIndent : _curParaFormat.paragraphEndIndent;
				if (_curParaFormat.direction == Direction.LTR && (curLine.location & TextFlowLineLocation.FIRST))
				{
					// recording leftSideIndent is here because there is an extra alignment step for non-left aligned paragraphs
					leftSideIndent = _curParaFormat.textIndent;
				}					

				if (alignData)
				{
					alignData.rightSidePadding = rightSidePadding;
					alignData.leftSidePadding  = leftSidePadding;
					alignData.lineWidth = lineWidth;
					alignData.rightSideIndent = rightSideIndent;
					alignData.leftSideIndent = leftSideIndent;
					
					// trace("AlignData",alignData.leftSidePadding,alignData.rightSidePadding,alignData.lineWidth,alignData.leftSideIndent,alignData.rightSideIndent);
				}
				
				// extent from the left margin
				var lineExtent:Number = lineWidth + leftSidePadding+leftSideIndent + rightSidePadding+rightSideIndent;
				_contentLogicalExtent = Math.max(_contentLogicalExtent, lineExtent);
				
				CONFIG::debug { assert(_parcelList.controller.textLength >= 0, "frame has negative composition"); }
				
				if (_parcelList.atEnd())
					return false;
					
				endLine();
				
				// advance to the next element, using the rootElement of the container as a limitNode
				// to prevent going past the content bound to this container
				if (_curElementOffset >= _curElement.textLength)
				{
					// We may have composed ahead over several spans; skip until we match up
					// Loop until we use catch up to where the line we just composed ended (pos).
					// Stop if we run out of elements. Skip empty inline elements, and skip floats
					// that came at the start of the line before any text -- they've already been 
					// processed.
					do{
						_curElementOffset -= _curElement.textLength;
						_curElementStart  += _curElement.textLength;
						if (_curElementStart == _curParaStart+_curParaElement.textLength)
						{
							_curElement = null;
							break;
						}
						_curElement = _curElement.getNextLeaf();
						CONFIG::debug { assert(_curElement && _curElement.getParagraph() == _curParaElement,"composeParagraphElement: bad textLength in TextLine"); }
					} while (_curElementOffset >= _curElement.textLength || _curElement.textLength == 0 );
				}
				
				_spaceCarried = curLine.spaceAfter;

					
				if (_curElement == null)
					break;
			}
			return true;
		}

		protected function composeNextLine():TextFlowLine
		{
			throw new Error("composeNextLine requires override");		
			return null;
		}
		
		// fills in _lineSlug
 		protected function fitLineToParcel(curLine:TextFlowLine):TextFlowLine
 		{
 			// Try to place the line in the current parcel.
			// get a zero height parcel. place the line there and then test if it still fits.
			// if it doesn't place it in the new result parcel
			// still need to investigate because the height used on the 2nd getLineSlug call may be too big.
			for (;;)
			{
				if (_parcelList.getLineSlug(_candidateLineSlug,0))
					break;
				_parcelList.next();
				if (_parcelList.atEnd())
					return null;
				_spaceCarried = 0;
			}
			
			curLine.setController(_parcelList.controller,_parcelList.columnIndex);
			
			// If we are at the last parcel, we let text be clipped if that's specified in the configuration. At the point where no part of text can be accommodated, we go overset.
			// If we are not at the last parcel, we let text flow to the next parcel instead of getting clipped.
			var spaceBefore:Number = Math.max(curLine.spaceBefore, _spaceCarried);
			for (;;)
			{
				finishComposeLine(curLine, _candidateLineSlug);	
				if (_parcelList.getLineSlug(_lineSlug, spaceBefore + (_parcelList.atLast() && _textFlow.configuration.overflowPolicy != OverflowPolicy.FIT_DESCENDERS ? curLine.height-curLine.ascent : curLine.height+curLine.descent)))
				{
					CONFIG::debug { assert(_parcelList.getComposeXCoord(_candidateLineSlug) == _parcelList.getComposeXCoord(_lineSlug) && _parcelList.getComposeYCoord(_candidateLineSlug) == _parcelList.getComposeYCoord(_lineSlug),"fitLineToParcel: slug mismatch"); }
					break;
				}
				spaceBefore = curLine.spaceBefore;
				for (;;)
				{
					_parcelList.next();
					if (_parcelList.atEnd())
						return null;
					if (_parcelList.getLineSlug(_candidateLineSlug,0))
						break;
				}
				curLine.setController(_parcelList.controller,_parcelList.columnIndex);
			}						
			
			// check to see if we got a good line
			return (_parcelList.getComposeWidth(_lineSlug) == curLine.outerTargetWidth) ? curLine : null;
		}
				
        protected function finishComposeLine(curLine:TextFlowLine, lineSlug:Rectangle):void
        {      	
       		var curTextLine:TextLine = curLine.getTextLine();
        	var lineHeight:Number = 0;
        	var lineX:Number = _parcelList.getComposeXCoord(lineSlug);
        	var lineY:Number = _parcelList.getComposeYCoord(lineSlug);
        	
        	if (_curParaFormat.direction == Direction.LTR)
        	{
        		if (_blockProgression != BlockProgression.RL)
        			lineX += curLine.lineOffset;
        		else
        			lineY += curLine.lineOffset;
        	} 
        	else 
        	{

	        	if (_blockProgression != BlockProgression.RL)
	       			lineX += curLine.outerTargetWidth-curLine.lineOffset-curLine.targetWidth;
	       		else
	       			lineY += curLine.outerTargetWidth-curLine.lineOffset-curLine.targetWidth;
	       		if (curLine.outerTargetWidth == TextLine.MAX_LINE_WIDTH)	// doing measurement ignore 
        		{
        			if (curLine.location&TextFlowLineLocation.FIRST)
        			{
        				if (_blockProgression != BlockProgression.RL)
        					lineX += curLine.paragraph.computedFormat.textIndent;
        				else
        					lineY += curLine.paragraph.computedFormat.textIndent;
        			}
        		}
        	}

			var containerAttrs:ITextLayoutFormat = _parcelList.controller.computedFormat;		
			var baselineType:Object = BaselineOffset.LINE_HEIGHT;
			if (_parcelList.isColumnStart())
			{
				// If we're at the top of the column, we need to check the container properties to see
				// what the firstBaselineOffset should be. This tells us how to treat the line.
				// However, when vertical alignment is center or bottom, ignore the firstBaselineOffset setting
				// and treat them as the BaselineOffset.AUTO case
				if (containerAttrs.firstBaselineOffset != BaselineOffset.AUTO && containerAttrs.verticalAlign != VerticalAlign.BOTTOM && containerAttrs.verticalAlign != VerticalAlign.MIDDLE) 
				{
					baselineType = containerAttrs.firstBaselineOffset;
					// The first line's offset is specified relative firstBaselineOffsetBasis, which used to be, but no longer is, a container-level property
					// Now it is implicitly deduced based on the container-level locale in the following manner: 
					// IDEOGRAPHIC_BOTTOM for ja and zh locales (this is the same locale set for which the default LeadingModel is IDEOGRAPHIC_TOP_DOWN)
					// ROMAN for all other locales
					var firstBaselineOffsetBasis:String = LocaleUtil.leadingModel(containerAttrs.locale) == LeadingModel.IDEOGRAPHIC_TOP_DOWN ?  flash.text.engine.TextBaseline.IDEOGRAPHIC_BOTTOM : flash.text.engine.TextBaseline.ROMAN;
					lineHeight -= curTextLine.getBaselinePosition(firstBaselineOffsetBasis);		
				}
				else
				{
					// The AUTO case requires aligning line top to container top inset. This efect can be achieved by using firstBaselineOffset=ASCENT
					// and firstBaselineOffsetBasis=ROMAN 
					baselineType = BaselineOffset.ASCENT;
					lineHeight -= curTextLine.getBaselinePosition(flash.text.engine.TextBaseline.ROMAN);
				}

			}
			else
			{
				// handle space before by adjusting y position of line
				if (curLine.spaceBefore != 0 || _spaceCarried != 0)
				{
					var spaceAdjust:Number = Math.max(curLine.spaceBefore, _spaceCarried);
					if (_blockProgression != BlockProgression.RL)
						lineY += spaceAdjust;
					else
						lineX -= spaceAdjust;
				}
			}
			
			_curLineLeading = curLine.getLineLeading(_blockProgression,_curElement,_curElementStart);
			_curLineLeadingModel = _curParaElement.getEffectiveLeadingModel();
			
			if (baselineType == BaselineOffset.ASCENT)
			{
				CONFIG::debug { assert(_curElement == _textFlow.findLeaf(curLine.absoluteStart),"Bad _curElement"); }
				CONFIG::debug { assert(_curElementStart == _textFlow.findLeaf(curLine.absoluteStart).getAbsoluteStart(), "Bad _curElementStart"); }
				lineHeight += curLine.getLineTypographicAscent(_curElement,_curElementStart);
			}
			else 
			{
				if (baselineType == BaselineOffset.LINE_HEIGHT)
				{
					if (_curLineLeadingModel == LeadingModel.ASCENT_DESCENT_UP)
					{
						lineHeight += _lastLineDescent + curTextLine.ascent + _curLineLeading;
					}
					else
					{
						// Leading direction is irrelevant for the first line. Treat it as (UP, UP)
						// TODO-9/3/2008-It may be better to handle Middle/Last lines separately because we know that the previous line also belongs in the same para 
						var curLeadingDirectionUp:Boolean = _parcelList.isColumnStart() ? true : ParagraphElement.useUpLeadingDirection(_curLineLeadingModel);
				
						var prevLeadingDirectionUp:Boolean = _parcelList.isColumnStart() || _lastLineLeadingModel == "" ? true : 
							ParagraphElement.useUpLeadingDirection(_lastLineLeadingModel);
						
						var prevLineFirstElement:FlowLeafElement;
						
						if (curLeadingDirectionUp)
						{	
							/*
							if (prevLeadingDirectionUp)
							{
								// Same leading directions; use current line's leading setting. 
								lineHeight += curLine.getLineLeading(FlowLeafElement(_curElement),_curElementStart) 											
							}
							else
							{
								// Select the larger of: 
								
								// a) Leading adjustment applied if previous para had also used leading direction UP
								var leadingUP:Number = curLine.getLineLeading(FlowLeafElement(_curElement),_curElementStart);
								
								// b) Leading adjustment applied if current para had also used leading direction DOWN
								prevLineFirstElement = _textFlow.findLeaf(prevLine.absoluteStart);
								var leadingDOWN:Number = prevLine.getLineLeading(prevLineFirstElement, prevLineFirstElement.getAbsoluteStart());
								
								lineHeight += leadingUP > leadingDOWN ? leadingUP : leadingDOWN;
							}*/		
							
							//TODO-9/12/2008-The above behavior is the InDesign behavior but raises some questions about selection shapes.
							//Should selection code associate leading with the influencing line? That would be weird. InDesign only
							//supports alternate leading directions in the J feature set, where leading is never included in selection,
							//so this question does not arise. We take the unambiguous route: ignore leading DOWN at the end of a para
							lineHeight += _curLineLeading;
						}
						else
						{
							if (!prevLeadingDirectionUp)
							{
								// Same leading directions; use previous line's leading setting.
								lineHeight += _lastLineLeading;
							}
							else
							{
								// Make NO leading adjustments. Set lines solid.
								lineHeight += _lastLineDescent + curTextLine.ascent;
							}
						}	
					}
				}
				else
					lineHeight += Number(baselineType);		// fixed offset
			}
			
			// adjust x position for line height in vertical text
			if (_blockProgression == BlockProgression.RL)
				lineX -= lineHeight;
			else
				lineY += lineHeight - curTextLine.ascent;	// adjust the line's baseline so the leading goes at the top of the slug
				
			curLine.setXYAndHeight(lineX,lineY,lineHeight);

			curLine.createAdornments(_blockProgression,_curElement,_curElementStart);
 		}
 		
 		// Calculate the text alignment of the current line we're composing. If alignment is required, the adjustment will be made in
 		// applyTextAlign, after we've calculated the width of the parcel (it may be based on measurement).
 		// TODO: optimization possibility - do the alignment here when not doing measurement
 		private function calculateTextAlign(curLine:TextFlowLine, curTextLine:TextLine):AlignData
 		{
       		// Adjust the coordinates of the line for center/right.  The line is always left aligned.  TextBlock handles justified cases
        	// If we're on the last line of a justified paragraph, use the textAlignLast value 
			var delta:Number = 0;
			var textAlignment:String = _curParaFormat.textAlign;
			if (textAlignment == TextAlign.JUSTIFY && _curParaFormat.textAlignLast != null)
			{
				var location:int = curLine.location;
				if (location == TextFlowLineLocation.LAST || location == TextFlowLineLocation.ONLY)
					textAlignment = _curParaFormat.textAlignLast;
			}
			switch(textAlignment)
			{
				case TextAlign.START:
					textAlignment = (_curParaFormat.direction == Direction.LTR) ? TextAlign.LEFT : TextAlign.RIGHT;
					break;
				case TextAlign.END:
					textAlignment = (_curParaFormat.direction == Direction.LTR) ? TextAlign.RIGHT : TextAlign.LEFT;
					break; 
			}


			if (textAlignment == TextAlign.CENTER || textAlignment == TextAlign.RIGHT)
			{
				var alignData:AlignData = new AlignData();
				alignData.textLine = curTextLine;
				alignData.center = (textAlignment == TextAlign.CENTER);
				if (!_alignLines)
					_alignLines = [];
				_alignLines.push(alignData);
				return alignData;
			}
			return null;
 		}
 		
 		private function applyTextAlign(effectiveParcelWidth:Number):void
 		{
 			var textLine:TextLine;
 			var line:TextFlowLine;
 			var i:int;
 			var alignData:AlignData;
 			
 			var coord:Number;
 			var delta:Number;
 			var adjustedLogicalRight:Number;
 			var extraSpace:Number;
 			var leftSideGap:Number;
 			var rightSideGap:Number;
 			
 			if (_blockProgression == BlockProgression.TB)
 			{
 				for each (alignData in _alignLines) 
 				{
 					textLine = alignData.textLine;
 					
 					rightSideGap = alignData.rightSidePadding;
 					leftSideGap = alignData.leftSidePadding;
					leftSideGap += alignData.leftSideIndent;
					rightSideGap += alignData.rightSideIndent;
 					
 		 			line = textLine.userData as TextFlowLine;
 					extraSpace = effectiveParcelWidth - leftSideGap - rightSideGap -  textLine.textWidth;
					delta = leftSideGap + (alignData.center ? extraSpace / 2 : extraSpace);
					coord = _curParcel.left + delta;
		 			if (line)
		 				line.x = coord;
		 			textLine.x = coord;
		 			
		 			adjustedLogicalRight = alignData.lineWidth + coord + Math.max(rightSideGap, 0);
		 			_parcelRight = Math.max(adjustedLogicalRight , _parcelRight);
 				}
 			}
			else
			{
 				for each (alignData in _alignLines) 
 				{
					textLine = alignData.textLine;
 					
 					rightSideGap = alignData.rightSidePadding;
 					leftSideGap = alignData.leftSidePadding;
					leftSideGap += alignData.leftSideIndent;
					rightSideGap += alignData.rightSideIndent;
 					
 		 			line = textLine.userData as TextFlowLine;
 					extraSpace = effectiveParcelWidth - leftSideGap - rightSideGap -  textLine.textWidth;
					delta = leftSideGap + (alignData.center ? extraSpace / 2 : extraSpace);
					coord = _curParcel.top + delta;
		 			if (line)
		 				line.y = coord;
		 			textLine.y = coord;
 								
		 			adjustedLogicalRight = alignData.lineWidth + coord + Math.max(rightSideGap, 0);
		 			_parcelBottom = Math.max(adjustedLogicalRight,_parcelBottom);
				}
			}
			_alignLines.length = 0;
		}
		
		protected function commitLastLineState(curLine:TextFlowLine):void
		{
			// Remember leading-related state that may be used for laying out the next line
			_lastLineDescent = curLine.descent;
			_lastLineLeading = _curLineLeading;
			_lastLineLeadingModel = _curLineLeadingModel;
		}
 
 		protected function doVerticalAlignment(canVerticalAlign:Boolean,nextParcel:Parcel):Boolean
 		{
  			// stub for required override
 			CONFIG::debug { assert(false, "override in derived class"); }
 			return false;
 		}
 		
 		protected function finalParcelAdjustment(controller:ContainerController):void
 		{
  			// stub for required override
			CONFIG::debug { assert(false, "finalParcelAdjustment missing override in derived class"); }
 		}
 		
 		protected function finishParcel(controller:ContainerController,nextParcel:Parcel):Boolean
 		{
 			if (_curParcelStart == _curElementStart+_curElementOffset)		// empty parcel -- nothing composed into it
 			{
 				CONFIG::debug { assert(_contentLogicalExtent == 0,"bad contentlogicalextent on empty container"); }
 				return false;
 			}
 				
			// We're only going to align the lines in measurement mode if there's only one parcel
 			var doTextAlign:Boolean = (_alignLines && _alignLines.length > 0); 
 			 			
 			// Figure out the contents bounds information for the parcel we just finished composing
 			
			// Content logical height is parcel depth, plus descenders of last line
			var totalDepth:Number = _parcelList.totalDepth;
			if (_textFlow.configuration.overflowPolicy == OverflowPolicy.FIT_DESCENDERS && !isNaN(_lastLineDescent))
				totalDepth += _lastLineDescent;
			
	 		// Initialize the parcel bounds
	 		// note we can later optimize away the adjustements
			if (_blockProgression == BlockProgression.TB)
			{
				_parcelLeft = _curParcel.left;
				_parcelTop = _curParcel.top;
				_parcelRight = _contentLogicalExtent+_curParcel.left;
				_parcelBottom = totalDepth+_curParcel.top;
			}
			else
			{
	 			// Push the values up to the controller running min/max, if they are bigger
				_parcelLeft = _curParcel.right-totalDepth;
				_parcelTop = _curParcel.top;
				_parcelRight = _curParcel.right;
				_parcelBottom = _contentLogicalExtent+_curParcel.top;
			}			
 			if (doTextAlign)
 			{
 				var effectiveParcelWidth:Number;
 				if (_blockProgression == BlockProgression.TB)
 					effectiveParcelWidth = controller.measureWidth ? _contentLogicalExtent : _curParcel.width;
 				else
 					effectiveParcelWidth = controller.measureHeight ? _contentLogicalExtent : _curParcel.height;
 				applyTextAlign(effectiveParcelWidth);
 			}

 			// If we're measuring, then don't do vertical alignment
 			var canVerticalAlign:Boolean = false;
 			if (_blockProgression == BlockProgression.TB)
 			{
 				if (!controller.measureHeight && (!_curParcel.fitAny || _curElementStart + _curElementOffset >= _textFlow.textLength))
 					canVerticalAlign = true;
 			}
 			else
 			{
 				if (!controller.measureWidth && (!_curParcel.fitAny || _curElementStart + _curElementOffset >= _textFlow.textLength))
 					canVerticalAlign = true;
 			}
 			
 			// need to always call this function because internal variables may need resetting
			if (doVerticalAlignment(canVerticalAlign,nextParcel))
				doTextAlign = true;
 			// This last adjustment is for two issues
 			// 1) inline graphics that extend above the top (any ILGS I expect)
 			// 2) negative first line indents (stil a worry here?)
 			// If neither of these are present it can be skipped - TODO optimization
 			// trace("BEF finalParcelAdjustment",_parcelLeft,_parcelRight,_parcelTop,_parcelBottom);
 			finalParcelAdjustment(controller);
 			// trace("AFT finalParcelAdjustment",_parcelLeft,_parcelRight,_parcelTop,_parcelBottom);
 			_contentLogicalExtent = 0;

 			return true;
 		}
 		
 		/** apply vj and adjust the parcel bounds */
 		protected function applyVerticalAlignmentToColumn(controller:ContainerController,vjType:String,lines:Array,beginIndex:int,numLines:int):void
 		{
 			var firstLine:IVerticalJustificationLine = lines[beginIndex];
			var lastLine:IVerticalJustificationLine = lines[beginIndex+numLines-1]
			var firstLineCoord:Number;
			var lastLineCoord:Number
			if (_blockProgression == BlockProgression.TB)
			{
				firstLineCoord = firstLine.y;
				lastLineCoord  = lastLine.y;
			}
			else
			{
				firstLineCoord = firstLine.x;
				lastLineCoord = lastLine.x;
			}
					
			VerticalJustifier.applyVerticalAlignmentToColumn(controller,vjType,lines,beginIndex,numLines);
			
			if (_blockProgression == BlockProgression.TB)
			{
				_parcelTop += firstLine.y-firstLineCoord;
				_parcelBottom += lastLine.y-lastLineCoord;
			}
			else
			{
				_parcelRight += firstLine.x-firstLineCoord;
				_parcelLeft += lastLine.x-lastLineCoord;
			}
		}
		
 		private function finishController(controller:ContainerController):void
 		{
 			var controllerTextLength:int = _curElementStart + _curElementOffset - controller.absoluteStart;
 			
 			if (controllerTextLength != 0)
 			{
	 			// Leave room for the padding. If the content overlaps the padding, don't count the padding twice.
	  			var paddingLeft:Number = controller.effectivePaddingLeft;
	  			var paddingTop:Number = controller.effectivePaddingTop;
	  			var paddingRight:Number = controller.effectivePaddingRight;
	  			var paddingBottom:Number = controller.effectivePaddingBottom;
	  			if (_blockProgression == BlockProgression.TB)
	  			{
		 			if (_controllerLeft > 0)
		 			{
		 				if (_controllerLeft < paddingLeft)
		 					_controllerLeft = 0;
		 				else 
		 					_controllerLeft -= paddingLeft;
		 			}
	
		 			if (_controllerTop > 0)
		 			{
		 				if (_controllerTop < paddingTop)
		 					_controllerTop = 0;
		 				else 
		 					_controllerTop -= paddingTop;
		 			}
	 			
		 			if (isNaN(controller.compositionWidth))
		 				_controllerRight += paddingRight;		 				
		 			else if (_controllerRight < controller.compositionWidth)
		 			{
		 				if (_controllerRight > controller.compositionWidth - paddingRight)
		 					_controllerRight = controller.compositionWidth;
		 				else 
		 					_controllerRight += paddingRight;
		 			}
					_controllerBottom += paddingBottom;	
	  			}
	  			else
	  			{
	  				_controllerLeft -= paddingLeft;
	  				if (_controllerTop > 0)
	  				{
	  					if (_controllerTop < paddingTop)
		 					_controllerTop = 0;
		 				else 
		 					_controllerTop -= paddingTop;
	  				}
	  				if (_controllerRight < 0)
	  				{
	  					if (_controllerRight > -paddingRight)
	  					{
	  						_controllerRight = 0;
	  					}
	  					else
	  						_controllerRight += paddingRight;
	  				}
	  				if (isNaN(controller.compositionHeight))
	  					_controllerBottom += paddingBottom;
	  				else if (_controllerBottom < controller.compositionHeight)
	  				{
	  					if (_controllerBottom > controller.compositionWidth - paddingBottom)
		 					_controllerBottom = controller.compositionWidth;
		 				else 
		 					_controllerBottom += paddingBottom;
	  				}
	  			}
 				controller.setContentBounds(_controllerLeft, _controllerTop, _controllerRight-_controllerLeft, _controllerBottom-_controllerTop);
			}
			else
				controller.setContentBounds(0,0,0,0);

 			controller.setTextLength(controllerTextLength);
 		}
 		
 		private function clearControllers(oldController:ContainerController, newController:ContainerController):void
 		{
  			// any controller between oldController and up to and including newController gets cleared
 			var firstToClear:int = oldController ? _flowComposer.getControllerIndex(oldController)+1 : 0;
 			var lastToClear:int  = newController ? _flowComposer.getControllerIndex(newController) : _flowComposer.numControllers-1;
 			while (firstToClear <= lastToClear)
 			{
 				var controllerToClear:ContainerController = ContainerController(_flowComposer.getControllerAt(firstToClear));
				controllerToClear.setContentBounds(0, 0, 0, 0);
				controllerToClear.setTextLength(0);
				firstToClear++;
 			}
 		}
 		
  		/** This is called when the parcel has changed 
  		 * @param oldParcel - the parcel we had before (you can get the new parcel from the parcel list)
  		 */
 		protected function parcelHasChanged(newParcel:Parcel):void
 		{
 			var oldController:ContainerController = _curParcel ? ContainerController(_curParcel.controller) : null;
 			var newController:ContainerController = newParcel  ? ContainerController(newParcel.controller)  : null;
 			
 			/* if (newParcel)
	 			trace("parcelHasChanged newParcel: ",newParcel.clone().toString()); */

 			if (_curParcel != null)
 			{
 				if (finishParcel(oldController,newParcel))
 				{
 					if (_parcelLeft < _controllerLeft)
 						_controllerLeft = _parcelLeft;
 					if (_parcelRight > _controllerRight)
 						_controllerRight = _parcelRight;
 					if (_parcelTop < _controllerTop)
 						_controllerTop = _parcelTop;
 					if (_parcelBottom > _controllerBottom)
 						_controllerBottom = _parcelBottom;
  				}
 			}
 				
			// update parcel data			
 			if (oldController != newController)		// we're going on to the next controller in the chain
 			{
	 			if (oldController)
	 				finishController(oldController);
 			
				resetControllerBounds();

				if (_flowComposer.numControllers > 1)
					clearControllers(oldController, newController);
 				// Parcel list will set totalDepth to newController's paddingTop
 			}
 			_curParcel = newParcel;
 			_curParcelStart = _curElementStart;
		}

	}
}

import flash.text.engine.TextLine;

class AlignData 
{
	public var textLine:TextLine;
	public var lineWidth:Number;
	public var leftSidePadding:Number;
	public var rightSidePadding:Number;
	public var center:Boolean;
	public var leftSideIndent:Number;
	public var rightSideIndent:Number;
}
