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

package flex2.compiler.asdoc;

import java.util.Comparator;

/** 
 * This class is used to sort the string based on the length of the string
 *    
 * @author gauravj
 */
public class SortComparator implements Comparator<String>
{
    public int compare(String first, String second)
    {
        if (first.length() < second.length())
        {
            return -1;
        }
        else if (first.length() > second.length())
        {
            return 1;
        }
        else
        {
            // makes sure that the string are equal only if lexicographically
            // equal. Same length doesn't mean equal.
            return first.compareTo(second);
        }
    }
}
