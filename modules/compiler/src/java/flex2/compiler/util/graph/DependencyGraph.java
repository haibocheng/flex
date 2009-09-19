////////////////////////////////////////////////////////////////////////////////
//
//  ADOBE SYSTEMS INCORPORATED
//  Copyright 2004-2006 Adobe Systems Incorporated
//  All Rights Reserved.
//
//  NOTICE: Adobe permits you to use, modify, and distribute this file
//  in accordance with the terms of the license agreement accompanying it.
//
////////////////////////////////////////////////////////////////////////////////

package flex2.compiler.util.graph;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

//TODO Try to remove this class and use ASC's equivalent

/**
 * @author Clement Wong
 */
public class DependencyGraph<EdgeWeight> extends Graph<String, EdgeWeight> 
{
	public DependencyGraph()
	{
		map = new HashMap<String, EdgeWeight>(300);
		vertices = new HashMap<String, Vertex<String,EdgeWeight>>(300);
	}

	private Map<String, EdgeWeight> map;
	private Map<String, Vertex<String,EdgeWeight>> vertices;

	// put(), get(), remove() are methods for 'map'

	public void put(String key, EdgeWeight value)
	{
		map.put(key, value);
	}

	public EdgeWeight get(String key)
	{
		return map.get(key);
	}

	public void remove(String key)
	{
		map.remove(key);
	}

	public Set<String> keySet()
	{
		return map.keySet();
	}

	public int size()
	{
		return map.size();
	}

	public boolean containsKey(String key)
	{
		return map.containsKey(key);
	}

	public boolean containsVertex(String key)
	{
		return vertices.containsKey(key);
	}

	public void clear()
	{
		super.clear();
		map.clear();
		vertices.clear();
	}

	// methods for graph manipulations

	public void addVertex(Vertex<String,EdgeWeight> v)
	{
		super.addVertex(v);
		vertices.put(v.getWeight(), v);
	}

	public Vertex<String,EdgeWeight> getVertex(String weight)
	{
		return vertices.get(weight);
	}
	
	public void removeVertex(String weight)
	{
		Vertex<String,EdgeWeight> v = vertices.remove(weight);
		if (v != null)
		{
			super.removeVertex(v);
		}
	}

	public void addDependency(String name, String dep)
	{
		Vertex<String,EdgeWeight> tail = null, head = null;

		if ((head = vertices.get(name)) == null)
		{
			head = new Vertex<String,EdgeWeight>(name);
			addVertex(head);
		}

		if ((tail = vertices.get(dep)) == null)
		{
			tail = new Vertex<String,EdgeWeight>(dep);
			addVertex(tail);
		}

		addEdge(new Edge<String,EdgeWeight>(tail, head, null));
	}

	public boolean dependencyExists(String name, String dep)
	{
		Vertex<String,EdgeWeight> tail = null, head = null;

		if ((head = vertices.get(name)) == null)
		{
			return false;
		}

		if ((tail = vertices.get(dep)) == null)
		{
			return false;
		}

		Set<Vertex<String,EdgeWeight>> predecessors = head.getPredecessors();

		if (predecessors != null)
		{
			return predecessors.contains(tail);
		}
		else
		{
			return false;
		}
	}
	
	/**
	 * Get the dependencies of a given node.
	 * 
	 * @param name The node to get the dependencies for.
	 * @return The set of dependencies.
	 */
	public Set<String> getDependencies(String name)
	{
        Vertex<String,EdgeWeight> head = null;

        if ((head = vertices.get(name)) == null)
        {
            return Collections.emptySet();
        }
	    
        Set<String> dependencies = new LinkedHashSet<String>();
        Set<Vertex<String,EdgeWeight>> predecessors = head.getPredecessors();

        if (predecessors != null)
        {
            for (Vertex<String,EdgeWeight> pred : predecessors)
            {
                dependencies.add(pred.getWeight());
            }
        }
	    
	    return dependencies;
	}
}

