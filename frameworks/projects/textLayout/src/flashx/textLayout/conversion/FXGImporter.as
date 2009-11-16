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
	import flash.utils.Dictionary;
	
	import flashx.textLayout.elements.BreakElement;
	import flashx.textLayout.elements.FlowGroupElement;
	import flashx.textLayout.elements.IConfiguration;
	import flashx.textLayout.elements.ParagraphElement;
	import flashx.textLayout.elements.ParagraphFormattedElement;
	import flashx.textLayout.elements.SpanElement;
	import flashx.textLayout.elements.TabElement;
	import flashx.textLayout.elements.TextFlow;
	import flashx.textLayout.formats.FormatValue;
	import flashx.textLayout.formats.Direction;
	import flashx.textLayout.formats.Category;
	import flashx.textLayout.formats.ITextLayoutFormat;
	import flashx.textLayout.formats.TextLayoutFormat;
	import flashx.textLayout.property.StringProperty;
	import flashx.textLayout.tlf_internal;
	import flashx.textLayout.elements.FlowElement;
	use namespace tlf_internal;
	
	[ExcludeClass]
	/** 
	 * @private
	 * Converts textual data in a structured MXML-G format into the internal 
	 * TextLayout format.
	 */ 	
	internal class FXGImporter extends BaseTextLayoutImporter 
	{
		/** Subset format descriptions for FXG.
		 * TODO-08/20/2008-Drive these from the attributes xls?
		 * TODO-08/20/2008-Handle lineBreak (a container attribute that FXG treats as a character attribute), 
		 * blockProgression (a container attribute that FXG treats as a paragraph attribute), and width/height for TextGraphic. 
		 */
		 
		static internal var _characterFormatDescription:Object = {
			  color:TextLayoutFormat.colorProperty
			, lineThrough:TextLayoutFormat.lineThroughProperty
			, textAlpha:TextLayoutFormat.textAlphaProperty
			, fontSize:TextLayoutFormat.fontSizeProperty
			//, baselineShift:CharacterFormat.baselineShiftProperty
			//, trackingLeft:CharacterFormat.trackingLeftProperty
			, trackingRight:TextLayoutFormat.trackingRightProperty
			, lineHeight:TextLayoutFormat.lineHeightProperty
			//, breakOpportunity:CharacterFormat.breakOpportunityProperty
			//, digitCase:CharacterFormat.digitCaseProperty
			//, digitWidth:CharacterFormat.digitWidthProperty
			//, dominantBaseline:CharacterFormat.dominantBaselineProperty
			, kerning:TextLayoutFormat.kerningProperty
			//, ligatureLevel:CharacterFormat.ligatureLevelProperty
			//, alignmentBaseline:CharacterFormat.alignmentBaselineProperty
			//, locale:CharacterFormat.localeProperty
			//, typographicCase:CharacterFormat.typographicCaseProperty
			, fontFamily:TextLayoutFormat.fontFamilyProperty
			, textDecoration:TextLayoutFormat.textDecorationProperty
			, fontWeight:TextLayoutFormat.fontWeightProperty
			, fontStyle:TextLayoutFormat.fontStyleProperty
			, whiteSpaceCollapse:TextLayoutFormat.whiteSpaceCollapseProperty
			//, renderingMode:CharacterFormat.renderingModeProperty
			//, cffHinting:CharacterFormat.cffHintingProperty
			//, fontLookup:CharacterFormat.fontLookupProperty
			//, textRotation:CharacterFormat.textRotationProperty
		}
		
		static internal var _paragraphFormatDescription:Object = {
			  textIndent:TextLayoutFormat.textIndentProperty
			, paragraphStartIndent:TextLayoutFormat.paragraphStartIndentProperty
			, paragraphEndIndent:TextLayoutFormat.paragraphEndIndentProperty
			, paragraphSpaceBefore:TextLayoutFormat.paragraphSpaceBeforeProperty
			, paragraphSpaceAfter:TextLayoutFormat.paragraphSpaceAfterProperty
			, textAlign:TextLayoutFormat.textAlignProperty
			, textAlignLast:TextLayoutFormat.textAlignLastProperty
			, direction:TextLayoutFormat.directionProperty
			//, tabStops:ParagraphFormat.tabStopsProperty
		}
		
		static internal var _containerFormatDescription:Object = {
			  //columnGap:ContainerFormat.columnGapProperty,
			  paddingLeft:TextLayoutFormat.paddingLeftProperty
			, paddingTop:TextLayoutFormat.paddingTopProperty
			, paddingRight:TextLayoutFormat.paddingRightProperty
			, paddingBottom:TextLayoutFormat.paddingBottomProperty
			//, columnCount:ContainerFormat.columnCountProperty
			//, columnWidth:ContainerFormat.columnWidthProperty
			//, firstBaselineOffset:ContainerFormat.firstBaselineOffsetProperty
			//, verticalAlign:ContainerFormat.verticalAlignProperty
			, blockProgression:TextLayoutFormat.blockProgressionProperty
			, lineBreak:TextLayoutFormat.lineBreakProperty
		}
		// for some reason following lines generate runtime errors
		// static private const _characterFormatImporter:FXGCustomCharacterFormatImporter = new FXGCustomCharacterFormatImporter(TextLayoutFormat,_characterFormatDescription);
		// static private var _paragraphFormatImporter:FXGCParagraphCharacterFormatImporter = new FXGCustomParagraphFormatImporter(TextLayoutFormat,_paragraphFormatDescription);
		static private var _characterFormatImporter:FXGCustomCharacterFormatImporter; 
		static private var _paragraphFormatImporter:FXGCustomParagraphFormatImporter; 
		static private const _containerFormatImporter:TLFormatImporter = new TLFormatImporter(TextLayoutFormat,_containerFormatDescription);
		// Support for FXG 1.0 properties which can't be migrated without additional context
		static internal const _obsoleteMarginFormatsDescription:Object = {
			marginLeft : new StringProperty("marginLeft", null, true, Category.PARAGRAPH),
			marginRight : new StringProperty("marginLeft", null, true, Category.PARAGRAPH)
		}
		static private const _obsoleteMarginFormatsImporter:TLFormatImporter = new TLFormatImporter(Dictionary,_obsoleteMarginFormatsDescription);
		
		
		/** Constructor
		 * Errors may be collected, and fetched later, or they can throw when they
		 * are encountered.
		 */
		public function FXGImporter(textFlowConfiguration:IConfiguration =  null)
		{
			var config:ImportExportConfiguration = new ImportExportConfiguration();			
 			config.addIEInfo("TextFlow", TextFlow,  BaseTextLayoutImporter.parseTextFlow, BaseTextLayoutExporter.exportTextFlow, true);
			config.addIEInfo("br", BreakElement,    BaseTextLayoutImporter.parseBreak,    null, false);
			config.addIEInfo("p", ParagraphElement, FXGImporter.parsePara,     BaseTextLayoutExporter.exportParagraphFormattedElement, true);
			config.addIEInfo("span", SpanElement,   BaseTextLayoutImporter.parseSpan,     BaseTextLayoutExporter.exportSpan, false);
			
			// create these here - can't be done above
			if (_characterFormatImporter == null)
				_characterFormatImporter = new FXGCustomCharacterFormatImporter(TextLayoutFormat,_characterFormatDescription);
			if (_paragraphFormatImporter == null)
				_paragraphFormatImporter = new FXGCustomParagraphFormatImporter(TextLayoutFormat,_paragraphFormatDescription);

			super(textFlowConfiguration, new Namespace("fxg", "http://ns.adobe.com/fxg/2008"), config);
		}

		/** @private */
		protected override function importFromXML(xmlSource:XML):TextFlow
			// Parse an XFL hierarchy into a TextFlow, using the geometry supplied by a TextFrame
			// to host child containers (e.g. tables). This is the main entry point into this class.
		{
			if (xmlSource.nodeKind() == "element" && xmlSource.name() == "Graphic")
				xmlSource = xmlSource..*::TextGraphic[0];
				
			if (!checkNamespace(xmlSource))
				return null;
	
			var newFlow:TextFlow = new TextFlow(_textFlowConfiguration);

			var formatImporters:Array = [ _characterFormatImporter,_paragraphFormatImporter,_containerFormatImporter, _obsoleteMarginFormatsImporter];
			parseAttributes(xmlSource,formatImporters);

			newFlow.format = extractAttributesHelper(newFlow.format,_containerFormatImporter) as ITextLayoutFormat;
			newFlow.format = extractAttributesHelper(newFlow.format,_paragraphFormatImporter) as ITextLayoutFormat;
			newFlow.format = extractAttributesHelper(newFlow.format,_characterFormatImporter) as ITextLayoutFormat;
			_flowDirection = newFlow.direction;
			extractObsoleteAttributes(newFlow);
				
			parseFlowChildren(xmlSource.*::content[0], newFlow);
				
			CONFIG::debug { newFlow.debugCheckNormalizeAll() ; }
			newFlow.normalize();
			
			newFlow.applyWhiteSpaceCollapse();

			return newFlow;
		}
		
		override public function createTextFlowFromXML(xmlToParse:XML, textFlow:TextFlow = null):TextFlow
		{
			return null;	// never called
		}
		
		public override function createParagraphFromXML(xmlToParse:XML):ParagraphElement
		{
			var paraElem:ParagraphElement = new ParagraphElement();
			
			var formatImporters:Array = [ _characterFormatImporter,_paragraphFormatImporter, _obsoleteMarginFormatsImporter];
			parseAttributes(xmlToParse,formatImporters);

			paraElem.format = extractAttributesHelper(paraElem.format,_paragraphFormatImporter) as ITextLayoutFormat;
			paraElem.format = extractAttributesHelper(paraElem.format,_characterFormatImporter) as ITextLayoutFormat;
			extractObsoleteAttributes(paraElem);

			return paraElem;
		}
		
		/** Static method to parse the supplied XML into a paragrph. Parse the <p ...> tag and it's children.
		 * 
		 * @param importFilter	parser object
		 * @param xmlToParse	content to parse
		 * @param parent 		the parent for the new content
		 */
		static public function parsePara(importFilter:BaseTextLayoutImporter, xmlToParse:XML, parent:FlowGroupElement):void
		{
			var paraElem:ParagraphElement = importFilter.createParagraphFromXML(xmlToParse);
			if (importFilter.addChild(parent, paraElem))
			{
				importFilter.parseFlowGroupElementChildren(xmlToParse, paraElem);
				//if parsing an empty paragraph, create a Span for it.
				if (paraElem.numChildren == 0)
					paraElem.addChild(new SpanElement());
			}
		}
		
		public override function createSpanFromXML(xmlToParse:XML):SpanElement
		{
			var spanElem:SpanElement = new SpanElement();
			
			var formatImporters:Array = [ _characterFormatImporter ];
			parseAttributes(xmlToParse,formatImporters);

			spanElem.format = extractAttributesHelper(spanElem.format,_characterFormatImporter) as ITextLayoutFormat;
			return spanElem;
		}
		
		public override function createTabFromXML(xmlToParse:XML):TabElement
		{
			return null;
		}
		
		private function extractObsoleteAttributes(elem:FlowElement):void
		{
			if (_obsoleteMarginFormatsImporter.result)
			{
				var direction:* = elem.direction;
				if (direction == undefined && !(elem is TextFlow))
					direction = _flowDirection; // inherit from flow
					 
				var rtlDirection:Boolean = (direction == Direction.RTL);
				
				var marginLeft:String = _obsoleteMarginFormatsImporter.result["marginLeft"];
				if (marginLeft)
				{
					if (rtlDirection)
						elem.paragraphEndIndent = Number (marginLeft);
					else
						elem.paragraphStartIndent = Number(marginLeft);
				}
				
				var marginRight:String = _obsoleteMarginFormatsImporter.result["marginRight"];
				if (marginRight)
				{
					if (rtlDirection)
						elem.paragraphStartIndent = Number (marginRight);
					else
						elem.paragraphEndIndent = Number(marginRight);
				}
			}	
		}
		
		/** Clear results from last export. */
		protected override function clear():void
		{
			_flowDirection = undefined;
			super.clear();
		}
		
		private var _flowDirection:*; 
	}		
}
import flashx.textLayout.conversion.TLFormatImporter;

class FXGCustomCharacterFormatImporter extends TLFormatImporter
{
	public function FXGCustomCharacterFormatImporter(classType:Class,description:Object)
	{
		super(classType,description);
	}
	
	public override function importOneFormat(key:String,val:String):Boolean
	{
		if (key == "tracking")
			key = "trackingRight";
		return super.importOneFormat(key,val);
	} 
}

class FXGCustomParagraphFormatImporter extends TLFormatImporter
{
	public function FXGCustomParagraphFormatImporter(classType:Class,description:Object)
	{
		super(classType,description);
	}
	
	public override function importOneFormat(key:String,val:String):Boolean
	{
		switch (key)
		{
			// Handle formats whose names have been changed since FXG 1.0
			case "marginTop":
				key = "paragraphSpaceBefore";
			case "marginBottom":
				key = "paragraphSpaceAfter";
			
			// marginLeft and marginRight are handled separately because the formats they map to depend on the direction
		}
		
		return super.importOneFormat(key,val);
	} 
}


