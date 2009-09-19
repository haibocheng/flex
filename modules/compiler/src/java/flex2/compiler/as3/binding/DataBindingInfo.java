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

package flex2.compiler.as3.binding;

import flex2.compiler.mxml.rep.BindingExpression;
import flex2.compiler.util.NameFormatter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class DataBindingInfo
{
    private String className;
    /**
     * Root watchers are watchers that watch things hanging off the
     * document (as opposed to children).
     */
    private Map<String, Watcher> rootWatchers;
    private List<BindingExpression> bindingExpressions;
    private String watcherSetupUtilClassName;
    private Set<String> imports;

    public DataBindingInfo(Set<String> imports)
    {
        this.imports = imports;
        rootWatchers = new HashMap<String, Watcher>();
    }

    public List<BindingExpression> getBindingExpressions()
    {
        return bindingExpressions;
    }

    public String getClassName()
    {
        return className;
    }

    public Set<String> getImports()
    {
        return imports;
    }

    public Map<String, Watcher> getRootWatchers()
    {
        return rootWatchers;
    }

    public String getWatcherSetupUtilClassName()
    {
        return watcherSetupUtilClassName;
    }

    public void setBindingExpressions(List<BindingExpression> bindingExpressions)
    {
        this.bindingExpressions = bindingExpressions;
    }

    public void setClassName(String className)
    {
        this.className = NameFormatter.toDot(className);
        watcherSetupUtilClassName = "_" + this.className.replace('.', '_') + "WatcherSetupUtil";
    }
}
