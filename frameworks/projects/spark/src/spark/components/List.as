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
import flash.display.DisplayObject;
import flash.events.Event;
import flash.events.KeyboardEvent;
import flash.events.MouseEvent;
import flash.geom.Point;
import flash.ui.Keyboard;

import mx.core.DragSource;
import mx.core.EventPriority;
import mx.core.IFactory;
import mx.core.IFlexDisplayObject;
import mx.core.IUID;
import mx.core.IVisualElement;
import mx.core.mx_internal;
import mx.events.DragEvent;
import mx.events.SandboxMouseEvent;
import mx.managers.DragManager;
import mx.managers.IFocusManagerComponent;
import mx.utils.ObjectUtil;
import mx.utils.UIDUtil;

import spark.components.supportClasses.ListBase;
import spark.core.NavigationUnit;
import spark.events.IndexChangeEvent;
import spark.events.RendererExistenceEvent;
import spark.layouts.supportClasses.DropLocation;

use namespace mx_internal;  //ListBase and List share selection properties that are mx_internal

/**
 *  @copy spark.components.supportClasses.GroupBase#style:alternatingItemColors
 * 
 *  @default undefined
 *  
 *  @langversion 3.0
 *  @playerversion Flash 10
 *  @playerversion AIR 1.5
 *  @productversion Flex 4
 */
[Style(name="alternatingItemColors", type="Array", arrayType="uint", format="Color", inherit="yes", theme="spark")]

/**
 *  The alpha of the border for this component.
 * 
 *  @langversion 3.0
 *  @playerversion Flash 10
 *  @playerversion AIR 1.5
 *  @productversion Flex 4
 */
[Style(name="borderAlpha", type="Number", inherit="no", theme="spark")]

/**
 *  The color of the border for this component.
 * 
 *  @langversion 3.0
 *  @playerversion Flash 10
 *  @playerversion AIR 1.5
 *  @productversion Flex 4
 */
[Style(name="borderColor", type="uint", format="Color", inherit="no", theme="spark")]

/**
 *  Controls the visibility of the border for this component.
 * 
 *  @langversion 3.0
 *  @playerversion Flash 10
 *  @playerversion AIR 1.5
 *  @productversion Flex 4
 */
[Style(name="borderVisible", type="Boolean", inherit="no", theme="spark")]

/**
 *  The alpha of the content background for this component.
 * 
 *  @langversion 3.0
 *  @playerversion Flash 10
 *  @playerversion AIR 1.5
 *  @productversion Flex 4
 */
[Style(name="contentBackgroundAlpha", type="Number", inherit="yes", theme="spark")]

/**
 *  @copy spark.components.supportClasses.GroupBase#style:contentBackgroundColor
 *   
 *  @default 0xFFFFFF
 *  
 *  @langversion 3.0
 *  @playerversion Flash 10
 *  @playerversion AIR 1.5
 *  @productversion Flex 4
 */
[Style(name="contentBackgroundColor", type="uint", format="Color", inherit="yes", theme="spark")]

/**
 *  The class to create instance of for the drag proxy during drag
 *  and drop operations initiated by the List.
 *
 *  Must be of type <code>IFlexDisplayObject</code>.
 *
 *  If the class implements the <code>ILayoutManagerClient</code> interface,
 *  then the instance will be validated by the DragManager.
 *
 *  If the class implements the <code>IVisualElement</code> interface,
 *  then the instance's <code>owner</code> property will be set to the List
 *  that initiates the drag.
 *
 *  The AIR DragManager takes a snapshot of the instance, while
 *  the non-AIR DragManager uses the instance directly.
 *
 *  @default spark.components.supportClasses.ListItemDragProxy
 *
 *  @langversion 3.0
 *  @playerversion Flash 10
 *  @playerversion AIR 1.5
 *  @productversion Flex 4
 */
[Style(name="dragIndicatorClass", type="Class", inherit="no")]

/**
 *  @copy spark.components.supportClasses.GroupBase#style:rollOverColor
 *   
 *  @default 0xCEDBEF
 *  
 *  @langversion 3.0
 *  @playerversion Flash 10
 *  @playerversion AIR 1.5
 *  @productversion Flex 4
 */
[Style(name="rollOverColor", type="uint", format="Color", inherit="yes", theme="spark")]

/**
 *  @copy spark.components.supportClasses.GroupBase#style:selectionColor
 *
 *  @default 0xA8C6EE
 *  
 *  @langversion 3.0
 *  @playerversion Flash 10
 *  @playerversion AIR 1.5
 *  @productversion Flex 4
 */
[Style(name="selectionColor", type="uint", format="Color", inherit="yes", theme="spark")]

/**
 *  @copy spark.components.supportClasses.GroupBase#style:symbolColor
 *   
 *  @default 0x000000
 *  
 *  @langversion 3.0
 *  @playerversion Flash 10
 *  @playerversion AIR 1.5
 *  @productversion Flex 4
 */ 
[Style(name="symbolColor", type="uint", format="Color", inherit="yes", theme="spark")]

//--------------------------------------
//  Other metadata
//--------------------------------------

[IconFile("List.png")]
[DefaultTriggerEvent("change")]

/**
 *  The List control displays a vertical list of items.
 *  Its functionality is very similar to that of the SELECT
 *  form element in HTML.
 *  If there are more items than can be displayed at once, it
 *  can display a vertical scroll bar so the user can access
 *  all items in the list.
 *  An optional horizontal scroll bar lets the user view items
 *  when the full width of the list items is unlikely to fit.
 *  The user can select one or more items from the list, depending
 *  on the value of the <code>allowMultipleSelection</code> property.
 *
 *  @mxml <p>The <code>&lt;s:List&gt;</code> tag inherits all of the tag 
 *  attributes of its superclass and adds the following tag attributes:</p>
 *
 *  <pre>
 *  &lt;s:List
 *    <strong>Properties</strong>
 *    allowMultipleSelection="false"
 *    selectedIndices="null"
 *    selectedItems="null"
 *    useVirtualLayout="true"
 * 
 *    <strong>Styles</strong>
 *    alternatingItemColors="undefined"
 *    contentBackgroundColor="0xFFFFFF"
 *    rollOverColor="0xCEDBEF"
 *    selectionColor="0xA8C6EE"
 *    symbolColor="0x000000"
 *  /&gt;
 *  </pre>
 *
 *  @see spark.skins.spark.ListSkin
 *
 *  @includeExample examples/ListExample.mxml
 *  
 *  @langversion 3.0
 *  @playerversion Flash 10
 *  @playerversion AIR 1.5
 *  @productversion Flex 4
 */
public class List extends ListBase implements IFocusManagerComponent
{
    include "../core/Version.as";
    
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
    public function List()
    {
        super();
        useVirtualLayout = true;
    }
    
    //--------------------------------------------------------------------------
    //
    //  Variables
    //
    //--------------------------------------------------------------------------
    
    /**
     *  @private
     *  The point where the mouse down event was received.
     *  Used to track whether a drag operation should be initiated when the user
     *  drags further than a certain threshold. 
     */
    private var mouseDownPoint:Point;

    /**
     *  @private
     *  The index of the element the mouse down event was received for. Used to
     *  track which is the "focus item" for a drag and drop operation.
     */
    private var mouseDownIndex:int = -1;
    
    //--------------------------------------------------------------------------
    //
    //  Skin Parts
    //
    //--------------------------------------------------------------------------

    [SkinPart(required="false", type="flash.display.DisplayObject")]

    /**
     *  A skin part that defines a drop indicator. The drop indicator is resized
     *  and positioned by the layout to outline the insert location when dragging
     *  over the List.
     *  
     *  @langversion 3.0
     *  @playerversion Flash 10
     *  @playerversion AIR 1.5
     *  @productversion Flex 4
     */
    public var dropIndicator:IFactory; 

    //----------------------------------
    //  scroller
    //----------------------------------

