// //////////////////////////////////////////////////////////////////////////////
//
// ADOBE SYSTEMS INCORPORATED
// Copyright 2009 Adobe Systems Incorporated
// All Rights Reserved.
//
// NOTICE: Adobe permits you to use, modify, and distribute this file
// in accordance with the terms of the license agreement accompanying it.
//
// //////////////////////////////////////////////////////////////////////////////

package com.adobe.internal.fxg.sax;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import com.adobe.fxg.FXGVersion;

/**
 * Maintains a registry of FXGVersionHandlers
 * 
 * @author Sujata Das
 */
public class FXGVersionHandlerRegistry
{

    private static HashMap<FXGVersion, FXGVersionHandler> versionHandlers = new HashMap<FXGVersion, FXGVersionHandler>(4);
    public static FXGVersion defaultVersion = FXGVersion.v2_0;
    private static boolean registered = false;

    /**
     * Registers known FXGVersionHandlers for FXG 1.0 and FXG 2.0
     */
    private static void registerKnownHandlers()
    {
        if (registered)
            return;
        registerHandler(FXGVersion.v1_0, new FXG_v1_0_Handler());
        registerHandler(FXGVersion.v2_0, new FXG_v2_0_Handler());
        registered = true;
    }

    /**
     * Returns the default FXGVersion Handler - FXG 2.0 in this case
     * 
     * @return
     */
    protected static FXGVersionHandler getDefaultHandler()
    {
        registerKnownHandlers();
        return (versionHandlers.get(defaultVersion));
    }

    /**
     * Register new FXGVersionHandler that can overwrite existing
     * FXGVersionHandler if one already exists for that FXG version.
     * 
     * @param version - FXGVersion of the new handler
     * @param obj - FXGVersionHandler
     */
    protected static void registerHandler(FXGVersion version,
            FXGVersionHandler obj)
    {
        if (versionHandlers != null)
        {
            FXGVersionHandler vHandler = versionHandlers.get(version);
            FXGVersion fxgVersion = (vHandler != null) ? vHandler.getVersion() : version;
            versionHandlers.put(fxgVersion, obj);
        }
    }

    /**
     * Unregister handler for the specified FXGVersion
     * 
     * @param version
     */
    protected static void unregisterHandler(FXGVersion version)
    {
        if (versionHandlers != null)
        {
            FXGVersionHandler vHandler = versionHandlers.get(version);
            if (vHandler != null)
            {
                FXGVersion fxgVersion = vHandler.getVersion();
                versionHandlers.remove(fxgVersion);
            }
        }
    }

    /**
     * Returns a Set of FXGVersions that correspond to the registered
     * FXGVersionHandlers
     * 
     * @return Set<FXGVersion> that correspond to the registered
     *         FXGVersionHandlers
     */
    protected static Set<FXGVersion> getVersionsForRegisteredHandlers()
    {
        if (versionHandlers == null)
            return null;
        registerKnownHandlers();
        return versionHandlers.keySet();
    }

    /**
     * Returns the FXGVersionHandler for the FXGVersion specified. Note that
     * FXGVersionHandlers are matched first for an exact match. If exact match
     * is not found, then a handler with the same major version is returned.
     * 
     * @param fxgVersion as a FXGVersion
     * @return
     */
    protected static FXGVersionHandler getVersionHandler(FXGVersion fxgVersion)
    {

        if (versionHandlers == null)
            return null;
        Set<FXGVersion> versions = getVersionsForRegisteredHandlers();

        // look for exact matches on the version
        Iterator<FXGVersion> iter = versions.iterator();
        while (iter.hasNext())
        {
            FXGVersion version = iter.next();
            if (version.equalTo(fxgVersion))
                return versionHandlers.get(version);
        }

        // look for matches based on matching major version
        iter = versions.iterator();
        while (iter.hasNext())
        {
            FXGVersion version = iter.next();
            if (version.getMajorVersion() == fxgVersion.getMajorVersion())
                return versionHandlers.get(version);
        }

        return null;

    }

    /**
     * Returns the FXGVersionHandler for the FXGVersion specified. Note that
     * FXGVersionHandlers are matched first for an exact match. If exact match
     * is not found, then a handler with the same major version is returned.
     * 
     * @param version - version as a double
     * @return
     */
    protected static FXGVersionHandler getVersionHandler(double version)
    {
        FXGVersion fxgVersion = FXGVersion.newInstance(version);
        return getVersionHandler(fxgVersion);
    }

    /**
     * Returns the FXGVersionHandler for the latest version handler registered.
     * 
     * @param version - version as a double
     * @return
     */
    protected static FXGVersionHandler getLatestVersionHandler()
    {
        if (versionHandlers == null)
            return null;
        Set<FXGVersion> versions = getVersionsForRegisteredHandlers();

        // look for exact matches on the version
        Iterator<FXGVersion> iter = versions.iterator();
        FXGVersion latest = null;
        while (iter.hasNext())
        {
            FXGVersion version = iter.next();
            if (latest == null)
            {
                latest = version;
            }
            else
            {
                if (version.greaterThan(latest))
                    latest = version;
            }
        }
        return versionHandlers.get(latest);
    }

}
