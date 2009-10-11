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
	import flashx.textLayout.debug.assert;
	import flashx.textLayout.elements.*;
	import flashx.textLayout.formats.TextLayoutFormat;
	import flashx.textLayout.formats.ITextLayoutFormat;
	import flashx.textLayout.formats.TextAlign;
	import flashx.textLayout.formats.Direction;
	import flashx.textLayout.formats.Float;
	import flash.text.engine.FontWeight;
	import flash.text.engine.FontPosture;
	import flash.utils.getQualifiedClassName;
	import flashx.textLayout.tlf_internal;
	use namespace tlf_internal;

	[ExcludeClass]
	/** 
	* @private
	* Export filter for HTML format. 
	*/
	internal class HtmlExporter implements ITextExporter	
	{
		private static var _config:ImportExportConfiguration;
		
		public function HtmlExporter()
		{
			if (!_config)
			{
				_config = new ImportExportConfiguration();
				_config.addIEInfo("p", ParagraphElement, null, exportParagraph, true);
				_config.addIEInfo("a", LinkElement, null, exportLink, false);
				_config.addIEInfo("tcy" /* only children exported, so name is irrelevant */, TCYElement, null, exportTCY, false);
				_config.addIEInfo("span", SpanElement, null, exportSpan, false);
				_config.addIEInfo("img", InlineGraphicElement, null, exportImage, false);
				_config.addIEInfo("tab" /* exported as a span, so name is irrelevant */, TabElement, null, exportTab, false);
				_config.addIEInfo("br", BreakElement, null, exportBreak, false);

			}
		}
		 
		/** Export text content
		 * @param source	the text to export
		 * @param conversionType 	what type to return
		 * @return Object	the exported content
		 */
		public function export(source:TextFlow, conversionType:String):Object
		{
			if (conversionType == ConversionType.STRING_TYPE)
				return exportToString(source);
			else if (conversionType == ConversionType.XML_TYPE)
				return exportToXML(source);
			return null;
		}
		
		/** Export text content as a string
		 * @param source	the text to export
		 * @return String	the exported content
		 */
		protected function exportToString(textFlow:TextFlow):String
		{
			var result:String;
			// We do some careful type casting here so that leading and trailing spaces in the XML don't
			// get dropped when it is converted to a string
			var originalSettings:Object = XML.settings();
			try
			{
				XML.ignoreProcessingInstructions = false;		
				XML.ignoreWhitespace = false;
				XML.prettyPrinting = false;
				result = exportToXML(textFlow).toXMLString();
				XML.setSettings(originalSettings);
			}
			catch(e:Error)
			{
				XML.setSettings(originalSettings);
				throw(e);
			}		
			return result;
		}
		
		/** Export text content of a TextFlow into HTML format.
		 * @param source	the text to export
		 * @return XML	the exported content
		 */
		protected function exportToXML(textFlow:TextFlow) : XML
		{
			var xml:XML = <body/>;
			
			var firstLeaf:FlowLeafElement = textFlow.getFirstLeaf();
			if (firstLeaf)
			{
				var para:ParagraphElement = firstLeaf.getParagraph();	
				var lastPara:ParagraphElement = textFlow.getLastLeaf().getParagraph();
	
				for (;;)
				{
					xml.appendChild(exportElement(para));
					if (para == lastPara)
						break;
						
					para = textFlow.findLeaf(para.getAbsoluteStart() + para.textLength).getParagraph();
				}
			}
			
			var html:XML = <html/>;
			html.appendChild(xml);
			
			return html;
		}
		
		/** Export a paragraph
		 * @param name name for the XML element
		 * @return XML	the populated XML element
		 */
		private function exportParagraph(name:String, para:ParagraphElement):XML
		{
			var xml:XML = <{name}/>;
			exportChildren (xml, para);
			return exportParagraphFormat(xml, para);
		}
		
		/** Export a link
		 * @param name name for the XML element
		 * @return XML	the populated XML element
		 */
		private function exportLink(name:String, link:LinkElement):XML
		{
			var xml:XML = <{name}/>;
			if (link.href)
				xml.@href= link.href;
			if (link.target)
				xml.@target = link.target;
			else
			{
				// TEXT_FIELD_HTML_FORMAT uses _self as the default target (to replicate TextField behavior) 
				// while the TLF object model uses null (funcionally identical to _blank). Account for this difference.
				xml.@target = "_blank";
			}
						
			exportChildren(xml, link);
			return xml;
		}
		
		/** Export a tcy element
		 * @param name ignored
		 * @return XMLList the exported children
		 */
		private function exportTCY(name:String, tcy:TCYElement):XMLList
		{
			var xml:XML = <{name}/>;
			exportChildren(xml, tcy);
			return xml.children(); // the element itself is not exported
		}
		
		static private const brRegEx:RegExp = /\u2028/;
		
		/** Gets the xml element used to represent a character in the export format
		 */
		static private function getSpanTextReplacementXML(ch:String):XML
		{
			CONFIG::debug {assert(ch == '\u2028', "Did not recognize character to be replaced with XML"); }
			return <br/>;
		}
		
		/** Export a span
		 * @param name name for the XML element
		 * @return XML	the populated XML element
		 */
		private function exportSpan(name:String, span:SpanElement):XML
		{
			var xml:XML  = <{name}/>; 
			BaseTextLayoutExporter.exportSpanText(xml, span, brRegEx, getSpanTextReplacementXML);
			if (span.styleName)
				xml["@class"] = span.styleName; // xml.@class confuses the compiler
			return exportCharacterFormat (xml, span);
		}
		
		/** Export an inline graphic
		 * @param name name for the XML element
		 * @return XML	the populated XML element
		 */
		private function exportImage(name:String, image:InlineGraphicElement):XML
		{
			var xml:XML = <{name}/>;
			xml.@height = image.actualHeight;
			xml.@width = image.actualWidth;
			xml.@src = image.source;
			if (image.float != Float.NONE)
				xml.@align = image.float;
			return xml;
		}
	
		/** Export a break
		 * Is this ever called: BreakElements are either merged with adjacent spans or become spans? 
		 * @param name name for the XML element
		 * @return XML	the populated XML element
		 */		
		private function exportBreak(name:String, breakElement:BreakElement):XML
		{
			return <{name}/>;
		}
		
		/** Export a tab
		 * Is this ever called: BreakElements are either merged with adjacent spans or become spans? 
		 * @param name ignored
		 * @return XML	the populated XML element
		 */	
		private function exportTab(name:String, tabElement:TabElement):XML
		{
			var xml:XML = <span/>; // exports as a span
			xml.appendChild("\t");
			return xml;
		}
		
		/** Exports the non-default properties of the computed character format on a flow leaf element  
		 * @param xml xml to decorate with attributes or add formatting children to
		 * @flowElement the leaf element
		 * @return XML	the outermost XML element after exporting 
		 */	
		private function exportCharacterFormat(xml:XML, flowElement:FlowLeafElement):XML
		{
			var outerElement:XML = xml;
			
			var charFormat:ITextLayoutFormat = flowElement.computedFormat;
			var defaultFormat:ITextLayoutFormat = TextLayoutFormat.defaultFormat;
			
			var font:XML = <font/>;
			if (charFormat.fontSize != defaultFormat.fontSize)
				font.@size = charFormat.fontSize;
			if (charFormat.fontFamily != defaultFormat.fontFamily)
				font.@face = charFormat.fontFamily;
			if (charFormat.color != defaultFormat.color)
				font.@color = "#" + charFormat.color.toString(16);
			if (font.attributes().length())
				outerElement = nest(font, outerElement);
			
			if (charFormat.textDecoration.toString() == flashx.textLayout.formats.TextDecoration.UNDERLINE)
				outerElement = nest (<u/>, outerElement);
			if (charFormat.fontWeight.toString() == flash.text.engine.FontWeight.BOLD)
				outerElement = nest (<b/>, outerElement);
			if (charFormat.fontStyle.toString() == flash.text.engine.FontPosture.ITALIC)
				outerElement = nest (<i/>, outerElement);

			return outerElement;				
		}
		
		/** Exports the non-default properties of the computed paragraph format on a paragraph  
		 * @param xml xml to decorate with attributes or add formatting children to
		 * @para the paragraph
		 * @return XML	the outermost XML element after exporting 
		 */	
		private function exportParagraphFormat(xml:XML, para:ParagraphElement):XML
		{	
			var paraFormat:ITextLayoutFormat = para.computedFormat;
			var defaultFormat:ITextLayoutFormat = TextLayoutFormat.defaultFormat;
			
			if (paraFormat.textAlign != defaultFormat.textAlign)
			{
				var textAlignment:String;
				switch(paraFormat.textAlign)
				{
					case TextAlign.START:
						textAlignment = (paraFormat.direction == Direction.LTR) ? TextAlign.LEFT : TextAlign.RIGHT;
						break;
					case TextAlign.END:
						textAlignment = (paraFormat.direction == Direction.LTR) ? TextAlign.RIGHT : TextAlign.LEFT;
						break;
					default:
						textAlignment = paraFormat.textAlign
				}
				xml.@align = textAlignment;
			}
			
			if (para.styleName)
				xml["@class"] = para.styleName; // xml.@class confuses the compiler
				
			var textFormat:XML = <textformat/>;
			if (paraFormat.paragraphStartIndent != defaultFormat.paragraphStartIndent)
			{
				if (paraFormat.direction == Direction.LTR)
					textFormat.@leftMargin = paraFormat.paragraphStartIndent;
				else 
					textFormat.@rightMargin = paraFormat.paragraphStartIndent;
			}
			if (paraFormat.paragraphEndIndent != defaultFormat.paragraphEndIndent)
			{
				if (paraFormat.direction == Direction.LTR)
					textFormat.@rightMargin = paraFormat.paragraphEndIndent;
				else 
					textFormat.@leftMargin = paraFormat.paragraphEndIndent;
			}
			if (paraFormat.textIndent != defaultFormat.textIndent)
				textFormat.@indent = paraFormat.textIndent;

			return textFormat.attributes().length() ? nest(textFormat, xml) : xml;
		}
				
		/** Exports the flow element by finding the appropriate exporter
		 * @param flowElement	Element to export
		 * @return Object	XML/XMLList for the flowElement
		 */
		private function exportElement(flowElement:FlowElement):Object
		{
			var className:String = flash.utils.getQualifiedClassName(flowElement);
			var info:FlowElementInfo = _config.lookupByClass(className);
			if (info != null)
				return info.exporter(_config.lookupName(className), flowElement);
			return null;
		}
		
		/** Exports the children of a flow group element
		 * @param xml XML to append children to
		 * @param flowGroupElement	the flow group element
		 */
		private function exportChildren(xml:XML, flowGroupElement:FlowGroupElement):void
		{
			for(var i:int=0; i < flowGroupElement.numChildren; ++i)
			{
				xml.appendChild(exportElement(flowGroupElement.getChildAt(i)));	
			}
		}
		
		/** Helper to establish a parent-child relationship between two xml elements
		 * and return the parent
		 * @param parent the intended parent
		 * @param child the intended child
		 * @return the parent
		 */
		private function nest (parent:XML, child:XML):XML
		{
			parent.setChildren(child);
			return parent;
		}
		
 	}
}