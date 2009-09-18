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
package flex.webtier.server.j2ee;

import flash.swf.Movie;
import flash.util.FileUtils;
import flex.webtier.services.ServiceFactory;
import flex.webtier.services.config.ServerConfiguration;
import flex2.compiler.common.CompilerConfiguration;
import flex2.compiler.io.FileUtil;
import flex2.compiler.io.LocalFile;
import flex2.compiler.io.VirtualFile;
import flex2.compiler.swc.SwcCache;
import flex2.tools.Target;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

public class CompileFilter extends BaseCompileFilter
{
    // SwcCache keeps SWCs around between compiles.  This allows SWCs (like mx.swc) to load quickly when
    // it has already been loaded and wasn't modified
    private SwcCache swcCache = new SwcCache();

    public CompileFilter(String swfExt)
    {
        super(swfExt);
    }

    protected SWFInfo compileMxml(SWFInfo swfInfo, String filename, MxmlContext context, HashMap dependencies) throws Exception
    {
        File appPath = FileUtil.openFile(filename);
        if (appPath != null)
        {
            appPath = appPath.getCanonicalFile();
        }
        VirtualFile appPathVirtual = new LocalFile(appPath);

        // create request-specific configuration
        ServerConfiguration configuration = ServiceFactory.getConfigurator().generateConfiguration(context.getRequest(), appPath, dependencies);

        CompilerConfiguration compilerConfig
                = configuration.getFlexConfigConfiguration().getCompilerConfiguration();

        if (compilerConfig.keepGeneratedActionScript())
        {
            File gendir = new File( FileUtils.addPathComponents( appPath.getParent(), "generated", File.separatorChar ) );
            gendir.mkdirs();
            compilerConfig.setGeneratedDirectory( gendir.getCanonicalPath() );
        }

        Map licenseMap = ServiceFactory.getLicenseService().getLicenseMap();
        
        // Save the license map with the swf in case it gets cached and 
        // incremental compile then gets enabled.
        swfInfo.setLicenseMap(licenseMap);
        
        Target target = flex2.tools.WebTierAPI.compile(appPathVirtual, configuration.getFlexConfigConfiguration(), swcCache, licenseMap);

        // link
        Movie movie = flex2.linker.LinkerAPI.link(target.units, new flex2.tools.PostLink(configuration.getFlexConfigConfiguration()),
                                            configuration.getFlexConfigConfiguration());

        getDependenciesFromCompilationUnits(target.units, dependencies);
        getDependenciesFromConfiguration(configuration, dependencies);

        setHtmlWrapperAttributes(movie, context);

        ByteArrayOutputStream bos = null;
        OutputStream swfOut = null;
        try
        {
            bos = new ByteArrayOutputStream();
            swfOut = new BufferedOutputStream(bos);
            // C: encode movie...
            flex2.compiler.CompilerAPI.encode(movie, swfOut);
            swfOut.flush();
        }
        catch (IOException ioe)
        {
            if (swfOut != null) { try { swfOut.close(); } catch (Exception e) {} }
        }

        if (bos != null)
        {
            swfInfo.setSwfBuffer(bos.toByteArray());

            if (configuration.getDebuggingConfiguration().keepGeneratedSwfs())
            {
                keepGenerated(bos, new File(appPath.getParent()), appPath);
            }
        }

        return swfInfo;
    }
}
