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
package flashx.textLayout.elements
{
	import flash.geom.Rectangle;
	import flash.text.engine.TextLine;
	import flash.text.engine.TextRotation;
	
	import flashx.textLayout.debug.assert;
	import flashx.textLayout.formats.BlockProgression;
	import flashx.textLayout.tlf_internal;
	
	use namespace tlf_internal;
	
	/** 
	 * The TCYElement (Tatechuuyoko - ta-tae-chu-yo-ko) class is a subclass of SubParagraphGroupElement that causes
	 * text to draw horizontally within a vertical line.  Traditionally, it is used to make small
	 * blocks of non-Japanese text or numbers, such as dates, more readable.  TCY can be applied to 
	 * horizontal text, but has no effect on drawing style unless and until it is turned vertically.
	 * 
	 * TCY blocks which contain no text will be removed from the text flow during the normalization process.
	 * <p>
	 * In the example below, the image on the right shows TCY applied to the number 57, while the
	 * image on the left has no TCY formatting.</p>
	 * <p><img src="../../../images/textLayout_TCYElement.png" alt="TCYElement" border="0"/>
	 * </p>
	 *
	 * @playerversion Flash 10
	 * @playerversion AIR 1.5
	 * @langversion 3.0
	 *
	 * @see TextFlow
	 * @see ParagraphElement
	 * @see SpanElement
	 */
	public final class TCYElement extends SubParagraphGroupElement
	{
		/** Constructor - creates a new TCYElement instance.
		 *
		 * @playerversion Flash 10
	 	 * @playerversion AIR 1.5
	 	 * @langversion 3.0
	 	 *
	 	 */
		public function TCYElement()
		{
			super();
		}
		
		/** @private */
		override tlf_internal function createContentElement():void
		{
			super.createContentElement();
			groupElement.textRotation = TextRotation.ROTATE_270;
		}
		
		/** @private */
		override protected function get abstract():Boolean
		{
			return false;
		}		
		/** @private */
        tlf_internal override function get precedence():uint { return 100; }
        
		/** @private */
		public override function splitAtIndex(childIndex:int):FlowGroupElement
		{
			if (childIndex > numChildren)
				throw RangeError("invalid parameter to FlowGroupElement.split");
					
			var newSibling:TCYElement = shallowCopy() as TCYElement;
			
			if (parent)
			{
				var myidx:int = parent.getChildIndex(this);
				parent.replaceChildren(myidx+1,myidx+1,newSibling);
			}
			
			var elemCount:int = numChildren;
			while (childIndex < elemCount)
			{
				var childToMove:FlowElement = this.getChildAt(childIndex);
				replaceChildren(childIndex,childIndex+1,null);
				elemCount--;
				newSibling.replaceChildren(newSibling.numChildren,newSibling.numChildren,childToMove);
			}
			return newSibling;
		}
		
		/** @private */
		public override function splitAtPosition(relativePosition:int):FlowElement
		{
			if ((relativePosition < 0) || (relativePosition > textLength))
				throw RangeError("invalid parameter to TCYElement.split");
				
			var curElementIdx:int = 0;
			var curFlowElement:FlowElement;
			while (curElementIdx < numChildren)
			{
				curFlowElement = getChildAt(curElementIdx);
				if (curFlowElement.parentRelativeStart == relativePosition)
				{
					//easy case.  Just split on this element
					return splitAtIndex(curElementIdx);
				}
				else if ((relativePosition > curFlowElement.parentRelativeStart) && (relativePosition < curFlowElement.parentRelativeEnd))
				{
					//we found the element and we need to split somewhere in the middle of the FlowElement.
					break;
				} 
				curElementIdx++;
			}
			
			if (curElementIdx == numChildren)
			{
				throw RangeError("invalid parameter to FlowGroupElement.split");
			}
					
			var newSibling:TCYElement = shallowCopy() as TCYElement;
			
			if (curFlowElement.splitAtPosition(relativePosition - curFlowElement.parentRelativeStart) != curFlowElement || relativePosition == curFlowElement.parentRelativeEnd)
				curElementIdx++; //increase by one. It's the new element that we want to move over.
			
			while (curElementIdx < numChildren)
			{
				var childToMove:FlowElement = this.getChildAt(curElementIdx);
				replaceChildren(curElementIdx,curElementIdx+1);
				newSibling.replaceChildren(newSibling.numChildren,newSibling.numChildren,childToMove);
			}
			
			if (parent)
			{
				var myidx:int = parent.getChildIndex(this);
				parent.replaceChildren(myidx+1,myidx+1,newSibling);
			}
				
			return newSibling;
		}
		
		
		/** @private */
		tlf_internal override function mergeToPreviousIfPossible():Boolean
		{	
			if (parent)
			{
				var myidx:int = parent.getChildIndex(this);
				if (myidx != 0)
				{
					var prevEl:TCYElement = parent.getChildAt(myidx - 1) as TCYElement;
					if(prevEl)
					{
						while(this.numChildren > 0)
						{
							var xferEl:FlowElement = this.getChildAt(0);
							replaceChildren(0, 1);
							prevEl.replaceChildren(prevEl.numChildren, prevEl.numChildren, xferEl);
						}
						parent.replaceChildren(myidx, myidx + 1);								
						return true;
					}		
				}
			}
			
			return false;
		}
		
		/** @private */
		tlf_internal override function acceptTextBefore():Boolean 
		{ 
			return false; 
		}
		
		/** @private */
		tlf_internal override function setParentAndRelativeStart(newParent:FlowGroupElement,newStart:int):void
		{
			super.setParentAndRelativeStart(newParent,newStart);
			var contElement:ContainerFormattedElement = getAncestorWithContainer();
			if (groupElement)
				groupElement.textRotation = contElement && contElement.computedFormat.blockProgression == BlockProgression.RL ? TextRotation.ROTATE_270 : TextRotation.ROTATE_0;
		}
		
		/** @private */
		tlf_internal override function formatChanged(notifyModelChanged:Boolean = true):void
		{
			super.formatChanged(notifyModelChanged);
			var contElement:ContainerFormattedElement = getAncestorWithContainer();
			if (groupElement)
				groupElement.textRotation = contElement && contElement.computedFormat.blockProgression == BlockProgression.RL ? TextRotation.ROTATE_270 : TextRotation.ROTATE_0;
		}
		
		/** @private */
		tlf_internal function calculateAdornmentBounds(spg:SubParagraphGroupElement, tLine:TextLine, blockProgression:String, spgRect:Rectangle):void
		{
			var childCount:int = 0;
			while(childCount < spg.numChildren)
			{
				var curChild:FlowElement = spg.getChildAt(childCount) as FlowElement;
				var curFlowLeaf:FlowLeafElement = curChild as FlowLeafElement;
				if(!curFlowLeaf && curChild is SubParagraphGroupElement)
				{
					calculateAdornmentBounds(curChild as SubParagraphGroupElement, tLine, blockProgression, spgRect);
					++childCount;
					continue;
				}
				
				CONFIG::debug{ assert(curFlowLeaf != null, "The TCY contains a non-FlowLeafElement!  Cannot calculate mirror!")};
				var curBounds:Rectangle = null;
				if(!(curFlowLeaf is InlineGraphicElement))
					curBounds = curFlowLeaf.getSpanBoundsOnLine(tLine, blockProgression)[0];
				else
				{
					curBounds = (curFlowLeaf as InlineGraphicElement).graphic.getBounds(tLine);
				}
				
				if(childCount != 0)
				{
					if(curBounds.top < spgRect.top)
						spgRect.top = curBounds.top;
						
					if(curBounds.bottom > spgRect.bottom)
						spgRect.bottom = curBounds.bottom;
					
					if(spgRect.x > curBounds.x)
						spgRect.x = curBounds.x;
				}
				else
				{
					spgRect.top = curBounds.top;
					spgRect.bottom = curBounds.bottom;
					spgRect.x = curBounds.x;
				}
				++childCount;
			}
		}
	}
}