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

/**
 * Objects that implement the Translatable interface can be
 * translated by RB.java using :
 *
 * RB.getString(Translatable instance)
 *
 * RB.getString(String key, Translatable instance)
 *
 * The first method will lookup the translation string using the
 * class name (without the package name) as the translation key.
 * The second method can be used if you want to produce different
 * strings from the same object (based on the key)
 * The translation string in resource.properties can use object fields
 * like {message} instead of positional variables like {0} {1} {2}
 *
 * Example:
 *
 * public class FileNotFoundException extends Exception
 *     implements Translatable
 * {
 *     public String fileName;
 *
 *     public FileNotFoundException(String fileName)
 *     {
 *         this.fileName = fileName;
 *     }
 *
 *    public String toString()
 *    {
 *        return RB.getString(this); // actual string in resource.properties
 *    }
 * }
 *
 * resource.properties:
 * FileNotFoundException = Cannot find the file {fileName}.
 *
 * If you wanted to print a different message based on the same object,
 * you could use:
 * RB.getString("FileNotFound.OtherMessage", new FileNotFoundException("foo"));
 *
 * resource.properties:
 * FileNotFound.OtherMesage: Could not find {fileName} to say the least.
 *
 */

public interface Translatable {
}
