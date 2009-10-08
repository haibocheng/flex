////////////////////////////////////////////////////////////////////////////////
//
//  ADOBE SYSTEMS INCORPORATED
//  Copyright 2005-2008 Adobe Systems Incorporated
//  All Rights Reserved.
//
//  NOTICE: Adobe permits you to use, modify, and distribute this file
//  in accordance with the terms of the license agreement accompanying it.
//
////////////////////////////////////////////////////////////////////////////////

package flex2.compiler.css;

import flex2.compiler.CompilationUnit;
import flex2.compiler.ResourceContainer;
import flex2.compiler.Source;
import flex2.compiler.SymbolTable;
import flex2.compiler.common.PathResolver;
import flex2.compiler.io.FileUtil;
import flex2.compiler.io.TextFile;
import flex2.compiler.io.VirtualFile;
import flex2.compiler.mxml.MxmlCompiler;
import flex2.compiler.mxml.MxmlConfiguration;
import flex2.compiler.mxml.SourceCodeBuffer;
import flex2.compiler.mxml.gen.VelocityUtil;
import flex2.compiler.mxml.lang.StandardDefs;
import flex2.compiler.mxml.reflect.Type;
import flex2.compiler.mxml.reflect.TypeTable;
import flex2.compiler.mxml.rep.AtEmbed;
import flex2.compiler.mxml.rep.MxmlDocument;
import flex2.compiler.swc.SwcFile;
import flex2.compiler.util.CompilerMessage.CompilerWarning;
import flex2.compiler.util.CompilerMessage;
import flex2.compiler.util.MimeMappings;
import flex2.compiler.util.NameFormatter;
import flex2.compiler.util.NameMappings;
import flex2.compiler.util.ThreadLocalToolkit;
import flex2.compiler.util.VelocityException;
import flex2.compiler.util.VelocityManager;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import flash.css.StyleDeclaration;
import flash.css.StyleProperty;
import flash.css.StyleSelector;
import flash.css.StyleSheet;
import flash.fonts.FontManager;
import macromedia.asc.util.ContextStatics;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;

/**
 * This class is an MXML document specific override of StyleModule. It provides
 * a context that manages style declarations for both default styles/themes
 * and document style nodes. 
 */
public class StylesContainer extends StyleModule
{
	private static final String TEMPLATE_PATH = "flex2/compiler/css/";
	private static final String ATEMBEDS_KEY = "atEmbeds";
    private static final String STYLEDEF_KEY = "styleDef";

    private static final String _FONTFACERULES = "_FontFaceRules";

    protected MxmlDocument mxmlDocument;
    protected MxmlConfiguration mxmlConfiguration;
    protected CompilationUnit compilationUnit;
    protected Set<String> localStyleTypeNames = new HashSet<String>();
    protected List<VirtualFile> implicitIncludes = new ArrayList<VirtualFile>();

    /**
     * Called by PreLink to load style declarations from defaults.css and
     * themes from SWCs.
     *
     * Also, called by MxmlDocument in preparation for local
     * StyleNodes.  DocumentBuilder.analyze(StyleNode) will call
     * extractStyles().
     * 
     * @param mxmlConfiguration
     * @param compilationUnit
     * @param perCompileData
     */
    public StylesContainer(MxmlConfiguration mxmlConfiguration,
                           CompilationUnit compilationUnit,
                           ContextStatics perCompileData)
    {
        super(compilationUnit.getSource(), perCompileData);
        this.mxmlConfiguration = mxmlConfiguration;
        this.compilationUnit = compilationUnit;

        if (mxmlConfiguration != null)
        {
            if (mxmlConfiguration.getCompatibilityVersion() <= flex2.compiler.common.MxmlConfiguration.VERSION_3_0)
            {
                setAdvanced(false);
                setQualifiedTypeSelectors(false);
            }
            else
            {
                setQualifiedTypeSelectors(mxmlConfiguration.getQualifiedTypeSelectors());
            }
        }
    }

    //--------------------------------------------------------------------------
    //
    // Properties - MXML
    //
    //--------------------------------------------------------------------------

    MxmlDocument getMxmlDocument()
    {
        return mxmlDocument;
    }

    public void setMxmlDocument(MxmlDocument doc)
    {
        mxmlDocument = doc;
    }

    //--------------------------------------------------------------------------
    //
    // Methods - Public Entry Points
    //
    //--------------------------------------------------------------------------

