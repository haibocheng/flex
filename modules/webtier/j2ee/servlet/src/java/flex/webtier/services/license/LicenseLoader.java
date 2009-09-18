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

import java.util.Properties;

public interface LicenseLoader
{
    String LICENSE_KEY_PROPERTY = "sn";
    String FLEXBUILDER3_LICENSE_KEY_PROPERTY = "flexbuilder3";
    String FB_LICENSE_KEY_PROPERTY = "fb";
    String FDS_LICENSE_KEY_PROPERTY = "fds";
    String EVALUATION_CODE_PROPERTY = "code";
    String FLEXBUILDER3_EVALUATION_CODE_PROPERTY = "11";
    String FB_EVALUATION_CODE_PROPERTY = "33";
    String FDS_EVALUATION_CODE_PROPERTY = "22";
    String LICENSE_FILE_NAME = "license.properties";
    
    Properties loadHiddenProperties();
    Properties loadProperties() throws Exception;
    void storeHiddenProperties(Properties hidden) throws Exception;
    void storeProperties(Properties props) throws Exception;
}
