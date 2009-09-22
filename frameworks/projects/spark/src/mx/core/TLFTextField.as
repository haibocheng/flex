////////////////////////////////////////////////////////////////////////////////
//
//  ADOBE SYSTEMS INCORPORATED
//  Copyright 2008-2009 Adobe Systems Incorporated
//  All Rights Reserved.
//
//  NOTICE: Adobe permits you to use, modify, and distribute this file
//  in accordance with the terms of the license agreement accompanying it.
//
////////////////////////////////////////////////////////////////////////////////

package mx.core
{
	
import flash.display.DisplayObject;
import flash.display.Graphics;
import flash.display.Shape;
import flash.display.Sprite;
import flash.events.Event;
import flash.events.MouseEvent;
import flash.events.TextEvent;
import flash.geom.Rectangle;
import flash.text.StyleSheet;
import flash.text.TextFieldAutoSize;
import flash.text.TextFieldType;
import flash.text.TextFormat;
import flash.text.TextFormatAlign;
import flash.text.TextLineMetrics;
import flash.text.engine.ElementFormat;
import flash.text.engine.FontDescription;
import flash.text.engine.FontLookup;
import flash.text.engine.FontMetrics;
import flash.text.engine.FontPosture;
import flash.text.engine.FontWeight;
import flash.text.engine.Kerning;
import flash.text.engine.LineJustification;
import flash.text.engine.SpaceJustifier;
import flash.text.engine.TextBlock;
import flash.text.engine.TextElement;
import flash.text.engine.TextLine;

import flashx.textLayout.compose.ITextLineCreator;
import flashx.textLayout.compose.TextLineRecycler;
import flashx.textLayout.container.TextContainerManager;
import flashx.textLayout.conversion.ConversionType;
import flashx.textLayout.conversion.ITextExporter;
import flashx.textLayout.conversion.ITextImporter;
import flashx.textLayout.conversion.TextConverter;
import flashx.textLayout.edit.EditingMode;
import flashx.textLayout.elements.Configuration;
import flashx.textLayout.elements.LinkElement;
import flashx.textLayout.elements.TextFlow;
import flashx.textLayout.events.FlowElementMouseEvent;
import flashx.textLayout.factory.TextFlowTextLineFactory;
import flashx.textLayout.formats.ITextLayoutFormat;
import flashx.textLayout.formats.LeadingModel;
import flashx.textLayout.formats.LineBreak;
import flashx.textLayout.formats.TextDecoration;
import flashx.textLayout.formats.TextLayoutFormat;

import mx.core.mx_internal;

use namespace mx_internal;

/**
 *  TLFTextField is a Sprite which displays text by using the new
 *  Text Layout Framework to implement the old TextField API.
 *
 *  @playerversion Flash 10
 *  @playerversion AIR 1.5
 *  @langversion 3.0
 */
public class TLFTextField extends Sprite
{
	// Current slot count: 21
	// (1 for every type except 2 for Number)
	
	//--------------------------------------------------------------------------
	//
	//  Class initialization
	//
	//--------------------------------------------------------------------------
	
	/**
	 *  @private
	 */
	private static function initClass():void
	{
		var format:TextLayoutFormat;
		var config:Configuration;
		
		// Create an importer for plain text.
		plainTextImporter =
			TextConverter.getImporter(TextConverter.PLAIN_TEXT_FORMAT);
			
		// Create an exporter for plain text.
		plainTextExporter =
			TextConverter.getExporter(TextConverter.PLAIN_TEXT_FORMAT);
		
		// Create an importer for HTML_FORMAT that collapses whitespace.
		// Note: We have to make a copy of the textFlowInitialFormat,
		// which has various formats set to "inherit",
		// and then modify it and set it back.
		config = new Configuration();
		format = new TextLayoutFormat(config.textFlowInitialFormat);
		format.whiteSpaceCollapse = "collapse";
		config.textFlowInitialFormat = format;
		collapsingHTMLImporter =
			TextConverter.getImporter(TextConverter.HTML_FORMAT, config);
		collapsingHTMLImporter.throwOnError = false;
		
		// Create an importer for HTML_FORMAT that preserves whitespace.
		// Note: We have to make a copy of the textFlowInitialFormat,
		// which has various formats set to "inherit",
		// and then modify it and set it back.
		config = new Configuration();
		format = new TextLayoutFormat(config.textFlowInitialFormat);
		format.whiteSpaceCollapse = "preserve";
		config.textFlowInitialFormat = format;
		preservingHTMLImporter =
			TextConverter.getImporter(TextConverter.HTML_FORMAT, config);
		preservingHTMLImporter.throwOnError = true;
		
		// Create an exporter for HTML_FORMAT.
		htmlExporter =
			TextConverter.getExporter(TextConverter.HTML_FORMAT);
		
		if ("recreateTextLine" in staticTextBlock)
			recreateTextLine = staticTextBlock["recreateTextLine"];
	}
	
	initClass();
	
	//--------------------------------------------------------------------------
	//
	//  Class constants
	//
	//--------------------------------------------------------------------------
	
	/**
	 *  @private
	 *  TextField has fixed 2-pixel padding.
	 */
	mx_internal static const PADDING_LEFT:Number = 2;
	mx_internal static const PADDING_TOP:Number = 2;
	mx_internal static const PADDING_RIGHT:Number = 2;
	mx_internal static const PADDING_BOTTOM:Number = 2;
	
	/**
	 *  @private
	 *  This regular expression is used to replace LF with CR
	 *  when the text property is set.
	 */
	private static const ALL_LINEFEEDS:RegExp = /\n/g;
	
	/**
	 *  @private
	 *  Masks for bits inside the 'flags' var
	 *  which store the state of Boolean TextField properties.
	 */
	private static const FLAG_BACKGROUND:uint = 1 << 0;
	private static const FLAG_BORDER:uint = 1 << 1;
	private static const FLAG_CONDENSE_WHITE:uint = 1 << 2;
	private static const FLAG_EMBED_FONTS:uint = 1 << 3;
	private static const FLAG_MULTILINE:uint = 1 << 4;
	private static const FLAG_WORD_WRAP:uint = 1 << 5;
	
	/**
	 *  @private
	 *  Masks for bits inside the 'flags' var
	 *  which control what work validateNow() needs to do.
	 */
	private static const FLAG_TEXT_CHANGED:uint = 1 << 6;
	private static const FLAG_HTML_TEXT_CHANGED:uint = 1 << 7;
	private static const FLAG_TEXT_LINES_INVALID:uint = 1 << 8;
	private static const FLAG_GRAPHICS_INVALID:uint = 1 << 9;
	
	/**
	 *  @private
	 *  Masks for bits inside the 'flags' var
	 *  tracking misc boolean variables.
	 */
	private static const FLAG_VALIDATE_IN_PROGRESS:uint = 1 << 10;
	private static const FLAG_USE_TCM:uint = 1 << 11;
	private static const FLAG_HAS_SCROLL_RECT:uint = 1 << 12;
		// FIXME (gosmith): should be set in override of set scrollRect?

	// FIXME (gosmith): Does TextField maintain
	// an internal vs. external concept of scrollRect?
		
	/**
	 *  @private
	 */
	private static const ALL_INVALIDATION_FLAGS:uint =
		FLAG_TEXT_CHANGED |
		FLAG_HTML_TEXT_CHANGED |
		FLAG_TEXT_LINES_INVALID |
		FLAG_GRAPHICS_INVALID;
	
	//--------------------------------------------------------------------------
	//
	//  Class variables
	//
	//--------------------------------------------------------------------------
	
	/**
	 *  @private
	 */
	private static var plainTextImporter:ITextImporter;
	
	/**
	 *  @private
	 */
	private static var plainTextExporter:ITextExporter;
	
	/**
	 *  @private
	 */
	private static var collapsingHTMLImporter:ITextImporter;
	
	/**
	 *  @private
	 */
	private static var preservingHTMLImporter:ITextImporter;
	
	/**
	 *  @private
	 */
	private static var htmlExporter:ITextExporter =
		TextConverter.getExporter(TextConverter.HTML_FORMAT);
	
	/**
	 *  @private
	 */
	private static var factory:TextFlowTextLineFactory =
		new TextFlowTextLineFactory();
	
	// We can re-use single instances of a few FTE classes over and over,
	// since they just serve as a factory for the TextLines that we care about.
	
	/**
	 *  @private
	 */
	private static var staticTextBlock:TextBlock = new TextBlock();
	
	/**
	 *  @private
	 */
	private static var staticTextElement:TextElement = new TextElement();
	
	/**
	 *  @private
	 */
	private static var staticSpaceJustifier:SpaceJustifier =
		new SpaceJustifier();
	
	/**
	 *  @private
	 *  A reference to the recreateTextLine() method in staticTextBlock,
	 *  if it exists. This method was added in player 10.1.
	 *  It allows better performance by making it possible to reuse
	 *  existing TextLines instead of having to create new ones.
	 */
	private static var recreateTextLine:Function;
	
	//--------------------------------------------------------------------------
	//
	//  Class methods
	//
	//--------------------------------------------------------------------------
	
	/**
	 *  @private
	 */
	private static function rint(x:Number):Number
	{
		var i:Number = Math.round(x);
		if (i - 0.5 == x && i & 1)
			--i;
		return i;
	}
	
	/**
	 *  @private
	 */
	private static function createDefaultTextFormat():TextFormat
	{
		var textFormat:TextFormat = new TextFormat(
			"Times New Roman",		// font // FIXME (gosmith): platform-dependent?
			12,						// size
			0x000000,				// color
			false,					// bold
			false,					// italic
			false,					// underline				
			"",						// url
			"",						// target
			TextFormatAlign.LEFT,	// align
			0,						// leftMargin
			0,						// rightMargin
			0,						// indent
			0);						// leading

		textFormat.blockIndent = 0;
		textFormat.bullet = false;
		textFormat.kerning = false;
		textFormat.letterSpacing = 0;
		textFormat.tabStops = [];
			// FIXME (gosmith): Flash apparently detects when an empty array
			// is assigned to tabStops and assigns null instead.
		
		return textFormat;
	}
	
	/**
	 *  @private
	 */
	private static function cloneTextFormat(
								textFormat:TextFormat):TextFormat
	{
		var newTextFormat:TextFormat = new TextFormat(
			textFormat.font, textFormat.size, textFormat.color,
			textFormat.bold, textFormat.italic, textFormat.underline,
			textFormat.url, textFormat.target, textFormat.align,
			textFormat.leftMargin, textFormat.rightMargin, textFormat.indent,
			textFormat.leading);
		
		newTextFormat.blockIndent = textFormat.blockIndent;
		newTextFormat.bullet = textFormat.bullet;
		newTextFormat.kerning = textFormat.kerning;
		newTextFormat.letterSpacing = textFormat.letterSpacing;
		newTextFormat.tabStops = textFormat.tabStops;
		
		return newTextFormat;
	}
	
	/**
	 *  @private
	 */
	private static function applyTextFormat(src:TextFormat, dst:TextFormat):void
	{
		if (src.align != null)
			dst.align = src.align;
		
		if (src.blockIndent != null)
			dst.blockIndent = src.blockIndent;
			
		if (src.bold != null)
			dst.bold = src.bold;
			
		if (src.bullet != null)
			dst.bullet = src.bullet;
			
		if (src.color != null)
			dst.color = src.color;
			
		if (src.font != null)
			dst.font = src.font;
			
		if (src.indent != null)
			dst.indent = src.indent;
			
		if (src.italic != null)
			dst.italic = src.italic;
			
		if (src.kerning != null)
			dst.kerning != src.kerning;
			
		if (src.leading != null)
			dst.leading = src.leading;
			
		if (src.leftMargin != null)
			dst.leftMargin = src.leftMargin;
			
		if (src.letterSpacing != null)
			dst.letterSpacing = src.letterSpacing;
			
		if (src.rightMargin != null)
			dst.rightMargin = src.rightMargin;
			
		if (src.size != null)
			dst.size = src.size;
			
		if (src.tabStops != null)
			dst.tabStops = src.tabStops;
			
		if (src.target != null)
			dst.target = src.target;
			
		if (src.underline != null)
			dst.underline = src.underline;
			
		if (src.url != null)
			dst.url = src.url;
	}
	
	//--------------------------------------------------------------------------
	//
	//  Constructor
	//
	//--------------------------------------------------------------------------
	
	/**
	 *  Constructor.
	 * 
	 *  @playerversion Flash 10
	 *  @playerversion AIR 1.5
	 *  @langversion 3.0
	 */
	public function TLFTextField()
	{
		super();
		
		// The mouse should not be aware of the TextLines.
		// Otherwise, TLFTextField will dispatch mouseOver and mouseOut
		// events over each line, thich TextField doesn't do.
		mouseChildren = false;
		
		_defaultTextFormat = createDefaultTextFormat();
		
		addEventListener(Event.ADDED_TO_STAGE, addedToStageHandler);
	}
	
	//--------------------------------------------------------------------------
	//
	//  Variables
	//
	//--------------------------------------------------------------------------
	
	/**
	 *  @private
	 *  Apps are likely to create thousands of instances of TLFTextField,
	 *  so in order to minimize memory usage we store flags as 1 bit
	 *  inside a uint instead of making each one a 4-byte Boolean var.
	 */
	private var flags:uint = 0;
	
	/**
	 *  @private
	 *  When we render the text using FTE,
	 *  this object represents the formatting for FTE.
	 *  Every time the defaultTextFormat is set,
	 *  this object is released because it is invalid.
	 *  It is regenerated just in time to render the text.
	 */
	private var elementFormat:ElementFormat;
	
	/**
	 *  @private
	 *  When we render the htmlText using TLF,
	 *  this object represents the formatting for TLF.
	 *  Every time the defaultTextFormat is set,
	 *  this object is released because it is invalid.
	 *  It is regenerated just in time to render the htmlText.
	 */
	private var hostFormat:ITextLayoutFormat;
	
	/**
	 *  @private
	 *  When we render the htmlText using TLF,
	 *  this object represents the rich text to be displayed.
	 *  It is created by using TLF's HTML importer to import the htmlText.
	 */
	private var textFlow:TextFlow;
	
	/**
	 *  @private
	 *  When we render the htmlText using TLF,
	 *  this object composes the textFlow
	 *  (with the hostFormat applied to it)
	 *  to create TextLines in this Sprite.
	 */
	private var textContainerManager:TextContainerManager;
	
	//--------------------------------------------------------------------------
	//
	//  Overridden properties: DisplayObject
	//
	//--------------------------------------------------------------------------
	
	//----------------------------------
	//  height
	//----------------------------------
	
	/**
	 *  @private
	 */
	private var _height:Number = 100;
	
	/**
	 *  @private
	 */
	override public function get height():Number
	{
		// If we're autosizing, _height may be invalid.
		// For example, the 'text' may have been set
		// but the TextLines for that text haven't
		// been created yet.
		if (autoSize != TextFieldAutoSize.NONE)
			validateNow();
		
		return _height;
	}
	
	/**
	 *  @private
	 */
	override public function set height(value:Number):void
	{
		// TextField ignores NaN and negative values.
		if (isNaN(value) || value < 0)
			return;
				
		if (value == _height)
			return;
		
		_height = value;
		
		// The TextLines may need to be recreated
		// and the border and background may need to be redrawn.
		setFlag(FLAG_TEXT_LINES_INVALID |
				FLAG_GRAPHICS_INVALID);
				
		invalidate();
	}
	
	//----------------------------------
	//  width
	//----------------------------------
	
	/**
	 *  @private
	 */
	private var _width:Number = 100;
	
	/**
	 *  @private
	 */
	override public function get width():Number
	{
		// If we're autosizing, _width may be invalid.
		// For example, the 'text' may have been set
		// but the TextLines for that text haven't
		// been created yet.
		if (autoSize != TextFieldAutoSize.NONE)
			validateNow();
		
		return _width;
	}
	
	/**
	 *  @private
	 */
	override public function set width(value:Number):void
	{
		// TextField ignores NaN and negative values.
		if (isNaN(value) || value < 0)
			return;
		
		if (value == _width)
			return;
		
		_width = value;
		
		// The TextLines may need to be recreated
		// and the border and background may need to be redrawn.
		setFlag(FLAG_TEXT_LINES_INVALID |
				FLAG_GRAPHICS_INVALID);
				
		invalidate();
	}
	
	//--------------------------------------------------------------------------
	//
	//  Properties: TextField
	//
	//--------------------------------------------------------------------------
	
	//----------------------------------
	//  alwaysShowSelection
	//----------------------------------
	
	/**
	 *  This property is not implemented in TLFTextField
	 *  because TLFTextField does not support selection.
	 *  Accessing it will throw a runtime error.
	 * 
	 *  @see flash.text.TextField#alwaysShowSelection
	 * 
	 *  @playerversion Flash 10
	 *  @playerversion AIR 1.5
	 *  @langversion 3.0
	 */
	public function get alwaysShowSelection():Boolean
	{
		throw new Error(notImplemented("alwaysShowSelection"));
	}
	
	/**
	 *  @private
	 */
	public function set alwaysShowSelection(value:Boolean):void
	{
		throw new Error(notImplemented("alwaysShowSelection"));
	}
	
	//----------------------------------
	//  antiAliasType
	//----------------------------------
	
	/**
	 *  This property has no effect in TLFTextField.
	 *  because FTE uses a newer font renderer than TextField.
	 *  Getting it will always return <code>null</code>
	 *  and setting it will do nothing.
	 *  
	 *  @see flash.text.TextField#antiAliasType
	 * 
	 *  @playerversion Flash 10
	 *  @playerversion AIR 1.5
	 *  @langversion 3.0
	 */
	public function get antiAliasType():String
	{
		return null;
	}
	
	/**
	 *  @private
	 */
	public function set antiAliasType(value:String):void
	{
	}
	
	//----------------------------------
	//  autoSize
	//----------------------------------
	
	/**
	 *  @private
	 */
	private var _autoSize:String = TextFieldAutoSize.NONE;
	
	/**
	 *  @copy flash.text.TextField#autoSize
	 * 
	 *  @playerversion Flash 10
	 *  @playerversion AIR 1.5
	 *  @langversion 3.0
	 */
	public function get autoSize():String
	{
		return _autoSize;
	}
	
	/**
	 *  @private
	 */
	public function set autoSize(value:String):void
	{
		// TextField throws this RTE when invalid values are set.
		if (value != TextFieldAutoSize.NONE &&
			value != TextFieldAutoSize.LEFT &&
			value != TextFieldAutoSize.CENTER &&
			value != TextFieldAutoSize.RIGHT)
		{
			throw new ArgumentError("Parameter autoSize must be one of the accepted values.");
		}
		
		if (value == autoSize)
			return;
		
		_autoSize = value;
		
		// The TextLines may need to be recreated
		// and the border and background may need to be redrawn.
		setFlag(FLAG_TEXT_LINES_INVALID |
				FLAG_GRAPHICS_INVALID);
		
		invalidate();
	}
	
	//----------------------------------
	//  background
	//----------------------------------
	
	/**
	 *  @copy flash.text.TextField#background
	 * 
	 *  @playerversion Flash 10
	 *  @playerversion AIR 1.5
	 *  @langversion 3.0
	 */
	public function get background():Boolean
	{
		return testFlag(FLAG_BACKGROUND);
	}
	
	/**
	 *  @private
	 */
	public function set background(value:Boolean):void
	{
		if (value == background)
			return;

		setFlagToValue(FLAG_BACKGROUND, value);
		
		// The border and background need to be redrawn.
		setFlag(FLAG_GRAPHICS_INVALID);
		
		invalidate();
	}
	
	//----------------------------------
	//  backgroundColor
	//----------------------------------
	
	/**
	 *  @private
	 */
	private var _backgroundColor:uint = 0xFFFFFF;
	
	/**
	 *  @copy flash.text.TextField#backgroundColor
	 * 
	 *  @playerversion Flash 10
	 *  @playerversion AIR 1.5
	 *  @langversion 3.0
	 */
	public function get backgroundColor():uint
	{
		return _backgroundColor;
	}
	
	/**
	 *  @private
	 */
	public function set backgroundColor(value:uint):void
	{
		if (value == _backgroundColor)
			return;
		
		_backgroundColor = value;
		
		// The border and background need to be redrawn.
		setFlag(FLAG_GRAPHICS_INVALID);
		
		invalidate();
	}
	
	//----------------------------------
	//  border
	//----------------------------------
	
	/**
	 *  @copy flash.text.TextField#border
	 * 
	 *  @playerversion Flash 10
	 *  @playerversion AIR 1.5
	 *  @langversion 3.0
	 */
	public function get border():Boolean
	{
		return testFlag(FLAG_BORDER);
	}
	
	/**
	 *  @private
	 */
	public function set border(value:Boolean):void
	{
		if (value == border)
			return;
		
		setFlagToValue(FLAG_BORDER,value);
		
		// The border and background need to be redrawn.
		setFlag(FLAG_GRAPHICS_INVALID);
		
		invalidate();
	}
	
	//----------------------------------
	//  borderColor
	//----------------------------------
	
	/**
	 *  @private
	 */
	private var _borderColor:uint = 0x000000;
	
	/**
	 *  @copy flash.text.TextField#borderColor
	 * 
	 *  @playerversion Flash 10
	 *  @playerversion AIR 1.5
	 *  @langversion 3.0
	 */
	public function get borderColor():uint
	{
		return _borderColor;
	}
	
	/**
	 *  @private
	 */
	public function set borderColor(value:uint):void
	{
		if (value == _borderColor)
			return;
		
		_borderColor = value;
		
		// The border and background need to be redrawn.
		setFlag(FLAG_GRAPHICS_INVALID);
		
		invalidate();
	}
	
	//----------------------------------
	//  bottomScrollV
	//----------------------------------
	
	/**
	 *  This property has not been implemented in TLFTextField
	 *  because TLFTextField does not support scrolling.
	 *  Accessing it will throw a runtime error.
	 *
	 *  @see flash.text.TextField#bottomScrollV
	 * 
	 *  @playerversion Flash 10
	 *  @playerversion AIR 1.5
	 *  @langversion 3.0
	 */
	public function get bottomScrollV():int
	{
		throw new Error(notImplemented("bottomScrollV"));
	}
	
	//----------------------------------
	//  caretIndex
	//----------------------------------
	
	/**
	 *  This property has not been implemented in TLFTextField
	 *  because TLFTextField does not support editing.
	 *  Accessing it will throw a runtime error.
	 *
	 *  @copy flash.text.TextField#caretIndex
	 * 
	 *  @playerversion Flash 10
	 *  @playerversion AIR 1.5
	 *  @langversion 3.0
	 */
	public function get caretIndex():int
	{
		throw new Error(notImplemented("caretIndex"));
	}
	
	//----------------------------------
	//  condenseWhite
	//----------------------------------
	
	/**
	 *  @copy flash.text.TextField#condenseWhite
	 * 
	 *  @playerversion Flash 10
	 *  @playerversion AIR 1.5
	 *  @langversion 3.0
	 */
	public function get condenseWhite():Boolean
	{
		return testFlag(FLAG_CONDENSE_WHITE);
	}
	
	/**
	 *  @private
	 */
	public function set condenseWhite(value:Boolean):void
	{
		setFlagToValue(FLAG_CONDENSE_WHITE, value);

		// FIXME (gosmith): Need to cache this so that if you then set
		// htmlText and change condenseWhite, the old value is used.
		
		// Note: There is nothing else to do immediately;
		// the new value doesn't have any effect
		// until 'htmlText' is set later.
	}
	
	//----------------------------------
	//  defaultTextFormat
	//----------------------------------
	
	/**
	 *  @private
	 *  Storage for the defaultTextFormat property.
	 *  This variable is initialized in the constructor
	 *  to a TextFormat instance filled with default values.
	 *  The setter applies non-null incoming formats
	 *  to the object stored here.
	 *  The getter returns a copy of the object stored here.
	 *  Note that No field of this TextFormat will ever be null.
	 */
	mx_internal var _defaultTextFormat:TextFormat;
	
	/**
	 *  @copy flash.text.TextField#defaultTextFormat
	 * 
	 *  @playerversion Flash 10
	 *  @playerversion AIR 1.5
	 *  @langversion 3.0
	 */
	public function get defaultTextFormat():TextFormat
	{
		// TextField returns a new TextFormat instance each time
		// you access defaultTextFormat; the proof is that
		//   textField.defaultTextFormat != textField.defaultTextFormat
		// is true.
		return cloneTextFormat(_defaultTextFormat);    	
	}
	
	/**
	 *  @private
	 */
	public function set defaultTextFormat(value:TextFormat):void
	{
		// TextField throws this RTE if a null value is set.
		if (!value)
			throw new TypeError("Parameter format must be non-null.");
		
		// Apply non-null formats in the incoming TextFormat
		// to the defaultTextFormat.
		applyTextFormat(value, _defaultTextFormat);
		
		// These FTE and TLF formatting objects are now invalid
		// and must be recreated when needed.
		elementFormat = null;
		hostFormat = null;
		
		// Note: Setting this does NOT cause already-rendered text
		// to change its format.
		// If establishes the formatting for text set or added later.
	}
	
	//----------------------------------
	//  displayAsPassword
	//----------------------------------
	
	/**
	 *  This property has not been implemented in TLFTextField
	 *  because TLFTextField does not support editing.
	 *  Accessing it will throw a runtime error.
	 *
	 *  @copy flash.text.TextField#displayAsPassword
	 * 
	 *  @playerversion Flash 10
	 *  @playerversion AIR 1.5
	 *  @langversion 3.0
	 */
	public function get displayAsPassword():Boolean
	{
		throw new Error(notImplemented("displayAsPassword"));
	}
	
	/**
	 *  @private
	 */
	public function set displayAsPassword(value:Boolean):void
	{
		throw new Error(notImplemented("displayAsPassword"));
	}
	
	//----------------------------------
	//  embedFonts
	//----------------------------------
	
	/**
	 *  @copy flash.text.TextField#embedFonts
	 * 
	 *  @playerversion Flash 10
	 *  @playerversion AIR 1.5
	 *  @langversion 3.0
	 */
	public function get embedFonts():Boolean
	{
		return testFlag(FLAG_EMBED_FONTS);
	}
	
	/**
	 *  @private
	 */
	public function set embedFonts(value:Boolean):void
	{
		if (value == embedFonts)
			return;
		
		setFlagToValue(FLAG_EMBED_FONTS, value);
		
		// These FTE and TLF formatting objects are now invalid
		// and must be recreated when needed.
		elementFormat = null;
		hostFormat = null;

		// The TextLines may need to be recreated
		// and the border and background may need to be redrawn.
		setFlag(FLAG_TEXT_LINES_INVALID |
				FLAG_GRAPHICS_INVALID);
				
		invalidate();
	}
	
	//----------------------------------
	//  gridFitType
	//----------------------------------
	
	/**
	 *  This property has no effect in TLFTextField.
	 *  because FTE uses a newer font renderer than TextField.
	 *  Getting it will always return <code>null</code>
	 *  and setting it will do nothing.
	 *  
	 *  @see flash.text.TextField#gridFitType
	 * 
	 *  @playerversion Flash 10
	 *  @playerversion AIR 1.5
	 *  @langversion 3.0
	 */
	public function get gridFitType():String
	{
		return null;
	}
	
	/**
	 *  @private
	 */
	public function set gridFitType(value:String):void
	{
	}
	
	//----------------------------------
	//  htmlText
	//----------------------------------
	
	/**
	 *  @private
	 */
	private var _htmlText:String = null;
	
	/**
	 *  @copy flash.text.TextField#htmlText
	 * 
	 *  @playerversion Flash 10
	 *  @playerversion AIR 1.5
	 *  @langversion 3.0
	 */
	public function get htmlText():String
	{
		// FIXME (gosmith): Explain this.
		if (_htmlText == null)
		{
			if (_text == "")
			{
				_htmlText = "";
			}
			else
			{
				if (!textFlow)
					textFlow = plainTextImporter.importToFlow(_text);
				_htmlText = String(htmlExporter.export(
					textFlow, ConversionType.STRING_TYPE));

				// FIXME (gosmith): Are newlines \r or \n, or none?
			}
		}	
		
		return _htmlText;
	}
	
	/**
	 *  @private
	 */
	public function set htmlText(value:String):void
	{
		// TextField throws this RTE if a null value is set.
		// It seems like this should say
		//   "Parameter htmlText must be non-null",
		// but that's not what TextField does.
		if (value == null)
		{
			throw new TypeError("Parameter text must be non-null.");
		}
		
		// FIXME (gosmith): Explain why we don't do this.		
		//    	if (value == htmlText)
		//    		return;
		
		_htmlText = value;
		
		// _text is now invalid and will get regenerated on demand.
		_text = null;
		
		clearFlag(FLAG_TEXT_CHANGED);
		
		setFlag(FLAG_HTML_TEXT_CHANGED |
				FLAG_TEXT_LINES_INVALID |
				FLAG_GRAPHICS_INVALID);
		
		invalidate();
		
		// NOTE: With hmtlText, what you set is NOT what you get.
		// You can set incomplete (or no) markup
		// and get back complete markup.
	}
	
	//----------------------------------
	//  length
	//----------------------------------
	
	/**
	 *  @copy flash.text.TextField#length
	 * 
	 *  @playerversion Flash 10
	 *  @playerversion AIR 1.5
	 *  @langversion 3.0
	 */
	public function get length():int
	{
		return text.length;
	}
	
	//----------------------------------
	//  maxChars
	//----------------------------------
	
	/**
	 *  This property has not been implemented in TLFTextField
	 *  because TLFTextField does not support editing.
	 *  Accessing it will throw a runtime error.
	 *
	 *  @see flash.text.TextField#maxChars
	 * 
	 *  @playerversion Flash 10
	 *  @playerversion AIR 1.5
	 *  @langversion 3.0
	 */
	public function get maxChars():int
	{
		throw new Error(notImplemented("maxChars"));
	}
	
	/**
	 *  @private
	 */
	public function set maxChars(value:int):void
	{
		throw new Error(notImplemented("maxChars"));
	}
	
	//----------------------------------
	//  maxScrollH
	//----------------------------------
	
	/**
	 *  This property has not been implemented in TLFTextField
	 *  because TLFTextField does not support scrolling.
	 *  Accessing it will throw a runtime error.
	 *
	 *  @see flash.text.TextField#maxScrollH
	 * 
	 *  @playerversion Flash 10
	 *  @playerversion AIR 1.5
	 *  @langversion 3.0
	 */
	public function get maxScrollH():int
	{
		throw new Error(notImplemented("maxScrollH"));
	}
	
	//----------------------------------
	//  maxScrollV
	//----------------------------------
	
	/**
	 *  This property has not been implemented in TLFTextField
	 *  because TLFTextField does not support scrolling.
	 *  Accessing it will throw a runtime error.
	 *
	 *  @see flash.text.TextField#maxScrollV
	 * 
	 *  @playerversion Flash 10
	 *  @playerversion AIR 1.5
	 *  @langversion 3.0
	 */
	public function get maxScrollV():int
	{
		throw new Error(notImplemented("maxScrollV"));
	}
	
	//----------------------------------
	//  mouseWheelEnabled
	//----------------------------------
	
	/**
	 *  This property has not been implemented in TLFTextField
	 *  because TLFTextField does not support scrolling.
	 *  Getting it will always return <code>false</code>
	 *  and setting it will do nothing.
	 *
	 *  @see flash.text.TextField#mouseWheelEnabled
	 * 
	 *  @playerversion Flash 10
	 *  @playerversion AIR 1.5
	 *  @langversion 3.0
	 */
	public function get mouseWheelEnabled():Boolean
	{
		return false;
	}
	
	/**
	 *  @private
	 */
	public function set mouseWheelEnabled(value:Boolean):void
	{
	}
	
	//----------------------------------
	//  multiline
	//----------------------------------
	
	/**
	 *  This property has no effect in TLFTextField
	 *  because TLFTextField does not support editing.
	 *  However, you can get and set it.
	 *
	 *  @see flash.text.TextField#multiline
	 * 
	 *  @playerversion Flash 10
	 *  @playerversion AIR 1.5
	 *  @langversion 3.0
	 */
	public function get multiline():Boolean
	{
		return testFlag(FLAG_MULTILINE);
	}
	
	/**
	 *  @private
	 */
	public function set multiline(value:Boolean):void
	{
		setFlagToValue(FLAG_MULTILINE, value);
	}
	
	//----------------------------------
	//  numLines
	//----------------------------------
	
	/**
	 *  @copy flash.text.TextField#numLines
	 * 
	 *  @playerversion Flash 10
	 *  @playerversion AIR 1.5
	 *  @langversion 3.0
	 */
	public function get numLines():int
	{
		validateNow();
		
		// All the of the children of this Sprite are TextLines,
		// so the number of lines is the number of children.
		// TextContainerManager can create Shapes as well,
		// but only when using TLF's backgroundColor and backgroundAlpha
		// formatting on spans, which TLFTextField doesn't use.
		return numChildren;
	}
	
	//----------------------------------
	//  restrict
	//----------------------------------
	
	/**
	 *  This property has not been implemented in TLFTextField
	 *  because TLFTextField does not support scrolling.
	 *  Accessing it will throw a runtime error.
	 *  
	 *  @see flash.text.TextField#restrict
	 * 
	 *  @playerversion Flash 10
	 *  @playerversion AIR 1.5
	 *  @langversion 3.0
	 */
	public function get restrict():String
	{
		throw new Error(notImplemented("restrict"));
	}
	
	/**
	 *  @private
	 */
	public function set restrict(value:String):void
	{
		throw new Error(notImplemented("restrict"));
	}
	
	//----------------------------------
	//  scrollH
	//----------------------------------
	
	/**
	 *  This property has not been implemented in TLFTextField
	 *  because TLFTextField does not support scrolling.
	 *  Accessing it will throw a runtime error.
	 *
	 *  @see flash.text.TextField#scrollH
	 * 
	 *  @playerversion Flash 10
	 *  @playerversion AIR 1.5
	 *  @langversion 3.0
	 */
	public function get scrollH():int
	{
		throw new Error(notImplemented("scrollH"));
	}
	
	/**
	 *  @private
	 */
	public function set scrollH(value:int):void
	{
		throw new Error(notImplemented("scrollH"));
	}
	
	//----------------------------------
	//  scrollV
	//----------------------------------
	
	/**
	 *  This property has not been implemented in TLFTextField
	 *  because TLFTextField does not support scrolling.
	 *  Accessing it will throw a runtime error.
	 *
	 *  @copy flash.text.TextField#scrollV
	 * 
	 *  @playerversion Flash 10
	 *  @playerversion AIR 1.5
	 *  @langversion 3.0
	 */
	public function get scrollV():int
	{
		throw new Error(notImplemented("scrollV"));
	}
	
	/**
	 *  @private
	 */
	public function set scrollV(value:int):void
	{
		throw new Error(notImplemented("scrollV"));
	}
	
	//----------------------------------
	//  selectable
	//----------------------------------
	
	/**
	 *  Setting this property has no effect in TLFTextField
	 *  because TLFTextField does not support selection.
	 *  If you get it, it will always be <code>false</code>.
	 *
	 *  @see flash.text.TextField#selectable
	 * 
	 *  @playerversion Flash 10
	 *  @playerversion AIR 1.5
	 *  @langversion 3.0
	 */
	public function get selectable():Boolean
	{
		return false;
	}
	
	/**
	 *  @private
	 */
	public function set selectable(value:Boolean):void
	{
	}
	
	//----------------------------------
	//  selectionBeginIndex
	//----------------------------------
	
	/**
	 *  This property has not been implemented in TLFTextField
	 *  because TLFTextField does not support selection.
	 *  Accessing it will throw a runtime error.
	 *
	 *  @see flash.text.TextField#selectionBeginIndex
	 * 
	 *  @playerversion Flash 10
	 *  @playerversion AIR 1.5
	 *  @langversion 3.0
	 */
	public function get selectionBeginIndex():int
	{
		throw new Error(notImplemented("selectionBeginIndex"));
	}
	
	//----------------------------------
	//  selectionEndIndex
	//----------------------------------
	
	/**
	 *  This property has not been implemented in TLFTextField
	 *  because TLFTextField does not support selection.
	 *  Accessing it will throw a runtime error.
	 * 
	 *  @see flash.text.TextField#selectionEndIndex
	 * 
	 *  @playerversion Flash 10
	 *  @playerversion AIR 1.5
	 *  @langversion 3.0
	 */
	public function get selectionEndIndex():int
	{
		throw new Error(notImplemented("selectionEndIndex"));
	}
	
	//----------------------------------
	//  sharpness
	//----------------------------------
	
	/**
	 *  This property has no effect in TLFTextField.
	 *  because FTE uses a newer font renderer than TextField.
	 *  Getting it will always return <code>NaN</code>
	 *  and setting it will do nothing.
	 *  
	 *  @see flash.text.TextField#sharpness
	 * 
	 *  @playerversion Flash 10
	 *  @playerversion AIR 1.5
	 *  @langversion 3.0
	 */
	public function get sharpness():Number
	{
		return NaN;
	}
	
	/**
	 *  @private
	 */
	public function set sharpness(value:Number):void
	{
	}
	
	//----------------------------------
	//  styleSheet
	//----------------------------------
	
	/**
	 *  @private
	 */
	private var _styleSheet:StyleSheet = null;
	
	/**
	 *  @copy flash.text.TextField#styleSheet
	 * 
	 *  @playerversion Flash 10
	 *  @playerversion AIR 1.5
	 *  @langversion 3.0
	 */
	public function get styleSheet():StyleSheet
	{
		return _styleSheet; // FIXME (gosmith): return copy?
	}
	
	/**
	 *  @private
	 */
	public function set styleSheet(value:StyleSheet):void
	{
		// TextField allows a null value to be set;
		// in fact, this is the default.
		
		// FIXME (gosmith): Explain why we don't do this.
		//if (value == _styleSheet)
		//	return;
		
		_styleSheet = value;
		
		// The TextLines may need to be recreated
		// and the border and background may need to be redrawn.
		setFlag(FLAG_TEXT_LINES_INVALID |
				FLAG_GRAPHICS_INVALID);
				
		invalidate();
	}
	
	//----------------------------------
	//  text
	//----------------------------------
	
	/**
	 *  @private
	 */
	private var _text:String = "";
	
	/**
	 *  @copy flash.text.TextField#text
	 * 
	 *  @playerversion Flash 10
	 *  @playerversion AIR 1.5
	 *  @langversion 3.0
	 */
	public function get text():String
	{
		// Explain this.
		if (_text == null)
		{
			if (!textFlow)
				textFlow = htmlImporter.importToFlow(_htmlText);

			_text = String(plainTextExporter.export(
				textFlow, ConversionType.STRING_TYPE));

			// FIXME (gosmith): line endings should be changed from \n to \r?
		}
			
		return _text;
	}
	
	/**
	 *  @private
	 */
	public function set text(value:String):void
	{
		// TextField throws this RTE if a null value is set.
		if (value == null)
			throw new TypeError("Parameter text must be non-null.");
		
		// FIXME (gosmith)
		
		// TextField turns all LF characters into CR characters,
		// including treating the Windows line-ending-sequence
		// CR+LF as two CRs.
		_text = value.replace(ALL_LINEFEEDS, "\r");
		
		// _htmlText is now invalid and will get regenerated on demand
		_htmlText = null;
		
		clearFlag(FLAG_HTML_TEXT_CHANGED);
		
		setFlag(FLAG_TEXT_CHANGED |
				FLAG_TEXT_LINES_INVALID |
				FLAG_GRAPHICS_INVALID);
		
		invalidate();
	}
	
	//----------------------------------
	//  textColor
	//----------------------------------
	
	/**
	 *  @copy flash.text.TextField#textColor
	 * 
	 *  @playerversion Flash 10
	 *  @playerversion AIR 1.5
	 *  @langversion 3.0
	 */
	public function get textColor():uint
	{
		// textColor is not an independent format in TextField;
		// getting textColor simply returns the color
		// in the defaultTextFormat.
		return uint(_defaultTextFormat.color);
	}
	
	/**
	 *  @private
	 *  Setting the textColor changes the color in the defaultTextFormat
	 *  and redraws the text in the new color.
	 */
	public function set textColor(value:uint):void
	{
		// FIXME (gosmith): Should this be commented out?
		//if (value == _textColor)
		//	return;
		
		_defaultTextFormat.color = value;
		
		setFlag(FLAG_TEXT_LINES_INVALID);
		
		invalidate();
	}
	
	//----------------------------------
	//  textHeight
	//----------------------------------
	
	/**
	 *  @private
	 */
	private var _textHeight:Number = 0;
	
	/**
	 *  @copy flash.text.TextField#textHeight
	 * 
	 *  @playerversion Flash 10
	 *  @playerversion AIR 1.5
	 *  @langversion 3.0
	 */
	public function get textHeight():Number
	{
		validateNow();
		
		return _textHeight;
	}
	
	//----------------------------------
	//  textWidth
	//----------------------------------
	
	/**
	 *  @private
	 */
	private var _textWidth:Number = 0;
	
	/**
	 *  @copy flash.text.TextField#textWidth
	 * 
	 *  @playerversion Flash 10
	 *  @playerversion AIR 1.5
	 *  @langversion 3.0
	 */
	public function get textWidth():Number
	{
		validateNow();
		
		return _textWidth;
	}
	
	//----------------------------------
	//  thickness
	//----------------------------------
	
	/**
	 *  This property has no effect in TLFTextField.
	 *  because FTE uses a newer font renderer than TextField.
	 *  Getting it will always return <code>NaN</code>
	 *  and setting it will do nothing.
	 *  
	 *  @see flash.text.TextField#thickness
	 * 
	 *  @playerversion Flash 10
	 *  @playerversion AIR 1.5
	 *  @langversion 3.0
	 */
	public function get thickness():Number
	{
		return NaN;
	}
	
	/**
	 *  @private
	 */
	public function set thickness(value:Number):void
	{
	}
	
	//----------------------------------
	//  type
	//----------------------------------
	
	/**
	 *  @copy flash.text.TextField#type
	 * 
	 *  @playerversion Flash 10
	 *  @playerversion AIR 1.5
	 *  @langversion 3.0
	 */
	public function get type():String
	{
		return TextFieldType.DYNAMIC;
	}
	
	/**
	 *  @private
	 */
	public function set type(value:String):void
	{
		// TextField throws this RTE when invalid values are set.
		if (value != TextFieldType.DYNAMIC &&
			value != TextFieldType.INPUT)
		{
			throw new ArgumentError("Parameter type must be one of the accepted values.");
		}
		
		if (value == TextFieldType.INPUT)
			throw new Error("FIXME (gosmith)");
	}
	
	//----------------------------------
	//  useRichTextClipboard
	//----------------------------------
	
	/**
	 *  This property is not implemented in TLFTextField
	 *  because TLFTextField does not support selection
	 *  or clipboard operations.
	 *  Accessing it will throw a runtime error.
	 *  
	 *  @copy flash.text.TextField#useRichTextClipboard
	 * 
	 *  @playerversion Flash 10
	 *  @playerversion AIR 1.5
	 *  @langversion 3.0
	 */
	public function get useRichTextClipboard():Boolean
	{
		throw new Error(notImplemented("useRichTextClipboard"));
	}

	/**
	 *  @private
	 */
	public function set useRichTextClipboard(value:Boolean):void
	{
		throw new Error(notImplemented("useRichTextClipboard"));
	}
	
	//----------------------------------
	//  wordWrap
	//----------------------------------
	
	/**
	 *  @copy flash.text.TextField#wordWrap
	 * 
	 *  @playerversion Flash 10
	 *  @playerversion AIR 1.5
	 *  @langversion 3.0
	 */
	public function get wordWrap():Boolean
	{
		return testFlag(FLAG_WORD_WRAP);
	}
	
	/**
	 *  @private
	 */
	public function set wordWrap(value:Boolean):void
	{
		if (value == wordWrap)
			return;
		
		setFlagToValue(FLAG_WORD_WRAP, value);
		
		// These FTE and TLF formatting objects are now invalid
		// and must be recreated when needed.
		elementFormat = null;
		hostFormat = null;
		
		// The TextLines may need to be recreated
		// and the border and background may need to be redrawn.
		setFlag(FLAG_TEXT_LINES_INVALID |
				FLAG_GRAPHICS_INVALID);
		
		invalidate();
	}
	
	//--------------------------------------------------------------------------
	//
	//  Properties
	//
	//--------------------------------------------------------------------------
	
	//----------------------------------
	//  direction
	//----------------------------------
	
	/**
	 *  @private
	 *  Storage for the direction property.
	 */
	private var _direction:String = "ltr";
	
	/**
	 *  The directionality of the text displayed by TLFTextField.
	 * 
	 *  <p>The allowed values are <code>"ltr"</code> for left-to-right text,
	 *  as in Latin-style scripts,
	 *  and <code>"rtl"</code> for right-to-left text,
	 *  as in Arabic and Hebrew.</p>
	 * 
	 *  <p>Note: This property does not exist in the classic
	 *  flash.text.TextField API.</p>
	 *
	 *  @default "ltr"
	 * 
	 *  @playerversion Flash 10
	 *  @playerversion AIR 1.5
	 *  @langversion 3.0
	 */
	public function get direction():String
	{
		return _direction;
	}
	
	/**
	 *  @private
	 */
	public function set direction(value:String):void
	{
		if (value != "ltr" && value != "rtl")
			throw new ArgumentError("Parameter direction must be one of the accepted values.");
		
		if (value == _direction)
			return;
		
		_direction = value;
		
		// These FTE and TLF formatting objects are now invalid
		// and must be recreated when needed.
		elementFormat = null;
		hostFormat = null;
		
		// The TextLines may need to be recreated
		// and the border and background may need to be redrawn.
		setFlag(FLAG_TEXT_LINES_INVALID |
				FLAG_GRAPHICS_INVALID);
		
		invalidate();
	}
	
	//----------------------------------
	//  locale
	//----------------------------------
	
	/**
	 *  @private
	 *  Storage for the locale property.
	 */
	private var _locale:String = "en";
	
	/**
	 *  The locale of the text displayed by TLFTextField.
	 * 
	 *  <p>FTE and TLF use this locale to map Unicode characters
	 *  to font glyphs and to find fallback fonts.</p>
	 */
	public function get locale():String
	{
		return _locale;
	}
	
	/**
	 *  @private
	 */
	public function set locale(value:String):void
	{
		if (value == _locale)
			return;
			
		_locale = value;
		
		// These FTE and TLF formatting objects are now invalid
		// and must be recreated when needed.
		elementFormat = null;
		hostFormat = null;
		
		// The TextLines may need to be recreated
		// and the border and background may need to be redrawn.
		setFlag(FLAG_TEXT_LINES_INVALID |
				FLAG_GRAPHICS_INVALID);
		
		invalidate();
	}
	
	//----------------------------------
	//  textLineCreator
	//----------------------------------
	
	/**
	 *  @private
	 *  Storage for the textLineCreator property.
	 */
	private var _textLineCreator:ITextLineCreator;
	
	/**
	 *  The ITextLineCreator instance that TLFTextField
	 *  uses for creating TextLines.
	 * 
	 *  <p>Set this if you need lines to be created in a different
	 *  SWF context than the one containing the TLF code.</p>
	 * 
	 *  <p>Note: This property does not exist in the classic
	 *  flash.text.TextField API.</p>
	 * 
	 *  @default null
	 *
	 *  @playerversion Flash 10
	 *  @playerversion AIR 1.5
	 *  @langversion 3.0
	 */
	public function get textLineCreator():ITextLineCreator
	{
		return _textLineCreator;
	}
	
	/**
	 *  @private
	 */
	public function set textLineCreator(value:ITextLineCreator):void
	{
		// TLFTextField allows a null value to be set;
		// in fact, this is the default.

		//FIXME (gosmith): set to existing value?
				
		_textLineCreator = value;
		
		// The TextLines may need to be recreated
		// and the border and background may need to be redrawn.
		setFlag(FLAG_TEXT_LINES_INVALID |
				FLAG_GRAPHICS_INVALID);
		
		invalidate();
	}
	
	//--------------------------------------------------------------------------
	//
	//  Properties: Private helpers
	//
	//--------------------------------------------------------------------------
	
	//----------------------------------
	//  htmlImporter
	//----------------------------------
		
	/**
	 *  @private
	 */
	private function get htmlImporter():ITextImporter
	{
		return condenseWhite ? collapsingHTMLImporter : preservingHTMLImporter;
	}	
		
	//--------------------------------------------------------------------------
	//
	//  Methods: TextField
	//
	//--------------------------------------------------------------------------
	
	/**
	 *  This method has not been implemented in TLFTextField
	 *  because very few components use it in TextField.
	 *  It will throw a runtime error if called.
	 * 
	 *  @see flash.text.TextField#appendText()
	 * 
	 *  @playerversion Flash 10
	 *  @playerversion AIR 1.5
	 *  @langversion 3.0
	 */
	public function appendText(newText:String):void
	{
		throw new Error(notImplemented("appendText()"));
	}
	
	/**
	 *  This method has not been implemented in TLFTextField
	 *  because very few components use it in TextField.
	 *  It will throw a runtime error if called.
	 * 
	 *  @see flash.text.TextField#getCharBoundaries()
	 * 
	 *  @playerversion Flash 10
	 *  @playerversion AIR 1.5
	 *  @langversion 3.0
	 */
	public function getCharBoundaries(charIndex:int):Rectangle
	{
		throw new Error(notImplemented("getCharBoundaries()"));
	}
	
	/**
	 *  This method has not been implemented in TLFTextField
	 *  because very few components use it in TextField.
	 *  It will throw a runtime error if called.
	 * 
	 *  @see flash.text.TextField#getCharIndexAtPoint()
	 * 
	 *  @playerversion Flash 10
	 *  @playerversion AIR 1.5
	 *  @langversion 3.0
	 */
	public function getCharIndexAtPoint(x:Number, y:Number):int
	{
		throw new Error(notImplemented("getCharIndexAtPoint()"));
	}
	
	/**
	 *  This method has not been implemented in TLFTextField
	 *  because very few components use it in TextField.
	 *  It will throw a runtime error if called.
	 * 
	 *  @see flash.text.TextField#getFirstCharInParagraph()
	 * 
	 *  @playerversion Flash 10
	 *  @playerversion AIR 1.5
	 *  @langversion 3.0
	 */
	public function getFirstCharInParagraph(charIndex:int):int
	{
		throw new Error(notImplemented("getFirstCharInParagraph()"));
	}
	
	/**
	 *  This method has not been implemented in TLFTextField
	 *  because very few components use it in TextField.
	 *  It will throw a runtime error if called.
	 * 
	 *  @see flash.text.TextField#getLineIndexAtPoint()
	 * 
	 *  @playerversion Flash 10
	 *  @playerversion AIR 1.5
	 *  @langversion 3.0
	 */
	public function getLineIndexAtPoint(x:Number, y:Number):int
	{
		throw new Error(notImplemented("getLineIndexAtPoint()"));
	}
	
	/**
	 *  This method has not been implemented in TLFTextField
	 *  because very few components use it in TextField.
	 *  It will throw a runtime error if called.
	 * 
	 *  @see flash.text.TextField#getLineIndexOfChar()
	 * 
	 *  @playerversion Flash 10
	 *  @playerversion AIR 1.5
	 *  @langversion 3.0
	 */
	public function getLineIndexOfChar(charIndex:int):int
	{
		throw new Error(notImplemented("getLineIndexOfChar()"));
	}
	
	/**
	 *  This method has not been implemented in TLFTextField
	 *  because very few components use it in TextField.
	 *  It will throw a runtime error if called.
	 * 
	 *  @see flash.text.TextField#getLineLength()
	 * 
	 *  @playerversion Flash 10
	 *  @playerversion AIR 1.5
	 *  @langversion 3.0
	 */
	public function getLineLength(lineIndex:int):int
	{
		throw new Error(notImplemented("getLineLength()"));
	}
	
	/**
	 *  @copy flash.text.TextField#getLineMetrics()
	 * 
	 *  @playerversion Flash 10
	 *  @playerversion AIR 1.5
	 *  @langversion 3.0
	 */
	public function getLineMetrics(lineIndex:int):TextLineMetrics
	{
		validateNow();
		
		// TextField throws this RTE when invalid values are set.
		if (lineIndex < 0 || lineIndex >= numChildren)
			throw new RangeError("The supplied index is out of bounds");
		
		// The nth line is the nth child.
		var textLine:TextLine = TextLine(getChildAt(lineIndex));
		
		var x:Number = textLine.x; // indent, blockIndent, leftMargin
		var width:Number = textLine.textWidth;
		var ascent:Number = Math.round(textLine.ascent + textLine.descent)
		var descent:Number = Math.round(textLine.descent);
		var leading:Number = Number(_defaultTextFormat.leading);
		var height:Number = ascent + descent + leading;
		
		return new TextLineMetrics(x, width, height, ascent, descent, leading);
	}
	
	/**
	 *  This method has not been implemented in TLFTextField
	 *  because very few components use it in TextField.
	 *  It will throw a runtime error if called.
	 * 
	 *  @see flash.text.TextField#getLineOffset()
	 * 
	 *  @playerversion Flash 10
	 *  @playerversion AIR 1.5
	 *  @langversion 3.0
	 */
	public function getLineOffset(lineIndex:int):int
	{
		throw new Error(notImplemented("getLineOffset()"));
	}
	
	/**
	 *  This method has not been implemented in TLFTextField
	 *  because very few components use it in TextField.
	 *  It will throw a runtime error if called.
	 * 
	 *  @see flash.text.TextField#getLineText()
	 * 
	 *  @playerversion Flash 10
	 *  @playerversion AIR 1.5
	 *  @langversion 3.0
	 */
	public function getLineText(lineIndex:int):String
	{
		throw new Error(notImplemented("getLineText()"));
	}
	
	/**
	 *  This method has not been implemented in TLFTextField
	 *  because very few components use it in TextField.
	 *  It will throw a runtime error if called.
	 * 
	 *  @see flash.text.TextField#getParagraphLength()
	 * 
	 *  @playerversion Flash 10
	 *  @playerversion AIR 1.5
	 *  @langversion 3.0
	 */
	public function getParagraphLength(charIndex:int):int
	{
		throw new Error(notImplemented("getParagraphLength()"));
	}
	
	/**
	 *  This method has been implemented in TLFTextField
	 *  to simply return a copy of the <code>defaultTextFormat</code>,
	 *  because TLFTextField does not support formatting a range.
	 * 
	 *  @see flash.text.TextField#getTextFormat()
	 * 
	 *  @playerversion Flash 10
	 *  @playerversion AIR 1.5
	 *  @langversion 3.0
	 */
	public function getTextFormat(beginIndex:int = -1,
								  endIndex:int = -1):TextFormat
	{
		// TextField returns a new TextFormat instance each time
		// you call getTextFormat(); the proof is that
		//   textField.getTextFormat() != textField.getTextFormat()
		// is true.
		return cloneTextFormat(_defaultTextFormat); // FIXME (gosmith) return null?
	}
	
	/**
	 *  This method has not been implemented in TLFTextField
	 *  because very few components use it in TextField.
	 *  It will throw a runtime error if called.
	 *
 	 *  @see flash.text.TextField#replaceSelectedText()
	 * 
	 *  @playerversion Flash 10
	 *  @playerversion AIR 1.5
	 *  @langversion 3.0
	 */
	public function replaceSelectedText(value:String):void
	{
		throw new Error(notImplemented("replaceSelectedText()"));
	}
	
	/**
	 *  This method has not been implemented in TLFTextField
	 *  because very few components use it in TextField.
	 *  It will throw a runtime error if called.
	 * 
	 *  @see flash.text.TextField#replaceText()
	 * 
	 *  @playerversion Flash 10
	 *  @playerversion AIR 1.5
	 *  @langversion 3.0
	 */
	public function replaceText(beginIndex:int, endIndex:int,
								newText:String):void
	{
		throw new Error(notImplemented("replaceText()"));
	}
	
	/**
	 *  This method has not been implemented in TLFTextField
	 *  because TLFTextField does not support selection.
	 *  It will throw a runtime error if called.
	 * 
	 *  @see flash.text.TextField#setSelection()
	 * 
	 *  @playerversion Flash 10
	 *  @playerversion AIR 1.5
	 *  @langversion 3.0
	 */
	public function setSelection(beginIndex:int, endIndex:int):void
	{
		throw new Error(notImplemented("setSelection()"));
	}
	
	/**
	 *  This method has no effect on a TLFTextField if beginIndex
     *  or endIndex != -1
	 *  because TLFTextField does not support formatting a range.
	 *
	 *  @see flash.text.TextField#setTextFormat()
	 * 
	 *  @playerversion Flash 10
	 *  @playerversion AIR 1.5
	 *  @langversion 3.0
	 */
	public function setTextFormat(format:TextFormat,
								  beginIndex:int = -1,
								  endIndex:int = -1):void
	{
        if (beginIndex == -1 && endIndex == -1)
        {
            defaultTextFormat = format;
            validateNow();
        }
	}
	
	/**
	 *  This method has not been implemented in TLFTextField
	 *  because very few components use it in TextField.
	 *  It will throw a runtime error if called.
	 *
	 *  @see flash.text.TextField#getImageReference()
	 * 
	 *  @playerversion Flash 10
	 *  @playerversion AIR 1.5
	 *  @langversion 3.0
	 */
	public function getImageReference(id:String):DisplayObject
	{
		throw new Error(notImplemented("getImageReference()"));
	}
	
	//--------------------------------------------------------------------------
	//
	//  Methods
	//
	//--------------------------------------------------------------------------
	
	/**
	 *  @private
	 */
	private function notImplemented(name:String):String
	{
		return "'" + name + "' is not implemented TLFTextField.";
	}
	
	/**
	 *  @private
	 */
	private function testFlag(mask:uint):Boolean
	{
		return (flags & mask) != 0;
	}
	
	/**
	 *  @private
	 */
	private function setFlag(mask:uint):void
	{
		flags |= mask;
	}
	
	private function clearFlag(mask:uint):void
	{
		flags &= ~mask;
	}
	
	private function setFlagToValue(mask:uint, value:Boolean):void
	{
		if (value)
			flags |= mask;
		else
			flags &= ~mask;
	}
	
	/**
	 *  @private
	 *  This method will cause a 'render' event later,
	 *  in response to which validateNow() will get called.
	 */
	private function invalidate():void
	{
		//FIXME (gosmith)
		//CONFIG::debug { assert(!testFlag(FLAG_VALIDATE_IN_PROGRESS), "invalidating during validateNow()"); }
		
		if (stage)
			stage.invalidate();

		// FIXME (gosmith): do addEventListener() for 'render' here
	}
	
	/**
	 *  @private
	 *  This method is the workhorse of TLFTextField.
	 *  It puts it into a state where all properties are consistent
	 *  with each other and where it is rendering what the properties
	 *  specify.
	 */
	private function validateNow():void
	{
		// FIXME (gosmith): When do we get recursive validateNow()?
		if (!testFlag(ALL_INVALIDATION_FLAGS) ||
			testFlag(FLAG_VALIDATE_IN_PROGRESS))
		{
			return;
		}
		
		setFlag(FLAG_VALIDATE_IN_PROGRESS);

		if (testFlag(FLAG_TEXT_LINES_INVALID))
		{
			// Determine whether we will use FTE directly
			// or TLF's TextContainerManager.
			// FIXME (gosmith): What about tabStops?
			var useTCM:Boolean = testFlag(FLAG_HTML_TEXT_CHANGED) ||
								 _defaultTextFormat.target != "" ||
								 _defaultTextFormat.url != ""; // FIXME (gosmith): forget this
			setFlagToValue(FLAG_USE_TCM, useTCM); // FIXME (gosmith): kill this flag
			
			// Remove the previous TextLines
			// (and recycle them, if supported by the player).
			removeTextLines();
						
			// Determine the composition width and height.
			var compositionWidth:Number = NaN; 
			var compositionHeight:Number = NaN; 
			if (_autoSize == TextFieldAutoSize.NONE)
			{
				compositionWidth = _width;
				compositionHeight = _height;
			}
			else if (wordWrap)
			{
				compositionWidth = _width;
			}
			
			if (!useTCM)
			{
				if (!elementFormat)
					createElementFormat();	
				
				composeText(compositionWidth, compositionHeight);							
			}
			else
			{
				if (!hostFormat)
					createHostFormat();
				
				composeHTMLText(compositionWidth, compositionHeight);							
			}
		}
		
		// FIXME (gosmith): Does this belong inside the previous block?
		var origX:Number = x;
		var origWidth:Number = _width; 
		var origHeight:Number = _height; 
				
		if (_autoSize != TextFieldAutoSize.NONE)
		{
			_height = _textHeight + PADDING_TOP + PADDING_BOTTOM;
			if (!wordWrap)
			{
				_width = _textWidth + PADDING_LEFT + PADDING_RIGHT;
				
				// adjust x for CENTER and RIGHT cases
				if (_autoSize == TextFieldAutoSize.RIGHT)
					x += origWidth - _width;
				else if (_autoSize == TextFieldAutoSize.CENTER)
					x += (origWidth - _width) / 2;
			}
			if (_height != origHeight || _width != origWidth || x != origX)
				setFlag(FLAG_GRAPHICS_INVALID);
		}
		
		if (_textWidth > origWidth || _textHeight > origHeight)
		{
			// need to clip
			//trace("clip");
			/*
			if (border)
			{
				// trying to match TextField behavior of border
				r.width += 1;
				r.height += 1;
			}
			*/
			var r:Rectangle = scrollRect;
			if (!r)
				r = new Rectangle();
			r.left = 0;
			r.top = 0;
			r.right = _width;
			r.bottom = _height;
			scrollRect = r;
			setFlag(FLAG_HAS_SCROLL_RECT);
		}
		else 
		{
			// don't need to clip
			//trace("don't clip");
			if (testFlag(FLAG_HAS_SCROLL_RECT))
			{
				scrollRect = null;
				clearFlag(FLAG_HAS_SCROLL_RECT);
			}
		}
		
		// Draw the border and background last,
		// once the width and height are known.
		if (testFlag(FLAG_GRAPHICS_INVALID))
		{
			var g:Graphics = graphics;
			g.clear();
			// First draw the background, then draw the border.
			// This is because TextField actually does something strange --- it expands itselft 1 pixel right and down when drawing a border
			// and fill without the stroke with the required stroking path does not match the "background sans border" behavior of TextField.
			if (background)
			{
				// Width/Height rounding differences between TextField and TLFTextField...
				// For width or height of the form E.5 where E is a positive even integer, Flash 10 on Windows seems to 
				// "round to even", i.e., round the dimension down to E rather than up to E+1. However we currently just 
				// round consistently up to E+1 using Math.round() here since for now are willing to live with this difference.
				var w:Number = rint(_width);
				var h:Number = rint(_height);
				
				g.beginFill(backgroundColor);
				g.drawRect(0, 0, w, h);
				g.endFill();
			}
			
			if (border)
			{
				g.lineStyle(1, borderColor);
				g.drawRect(0.5, 0.5, _width, _height); // TextField actually expands by a pixel down and to the right when it has a border!
			}
		}
		
		clearFlag(ALL_INVALIDATION_FLAGS | FLAG_VALIDATE_IN_PROGRESS);
	}
	
	/**
	 *  @private
	 */
	private function createElementFormat():void
	{
		var fontDescription:FontDescription = new FontDescription();
		
		fontDescription.fontLookup = embedFonts ?
									 FontLookup.EMBEDDED_CFF :
									 FontLookup.DEVICE;
		
		fontDescription.fontName = _defaultTextFormat.font;
		
		fontDescription.fontPosture = _defaultTextFormat.italic ?
									  FontPosture.ITALIC :
									  FontPosture.NORMAL;
		
		fontDescription.fontWeight = _defaultTextFormat.bold ?
									 FontWeight.BOLD :
									 FontWeight.NORMAL;
		
		elementFormat = new ElementFormat();
				
		elementFormat.color = uint(_defaultTextFormat.color);
				
		elementFormat.fontDescription = fontDescription;
		
		elementFormat.fontSize = Number(_defaultTextFormat.size);
		
		elementFormat.kerning = _defaultTextFormat.kerning ?
								Kerning.AUTO :
								Kerning.OFF;
								
		elementFormat.locale = locale;
				
		elementFormat.trackingRight = Number(_defaultTextFormat.letterSpacing); // FIXME (gosmith): 1/2 left, 1/2 right?
	}
	
	/**
	 *  @private
	 */
	private function createHostFormat():void
	{
		hostFormat = new TLFTextFieldHostFormat(this);
		
		/*
		//FIXME (gosmith)
		
		hostFormat = new TextLayoutFormat();

		hostFormat.color = _defaultTextFormat.color;
		
		hostFormat.fontFamily = _defaultTextFormat.font;
		
		hostFormat.fontLookup = embedFonts ?
								FontLookup.EMBEDDED_CFF :
								FontLookup.DEVICE;

		hostFormat.fontSize = _defaultTextFormat.size;

		hostFormat.fontStyle = _defaultTextFormat.italic ?
							   FontPosture.ITALIC :
							   FontPosture.NORMAL;
		
		hostFormat.fontWeight = _defaultTextFormat.bold ?
								FontWeight.BOLD :
								FontWeight.NORMAL;

		hostFormat.kerning = _defaultTextFormat.kerning ?
							 Kerning.AUTO :
							 Kerning.OFF;
		
		hostFormat.leadingModel = LeadingModel.ASCENT_DESCENT_UP;
		
		hostFormat.lineBreak = wordWrap ?
							   LineBreak.TO_FIT :
							   LineBreak.EXPLICIT;
		
		hostFormat.lineHeight = 2 + _defaultTextFormat.leading; // FIXME (gosmith)
		
		hostFormat.locale = locale;
	
		hostFormat.paddingBottom = PADDING_BOTTOM;
		
		hostFormat.paddingLeft = PADDING_LEFT;
	
		hostFormat.paddingRight = PADDING_RIGHT;
		
		hostFormat.paddingTop = PADDING_TOP;

		hostFormat.paragraphEndIndent = _defaultTextFormat.rightMargin;

		hostFormat.paragraphStartIndent = _defaultTextFormat.leftMargin;

		hostFormat.tabStops = _defaultTextFormat.tabStops; // FIXME (gosmith)

		hostFormat.textAlign = _defaultTextFormat.align;
		
		hostFormat.textAlignLast = _defaultTextFormat.align;
		
		hostFormat.textDecoration = _defaultTextFormat.underline ?
									TextDecoration.UNDERLINE :
									TextDecoration.NONE;
		
		hostFormat.textIndent = _defaultTextFormat.indent;
		
		hostFormat.trackingRight = _defaultTextFormat.letterSpacing;
		*/
	}
	
	/**
	 *  @private
	 */
	private function removeTextLines():void
	{
		var n:int = numChildren;
		for (var i:int = 0; i < n; i++)
		{
			// Repeatedly removing the 0th child is supposed
			// to be the fastest way to remove all children.
			var textLine:TextLine = TextLine(removeChildAt(0));
			
			// TLF provides a TextLine cache,
			// for use with recreateTextLine().
			TextLineRecycler.addLineForReuse(textLine);
		}
		
		_textWidth = 0;
		_textHeight = 0;
	}
	
	/**
	 *  @private
	 */
	private function composeText(compositionWidth:Number,
								 compositionHeight:Number):void
	{
		var innerWidth:Number =
			compositionWidth - PADDING_LEFT - PADDING_RIGHT;
		var innerHeight:Number =
			compositionHeight - PADDING_TOP - PADDING_BOTTOM;
			
		// FIXME (gosmith)
		var emBox:Rectangle = elementFormat.getFontMetrics().emBox;
		var ascent:int = Math.round(emBox.height);
		var descent:int = Math.round(emBox.bottom);
		
		// Break the text into paragraphs at CR characters.
		// (Each LF character has already been turned into a CR.)
		// We could use split(), but that would create a temporary Array.
		var paragraphY:int = 0;
		var n:int = text.length;
		var i:int = 0;
		do
		{
			var j:int = text.indexOf("\r", i);
			if (j == -1)
				j = n;
			var paragraphText:String = i == 0 && j == n ?
									   text :
									   text.substring(i, j);
			
			// Use an FTE TextBlock to compose TextLines
			// for one paragraph of the text, keeping track
			// of how far down we've composed.
			paragraphY = createTextLines(innerWidth, innerHeight,
										 paragraphText, paragraphY,
										 ascent, descent);
										 
			// TextField puts the same leading between paragraphs
			// as between lines in a paragraph.
			paragraphY += _defaultTextFormat.leading;
			
			i = j + 1;
		}
		while (j != n);
		
		// At this point, all TextLines have been composed
		// and have the correct spacing, but are all left-aligned
		// starting at (0, 0).
		// This method will adjust their x and y so that they
		// are correctly aligned and inset by the left and top padding.
		alignTextLines(innerWidth);
		
		// FIXME (gosmith)
		_textWidth = Math.round(_textWidth);
		_textHeight = Math.round(
			numChildren * (ascent + descent) +
			(numChildren - 1) * Number(_defaultTextFormat.leading));
	}

	/**
	 *  @private
	 *  Stuffs the specified paragraph text and formatting info into a TextBlock
	 *  and uses it to create as many TextLines as fit into the bounds.
	 *  Returns true if all the text was composed into textLines.
	 */
	private function createTextLines(innerWidth:Number,
									 innerHeight:Number,
									 paragraphText:String,
									 paragraphY:int,
									 ascent:int, descent:int):int
	{
		// Set the TextBlock's content.
		// Note: If there is no text, we do what TLF does and compose
		// a paragraph terminator character, so that a TextLine
		// gets created and we can measure it.
		// It will have a width of 0 but a height equal
		// to the font's ascent plus descent.
		staticTextElement.text = paragraphText.length > 0 ?
								 paragraphText :
								 "\u2029";
		staticTextElement.elementFormat = elementFormat;
		staticTextBlock.content = staticTextElement;
		
		// And its bidiLevel.
		staticTextBlock.bidiLevel = direction == "ltr" ? 0 : 1;
		
		// And its justifier.
		staticSpaceJustifier.lineJustification =
			_defaultTextFormat.align == "justify" ?
			LineJustification.ALL_BUT_LAST :
			LineJustification.UNJUSTIFIED;;
		//FIXME (gosmith) staticSpaceJustifier.letterSpacing
		staticTextBlock.textJustifier = staticSpaceJustifier;
		
		// Then create and add TextLines using this TextBlock.
		paragraphY = createTextLinesFromTextBlock(
						innerWidth, innerHeight,
						staticTextBlock, paragraphY,
						ascent, descent);
		
		// Cleans up and sets the validity of the lines associated 
		// with the TextBlock to TextLineValidity.INVALID.
		var firstLine:TextLine = staticTextBlock.firstLine;
		var lastLine:TextLine = staticTextBlock.lastLine;
		if (firstLine)
			staticTextBlock.releaseLines(firstLine, lastLine);
			
		return paragraphY;     
	}
	
	/**
	 *  @private
	 *  Compose into textLines.  bounds on input is size of composition
	 *  area and on output is the size of the composed content.
	 *  The caller must call releaseLinesFromTextBlock() to release the
	 *  textLines from the TextBlock.
	 * 
	 *  Returns true if all the text was composed into textLines.
	 */
	private function createTextLinesFromTextBlock(innerWidth:Number,
												  innerHeight:Number,
												  textBlock:TextBlock,
												  paragraphY:int,
												  ascent:int,
												  descent:int):int
	{
		if (innerWidth < 0 || innerHeight < 0)
			return paragraphY;
		
		var maxLineWidth:Number =
			wordWrap ? innerWidth : TextLine.MAX_LINE_WIDTH;
		
		var n:int = 0;
		var nextTextLine:TextLine;
		var nextY:int = paragraphY;
		var textLine:TextLine;
		
		// Generate TextLines, stopping when we run out of text
		// or reach the bottom of the requested bounds.
		// In this loop the lines are positioned within the rectangle
		// (0, 0, innerWidth, innerHeight), with left alignment.
		while (true)
		{
			var recycleLine:TextLine = TextLineRecycler.getLineForReuse();
			if (recycleLine)
			{
				if (textLineCreator)
				{
					nextTextLine = textLineCreator.recreateTextLine(
						textBlock, recycleLine, textLine, maxLineWidth);		
				}        
				else
				{
					nextTextLine = recreateTextLine(
						recycleLine, textLine, maxLineWidth);
				}  
			}
			else
			{
				if (textLineCreator)
				{
					nextTextLine = textLineCreator.createTextLine(
						textBlock, textLine, maxLineWidth);
				}
				else
				{
					nextTextLine = textBlock.createTextLine(
						textLine, maxLineWidth);
				}
			}
			
			if (!nextTextLine)
				break;
			
			// Determine the natural baseline position for this line.
			// Note: The y coordinate of a TextLine is the location
			// of its baseline, not of its "top".
			if (n == 0)
				nextY += ascent;
			else
				nextY += descent + _defaultTextFormat.leading + ascent;
			
			// If the next line is completely outside the rectangle,
			// we're done.
//			if (nextY - nextTextLine.ascent > innerHeight)
//				break;
			
			// We'll keep this line.
			textLine = nextTextLine;
			n++; // FIXME (gosmith): remove counter
			
			// Assign its location based on left/top alignment.
			// Its x position is 0 by default.
			textLine.y = nextY;
			//trace(textLine.y);
			
			if (_defaultTextFormat.underline)
			{
				// FTE doesn't render underlines,
				// but it can tell us where to draw them.
				// You can't draw in a TextLine but it can have children,
				// so we create a child Shape to draw them in.
				
				var fontMetrics:FontMetrics = elementFormat.getFontMetrics();
				
				var shape:Shape = new Shape();
				var g:Graphics = shape.graphics;
				g.lineStyle(fontMetrics.underlineThickness, 
					elementFormat.color, elementFormat.alpha);
				g.moveTo(0, fontMetrics.underlineOffset);
				g.lineTo(textLine.textWidth, fontMetrics.underlineOffset);
				
				textLine.addChild(shape);
			}
			
			addChild(textLine);

			_textWidth = Math.max(_textWidth, textLine.textWidth);
		}
		
		return nextY + descent;
	}
	
	/**
	 *  @private
	 */
	private function alignTextLines(innerWidth:Number):void
	{
		var align:String = _defaultTextFormat.align;
		var leftAligned:Boolean = 
			align == "left" && direction == "ltr" ||
			align == "right" && direction == "rtl" ||
			align == "justify";
		var centerAligned:Boolean = align == "center";
		var rightAligned:Boolean =
			align == "left" && direction == "rtl" ||
			align == "right" && direction == "ltr"; 
		
		// Calculate loop constants for horizontal alignment.
		var leftOffset:Number = PADDING_LEFT;
		var centerOffset:Number = leftOffset + innerWidth / 2;
		var rightOffset:Number = leftOffset + innerWidth;
		
		// Reposition each line if necessary.
		// based on the horizontal alignment,
		// and adjusting for the padding.
		var n:int = numChildren;
		for (var i:int = 0; i < n; i++)
		{
			var textLine:TextLine = TextLine(getChildAt(i));
			
			if (leftAligned)
				textLine.x = leftOffset;
			else if (centerAligned)
				textLine.x = centerOffset - textLine.textWidth / 2;
			else if (rightAligned)
				textLine.x = rightOffset - textLine.textWidth;
				
			textLine.y += PADDING_TOP; 
		}
	}
	
	/**
	 *  @private
	 */
	private function composeHTMLText(compositionWidth:Number,
									 compositionHeight:Number):void
	{
		// FIXME (gosmith): if (!textFlow) ?
		textFlow = htmlImporter.importToFlow(_htmlText);
		
		textFlow.addEventListener(MouseEvent.CLICK, linkClickHandler);
					
		if (!textContainerManager)
			textContainerManager = new TLFTextFieldTextContainerManager(this);
			
		textContainerManager.compositionWidth = compositionWidth;
		textContainerManager.compositionHeight = compositionHeight;
		
		textContainerManager.editingMode = EditingMode.READ_ONLY;
		
		textContainerManager.hostFormat = hostFormat;
		
		textContainerManager.textLineCreator = textLineCreator;
		
		textContainerManager.setTextFlow(textFlow)
		
		textContainerManager.updateContainer();
		
		var bounds:Rectangle = textContainerManager.getContentBounds();
		_textWidth = Math.ceil(bounds.width); // FIXME (gosmith): round?
		_textHeight = Math.ceil(bounds.height);
	}

	// FIXME (gosmith): listen for events
	// when asynchronous images are loaded to resize?

	//--------------------------------------------------------------------------
	//
	//  Event handlers
	//
	//--------------------------------------------------------------------------
	
	/**
	 *  @private
	 */
	private function addedToStageHandler(event:Event):void
	{
		// Having renderHandler() attached only while on the stage
		// gives a performance improvement.
		removeEventListener(Event.ADDED_TO_STAGE, addedToStageHandler);
		addEventListener(Event.RENDER, renderHandler);
		addEventListener(Event.REMOVED_FROM_STAGE, removedFromStageHandler);
		validateNow();
	}
	
	/**
	 *  @private
	 */
	private function removedFromStageHandler(event:Event):void
	{
		removeEventListener(Event.RENDER, renderHandler);
		removeEventListener(Event.REMOVED_FROM_STAGE, removedFromStageHandler);
		addEventListener(Event.ADDED_TO_STAGE, addedToStageHandler);
	}
	
	/**
	 *  @private
	 */
	private function renderHandler(event:Event):void // FIXME (gosmith): don't keep this around
	{
		validateNow();
	}
	
	/**
	 *  @private
	 */
	private function linkClickHandler(event:FlowElementMouseEvent):void
	{
		// Need to remove the event: portion of the href if it has one.
		// Only dispatch the event if it has the event portion.
		var href:String = LinkElement(event.flowElement).href;
		var i:int = href.search("event:"); // FIXME (gosmith): indexOf; need starts-with logic
		if (i < 0)
			return;
		var textEvent:TextEvent = new TextEvent(TextEvent.LINK);
		textEvent.text = href.substring(i + 6, href.length - i + 5);
		dispatchEvent(textEvent);
	}
}
	
}

