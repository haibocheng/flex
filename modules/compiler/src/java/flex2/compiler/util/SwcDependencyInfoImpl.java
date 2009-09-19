//  ADOBE SYSTEMS INCORPORATED
//  Copyright 2009 Adobe Systems Incorporated
//  All Rights Reserved.
//
//  NOTICE: Adobe permits you to use, modify, and distribute this file
//  in accordance with the terms of the license agreement accompanying it.
//
////////////////////////////////////////////////////////////////////////////////

package flex2.compiler.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import flex2.compiler.util.graph.Algorithms;
import flex2.compiler.util.graph.DependencyGraph;
import flex2.compiler.util.graph.Vertex;
import flex2.compiler.util.graph.Visitor;

/**
 * Implementation to store the swc dependency graph, hiding the actual
 * implementation details. Also stores the externs of a SWC which
 * can be retrieved later via the api.
 * 
 * @author dloverin
 *
 */
class SwcDependencyInfoImpl implements SwcDependencyInfo
{
    private DependencyGraph<SwcExternalScriptInfo> dependencies;
    

    public SwcDependencyInfoImpl()
    {
        dependencies = new DependencyGraph<SwcExternalScriptInfo>();
    }
    
    public boolean dependencyExists(String swcLocation1, String swcLocation2)
    {
        return dependencies.dependencyExists(swcLocation1, swcLocation2);
    }

    public List<String> getSwcDependencyOrder()
    {
        final List<String> depOrder = new ArrayList<String>(dependencies.size());
        
        Algorithms.topologicalSort(dependencies, new Visitor<Vertex<String,SwcExternalScriptInfo>>()
        {   
            public void visit(Vertex<String,SwcExternalScriptInfo> v)
            {
                String name = v.getWeight();
                depOrder.add(name);
            }
        });
        
        return depOrder;
    }

    public Set<String> getDependencies(String swcLocation)
    {
        return dependencies.getDependencies(swcLocation);
    }

    public SwcExternalScriptInfo getSwcExternalScriptInfo(String swcLocation)
    {
        return dependencies.get(swcLocation);
    }

    /**
     * Add a map of a SWCs external symbols.
     * 
     * @param swcLocation A SWCs location in the file system.
     * @param externals A map of definitions that are not in a SWC. The definitions is the key
     * and the dependency type is the value.
     */
    public void addSwcExternals(String swcLocation, SwcExternalScriptInfo externals)
    {
        if (swcLocation == null || externals == null)
            throw new NullPointerException();

        String name = swcLocation;
        dependencies.put(name, externals);
        
        if (!dependencies.containsVertex(name))
        {
            dependencies.addVertex(new Vertex<String,SwcExternalScriptInfo>(name));
        }
    }
    
    /**
     * Add dependency that swc1 depends on swc2.
     * 
     * @param swc1
     * @param swc2
     */
    public void addDependency(String swcLocation1, String swcLocation2)
    {
        if (swcLocation1 == null || swcLocation2 == null)
            throw new NullPointerException();
        
        String head = swcLocation1;
        String tail = swcLocation2;
        if (!head.equals(tail) && dependencies.containsKey(head) && dependencies.containsKey(tail) &&
            !dependencies.dependencyExists(head, tail))
        {
            //System.out.println(swc1.getLocation() + " depends on " + swc2.getLocation());
            dependencies.addDependency(head, tail);
        }
    }

    public Set<Vertex<String, SwcExternalScriptInfo>> detectCycles()
    {
        return Algorithms.detectCycles(dependencies);        
    }

}
