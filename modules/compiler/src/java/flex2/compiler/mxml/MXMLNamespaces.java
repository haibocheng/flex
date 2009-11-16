////////////////////////////////////////////////////////////////////////////////
//
//  ADOBE SYSTEMS INCORPORATED
//  Copyright 2008 Adobe Systems Incorporated
//  All Rights Reserved.
//
//  NOTICE: Adobe permits you to use, modify, and distribute this file
//  in accordance with the terms of the license agreement accompanying it.
//
////////////////////////////////////////////////////////////////////////////////

package flex2.compiler.mxml;

/**
 * A collection of language namespaces used in various versions of MXML.
 * <p>
 * Note that prior to Flex 4, language and component namespaces overlapped.
 * </p>
 */
public interface MXMLNamespaces
{
    public static final String FXG_2008_NAMESPACE = "http://ns.adobe.com/fxg/2008";

    public static final String MXML_1_NAMESPACE = "http://www.macromedia.com/2003/mxml";
    public static final String MXML_2_NAMESPACE = "http://www.macromedia.com/2005/mxml";
    public static final String MXML_2006_NAMESPACE = "http://www.adobe.com/2006/mxml";
    public static final String MXML_2009_NAMESPACE = "http://ns.adobe.com/mxml/2009";

    public static final String SPARK_NAMESPACE = "library://ns.adobe.com/flex/spark";
    public static final String HALO_NAMESPACE = "library://ns.adobe.com/flex/halo";
    public static final String MX_NAMESPACE = "library://ns.adobe.com/flex/mx";
}