    /**
     * Generate style classes for components which we want to link in.
     * 
     * Called from PreLink.processMainUnit()
     */
    public List<Source> processDependencies(Set<String> defNames, ResourceContainer resources)
    {
        List<Source> extraSources = new ArrayList<Source>();

        if (!fontFaceRules.isEmpty())
        {
            // C: mixins in the generated FlexInit class are referred to by
            // "name". that's why extraClasses is necessary.
            compilationUnit.extraClasses.add(_FONTFACERULES);
            compilationUnit.mixins.add(_FONTFACERULES);

            extraSources.add(generateFontFaceRules(resources));
        }

        Set<String> processedDefNames = new HashSet<String>();
        Iterator<String> defNameIterator = defNames.iterator();
        while (defNameIterator.hasNext())
        {
            String defName = defNameIterator.next();
            if (qualifiedTypeSelectors)
                processedDefNames.add(NameFormatter.toDot(defName));
            else
                processedDefNames.add(defName.replaceFirst(".*:", ""));
        }

        Iterator<Entry<String, StyleDef>> iterator = styleDefs.entrySet().iterator();
        while (iterator.hasNext())
        {
            Entry<String, StyleDef> entry = iterator.next();
            String styleName = entry.getKey();
            StyleDef styleDef = entry.getValue();
            String typeName = StyleDef.dehyphenize(styleName);

            if (!styleDef.isTypeSelector() ||
                (processedDefNames.contains(typeName) ||
                 mxmlConfiguration.keepAllTypeSelectors()) ||
                styleName.equals(StyleDef.GLOBAL))
            {
                String className = "_" + StyleDef.createTypeName(typeName) + "Style";
                // C: mixins in the generated FlexInit class are referred to by
                // "name". that's why extraClasses is necessary.
                compilationUnit.extraClasses.add(className);
                compilationUnit.mixins.add(className);

                extraSources.add(generateStyleSource(styleDef, resources));
            }
        }

        return extraSources;
    }

    /**
     * Warn if we have a type selector outside of the root MXML (Application).
     */
    private boolean hasNonRootTypeSelectors(String subject, String selector, int lineNumber)
    {
        if (!compilationUnit.isRoot() && !StyleDef.UNIVERSAL.equals(subject))
        {
            // [preilly] This restriction should be removed once the
            // app model supports encapsulation of CSS styles.
            ComponentTypeSelectorsNotSupported componentTypeSelectorsNotSupported =
                new ComponentTypeSelectorsNotSupported(getSource().getName(),
                                                       lineNumber,
                                                       selector);
            ThreadLocalToolkit.log(componentTypeSelectorsNotSupported);
            return true;
        }

        return false;
    }

    /**
     * Check for simple type selectors that were not needed as the associated
     * component definition was not used in the Application.
     * 
     * Called from PreLink.processMainUnit()
     */
    public void validate(SymbolTable symbolTable, NameMappings nameMappings,
                         StandardDefs standardDefs, Set<String> themeNames)
    {
        Set<String> classNames;
        TypeTable typeTable = null;

        if (qualifiedTypeSelectors)
        {
            classNames = symbolTable.getClassNames();
            typeTable = (TypeTable) symbolTable.getContext().getAttribute(MxmlCompiler.TYPE_TABLE);

            if (typeTable == null)
            {
                typeTable = new TypeTable(symbolTable, nameMappings, standardDefs, themeNames);
            }
        }
        else
        {
            classNames = new HashSet<String>();

            for (String className : symbolTable.getClassNames())
            {
                if (qualifiedTypeSelectors)
                    classNames.add(NameFormatter.toDot(className));
                else
                    classNames.add(className.replaceFirst(".*:", ""));
            }
        }

        // Strip off the leading '[' and trailing ']'.
        String themeNamesString = themeNames.toString();
        themeNamesString = themeNamesString.substring(1, themeNamesString.length() - 1);

        for (Entry<String, StyleDef> entry : styleDefs.entrySet())
        {
            String styleName = entry.getKey();
            StyleDef styleDef = entry.getValue();
            String typeName = StyleDef.dehyphenize(styleName);

            if (styleDef.isTypeSelector())
            {
                if (qualifiedTypeSelectors && mxmlConfiguration.showInvalidCssPropertyWarnings())
                {
                    Type type = typeTable.getType(NameFormatter.toColon(typeName));

                    if (type != null)
                    {
                        Map<String, StyleDeclaration> declarations = styleDef.getDeclarations();

                        if (declarations != null)
                        {
                            for (StyleDeclaration styleDeclaration : declarations.values())
                            {
                                Map<String, StyleProperty> styleProperties = styleDeclaration.getProperties();
                                validateStyleProperties(styleProperties, type, styleDef,
                                                        typeName, themeNamesString);
                            }
                        }
                    }
                }

                if (localStyleTypeNames.contains(styleName) &&
                    !classNames.contains(NameFormatter.toColon(typeName)) &&
                    !styleName.equals(StyleDef.GLOBAL))
                {
                    if (mxmlConfiguration.showUnusedTypeSelectorWarnings())
                    {
                        ThreadLocalToolkit.log(new UnusedTypeSelector(getPathForReporting(styleDef),
                                                                      styleDef.getLineNumber(),
                                                                      styleName));
                    }
                }
            }
        }
    }

