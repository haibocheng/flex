package flashx.textLayout.compose
{
	/**
	 *  The FlowDamageType class is an enumeration class that defines types of damage for damage methods and events.
	 *
	 * @playerversion Flash 10
	 * @playerversion AIR 1.5
	 * @langversion 3.0
	 */
	public class FlowDamageType
	{
		/** 
		 * Specifies the invalid damage type used by IFlowComposer when handling damage.
		 *
		 * @playerversion Flash 10
	 	 * @playerversion AIR 1.5
	 	 * @langversion 3.0
	 	 *
	 	 * @see flashx.textLayout.compose.TextFlowLine#validity	 	 */
		static public const INVALID:String = "invalid";
		
		/**
		 * Value is used to set the <code>validity</code> property if the line has been invalidated by other lines 
		 * moving around. The text line might or might not need recreating at the next compose operation. 
		 *
		 * @playerversion Flash 10
		 * @playerversion AIR 1.5
	 	 * @langversion 3.0
	 	 *
	 	 * @see flashx.textLayout.compose.TextFlowLine#validity
	 	 */
		static public const GEOMETRY:String = "geometry";
	}
}