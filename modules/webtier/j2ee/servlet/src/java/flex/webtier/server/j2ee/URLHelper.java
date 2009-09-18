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

import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.Iterator;

public class URLHelper {

    public static String getQueryString(String url)
    {
        String queryString = null;

        if (url != null) {
            int queryMark = url.indexOf('?');
            if (queryMark != -1) {
                queryString = url.substring(queryMark + 1);
            }
        }

        return queryString;
    }

    public static Map getParameterMap(String queryString)
    {
        Map map;

        if (queryString != null) {
            StringTokenizer tokens = new StringTokenizer(queryString, "?&");
            // multiply by 2 to create a sufficiently large HashMap
            map = new HashMap(tokens.countTokens() * 2);

            while (tokens.hasMoreElements()) {
                String nameValuePair = tokens.nextToken();
                String name = nameValuePair;
                String value = "";
                int equalsIndex = nameValuePair.indexOf('=');
                if (equalsIndex != -1) {
                    name = nameValuePair.substring(0, equalsIndex);
                    if (name.length() > 0) {
                        value = nameValuePair.substring(equalsIndex + 1);
                    }
                }
                map.put(name, value);
            }
        } else {
            map = new HashMap();
        }

        return map;
    }

    /**
     * returns null if there are no elements in the map or if
     * the map is null
     */
    public static String encode(Map parameterMap) { return encode(parameterMap, false); }

    public static String encode(Map parameterMap, boolean quoteAmpersands)
    {

        if ( (parameterMap != null) && ( !parameterMap.isEmpty() ) ) {
            StringBuffer queryString = new StringBuffer();

            Iterator it = parameterMap.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry entry = (Map.Entry) it.next();
                String name = (String) entry.getKey();
                String value = String.valueOf(entry.getValue());
                queryString.append(URLEncoder.encode(name));
                if ( (value != null) && (!value.equals("")) ) {
                    queryString.append('=');
                    queryString.append(URLEncoder.encode(value));
                }
                if (it.hasNext()) {
                     queryString = (quoteAmpersands) ? queryString.append("\"&\"") : queryString.append('&');
                }
            }

            return queryString.toString();
        } else {
            return null;
        }
    }

	// shortcut for converting spaces to %20 in URIs
	public static String escapeSpace(String uri) { return escapeCharacter(uri, ' ', "%20"); }

	/**
	 * Locates characters 'c' in the scheme specific portion of a URI and translates them into 'to'
	 */
	public static String escapeCharacter(String uri, char c, String to)
	{
		StringBuffer sb = new StringBuffer();

		int size = uri.length();
		int at = uri.indexOf(':');
		int lastAt = 0;

		// skip the scheme
		if (at > -1)
		{
			for(int i=0; i<=at; i++)
				sb.append(uri.charAt(i));
			lastAt = ++at;
		}

		// while we have 'c's in uri
		while( (at = uri.indexOf(c, at)) > -1)
		{
			// original portion
			for(int i=lastAt; i<at; i++)
				sb.append(uri.charAt(i));

			// conversion
			sb.append(to);
			lastAt = ++at;  // advance to char after conversion
		}

		if (lastAt < size)
		{
			for(int i=lastAt; i<size; i++)
				sb.append(uri.charAt(i));
		}
		return sb.toString();
	}
}