    [SkinPart(required="false")]

    /**
     *  The optional Scroller used to scroll the List.
     *  
     *  @langversion 3.0
     *  @playerversion Flash 10
     *  @playerversion AIR 1.5
     *  @productversion Flex 4
     */
    public var scroller:Scroller;
    
    //--------------------------------------------------------------------------
    //
    //  Properties
    //
    //--------------------------------------------------------------------------
    
    //----------------------------------
    //  allowMultipleSelection
    //----------------------------------
    
    private var _allowMultipleSelection:Boolean = false;
    
    /**
     *  If <code>true</code> multiple selections is enabled. 
     *  When switched at run time, the current selection
     *  is cleared. 
     *
     *  @default false
     *  
     *  @langversion 3.0
     *  @playerversion Flash 10
     *  @playerversion AIR 1.5
     *  @productversion Flex 4
     */
    public function get allowMultipleSelection():Boolean
    {
        return _allowMultipleSelection;
    }
    
    /**
     *  @private
     */
    public function set allowMultipleSelection(value:Boolean):void
    {
        if (value == _allowMultipleSelection)
            return;     
        
        _allowMultipleSelection = value; 
    }
    
    //----------------------------------
    //  dragEnabled
    //----------------------------------
    
    /**
     *  @private
     *  Storage for the dragEnabled property.
     */
    private var _dragEnabled:Boolean = false;
    
    [Inspectable(defaultValue="false")]
    
    /**
     *  A flag that indicates whether you can drag items out of
     *  this control and drop them on other controls.
     *  If <code>true</code>, dragging is enabled for the control.
     *  If the <code>dropEnabled</code> property is also <code>true</code>,
     *  you can drag items and drop them within this control
     *  to reorder the items.
     *
     *  @default false
     *  
     *  @langversion 3.0
     *  @playerversion Flash 10
     *  @playerversion AIR 1.5
     *  @productversion Flex 4
     */
    public function get dragEnabled():Boolean
    {
        return _dragEnabled;
    }
    
    /**
     *  @private
     */
    public function set dragEnabled(value:Boolean):void
    {
        if (value == _dragEnabled)
            return;
        _dragEnabled = value;
        
        if (_dragEnabled)
        {
            addEventListener(DragEvent.DRAG_START, dragStartHandler, false, EventPriority.DEFAULT_HANDLER);
            addEventListener(DragEvent.DRAG_COMPLETE, dragCompleteHandler, false, EventPriority.DEFAULT_HANDLER);
        }
        else
        {
            removeEventListener(DragEvent.DRAG_START, dragStartHandler, false);
            removeEventListener(DragEvent.DRAG_COMPLETE, dragCompleteHandler, false);
        }
    }
    
    //----------------------------------
    //  dragMoveEnabled
    //----------------------------------
    
    /**
     *  @private
     *  Storage for the dragMoveEnabled property.
     */
    private var _dragMoveEnabled:Boolean = false;
    
    [Inspectable(defaultValue="false")]
    
    /**
     *  A flag that indicates whether items can be moved instead
     *  of just copied from the control as part of a drag-and-drop
     *  operation.
     *  If <code>true</code>, and the <code>dragEnabled</code> property
     *  is <code>true</code>, items can be moved.
     *  Often the data provider cannot or should not have items removed
     *  from it, so a MOVE operation should not be allowed during
     *  drag-and-drop.
     *
     *  @default false
     *  
     *  @langversion 3.0
     *  @playerversion Flash 10
     *  @playerversion AIR 1.5
     *  @productversion Flex 4
     */
    public function get dragMoveEnabled():Boolean
    {
        return _dragMoveEnabled;
    }
    
    /**
     *  @private
     */
    public function set dragMoveEnabled(value:Boolean):void
    {
        _dragMoveEnabled = value;
    }

    //----------------------------------
    //  dropEnabled
    //----------------------------------

    /**
     *  @private
     *  Storage for the <code>dropEnabled</code> property.
     */
    private var _dropEnabled:Boolean = false;

    [Inspectable(defaultValue="false")]

    /**
     *  A flag that indicates whether dragged items can be dropped onto the 
     *  control.
     *
     *  <p>If you set this property to <code>true</code>,
     *  the control accepts all data formats, and assumes that
     *  the dragged data matches the format of the data in the data provider.
     *  If you want to explicitly check the data format of the data
     *  being dragged, you must handle one or more of the drag events,
     *  such as <code>dragEnter</code> and <code>dragOver</code>, 
     *  and call the DragEvent's <code>preventDefault()</code> method 
     *  to customize the way the list class accepts dropped data.</p>
     *
     *  <p>When you set <code>dropEnabled</code> to <code>true</code>, 
     *  Flex automatically calls the <code>showDropFeedback()</code> 
     *  and <code>hideDropFeedback()</code> methods to display the drop
     *  indicator.</p>
     *
     *  @default false
     *  
     *  @langversion 3.0
     *  @playerversion Flash 10
     *  @playerversion AIR 1.5
     *  @productversion Flex 4
     */
    public function get dropEnabled():Boolean
    {
        return _dropEnabled;
    }

    /**
     *  @private
     */
    public function set dropEnabled(value:Boolean):void
    {
        if (value == _dropEnabled)
            return;
        _dropEnabled = value;
        
        if (_dropEnabled)
        {
            addEventListener(DragEvent.DRAG_ENTER, dragEnterHandler, false, EventPriority.DEFAULT_HANDLER);
            addEventListener(DragEvent.DRAG_EXIT, dragExitHandler, false, EventPriority.DEFAULT_HANDLER);
            addEventListener(DragEvent.DRAG_OVER, dragOverHandler, false, EventPriority.DEFAULT_HANDLER);
            addEventListener(DragEvent.DRAG_DROP, dragDropHandler, false, EventPriority.DEFAULT_HANDLER);
        }
        else
        {
            removeEventListener(DragEvent.DRAG_ENTER, dragEnterHandler, false);
            removeEventListener(DragEvent.DRAG_EXIT, dragExitHandler, false);
            removeEventListener(DragEvent.DRAG_OVER, dragOverHandler, false);
            removeEventListener(DragEvent.DRAG_DROP, dragDropHandler, false);
        }
    }

    //----------------------------------
    //  selectedIndices
    //----------------------------------
    
    /**
     *  @private
     *  Internal storage for the selectedIndices property and invalidation variables.
     */
    private var _selectedIndices:Vector.<int>;
    private var _proposedSelectedIndices:Vector.<int> = new Vector.<int>(); 
    private var multipleSelectionChanged:Boolean; 
    
    [Bindable("change")]
    /**
     *  A Vector of ints representing the indices of the currently selected  
     *  item or items. 
     *  If multiple selection is disabled by setting 
     *  <code>allowMultipleSelection</code> to <code>false</code>, and this property  
     *  is set, the data item corresponding to the first index in the Vector is selected.  
     *
     *  <p>If multiple selection is enabled by setting 
     *  <code>allowMultipleSelection</code> to <code>true</code>, this property  
     *  contains a list of the selected indices in the reverse order in which they were selected. 
     *  That means the first element in the Vector corresponds to the last item selected.</p>
     *  
     *  @default null
     *  
     *  @langversion 3.0
     *  @playerversion Flash 10
     *  @playerversion AIR 1.5
     *  @productversion Flex 4
     */
    public function get selectedIndices():Vector.<int>
    {
        return _selectedIndices;
    }
    
    /**
     *  @private
     */
    public function set selectedIndices(value:Vector.<int>):void
    {
        if (_proposedSelectedIndices == value)
            return; 
        
        _proposedSelectedIndices = value;
        multipleSelectionChanged = true;  
        invalidateProperties();
    }
    
    //----------------------------------
    //  selectedItems
    //----------------------------------