    private void validateStyleProperties(Map<String, StyleProperty> styleProperties, Type type,
                                         StyleDef styleDef, String typeName, String themeNamesString)
    {
        if (styleProperties != null)
        {
            for (StyleProperty styleProperty : styleProperties.values())
            {
                String stylePropertyName = styleProperty.getName();

                if (type.getStyle(stylePropertyName) == null)
                {
                    String styleThemes = type.getStyleThemes(stylePropertyName);

                    if (type.isExcludedStyle(stylePropertyName))
                    {
                        ThreadLocalToolkit.log(new ExcludedStyleProperty(getPathForReporting(styleDef),
                                                                         styleProperty.getLineNumber(),
                                                                         stylePropertyName,
                                                                         typeName));
                    }
                    else if (styleThemes != null)
                    {
                        ThreadLocalToolkit.log(new InvalidStyleTheme(getPathForReporting(styleDef),
                                                                     styleProperty.getLineNumber(),
                                                                     stylePropertyName,
                                                                     typeName,
                                                                     styleThemes));
                    }
                    else if (mxmlDocument != null)
                    {
                        ThreadLocalToolkit.log(new InvalidStyleProperty(getPathForReporting(styleDef),
                                                                        styleProperty.getLineNumber(),
                                                                        stylePropertyName,
                                                                        typeName,
                                                                        themeNamesString));
                    }
                }
            }
        }
    }

    //--------------------------------------------------------------------------
    //
    // Methods - MXML Overrides
    //
    //--------------------------------------------------------------------------

    @Override
    /**
     * This MXML Specific Override only allows type selectors to be declared
     * on the root document.
     */
    protected void addSelectorToStyleDef(String subject, StyleDeclaration declaration,
            boolean isTypeSelector, boolean isLocal, int lineNumber)
    {
        // Only allow type selectors on the root (Application). StyleManager is
        // a singleton so type selector overrides in arbitrary custom components
        // would be difficult to track down and not behave as expected.
        if (isTypeSelector && hasNonRootTypeSelectors(subject, subject, lineNumber))
            return;

        StyleDef styleDef;

        if (isTypeSelector && isLocal)
        {
            localStyleTypeNames.add(subject);
        }

        if (styleDefs.containsKey(subject))
        {
            styleDef = styleDefs.get(subject);
        }
        else
        {
            styleDef = new StyleDef(subject, isTypeSelector, mxmlDocument,
                    compilationUnit.getSource(), lineNumber, perCompileData);
            styleDefs.put(subject, styleDef);
        }

        styleDef.addDeclaration(declaration);

        if (mxmlDocument != null)
        {
            Iterator<Import> iterator = styleDef.getImports().iterator();
            while (iterator.hasNext())
            {
                Import importObject = iterator.next();
                mxmlDocument.addImport(importObject.getValue(), importObject.getLineNumber());
            }
        }
    }

