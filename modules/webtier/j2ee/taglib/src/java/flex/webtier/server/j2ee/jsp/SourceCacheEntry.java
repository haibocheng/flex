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
package flex.webtier.server.j2ee.jsp;

import java.io.Serializable;

/**
 * @author Edwin Smith
 */
/** objects associated with a piece of generated source code */
public class SourceCacheEntry implements Serializable
{
    public SourceCacheEntry(String sourceCode, String realPath, String sourceKey, String sourceHash)
    {
        this.sourceCode = sourceCode;
        this.realPath = realPath;
        this.sourceKey = sourceKey;
        this.sourceHash = sourceHash;
    }

    public final String realPath;
    public final String sourceCode;
    public final String sourceKey;
    public final String sourceHash;
}