    [Bindable("change")]
    /**
     *  An Vector of Objects representing the currently selected data items. 
     *  If multiple selection is disabled by setting <code>allowMultipleSelection</code>
     *  to <code>false</code>, and this property is set, the data item 
     *  corresponding to the first item in the Vector is selected.  
     *
     *  <p>If multiple selection is enabled by setting 
     *  <code>allowMultipleSelection</code> to <code>true</code>, this property  
     *  contains a list of the selected items in the reverse order in which they were selected. 
     *  That means the first element in the Vector corresponds to the last item selected.</p>
     * 
     *  @default null
     *  
     *  @langversion 3.0
     *  @playerversion Flash 10
     *  @playerversion AIR 1.5
     *  @productversion Flex 4
     */
    public function get selectedItems():Vector.<Object>
    {
        var result:Vector.<Object>;
        
        if (selectedIndices)
        {
            result = new Vector.<Object>();
            
            var count:int = selectedIndices.length;
            
            for (var i:int = 0; i < count; i++)
                result[i] = dataProvider.getItemAt(selectedIndices[i]);  
        }
        
        return result;
    }
    
    /**
     *  @private
     */
    public function set selectedItems(value:Vector.<Object>):void
    {
        var indices:Vector.<int>;
        
        if (value)
        {
            indices = new Vector.<int>();
            
            var count:int = value.length;
            
            for (var i:int = 0; i < count; i++)
            {
                // FIXME (dsubrama): What exactly should we do if an 
                //invalid item is in the selectedItems vector? 
                var index:int = dataProvider.getItemIndex(value[i]);
                if (index != -1)
                { 
                    indices.splice(0, 0, index);   
                }
                if (index == -1)
                {
                    indices = new Vector.<int>();
                    break;  
                }
            }
        }
        
        _proposedSelectedIndices = indices;
        multipleSelectionChanged = true;
        invalidateProperties(); 
    }

    //----------------------------------
    //  useVirtualLayout
    //----------------------------------

    /**
     *  @inheritDoc
     *
     *  @default true
     *  
     *  @langversion 3.0
     *  @playerversion Flash 10
     *  @playerversion AIR 1.5
     *  @productversion Flex 4
     */
    override public function get useVirtualLayout():Boolean
    {
        return super.useVirtualLayout;
    }

    /**
     *  Overrides the inherited default property , it is true for this class.
     * 
     *  Sets the value of the <code>useVirtualLayout</code> property
     *  of the layout associated with this control.  
     *  If the layout is subsequently replaced and the value of this 
     *  property is <code>true</code>, then the new layout's 
     *  <code>useVirtualLayout</code> property is set to <code>true</code>.
     *
     *  @default true
     *  
     *  @langversion 3.0
     *  @playerversion Flash 10
     *  @playerversion AIR 1.5
     *  @productversion Flex 4
     */
    override public function set useVirtualLayout(value:Boolean):void
    {
        super.useVirtualLayout = value;
    }
        
    //--------------------------------------------------------------------------
    //
    //  Overridden Methods
    //
    //--------------------------------------------------------------------------
    
     /**
     *  @private
     */
    override protected function commitProperties():void
    {
        super.commitProperties(); 
        
        if (multipleSelectionChanged)
        {
            commitSelection(); 
            multipleSelectionChanged = false; 
        }
    }
    
    /**
     *  @private
     */
    override public function set hasFocusableChildren(value:Boolean):void
    {
        super.hasFocusableChildren = value;
        if (scroller)
            scroller.hasFocusableChildren = value;
    }
    
    /**
     *  @private
     *  Let ListBase handle single selection and afterwards come in and 
     *  handle multiple selection via the commitMultipleSelection() 
     *  helper method. 
     */
    override protected function commitSelection(dispatchChangedEvents:Boolean = true):Boolean
    {
        var oldSelectedIndex:Number = _selectedIndex;
        var oldCaretIndex:Number = _caretIndex;  
        
        // Ensure that multiple selection is allowed and that proposed 
        // selected indices honors it. For example, in the single 
        // selection case, proposedSelectedIndices should only be a 
        // vector of 1 entry. If its not, we pare it down and select the 
        // first item.  
        if (!allowMultipleSelection && !isEmpty(_proposedSelectedIndices))
        {
            var temp:Vector.<int> = new Vector.<int>(); 
            temp.push(_proposedSelectedIndices[0]); 
            _proposedSelectedIndices = temp;  
        }
        // Keep _proposedSelectedIndex in-sync with multiple selection properties. 
        if (!isEmpty(_proposedSelectedIndices))
           _proposedSelectedIndex = getFirstItemValue(_proposedSelectedIndices); 
        
        // Let ListBase handle the validating and commiting of the single-selection
        // properties.  
        var retVal:Boolean = super.commitSelection(false); 
        
        // If super.commitSelection returns a value of false, 
        // the selection was cancelled, so return false and exit. 
        if (!retVal)
            return false; 
        
        // Now keep _proposedSelectedIndices in-sync with single selection 
        // properties now that the single selection properties have been 
        // comitted.  
        if (selectedIndex > NO_SELECTION)
        {
            if (_proposedSelectedIndices && _proposedSelectedIndices.indexOf(selectedIndex) == -1)
                _proposedSelectedIndices.push(selectedIndex);
        }
        
        // Validate and commit the multiple selection related properties. 
        commitMultipleSelection(); 
        
        // Set the caretIndex based on the current selection 
        setCurrentCaretIndex(selectedIndex);
        
        // And dispatch change and caretChange events so that all of 
        // the bindings update correctly. 
        if (dispatchChangedEvents && retVal)
        {
            var e:IndexChangeEvent = new IndexChangeEvent(IndexChangeEvent.CHANGE);
            e.oldIndex = oldSelectedIndex;
            e.newIndex = _selectedIndex;
            dispatchEvent(e);
            
            e = new IndexChangeEvent(IndexChangeEvent.CARET_CHANGE); 
            e.oldIndex = oldCaretIndex; 
            e.newIndex = caretIndex;
            dispatchEvent(e);    
        }
        
        return retVal; 
    }
    
    /**
     *  @private
     *  Given a new selection interval, figure out which
     *  items are newly added/removed from the selection interval and update
     *  selection properties and view accordingly. 
     */
    protected function commitMultipleSelection():void
    {
        var removedItems:Vector.<int> = new Vector.<int>();
        var addedItems:Vector.<int> = new Vector.<int>();
        var i:int;
        var count:int;
        
        if (!isEmpty(_selectedIndices) && !isEmpty(_proposedSelectedIndices))
        {
            // Changing selection, determine which items were added to the 
            // selection interval 
            count = _proposedSelectedIndices.length;
            for (i = 0; i < count; i++)
            {
                if (_selectedIndices.indexOf(_proposedSelectedIndices[i]) < 0)
                    addedItems.push(_proposedSelectedIndices[i]);
            }
            // Then determine which items were removed from the selection 
            // interval 
            count = _selectedIndices.length; 
            for (i = 0; i < count; i++)
            {
                if (_proposedSelectedIndices.indexOf(_selectedIndices[i]) < 0)
                    removedItems.push(_selectedIndices[i]);
            }
        }
        else if (!isEmpty(_selectedIndices))
        {
            // Going to a null selection, remove all
            removedItems = _selectedIndices;
        }
        else if (!isEmpty(_proposedSelectedIndices))
        {
            // Going from a null selection, add all
            addedItems = _proposedSelectedIndices;
        }
         
        // De-select the old items that were selected 
        if (removedItems.length > 0)
        {
            count = removedItems.length;
            for (i = 0; i < count; i++)
            {
                itemSelected(removedItems[i], false);
            }
        }
        
        // Select the new items in the new selection interval 
        if (!isEmpty(_proposedSelectedIndices))
        {
            count = _proposedSelectedIndices.length;
            for (i = 0; i < count; i++)
            {
                itemSelected(_proposedSelectedIndices[i], true);
            }
        }
        
        // Commit the selected indices and put _proposedSelectedIndices
        // back to its default value.  
        _selectedIndices = _proposedSelectedIndices;
        _proposedSelectedIndices = new Vector.<int>();
    }
    