    @Override
    /**
     * This MXML Specific Override only allows type selectors to be declared
     * on the root document.
     */
    protected void addAdvancedSelectorToStyleDef(String subject, StyleDeclaration declaration,
            StyleSelector selector, boolean isLocal, int lineNumber)
    {
        // Only allow type selectors on the root (Application). StyleManager is
        // a singleton so type selector overrides in arbitrary custom components
        // would be difficult to track down and not behave as expected.
        if (hasNonRootTypeSelectors(subject, selector.toString(), lineNumber))
            return;

        StyleDef styleDef;
        String styleDefKey = subject;

        // Treat a "*" subject like Flex's special "global" subject to follow
        // mxmlc's distinction of type selectors vs. universal selectors for
        // the purposes of code-generation.
        if (StyleDef.UNIVERSAL.equals(subject))
        {
            styleDefKey = StyleDef.GLOBAL;

            // If we have conditions, we can make "*" implied.
            if (selector.getConditions() != null && selector.getConditions().size() > 0)
            {
                selector.setValue("");
            }
        }

        if (styleDefs.containsKey(styleDefKey))
        {
            styleDef = styleDefs.get(styleDefKey);
        }
        else
        {
            if (isLocal && !StyleDef.GLOBAL.equals(styleDefKey))
                localStyleTypeNames.add(subject);

            styleDef = new StyleDef(subject, mxmlDocument, getSource(), lineNumber, perCompileData);
            styleDefs.put(styleDefKey, styleDef);
        }

        styleDef.addDeclaration(subject, selector, declaration);

        if (mxmlDocument != null)
        {
            Iterator<Import> iterator = styleDef.getImports().iterator();
            while (iterator.hasNext())
            {
                Import importObject = iterator.next();
                mxmlDocument.addImport(importObject.getValue(),
                        importObject.getLineNumber());
            }
        }
    }

    @Override
    protected void addAtEmbed(AtEmbed atEmbed)
    {
        if (mxmlDocument != null)
        {
            mxmlDocument.addAtEmbed(atEmbed);
        }
        else if (!atEmbeds.containsKey(atEmbed.getPropName()))
        {
            atEmbeds.put(atEmbed.getPropName(), atEmbed);
        }
    }

    //--------------------------------------------------------------------------
    //
    // Helper Methods - Font Face Rules 
    //
    //--------------------------------------------------------------------------

	private String generateFontFaceRuleSourceName()
	{
		String genFileName;
		String genDir = mxmlConfiguration.getGeneratedDirectory();
	    if (genDir != null)
	    {
		    genFileName = genDir + File.separatorChar + "_FontFaceRules.as";
	    }
	    else
	    {
		    genFileName = "_FontFaceRules.as";
	    }
		return genFileName;
	}

    private Source generateFontFaceRules(ResourceContainer resources)
    {
	    String genFileName = generateFontFaceRuleSourceName();
	    Source styleSource = resources.findSource(genFileName);
	    if (styleSource != null)
	    {
            if (styleSource.getCompilationUnit() == null) 
            {
                // if no compilationUnit, then we need to generate source so we can recompile.
                styleSource = null;
            }
            else 
            {
                // C: it is safe to return because this method deals with per-app styles, like defaults.css and themes.
                //    ResourceContainer will not have anything if any of the theme files is touched.
                return styleSource;
            }
	    }

	    StandardDefs standardDefs = ThreadLocalToolkit.getStandardDefs();
	    String fontFaceRulesTemplate = TEMPLATE_PATH + standardDefs.getFontFaceRulesTemplate();
		Template template;

        try
		{
            template = VelocityManager.getTemplate(fontFaceRulesTemplate);
        }
        catch (Exception exception)
        {
			ThreadLocalToolkit.log(new VelocityException.TemplateNotFound(fontFaceRulesTemplate));
			return null;
		}

		SourceCodeBuffer out = new SourceCodeBuffer();

		try
		{
			VelocityUtil util = new VelocityUtil(TEMPLATE_PATH, mxmlConfiguration.debug(), out, null);
			VelocityContext vc = VelocityManager.getCodeGenContext(util);
            vc.put(ATEMBEDS_KEY, atEmbeds);
			template.merge(vc, out);
		}
		catch (Exception e)
		{
			ThreadLocalToolkit.log(new VelocityException.GenerateException(compilationUnit.getSource().getRelativePath(),
                                                                           e.getLocalizedMessage()));
			return null;
		}

	    return resources.addResource(createSource(genFileName, out));
    }

    //--------------------------------------------------------------------------
    //
    // Methods - ActionScript Code Generation 
    //
    //--------------------------------------------------------------------------

    private Source createSource(String fileName, SourceCodeBuffer sourceCodeBuffer)
    {
        Source result = null;

        if (sourceCodeBuffer.getBuffer() != null)
        {
            String sourceCode = sourceCodeBuffer.toString();

            if (mxmlConfiguration.keepGeneratedActionScript())
            {
                try
                {
                    FileUtil.writeFile(fileName, sourceCode);
                }
                catch (IOException e)
                {
                    ThreadLocalToolkit.log(new VelocityException.UnableToWriteGeneratedFile(fileName, e.getMessage()));
                }
            }

            VirtualFile genFile = new TextFile(sourceCode, fileName, null, MimeMappings.AS, Long.MAX_VALUE);
            String shortName = fileName.substring(0, fileName.lastIndexOf('.'));

            result = new Source(genFile, "", shortName, null, false, false, false);
            result.setPathResolver(compilationUnit.getSource().getPathResolver());

            Iterator<VirtualFile> iterator = implicitIncludes.iterator();

            while ( iterator.hasNext() )
            {
                VirtualFile virtualFile = iterator.next();
                result.addFileInclude(virtualFile);
            }
        }

        return result;
    }

