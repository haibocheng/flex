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
package flex.webtier.server.j2ee;

import flex.webtier.services.ServiceFactory;

public class FileNameGenerator {

    private static String mxmlExt;
    private static String swfExt;
    private static String mxmlSwfExt;
    private static String asExt;
    private static String asSwfExt;

    private static String stripSuffixes(String fileName)
    {
        int index = -1;

        if (mxmlExt == null) {
            mxmlExt = ServiceFactory.getExtensionManager().getMxmlExt();
        }
        // handles .mxml, .mxml.swf, and .mxml.swd
        if (fileName.lastIndexOf(mxmlExt) != -1) {
            index = fileName.lastIndexOf(mxmlExt);
        } else if (fileName.lastIndexOf(asExt) != -1) {
            index = fileName.lastIndexOf(asExt);
        } else if (fileName.lastIndexOf('.') != -1) {
            index = fileName.lastIndexOf('.');
        }

        if (index == -1) {
            return fileName;
        } else {
            return fileName.substring(0, index);
        }
    }

    public static String sourceFileName(String fileName)
    {
        if (mxmlExt == null) {
            mxmlExt = ServiceFactory.getExtensionManager().getMxmlExt();
        }
        if (asExt == null) {
            asExt = ServiceFactory.getExtensionManager().getAsExt();
        }

        if (fileName.lastIndexOf(asExt) != -1) {
            return stripSuffixes(fileName) + asExt;
        } else {
            return stripSuffixes(fileName) + mxmlExt;
        }
    }

    public static String generatedSwfFileName(String fileName)
    {
        if (swfExt == null) {
            swfExt = ServiceFactory.getExtensionManager().getSwfExt();
        }
        return stripSuffixes(fileName) + swfExt;
    }

    public static String swfFileName(String fileName)
    {
        if (mxmlSwfExt == null) {
            mxmlSwfExt = ServiceFactory.getExtensionManager().getMxmlSwfExt();
        }
        if (asExt == null) {
            asExt = ServiceFactory.getExtensionManager().getAsExt();
        }
        if (asSwfExt == null) {
            asSwfExt = ServiceFactory.getExtensionManager().getAsSwfExt();
        }
        if (swfExt == null) {
            swfExt = ServiceFactory.getExtensionManager().getSwfExt();
        }

        if (fileName.lastIndexOf(asExt) != -1) {
            return stripSuffixes(fileName) + asSwfExt;
        } 
        else if (ServiceFactory.isMxmlSwfExtension()) {
            return stripSuffixes(fileName) + mxmlSwfExt;            
        }
        else {
            return stripSuffixes(fileName) + swfExt;
        }
    }
}