    /**
     *  @private
     */
    override protected function itemSelected(index:int, selected:Boolean):void
    {
        super.itemSelected(index, selected);
        
        var renderer:Object = dataGroup ? dataGroup.getElementAt(index) : null;
        
        if (renderer is IItemRenderer)
        {
            IItemRenderer(renderer).selected = selected;
        }
    }
    
    /**
     *  @private 
     */
    override protected function itemShowingCaret(index:int, showsCaret:Boolean):void
    {
        super.itemShowingCaret(index, showsCaret); 
        
        var renderer:Object = dataGroup ? dataGroup.getElementAt(index) : null;
        
        if (renderer is IItemRenderer)
        {
            IItemRenderer(renderer).showsCaret = showsCaret;
        }
    }
    
    /**
     *  @private
     */
    override mx_internal function isItemIndexSelected(index:int):Boolean
    {
        if (allowMultipleSelection && (selectedIndices != null))
            return selectedIndices.indexOf(index) != -1;
        
        return index == selectedIndex;
    }
    
    /**
     *  @private
     */
    override protected function partAdded(partName:String, instance:Object):void
    {
        super.partAdded(partName, instance);
        if (instance == dataGroup)
        {
            dataGroup.addEventListener(
                RendererExistenceEvent.RENDERER_ADD, dataGroup_rendererAddHandler);
            dataGroup.addEventListener(
                RendererExistenceEvent.RENDERER_REMOVE, dataGroup_rendererRemoveHandler);
        }
        if (instance == scroller)
            scroller.hasFocusableChildren = hasFocusableChildren;
    }

    /**
     *  @private
     */
    override protected function partRemoved(partName:String, instance:Object):void
    {
        if (instance == dataGroup)
        {
            dataGroup.removeEventListener(
                RendererExistenceEvent.RENDERER_ADD, dataGroup_rendererAddHandler);
            dataGroup.removeEventListener(
                RendererExistenceEvent.RENDERER_REMOVE, dataGroup_rendererRemoveHandler);
        }
        
        super.partRemoved(partName, instance);
    }
    
    /**
     *  @private
     *  Called when an item has been added to this component.
     */
    override protected function itemAdded(index:int):void
    {
        adjustSelection(index, true); 
    }
    
    /**
     *  @private
     *  Called when an item has been removed from this component.
     */
    override protected function itemRemoved(index:int):void
    {
        adjustSelection(index, false);        
    }
    
    //--------------------------------------------------------------------------
    //
    //  Private Methods
    //
    //--------------------------------------------------------------------------
    
    /**
     *  @private
     *  Returns the index of the last selected item. In single 
     *  selection, this is just selectedIndex. In multiple 
     *  selection, this is the index of the first selected item.  
     */
    private function getLastSelectedIndex():int
    {
        if (selectedIndices && selectedIndices.length > 0)
            return selectedIndices[selectedIndices.length - 1]; 
        else 
            return 0; 
    }
    
    /**
     *  @private
     *  Given a Vector, returns the value of the first item, 
     *  or -1 if there are no items in the Vector; 
     */
    private function getFirstItemValue(v:Vector.<int>):int
    {
        if (v && v.length > 0)
            return v[0]; 
        else 
            return -1; 
    }
    
    /**
     *  @private
     *  Returns true if v is null or an empty Vector.
     */
    private function isEmpty(v:Vector.<int>):Boolean
    {
        return v == null || v.length == 0;
    }
    
    /**
     *  @private
     *  Taking into account which modifier keys were clicked, the new
     *  selectedIndices interval is calculated. 
     */
    private function calculateSelectedIndicesInterval(renderer:IVisualElement, shiftKey:Boolean, ctrlKey:Boolean):Vector.<int>
    {
        var i:int; 
        var interval:Vector.<int> = new Vector.<int>();  
        var index:Number = dataGroup.getElementIndex(renderer); 
        
        if (!shiftKey)
        {
            if (ctrlKey)
            {
                if (!isEmpty(selectedIndices))
                {
                    // Quick check to see if selectedIndices had only one selected item
                    // and that item was de-selected
                    if (selectedIndices.length == 1 && (selectedIndices[0] == index))
                    {
                        // We need to respect requireSelection 
                        if (!requireSelection)
                            return interval; 
                        else 
                        {
                            interval.splice(0, 0, selectedIndices[0]); 
                            return interval; 
                        }
                    }
                    else
                    {
                        // Go through and see if the index passed in was in the 
                        // selection model. If so, leave it out when constructing
                        // the new interval so it is de-selected. 
                        var found:Boolean = false; 
                        for (i = 0; i < _selectedIndices.length; i++)
                        {
                            if (_selectedIndices[i] == index)
                                found = true; 
                            else if (_selectedIndices[i] != index)
                                interval.splice(0, 0, _selectedIndices[i]);
                        }
                        if (!found)
                        {
                            // Nothing from the selection model was de-selected. 
                            // Instead, the Ctrl key was held down and we're doing a  
                            // new add. 
                            interval.splice(0, 0, index);   
                        }
                        return interval; 
                    } 
                }
                // Ctrl+click with no previously selected items 
                else
                { 
                    interval.splice(0, 0, index); 
                    return interval; 
            }
            }
            // A single item was newly selected, add that to the selection interval.  
            else 
            { 
                interval.splice(0, 0, index); 
                return interval; 
        }
        }
        else // shiftKey
        {
            // A contiguous selection action has occurred. Figure out which new 
            // indices to add to the selection interval and return that. 
            var start:int = (!isEmpty(selectedIndices)) ? selectedIndices[selectedIndices.length - 1] : 0; 
            var end:int = index; 
            if (start < end)
            {
                for (i = start; i <= end; i++)
                {
                    interval.splice(0, 0, i); 
                }
            }
            else 
            {
                for (i = start; i >= end; i--)
                {
                    interval.splice(0, 0, i); 
                }
            }
            return interval; 
        }
    }
    
    //--------------------------------------------------------------------------
    //
    //  Drag methods
    //
    //--------------------------------------------------------------------------
    
    /**
     *  The default handler for the <code>dragStart</code> event.
     *
     *  @param event The DragEvent object.
     *  
     *  @langversion 3.0
     *  @playerversion Flash 10
     *  @playerversion AIR 1.5
     *  @productversion Flex 4
     */
    protected function dragStartHandler(event:DragEvent):void
    {
        if (event.isDefaultPrevented())
            return;
        
        var dragSource:DragSource = new DragSource();
        addDragData(dragSource);
        DragManager.doDrag(this, 
                           dragSource, 
                           event, 
                           createDragIndicator(), 
                           0 /*xOffset*/, 
                           0 /*yOffset*/, 
                           0.8 /*imageAlpha*/, 
                           dragMoveEnabled);
    }
    
    /**
     *  @private
     *  Used to sort the selected indices during drag and drop operations.
     */
    private function compareValues(a:int, b:int):int
    {
        return a - b;
    } 
    
    /**
     *  Handles <code>DragEvent.DRAG_COMPLETE</code> events.  This method
     *  removes the items from the data provider.
     *
     *  @param event The DragEvent object.
     *  
     *  @langversion 3.0
     *  @playerversion Flash 10
     *  @playerversion AIR 1.5
     *  @productversion Flex 4
     */
    protected function dragCompleteHandler(event:DragEvent):void
    {
        if (event.isDefaultPrevented())
            return;
        
        // Remove the dragged items only if they were drag moved to
        // a different list. If the items were drag moved to this
        // list, the reordering was already handles in the 
        // DragEvent.DRAG_DROP listener.
        if (!dragMoveEnabled ||
            event.action != DragManager.MOVE || 
            event.relatedObject == this)
            return;
        
        // Clear the selection, but remember which items were moved
        var movedIndices:Vector.<int> = selectedIndices;
        selectedIndices = new Vector.<int>();
        
        // Remove the moved items
        movedIndices.sort(compareValues);
        var count:int = movedIndices.length;
        for (var i:int = count - 1; i >= 0; i--)
        {
            dataProvider.removeItemAt(movedIndices[i]);
        }
    }
    
