////////////////////////////////////////////////////////////////////////////////
//
//  ADOBE SYSTEMS INCORPORATED
//  Copyright 2003-2006 Adobe Systems Incorporated
//  All Rights Reserved.
//
//  NOTICE: Adobe permits you to use, modify, and distribute this file
//  in accordance with the terms of the license agreement accompanying it.
//
////////////////////////////////////////////////////////////////////////////////

package flex2.compiler.as3.binding;

public class PropertyWatcher extends Watcher
{
    private String property;
	private boolean suppressed;
    private boolean staticProperty;

    public PropertyWatcher(int id, String property)
    {
        super(id);
        this.property = property;
		suppressed = false;
	}

	public boolean shouldWriteSelf()
	{
        boolean result = !suppressed;

        // Fixes SDK-18764 by making sure we write out Watchers, when
        // they have unsuppressed children, even if the Watcher is
        // suppressed.
        if (suppressed)
        {
            for (Watcher watcher : childWatchers.values())
            {
                // ArrayElementWatcher.shouldWriteSelf() calls
                // parent.shouldWriteSelf(), so skip them to avoid
                // infinite recursion.
                if (!(watcher instanceof ArrayElementWatcher) && watcher.shouldWriteSelf())
                {
                    result = true;
                    break;
                }
            }
        }

		return result;
	}

    public String getPathToProperty()
    {
        String result;

        Watcher parent = getParent();
        if (parent instanceof PropertyWatcher)
        {
            PropertyWatcher parentPropertyWatcher = (PropertyWatcher) parent;

            result = parentPropertyWatcher.getPathToProperty() + "." + property;
        }
        else
        {
            result = property;
        }

        return result;
    }

    public String getProperty()
    {
        return property;
    }

    public boolean getStaticProperty()
    {
        return staticProperty;
    }

    public void setStaticProperty(boolean staticProperty)
    {
        this.staticProperty = staticProperty;
    }

	public void suppress()
	{
		suppressed = true;
	}
}
