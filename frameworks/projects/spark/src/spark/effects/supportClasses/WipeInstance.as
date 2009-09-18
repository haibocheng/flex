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
package spark.effects.supportClasses
{
import spark.effects.Wipe;
import spark.effects.WipeDirection;

/**
 *  The WipeInstance class implements the instance class
 *  for the Wipe effect.
 *  Flex creates an instance of this class when it plays a Wipe
 *  effect; you do not create one yourself.
 *
 *  @see spark.effects.Wipe
 *  
 *  @langversion 3.0
 *  @playerversion Flash 10
 *  @playerversion AIR 1.5
 *  @productversion Flex 4
 */  
public class WipeInstance extends AnimateTransitionShaderInstance
{
    /**
     *  @copy spark.effects.Wipe#direction
     *  
     *  @langversion 3.0
     *  @playerversion Flash 10
     *  @playerversion AIR 1.5
     *  @productversion Flex 4
     */
    public var direction:String;

    /**
     *  Constructor.
     *
     *  @param target The Object to animate with this effect.
     *  
     *  @langversion 3.0
     *  @playerversion Flash 10
     *  @playerversion AIR 1.5
     *  @productversion Flex 4
     */
    public function WipeInstance(target:Object)
    {
        super(target);
    }
    
    /**
     *  @private
     */
    override public function play():void
    {
        var shaderDir:int;
        switch (direction) {
            case WipeDirection.RIGHT:
                shaderDir = 0;
                break;
            case WipeDirection.UP:
                shaderDir = 1;
                break;
            case WipeDirection.LEFT:
                shaderDir = 2;
                break;
            case WipeDirection.DOWN:
                shaderDir = 3;
                break;            
        }

        shaderProperties = new Object();
        shaderProperties["direction"] = shaderDir;
        super.play();
    }
    
}
}