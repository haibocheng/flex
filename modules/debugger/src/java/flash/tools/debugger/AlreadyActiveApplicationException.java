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

package flash.tools.debugger;
import java.io.IOException;

/**
 * AlreadyActiveApplicationException is thrown when run/debug the application while there is 
 * an already running application. 
 * Exception is detected with ADL exit code 1 (Successful invocation of an already running AIR application. ADL exits immediately.)
 * 
 * @author sakkus
 */
public class AlreadyActiveApplicationException extends IOException {
	private static final long serialVersionUID = 0L;
	
	private boolean m_isDebugging;
	
	public AlreadyActiveApplicationException (String detailedMessage,boolean isDebugging)
	{
		super(detailedMessage);
		m_isDebugging=isDebugging;
	}
	
    public String getMessage()
	{
    	if(m_isDebugging) 	//DEBUGGING
    		return Bootstrap.getLocalizationManager().getLocalizedTextString("maybeAlreadyRunningForDebug"); //$NON-NLS-1$
    	else    			//RUNNING
    		return Bootstrap.getLocalizationManager().getLocalizedTextString("maybeAlreadyRunningForRun"); //$NON-NLS-1$
	}
}
