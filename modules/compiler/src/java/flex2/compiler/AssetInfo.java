////////////////////////////////////////////////////////////////////////////////
//
//  ADOBE SYSTEMS INCORPORATED
//  Copyright 2007 Adobe Systems Incorporated
//  All Rights Reserved.
//
//  NOTICE: Adobe permits you to use, modify, and distribute this file
//  in accordance with the terms of the license agreement accompanying it.
//
////////////////////////////////////////////////////////////////////////////////

package flex2.compiler;

import flash.swf.tags.DefineTag;
import flex2.compiler.io.VirtualFile;
import java.util.Map;

public final class AssetInfo
{
    private DefineTag defineTag;
    private VirtualFile path;
    private long creationTime;
    private Map<String, Object> args;
    
    public AssetInfo(DefineTag defineTag, VirtualFile path, long creationTime, Map<String, Object> args)
    {
        this.defineTag = defineTag;
        this.path = path;
        this.creationTime = creationTime;
        this.args = args;
    }

    AssetInfo(DefineTag defineTag)
    {
        this.defineTag = defineTag;
    }

    public Map<String, Object> getArgs()
    {
        return args;
    }

    public long getCreationTime()
    {
        return creationTime;
    }

    public DefineTag getDefineTag()
    {
        return defineTag;
    }

    /**
     * This is used by the webtier compiler.
     */
    public VirtualFile getPath()
    {
        return path;
    }

    void setDefineTag(DefineTag defineTag)
    {
        this.defineTag = defineTag;
    }
}
