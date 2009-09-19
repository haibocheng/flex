////////////////////////////////////////////////////////////////////////////////
//
//  ADOBE SYSTEMS INCORPORATED
//  Copyright 2005-2006 Adobe Systems Incorporated
//  All Rights Reserved.
//
//  NOTICE: Adobe permits you to use, modify, and distribute this file
//  in accordance with the terms of the license agreement accompanying it.
//
////////////////////////////////////////////////////////////////////////////////

package flex2.compiler.as3;

import flex2.compiler.CompilationUnit;
import flex2.compiler.as3.reflect.TypeTable;

/**
 * SubCompiler extension interface.
 *
 * @author Clement Wong
 */
public interface Extension
{
	/**
	 * Called at the end of SubCompiler.parse()
	 */
	void parse1(CompilationUnit unit, TypeTable typeTable);
	void parse2(CompilationUnit unit, TypeTable typeTable);

	/**
	 * Called at the end of SubCompiler.analyze1()
	 */
	void analyze1(CompilationUnit unit, TypeTable typeTable);

	/**
	 * Called at the end of SubCompiler.analyze2()
	 */
	void analyze2(CompilationUnit unit, TypeTable typeTable);

	/**
	 * Called at the end of SubCompiler.analyze3()
	 */
	void analyze3(CompilationUnit unit, TypeTable typeTable);

	/**
	 * Called at the end of SubCompiler.analyze4()
	 */
	void analyze4(CompilationUnit unit, TypeTable typeTable);

	/**
	 * Called at the end of SubCompiler.generate()
	 */
	void generate(CompilationUnit unit, TypeTable typeTable);
}
