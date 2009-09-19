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

package flex2.compiler.asdoc;

import java.util.ArrayList;

/**
 * Stores the info for the class related to a method/field/class and parent classes.
 * 
 * @author gauravj
 */
public class QualifiedNameInfo
{
    private String packageName;
    /**
     * if method/field is inherited then contains parent classes. 
     */
    private ArrayList<String> classNames;
    /**
     * if method/field is inherited then contains parent classes namespace. 
     */
    private ArrayList<String> classNameSpaces;

    private String methodName;
    private String methodNameSpace;
    
    /** 
     * contains combined value for packagename:class name/method name
     */
    private String fullClassName;
    
    /**
     * This field stores the value to indicate whether method is a getter or setter. 
     * values: Get|Set or null
     */
    private String getterSetter;

    public QualifiedNameInfo()
    {
        packageName = "";
        classNames = new ArrayList<String>();
        classNameSpaces = new ArrayList<String>();
        methodName = "";
        methodNameSpace = "";
        fullClassName = "";
        getterSetter = "";
    }

    public String getPackageName()
    {
        return packageName;
    }

    public void setPackageName(String packageName)
    {
        this.packageName = packageName;
    }

    public ArrayList<String> getClassNames()
    {
        return classNames;
    }

    public void setClassNames(ArrayList<String> classNames)
    {
        this.classNames = classNames;
    }

    public ArrayList<String> getClassNameSpaces()
    {
        return classNameSpaces;
    }

    public void setClassNameSpaces(ArrayList<String> classNameSpaces)
    {
        this.classNameSpaces = classNameSpaces;
    }

    public String getMethodName()
    {
        return methodName;
    }

    public void setMethodName(String methodName)
    {
        this.methodName = methodName;
    }

    public String getMethodNameSpace()
    {
        return methodNameSpace;
    }

    public void setMethodNameSpace(String methodNameSpace)
    {
        this.methodNameSpace = methodNameSpace;
    }

    public String getFullClassName()
    {
        return fullClassName;
    }

    public void setFullClassName(String fullClassName)
    {
        this.fullClassName = fullClassName;
    }

    public String getGetterSetter()
    {
        return getterSetter;
    }

    public void setGetterSetter(String getterSetter)
    {
        this.getterSetter = getterSetter;
    }
}
