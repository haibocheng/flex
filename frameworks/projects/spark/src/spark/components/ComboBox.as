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

package spark.components
{
import adobe.utils.CustomActions;

import flash.display.DisplayObject;
import flash.events.Event;
import flash.events.FocusEvent;
import flash.events.KeyboardEvent;
import flash.events.MouseEvent;
import flash.ui.Keyboard;

import flashx.textLayout.operations.CutOperation;
import flashx.textLayout.operations.DeleteTextOperation;
import flashx.textLayout.operations.FlowOperation;
import flashx.textLayout.operations.InsertTextOperation;

import mx.core.mx_internal;
import mx.styles.StyleProxy;

import spark.components.supportClasses.DropDownListBase;
import spark.components.supportClasses.ListBase;
import spark.core.NavigationUnit;
import spark.events.DropDownEvent;
import spark.events.TextOperationEvent;
import spark.utils.LabelUtil;
 
use namespace mx_internal;

/**
 *  Bottom inset, in pixels, for the text in the 
 *  prompt area of the control.  
 * 
 *  @default 3
 * 
 *  @langversion 3.0
 *  @playerversion Flash 10
 *  @playerversion AIR 1.5
 *  @productversion Flex 4
 */
[Style(name="paddingBottom", type="Number", format="Length", inherit="no")]

/**
 *  Left inset, in pixels, for the text in the 
 *  prompt area of the control.  
 * 
 *  @default 3
 * 
 *  @langversion 3.0
 *  @playerversion Flash 10
 *  @playerversion AIR 1.5
 *  @productversion Flex 4
 */
[Style(name="paddingLeft", type="Number", format="Length", inherit="no")]

/**
 *  Right inset, in pixels, for the text in the 
 *  prompt area of the control.  
 * 
 *  @default 3
 * 
 *  @langversion 3.0
 *  @playerversion Flash 10
 *  @playerversion AIR 1.5
 *  @productversion Flex 4
 */
[Style(name="paddingRight", type="Number", format="Length", inherit="no")]

/**
 *  Top inset, in pixels, for the text in the 
 *  prompt area of the control.  
 * 
 *  @default 5
 * 
 *  @langversion 3.0
 *  @playerversion Flash 10
 *  @playerversion AIR 1.5
 *  @productversion Flex 4
 */
[Style(name="paddingTop", type="Number", format="Length", inherit="no")]

//--------------------------------------
//  Other metadata
//--------------------------------------

[AccessibilityClass(implementation="spark.accessibility.ComboBoxAccImpl")]
[IconFile("ComboBox.png")]

/**
 *  The ComboBox control is a child class of the DropDownListBase control. 
 *  Like the DropDownListBase control, when the user selects an item from 
 *  the drop-down list in the ComboBox control, the data item appears 
 *  in the prompt area of the control. 
 *
 *  <p>One difference between the controls is that the prompt area of 
 *  the ComboBox control is implemented by using the TextInput control, 
 *  instead of the Label control for the DropDownList control. 
 *  Therefore, a user can edit the prompt area of the control to enter 
 *  a value that is not one of the predefined options.</p>
 *
 *  <p>For example, the DropDownList control only lets the user select 
 *  from a list of predefined items in the control. 
 *  The ComboBox control lets the user either select a predefined item, 
 *  or enter a new item into the prompt area. 
 *  Your application can recognize that a new item has been entered and, 
 *  optionally, add it to the list of items in the control.</p>
 *
 *  <p>The ComboBox control also searches the item list as the user 
 *  enters characters into the prompt area. As the user enters characters, 
 *  the drop-down area of the control opens. 
 *  It then and scrolls to and highlights the closest match in the item list.</p>
 *
 *  <p><b>Note: </b>The Spark list-based controls (the Spark ListBase class and its subclasses
 *  such as ButtonBar, ComboBox, DropDownList, List, and TabBar) do not support the BasicLayout class
 *  as the value of the <code>layout</code> property. 
 *  Do not use BasicLayout with the Spark list-based controls.</p>
 * 
 *  <p>The ComboBox control has the following default characteristics:</p>
 *     <table class="innertable">
 *        <tr>
 *           <th>Characteristic</th>
 *           <th>Description</th>
 *        </tr>
 *        <tr>
 *           <td>Default size</td>
 *           <td>146 pixels wide by 23 pixels high</td>
 *        </tr>
 *        <tr>
 *           <td>Minimum size</td>
 *           <td>20 pixels wide by 23 pixels high</td>
 *        </tr>
 *        <tr>
 *           <td>Maximum size</td>
 *           <td>10000 pixels wide and 10000 pixels high</td>
 *        </tr>
 *        <tr>
 *           <td>Default skin class</td>
 *           <td>spark.skins.spark.ComboBoxSkin
 *               <p>spark.skins.spark.ComboBoxTextInputSkin</p></td>
 *        </tr>
 *     </table>
 *
 *  @mxml <p>The <code>&lt;s:ComboBox&gt;</code> tag inherits all of the tag 
 *  attributes of its superclass and adds the following tag attributes:</p>
 *
 *  <pre>
 *  &lt;s:ComboBox
 *    <strong>Properties</strong>
 *    itemMatchingFunction="null"
 *    labelToItemFunction="null"
 *    maxChars="0"
 *    openOnInput="true"
 *    restrict=""
 *
 *    <strong>Styles</strong>
 *    paddingBottom="3"
 *    paddingLeft="3"
 *    paddingRight="3"
 *    paddingTop="5"
 *  /&gt;
 *  </pre>
 *
 *  @see spark.skins.spark.ComboBoxSkin
 *  @see spark.skins.spark.ComboBoxTextInputSkin
 *  @includeExample examples/ComboBoxExample.mxml
 *  
 *  @langversion 3.0
 *  @playerversion Flash 10
 *  @playerversion AIR 1.5
 *  @productversion Flex 4
 */
public class ComboBox extends DropDownListBase
{
    //--------------------------------------------------------------------------
    //
    //  Skin Parts
    //
    //--------------------------------------------------------------------------    
    /**
     *  Optional skin part that holds the input text or the selectedItem text. 
     *  
     *  @langversion 3.0
     *  @playerversion Flash 10
     *  @playerversion AIR 1.5
     *  @productversion Flex 4
     */
    [SkinPart(required="false")]
    public var textInput:TextInput;
    
    //--------------------------------------------------------------------------
    //
    //  Class mixins
    //
    //--------------------------------------------------------------------------
    
    /**
     *  @private
     *  Placeholder for mixin by ComboBoxAccImpl.
     */
    mx_internal static var createAccessibilityImplementation:Function;
    
    /**
     *  Constructor. 
     *  
     *  @langversion 3.0
     *  @playerversion Flash 10
     *  @playerversion AIR 1.5
     *  @productversion Flex 4
     */
    public function ComboBox()
    {
        super();
        
        addEventListener(KeyboardEvent.KEY_DOWN, capture_keyDownHandler, true);
        allowCustomSelectedItem = true;
    }
    
    //--------------------------------------------------------------------------
    //
    //  Static Variables
    //
    //--------------------------------------------------------------------------
    /**
     *  Static constant representing the value of the <code>selectedIndex</code> property
     *  when the user enters a value into the prompt area, and the value is committed. 
     *  
     *  @langversion 3.0
     *  @playerversion Flash 10
     *  @playerversion AIR 1.5
     *  @productversion Flex 4
     */
    public static const CUSTOM_SELECTED_ITEM:int = ListBase.CUSTOM_SELECTED_ITEM;
    
    //--------------------------------------------------------------------------
    //
    //  Variables
    //
    //--------------------------------------------------------------------------
    
    private var isTextInputInFocus:Boolean;
    
    private var actualProposedSelectedIndex:Number = NO_SELECTION;  
    
    private var userTypedIntoText:Boolean;
    
    //--------------------------------------------------------------------------
    //
    //  Properties
    //
    //--------------------------------------------------------------------------
    
    //--------------------------------------------------------------------------
    //  itemMatchingFunction
    //--------------------------------------------------------------------------
    
    /**
     *  Specifies a callback function used to search the item list as the user 
     *  enters characters into the prompt area. 
     *  As the user enters characters, the drop-down area of the control opens. 
     *  It then and scrolls to and highlights the closest match in the item list.
     * 
     *  <p>The function referenced by this property takes an input string and returns
     *  the index of the items in the data provider that match the input. 
     *  The items are returned as a Vector.&lt;int&gt; of indices in the data provider. </p>
     * 
     *  <p>The callback function must have the following signature: </p>
     * 
     *  <pre>
     *    function myMatchingFunction(comboBox:ComboBox, inputText:String):Vector.&lt;int&gt;</pre>
     * 
     *  <p>If the value of this property is null, the ComboBox finds matches 
     *  using the default algorithm.  
     *  By default, if an input string of length n is equivalent to the first n characters 
     *  of an item (ignoring case), then it is a match to that item. For example, 'aRiz' 
     *  is a match to "Arizona" while 'riz' is not.</p>
     *
     *  <p>To disable search, create a callback function that returns an empty Vector.&lt;int&gt;.</p>
     * 
     *  @default null
     *  
     *  @langversion 3.0
     *  @playerversion Flash 10
     *  @playerversion AIR 1.5
     *  @productversion Flex 4
     */ 
    public var itemMatchingFunction:Function = null;     
    
    //--------------------------------------------------------------------------
    //  labelToItemFunction
    //--------------------------------------------------------------------------
    private var _labelToItemFunction:Function;
    private var labelToItemFunctionChanged:Boolean = false;
    
    /**
     *  Specifies a callback function to convert a new value entered 
     *  into the prompt area to the same data type as the data items in the data provider.
     *  The function referenced by this properly is called when the text in the prompt area 
     *  is committed, and is not found in the data provider. 
     * 
     *  <p>The callback function must have the following signature: </p>
     * 
     *  <pre>
     *    function myLabelToItem(value:String):Object</pre>
     * 
     *  <p>Where <code>value</code> is the String entered in the prompt area.
     *  The function returns an Object that is the same type as the items 
     *  in the data provider.</p>
     * 
     *  <p>The default callback function returns <code>value</code>. </p>
     *  
     *  @langversion 3.0
     *  @playerversion Flash 10
     *  @playerversion AIR 1.5
     *  @productversion Flex 4
     */ 
    public function set labelToItemFunction(value:Function):void
    {
        if (value == _labelToItemFunction)
            return;
        
        _labelToItemFunction = value;
        labelToItemFunctionChanged = true;
        invalidateProperties();
    }
    
    /**
     *  @private 
     */
    public function get labelToItemFunction():Function
    {
        return _labelToItemFunction;
    }
    
    //--------------------------------------------------------------------------
    //  maxChars
    //--------------------------------------------------------------------------
    
    private var _maxChars:int = 0;
    private var maxCharsChanged:Boolean = false;
    
    /**
     *  The maximum number of characters that the prompt area can contain, as entered by a user. 
     *  A value of 0 corresponds to no limit.
     * 
     *  @default 0
     *  
     *  @langversion 3.0
     *  @playerversion Flash 10
     *  @playerversion AIR 1.5
     *  @productversion Flex 4
     */ 
    public function set maxChars(value:int):void
    {
        if (value == _maxChars)
            return;
        
        _maxChars = value;
        maxCharsChanged = true;
        invalidateProperties();
    }
    
    /**
     *  @private 
     */
    public function get maxChars():int
    {
        return _maxChars;
    }
    
    //--------------------------------------------------------------------------
    //  openOnInput
    //--------------------------------------------------------------------------
    
    /**
     *  If <code>true</code>, the drop-down list opens when the user edits the prompt area.
     * 
     *  @default true 
     *  
     *  @langversion 3.0
     *  @playerversion Flash 10
     *  @playerversion AIR 1.5
     *  @productversion Flex 4
     */ 
    public var openOnInput:Boolean = true;
    
    //--------------------------------------------------------------------------
    //  restrict
    //--------------------------------------------------------------------------
    
    private var _restrict:String;
    private var restrictChanged:Boolean;
    
    /**
     *  Specifies the set of characters that a user can enter into the prompt area.
     *  By default, the user can enter any characters, corresponding to a value of
     *  an empty string.
     * 
     *  @default ""
     *  
     *  @langversion 3.0
     *  @playerversion Flash 10
     *  @playerversion AIR 1.5
     *  @productversion Flex 4
     */ 
    public function set restrict(value:String):void
    {
        if (value == _restrict)
            return;
        
        _restrict = value;
        restrictChanged = true;
        invalidateProperties();
    }
    
    /**
     *  @private 
     */
    public function get restrict():String
    {
        return _restrict;
    }
 
    //--------------------------------------------------------------------------
    //
    //  Overridden Properties
    //
    //--------------------------------------------------------------------------
    
    /**
     *  @private 
     */
    override public function set selectedIndex(value:int):void
    {
        super.selectedIndex = value;
        actualProposedSelectedIndex = value;
    }
    
    private var typicalItemChanged:Boolean = false;
    
    /**
     *  @private
     */
    override public function set typicalItem(value:Object):void
    {   
        if (value != typicalItem)
            return;
     
        super.typicalItem = value;
        
        typicalItemChanged = true;
        invalidateProperties();
    }
    
    /**
     *  @private 
     */
    override mx_internal function set userProposedSelectedIndex(value:Number):void
    {
        super.userProposedSelectedIndex = value;
        actualProposedSelectedIndex = value;
    }
    
    //--------------------------------------------------------------------------
    //
    //  Methods
    //
    //--------------------------------------------------------------------------
    
    /**
     *  @private 
     */ 
    private function processInputField():void
    {
        var matchingItems:Vector.<int>;
                
        if (!dataProvider || dataProvider.length <= 0)
            return;
        
        // If the textInput has been changed, then use the input string as the selectedItem
        actualProposedSelectedIndex = CUSTOM_SELECTED_ITEM; 
                    
        if (textInput.text != "")
        {
            if (itemMatchingFunction != null)
                matchingItems = itemMatchingFunction(this, textInput.text);
            else
                matchingItems = findMatchingItems(textInput.text);
            
            if (matchingItems.length > 0)
            {
                super.changeHighlightedSelection(matchingItems[0], true);
                
                var typedLength:int = textInput.text.length;
                var item:Object = dataProvider ? dataProvider.getItemAt(matchingItems[0]) : undefined;
                if (item)
                {
                    // If we found a match, then replace the textInput text with the match and 
                    // select the non-typed characters
                    var itemString:String = itemToLabel(item);
                    textInput.selectAll();
                    textInput.insertText(itemString);
                    textInput.selectRange(typedLength, itemString.length);
                }
            }
            else
            {
                super.changeHighlightedSelection(CUSTOM_SELECTED_ITEM);
            }
        }
        else
        {
            // If the input string is empty, then don't select anything
            super.changeHighlightedSelection(NO_SELECTION);  
        }
    }
    
    /**
     *  @private 
     */ 
    // Returns an array of possible values
    private function findMatchingItems(input:String):Vector.<int>
    {
        // For now, just select the first match
        var startIndex:int;
        var stopIndex:int;
        var retVal:int;  
        var retVector:Vector.<int> = new Vector.<int>;
                
        retVal = findStringLoop(input, 0, dataProvider.length); 
        
        if (retVal != -1)
            retVector.push(retVal);
        return retVector;
    }
    
    /**
     *  @private 
     */ 
    private function getCustomSelectedItem():*
    {
        // Grab the text from the textInput and process it through labelToItemFunction
        var input:String = textInput.text;
        if (input == "")
            return undefined;
        else if (labelToItemFunction != null)
            return _labelToItemFunction(input);
        else
            return input;
    }
    
    /**
     *  @private 
     *  Helper function to apply the textInput text to selectedItem
     */ 
    mx_internal function applySelection():void
    {
        if (actualProposedSelectedIndex == CUSTOM_SELECTED_ITEM)
        {
            var itemFromInput:* = getCustomSelectedItem();
            if (itemFromInput != undefined)
                selectedItem = itemFromInput;
            else
                selectedIndex = NO_SELECTION;
        }
        else
        {
            selectedIndex = actualProposedSelectedIndex;
        }
                
        textInput.selectRange(-1, -1);
        
        userTypedIntoText = false;
    }
    
    //--------------------------------------------------------------------------
    //
    //  Overridden methods
    //
    //--------------------------------------------------------------------------
    
    /**
     *  @private
     */
    override protected function initializeAccessibility():void
    {
        if (ComboBox.createAccessibilityImplementation != null)
            ComboBox.createAccessibilityImplementation(this);
    }
    
    /**
     *  @private 
     */
    override protected function commitProperties():void
    {        
        // Keep track of whether selectedIndex was programmatically changed
        var selectedIndexChanged:Boolean = _proposedSelectedIndex != NO_PROPOSED_SELECTION;
        
        // If selectedIndex was set to CUSTOM_SELECTED_ITEM, and no selectedItem was specified,
        // then don't change the selectedIndex
        if (_proposedSelectedIndex == CUSTOM_SELECTED_ITEM && 
            !_pendingSelectedItem)
        {
            _proposedSelectedIndex = NO_PROPOSED_SELECTION;
        }
                
        super.commitProperties();
        
        if (textInput)
        {
            if (maxCharsChanged)
            {
                textInput.maxChars = _maxChars;
                maxCharsChanged = false;
            }
            
            if (restrictChanged)
            {
                textInput.restrict = _restrict;
                restrictChanged = false;
            }
            
            if (typicalItemChanged)
            {
                if (typicalItem != null)
                {
                    var itemString:String = LabelUtil.itemToLabel(typicalItem, labelField, labelFunction);
                    textInput.widthInChars = itemString.length;
                }
                else
                {
                    // Just set it back to the default value
                    textInput.widthInChars = 10; 
                }
                
                typicalItemChanged = false;
            }
        }
        
        // Clear the TextInput because we were programmatically set to NO_SELECTION
        // We call this after super.commitProperties because commitSelection might have
        // changed the value to NO_SELECTION
        if (selectedIndexChanged && selectedIndex == NO_SELECTION)
            textInput.text = "";
    }    
    
    /**
     *  @private 
     */ 
    override mx_internal function updateLabelDisplay(displayItem:* = undefined):void
    {
        super.updateLabelDisplay();
        
        if (textInput)
        {
            if (displayItem == undefined)
                displayItem = selectedItem;
            if (displayItem != null && displayItem != undefined)
            {
                textInput.text = LabelUtil.itemToLabel(displayItem, labelField, labelFunction);
            }
        }
    }
    
    /**
     *  @private 
     */     
    override protected function partAdded(partName:String, instance:Object):void
    {
        super.partAdded(partName, instance);
        
        if (instance == textInput)
        {
            updateLabelDisplay();
            textInput.addEventListener(TextOperationEvent.CHANGE, textInput_changeHandler);
            textInput.addEventListener(FocusEvent.FOCUS_IN, textInput_focusInHandler, true);
            textInput.addEventListener(FocusEvent.FOCUS_OUT, textInput_focusOutHandler, true);
            textInput.maxChars = maxChars;
            textInput.restrict = restrict;
            textInput.focusEnabled = false;
            
            textInput.textDisplay.batchTextInput = false;
        }
    }
    
    /**
     *  @private 
     */     
    override protected function partRemoved(partName:String, instance:Object):void
    {
        super.partRemoved(partName, instance);
        
        if (instance == textInput)
        {
            textInput.removeEventListener(TextOperationEvent.CHANGE, textInput_changeHandler);
            textInput.removeEventListener(FocusEvent.FOCUS_IN, textInput_focusInHandler, true);
            textInput.removeEventListener(FocusEvent.FOCUS_OUT, textInput_focusOutHandler, true);
        }
    }
    
    /**
     *  @private 
     */ 
    override mx_internal function changeHighlightedSelection(newIndex:int, scrollToTop:Boolean = false):void
    {
        super.changeHighlightedSelection(newIndex, scrollToTop);
        
        if (newIndex > 0)
        {
            var item:Object = dataProvider ? dataProvider.getItemAt(newIndex) : undefined;
            if (item)
            {
                var itemString:String = itemToLabel(item);
                textInput.selectAll();
                textInput.insertText(itemString);
                textInput.selectAll();
             
                userTypedIntoText = false;
            }
        }
    }
    
    /**
     *  @private 
     */ 
    override mx_internal function findKey(eventCode:int):Boolean
    {
        return false;
    }
    
    // If the TextInput is in focus, listen for keyDown events in the capture phase so that 
    // we can process the navigation keys (UP/DOWN, PGUP/PGDN, HOME/END). If the ComboBox is in 
    // focus, just handle keyDown events in the bubble phase
    
    /**
     *  @private 
     */ 
    override protected function keyDownHandler(event:KeyboardEvent) : void
    {        
        if (!isTextInputInFocus)
            keyDownHandlerHelper(event);
    }
    
    /**
     *  @private 
     */ 
    protected function capture_keyDownHandler(event:KeyboardEvent):void
    {        
        if (isTextInputInFocus)
            keyDownHandlerHelper(event);
    }
    
    /**
     *  @private 
     */ 
    mx_internal function keyDownHandlerHelper(event:KeyboardEvent):void
    {
        super.keyDownHandler(event);
        
        if (event.keyCode == Keyboard.ENTER && !isDropDownOpen) 
        {
            // commit the current text
            applySelection();
        }
        else if (event.keyCode == Keyboard.ESCAPE)
        {
            // Restore the previous selectedItem
            textInput.text = itemToLabel(selectedItem);
            changeHighlightedSelection(selectedIndex);
        }
    }
    
    /**
     *  @private
     */
    override public function setFocus():void
    {
        if (stage)
        {            
            stage.focus = textInput.textDisplay;            
        }
    }
    
    /**
     *  @private
     */
    override protected function isOurFocus(target:DisplayObject):Boolean
    {
        return target == textInput.textDisplay;
    }
    
    /**
     *  @private
     */
    override protected function focusInHandler(event:FocusEvent):void
    {
        super.focusInHandler(event);
        
        // Since the API ignores the visual editable and selectable 
        // properties make sure the selection should be set first.
        if (textInput && 
            (textInput.editable || textInput.selectable))
        {
            // Workaround RET handling the mouse and performing its own selection logic
            callLater(textInput.selectAll);
        }
        
        userTypedIntoText = false;
    }
    
    /**
     *  @private
     */
    override protected function focusOutHandler(event:FocusEvent):void
    {
        // always commit the selection if we focus out        
        if (!isDropDownOpen)
        {
            if (textInput.text != itemToLabel(selectedItem))
                applySelection();
        }
            
        super.focusOutHandler(event);
    }
    
    /**
     *  @private
     */
    override mx_internal function dropDownController_openHandler(event:DropDownEvent):void
    {
        super.dropDownController_openHandler(event);
        
        // If the user typed in text, start off by not showing any selection
        // If this does match, then processInputField will highlight the match
        userProposedSelectedIndex = userTypedIntoText ? NO_SELECTION : selectedIndex;  
    }
    
    /**
     *  @private 
     */ 
    override protected function dropDownController_closeHandler(event:DropDownEvent):void
    {        
        super.dropDownController_closeHandler(event);      
        
        // Commit the textInput text as the selection
        if (!event.isDefaultPrevented())
        {
            applySelection();
        }
    }
    
    //--------------------------------------------------------------------------
    //
    //  Event Handlers
    //
    //--------------------------------------------------------------------------
    
    /**
     *  @private 
     */ 
    private function textInput_focusInHandler(event:FocusEvent):void
    {
        isTextInputInFocus = true;
    }
    
    /**
     *  @private 
     */ 
    private function textInput_focusOutHandler(event:FocusEvent):void
    {
        isTextInputInFocus = false;
    }
    
    /**
     *  @private 
     */ 
    protected function textInput_changeHandler(event:TextOperationEvent):void
    {  
        userTypedIntoText = true;
        
        var operation:FlowOperation = event.operation;

        // Close the dropDown if we press delete or cut the selected text
        if (operation is DeleteTextOperation || operation is CutOperation)
        {
            super.changeHighlightedSelection(CUSTOM_SELECTED_ITEM);
        }
        else
        {
            if (openOnInput)
            {
                if (!isDropDownOpen)
                {
                    // Open the dropDown if it isn't already open
                    openDropDown();
                    addEventListener(DropDownEvent.OPEN, editingOpenHandler);
                    return;
                }   
            }
            
            processInputField();
        }
    }
    
    /**
     *  @private 
     */ 
    private function editingOpenHandler(event:DropDownEvent):void
    {
        removeEventListener(DropDownEvent.OPEN, editingOpenHandler);
        processInputField();
    }
        
}
}