import flash.display.Sprite;
import flash.text.engine.FontLookup;
import flash.text.engine.FontPosture;
import flash.text.engine.FontWeight;
import flash.text.engine.Kerning;

import flashx.textLayout.container.TextContainerManager;
import flashx.textLayout.elements.IConfiguration;
import flashx.textLayout.formats.ITextLayoutFormat;
import flashx.textLayout.formats.LeadingModel;
import flashx.textLayout.formats.LineBreak;
import flashx.textLayout.formats.TextDecoration;

import mx.core.TLFTextField;
import mx.core.mx_internal;

use namespace mx_internal;

/**
 *  @private
 */
class TLFTextFieldTextContainerManager extends TextContainerManager
{
	/**
	 *  @private
	 */
	public function TLFTextFieldTextContainerManager(container:Sprite, configuration:IConfiguration = null)
	{
		super(container, configuration);
	}
	
	/**
	 *  @private
	 */
	override public function drawBackgroundAndSetScrollRect(scrollX:Number, scrollY:Number):Boolean
	{
		return true;
	}
}

/**
 *  @private
 */
class TLFTextFieldHostFormat implements ITextLayoutFormat
{
	public function TLFTextFieldHostFormat(textField:TLFTextField)
	{
		super();
		
		this.textField = textField;
	}
	
