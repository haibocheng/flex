////////////////////////////////////////////////////////////////////////////////
//
//  ADOBE SYSTEMS INCORPORATED
//  Copyright 2005 Adobe Systems Incorporated
//  All Rights Reserved.
//
//  NOTICE: Adobe permits you to use, modify, and distribute this file
//  in accordance with the terms of the license agreement accompanying it.
//
////////////////////////////////////////////////////////////////////////////////

package flex2.compiler.abc;

import flex2.compiler.util.QName;
import java.util.List;

/**
 * @author Clement Wong
 */
public interface Variable
{
    QName getQName();

    String getTypeName();

    String getElementTypeName();

    AbcClass getDeclaringClass();

    Attributes getAttributes();

    List<MetaData> getMetaData();

    List<MetaData> getMetaData(String name);
}

