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
package flex.webtier.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Vector;

public class StringUtils {
    public static String quoteIfNotNull(String str) {
        return str = (str != null) ? str = "'" + escapeApostrophe(str) + "'" : null;
    }

    public static String doubleQuoteIfNotNull(String str) {
        return str = (str != null) ? str = "\"" + str + "\"" : null;
    }

    public static String escapeApostrophe(String str) {
        if (str.indexOf("'") > -1) {
            return substitute(str, "'", "''");
        }
        return str;
    }

    public static String appendToQuotedString(String str1, String str2) throws Exception {
        StringBuffer sb = new StringBuffer();
        if (str1.startsWith("\"") && str1.endsWith("\"")) {
            sb.append(str1);
            sb.insert(sb.length() - 1, str2);
        }
        else {
            sb.append("\"");
            sb.append(str1);
            sb.append(str2);
            sb.append("\"");
        }
        return sb.toString();
    }

    /**
     * This function will replace all double characters with the a single
     * character
     */
    public static String removeDoubleChar(String str, char c) {
        // Same as removeDuplicates
        return removeDuplicates(str, c);
    }

    /**
     * Removes the all sequential duplicate characters from the given string.
     * @param str The string
     * @param c The character to compress
     * @return The new string
     */
    public static String removeDuplicates(String str, char c) {
        if (str == null) return str;

        // Before going through the trouble, at least make sure there
        // are some of the characters we're looking for
        int newCharPos = str.indexOf(c);
        if ((newCharPos < 0) || ((newCharPos + 1) == str.length())) return str;

        // Can't have duplicates in a null string or a string with
        // less than 2 characters
        if ((str == null) || (str.length() < 2)) {
            return str;
        }

        char[] chars = str.toCharArray();
        char[] newChars = new char[chars.length];
        newCharPos = 0;

        boolean lastWasChar = false;

        for (int i = 0; i < chars.length; i++) {

            char thisChar = chars[i];
            if (thisChar == c) {
                // If this is the character we are compressing and
                // the last character we saw was the same, skip
                // this one
                if (lastWasChar) {
                    continue;
                }
                lastWasChar = true;
            }
            else {
                lastWasChar = false;
            }
            newChars[newCharPos++] = thisChar;
        }

        return new String(newChars, 0, newCharPos);
    }

    /**
     * Finds the first occurance of 'c' that is not a double 'c' (i.e. '##')
     * @return	The index of the occurance of the char in the string
     */
    public static int findSingleChar(String str, int currentIndex, int endIndex, char c) {
        if (str == null) return endIndex;
        boolean foundChar = false;
        int strIndex = endIndex;

        while (foundChar == false) {
            strIndex = str.indexOf(c, currentIndex); //indexOf returns -1 if not found

            // Found no pounds, so return the end of string ;
            if (strIndex == -1)
                return endIndex;

            // Check to see if this is actually an escaped pound (i.e. ##)
            if (str.charAt(strIndex + 1) == c)
                currentIndex = strIndex + 2;
            else
                foundChar = true;
        }

        return strIndex;
    }


    /**
     * Load a file and return the contents of the file.
     * @return	The contents of the file.
     */
    public static String loadFileToString(String filename) throws IOException {
        if (filename == null) return null;

        File f = new File(filename);
        int size = (int) f.length();
        int bytes_read = 0;
        FileInputStream in = new FileInputStream(f);
        byte[] data = new byte[size];

        while (bytes_read < size)
            bytes_read += in.read(data, bytes_read, size - bytes_read);

        in.close();

        return new String(data);
    }


