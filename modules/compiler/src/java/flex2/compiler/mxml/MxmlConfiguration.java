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

package flex2.compiler.mxml;

import flex.messaging.config.ServicesDependencies;
import flex2.compiler.common.FontsConfiguration;
import flex2.compiler.io.VirtualFile;

import java.util.List;
import java.util.Set;

/**
 * This interface is used to restrict consumers of
 * CompilerConfiguration to mxml compiler specific options.
 *
 * @author Clement Wong
 */
public interface MxmlConfiguration
{
	/**
	 * Generate SWFs for debugging.
	 */
	boolean debug();

	/**
	 * Generate accessible SWFs.
	 */
	boolean accessible();

	/**
	 * Write xxx-generated.as to disk.
	 */
	boolean keepGeneratedActionScript();

	/**
	 * Enable runtime DesignLayer support.
	 */
	boolean enableRuntimeDesignLayers();
	
	/**
	 * Enable swc version filtering (filer out swcs with 
	 * minimumSupportedVersion > compatibility-version)
	 */
	boolean enableSwcVersionFiltering();
	
    /**
     * Controls whether unused type selectors are processed.
     */
    boolean keepAllTypeSelectors();

    String getGeneratedDirectory();

    /**
     * Controls whether invalid styles are report as errors or
     * warnings.
     */
    boolean reportInvalidStylesAsWarnings();

	/**
	 * Controls whether warnings are displayed when a deprecated API is used.
	 */
	boolean showDeprecationWarnings();

    /**
     * Controls whether warnings are displayed when a deprecated API is used.
     */
    boolean showBindingWarnings();

    /**
     * Controls whether warnings are displayed when styles, which
     * don't apply to the current theme(s), are used in CSS.
     */
    boolean showInvalidCssPropertyWarnings();

    /**
     * Controls whether warnings are displayed when an embedded font name
     * shadows a device font name.
     */
    boolean showShadowedDeviceFontWarnings();

    /**
     * Toggles whether warnings generated from unused type selectors are displayed.
     */
    boolean showUnusedTypeSelectorWarnings();
    
    /**
     * Context path used to resolve {context.root} tokens in flex-enterprise-services.xml config file
     */
    String getContextRoot();

    /**
     * Compiler-specific representation of flex-enterprise-services.xml config file
     */
    ServicesDependencies getServicesDependencies();

	/**
	 * The compatibility version as a String.
	 */
	String getCompatibilityVersionString();

	/**
	 * The compatibility version as a uint.
	 */
	int getCompatibilityVersion();

	/**
	 * Determines whether CSS type selectors need to be qualified.
	 */
	boolean getQualifiedTypeSelectors();

	/**
	 * Location of defaults.css stylesheet.
	 */
	VirtualFile getDefaultsCssUrl();

	/**
	 * Location of SWC defaults.css stylesheets.
	 */
	List<VirtualFile> getDefaultsCssFiles();

	/**
	 * Location of theme stylesheets.
	 */
	List<VirtualFile> getThemeCssFiles();

	/**
	 * Path locations of source files.
	 */
	VirtualFile[] getSourcePath();

	/**
	 * Path locations of component libraries, including swcs, mxml, and as components.
	 * Note: all SWCs found in the lib-path are merged together and resolved via priority and version.
	 * The order is ignored.
	 */
	VirtualFile[] getLibraryPath();

	/**
	 * True if we are compiling against airglobal.swc.
	 */
	boolean getCompilingForAIR();

	/**
	 * Provides settings for the Font Managers, their caches, and aliases for character
	 * ranges to embed for font face.
	 */
	FontsConfiguration getFontsConfiguration();
    void setFontsConfiguration(FontsConfiguration fc);

	boolean getGenerateAbstractSyntaxTree();

    Set<String> getThemeNames();
	
    /**
     * Allow a style manager to have the same style declarations as
     * their parent.
     */
	boolean getAllowDuplicateDefaultStyleDeclarations();
}
