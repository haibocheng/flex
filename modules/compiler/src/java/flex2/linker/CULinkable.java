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

package flex2.linker;

import flex2.compiler.CompilationUnit;
import flex2.compiler.util.MultiName;
import flex2.compiler.util.Name;
import flex2.compiler.util.QName;

import java.util.Iterator;
import java.util.Set;
import java.util.HashSet;

/**
 * @author Roger Gonzalez
 */
public class CULinkable implements Linkable
{
    public CULinkable( CompilationUnit unit )
    {
        this.unit = unit;
        defs.addAll( unit.topLevelDefinitions.getStringSet() );
        addDeps( prereqs, unit.inheritance );
        addDeps( deps, unit.expressions );
        addDeps( deps, unit.namespaces );
        addDeps( deps, unit.types );
        deps.addAll( unit.extraClasses );
	    deps.addAll( unit.resourceBundles );	    
    }
    public String getName()
    {
        return unit.getSource().getName();
    }

    public CompilationUnit getUnit()
    {
        return unit;
    }

    public long getLastModified()
    {
        return unit.getSource().getLastModified();
    }

    public long getSize()
    {
        return unit.bytes.size();
    }

    public boolean hasDefinition( String defName )
    {
        return defs.contains( defName );
    }

    public Iterator<String> getDefinitions()
    {
        return defs.iterator();
    }

    public Iterator<String> getPrerequisites()
    {
        return prereqs.iterator();
    }

    public Iterator<String> getDependencies()
    {
        return deps.iterator();
    }
    public String toString()
    {
        return unit.getSource().getName();
    }

    public void addDep( String val )
    {
        deps.add( val );
    }

    public boolean dependsOn( String s )
    {
        return deps.contains( s ) || prereqs.contains( s );
    }

    public boolean isNative()
    {
        return unit.getSource().isInternal();
    }

    // todo - nuke this
    private void addDeps( Set<String> set, Set<Name> nameSet )
    {
        for (Name name : nameSet)
        {
            if (name instanceof MultiName)
            {
                MultiName mname = (MultiName) name;
                // FIXME - deps should only have a single qname in their multiname

                assert mname.getNumQNames() == 1;

                set.add( mname.getQName( 0 ).toString() );
            }
            else
            {
                assert name instanceof QName;
                set.add( name.toString() );
            }
        }
    }
    private final Set<String> defs = new HashSet<String>();
    private final Set<String> prereqs = new HashSet<String>();
    private final Set<String> deps = new HashSet<String>();

    private final CompilationUnit unit;
}
