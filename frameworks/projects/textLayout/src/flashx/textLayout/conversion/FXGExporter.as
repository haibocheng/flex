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
package flashx.textLayout.conversion
{
	import flash.text.engine.SpaceJustifier;
	
	import flashx.textLayout.elements.BreakElement;
	import flashx.textLayout.elements.ContainerFormattedElement;
	import flashx.textLayout.elements.DivElement;
	import flashx.textLayout.elements.FlowElement;
	import flashx.textLayout.elements.LinkElement;
	import flashx.textLayout.elements.ParagraphElement;
	import flashx.textLayout.elements.ParagraphFormattedElement;
	import flashx.textLayout.elements.SpanElement;
	import flashx.textLayout.elements.SubParagraphGroupElement;
	import flashx.textLayout.elements.TCYElement;
	import flashx.textLayout.elements.TabElement;
	import flashx.textLayout.elements.TextFlow;
	import flashx.textLayout.formats.FormatValue;
	import flashx.textLayout.formats.ITextLayoutFormat;
	import flashx.textLayout.formats.TextLayoutFormat;
	import flashx.textLayout.formats.WhiteSpaceCollapse;
	import flashx.textLayout.property.Property;
	import flashx.textLayout.tlf_internal;

	use namespace tlf_internal;
	
	[ExcludeClass]
	/** 
	 * @private
	 * Export filter for FXG format. 
	 */
	internal class FXGExporter extends BaseTextLayoutExporter
	{			
		private var _tabSpan:SpanElement;
		
		private var _characterFormatDescription:Object;
		private var _paragraphFormatDescription:Object;
		private var _containerFormatDescription:Object;

		public function FXGExporter()
		{
			_tabSpan = new SpanElement();
			_tabSpan.text = '\t';
			
			var config:ImportExportConfiguration = new ImportExportConfiguration();
 			config.addIEInfo("TextGraphic", TextFlow, null, FXGExporter.exportTextFlow, true);
			config.addIEInfo("br", BreakElement, null, BaseTextLayoutExporter.exportFlowElement, false);
			config.addIEInfo("p", ParagraphElement, null, BaseTextLayoutExporter.exportParagraphFormattedElement, true);
			config.addIEInfo("span", SpanElement, null, BaseTextLayoutExporter.exportSpan, false);
			config.addIEInfo("tab", TabElement, null, FXGExporter.exportTab, false);
			config.addIEInfo("a", LinkElement, null, FXGExporter.exportLinkTCY, false);
			config.addIEInfo("tcy", TCYElement, null, FXGExporter.exportLinkTCY, false);
			config.addIEInfo("div", DivElement, null, FXGExporter.exportDiv, true);
			
			super(new Namespace("mx", "http://ns.adobe.com/fxg/2008"), <Graphic/>, config);

			_characterFormatDescription = FXGImporter._characterFormatDescription;
			_containerFormatDescription = FXGImporter._containerFormatDescription;
			_paragraphFormatDescription = FXGImporter._paragraphFormatDescription;
		}
		
		/** Similar to BaseTextLayoutExporter.exportTextFlow, but adds an intermediate node, <content> in the XML hierarchy
		 * @param exporter	Root object for the export
		 * @param textFlow	Element to export
		 * @return XMLList	XML for the element
		 */
		static public function exportTextFlow(exporter:BaseTextLayoutExporter, textFlow:TextFlow):XMLList
		{
			var output:XMLList = exportContainerFormattedElement(exporter, textFlow);
			
			var content:XML = <content/>;
			content.setNamespace(exporter.flowNS);
			content.setChildren((output[0] as XML).children());
			output[0].setChildren(content);
			
			// FXG will use PRESERVE on output
			output.@[TextLayoutFormat.whiteSpaceCollapseProperty.name] = WhiteSpaceCollapse.PRESERVE;
						
			return output;
		}
		
		/** Functionality for exporting a TabElement. Exports as a span with just a tab character.
		 * @param exporter	Root object for the export
		 * @param tab	Element to export
		 * @return XMLList	XML for the element
		 */
		static public function exportTab(exporter:BaseTextLayoutExporter, tab:TabElement):XMLList
		{
			return exportSpan (exporter, FXGExporter(exporter)._tabSpan);
		}
		
		/** Functionality for exporting a SubParagraphGroupElement. Export its children (but not self), with character formats concatenated.
		 * @param exporter	Root object for the export
		 * @param subparagraphGroupElement	element to export
		 * @return XMLList	XML for the element
		 */
		static public function exportLinkTCY(exporter:BaseTextLayoutExporter, subparagraphGroupElement:SubParagraphGroupElement):XMLList
		{
			var output:XML = <dummy/>; // tag doesn't matter because the subparagraph group element itself is not exported
			
			// output each child
			for(var childIter:int = 0; childIter < subparagraphGroupElement.numChildren; ++childIter)
			{
				var flowChild:FlowElement;
				var originalCharacterFormat:Object;
				
				try
				{
					flowChild = subparagraphGroupElement.getChildAt(childIter);

					if (subparagraphGroupElement.format)
					{
						originalCharacterFormat = flowChild.coreStyles;
						var characterFormat:TextLayoutFormat = new TextLayoutFormat(flowChild.format);
						characterFormat.concat(subparagraphGroupElement.format);
						flowChild.setCoreStylesInternal(characterFormat);
					}
					
					output.appendChild(exporter.exportChild(flowChild));
				}
				finally
				{
					if (subparagraphGroupElement.format)
						flowChild.setCoreStylesInternal(originalCharacterFormat);
				}
			}
			
			return output.children();
		}
		
		/** Functionality for exporting a DivElement. Export its children (but not self), with character/paragraph/container formats concatenated.
		 * @param exporter	Root object for the export
		 * @param div	element to export
		 * @return XMLList	XML for the element
		 */
		static public function exportDiv(exporter:BaseTextLayoutExporter, div:DivElement):XMLList
		{
			var output:XML = <dummy/>; // tag doesn't matter because the container formatted element itself is not exported
			
			// output each child
			for(var childIter:int = 0; childIter < div.numChildren; ++childIter)
			{
				var flowChild:FlowElement;
				var paraChild:ParagraphFormattedElement;
				var containerChild:ContainerFormattedElement;

				var originalCharacterFormat:Object;
				
				try
				{
					flowChild = div.getChildAt(childIter);

					if (div.format)
					{
						originalCharacterFormat = flowChild.coreStyles;
						var characterFormat:TextLayoutFormat = new TextLayoutFormat(flowChild.format);
						characterFormat.concat(div.format);
						flowChild.setCoreStylesInternal(characterFormat);
					}
					
					
					output.appendChild(exporter.exportChild(flowChild));
				}
				finally
				{
					if (div.format)
						flowChild.setCoreStylesInternal(originalCharacterFormat);
				}
			}
			
			return output.children();
		}
		
		static private const _fxgExportStylesExclusions:Array = [FormatValue.INHERIT]; // contains values not to be exported
		
		/** Overrides BaseTextLayoutExporter.exportFlowElement to fix trackingRight (in TextLayout) vs. tracking (in FXG) discrepancy
		 * @param flowElement	Element to export
		 * @return XMLList	XML for the element
		 */
		protected override function exportFlowElement (flowElement:FlowElement):XMLList
		{
			var output:XMLList = super.exportFlowElement(flowElement);
			
			var coreStyles:Object = flowElement.coreStyles;
			if (coreStyles)
			{ 
				// WhiteSpaceCollapse attribute should never be exported (except on TextFlow -- handled separately)
				delete coreStyles[TextLayoutFormat.whiteSpaceCollapseProperty.name];
				exportStyles(output, coreStyles, characterFormatDescription, _fxgExportStylesExclusions);	
			}
			
			if (output.hasOwnProperty("@trackingRight"))
			{
				var trackingVal:String = output.@trackingRight;
				delete output.@trackingRight;
				
				output.@tracking = trackingVal;
			}
			return output;
		}
		
		/** Overridable worker method for exporting a ParagraphFormattedElement. Creates the XMLList.
		 * @param flowElement	Element to export
		 * @return XMLList	XML for the element
		 */
		protected override function exportParagraphFormattedElement(flowElement:FlowElement):XMLList
		{
			var rslt:XMLList = exportFlowElement(flowElement);
			var coreStyles:Object = flowElement.coreStyles;
			if (coreStyles)
				exportStyles(rslt, coreStyles, paragraphFormatDescription, _fxgExportStylesExclusions);
			// output each child
			for(var childIter:int = 0; childIter < ParagraphFormattedElement(flowElement).numChildren; ++childIter)
			{
				var flowChild:FlowElement = ParagraphFormattedElement(flowElement).getChildAt(childIter);
				var childXML:XMLList = exportChild(flowChild);
				if (childXML)
					rslt.appendChild(childXML);
			}
			return rslt;
		}
		
		/** Overridable worker method for exporting a ParagraphFormattedElement. Creates the XMLList.
		 * @param flowElement	Element to export
		 * @return XMLList	XML for the element
		 */
		protected override function exportContainerFormattedElement(flowElement:FlowElement):XMLList
		{
			var output:XMLList = exportParagraphFormattedElement(flowElement);
			var coreStyles:Object = flowElement.coreStyles;
			if (coreStyles)
				exportStyles(output, coreStyles, containerFormatDescription, _fxgExportStylesExclusions);
			return output;
		}
	
		protected function get characterFormatDescription():Object
		{
			return _characterFormatDescription;
		}
		
		protected function get paragraphFormatDescription():Object
		{
			return _paragraphFormatDescription;
		}
		
		protected function get containerFormatDescription():Object
		{
			return _containerFormatDescription;
		}
	}
}
