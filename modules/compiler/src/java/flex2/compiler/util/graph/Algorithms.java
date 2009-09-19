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

import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Set;

//TODO Try to remove this class and use ASC's equivalent

/**
 * @author Clement Wong
 */
public final class Algorithms
{
	public static <VertexWeight,EdgeWeight> boolean isCyclic(Graph<VertexWeight,EdgeWeight> g)
	{
		ConnectednessCounter<VertexWeight,EdgeWeight> counter = new ConnectednessCounter<VertexWeight,EdgeWeight>();
		topologicalSort(g, counter);
		return counter.count != g.getVertices().size();
	}

	public static <VertexWeight,EdgeWeight> Set<Vertex<VertexWeight,EdgeWeight>> detectCycles(Graph<VertexWeight,EdgeWeight> g)
	{
		ConnectednessCounter<VertexWeight,EdgeWeight> counter = new ConnectednessCounter<VertexWeight,EdgeWeight>(g.getVertices());
		topologicalSort(g, counter);
		return counter.remained;
	}

    public static <VertexWeight,EdgeWeight> void topologicalSort(Graph<VertexWeight,EdgeWeight> g, Visitor<Vertex<VertexWeight,EdgeWeight>> visitor)
	{
		int[] inDegree = new int[g.getVertices().size()];
        
        // unchecked because you cannot create a generic array
		@SuppressWarnings("unchecked")
		Vertex<VertexWeight,EdgeWeight>[] vertices = new Vertex[inDegree.length];

		for (Iterator<Vertex<VertexWeight,EdgeWeight>> i = g.getVertices().iterator(); i.hasNext();)
		{
			Vertex<VertexWeight,EdgeWeight> v = i.next();
			vertices[v.id] = v;
			inDegree[v.id] = v.inDegrees();
		}

		LinkedList<Vertex<VertexWeight,EdgeWeight>> queue = new LinkedList<Vertex<VertexWeight,EdgeWeight>>();
		for (int i = 0, length = vertices.length; i < length; i++)
		{
			// in case of seeing multiple degree-zero candidates, we could
			// use the vertices different weights...
			if (inDegree[i] == 0)
			{
				queue.add(vertices[i]);
			}
		}

		while (!queue.isEmpty())
		{
			Vertex<VertexWeight,EdgeWeight> v = queue.removeFirst();
			if (visitor != null)
			{
				visitor.visit(v);
			}
			if (v.getSuccessors() != null)
			{
				for (Iterator<Vertex<VertexWeight,EdgeWeight>> i = v.getSuccessors().iterator(); i.hasNext();)
				{
					Vertex<VertexWeight,EdgeWeight> head = i.next();
					inDegree[head.id] -= 1;
					if (inDegree[head.id] == 0)
					{
						queue.add(head);
					}
				}
			}
		}
	}

	private static class ConnectednessCounter<VertexWeight,EdgeWeight> implements Visitor<Vertex<VertexWeight,EdgeWeight>>
	{
		private ConnectednessCounter()
		{
			count = 0;
		}

		private ConnectednessCounter(Set<Vertex<VertexWeight,EdgeWeight>> vertices)
		{
			this.remained = new HashSet<Vertex<VertexWeight,EdgeWeight>>(vertices);
		}

		private int count;
		private Set<Vertex<VertexWeight,EdgeWeight>> remained;

		public void visit(Vertex<VertexWeight,EdgeWeight> v)
		{
			count++;
			remained.remove(v);
		}
	}
}
