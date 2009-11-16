////////////////////////////////////////////////////////////////////////////////
//
//  ADOBE SYSTEMS INCORPORATED
//  Copyright 2009 Adobe Systems Incorporated
//  All Rights Reserved.
//
//  NOTICE: Adobe permits you to use, modify, and distribute this file
//  in accordance with the terms of the license agreement accompanying it.
//
////////////////////////////////////////////////////////////////////////////////

package spark.accessibility
{

import flash.accessibility.Accessibility;
import flash.events.Event;

import mx.core.UIComponent;
import mx.core.mx_internal;

import spark.components.ButtonBar;
import spark.components.supportClasses.ListBase;

use namespace mx_internal;

/**
 *  ButtonBarAccImpl is a subclass of AccessibilityImplementation
 *  which implements accessibility for the List class.
 *
 *  @langversion 3.0
 *  @playerversion Flash 10
 *  @playerversion AIR 1.5
 *  @productversion Flex 4
 */
public class ButtonBarAccImpl extends ListBaseAccImpl
{
    include "../core/Version.as";
    //--------------------------------------------------------------------------
    //
    //  Class constants
    //
    //--------------------------------------------------------------------------

    /**
     *  @private
     */
    private static const EVENT_OBJECT_STATECHANGE:uint = 0x800a;

    /**
     *  @private
     */
    private static const STATE_SYSTEM_PRESSED:uint = 0x00000008;

    /**
     *  @private
     */
    private static const ROLE_SYSTEM_PUSHBUTTON:uint = 0x2B;

    /**
     *  @private
     */
    private static const STATE_SYSTEM_FOCUSED:uint = 0x00000004;


    //--------------------------------------------------------------------------
    //
    //  Class methods
    //
    //--------------------------------------------------------------------------

    /**
     *  Enables accessibility in the ButtonBar class.
     *
     *  <p>This method is called by application startup code
     *  that is autogenerated by the MXML compiler.
     *  Afterwards, when instances of ButtonBar are initialized,
     *  their <code>accessibilityImplementation</code> property
     *  will be set to an instance of this class.</p>
     *
     *  @langversion 3.0
     *  @playerversion Flash 10
     *  @playerversion AIR 1.5
     *  @productversion Flex 4
     */
    public static function enableAccessibility():void
    {
        ButtonBar.createAccessibilityImplementation =
            createAccessibilityImplementation;
    }

    /**
     *  @private
     *  Creates a ButtonBar's AccessibilityImplementation object.
     *  This method is called from UIComponent's
     *  initializeAccessibility() method.
     */
    mx_internal static function createAccessibilityImplementation(
                                component:UIComponent):void
    {
        component.accessibilityImplementation =
            new ButtonBarAccImpl(component);
    }

    //--------------------------------------------------------------------------
    //
    //  Constructor
    //
    //--------------------------------------------------------------------------

    /**
     *  Constructor.
     *
     *  @param master The UIComponent instance that this AccImpl instance
     *  is making accessible.
     *
     *  @langversion 3.0
     *  @playerversion Flash 10
     *  @playerversion AIR 1.5
     *  @productversion Flex 4
     */
    public function ButtonBarAccImpl(master:UIComponent)
    {
        super(master);
        role = 0x16; // ROLE_SYSTEM_TOOLBAR
    }

    //--------------------------------------------------------------------------
    //
    //  Overridden methods: AccessibilityImplementation
    //
    //--------------------------------------------------------------------------

    /**
     *  @private
     *  Gets the role for the component.
     *
     *  @param childID children of the component
     */
    override public function get_accRole(childID:uint):uint
    {
        return childID == 0 ? role : ROLE_SYSTEM_PUSHBUTTON;
    }

    /**
     *  @private
     *  IAccessible method for returning the state of the ButtonBarButton.
     *  States are predefined for all the components in MSAA.
     *  Values are assigned to each state.
     *  Depending upon the ButtonBarButton being Selected, Selectable, pressed
     *  a value is returned.
     *
     *  @param childID uint
     *
     *  @return State uint
     */
    override public function get_accState(childID:uint):uint
    {
        var accState:uint = getState(childID);

        if (childID > 0)
        {
            var index:int = childID - 1;
            if (ListBase(master).isItemIndexSelected(index))
                accState |= STATE_SYSTEM_PRESSED;
            if (index == ListBase(master).caretIndex)
                accState |= STATE_SYSTEM_FOCUSED;
        }
        return accState;
    }

    /**
     *  @private
     *  IAccessible method for returning the Default Action.
     *
     *  @param childID uint
     *
     *  @return DefaultAction String
     */
    override public function get_accDefaultAction(childID:uint):String
    {
        if (childID == 0)
            return null;

        return "Press";
    }
    
    //--------------------------------------------------------------------------
    //
    //  Event handlers
    //
    //--------------------------------------------------------------------------
    
    override protected function eventHandler(event:Event):void
    {
        // Let AccImpl class handle the events
        // that all accessible UIComponents understand.
        $eventHandler(event);
        
        switch (event.type)
        {
            case "change":
            {
                var pressed:int = ButtonBar(master).selectedIndex;
                
                Accessibility.sendEvent(master, pressed + 1,
                    EVENT_OBJECT_STATECHANGE, true);
                break;
            }
            default:
            {
                super.eventHandler(event);
                break;
            }
        }
    }

}

}