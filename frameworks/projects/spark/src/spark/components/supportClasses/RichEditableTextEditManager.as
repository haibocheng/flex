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
import flash.geom.Rectangle;

import flashx.textLayout.edit.EditManager;
import flashx.textLayout.tlf_internal;
import flashx.undo.IUndoManager;

import mx.core.mx_internal;

import spark.components.RichEditableText;
import flashx.textLayout.container.ContainerController;
import flashx.textLayout.compose.IFlowComposer;

use namespace mx_internal;
use namespace tlf_internal;

[ExcludeClass]

/**
 *  @private
 *  A subclass of EditManager that turns off screen updates when there is an
 *  edit.  This allows us to control screen update by calling the
 *  TextContainerManager's updateContainers() directly.
 *  
 *  @langversion 3.0
 *  @playerversion Flash 10
 *  @playerversion AIR 1.5
 *  @productversion Flex 4
 */
public class RichEditableTextEditManager extends EditManager
{
    /**
     *  Constructor. 
     *  
     *  @langversion 3.0
     *  @playerversion Flash 10
     *  @playerversion AIR 1.5
     *  @productversion Flex 4
     */
    public function RichEditableTextEditManager(
                                component:RichEditableText,
                                undoManager:IUndoManager = null)
    {
        super(undoManager);
        
        textDisplay = component;        
    }

    //--------------------------------------------------------------------------
    //
    //  Variables
    //
    //--------------------------------------------------------------------------

    /**
     *  @private
     */
    private var textDisplay:RichEditableText;
    
    //--------------------------------------------------------------------------
    //
    //  Overridden methods
    //
    //--------------------------------------------------------------------------
    
    /**
    *  @private
    * 
    *  Override so if we are auto-sizing it does not update the display after an 
    *  operation has modified the textFlow.  We must compose so that operations such
    *  as deletePreviousCharacter can be done before the next display update.
    * 
    *  For performance reasons, the call to this is delayed until 
    *  SelectionManager.enterFrameHandler().
    */ 
   
    override protected function updateAllControllers():void
    {  
        // If fixed dimensions, go ahead and do the update to minimize
        // the risk of problems from doing something different.
        if (textDisplay.isMeasureFixed())
        {
            super.updateAllControllers();
            return;
        }

        // Only compose - returns true if there were changes.
        var flowComposer:IFlowComposer = textFlow.flowComposer;
        if (flowComposer && flowComposer.composeToPosition())
        {        
            // If "auto-sizing" and contentsBounds bounds change we need
            // to tell layout manager that size has changed.   
            // Use isMeasureFixed() rather than autoSize because deleting 
            // text may allow a component that is no longer auto-sizing 
            // room to grow again.
            // 
            var controller:ContainerController = 
                    flowComposer.getControllerAt(0);
            var contentBounds:Rectangle = controller.getContentBounds();
        
            // Can't use >= because text could be being deleted and
            // dims have to shrink.
            // TODO:(cframpto) maybe can optimize this, for example, if toFit 
            // the toFit dim just needs to be <= the measured dim.
            if (Math.ceil(contentBounds.width) != textDisplay.measuredWidth || 
                Math.ceil(contentBounds.height) != textDisplay.measuredHeight)
            {
                textDisplay.invalidateSize();

                // Delay updating the display until updateDisplayList().
                textDisplay.invalidateDisplayList();                
                
                // Delay scroll until after the container is updated.
                textDisplay.scrollAfterUpdate = true;
                
                // Dispatch selection change event.
                selectionChanged(true, false);            
            }
            else
            {
                super.updateAllControllers();
            }
        }
    }
}

}