    /**
     *  @private
     *
     *  Gets an instance of a class that displays the visuals
     *  during a drag and drop operation.
     */
    private function createDragIndicator():IFlexDisplayObject
    {
        var dragIndicator:IFlexDisplayObject;
        var dragIndicatorClass:Class = Class(getStyle("dragIndicatorClass"));
        if (dragIndicatorClass)
        {
            dragIndicator = new dragIndicatorClass();
            if (dragIndicator is IVisualElement)
                IVisualElement(dragIndicator).owner = this;
        }
        
        return dragIndicator;
    }
    
    /**
     *  Adds the selected items to the DragSource object as part of
     *  a drag-and-drop operation.
     *  Override this method to add other data to the drag source.
     * 
     *  @param ds The DragSource object to which to add the data.
     *  
     *  @langversion 3.0
     *  @playerversion Flash 10
     *  @playerversion AIR 1.5
     *  @productversion Flex 4
     */
    public function addDragData(dragSource:DragSource):void
    {
        dragSource.addHandler(copySelectedItemsForDragDrop, "orderedItems");
        
        // Calculate the index of the focus item within the vector
        // of ordered items returned for the "sourceOrderedItems" format.
        var caretIndex:int = 0;
        var draggedIndices:Vector.<int> = selectedIndices;
        var count:int = draggedIndices.length;
        for (var i:int = 0; i < count; i++)
        {
            if (mouseDownIndex > draggedIndices[i])
                caretIndex++;
        }
        dragSource.addData(caretIndex, "orderedItemsCaretIndex");
    }

    /**
     *  @private.
     */
    private function copySelectedItemsForDragDrop():Vector.<Object>
    {
        // Copy the vector so that we don't modify the original
         // since selectedIndices returns a reference.
        var draggedIndices:Vector.<int> = selectedIndices.slice(0, selectedIndices.length);
        var result:Vector.<Object> = new Vector.<Object>(draggedIndices.length);

        // Sort in the order of the data source
        draggedIndices.sort(compareValues);
        
        // Copy the items
        var count:int = draggedIndices.length;
        for (var i:int = 0; i < count; i++)
            result[i] = dataProvider.getItemAt(draggedIndices[i]);  
        return result;
    }
    
    /**
     *  Handles <code>MouseEvent.MOUSE_DOWN</code> events from any of the 
     *  item renderers. This method handles the updating and commitment 
     *  of selection as well as remembers the mouse down point and
     *  attaches <code>MouseEvent.MOUSE_MOVE</code> and
     *  <code>MouseEvent.MOUSE_UP</code> listeners in order to handle
     *  drag gestures.
     *
     *  @param event The MouseEvent object.
     *  
     *  @langversion 3.0
     *  @playerversion Flash 9
     *  @playerversion AIR 1.1
     *  @productversion Flex 3
     */
    protected function item_mouseDownHandler(event:MouseEvent):void
    {
        // Handle the fixup of selection 
        var newIndex:Number; 
        
        if (!allowMultipleSelection)
        {
            // Single selection case, set the selectedIndex 
            newIndex = dataGroup.getElementIndex(event.currentTarget as IVisualElement);  
            
            var currentRenderer:IItemRenderer;
            if (caretIndex >= 0)
            {
                currentRenderer = dataGroup.getElementAt(caretIndex) as IItemRenderer;
                if (currentRenderer)
                    currentRenderer.showsCaret = false;
            }
            
            // Check to see if we're deselecting the currently selected item 
            if (event.ctrlKey && selectedIndex == newIndex)
                selectedIndex = NO_SELECTION;
                // Otherwise, select the new item 
            else
                selectedIndex = newIndex;
        }
        else 
        {
            // Multiple selection is handled by the helper method below
            selectedIndices = calculateSelectedIndicesInterval(event.currentTarget as IVisualElement, event.shiftKey, event.ctrlKey); 
        }
        
        // Handle any drag gestures that may have been started
        var renderer:IItemRenderer = event.currentTarget as IItemRenderer;
        if (!renderer || !dragEnabled)
            return;
        
        mouseDownPoint = event.target.localToGlobal(new Point(event.localX, event.localY));
        
        // Find the index of the item we're down on, this is the drag focus item.
        // FIXME (egeorgie): When we start selecting/updating caret on mouse down, reuse the caret item,
        // instead of calculating the index.
        mouseDownIndex = dataGroup.getElementIndex(renderer);
        
        // Listen for MOUSE_MOVE on both the list and the sandboxRoot.
        // The user may have cliked on the item renderer close
        // to the edge of the list, and we still want to start a drag
        // operation if they move out of the list.
        systemManager.getSandboxRoot().addEventListener(MouseEvent.MOUSE_MOVE, mouseMoveHandler, false, 0, true);
        systemManager.getSandboxRoot().addEventListener(SandboxMouseEvent.MOUSE_UP_SOMEWHERE, mouseUpHandler, false, 0, true);
        systemManager.getSandboxRoot().addEventListener(MouseEvent.MOUSE_UP, mouseUpHandler, false, 0, true);
    }
    
    /**
     *  Handles <code>MouseEvent.MOUSE_MOVE</code> events from any mouse
     *  targets contained in the list including the renderers.  This method
     *  watches for a gesture that constitutes the beginning of a
     *  drag drop and send a <code>DragEvent.DRAG_START</code> event.
     *  It also checks to see if the mouse is over a non-target area of a
     *  renderer so that Flex can try to make it look like that renderer was 
     *  the target.
     *
     *  @param event The MouseEvent object.
     *  
     *  @langversion 3.0
     *  @playerversion Flash 9
     *  @playerversion AIR 1.1
     *  @productversion Flex 3
     */
    protected function mouseMoveHandler(event:MouseEvent):void
    {
        if (!mouseDownPoint || !dragEnabled)
            return;
        
        var pt:Point = new Point(event.localX, event.localY);
        pt = DisplayObject(event.target).localToGlobal(pt);
        
        const DRAG_THRESHOLD:int = 5;
        
        if (Math.abs(mouseDownPoint.x - pt.x) > DRAG_THRESHOLD ||
            Math.abs(mouseDownPoint.y - pt.y) > DRAG_THRESHOLD)
        {
            var dragEvent:DragEvent = new DragEvent(DragEvent.DRAG_START);
            dragEvent.dragInitiator = this;
            
            var localMouseDownPoint:Point = this.globalToLocal(mouseDownPoint);
            
            dragEvent.localX = localMouseDownPoint.x;
            dragEvent.localY = localMouseDownPoint.y;
            dragEvent.buttonDown = true;
            
            // We're starting a drag operation, remove the handlers
            // that are monitoring the mouse move, we don't need them anymore:
            dispatchEvent(dragEvent);

            // Finally, remove the mouse handlers
            removeMouseHandlersForDragStart();
        }
    }
    
    private function removeMouseHandlersForDragStart():void
    {
        mouseDownPoint = null;
        mouseDownIndex = -1;
        
        systemManager.getSandboxRoot().removeEventListener(MouseEvent.MOUSE_MOVE, mouseMoveHandler, true);
        systemManager.getSandboxRoot().removeEventListener(MouseEvent.MOUSE_UP, mouseUpHandler, true);
        systemManager.getSandboxRoot().removeEventListener(SandboxMouseEvent.MOUSE_UP_SOMEWHERE, mouseUpHandler, true);
    }
    
