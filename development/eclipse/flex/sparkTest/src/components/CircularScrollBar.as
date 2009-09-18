package components {

import spark.components.supportClasses.ScrollBar;

public class CircularScrollBar extends ScrollBar
{
    public function CircularScrollBar()
    {
        super();
    }
    
    /**
     *  @private
     */
    override protected function pointToValue(x:Number, y:Number):Number
    {
        if (!track || !thumb)
            return 0;

        var cx:Number = x + (thumb.width / 2) - (track.width / 2);
        var cy:Number = y + (thumb.height / 2) - (track.height / 2);
        var angle:Number = Math.atan2(cy, cx);
        if (angle < 0) angle += 2 * Math.PI;
        return (maximum - minimum) * (angle / (2 * Math.PI));
    }
    
    /**
     *  @private
     */
    override protected function updateSkinDisplayList():void
    {
        var range:Number = maximum - minimum;
        if (!thumb || !track || (range <= 0))
            return;

        var radius:Number = width / 2;
        var angle:Number = ((value - minimum) / range) * 2 * Math.PI;
        var thumbX:Number = (width / 2)  + (radius * Math.cos(angle)) - (thumb.width / 2);
        var thumbY:Number = (height / 2) + (radius * Math.sin(angle)) - (thumb.height / 2);
        thumb.setLayoutBoundsPosition(thumbX, thumbY);

    }

}
}