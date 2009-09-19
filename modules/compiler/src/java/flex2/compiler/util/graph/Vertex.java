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

import java.util.*;

//TODO Try to remove this class and use ASC's equivalent

/**
 * @author Clement Wong
 */
public final class Vertex <VertexWeight,EdgeWeight>
{
	private static final int INITIAL_CAPACITY = 5;

	public Vertex(VertexWeight weight)
	{
		this.weight = weight;
	}

	private VertexWeight weight;

	int id;
	private Set<Edge<VertexWeight,EdgeWeight>> incidentEdges;     // pointing to this vertex
	private Set<Edge<VertexWeight,EdgeWeight>> emanatingEdges;    // pointing out of this vertex
	private Set<Vertex<VertexWeight,EdgeWeight>> predecessors; // tails of the incident edges
	private List<Vertex<VertexWeight,EdgeWeight>> successors;  // heads of the emanating edges

	public VertexWeight getWeight()
	{
		return weight;
	}

	public void addIncidentEdge(Edge<VertexWeight,EdgeWeight> e)
	{
		if (incidentEdges == null)
		{
			incidentEdges = new HashSet<Edge<VertexWeight,EdgeWeight>>(INITIAL_CAPACITY);
		}
		incidentEdges.add(e);
	}
	
	public void removeIncidentEdge(Edge<VertexWeight,EdgeWeight> e)
	{
		if (incidentEdges != null)
		{
			incidentEdges.remove(e);
		}
	}

	public Set<Edge<VertexWeight,EdgeWeight>> getIncidentEdges()
	{
		return incidentEdges;
	}

	public void addEmanatingEdge(Edge<VertexWeight,EdgeWeight> e)
	{
		if (emanatingEdges == null)
		{
			emanatingEdges = new HashSet<Edge<VertexWeight,EdgeWeight>>(INITIAL_CAPACITY);
		}
		emanatingEdges.add(e);
	}

	public void removeEmanatingEdge(Edge<VertexWeight,EdgeWeight> e)
	{
		if (emanatingEdges != null)
		{
			emanatingEdges.remove(e);
		}
	}

	public Set<Edge<VertexWeight,EdgeWeight>> getEmanatingEdges()
	{
		return emanatingEdges;
	}

	public void addPredecessor(Vertex<VertexWeight,EdgeWeight> v)
	{
		if (predecessors == null)
		{
			predecessors = new HashSet<Vertex<VertexWeight,EdgeWeight>>(INITIAL_CAPACITY);
		}
		predecessors.add(v);
	}
	
	public void removePredecessor(Vertex<VertexWeight,EdgeWeight> v)
	{
		if (predecessors != null)
		{
			predecessors.remove(v);
		}
	}

	public Set<Vertex<VertexWeight,EdgeWeight>> getPredecessors()
	{
		return predecessors;
	}

	public void addSuccessor(Vertex<VertexWeight,EdgeWeight> v)
	{
		if (successors == null)
		{
			successors = new ArrayList<Vertex<VertexWeight,EdgeWeight>>(INITIAL_CAPACITY);
		}
		successors.add(v);
	}

	public void removeSuccessor(Vertex<VertexWeight,EdgeWeight> v)
	{
		if (successors != null)
		{
			successors.remove(v);
		}
	}

	public List<Vertex<VertexWeight,EdgeWeight>> getSuccessors()
	{
		return successors;
	}

	public int inDegrees()
	{
		return incidentEdges == null ? 0 : incidentEdges.size();
	}

	public int outDegrees()
	{
		return emanatingEdges == null ? 0 : emanatingEdges.size();
	}

	public boolean equals(Object object)
	{
		if (object instanceof Vertex)
		{
			return (weight == null) ? super.equals(object) : weight.equals(((Vertex) object).weight);
		}
		return false;
	}

	public int hashCode()
	{
		return (weight != null) ? weight.hashCode() : super.hashCode();
	}
}
