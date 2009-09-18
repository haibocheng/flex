////////////////////////////////////////////////////////////////////////////////
//
//  ADOBE SYSTEMS INCORPORATED
//  Copyright 2008 Adobe Systems Incorporated
//  All Rights Reserved.
//
//  NOTICE: Adobe permits you to use, modify, and distribute this file
//  in accordance with the terms of the license agreement accompanying it.
//
////////////////////////////////////////////////////////////////////////////////

package flash.tools.debugger.concrete;

/**
 * @author Mike Morearty
 */
public enum BinaryOp {
	// These correspond to the values in the player, in playerdebugger.h,
	// enum BinaryOp.  These values must be kept synchronized with those
	// ones.
	Is(0, "is"), //$NON-NLS-1$
	Instanceof(1, "instanceof"), //$NON-NLS-1$
	In(2, "in"), //$NON-NLS-1$
	As(3, "as"); //$NON-NLS-1$

	private int m_value;
	private String m_name;

	private BinaryOp(int value, String name) {
		m_value = value;
		m_name = name;
	}

	public int getValue() {
		return m_value;
	}

	public String getName() {
		return m_name;
	}
}
