package flashx.textLayout.formats
{
	/**
	 *  Used internally for specifying the status of clauses in IME text during an IME text entry session.
	 *
	 * @playerversion Flash 10
	 * @playerversion AIR 1.5
	 * @langversion 3.0 
	 */
	public final class IMEStatus
	{
		public function IMEStatus()
		{
		}
		
		/** The name of the IMEClause property. Value is an integer.
		 *
		 * @playerversion Flash 10
	 	 * @playerversion AIR 1.5
	 	 * @langversion 3.0
	 	 */
	 	 
		public static const IME_CLAUSE:String = "imeClause";
		
		/** The name of the IMEStatus property
		 *
		 * @playerversion Flash 10
	 	 * @playerversion AIR 1.5
	 	 * @langversion 3.0
	 	 */
	 	 
		public static const IME_STATUS:String = "imeStatus";
		
		// Following are all the possible values of imeStatus property:
		
		/** Raw - text has not yet been converted.
		 *
		 * @playerversion Flash 10
	 	 * @playerversion AIR 1.5
	 	 * @langversion 3.0
	 	 */
	 	 
		public static const RAW:String = "raw";

		/** Selected - text has been converted and is the current clause in the IME session
		 *
		 * @playerversion Flash 10
	 	 * @playerversion AIR 1.5
	 	 * @langversion 3.0
	 	 */
	 	 
		public static const SELECTED:String = "selected";

		/** Not selected - text has been converted and is not part of the current clause in the IME session
		 *
		 * @playerversion Flash 10
	 	 * @playerversion AIR 1.5
	 	 * @langversion 3.0
	 	 */
	 	 
		public static const NOT_SELECTED:String = "notSelected";

		/** Dead key input state - in the process of entering a multi-key character, such as an accented char
		 *
		 * @playerversion Flash 10
	 	 * @playerversion AIR 1.5
	 	 * @langversion 3.0
	 	 */
	 	 
		public static const DEAD_KEY_INPUT_STATE:String = "deadKeyInputState";

	}
}