    private Source generateStyleSource(StyleDef styleDef, ResourceContainer resources)
    {
	    String genFileName = generateStyleSourceName(styleDef);
	    Source styleSource = resources.findSource(genFileName);
	    
	    if (styleSource != null)
	    {
	    	if (styleSource.getCompilationUnit() == null) 
	    	{
	    		// if no compilationUnit, then we need to generate source so we can recompile.
	    		styleSource = null;
	    	}
	    	else 
	    	{
	    		// C: it is safe to return because this method deals with per-app styles, like defaults.css and themes.
	    		//    ResourceContainer will not have anything if any of the theme files is touched.
	    		return styleSource;
	    	}
	    }

		//	load template

	    Template template;
        StandardDefs standardDefs = ThreadLocalToolkit.getStandardDefs();
        String styleDefTemplate = TEMPLATE_PATH + standardDefs.getStyleDefTemplate();

        try
		{
            template = VelocityManager.getTemplate(styleDefTemplate);
        }
        catch (Exception exception)
        {
			ThreadLocalToolkit.log(new VelocityException.TemplateNotFound(styleDefTemplate));
			return null;
		}

		SourceCodeBuffer out = new SourceCodeBuffer();

		try
		{
			VelocityUtil util = new VelocityUtil(TEMPLATE_PATH, mxmlConfiguration.debug(), out, null);
			VelocityContext vc = VelocityManager.getCodeGenContext(util);
			vc.put(STYLEDEF_KEY, styleDef);
			template.merge(vc, out);
		}
		catch (Exception e)
		{
			ThreadLocalToolkit.log(new VelocityException.GenerateException(compilationUnit.getSource().getRelativePath(),
                                                                           e.getLocalizedMessage()));
			return null;
		}

	    return resources.addResource(createSource(genFileName, out));
    }

    private String generateStyleSourceName(StyleDef styleDef)
    {
        String genFileName;
        String genDir = mxmlConfiguration.getGeneratedDirectory();
        String fileName = styleDef.getTypeName();
        if (StyleDef.UNIVERSAL.equals(fileName))
            fileName = StyleDef.GLOBAL;

        if (genDir != null)
        {
            genFileName = genDir + File.separatorChar + "_" + fileName + "Style.as";
        }
        else
        {
            genFileName = "_" + fileName + "Style.as";
        }

        return genFileName;
    }

    //--------------------------------------------------------------------------
    //
    // Initialization and defaults.css
    //
    //--------------------------------------------------------------------------

    public void loadDefaultStyles()
    {
        VirtualFile defaultsCSSFile = resolveDefaultsCssFile();

        // Load the per SWC default styles first
        for (Iterator<VirtualFile> it = mxmlConfiguration.getDefaultsCssFiles().iterator(); it.hasNext();)
        {
            VirtualFile swcDefaultsCssFile = it.next();

            // Make sure that we resolve things relative to the SWC.
            ThreadLocalToolkit.getPathResolver().addSinglePathResolver(0, swcDefaultsCssFile);
            processStyleSheet(swcDefaultsCssFile);
            ThreadLocalToolkit.getPathResolver().removeSinglePathResolver(swcDefaultsCssFile);
        }

        // Load the default styles next, so they can override the SWC defaults
        if (defaultsCSSFile != null)
        {
            // Only load the defaults if it's not a SwcFile.  If it's
            // a SwcFile, it should have already been loaded.
            if (!(defaultsCSSFile instanceof SwcFile))
            {
                processStyleSheet(defaultsCSSFile);
            }
        }
        else
        {
            ThreadLocalToolkit.log(new DefaultCSSFileNotFound());
        }

        // Load the theme styles next, so they can override the defaults
        for (Iterator<VirtualFile> it = mxmlConfiguration.getThemeCssFiles().iterator(); it.hasNext();)
        {
            VirtualFile themeCssFile = it.next();

            // Make sure that we resolve things in the theme relative
            // to the theme SWC first.
            ThreadLocalToolkit.getPathResolver().addSinglePathResolver(0, themeCssFile);
            processStyleSheet(themeCssFile);
            ThreadLocalToolkit.getPathResolver().removeSinglePathResolver(themeCssFile);
        }
    }