    /**
     *  Handles <code>MouseEvent.MOUSE_DOWN</code> events from any mouse
     *  targets contained in the list including the renderers. This method
     *  finds the renderer that was pressed and prepares to receive
     *  a <code>MouseEvent.MOUSE_UP</code> event.
     *
     *  @param event The MouseEvent object.
     *  
     *  @langversion 3.0
     *  @playerversion Flash 9
     *  @playerversion AIR 1.1
     *  @productversion Flex 3
     */
    protected function mouseUpHandler(event:Event):void
    {
        removeMouseHandlersForDragStart();
    }
    
    //--------------------------------------------------------------------------
    //
    //  Drop methods
    //
    //--------------------------------------------------------------------------
    
    private function calculateDropLocation(event:DragEvent):DropLocation
    {
        // Verify data format
        if (!enabled || !event.dragSource.hasFormat("orderedItems"))
            return null;
        
        // Calculate the drop location
        return layout.calculateDropLocation(event);
    }

    /**
     *  Handles <code>DragEvent.DRAG_ENTER</code> events.  This method
     *  determines if the DragSource object contains valid elements and uses
     *  the <code>DragManager.showDropFeedback()</code> method to set up the 
     *  UI feedback as well as the layout's <code>showDropIndicator()</code>
     *  method to display the drop indicator and initiate drag scrolling.
     *
     *  @param event The DragEvent object.
     * 
     *  @see spark.layouts.LayoutBase#showDropIndicator
     *  @see spark.layouts.LayoutBase#hideDropIndicator
     *  
     *  @langversion 3.0
     *  @playerversion Flash 10
     *  @playerversion AIR 1.5
     *  @productversion Flex 4
     */
    protected function dragEnterHandler(event:DragEvent):void
    {
        if (event.isDefaultPrevented())
            return;
        
        var dropLocation:DropLocation = calculateDropLocation(event); 
        if (dropLocation)
        {
            DragManager.acceptDragDrop(this);
            
            // Create the dropIndicator instance. The layout will take care of
            // parenting, sizing, positioning and validating the dropIndicator.
            if (dropIndicator)
                layout.dropIndicator = DisplayObject(createDynamicPartInstance("dropIndicator"));
            
            // Show drop indicator
            layout.showDropIndicator(dropLocation);
            
            // Show focus
            drawFocusAnyway = true;
            drawFocus(true);
            
            // Notify manager we can drop
            DragManager.showFeedback(event.ctrlKey ? DragManager.COPY : DragManager.MOVE);
        }
        else
        {
            DragManager.showFeedback(DragManager.NONE);
        }
    }
    
    /**
     *  Handles <code>DragEvent.DRAG_OVER</code> events. This method
     *  determines if the DragSource object contains valid elements and uses
     *  the <code>showDropFeedback()</code> method to set up the UI feedback 
     *  as well as the layout's <code>showDropIndicator()</code> method
     *  to display the drop indicator and initiate drag scrolling.
     *
     *  @param event The DragEvent object.
     *  
     *  @see spark.layouts.LayoutBase#showDropIndicator
     *  @see spark.layouts.LayoutBase#hideDropIndicator
     *  
     *  @langversion 3.0
     *  @playerversion Flash 10
     *  @playerversion AIR 1.5
     *  @productversion Flex 4
     */
    protected function dragOverHandler(event:DragEvent):void
    {
        if (event.isDefaultPrevented())
            return;
        
        var dropLocation:DropLocation = calculateDropLocation(event);
        if (dropLocation)
        {
            // Show drop indicator
            layout.showDropIndicator(dropLocation);

            // Show focus
            drawFocusAnyway = true;
            drawFocus(true);
            
            // Notify manager we can drop
            DragManager.showFeedback(event.ctrlKey ? DragManager.COPY : DragManager.MOVE);
        }
        else
        {
            // Hide if previously showing
            layout.hideDropIndicator();

            // Hide focus
            drawFocus(false);
            drawFocusAnyway = false;
            
            // Notify manager we can't drop
            DragManager.showFeedback(DragManager.NONE);
        }
    }
    
    /**
     *  Handles <code>DragEvent.DRAG_EXIT</code> events. This method hides
     *  the UI feedback by calling the <code>hideDropFeedback()</code> method
     *  and also hides the drop indicator by calling the layout's 
     *  <code>hideDropIndicator()</code> method.
     *
     *  @param event The DragEvent object.
     *  
     *  @see spark.layouts.LayoutBase#showDropIndicator
     *  @see spark.layouts.LayoutBase#hideDropIndicator
     *  
     *  @langversion 3.0
     *  @playerversion Flash 10
     *  @playerversion AIR 1.5
     *  @productversion Flex 4
     */
    protected function dragExitHandler(event:DragEvent):void
    {
        if (event.isDefaultPrevented())
            return;
        
        // Hide if previously showing
        layout.hideDropIndicator();
        
        // Hide focus
        drawFocus(false);
        drawFocusAnyway = false;
        
        // Destroy the dropIndicator instance
        layout.dropIndicator = null;
    }
    
    /**
     *  Handles <code>DragEvent.DRAG_DROP events</code>. This method  hides
     *  the drop feedback by calling the <code>hideDropFeedback()</code> method.
     *
     *  <p>If the action is a <code>COPY</code>, 
     *  then this method makes a deep copy of the object 
     *  by calling the <code>ObjectUtil.copy()</code> method, 
     *  and replaces the copy's <code>uid</code> property (if present) 
     *  with a new value by calling the <code>UIDUtil.createUID()</code> method.</p>
     * 
     *  @param event The DragEvent object.
     *
     *  @see mx.utils.ObjectUtil
     *  @see mx.utils.UIDUtil
     *  
     *  @langversion 3.0
     *  @playerversion Flash 10
     *  @playerversion AIR 1.5
     *  @productversion Flex 4
     */
    protected function dragDropHandler(event:DragEvent):void
    {
        if (event.isDefaultPrevented())
            return;
        
        // Hide the drop indicator
        layout.hideDropIndicator();
        
        // Hide focus
        drawFocus(false);
        drawFocusAnyway = false;
        
        // Get the dropLocation
        var dropLocation:DropLocation = calculateDropLocation(event);
        if (!dropLocation)
            return;
        
        // Find the dropIndex
        var dropIndex:int = dropLocation.dropIndex;
        
        // Make sure the manager has the appropriate action
        DragManager.showFeedback(event.ctrlKey ? DragManager.COPY : DragManager.MOVE);
        
        var dragSource:DragSource = event.dragSource;
        var items:Vector.<Object> = dragSource.dataForFormat("orderedItems") as Vector.<Object>;

        var caretIndex:int = -1;
        if (dragSource.hasFormat("orderedItemsCaretIndex"))
            caretIndex = event.dragSource.dataForFormat("orderedItemsCaretIndex") as int;
        
        // If we are reordering the list, remove the items now,
        // adjusting the dropIndex in the mean time.
        // If the items are drag moved to this list from a different list,
        // the drag initiator will remove the items when it receives the
        // DragEvent.DRAG_COMPLETE event.
        if (dragMoveEnabled &&
            event.action == DragManager.MOVE &&
            event.dragInitiator == this)
        {
            var indices:Vector.<int> = selectedIndices;
            indices.sort(compareValues);
            
            // Remove the previously selected items
            for (var i:int = indices.length - 1; i >= 0; i--)
            {
                if (indices[i] < dropIndex)
                    dropIndex--;
                dataProvider.removeItemAt(indices[i]);
            }
        }
        
        // Drop the items at the dropIndex
        var newSelection:Vector.<int> = new Vector.<int>();

        // Update the selection with the index of the caret item
        if (caretIndex != -1)
            newSelection.push(dropIndex + caretIndex);

        var copyItems:Boolean = (event.action == DragManager.COPY);
        for (i = 0; i < items.length; i++)
        {
            // Get the item, clone if needed
            var item:Object = items[i];
            if (copyItems)
                item = copyItemWithUID(item);

            // Copy the data
            dataProvider.addItemAt(items[i], dropIndex + i);

            // Update the selection
            if (i != caretIndex)
                newSelection.push(dropIndex + i);
        }

        // Set the selection
        selectedIndices = newSelection;

        // Scroll the caret index in view
        if (caretIndex != -1)
        {
            // Sometimes we may need to scroll several times as for virtual layouts
            // this is not guaranteed to bring in the element in view the first try
            // as some items in between may not be loaded yet and their size is only
            // estimated.
            var delta:Point;
            var loopCount:int = 0;
            while (loopCount++ < 10)
            {
                validateNow();
                delta = layout.getScrollPositionDeltaToElement(dropIndex + caretIndex);
                if (!delta || (delta.x == 0 && delta.y == 0))
                    break;
                layout.horizontalScrollPosition += delta.x;
                layout.verticalScrollPosition += delta.y;
            }
        }
    }

