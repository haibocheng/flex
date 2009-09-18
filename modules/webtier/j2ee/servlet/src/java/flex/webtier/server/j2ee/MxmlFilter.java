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
package flex.webtier.server.j2ee;

/**
 * Base filter definition that defines the filter contract.
 * Filters perform pre- and post-processing duties on the MxmlContext
 *
 * @author cmurphy
 */
public abstract class MxmlFilter
{
    protected MxmlFilter next;

    public MxmlFilter()
    {
    }

    public void setNext(MxmlFilter next)
    {
        this.next = next;
    }

    /**
     * The core business method
     *
     * @param context
     * @throws Throwable
     */
    public abstract void invoke(MxmlContext context) throws Throwable;
}
