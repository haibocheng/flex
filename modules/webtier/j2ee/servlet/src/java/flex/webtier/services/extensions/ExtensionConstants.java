////////////////////////////////////////////////////////////////////////////////
//
//  ADOBE SYSTEMS INCORPORATED
//  Copyright 2004-2007 Adobe Systems Incorporated
//  All Rights Reserved.
//
//  NOTICE: Adobe permits you to use, modify, and distribute this file
//  in accordance with the terms of the license agreement accompanying it.
//
////////////////////////////////////////////////////////////////////////////////
package flex.webtier.services.extensions;

/**
 * If the swf ends in a .swf extension, the debug file is specified as .swd.
 *  ex. xxx.swf -> xxx.swd
 * If the ends with any other extension, the debug file is that extension plus .swd.
 *  ex. xxx.ext -> xxx.ext.swd
 */
public class ExtensionConstants
{

    /** general swf file extension */
    public static String SWF_EXT = ".swf";

    /** general swd file extension */
    /** changing this is unsupported */
    public static String SWD_EXT = ".swd";

    /** compiled mxml file extensions */
    public static String MXML_SWF_EXT = ".mxml.swf";

    /** compiled mxml file extensions - debug */
    /** changing this is unsupported */
    public static String MXML_SWD_EXT = ".mxml.swd";

    /** compile swc extensions */
    public static String SWC_SWF_EXT = ".swc.swf";

    /** compiled swc extensions - debug */
    /** changing this is unsupported */
    public static String SWC_SWD_EXT = ".swc.swd";

    public static String AS_SWF_EXT = ".as.swf";

    /** mxml source file extensions */
    public static String MXML_EXT = ".mxml";

    /** as source file extensions */
    public static String AS_EXT = ".as";

    /** swc source file extension */
    public static String SWC_EXT = ".swc";

    /** sws source file extension */
    public static String SWS_EXT = ".sws";
}
