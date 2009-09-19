////////////////////////////////////////////////////////////////////////////////
//
//  ADOBE SYSTEMS INCORPORATED
//  Copyright 2005-2007 Adobe Systems Incorporated
//  All Rights Reserved.
//
//  NOTICE: Adobe permits you to use, modify, and distribute this file
//  in accordance with the terms of the license agreement accompanying it.
//
////////////////////////////////////////////////////////////////////////////////

package flex2.compiler.as3.genext;

import flex2.compiler.as3.reflect.TypeTable;
import flex2.compiler.mxml.lang.StandardDefs;
import flex2.compiler.util.ThreadLocalToolkit;
import flash.swf.tools.as3.EvaluatorAdapter;

import java.util.Map;

/**
 * common superclass for Bindable and Managed first pass evaluators.
 */
public abstract class GenerativeFirstPassEvaluator extends EvaluatorAdapter
{
	protected final TypeTable typeTable;
    protected final StandardDefs standardDefs;

	public GenerativeFirstPassEvaluator(TypeTable typeTable, StandardDefs defs)
	{
		this.typeTable = typeTable;
		this.standardDefs = defs;
		setLocalizationManager(ThreadLocalToolkit.getLocalizationManager());
	}

	public abstract boolean makeSecondPass();

	public abstract Map<String, ? extends GenerativeClassInfo> getClassMap();

}
