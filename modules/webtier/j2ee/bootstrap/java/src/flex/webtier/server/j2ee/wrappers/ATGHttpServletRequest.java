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
package flex.webtier.server.j2ee.wrappers;

import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletRequest;
import java.util.Map;
import java.util.HashMap;
import java.util.Enumeration;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import java.text.SimpleDateFormat;
import java.text.FieldPosition;
import java.text.ParseException;

/**
 * Wraps a HttpServletRequest and uses the wrapped method for all but getParameterMap and getDateHeader
 *
 * @author Brian Deitte
 */
public class ATGHttpServletRequest extends HttpServletRequestWrapper
{
    private static HeaderDateFormat dateFormat = new HeaderDateFormat(HeaderDateFormat.HTTP_FORMAT);

    public ATGHttpServletRequest(HttpServletRequest httpServletRequest)
    {
        super(httpServletRequest);
    }

    public Map getParameterMap()
    {
        // ATG once again is broken.  getParameterMap returns null so we create the map ourselves
        HashMap map = new HashMap();
        Enumeration enumer = getParameterNames();
        while (enumer.hasMoreElements())
        {
            String str = (String) enumer.nextElement();
            map.put(str, getParameterValues(str));
        }

        return map;
    }

    public long getDateHeader(String header)
    {
        // ATG is broken and doesn't give back date headers.  so we parse it ourselves
        return dateFormat.parseDate(getHeader(header));
    }

    /**
     * faster header date formatter.  Values are only accurate
     * to the second and can therefore be cached
     *
     * For reference, the latest HTTP spec is here:
     * http://www.ietf.org/rfc/rfc2616.txt
     *
     * @author Karl Moss
     * @author Edwin Smith
     */
    static final class HeaderDateFormat
    {
        /**
         * http header format.  given by RFC 822, updated in RFC 1123,
         * for example: Sun, 06 Nov 1994 08:49:37 GMT
         */
        public static final String HTTP_FORMAT = "EE, dd MMM yyyy HH:mm:ss z";

        /**
         * cookie format.  same as http format, except only legal timezone is GMT
         * and dashes are used as date field separators.  Given by RFC 850
         * and obsoleted by RFC 1036
         */
        public static final String COOKIE_FORMAT = "EEE, dd-MMM-yyyy HH:mm:ss z";

        private static final int CAPACITY = 8;  // must be power of 2
        private final SimpleDateFormat formatter;
        private final String[] lastDate;
        private final Date date;
        private final FieldPosition pos;
        private final long[] lastTime;
        private final int length;

        HeaderDateFormat(String format)
        {
            formatter = new SimpleDateFormat(format, Locale.US);
            length = 2*format.length();
            formatter.setTimeZone(TimeZone.getTimeZone("GMT"));
            date = new Date();
            pos = new FieldPosition(0);
            lastDate = new String[CAPACITY];
            lastTime = new long[CAPACITY];
        }

        String formatDate(long time)
        {
            long qtime = time/1000;
            int i = (int) ( qtime & (CAPACITY-1) );

            synchronized (formatter)
            {
                if (qtime == lastTime[i])
                {
                    return lastDate[i];
                }
                else
                {
                    lastTime[i] = qtime;
                    date.setTime(time);
                    return lastDate[i] = formatter.format(date, new StringBuffer(length), pos).toString();
                }
            }
        }

        public long parseDate(String text)
        {
            // fixme - see HTTP spec.  we should parse both formats and also
            // asctime() format.
            if (text == null)
            {
                return -1;
            }

            synchronized(formatter)
            {
                try
                {
                    return formatter.parse(text).getTime();
                } catch (ParseException ex) {
                    throw new IllegalArgumentException(ex.getMessage());
                }
            }
        }
    }
}