	private var textField:TLFTextField;
	
	public function get alignmentBaseline():*
	{
		return undefined;
	}
	
	public function get backgroundAlpha():*
	{
		return undefined;
	}
	
	public function get backgroundColor():*
	{
		return undefined;
	}
	
	public function get baselineShift():*
	{
		return undefined;
	}
	
	public function get blockProgression():*
	{
		return undefined;
	}
	
	public function get breakOpportunity():*
	{
		return undefined;
	}
	
	public function get cffHinting():*
	{
		return undefined;
	}
	
	public function get color():*
	{
		textField._defaultTextFormat.color;
	}
	
	public function get columnCount():*
	{
		return undefined;
	}
	
	public function get columnGap():*
	{
		return undefined;
	}
	
	public function get columnWidth():*
	{
		return undefined;
	}
	
	public function get digitCase():*
	{
		return undefined;
	}
	
	public function get digitWidth():*
	{
		return undefined;
	}
	
	public function get direction():*
	{
		return textField.direction;
	}
	
	public function get dominantBaseline():*
	{
		return undefined;
	}
	
	public function get firstBaselineOffset():*
	{
		return undefined;
	}
	
	public function get fontFamily():*
	{
		return textField._defaultTextFormat.font;
	}
	
	public function get fontLookup():*
	{
		return textField.embedFonts ?
			   FontLookup.EMBEDDED_CFF :
			   FontLookup.DEVICE;
	}
	
