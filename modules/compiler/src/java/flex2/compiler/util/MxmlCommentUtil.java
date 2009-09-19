// //////////////////////////////////////////////////////////////////////////////
//
// ADOBE SYSTEMS INCORPORATED
// Copyright 2008 Adobe Systems Incorporated
// All Rights Reserved.
//
// NOTICE: Adobe permits you to use, modify, and distribute this file
// in accordance with the terms of the license agreement accompanying it.
//
// //////////////////////////////////////////////////////////////////////////////

package flex2.compiler.util;

import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This class converts the plain comment to the format expected by the
 * ClassTable.
 * 
 * @author gauravj
 */
public class MxmlCommentUtil
{
    public static String commentToXmlComment(String comment)
    {
        if(!comment.equals(""))
        {
            // replace all asterisk at the beginning of the line. Some users may
            // copy paste comments from as to mxml.
            Pattern pattern = Pattern.compile("^\\s*\\*", Pattern.MULTILINE);
            Matcher matcher = pattern.matcher(comment);
            comment = matcher.replaceAll("");            
        }

        StringBuilder commentBuilder = new StringBuilder();

        // If there is a comment there has to be a description. It can be "",
        // but it must be present.
        String tagName = "description";

        int index = comment.indexOf("@");

        // Presence of @ means there could be special tags like @see, @copy,
        // @inheritDoc etc.
        if (index != -1)
        {
            // lets get the description in then parse special tags.
            commentBuilder.append("<");
            commentBuilder.append(tagName);
            commentBuilder.append("><![CDATA[");
            commentBuilder.append(comment.substring(0, index));
            commentBuilder.append("]]></");
            commentBuilder.append(tagName);
            commentBuilder.append(">");
            tagName = "";

            comment = comment.substring(index);

            String[] commentFragment = comment.split("@");

            for (int ix = 0; ix < commentFragment.length; ix++)
            {
                StringTokenizer tokenizer = new StringTokenizer(commentFragment[ix], " \t\n\r\f", true);
                if (tokenizer.hasMoreElements())
                {
                    tagName = ((String)tokenizer.nextElement()).trim();
                }

                String tagContent = "";

                // build the content for a tag.
                while (tokenizer.hasMoreElements())
                {
                    tagContent += tokenizer.nextElement();
                }

                // if there is a tag name. lets wrap content inside the tag.
                if (!tagName.trim().equals(""))
                {
                    commentBuilder.append("<");
                    commentBuilder.append(tagName);
                    commentBuilder.append("><![CDATA[");
                    commentBuilder.append(tagContent);
                    commentBuilder.append("]]></");
                    commentBuilder.append(tagName);
                    commentBuilder.append(">");
                }
                else
                {
                    commentBuilder.append(tagContent);
                }
            }
        }
        else
        {
            // there are no special tags. lets just process the description.
            commentBuilder.append("<");
            commentBuilder.append(tagName);
            commentBuilder.append("><![CDATA[");
            commentBuilder.append(comment);
            commentBuilder.append("]]></");
            commentBuilder.append(tagName);
            commentBuilder.append(">");
        }

        return commentBuilder.toString();
    }
}
