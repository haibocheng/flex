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

package flex2.compiler.mxml.reflect;

/**
 * 
 */
public interface Assignable extends Stateful
{
    /**
     * @return The name of the assignable field.
     */
    String getName();

    /**
     * @return The declared type of the assignable field.
     */
    Type getType();

    /**
     * @return The expected type of the assignable field. While typically
     * the same as getType(), this may be customized with metadata.
     */
    Type getLValueType();

    /**
     * [ArrayElementType] or Vector data type.
     *
     * @return null if the assignable field is of type Array and the array
     * element type metadata is not available or if the array element type is
     * not specified.
     */
    Type getElementType();
}
