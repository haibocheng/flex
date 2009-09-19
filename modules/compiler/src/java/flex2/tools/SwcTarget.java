////////////////////////////////////////////////////////////////////////////////
//
//  ADOBE SYSTEMS INCORPORATED
//  Copyright 2006 Adobe Systems Incorporated
//  All Rights Reserved.
//
//  NOTICE: Adobe permits you to use, modify, and distribute this file
//  in accordance with the terms of the license agreement accompanying it.
//
////////////////////////////////////////////////////////////////////////////////

package flex2.tools;

import flex2.compiler.Source;
import flex2.compiler.swc.SwcComponent;
import java.util.List;
import java.util.Map;

/**
 * Fcsh helper class.
 *
 * @author Clement Wong
 */
public class SwcTarget extends Target
{
	public List<SwcComponent> nsComponents;
    //public List<> fileList;
	public Map<String, Source> classes;
}
