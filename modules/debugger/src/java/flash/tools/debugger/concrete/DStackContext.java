////////////////////////////////////////////////////////////////////////////////
//
//  ADOBE SYSTEMS INCORPORATED
//  Copyright 2003-2007 Adobe Systems Incorporated
//  All Rights Reserved.
//
//  NOTICE: Adobe permits you to use, modify, and distribute this file
//  in accordance with the terms of the license agreement accompanying it.
//
////////////////////////////////////////////////////////////////////////////////

package flash.tools.debugger.concrete;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import flash.tools.debugger.Frame;
import flash.tools.debugger.Location;
import flash.tools.debugger.NoResponseException;
import flash.tools.debugger.NotConnectedException;
import flash.tools.debugger.NotSuspendedException;
import flash.tools.debugger.Session;
import flash.tools.debugger.Variable;

public class DStackContext implements Frame
{
	private DModule		m_source;
	private String		m_functionSignature;
	private int			m_depth;
	private int			m_module;
	private int			m_line;
	private DVariable	m_this;
	private Map<String, DVariable> m_args;
	private Map<String, DVariable> m_locals;
	private List<DVariable>        m_scopeChain;
	private DLocation	m_location;
	private int			m_swfIndex; /* index of swf that we halted within (really part of location) */
	private int			m_offset;   /* offset within swf where we halted. (really part of location) */
	private boolean		m_populated;
	private DVariable	m_activationObject;

	public DStackContext(int module, int line, DModule f, long thisId /* bogus */,
						 String functionSignature, int depth)
	{
		m_source = f;
		m_module = module;
		m_line = line;
		// the passed-in 'thisId' seems to always be equal to one, which does more harm than good
		m_this = null;
		m_functionSignature = functionSignature;
		m_depth = depth;
		m_args = new LinkedHashMap<String, DVariable>(); // preserves order
		m_locals = new LinkedHashMap<String, DVariable>(); // preserves order
		m_scopeChain = new ArrayList<DVariable>();
		m_populated = false;
		m_location = new DLocation(m_source, line);
	}

	/*
	 * @see flash.tools.debugger.Frame#getLocation()
	 */
	public Location   getLocation()
	{
		return m_location;
	}

    /*
     * @see flash.tools.debugger.Frame#getArguments(flash.tools.debugger.Session)
     */
    public Variable[] getArguments(Session s) throws NoResponseException, NotSuspendedException, NotConnectedException
    {
    	populate(s);
    	return m_args.values().toArray( new Variable[m_args.size()] );
    }

	/*
	 * @see flash.tools.debugger.Frame#getLocals(flash.tools.debugger.Session)
	 */
	public Variable[] getLocals(Session s) throws NoResponseException, NotSuspendedException, NotConnectedException
	{
		populate(s);
		return m_locals.values().toArray( new Variable[m_locals.size()] );
	}

    /*
     * @see flash.tools.debugger.Frame#getThis(flash.tools.debugger.Session)
     */
    public Variable getThis(Session s) throws NoResponseException, NotSuspendedException, NotConnectedException
	{
		populate(s);
		return getThis();
	}

	/*
	 * @see flash.tools.debugger.Frame#getScopeChain()
	 */
	public Variable[] getScopeChain(Session s) throws NoResponseException, NotSuspendedException, NotConnectedException
	{
		populate(s);
		return m_scopeChain.toArray(new Variable[m_scopeChain.size()]);
	}

	/* getters */
	public String		getCallSignature()		{ return m_functionSignature; }
	public int			getModule()				{ return m_module; }
	public int			getLine()				{ return m_line; }
	public DVariable	getThis()				{ return m_this; }

	/* setters */
	void addArgument(DVariable v)				{ m_args.put(v.getName(), v); }
	void addLocal(DVariable v)					{ m_locals.put(v.getName(), v); }
	void addScopeChainEntry(DVariable v)		{ m_scopeChain.add(v); }
	void removeAllArguments()					{ m_args.clear(); }
	void removeAllLocals()						{ m_locals.clear(); m_activationObject = null; }
	void removeAllScopeChainEntries()			{ m_scopeChain.clear(); }
	void removeAllVariables()					{ removeAllLocals(); removeAllArguments(); removeAllScopeChainEntries(); }
	void setDepth(int depth)					{ m_depth = depth; }
	void setThis(DVariable v)					{ m_this = v; }
	void setSwfIndex(int index)					{ m_swfIndex = index; }
	void setOffset(int offset)					{ m_offset = offset; }
	void markStale()							{ m_populated = false; } // triggers a reload of variables.

	/**
	 * Removes the specified variable from the list of locals, and
	 * remembers that the specified variable is the "activation object"
	 * for this frame.  See bug 155031.
	 */
	void convertLocalToActivationObject(DVariable v)
	{
		m_activationObject = v;
		m_locals.remove(v.getName());
	}

	/**
	 * Gets the activation object for this frame, or <code>null</code>
	 * if none.  See bug FB-2674.
	 */
	DVariable getActivationObject()
	{
		return m_activationObject;
	}

	/**
	 * Populate ensures that we have some locals and args. That is
	 * that we have triggered a InFrame call to the player
	 * @throws NoResponseException
	 * @throws NotSuspendedException
	 * @throws NotConnectedException
	 */
	void populate(Session s) throws NoResponseException, NotSuspendedException, NotConnectedException
	{
		if (!m_populated)
		{
			((PlayerSession)s).requestFrame(m_depth);
			m_populated = true;
		}
	}
}
