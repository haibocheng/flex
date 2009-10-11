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
	import flash.text.engine.TextRotation;
	import flashx.textLayout.container.ContainerController;
	import flashx.textLayout.debug.assert;
	import flashx.textLayout.elements.IConfiguration;
	import flashx.textLayout.elements.TabElement;
	import flashx.textLayout.elements.DivElement;
	import flashx.textLayout.elements.FlowElement;
	import flashx.textLayout.elements.SpanElement;
	import flashx.textLayout.elements.BreakElement;
	import flashx.textLayout.elements.FlowGroupElement;
	import flashx.textLayout.elements.InlineGraphicElement;
	import flashx.textLayout.elements.LinkElement;
	import flashx.textLayout.elements.ParagraphElement;
	import flashx.textLayout.elements.SpecialCharacterElement;
	import flashx.textLayout.elements.TextFlow;
	import flashx.textLayout.formats.TextLayoutFormat;
	import flashx.textLayout.formats.TextLayoutFormatValueHolder;
	import flashx.textLayout.formats.ITextLayoutFormat;
	import flashx.textLayout.property.StringProperty;
	import flashx.textLayout.tlf_internal;
	use namespace tlf_internal;
	
	[ExcludeClass]
	/** 
	 * @private
	 * HtmlImporter converts from HTML to TextLayout data structures
	 */ 	
	internal class HtmlImporter extends BaseTextLayoutImporter
	{
		static internal var _fontDescription:Object = {
			color:TextLayoutFormat.colorProperty,
			fontSize:TextLayoutFormat.fontSizeProperty,
			fontFamily:TextLayoutFormat.fontFamilyProperty
		};
		// for some reason next line generates a runtime error
		// static private const _fontImporter:FontImporter = new FontImporter(CharacterFormat, _fontDescription);
		static private var _fontImporter:FontImporter;
		
		static internal var _textFormatDescription:Object = {
			paragraphStartIndent:TextLayoutFormat.paragraphStartIndentProperty,
			paragraphEndIndent:TextLayoutFormat.paragraphEndIndentProperty,
			textIndent:TextLayoutFormat.textIndentProperty
		};
		// static private const _textFormatImporter:TextFormatImporter = new TextFormatImporter(ParagraphFormat, _textFormatDescription);
		static private var _textFormatImporter:TextFormatImporter;
		
		static internal var _paragraphFormatDescription:Object = {
			textAlign:TextLayoutFormat.textAlignProperty
		};
		// static private const _paragraphFormatImporter:HtmlCustomParaFormatImporter = new HtmlCustomParaFormatImporter(ParagraphFormat, _paragraphFormatDescription);
		static private var _paragraphFormatImporter:HtmlCustomParaFormatImporter;
		
		static internal const _linkDescription:Object = {
			href	: new StringProperty("href",   null, false, null),
			target	: new StringProperty("target", null, false, null)
		};
		static private const _linkFormatImporter:TLFormatImporter = new TLFormatImporter(Dictionary,_linkDescription);
		
		static internal const _imageDescription:Object = {
			height	: InlineGraphicElement.heightPropertyDefinition,
			width	: InlineGraphicElement.widthPropertyDefinition,
			src		: new StringProperty("src", null, false, null),
			align	: new StringProperty("align", null, false, null)};
		static private const _ilgFormatImporter:TLFormatImporter = new TLFormatImporter(Dictionary,_imageDescription);
		
		static internal const _classDescription:Object =
		{
			// A property named 'class' confuses the compiler. 
			// class	: new StringProperty("class",   null, false, null)
			// So, we initialize _classDescription in the constructor 
		};
		static private const _classImporter:TLFormatImporter = new TLFormatImporter(Dictionary,_classDescription);
		
		// Character/paragraph formats specified by formatting elements in the ancestry of the element being parsed currently 
		static private var _activeCharFormat:TextLayoutFormatValueHolder = new TextLayoutFormatValueHolder();
		static private var _activeParaFormat:TextLayoutFormatValueHolder = new TextLayoutFormatValueHolder();
		
		/** Constructor */
		public function HtmlImporter(textFlowConfiguration:IConfiguration)
		{
			var config:ImportExportConfiguration = new ImportExportConfiguration();
			
			// inherited	
 			config.addIEInfo("br", BreakElement, BaseTextLayoutImporter.parseBreak, null, false);
 			config.addIEInfo("p", ParagraphElement, BaseTextLayoutImporter.parsePara, null, true);
 			config.addIEInfo("span", SpanElement, BaseTextLayoutImporter.parseSpan, null, false);
 			
 			// specialized
 			config.addIEInfo("a", LinkElement, HtmlImporter.parseLink, null, false);
			config.addIEInfo("img", InlineGraphicElement, HtmlImporter.parseInlineGraphic, null, false);
		
			// non-flow elements (formatting elements like <i> or HTML container elements like <html> or <body>)
			config.addIEInfo("html", null, HtmlImporter.parseHtmlContainer, null, false);
			config.addIEInfo("head", null, HtmlImporter.parseHead, null, false);
			config.addIEInfo("body", null, HtmlImporter.parseHtmlContainer, null, false);
			config.addIEInfo("font", null, HtmlImporter.parseFont, null, false);
			config.addIEInfo("textformat", null, HtmlImporter.parseTextFormat, null, false);
			config.addIEInfo("u", null, HtmlImporter.parseUnderline, null, false);
			config.addIEInfo("i", null, HtmlImporter.parseItalic, null, false);
			config.addIEInfo("b", null, HtmlImporter.parseBold, null, false);
			
			// create these here - can't be done above
			if (_paragraphFormatImporter == null)
				_paragraphFormatImporter = new HtmlCustomParaFormatImporter(TextLayoutFormat, _paragraphFormatDescription);
			if (_textFormatImporter == null)
				_textFormatImporter = new TextFormatImporter(TextLayoutFormat, _textFormatDescription);
			if (_fontImporter == null)
				_fontImporter = new FontImporter(TextLayoutFormat, _fontDescription);
				
			_classDescription["class"] = new StringProperty("class", null, false, null);
						
			super(textFlowConfiguration, null, config);
		}
		
		
		/** Parse and convert input data
		 * 
		 * @param source - the HTML string
		 */
		protected override function importFromString(source:String):TextFlow
		{	
			// Use toXML rather than the XML constructor because the latter expects
			// well-formed XML, which source may not be 
			var xml:XML = toXML(source);
			return xml ? importFromXML(xml) : null;
		}

		/** Parse and convert input XML data
		 */
		protected override function importFromXML(xmlSource:XML):TextFlow
		{
			var textFlow:TextFlow = new TextFlow(_textFlowConfiguration);
			
			// Unlike other markup formats, the HTML format for TLF does not have a fixed root XML element.
			// <html> and <body> are optional, and flow elements may or may not be encapsulated in formatting 
			// elements like <i> or <textformat>. Use parseObject to handle any (expected) root element.
			parseObject(xmlSource.name().localName, xmlSource, textFlow);
			
			CONFIG::debug { textFlow.debugCheckNormalizeAll() ; }
			textFlow.normalize();
			textFlow.applyWhiteSpaceCollapse();
			
			return textFlow;
		}		

		protected override function clear():void
		{
			// reset active char and para formats
			_activeCharFormat.coreStyles = null;
			_activeParaFormat.coreStyles = null;
			super.clear();
		}
		
		public override function createParagraphFromXML(xmlToParse:XML):ParagraphElement
		{
			var paraElem:ParagraphElement = new ParagraphElement();
				
			// parse xml attributes for paragraph format
			var formatImporters:Array = [_paragraphFormatImporter, _classImporter];
			parseAttributes(xmlToParse, formatImporters);
			var paragraphFormat:TextLayoutFormat = new TextLayoutFormat(_paragraphFormatImporter.result as ITextLayoutFormat);
			
			// apply paragraph format inherited from formatting elements
			paragraphFormat.apply(_activeParaFormat);
			
			// TODO: concat here???
			paraElem.format = paragraphFormat;
			
			// Use the value of the 'class' attribute (if present) as styleName
			if (_classImporter.result)
				paraElem.styleName = _classImporter.result["class"]; 
			
			return paraElem;
		}
		
		public function createLinkFromXML(xmlToParse:XML):LinkElement
		{
			var linkElem:LinkElement = new LinkElement();

			var formatImporters:Array = [ _linkFormatImporter ];
			parseAttributes(xmlToParse, formatImporters);
			
			if (_linkFormatImporter.result)
			{
				linkElem.href = _linkFormatImporter.result["href"];
				linkElem.target = _linkFormatImporter.result["target"];
			}
			
			// TEXT_FIELD_HTML_FORMAT uses _self as the default target (to replicate TextField behavior) 
			// while the TLF object model uses null. Account for this difference.
			if (!linkElem.target)
				linkElem.target = "_self";

			return linkElem;
		}
		
		/** Parse a LinkElement
		 * 
		 * @param - importFilter:BaseTextLayoutImporter - parser object
		 * @param - xmlToParse:XML - the xml describing the Link
		 * @param - parent:FlowBlockElement - the parent of the new Link
		 * @return LinkElement - a new LinkElement and its children
		 */
		static public function parseLink(importFilter:BaseTextLayoutImporter, xmlToParse:XML, parent:FlowGroupElement):void
		{
			var linkElem:LinkElement = HtmlImporter(importFilter).createLinkFromXML(xmlToParse);
			if (importFilter.addChild(parent, linkElem))
			{
				importFilter.parseFlowGroupElementChildren(xmlToParse, linkElem);
				//if parsing an empty link, create a Span for it.
				if (linkElem.numChildren == 0)
					linkElem.addChild(new SpanElement());
			}
		}	

		public override function createSpanFromXML(xmlToParse:XML):SpanElement
		{
			var spanElem:SpanElement = new SpanElement();
			
			// Use the value of the 'class' attribute (if present) as styleName
			var formatImporters:Array = [_classImporter];
			parseAttributes(xmlToParse,formatImporters);
			if (_classImporter.result)
				spanElem.styleName = _classImporter.result["class"];
			
			// no char format expressed as xml attributes
			// apply char format inherited from formatting elements
			// TODO: possible concat needed?
			spanElem.format = _activeCharFormat;
			
			return spanElem;
		}
		
		public function createInlineGraphicFromXML(xmlToParse:XML):InlineGraphicElement
		{				
			var imgElem:InlineGraphicElement = new InlineGraphicElement();

			var formatImporters:Array = [ _ilgFormatImporter ];
				
			parseAttributes(xmlToParse,formatImporters);
			
			if (_ilgFormatImporter.result)
			{
				var source:String = _ilgFormatImporter.result["src"];
				imgElem.source = source;
				
				// if not defined then let InlineGraphic set its own default
				imgElem.height = InlineGraphicElement.heightPropertyDefinition.valueFromString(_ilgFormatImporter.result["height"]);
				imgElem.width  = InlineGraphicElement.heightPropertyDefinition.valueFromString(_ilgFormatImporter.result["width"]);
				
				// TODO: make float a first class property	
				var floatVal:String = _ilgFormatImporter.result["align"] as String;
				if (floatVal != null)
					imgElem.float = floatVal;
			}
			
			return imgElem;
		}
	
	
		/** Parse a leaf element, the <image ...>  tag.
		 * 
		 * @param - importFilter:BaseTextLayoutImporter - parser object
		 * @param - xmlToParse:XML - the xml describing the InlineGraphic FlowElement
		 * @param - parent:FlowBlockElement - the parent of the new image FlowElement
		 */
		static public function parseInlineGraphic(importFilter:BaseTextLayoutImporter, xmlToParse:XML, parent:FlowGroupElement):void
		{
			var ilg:InlineGraphicElement = HtmlImporter(importFilter).createInlineGraphicFromXML(xmlToParse);
			importFilter.addChild(parent, ilg);
		}
		
		public override function createTabFromXML(xmlToParse:XML):TabElement
		{
			return null; // no tabs in HTML
		}
		
		static private function parseHtmlContainer(importFilter:BaseTextLayoutImporter, xmlToParse:XML, parent:FlowGroupElement):void
		{
			importFilter.parseFlowGroupElementChildren (xmlToParse, parent, null, true);
		}
		
		static private function parseHead(importFilter:BaseTextLayoutImporter, xmlToParse:XML, parent:FlowGroupElement):void
		{
			// do nothing
		}
		
		/** Parse the <Font> formatting element
		 * Saves corresponding attributes onto _activeCharFormat and continues parsing down the hierarchy
		 */
		static public function parseFont(importFilter:BaseTextLayoutImporter, xmlToParse:XML, parent:FlowGroupElement):void
		{
			// remember original state
			var charFormat:TextLayoutFormatValueHolder = new TextLayoutFormatValueHolder(_activeCharFormat);
			
			// parse xml attributes for character format
			var formatImporters:Array = [_fontImporter];
			importFilter.parseAttributes(xmlToParse, formatImporters);
			var format:ITextLayoutFormat = _fontImporter.result as ITextLayoutFormat;
			if (format)
				_activeCharFormat.apply(format);

			importFilter.parseFlowGroupElementChildren(xmlToParse, parent, null, true);
			
			// restore original state
			_activeCharFormat = charFormat;
		}
		
		/** Parse the <TextFormat> formatting element
		 * Saves corresponding attributes onto _activeParaFormat and continues parsing down the hierarchy
		 */
		static public function parseTextFormat(importFilter:BaseTextLayoutImporter, xmlToParse:XML, parent:FlowGroupElement):void
		{	
			// remember original state
			var paraFormat:TextLayoutFormatValueHolder = new TextLayoutFormatValueHolder(_activeParaFormat);
			
			// parse xml attributes for paragraph format
			var formatImporters:Array = [_textFormatImporter];
			importFilter.parseAttributes(xmlToParse, formatImporters);
			var format:ITextLayoutFormat = _textFormatImporter.result as ITextLayoutFormat;
			if (format)
				_activeParaFormat.apply(format);

			importFilter.parseFlowGroupElementChildren(xmlToParse, parent, null, true);
			
			// restore original state
			_activeParaFormat = paraFormat;
		}
		
		/** Parse the <b> formatting element
		 * Saves corresponding attributes onto _activeCharFormat and continues parsing down the hierarchy
		 */
		static public function parseBold(importFilter:BaseTextLayoutImporter, xmlToParse:XML, parent:FlowGroupElement):void
		{	
			var fontWeight:* = _activeCharFormat.fontWeight;
			_activeCharFormat.fontWeight = flash.text.engine.FontWeight.BOLD;
			
			importFilter.parseFlowGroupElementChildren(xmlToParse, parent, null, true);
			
			_activeCharFormat.fontWeight = fontWeight;
		}
		
		/** Parse the <i> formatting element
		 * Saves corresponding attributes onto _activeCharFormat and continues parsing down the hierarchy
		 */
		static public function parseItalic(importFilter:BaseTextLayoutImporter, xmlToParse:XML, parent:FlowGroupElement):void
		{	
			var fontStyle:* = _activeCharFormat.fontStyle;
			_activeCharFormat.fontStyle = flash.text.engine.FontPosture.ITALIC;
			
			importFilter.parseFlowGroupElementChildren(xmlToParse, parent, null, true);
			
			_activeCharFormat.fontStyle = fontStyle; 
		}
		
		/** Parse the <u> formatting element
		 * Saves corresponding attributes onto _activeCharFormat and continues parsing down the hierarchy
		 */
		static public function parseUnderline(importFilter:BaseTextLayoutImporter, xmlToParse:XML, parent:FlowGroupElement):void
		{	
			var textDecoration:* = _activeCharFormat.textDecoration;
			_activeCharFormat.textDecoration = flashx.textLayout.formats.TextDecoration.UNDERLINE;
			
			importFilter.parseFlowGroupElementChildren(xmlToParse, parent, null, true);
			
			_activeCharFormat.textDecoration = textDecoration; 
		}												
		
		protected override function checkNamespace(xmlToParse:XML):Boolean
		{	
			var elementNS:Namespace = xmlToParse.namespace();
			if (elementNS.uri.length) 
			{
				// must be empty 
				reportError("Unexpected namespace " + elementNS.toString());
				return false;
			}
			return true;
		}
		
		/** HTML parsing code
		 *  Uses regular expressions for recognizing constructs like comments, tags etc.
		 *  and a hand-coded parser to recognize the document structure and covert to well-formed xml
		 *  TODO-1/16/2009:List caveats
		 */ 
		
		/** Regex for stuff to be stripped: a comment, processing instruction, or a declaration
		 *
		 * <!--.*?--> - comment
		 *   <!-- - start comment
		 *   .*? - anything (including newline character, thanks to the s flag); the ? prevents a greedy match (which could match a --> later in the string) 
		 *  --> - end comment
		 *  
		 * <\?(".*?"|'.*?'|[^>]+)*> - processing instruction
		 *   <\? - start processing instruction
		 *   (".*?"|'.*?'|[^>]+)* - 0 or more of the following (interleaved in any order)
		 *     ".*?" - anything (including >) so long as it is within double quotes; the ? prevents a greedy match (which could match everything until a later " in the string) 
		 *     '.*?' - anything (including >) so long as it is within single quotes; the ? prevents a greedy match (which could match everything until a later ' in the string)
		 *     [^>"']+ - one or more characters other than > (because > ends the processing instruction), " (handled above), ' (handled above) 
		 *   > - end processing instruction
		 *
		 * <!(".*?"|'.*?'|[^>"']+)*> - declaration; 
		 * TODO-1/15/2009:not sure if a declaration can contain > within quotes. Assuming it can, the regex is  
		 *  is exactly like processing instruction above except it uses a ! instead of a ?
		 */
		private static var stripRegex:RegExp = /<!--.*?-->|<\?(".*?"|'.*?'|[^>"']+)*>|<!(".*?"|'.*?'|[^>"']+)*>/sg;
						
		/** Regular expression for an HTML tag
		 * < - open
		 *
		 * (\/?) - start modifier; 0 or 1 occurance of one of /
		 *
		 * (\w+) - tag name; 1 or more name characters
		 *
		 * ((?:\s+\w+(?:\s*=\s*(?:".*?"|'.*?'|[\w\.]+))?)*) - attributes; 0 or more of the following
		 *   (?:\s+\w+(?:\s*=\s*(?:".*?"|'.*?'|[\w\.]+))?) - attribute; 1 or more space, followed by 1 or more name characters optionally followed by
		 *      \s*=\s*(?:".*?"|'.*?'|[\w\.]+) - attribute value assignment; optional space followed by = followed by more optional space followed by one of
		 *         ".*?" - quoted attribute value (using double quotes); the ? prevents a greedy match (which could match everything until a later " in the string)
		 *         '.*?' - quoted attribute value (using single quotes); the ? prevents a greedy match ((which could match everything until a later ' in the string)
		 *         [\w\.]+ - unquoted attribute value; can only contain name characters or a period
		 *  Note: ?: specifies a non-capturing group (i.e., match won't be recorded or used as a numbered back-reference)
		 *
		 * \s* - optional space
		 *
		 * (\/?) - end modifer (0 or 1 occurance of /)
		 *
		 * > - close*/
		private static var tagRegex:RegExp = /<(\/?)(\w+)((?:\s+\w+(?:\s*=\s*(?:".*?"|'.*?'|[\w\.]+))?)*)\s*(\/?)>/sg;
		
		/** Regular expression for an attribute. Except for grouping differences, this regex is the same as the one that appears in tagRegex
		 */
		private static var attrRegex:RegExp = /\s+(\w+)(?:\s*=\s*(".*?"|'.*?'|[\w\.]+))?/sg;
		
		/** Wrapper for core HTML parsing code that manages XML settings during the process
		 */
		private function toXML(source:String):XML
		{
			var xml:XML;
			
			var originalSettings:Object = XML.settings();
			try
			{
				XML.ignoreProcessingInstructions = false;		
				XML.ignoreWhitespace = false;	

				xml = toXMLInternal(source);				
			}			
			finally
			{
				XML.setSettings(originalSettings);
			}	
			
			return xml;
		}	
		
		/** Convert HTML string to well-formed xml, accounting for the following HTML oddities
		 * 
		 * 1) Start tags are optional for some elements.
		 * Optional start tag not specified</html>
		 * 
		 * 2) End tags are optional for some elements. Elements with missing end tags may be implicitly closed by
		 *    a) start-tag for a peer element
		 *    <p>p element without end tag; closed by next p start tag
		 *    <p>closes previous p element with missing end tag</p>
		 * 
		 *    b) end-tag for an ancestor element 
		 * 	  <html><p>p element without end tag; closed by next end tag of an ancestor</html>
		 * 
		 * 3) End tags are forbidden for some elements
		 * <br> and <br/> are valid, but <br></br> is not
		 * 
		 * 4) Element and attribute names may use any case
		 * <P ALign="left"></p>
		 * 
		 * 5) Attribute values may be unquoted
		 * <p align=left/>
		 * 
		 * 6) Boolean attributed may assume a minimized form
		 * <p selected/> is equivalent to <p selected="selected"/>
		 * 
		 */	
		private function toXMLInternal(source:String):XML
		{
			// Strip out comments, processing instructions and declaratins	
			source = source.replace(stripRegex, "");
			
			// Parse the source, looking for tags and interleaved text content, creating an XML hierarchy in the process.
			// At any given time, there is a chain of 'open' elements corresponding to unclosed tags, the innermost of which is 
			// tracked by the currElem. Content (element or text) parsed next is added as a child of currElem.
			
			// Root of the XML hierarchy (set to <html/> because the html start tag is optional)
			// Note that source may contain an html start tag, in which case we'll end up with two such elements
			// This is not quite correct, but handled by the importer  
			var root:XML = <html/>; 
			var currElem:XML = root;  
			
			var lastIndex:int = tagRegex.lastIndex = 0;
			var openElemName:String;
						
			do
			{						
				var result:Object = tagRegex.exec(source);
				if (!result)
				{
					// No more tags: add text (starting at search index) as a child of the innermost open element and break out
					appendTextChild (currElem, source.substring(lastIndex));
					break;
				}
				
				if (result.index != lastIndex)
				{
					// Add text between tags as a child of the innermost open element
					appendTextChild (currElem, source.substring(lastIndex, result.index));
				}
				
				var tag:String = result[0]; // entire tag
				var hasStartModifier:Boolean = (result[1] == "\/"); // modifier after < (/ for end tag)
				var name:String = result[2].toLowerCase();  // name; use lower case
				var attrs:String = result[3];  // attributes; including whitespace
				var hasEndModifier:Boolean = (result[4] == "\/"); // modifier before > (/ for composite start and end tag)

				if (!hasStartModifier) // start tag 
				{	
					// Special case for implicit closing of <p>
					// TODO-12/23/2008: this will need to be handled more generically				
					if (name == "p" && currElem.name().localName == "p")
						currElem = currElem.parent();
						
					// Create an XML element by constructing a tag that can be fed to the XML constructor. Specifically, ensure
					// - it is a composite tag (start and end tag together) using the terminating slash shorthand
					// - element and attribute names are lower case
					// - attribute values are quoted  	
					// - boolean attributes are fully specified (e.g., selected="selected" rather than selected)
					tag = "<" + name;
					do
					{
						var innerResult:Object = attrRegex.exec(attrs);
						if (!innerResult)
							break;
							
						var attrName:String = innerResult[1].toLowerCase();
						tag += " " + attrName + "="; 
						var val:String = innerResult[2] ? innerResult[2] : attrName /* boolean attribute with implied value equal to attribute name */; 
						var startChar:String = val.charAt(0); 
						tag += ((startChar == "'" || startChar == "\"") ? val : ("\"" + val + "\""));
						
					} while (true);	 
					tag += "\/>";
					
					// Add the corresponding element as a child of the innermost open element 
					currElem.appendChild(new XML(tag));
					
					// The new element becomes the innermost open element unless it is already closed because
					// - this is a composite start and end tag (i.e., has an end modifier) 
					// - the end tag is forbidden, in which case the start tag itself implies closure 
					if (!hasEndModifier && !isEndTagForbidden(name))
						currElem = currElem.children()[currElem.children().length()-1];	
				}	
				else // end tag
				{	
					if (hasEndModifier || attrs.length)
					{
						reportError("Malformed tag " + tag);
						return null;
					}
					
					if (isEndTagForbidden(name))
					{
						reportError("End tag is not allowed for element " + name);
						return null;
					}
				
					// Move up the chain of open elements looking for a matching name
					// The matching element is closed and its parent becomes the innermost open element
					// Report error if matching element is not found and it requires a start tag
					// All intermediate open elements are also closed provided they don't require end tags
					// Report error if an intermediate element requires end tags
					var openElem:XML = currElem;
					do
					{
						openElemName = openElem.name().localName; 
						openElem = openElem.parent();
						
						if (openElemName == name)
						{
							currElem = openElem;
							break;
						}
						else if (isEndTagRequired(openElemName))
						{
							reportError("Missing end tag for element " + openElemName);
							return null;
						}

						if (!openElem)
						{
							if (isStartTagRequired(name))
							{
								reportError("Unexpected end tag " + name);
								return null;
							}
							break;
						}						
					}
					while (true);
				}
				
				lastIndex = tagRegex.lastIndex;
				if (lastIndex == source.length)
					break; // string completely parsed
					
			} while (true);		
			
			// No more string to parse, specifically, no more end tags. 
			// Validate that remaining open elements do not require end tags.
			while (currElem)
			{
				openElemName = currElem.name().localName; 
				if (isEndTagRequired(openElemName))
				{
					reportError("Missing end tag for element " + openElemName);
					return null;
				}
				currElem = currElem.parent();
			}	
			
			return root;
		}
		
		/** TODO-1/16/2009-Evaluate if following code may be better implemented using dictionaries queried at runtime
		 */
		 
		private function isStartTagRequired (tagName:String):Boolean
		{
			switch (tagName)
			{
				case "a":
				case "b":
				case "br":
				case "font":
				case "i":
				case "img":
				case "p":
				case "span":
				case "textformat":
				case "u":
					return true;
				default:
					// html, head, body, and unrecognized elements (which are handled leniently)
					return false;
			}
		}
		
		private function isEndTagRequired (tagName:String):Boolean
		{
			switch (tagName)
			{
				case "a":
				case "b":
				case "font":
				case "i":
				case "span":
				case "textformat":
				case "u":
					return true;
				default:
					// html, head, body, p, br, image and unrecognized elements (which are handled leniently)
					return false; 	
			}
		}
		
		private function isEndTagForbidden (tagName:String):Boolean
		{
			switch (tagName)
			{
				case "br":
				case "img":
					return true;
				default:
					return false;
			}
		}
		
		private static const anyPrintChar:RegExp = /[^\u0009\u000a\u000d\u0020]/g;	

		/** Adds text as a descendant of the specified XML element. Adds an intermediate <span> element is created if parent is not a <span>
		 *  No action is taken for whitespace-only text
		 */
		private function appendTextChild(parent:XML, text:String):void
		{
			if (text.match(anyPrintChar).length != 0)
			{
				var parentIsSpan:Boolean = (parent.localName() == "span");
				var elemName:String = parentIsSpan ? "dummy" : "span";
				
				 
				//var xml:XML = <{elemName}/>;
				//xml.appendChild(text);
				// The commented-out code above doesn't handle character entities like &lt; 
				// The following lets the XML constructor handle them 
				var xmlText:String = "<" + elemName + ">" + text + "<\/" + elemName + ">"; 
				var xml:XML = new XML(xmlText);
				
				parent.appendChild(parentIsSpan ? xml.children()[0] : xml);		
			}
		}	
	}
}

import flashx.textLayout.conversion.TLFormatImporter;
class HtmlCustomParaFormatImporter extends TLFormatImporter
{
	public function HtmlCustomParaFormatImporter(classType:Class,description:Object)
	{
		super(classType,description);
	}
	
	public override function importOneFormat(key:String,val:String):Boolean
	{
		if (key == "align")
			key = "textAlign";
		return super.importOneFormat(key,val);
	} 
}

class TextFormatImporter extends TLFormatImporter
{
	public function TextFormatImporter(classType:Class,description:Object)
	{
		super(classType,description);
	}
	
	public override function importOneFormat(key:String,val:String):Boolean
	{
		if (key == "leftmargin")
			key = "paragraphStartIndent"; // assumed to be left-to-right text since we don't handle DIR attribute
		else if (key == "rightmargin")
			key = "paragraphEndIndent";   // assumed to be left-to-right text since we don't handle DIR attribute
		else if (key == "indent")
			key = "textIndent";
		return super.importOneFormat(key,val);
	} 
}

class FontImporter extends TLFormatImporter
{
	public function FontImporter(classType:Class,description:Object)
	{
		super(classType,description);
	}
	
	public override function importOneFormat(key:String,val:String):Boolean
	{
		if (key == "size")
			key = "fontSize";
		else if (key == "face")
			key = "fontFamily";
		return super.importOneFormat(key,val);
	} 
}