	public function get fontSize():*
	{
		return textField._defaultTextFormat.size;
	}
	
	public function get fontStyle():*
	{
		return textField._defaultTextFormat.italic ?
			   FontPosture.ITALIC :
			   FontPosture.NORMAL;
	}
	
	public function get fontWeight():*
	{
		return textField._defaultTextFormat.bold ?
			   FontWeight.BOLD :
			   FontWeight.NORMAL;
	}
	
	public function get justificationRule():*
	{
		return undefined;
	}
	
	public function get justificationStyle():*
	{
		return undefined;
	}
	
	public function get kerning():*
	{
		return textField._defaultTextFormat.kerning ?
			   Kerning.AUTO :
			   Kerning.OFF;
	}
	
	public function get leadingModel():*
	{
		return LeadingModel.ASCENT_DESCENT_UP;
	}
	
	public function get ligatureLevel():*
	{
		return undefined;
	}
	
	public function get lineBreak():*
	{
		return textField.wordWrap ?
			   LineBreak.TO_FIT :
			   LineBreak.EXPLICIT;
	}
	
	public function get lineHeight():*
	{
		return textField._defaultTextFormat.leading + 2; // FIXME (gosmith)
	}
	
	public function get lineThrough():*
	{
		return undefined;
	}
	
	public function get locale():*
	{
		return textField.locale;
	}
	
