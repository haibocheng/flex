//  ADOBE SYSTEMS INCORPORATED
//  Copyright 2009 Adobe Systems Incorporated
//  All Rights Reserved.
//
//  NOTICE: Adobe permits you to use, modify, and distribute this file
//  in accordance with the terms of the license agreement accompanying it.
//
////////////////////////////////////////////////////////////////////////////////

package flex2.compiler.util;

import java.util.Set;

/**
 * Information about how an external script is resolved.
 * 
 * @author dloverin
 *
 */
public interface SwcExternalScriptInfo
{
    /**
     * 
     * @return The location of this SWC.
     */
    String getSwcLocation();
    
    /**
     * The set of dependency types found for this script.
     * 
     * @return Set of dependency types. One of 
     * <ul>
     * <li>"i" - inheritance</li>
     * <li>"n" - namespace</li>
     * <li>"s" - signature</li>
     * <li>"e" - expression</li>
     * </ul>
     */
    Set<String> getScriptDependencyTypes(String scriptName);
    
    /**
     * The set of SWCs an external script in this SWC was resolved in.
     * @return Set of Strings where each String is the location
     * of a SWC. 
     */
    Set<String> getSwcDependencies(String scriptName);
    
    /**
     * The set of external classes found in this SWC.
     * @return Set of Strings where each String is the name of an 
     * external class.
     */
    Set<String> getExternalScripts();
    
    /**
     * The set of external classes found in this SWC and resovled in 
     * resolvedSwcLocation.
     * @return Set of Strings where each String is the name of an 
     * external class.
     */
    Set<String> getExternalScripts(String resolvedSwcLocation);
}
