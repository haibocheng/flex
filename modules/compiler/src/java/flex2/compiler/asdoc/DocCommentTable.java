////////////////////////////////////////////////////////////////////////////////
//
//  ADOBE SYSTEMS INCORPORATED
//  Copyright 2007-2008 Adobe Systems Incorporated
//  All Rights Reserved.
//
//  NOTICE: Adobe permits you to use, modify, and distribute this file
//  in accordance with the terms of the license agreement accompanying it.
//
////////////////////////////////////////////////////////////////////////////////

package flex2.compiler.asdoc;

import java.util.List;
import java.util.Map;

public interface DocCommentTable
{
    /**
     * @return Map of all packages where key = package name, value = DocComment.
     */
    public Map getPackages();
    
    /**
     * Useful to retrieve all the class names from a package (since they must be unique
     * within a package).
     * 
     * @param packageName
     * @return Map of all classes and interfaces in a specific package where
     * key = class or interface name, value = DocComment.
     *
     */
    public Map getClassesAndInterfaces(String packageName);
    
    
    /**
     * @param className
     * @param packageName
     * @return all the DocComments associated with the specified class and package
     */
    public List getAllClassComments(String className, String packageName);
}
