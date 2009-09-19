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

import java.io.File;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;

/**
 * This class replaces the call to avmplus (asDochelper.exe) This would read the
 * toplevel.xml and pass the contents to classes responsible for generating
 * dita based xml files.
 * 
 * @author gauravj
 */
public class AsDocHelper
{
    private String topLevelXmlPath = "toplevel.xml";
    private String ditaOutputDir = "tempdita";
    private String outputDir = "";
    private String asDocConfigPath = "ASDoc_Config.xml";

    /**
     * @param topLevelXmlPath path to toplevel.xml
     * @param outputDir output location for xml files
     * @param asDocConfigPath location of ASDoc_Config.xml
     */
    public AsDocHelper(String topLevelXmlPath, String ditaOutputDir,
            String outputDir, String asDocConfigPath)
    {
        this.topLevelXmlPath = topLevelXmlPath;
        this.ditaOutputDir = ditaOutputDir;
        this.outputDir = outputDir;
        this.asDocConfigPath = asDocConfigPath;
    }

    /**
     * Create xml files for each package using toplevel.xml and ASDoc_Config.xml
     * 
     * @param lenient
     * @throws Exception
     */
    public void createTopLevelClasses(boolean lenient) throws Exception
    {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder parser = factory.newDocumentBuilder();

        // get all the asdoc config options.
        Document asDocConfig = parser.parse(new File(asDocConfigPath));

        // read in the toplevel.xml
        Document domObject = parser.parse(new File(topLevelXmlPath));

        TopLevelClassesGenerator topLevelClassesGenerator = new TopLevelClassesGenerator(asDocConfig, domObject);
        topLevelClassesGenerator.initialize();
        topLevelClassesGenerator.generate();

        topLevelClassesGenerator.writeOutputFiles(ditaOutputDir, outputDir, lenient);
    }
}
