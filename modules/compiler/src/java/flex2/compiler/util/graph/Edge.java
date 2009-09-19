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

//TODO Try to remove this class and use ASC's equivalent

/**
 * @author Clement Wong
 */
public final class Edge<VertexWeight,EdgeWeight>
{
	public Edge(Vertex<VertexWeight,EdgeWeight> tail, Vertex<VertexWeight,EdgeWeight> head, EdgeWeight weight)
	{
		this.head = head;
		this.tail = tail;
		this.weight = weight;

		tail.addEmanatingEdge(this);
		tail.addSuccessor(head);
		head.addIncidentEdge(this);
		head.addPredecessor(tail);
	}

	private Vertex<VertexWeight,EdgeWeight> head, tail;
	private EdgeWeight weight;

	public Vertex<VertexWeight,EdgeWeight> getHead()
	{
		return head;
	}

	public Vertex<VertexWeight,EdgeWeight> getTail()
	{
		return tail;
	}

	public EdgeWeight getWeight()
	{
		return weight;
	}

	public boolean equals(Object object)
	{
		if (object instanceof Edge)
		{
			Edge<?,?> e = (Edge) object;
			return e.head == head && e.tail == tail && e.weight == weight;
		}
		else
		{
			return false;
		}
	}

	public int hashCode()
	{
		return (weight != null) ? weight.hashCode() : super.hashCode();
	}
}
