////////////////////////////////////////////////////////////////////////////////
//
//  ADOBE SYSTEMS INCORPORATED
//  Copyright 2008 Adobe Systems Incorporated
//  All Rights Reserved.
//
//  NOTICE: Adobe permits you to use, modify, and distribute this file
//  in accordance with the terms of the license agreement accompanying it.
//
////////////////////////////////////////////////////////////////////////////////

package spark.components.supportClasses
{

import flash.display.DisplayObject;
import flash.display.DisplayObjectContainer;
import flash.display.Graphics;
import flash.display.Sprite;
import flash.events.Event;
import flash.geom.Rectangle;
import flash.text.engine.TextLine;

import flashx.textLayout.compose.TextLineRecycler;

import mx.core.UIComponent;
import mx.core.mx_internal;
import mx.events.FlexEvent;
import mx.resources.IResourceManager;
import mx.resources.ResourceManager;
import mx.styles.CSSStyleDeclaration;
import mx.styles.IAdvancedStyleClient;
import mx.styles.StyleManager;
import mx.styles.StyleProtoChain;
import mx.utils.NameUtil;

use namespace mx_internal;

//--------------------------------------
//  Styles
//--------------------------------------

/**
 *  The alpha level of the color defined by
 *  the <code>backgroundColor</code> style.
 *  Valid values range from 0.0 to 1.0.
 * 
 *  @default 1.0
 *
 *  @langversion 3.0
 *  @playerversion Flash 10
 *  @playerversion AIR 1.5
 *  @productversion Flex 4
 */
[Style(name="backgroundAlpha", type="Number", inherit="no")]

/**
 *  The color of the background of the entire
 *  bounding rectangle of this component.
 *  If this style is <code>undefined</code>,
 *  no background is drawn.
 *  Otherwise, this RGB color is drawn with an alpha level
 *  determined by the <code>backgroundAlpha</code> style.
 * 
 *  @default undefined
 *
 *  @langversion 3.0
 *  @playerversion Flash 10
 *  @playerversion AIR 1.5
 *  @productversion Flex 4
 */
[Style(name="backgroundColor", type="uint", format="Color", inherit="no")]

/**
 *  The base class for Spark Text controls such as Label and RichText
 *  which display text using CSS styles for the default format.
 *
 *  <p>In addition to adding a <code>text</code> property,
 *  it also adds <code>maxDisplayedLines</code>
 *  and <code>isTruncated</code> properties to support truncation.</p>
 *
 *  @langversion 3.0
 *  @playerversion Flash 10
 *  @playerversion AIR 1.5
 *  @productversion Flex 4
 */
public class TextBase extends UIComponent
{
    include "../../core/Version.as";

    //--------------------------------------------------------------------------
    //
    //  Class variables
    //
    //--------------------------------------------------------------------------

    /**
     *  @private
     *  Most resources are fetched on the fly from the ResourceManager,
     *  so they automatically get the right resource when the locale changes.
     *  But since truncation can happen frequently,
     *  this class caches this resource value in this variable
     *  and updates it when the locale changes.
     */ 
    mx_internal static var truncationIndicatorResource:String;

    //--------------------------------------------------------------------------
    //
    //  Constructor
    //
    //--------------------------------------------------------------------------

    /**
     *  Constructor. 
     *  
     *  @langversion 3.0
     *  @playerversion Flash 10
     *  @playerversion AIR 1.5
     *  @productversion Flex 4
     */
    public function TextBase()
    {
        super();
        
		var resourceManager:IResourceManager = ResourceManager.getInstance();
                                    
		if (!truncationIndicatorResource)
        {
            truncationIndicatorResource = resourceManager.getString(
                "core", "truncationIndicator");
        }
        
        addEventListener(FlexEvent.UPDATE_COMPLETE, updateCompleteHandler);
                
        // Register as a weak listener for "change" events from ResourceManager.
        // If TextBases registered as a strong listener,
        // they wouldn't get garbage collected.
        resourceManager.addEventListener(
            Event.CHANGE, resourceManager_changeHandler, false, 0, true);
    }
    
    //--------------------------------------------------------------------------
    //
    //  Variables
    //
    //--------------------------------------------------------------------------
    
	/**
     *  @private
     *  The composition bounds used when creating the TextLines.
     */
    mx_internal var bounds:Rectangle = new Rectangle(0, 0, NaN, NaN);

    /**
     *  @private
	 *  The TextLines and Shapes created to render the text.
	 *  (Shapes are used to render the backgroundColor format.)
     */
    mx_internal var textLines:Vector.<DisplayObject> =
    	new Vector.<DisplayObject>();

    /**
     *  @private
     *  This flag is set to true if the text must be clipped.
     */
    mx_internal var isOverset:Boolean = false;

    /**
     *  @private
     *  This flag is used to avoid getting or setting the scrollRect
     *  of our displayObject unnecessarily when we need to clip TextLines
     *  that extend beyond our bounds.
     *  It shouldn't even be set to null if you can avoid it,
     *  because Player 10.0 allocates a surface even in this case.
     */
    mx_internal var hasScrollRect:Boolean = false;
    
    /**
     *  @private
     */
    mx_internal var invalidateCompose:Boolean = true;    

    /**
     *  @private
     *  The value of bounds.width, before the compose was done.
     */
    private var _composeWidth:Number;

    /**
     *  @private
     *  The value of bounds.height, before the compose was done.
     */
    private var _composeHeight:Number;
    
    /**
     *  @private
     *  Cache the width constraint as set by the layout in setLayoutBoundsSize()
     *  so that text reflow can be calculated during a subsequent measure pass.
     */
    private var _widthConstraint:Number = NaN;
    
    /**
     *  @private
     *  We can optimize for a single line text reflow, which is a lot of cases.
     */
    private var _measuredOneTextLine:Boolean = false;
    
    //--------------------------------------------------------------------------
    //
    //  Overridden properties: UIComponent
    //
    //--------------------------------------------------------------------------
        
    //----------------------------------
    //  baselinePosition
    //----------------------------------

    [Inspectable(category="General")]

    /**
     *  @private
     */
    override public function get baselinePosition():Number
    {
        validateBaselinePosition();
        
        // Return the baseline of the first line of composed text.
        return textLines.length > 0 ? textLines[0].y : 0;
    }

    //----------------------------------
    //  visible
    //----------------------------------
    
    /**
     *  @private
     */
    private var visibleChanged:Boolean = false;

    /**
     *  @private
     */
    override public function set visible(value:Boolean):void
    {        
    	super.visible = value;
    	visibleChanged = true;

    	invalidateDisplayList();
	}

    //--------------------------------------------------------------------------
    //
    //  Properties
    //
    //--------------------------------------------------------------------------

    //----------------------------------
	//  isTruncated
    //----------------------------------
	
    /**
     *  @private
	 *  Storage for the isTruncated property.
     */
	mx_internal var _isTruncated:Boolean = false;
        
    /**
	 *  A read-only property reporting whether the text has been truncated.
	 *
	 *  <p>Truncating text means replacing excess text
	 *  with a truncation indicator such as "...".
     *  The truncation indicator is locale-dependent;
     *  it is specified by the "truncationIndicator" resource
     *  in the "core" resource bundle.</p>
	 *
     *  <p>If <code>maxDisplayedLines</code> is 0, no truncation occurs.
     *  Instead, the text will simply be clipped
	 *  if it doesn't fit within the component's bounds.</p>
     *
     *  <p>If <code>maxDisplayedLines</code> is is a positive integer,
	 *  the text will be truncated if necessary to reduce
	 *  the number of lines to this integer.</p>
     *
     *  <p>If <code>maxDisplayedLines</code> is -1, the text will be truncated
	 *  to display as many lines as will completely fit within the height
     *  of the component.</p>
     *
     *  <p>Truncation is only performed if the <code>lineBreak</code>
     *  style is <code>"toFit"</code>; the value of this property
     *  is ignored if <code>lineBreak</code> is <code>"explicit"</code>.</p>
	 *
	 *  @default false
     *  
     *  @langversion 3.0
     *  @playerversion Flash 10
     *  @playerversion AIR 1.5
     *  @productversion Flex 4
     */
	public function get isTruncated():Boolean
    {
		// For some reason, the compiler needs an explicit cast to Boolean
		// to avoid a warning even though at runtime "is Boolean" is true.
		return Boolean(_isTruncated);
    }
    
    //----------------------------------
	//  maxDisplayedLines
    //----------------------------------
    
    /**
     *  @private
     */
	private var _maxDisplayedLines:int = 0;
    
    /**
     *  An integer which determines whether, and where,
	 *  the text gets truncated.
	 *
	 *  <p>Truncating text means replacing excess text
	 *  with a truncation indicator such as "...".
     *  The truncation indicator is locale-dependent;
     *  it is specified by the "truncationIndicator" resource
     *  in the "core" resource bundle.</p>
     *
     *  <p>If the value is 0, no truncation occurs.
     *  Instead, the text will simply be clipped
	 *  if it doesn't fit within the component's bounds.</p>
     *
     *  <p>If the value is is a positive integer,
	 *  the text will be truncated if necessary to reduce
	 *  the number of lines to this integer.</p>
     *
     *  <p>If the value is -1, the text will be truncated to display
     *  as many lines as will completely fit within the height
     *  of the component.</p>
     *
     *  <p>Truncation is only performed if the <code>lineBreak</code>
     *  style is <code>"toFit"</code>; the value of this property
     *  is ignored if <code>lineBreak</code> is <code>"explicit"</code>.</p>
	 *
	 *  @default 0
     *  
     *  @langversion 3.0
     *  @playerversion Flash 10
     *  @playerversion AIR 1.5
     *  @productversion Flex 4
     */
	public function get maxDisplayedLines():int
    {
		return _maxDisplayedLines;
    }
    
    /**
     *  @private
     */
	public function set maxDisplayedLines(value:int):void
    {
		if (value != _maxDisplayedLines)
    	{
			_maxDisplayedLines = value;
    		
			invalidateTextLines();
                 		
    		invalidateSize();
    		invalidateDisplayList();
    	}
    }

    //----------------------------------
    //  text
    //----------------------------------

    /**
     *  @private
     */
    mx_internal var _text:String = "";
        
    /**
     *  The text displayed by this text component.
	 *
     *  <p>The formatting of this text is controlled by CSS styles.
     *  The supported styles depend on the subclass.</p>
	 *
	 *  @default ""
     *  
     *  @langversion 3.0
     *  @playerversion Flash 10
     *  @playerversion AIR 1.5
     *  @productversion Flex 4
     */
    public function get text():String 
    {
        return _text;
    }
    
    /**
     *  @private
     */
    public function set text(value:String):void
    {
        if (value != _text)
        {
            _text = value;

            invalidateTextLines();
            invalidateSize();
            invalidateDisplayList();
        }
    }
    
    /**
     *  @private
     */
    override protected function measure():void
    {
        // _widthConstraint trumps even explicitWidth as some layouts may choose
        // to specify width different from the explicit.
        var constrainedWidth:Number =
            !isNaN(_widthConstraint) ? _widthConstraint : explicitWidth;
            
        var allLinesComposed:Boolean =
            composeTextLines(constrainedWidth, explicitHeight);
        
        // Anytime we are composing we need to invalidate the display list
        // as we may have messed up the text lines.
        invalidateDisplayList();
        
        // Put on next pixel boundary for crisp edges.
        bounds.width = Math.ceil(bounds.width);        
        bounds.height = Math.ceil(bounds.height);

        // If the measured height is not affected, then constrained
        // width measurement is not neccessary.
        if (!isNaN(_widthConstraint) && measuredHeight == bounds.height)
            return;
            
        // Call super.measure() here insted of in the beginning of the method,
        // as it zeroes the measuredWidth, measuredHeight and these values will
        // still be valid if we decided to do an early return above.
        super.measure();

        measuredWidth = bounds.width;
        measuredHeight = bounds.height;
        
        // Remember the number of text lines during measure. We can use this to
        // optimize the double measure scheme for text reflow.
        _measuredOneTextLine = allLinesComposed && 
                               textLines.length == 1; 

        //trace(id, drawnDisplayObject.name, "measure", measuredWidth, measuredHeight);
    }

    /**
     *  @private
     *  We override the setLayoutBoundsSize to determine whether to perform
     *  text reflow. This is a convenient place, as the layout passes NaN
     *  for a dimension not constrained to the parent.
     */
    override public function setLayoutBoundsSize(width:Number,
                                                 height:Number,
                                                 postLayoutTransform:Boolean = true):void
    {
        super.setLayoutBoundsSize(width, height, postLayoutTransform);

        // FIXME (egeorgie): possible optimization - if we reflow the text
        // immediately, we'll be able to detect whether the constrained
        // width causes the measured height to change.
        // Also certain layouts like vertical/horizontal will
        // be able to get the better performance as subsequent elements
        // will not go through updateDisplayList twice. This also has the
        // potential of avoiding text compositing during measure.

        // Did we already constrain the width?
        if (_widthConstraint == width)
            return;

        // No reflow for explicit lineBreak
        if (getStyle("lineBreak") == "explicit")
            return;

        // If we don't measure
        if (canSkipMeasurement())
            return;

        if (!isNaN(explicitHeight))
            return;

        // We support reflow only in the case of constrained width and
        // unconstrained height. Note that we compare with measuredWidth,
        // as for example the TextBase can be
        // constrained by the layout with "left" and "right", but the
        // container width itself may not be constrained and it would depend
        // on the element's measuredWidth.
        var constrainedWidth:Boolean = !isNaN(width) && (width != measuredWidth) && (width != 0); 
        if (!constrainedWidth)
            return;
            
        // Special case - if we have a single line, then having a constraint larger
        // than the measuredWidth will not result in measuredHeight change, as we
        // will still have only a single line
        if (_measuredOneTextLine && width > measuredWidth)
            return;
    
        // We support reflow only when we don't have a transform.
        // We could add support for scale, but not skew or rotation.
        if (postLayoutTransform && hasComplexLayoutMatrix)
			return;

        _widthConstraint = width;
        invalidateSize();
    
    }

    /**
     *  @private
     */
    override protected function updateDisplayList(unscaledWidth:Number, 
                                                  unscaledHeight:Number):void
    {
        //trace(id, drawnDisplayObject.name, "updateDisplayList", unscaledWidth, unscaledHeight);  
        super.updateDisplayList(unscaledWidth, unscaledHeight);

        // Figure out if a compose is needed or maybe just clip what is already
        // composed.

        var compose:Boolean = false;
        var clipText:Boolean = false;
        
        // FIXME (gosmith): optimize for right-to-left text so compose isn't always done
        // when height or width changes.
        if (invalidateCompose || 
            composeForAlignStyles(unscaledWidth, unscaledHeight))
        {
            compose = true;
        }
        else if (unscaledHeight != bounds.height)
        {
            // Height changed.
            if (composeOnHeightChange(unscaledHeight))
            {
                compose = true;
            }
            else if (unscaledHeight < bounds.height)
            {
                // Don't need to recompose but need to clip since not all the
                // height is needed.
                clipText = true;                   
            }
        }

        // Width changed.        
        if (!compose && unscaledWidth != bounds.width)
        {
            if (composeOnWidthChange(unscaledWidth))
            {
                compose = true;
            }
            else if (getStyle("lineBreak") == "explicit" &&
                     unscaledWidth < bounds.width)
            {
                // Explicit line breaks.  Don't need to recompose but need to 
                // clip since the not all the width is needed.
                clipText = true;                   
            }
        }

        // Compose will add the new text lines to the display object container.
        // Otherwise, if the text is in a shared container, make sure the 
        // position of the lines has remained the same.
        if (compose)
            composeTextLines(unscaledWidth, unscaledHeight);
            
        // If the text is overset it always has to be clipped (as well as if 
        // it is being clipped to reduce the size to avoid a recomposition).              
        if (isOverset)
            clipText = true;
        
        //trace(id, drawnDisplayObject.name, "udl", "compose", compose, "clip", 
        //      clipText, "bounds", bounds);        

        // Set the scrollRect used for clipping appropriately.              
        clip(clipText, unscaledWidth, unscaledHeight);
 
     	// If backgroundColor is defined, fill the bounds of the component
    	// with backgroundColor drawn with alpha level backgroundAlpha, 
        // otherwise, render a fully transparent background.
        
    	var backgroundColor:* = getStyle("backgroundColor");
        var backgroundAlpha:Number = getStyle("backgroundAlpha");
        
    	if (backgroundColor === undefined)
        {
            backgroundColor = 0;
            backgroundAlpha = 0;
        }
        
        var g:Graphics = graphics;
        g.clear();
        g.beginFill(uint(backgroundColor), backgroundAlpha);
        g.drawRect(0, 0, unscaledWidth, unscaledHeight);
        g.endFill();
    }

    //--------------------------------------------------------------------------
    //
    //  Overidden Methods: ISimpleStyleClient
    //
    //--------------------------------------------------------------------------

    /**
     *  @private
     */
    override public function styleChanged(styleProp:String):void
    {
        super.styleChanged(styleProp);

        invalidateTextLines();
    }

    //--------------------------------------------------------------------------
    //
    //  Methods
    //
    //--------------------------------------------------------------------------

    /**
     *  @private
     */
    mx_internal function invalidateTextLines():void
    {
        invalidateCompose = true;
    }
    
    /**
     *  @private
     */
    private function composeForAlignStyles(unscaledWidth:Number, 
                                             unscaledHeight:Number):Boolean
    {
        // For textAlign, if the composeWidth isn't the same
        // as the unscaledWidth, and the text isn't left aligned, we need to 
        // recompose.
        //
        if (isNaN(_composeWidth) || _composeWidth != unscaledWidth)
        {
            var direction:String = getStyle("direction");
            var textAlign:String = getStyle("textAlign");

            var leftAligned:Boolean =
                textAlign == "left" ||
                textAlign == "start" && direction == "ltr" ||
                textAlign == "end" && direction == "rtl";

            if (!leftAligned)
                return true;
        }
        
        // For verticalAlign, if the composeHeight isn't the same as the
        // unscaledHeight, and the text isn't top aligned, we need to
        // recompose (or adjust the y values of all the text lines).
        if (isNaN(_composeHeight) || _composeHeight != unscaledHeight)
        {
            var verticalAlign:String = getStyle("verticalAlign");
            var topAligned:Boolean = (verticalAlign == "top");

            if (!topAligned)
                return true;
        }

        return false;   
    }

    /**
     *  @private
     */
    private function composeOnHeightChange(unscaledHeight:Number):Boolean
    {
        // Height increased and there may be more text.  It is possible that
        // there is more text even if isOverset isn't set.  If height
        // was specified, it will compose just enough text to fit the 
        // composition bounds and depending on text placement, it may not 
        // be overset.
        if (unscaledHeight > bounds.height &&
            (isOverset || !isNaN(_composeHeight)))
            return true;
            
        if (maxDisplayedLines != 0 && getStyle("lineBreak") == "toFit")
        {
            // -1 is fill the bounds and the bounds changed so recompose.
            if (maxDisplayedLines == -1)            
                return true;
                
            // If truncating at n lines and the height got smaller, may need to
            // redo the truncation. Or if the height got larger, only have to
            // redo the truncation if we don't already have the number of
            // truncation lines needed.
            if (maxDisplayedLines > 0 &&
               (unscaledHeight < bounds.height ||
                 textLines.length != maxDisplayedLines))
            {
                return true;
            }
        }

        // FIXME (gosmith): optimize this case.        
        if (getStyle("blockProgression") != "tb")
            return true;    
            
        return false;                
    }

    /**
     *  @private
     */
    private function composeOnWidthChange(unscaledWidth:Number):Boolean
    {
        // If toFit, then the composeWidth must equal the unscaledWidth
        // so that the text flows properly. 
        if (getStyle("lineBreak") == "toFit")
        {
            if (isNaN(_composeWidth) || _composeWidth != unscaledWidth)
                return true;
        }
        
        // FIXME (gosmith): optimize this case.        
        if (getStyle("blockProgression") != "tb")
            return true;
                    
        return false;
    }

    /**
     *  @private
     *  Returns false to indicate no lines were composed.
     */
    mx_internal function composeTextLines(width:Number = NaN,
										height:Number = NaN):Boolean
	{
	    _composeWidth = width;
	    _composeHeight = height;
	    
	    _isTruncated = false;
	    
	    return false;
	}

	/**
	 *  @private
	 *  Adds the TextLines created by composeTextLines()
     *  to a specified DisplayObjectContainer.
	 */
	mx_internal function addTextLines(container:DisplayObjectContainer,
								      index:int = 0):void
	{
		var n:int = textLines.length;
        if (n == 0)
            return;

        var addChildAtMethod:Function = container is UIComponent ?
        								UIComponent(container).$addChildAt :
        								container.addChildAt;
            
        for (var i:int = n - 1; i >= 0; i--)
		{
			var textLine:DisplayObject = textLines[i];
						
            //trace(container.name, "addTextLines", textLine.x, textLine.y, drawX, drawY);
            
			addChildAtMethod(textLine, index);
		}
	}

	/**
	 *  @private
	 *  Removes the TextLines created by composeTextLines()
     *  from whatever container they were in.
     * 
	 *  This does not empty the textLines Array.
	 */
	mx_internal function removeTextLines():void
	{
		var n:int = textLines.length;		
		if (n == 0)
			return;

		// The old TextLines might have been added to a different
		// container than the one we'd use now to add new TextLines.
		var container:DisplayObjectContainer =
			textLines[0].parent;

         var removeChildMethod:Function = container is UIComponent ?
										 UIComponent(container).$removeChild :
										 container.removeChild;
            
		for (var i:int = 0; i < n; i++)
		{
			var textLine:DisplayObject = textLines[i];
			
            removeChildMethod(textLine);
		}
	}

    /**
     *  @private
     *  Adds the TextLines to the reuse cache, and clears the textLines array.
     */
    mx_internal function releaseTextLines(
    	textLinesVector:Vector.<DisplayObject> = null):void
    {
        if (!textLinesVector)
            textLinesVector = textLines;
            
        var n:int = textLinesVector.length;
        for (var i:int = 0; i < n; i++)
        {
            var textLine:DisplayObject = textLinesVector[i];
            
            // This method does the Flash Player version check so we don't
            // have to.
            if (textLine is TextLine)
            	TextLineRecycler.addLineForReuse(TextLine(textLine));
        }
        
        textLinesVector.length = 0;
   }
    
    /**
     *  @private
     *  Does the bounds of the contents rectangle fit within the bounds
     *  of the composition rectangle?
     */
    mx_internal function isTextOverset(composeWidth:Number, 
                                       composeHeight:Number):Boolean
    {        
        // The composition bounds available for text placement.
        var compositionRect:Rectangle =
        	new Rectangle(0, 0, composeWidth, composeHeight);
    
        // Add in a half-pixel slop factor to the throw-away rectangle (do
        // not modify bounds).  This covers the case where the
        // y (textLine.y - textLine.ascent) is slightly less than 0 because of
        // rounding errors.
        compositionRect.inflate(0.25, 0.25);

        // The bounds of the composed text.
        var contentRect:Rectangle = bounds;
       
        // Does the text fit totally within the composition area?  This is
        // a Rectangle.contains but allows for composition width and/or height
        // to be NaN.
        var isOverset:Boolean = (contentRect.top < compositionRect.top || 
                                 contentRect.left < compositionRect.left ||
                                 (!isNaN(compositionRect.bottom) &&
                                 contentRect.bottom > compositionRect.bottom) ||
                                 (!isNaN(compositionRect.right) &&
                                 contentRect.right > compositionRect.right));
                   
        //trace(id, drawnDisplayObject.name, "bounds", contentRect, "overset", isOverset);
        
        return isOverset;                                                     
    } 
    
    /**
	 *  Use scrollRect to clip overset lines.
	 *  But don't read or write scrollRect if you can avoid it,
	 *  because this causes Player 10.0 to allocate memory.
	 *  And if scrollRect is already set to a Rectangle instance,
	 *  reuse it rather than creating a new one.
     *  
     *  @langversion 3.0
     *  @playerversion Flash 10
     *  @playerversion AIR 1.5
     *  @productversion Flex 4
     */
    mx_internal function clip(clipText:Boolean, w:Number, h:Number):void
	{
        // TODO (rfrishbe): What if someone else sets the scrollRect?
        if (clipText)
        {
            var r:Rectangle = scrollRect;
            if (r)
            {
            	r.x = 0;
            	r.y = 0;
            	r.width = w;
            	r.height = h;
            }
            else
            {
            	r = new Rectangle(0, 0, w, h);
            }
            scrollRect = r;
            hasScrollRect = true;
        }
        else if (hasScrollRect)
        {
            scrollRect = null;
            hasScrollRect = false;
        }
    }

    /**
     *  @private
     *  Used to ensure baselinePosition will reflect something
     *  reasonable.
     */ 
    override mx_internal function validateBaselinePosition():Boolean
    {
        if (!parent)
            return false;
        
        // Ensure we're validated and that we have something to 
        // compute our baseline from.
        var isEmpty:Boolean = (text == "");
        text = isEmpty ? "Wj" : text;
        
        if (invalidatePropertiesFlag ||
        	invalidateSizeFlag || 
            invalidateDisplayListFlag ||
            isEmpty)
        {
            validateNow();  
            text = isEmpty ? "" : text;
        }
        
        return true;
    }
    
    //--------------------------------------------------------------------------
    //
    //  Event handlers
    //
    //--------------------------------------------------------------------------

    /**
     *  @private
     */
    private function resourceManager_changeHandler(event:Event):void
    {
		var resourceManager:IResourceManager = ResourceManager.getInstance();

        truncationIndicatorResource = resourceManager.getString(
            "core", "truncationIndicator");

        // If we're truncating, recompose the text.
        if (maxDisplayedLines != 0)
        {
            invalidateCompose = true;

            invalidateSize();
            invalidateDisplayList();
        }
    }

    /**
     *  @private
     *  We clear the width constraint that's used for the text reflow
     *  after the layout pass is complete.
     */
    private function updateCompleteHandler(event:FlexEvent):void
    {
        // Make sure that if we did a double pass, next time around we'll
        // measure normally
        _widthConstraint = NaN;
    }
}

}