    /**
     * Parses an argument list and returns an array of Strings of those
     * arguments.
     * @return	An array of Strings containing the arguments
     */
    public static String[] parseArguments(String str, Character trimChar) {
        if (str == null) return null;

        int quote = 0;
        int paren = 0;
        boolean done = false;
        Vector args = new Vector();
        int index, endIndex;

        // Convert the string into a character array
        char[] chars = str.toCharArray();

        // Now reset the end of beginning index vars
        endIndex = str.length() - 1;
        index = 0;

        // Find all of the arguments
        while (!done) {

            StringBuffer param = new StringBuffer(255);

            int lastIndex = index;

            while ((index < endIndex) &&
                    ((quote > 0) || (paren > 0) ||
                    ((chars[index] != ')' &&
                    (index == 0 || (chars[index] != ',' &&
                    chars[index - 1] != '\\')))))) {

                if (chars[index] == '\\') {
                    index++;

                }
                else if (chars[index] == '"') {
                    quote = 1 - quote;

                }
                else if (chars[index] == '(') {
                    paren++;

                }
                else if (chars[index] == ')') {
                    paren--;
                }

                param.append(chars[index++]);
            }

            // Did we actually find something? If so, trim it, send it.
            if (index > lastIndex) {
                String s = param.toString().trim();
                if (s.equals("") == false && s.charAt(0) == '"' && s.charAt(s.length() - 1) == '"')
                    s = s.substring(1, s.length() - 1);
                args.addElement(s);
            }

            // Move past the stopping character
            index++;

            // Make sure we're not past the end
            if (index >= endIndex)
                done = true;
        }

        // Create a new string array as large as the vector
        String[] strArgs = new String[args.size()];

        // Move a shapshot of the vector into the string array
        for (int i = 0; i < args.size(); i++) {
            String arg = (String) args.elementAt(i);
            if (trimChar != null) {
                if (arg != null && arg.length() > 1 && arg.charAt(0) == trimChar.charValue())
                    arg = arg.substring(1);
            }

            strArgs[i] = arg;
        }

        return strArgs;
    }


    /**
     * @param value String value to replace quotes
     * @param wrapQuotes when set to true, will wrap the string in
     *        singlequotes.
     **/
    public static String databaseQuote(String value, boolean wrapQuotes) {
        if (value == null) return value;

        String result = "";
        if (wrapQuotes) result = "'";

        for (int i = 0; i < value.length(); i++) {
            String s = value.substring(i, i + 1);
            if (s.equals("'"))
                result += "''";
            else
                result += s;
        }

        if (wrapQuotes) result += "'";

        return result;
    }

    public static String databaseQuote(String value) {
        return databaseQuote(value, true);
    }

    public static String surroundWithQuotes(String name) {
        return "\"" + name + "\"";
    }

    public static String surroundWithSingleQuotes(String name) {
        return "\'" + name + "\'";
    }

    public static String escapeQuotes(String s) {
        if (s == null) return s;

        StringBuffer sb = new StringBuffer();

        int i = 0;
        int len = s.length();
        while (i < len) {
            char c = s.charAt(i);
            if (c == '\"')
                sb.append("\\\"");
            else
                sb.append(c);
            i++;
        }

        return sb.toString();
    }

    /// Checks whether a string matches a given wildcard pattern.
    // Only does ? and *, and multiple patterns separated by |.
    public static boolean matchString(String pattern, String string, char sep) {
        if (pattern == null || string == null)
            return false;

        for (int p = 0; ; ++p) {
            for (int s = 0; ; ++p, ++s) {
                boolean sEnd = (s >= string.length());
                boolean pEnd = (p >= pattern.length() ||
                        pattern.charAt(p) == sep);
                if (sEnd && pEnd)
                    return true;
                if (sEnd || pEnd)
                    break;
                if (pattern.charAt(p) == '?')
                    continue;
                if (pattern.charAt(p) == '*') {
                    int i;
                    ++p;
                    for (i = string.length(); i >= s; --i)
                        if (matchString(
                                pattern.substring(p),
                                string.substring(i)))  /* not quite right */
                            return true;
                    break;
                }
                if (pattern.charAt(p) != string.charAt(s))
                    break;
            }
            p = pattern.indexOf(sep, p);
            if (p == -1)
                return false;
        }
    }

    /// Checks whether a string matches a given wildcard pattern.
    // Only does ? and *, and multiple patterns separated by |.
    public static boolean matchString(String pattern, String string) {
        return matchString(pattern, string, '|');
    }

    /// Sorts an array of Strings.
    // Java currently has no general sort function.  Sorting Strings is
    // common enough that it's worth making a special case.
    public static void sortStrings(String[] strings) {
        if (strings != null) {
            // Just does a bubblesort.
            for (int i = 0; i < strings.length - 1; ++i) {
                for (int j = i + 1; j < strings.length; ++j) {
                    if (strings[i].compareTo(strings[j]) > 0) {
                        String t = strings[i];
                        strings[i] = strings[j];
                        strings[j] = t;
                    }
                }
            }
        }
    }

