//  ADOBE SYSTEMS INCORPORATED
//  Copyright 2009 Adobe Systems Incorporated
//  All Rights Reserved.
//
//  NOTICE: Adobe permits you to use, modify, and distribute this file
//  in accordance with the terms of the license agreement accompanying it.
//
////////////////////////////////////////////////////////////////////////////////

package flex2.compiler.util;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

/**
 * Package private class to implement the booking of SwcScript definitions in 
 * a SWC that are resolved in other SWCs. For each script, the dependency types
 * are kept as are the SWCs that resolve the dependency.
 *  
 * @author dloverin
 *
 */
class SwcExternalScriptInfoImpl implements SwcExternalScriptInfo
{
    
    private String swcLocation;
    private Map<String, Set<String>> scriptToTypes = new TreeMap<String, Set<String>>(); // map a script name to a list of type dependencies
    private Map<String, Set<String>> scriptToSwcs  = new HashMap<String, Set<String>>(); // map a script name to a list of swcs that contain the script
    
    
    public SwcExternalScriptInfoImpl()
    {
    }
    
    public SwcExternalScriptInfoImpl(String swcLocation)
    {
        assert swcLocation != null;
        this.swcLocation = swcLocation;
    }
    
    public String getSwcLocation()
    {
        return swcLocation;
    }

    public Set<String> getScriptDependencyTypes(String scriptName)
    {
        Set<String>dependencySet = scriptToTypes.get(scriptName);
        return dependencySet != null ? dependencySet : Collections.<String>emptySet();
    }

    public Set<String> getSwcDependencies(String scriptName)
    {
        Set<String>swcSet = scriptToSwcs.get(scriptName);
        return swcSet != null ? swcSet : Collections.<String>emptySet();
    }

    public Set<String> getExternalScripts()
    {
        return scriptToTypes != null ? scriptToTypes.keySet() : 
                                       Collections.<String>emptySet();
    }
    
    // Additional methods beyond the interface
    
    /**
     * Add the dependency type information about a script.
     * 
     * @param scriptName Name of the script class.
     * @param dependencyType - "n", "s", "e", or "i".
     */
    public void addScriptDependencyType(String scriptName, String dependencyType)
    {
        if (scriptName == null || dependencyType == null)
            throw new NullPointerException();

        Set<String>dependencySet = scriptToTypes.get(scriptName);
        if (dependencySet == null)
        {
            dependencySet = new HashSet<String>();
            scriptToTypes.put(scriptName, dependencySet);
        }
        
        dependencySet.add(dependencyType);
    }

    /**
     * Add a SWC where an external script is resolved.
     * 
     * @param scriptName Name of the script class.
     * @param swcLocation Path with the SWC is location in the file system.
     */
    public void addResolvingSwc(String scriptName, String swcLocation)
    {
        if (scriptName == null || swcLocation == null)
            throw new NullPointerException();
        
        Set<String>swcSet = scriptToSwcs.get(scriptName);
        if (swcSet == null)
        {
            swcSet = new HashSet<String>();
            scriptToSwcs.put(scriptName, swcSet);
        }
        
        swcSet.add(swcLocation);
    }

    public Set<String> getExternalScripts(String resolvedSwcLocation)
    {
        Set<String>externalScripts = new HashSet<String>();
        
        for (Map.Entry<String, Set<String>> scriptMap : scriptToSwcs.entrySet())
        {
            Set<String> resolvingSwcs = scriptMap.getValue();
            
            if (resolvingSwcs != null && resolvingSwcs.contains(resolvedSwcLocation))
            {
                externalScripts.add(scriptMap.getKey());
            }
        }
        
        return externalScripts;
    }

}