	public function get paddingBottom():*
	{
		return TLFTextField.PADDING_BOTTOM;
	}
	
	public function get paddingLeft():*
	{
		return TLFTextField.PADDING_LEFT;
	}
	
	public function get paddingRight():*
	{
		return TLFTextField.PADDING_RIGHT;
	}
	
	public function get paddingTop():*
	{
		return TLFTextField.PADDING_TOP;
	}
	
	public function get paragraphEndIndent():*
	{
		return textField._defaultTextFormat.rightMargin;
	}
	
	public function get paragraphSpaceAfter():*
	{
		return undefined;
	}
	
	public function get paragraphSpaceBefore():*
	{
		return undefined;
	}
	
	public function get paragraphStartIndent():*
	{
		return textField._defaultTextFormat.leftMargin;
	}
	
	public function get renderingMode():*
	{
		return undefined;
	}
	
	public function get tabStops():*
	{
		return textField._defaultTextFormat.tabStops; // FIXME (gosmith)
	}
	
	public function get textAlign():*
	{
		return textField._defaultTextFormat.align;
	}
	
	public function get textAlignLast():*
	{
		return textField._defaultTextFormat.align;
	}
	
	public function get textAlpha():*
	{
		return undefined;
	}
	
	public function get textDecoration():*
	{
		return textField._defaultTextFormat.underline ?
			   TextDecoration.UNDERLINE :
			   TextDecoration.NONE;
	}
	
	public function get textIndent():*
	{
		return textField._defaultTextFormat.indent;
	}
	
	public function get textJustify():*
	{
		return undefined;
	}
	
	public function get textRotation():*
	{
		return undefined;
	}
	
	public function get trackingLeft():*
	{
		return undefined; // FIXME (gosmith): divide letterSpacing in 2?
	}
	
	public function get trackingRight():*
	{
		return textField._defaultTextFormat.letterSpacing;
	}
	
	public function get typographicCase():*
	{
		return undefined;
	}
	
	public function get verticalAlign():*
	{
		return undefined;
	}
	
	public function get whiteSpaceCollapse():*
	{
		return undefined;
	}
}
