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

package flex2.compiler.fxg;

import java.util.ArrayList;
import java.util.List;

import flash.swf.tags.DefineTag;

/**
 * Used to map a SWF symbol generated for a particular FXG node to
 * an ActionScript class. This association links a tag primitive to more
 * complex assets, such as the ActionScript implementation of a TextGraphic
 * node (which does not have a tag primitive equivalent).
 */
public class FXGSymbolClass
{
    private static final String DEFAULT_PACKAGE = "";
    private static final char PACKAGE_SEPARATOR = '.';

    private String packageName;
    private String className;
    private String generatedSource;
    private DefineTag symbol;

    private List<FXGSymbolClass> additionalSymbolClasses;

    /**
     * An FXG node may have child nodes that also require a symbol class
     * mapping that will be included along with the parent symbol for the
     * compilation unit.
     * 
     * @param spriteClass - an additional symbol class
     */
    public void addAdditionalSymbolClass(FXGSymbolClass symbolClass)
    {
        if (additionalSymbolClasses == null)
            additionalSymbolClasses = new ArrayList<FXGSymbolClass>();

        additionalSymbolClasses.add(symbolClass);
    }

    /**
     * @return the list of additional symbol classes to be included with this
     * symbol class
     */
    public List<FXGSymbolClass> getAdditionalSymbolClasses()
    {
        return additionalSymbolClasses;
    }

    /**
     * @return the qualified class name of the generated ActionScript class
     */
    public String getQualifiedClassName()
    {
        if (packageName != null && packageName != DEFAULT_PACKAGE)
            return packageName + PACKAGE_SEPARATOR + className;

        return className;
    }

    /**
     * @return the package name of the generated ActionScript class
     */
    public String getPackageName()
    {
        if (packageName == null)
            return DEFAULT_PACKAGE;

        return packageName;
    }

    /**
     * @param packageName - the package name of the generated ActionScript class
     */
    public void setPackageName(String value)
    {
        packageName = value;
    }

    /**
     * @return the class name of the generated ActionScript class
     */
    public String getClassName()
    {
        return className;
    }

    /**
     * @param value - the class name of the generated ActionScript class
     */
    public void setClassName(String value)
    {
        className = value;
    }

    /**
     * @return - the source code of the generated ActionScript class
     */
    public String getGeneratedSource()
    {
        return generatedSource;
    }

    /**
     * @param value - the source code of the generated ActionScript class
     */
    public void setGeneratedSource(String value)
    {
        this.generatedSource = value;
    }

    /**
     * @return the SWF symbol
     */
    public DefineTag getSymbol()
    {
        return symbol;
    }

    /**
     * @param value - the SWF symbol
     */
    public void setSymbol(DefineTag value)
    {
        this.symbol = value;
    }

}
