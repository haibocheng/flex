////////////////////////////////////////////////////////////////////////////////
//
//  ADOBE SYSTEMS INCORPORATED
//  Copyright 2005 Adobe Systems Incorporated
//  All Rights Reserved.
//
//  NOTICE: Adobe permits you to use, modify, and distribute this file
//  in accordance with the terms of the license agreement accompanying it.
//
////////////////////////////////////////////////////////////////////////////////

package flex2.compiler.i18n;

/**
 * @author Brian Deitte
 */
public class TranslationException extends Exception
{
    private static final long serialVersionUID = 1863414006518097804L;

    public TranslationException(String str)
    {
        super(str);
    }

    public TranslationException(Exception e)
    {
        super(e);
    }
}