    /**
     *  Makes a deep copy of the object by calling the 
     *  <code>ObjectUtil.copy()</code> method, and replaces 
     *  the copy's <code>uid</code> property (if present) with a 
     *  new value by calling the <code>UIDUtil.createUID()</code> method.
     * 
     *  <p>This method is used for a drag and drop copy.</p>
     * 
     *  @param item The item to copy.
     *  
     *  @return The copy of the object.
     *
     *  @see mx.utils.ObjectUtil
     *  @see mx.utils.UIDUtil
     *  
     *  @langversion 3.0
     *  @playerversion Flash 10
     *  @playerversion AIR 1.5
     *  @productversion Flex 4
     */
    protected function copyItemWithUID(item:Object):Object
    {
        var copyObj:Object = ObjectUtil.copy(item);
        
        if (copyObj is IUID)
        {
            IUID(copyObj).uid = UIDUtil.createUID();
        }
        else if (copyObj is Object && "mx_internal_uid" in copyObj)
        {
            copyObj.mx_internal_uid = UIDUtil.createUID();
        }
        
        return copyObj;
    }

    //--------------------------------------------------------------------------
    //
    //  Event Handlers
    //
    //--------------------------------------------------------------------------
    
    /**
     *  @private
     *  Called when an item has been added to this component.
     */
    private function dataGroup_rendererAddHandler(event:RendererExistenceEvent):void
    {
        var index:int = event.index;
        var renderer:IVisualElement = event.renderer;
        
        if (!renderer)
            return;
        
        renderer.addEventListener(MouseEvent.MOUSE_DOWN, item_mouseDownHandler);
    }
    
    /**
     *  @private
     *  Called when an item has been removed from this component.
     */
    private function dataGroup_rendererRemoveHandler(event:RendererExistenceEvent):void
    {
        var index:int = event.index;
        var renderer:Object = event.renderer;
        
        if (!renderer)
            return;
        
        renderer.removeEventListener(MouseEvent.MOUSE_DOWN, item_mouseDownHandler);
    }
    
    /**
     *  A convenience method that handles scrolling a data item
     *  into view. 
     * 
     *  If the data item at the specified index is not completely 
     *  visible, the List will scroll until it is brought into 
     *  view. If the data item is already in view, no additional
     *  scrolling will occur. 
     * 
     *  @param index The index of the data item.
     *  
     *  @langversion 3.0
     *  @playerversion Flash 10
     *  @playerversion AIR 1.5
     *  @productversion Flex 4
     */
    public function ensureIndexIsVisible(index:int):void
    {
        if (!layout)
            return;

        var spDelta:Point = dataGroup.layout.getScrollPositionDeltaToElement(index);
         
        if (spDelta)
        {
            dataGroup.horizontalScrollPosition += spDelta.x;
            dataGroup.verticalScrollPosition += spDelta.y;
        }
    }
    
    /**
     *  Adjusts the selected indices to account for items being added to or 
     *  removed from this component. 
     *   
     *  @param index The new index.
     *   
     *  @param add <code>true</code> if an item was added to the component, 
     *  and <code>false</code> if an item was removed.
     *  
     *  @langversion 3.0
     *  @playerversion Flash 10
     *  @playerversion AIR 1.5
     *  @productversion Flex 4
     */
    override protected function adjustSelection(index:int, add:Boolean=false):void
    {
        var i:int; 
        var curr:Number; 
        var newInterval:Vector.<int> = new Vector.<int>(); 
        var e:IndexChangeEvent; 
        
        if (selectedIndex == NO_SELECTION || doingWholesaleChanges)
        {
            // The case where one item has been newly added and it needs to be 
            // selected and careted because requireSelection is true. 
            if (dataProvider && dataProvider.length == 1 && requireSelection)
            {
                newInterval.push(0);
                _selectedIndices = newInterval;   
                _selectedIndex = 0; 
                itemShowingCaret(0, true); 
                // If the selection properties have been adjusted to account for items that
                // have been added or removed, send out a "change" event and 
                // "caretChange" event so any bindings to them are updated correctly.
                e = new IndexChangeEvent(IndexChangeEvent.CHANGE);
                e.oldIndex = -1;
                e.newIndex = _selectedIndex;
                dispatchEvent(e);
                
                e = new IndexChangeEvent(IndexChangeEvent.CARET_CHANGE); 
                e.oldIndex = -1; 
                e.newIndex = _caretIndex;
                dispatchEvent(e); 
            }
            return; 
        }
        
        // Ensure multiple and single selection are in-sync before adjusting  
        // selection. Sometimes if selection has been changed before adding/removing
        // an item, we may not have handled selection via invalidation, so in those 
        // cases, force a call to commitSelection() to validate and commit the selection. 
        if ((!selectedIndices && selectedIndex > NO_SELECTION) ||
            (selectedIndex > NO_SELECTION && selectedIndices.indexOf(selectedIndex) == -1))
        {
            commitSelection(); 
        }
        
        // Handle the add or remove and adjust selection accordingly. 
        if (add)
        {
            for (i = 0; i < selectedIndices.length; i++)
            {
                curr = selectedIndices[i];
                 
                // Adding an item above one of the selected items,
                // bump the selected item up. 
                if (curr >= index)
                    newInterval.push(curr + 1); 
                else 
                    newInterval.push(curr); 
            }
        }
        else
        {
            // Quick check to see if we're removing the only selected item
            // in which case we need to honor requireSelection. 
            if (!isEmpty(selectedIndices) && selectedIndices.length == 1 
                && index == selectedIndex && requireSelection)
            {
                //Removing the last item 
                if (dataProvider.length == 0)
                {
                    newInterval = new Vector.<int>(); 
                }
                else if (index == 0)
                {
                    // We can't just set selectedIndex to 0 directly
                    // since the previous value was 0 and the new value is
                    // 0, so the setter will return early.
                    _proposedSelectedIndex = 0; 
                    invalidateProperties();
                    return;
                }    
                else
                {
                    newInterval.push(0);  
                }
            }
            else
            {    
                for (i = 0; i < selectedIndices.length; i++)
                {
                    curr = selectedIndices[i]; 
                    // Removing an item above one of the selected items,
                    // bump the selected item down. 
                    if (curr > index)
                        newInterval.push(curr - 1); 
                    else if (curr < index) 
                        newInterval.push(curr);
                }
            }
        }
        
        if (caretIndex == selectedIndex)
        {
            // caretIndex is not changing, so we just need to dispatch
            // an "caretChange" event to update any bindings and update the 
            // caretIndex backing variable. 
            var oldIndex:Number = caretIndex; 
            _caretIndex = getFirstItemValue(newInterval);
            e = new IndexChangeEvent(IndexChangeEvent.CARET_CHANGE); 
            e.oldIndex = oldIndex; 
            e.newIndex = caretIndex; 
            dispatchEvent(e); 
        }
        else 
        {
            // De-caret the previous caretIndex renderer and set the 
            // caretIndexAdjusted flag to true. This will mean in 
            // commitProperties, the caretIndex will be adjusted to 
            // match the selectedIndex; 
            
            // FIXME (dsubrama): We should revisit the synchronous nature of the 
            // de-careting/re-careting behavior.
            itemShowingCaret(caretIndex, false); 
            caretIndexAdjusted = true; 
            invalidateProperties(); 
        }
        
        var oldIndices:Vector.<int> = selectedIndices;  
        _selectedIndices = newInterval;
        _selectedIndex = getFirstItemValue(newInterval);
        // If the selection has actually changed, trigger a pass to 
        // commitProperties where a change event will be 
        // fired to update any bindings to selection properties. 
        if (_selectedIndices != oldIndices)
        {
            selectedIndexAdjusted = true; 
            invalidateProperties(); 
        }
    }
    