    public static String leftPad(String str, int pad) {
        if (str == null)
            return null;

        if (str.length() >= pad)
            return str;

        int len = pad - str.length();

        for (int i = 0; i < len; i++)
            str = " " + str;

        return str;
    }

    /**
     * Simple string replace routine, return the same string with
     * all instances of from replaced with to
     **/
    public static String substitute(String str, String from, String to) {
        if (from == null || from.equals("") || to == null)
            return str;

        int index = str.indexOf(from);

        if (index == -1)
            return str;

        StringBuffer buf = new StringBuffer(str.length());
        int lastIndex = 0;

        while (index != -1) {
            buf.append(str.substring(lastIndex, index));
            buf.append(to);
            lastIndex = index + from.length();
            index = str.indexOf(from, lastIndex);
        }

        // add in last chunk
        buf.append(str.substring(lastIndex));

        return buf.toString();
    }

    /**
     * Pull a parameter from a MIME message header
     * @param header The header to pull the param from
     * @param parameter The parameter to look for
     **/
    public static String getHeaderParameter(String header, String parameter) {
        return getHeaderParameter(header, parameter, null);
    }

    /**
     * Pull a parameter from a MIME message header
     * @param header The header to pull the param from
     * @param parameter The parameter to look for
     * @param default Default value if the parameter isn't found
     **/
    public static String getHeaderParameter(String header, String parameter, String deflt) {
        if (header == null || parameter == null)
            return deflt;

        int i = header.indexOf(parameter);

        if (i == -1)
            return deflt;

        int start = header.indexOf('=', i) + 1;
        int end = header.indexOf(';', start);

        if (end == -1)
            return header.substring(start).trim();
        else
            return header.substring(start, end).trim();
    }

    /**
     * Take a header and strip any parameters from it
     **/
    public static String stripParamsFromHeader(String header) {
        if (header == null)
            return null;

        int semi_colon = header.indexOf(';');

        if (semi_colon == -1)
            return header;

        return header.substring(0, semi_colon).trim();
    }

    /**
     * Says if char can appear after a backslash in a Java String literal
     **/
    public static boolean isJavaEscape(char c) {
        return (c == 'b' || c == 't' || c == 'n' || c == 'f' ||
                c == 'r' || c == '"' || c == '\'' || c == '0' ||
                c == '1' || c == '2' || c == '3' || c == '4' ||
                c == '5' || c == '6' || c == '7' || c == '\\' ||
                c == 'u');
    }

    /**
     * count the occurances of a char in a string
     **/
    public static int occurrences(String s, char c) {
        if (s == null) return 0;

        char[] ca = s.toCharArray();
        int count = 0;
        int i = 0;
        for (; i < ca.length; i++)
            if (ca[i] == c)
                count++;

        return count;
    }

    /**
     * Convert a string array to a string separated by some char.
     **/
    public static String arrayToString(String[] str, char sep) {
        if (str == null) return null;

        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < str.length; i++) {
            sb.append(str[i]);
            sb.append(sep);
        }
        return sb.toString();
    }

    /**
     * <p>Entitize the given HTML buffer. This process will convert
     * the following characters into HTML entities:
     * <dir><pre>
     * < to &lt;
     * > to &gt;
     * </pre></dir>
     * @param buffer The HTML buffer
     * @returns The converted buffer
     */
    public static String entitizeHtml(String buffer) {
        if (buffer == null) return buffer;

        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < buffer.length(); i++) {
            char c = buffer.charAt(i);
            switch (c) {
                case '>':
                    sb.append("&gt;");
                    break;
                case '<':
                    sb.append("&lt;");
                    break;
                default:
                    sb.append(c);
            }
        }
        return sb.toString();
    }

    public static String lowerCaseFirstInitial(String str) {
        if (str != null && str.length() >= 1)
            return str.substring(0, 1).toLowerCase() + str.substring(1);
        return str;
    }

    public static String upperCaseFirstInitial(String str) {
        if (str != null && str.length() >= 1)
            return str.substring(0, 1).toUpperCase() + str.substring(1);
        return str;
    }
}

