////////////////////////////////////////////////////////////////////////////////
//
//  ADOBE SYSTEMS INCORPORATED
//  Copyright 2004-2008 Adobe Systems Incorporated
//  All Rights Reserved.
//
//  NOTICE: Adobe permits you to use, modify, and distribute this file
//  in accordance with the terms of the license agreement accompanying it.
//
////////////////////////////////////////////////////////////////////////////////
package flex.webtier.services.license;

import java.util.Map;
import flex2.compiler.common.Configuration;

public interface LicenseService {

    /**
     * Returns whether the specific feature is enabled.
     */
    boolean isMxmlCompileEnabled();

    /**
     * Create a map from license.properties.
     */
    Map getLicenseMap();
    
    /**
      * Consolidate license keys from the LicenseLoader and the flex
      * configuration file.  The flex configuration file takes precedence.
      * 
     */
    Map getConsolidatedLicenseMap(Configuration c);

    /**
     * This method checks if the beta license has expired.
     * If not beta, will always return false.
     */
    boolean isBetaExpired();

    /**
     * Returns the beta expired message.
     */
    String expiredMessage();

    /**
     * Returns special message for non-expired beta.
     */
    String getBetaRemainingMessage();

    /**
     * Load the license, using the specified LicenseLoader.
     */
    void init();
}