    private VirtualFile resolveDefaultsCssFile()
    {
        VirtualFile defaultsCSSFile = mxmlConfiguration.getDefaultsCssUrl();

        if (defaultsCSSFile == null)
        {
            PathResolver resolver = ThreadLocalToolkit.getPathResolver();

            String version = mxmlConfiguration.getCompatibilityVersionString();

            if (version != null)
            {
                defaultsCSSFile = resolver.resolve("defaults-" + version + ".css");
            }

            if (defaultsCSSFile == null)
            {
                defaultsCSSFile = resolver.resolve("defaults.css");
            }
        }

        return defaultsCSSFile;
    }

    private void processStyleSheet(VirtualFile cssFile)
    {
        implicitIncludes.add(cssFile);

        try
        {
            FontManager fontManager = mxmlConfiguration.getFontsConfiguration().getTopLevelManager();
            StyleSheet styleSheet = new StyleSheet();
            styleSheet.checkDeprecation(mxmlConfiguration.showDeprecationWarnings());
            styleSheet.parse(cssFile.getName(), cssFile.getInputStream(),
                             ThreadLocalToolkit.getLogger(), fontManager);
            
            extractStyles(styleSheet, false);
        }
        catch (Exception exception)
        {
            CompilerMessage m = new ParseError(exception.getLocalizedMessage());
            m.setPath(cssFile.getName());
            ThreadLocalToolkit.log(m);
        }
    }

    //--------------------------------------------------------------------------
    //
    // Errors and Warnings 
    //
    //--------------------------------------------------------------------------

    private String getPathForReporting(StyleDef styleDef)
    {
        if (styleDef.isAdvanced())
        {
            // Return the path of the first StyleDeclaration that refers to
            // the subject of this StyleDef
            Map<String, StyleDeclaration> declarations = styleDef.getDeclarations(); 
            for (StyleDeclaration decl : declarations.values())
            {
                if (decl != null && decl.getPath() != null)
                {
                    return decl.getPath();
                }
            }
        }

        return compilationUnit.getSource().getName();
    }
    
    public static class DefaultCSSFileNotFound extends CompilerWarning
    {
        private static final long serialVersionUID = -7274067342526310418L;

        public DefaultCSSFileNotFound()
        {
        }
    }

    public static class ExcludedStyleProperty extends CompilerWarning
    {
        private static final long serialVersionUID = -655374071288180325L;
        public String stylePropertyName;
        public String typeName;

        public ExcludedStyleProperty(String path, int line, String stylePropertyName,
                                     String typeName)
        {
            this.path = path;
            this.line = line;
            this.stylePropertyName = stylePropertyName;
            this.typeName = typeName;
        }
    }

    public static class InvalidStyleProperty extends CompilerWarning
    {
        private static final long serialVersionUID = -655374071288180326L;
        public String stylePropertyName;
        public String typeName;
        public String themeNames;

        public InvalidStyleProperty(String path, int line, String stylePropertyName,
                                    String typeName, String themeNames)
        {
            this.path = path;
            this.line = line;
            this.stylePropertyName = stylePropertyName;
            this.typeName = typeName;
            this.themeNames = themeNames;
        }
    }

    public class InvalidStyleTheme extends CompilerWarning
    {
        private static final long serialVersionUID = -655374071288180328L;

        public String stylePropertyName;
        public String typeName;
        public String styleThemes;

        public InvalidStyleTheme(String path, int line, String stylePropertyName,
                                 String typeName, String styleThemes)
        {
            this.path = path;
            this.line = line;
            this.stylePropertyName = stylePropertyName;
            this.typeName = typeName;
            this.styleThemes = styleThemes;
        }
    }

    public static class UnusedTypeSelector extends CompilerWarning
    {
        private static final long serialVersionUID = -655374071288180326L;
        public String styleName;

        public UnusedTypeSelector(String path, int line, String styleName)
        {
            this.path = path;
            this.line = line;
            this.styleName = styleName;
        }
    }

    public static class ComponentTypeSelectorsNotSupported extends CompilerWarning
    {
        private static final long serialVersionUID = -1211821282841071569L;
        public String selector;

        public ComponentTypeSelectorsNotSupported(String path, int line, String selector)
        {
            this.path = path;
            this.line = line;
            this.selector = selector;
        }
    }
}
