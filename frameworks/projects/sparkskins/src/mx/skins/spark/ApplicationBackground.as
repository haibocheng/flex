////////////////////////////////////////////////////////////////////////////////
//
//  ADOBE SYSTEMS INCORPORATED
//  Copyright 2006 Adobe Systems Incorporated
//  All Rights Reserved.
//
//  NOTICE: Adobe permits you to use, modify, and distribute this file
//  in accordance with the terms of the license agreement accompanying it.
//
////////////////////////////////////////////////////////////////////////////////

package mx.skins.spark
{
    
    import flash.display.Graphics;
    
    import mx.core.FlexVersion;
    import mx.skins.ProgrammaticSkin;
    
    /**
     *  The skin for application background.
     *  
     *  @langversion 3.0
     *  @playerversion Flash 10
     *  @playerversion AIR 1.5
     *  @productversion Flex 4
     */
    public class ApplicationBackground extends ProgrammaticSkin
    {        
        //--------------------------------------------------------------------------
        //
        //  Constructor
        //
        //--------------------------------------------------------------------------
        
        /**
         *  Constructor	 
         *  
         *  @langversion 3.0
         *  @playerversion Flash 10
         *  @playerversion AIR 1.5
         *  @productversion Flex 4
         */
        public function ApplicationBackground()
        {
            super();             
        }
        
        //--------------------------------------------------------------------------
        //
        //  Overridden properties
        //
        //--------------------------------------------------------------------------
        
        //----------------------------------
        //  measuredWidth
        //----------------------------------
        
        /**
         *  @private
         */    
        override public function get measuredWidth():Number
        {
            return 8;
        }
        
        //----------------------------------
        //  measuredHeight
        //----------------------------------
        
        /**
         *  @private
         */        
        override public function get measuredHeight():Number
        {
            return 8;
        }
        
        //--------------------------------------------------------------------------
        //
        //  Overridden methods
        //
        //--------------------------------------------------------------------------
        
        /**
         *  @private
         */    
        override protected function updateDisplayList(w:Number, h:Number):void
        {
            super.updateDisplayList(w, h);
            
            var g:Graphics = graphics;
            var bgColor:uint = getStyle("backgroundColor");
                
            if (isNaN(bgColor))
                bgColor = 0xFFFFFF;

            g.clear();
            g.beginFill(bgColor);
            g.drawRect(0, 0, w, h);
            g.endFill();
        }
    }
    
}