    /**
     *  Tries to find the next item in the data provider that
     *  starts with the character in the <code>eventCode</code> parameter.
     *  You can override this to do fancier typeahead lookups. The search
     *  starts at the <code>selectedIndex</code> location; if it reaches
     *  the end of the data provider it starts over from the beginning.
     *
     *  @param eventCode The key that was pressed on the keyboard.
     *  @return <code>true</code> if a match was found.
     *  
     *  @langversion 3.0
     *  @playerversion Flash 10
     *  @playerversion AIR 1.5
     *  @productversion Flex 4
     */
    protected function findKey(eventCode:int):Boolean
    {
        var tmpCode:int = eventCode;
        
        return tmpCode >= 33 &&
               tmpCode <= 126 &&
               findString(String.fromCharCode(tmpCode));
    }
    
    /**
     *  Finds an item in the list based on a String,
     *  and moves the selection to it. The search
     *  starts at the <code>selectedIndex</code> location; if it reaches
     *  the end of the data provider it starts over from the beginning.
     *
     *  @param str The String to match.
     * 
     *  @return <code>true</code> if a match is found.
     *  
     *  @langversion 3.0
     *  @playerversion Flash 10
     *  @playerversion AIR 1.5
     *  @productversion Flex 4
     */
    public function findString(str:String):Boolean
    {
        if (!dataProvider || dataProvider.length == 0)
            return false;

        var startIndex:int;
        var stopIndex:int;
        var retVal:Number;  

        if (selectedIndex == -1)
        {
            startIndex = 0;
            stopIndex = dataProvider.length; 
            retVal = findStringLoop(str, startIndex, stopIndex);
        }
        else
        {
            startIndex = selectedIndex + 1; 
            stopIndex = dataProvider.length; 
            retVal = findStringLoop(str, startIndex, stopIndex); 
            // We didn't find the item, loop back to the top 
            if (retVal == -1)
            {
                retVal = findStringLoop(str, 0, selectedIndex); 
            }
        }
        if (retVal != -1)
        {
            selectedIndex = retVal;
            ensureIndexIsVisible(retVal); 
            return true; 
        }
        else 
            return false; 
    }
    
    /**
     *  @private
     */
    private function findStringLoop(str:String, startIndex:int, stopIndex:int):Number
    {
        // Try to find the item based on the start and stop indices. 
        for (startIndex; startIndex != stopIndex; startIndex++)
        {
            var itmStr:String = itemToLabel(dataProvider.getItemAt(startIndex));

            itmStr = itmStr.substring(0, str.length);
            if (str == itmStr || str.toUpperCase() == itmStr.toUpperCase())
            {
               return startIndex;
            }
        }
        return -1;
    }
    
    /**
     *  @private
     *  Build in basic keyboard navigation support in List. 
     */
    override protected function keyDownHandler(event:KeyboardEvent):void
    {   
        super.keyDownHandler(event);

        if (!dataProvider || !layout || event.isDefaultPrevented())
            return;
        
        // 1. Was the space bar hit? 
        // Hitting the space bar means the current caret item, 
        // that is the item currently in focus, is being 
        // selected. 
        if (event.keyCode == Keyboard.SPACE)
        {
            selectedIndex = caretIndex; 
            event.preventDefault();
            return; 
        }

        // 2. Or was an alphanumeric key hit? 
        // Hitting an alphanumeric key causes List's
        // findKey method to run to find a matching 
        // item in the dataProvider whose first char 
        // matches the keystroke. 
        if (findKey(event.charCode))
        {
            event.preventDefault();
            return;
        }
            
        // 3. Was a navigation key hit (like an arrow key,
        // or Shift+arrow key)?  
        // Delegate to the layout to interpret the navigation
        // key and adjust the selection and caret item based
        // on the combination of keystrokes encountered.      
        adjustSelectionAndCaretUponNavigation(event); 
    }
    
    /**
     *  Adjusts the selection based on what keystroke or 
     *  keystroke combinations were encountered. The keystroke
     *  is sent down to the layout and its up to the layout's
     *  getNavigationDestinationIndex() method to determine 
     *  what the index to navigate to based on the item that 
     *  is currently in focus. Once the index is determined, 
     *  single selection, caret item and if necessary, multiple 
     *  selection are fixed up to reflect the newly selected
     *  item.  
     *
     *  @param event The Keyboard Event encountered
     * 
     *  
     *  @langversion 3.0
     *  @playerversion Flash 10
     *  @playerversion AIR 1.5
     *  @productversion Flex 4
     */
    protected function adjustSelectionAndCaretUponNavigation(event:KeyboardEvent):void
    {
        // Some unrecognized key stroke was entered, return. 
        var navigationUnit:uint = event.keyCode;    
        if (!NavigationUnit.isNavigationUnit(event.keyCode))
            return; 
            
        // Delegate to the layout to tell us what the next item is we should select or focus into.
        // FIXME (dsubrama): At some point we should refactor this so we don't depend on layout
        // for keyboard handling. If layout doesn't exist, then use some other keyboard handler
        var proposedNewIndex:int = layout.getNavigationDestinationIndex(caretIndex, navigationUnit, arrowKeysWrapFocus); 
        
        // FIXME (dsubrama): proposedNewIndex depends on CTRL key
        // move CTRL key logic into single selection
        // add SPACE logic - add to selection for multi-select or change selection for single-select

        // Note that the KeyboardEvent is canceled even if the current selected or in focus index
        // doesn't change because we don't want another component to start handling these
        // events when the index reaches a limit.
        if (proposedNewIndex == -1)
            return;
            
        event.preventDefault(); 
        
        // Contiguous multi-selection action. Create the new selection
        // interval.   
        if (allowMultipleSelection && event.shiftKey && selectedIndices)
        {
            var startIndex:Number = getLastSelectedIndex(); 
            var newInterval:Vector.<int> = new Vector.<int>();  
            var i:int; 
            if (startIndex <= proposedNewIndex)
            {
                for (i = startIndex; i <= proposedNewIndex; i++)
                {
                    newInterval.splice(0, 0, i); 
                }
            }
            else 
            {
                for (i = startIndex; i >= proposedNewIndex; i--)
                {
                    newInterval.splice(0, 0, i); 
                }
            }
            selectedIndices = newInterval;  
            ensureIndexIsVisible(proposedNewIndex); 
        }
        // Entering the caret state with the Ctrl key down 
        else if (event.ctrlKey)
        {
            var oldCaretIndex:Number = caretIndex; 
            setCurrentCaretIndex(proposedNewIndex);
            var e:IndexChangeEvent = new IndexChangeEvent(IndexChangeEvent.CARET_CHANGE); 
            e.oldIndex = oldCaretIndex; 
            e.newIndex = caretIndex; 
            dispatchEvent(e);    
            ensureIndexIsVisible(proposedNewIndex); 
        }
        // Its just a new selection action, select the new index.
        else
        {
            selectedIndex = proposedNewIndex;
            ensureIndexIsVisible(proposedNewIndex);
        }
    }
}

}