////////////////////////////////////////////////////////////////////////////////
//
//  ADOBE SYSTEMS INCORPORATED
//  Copyright 2006-2008 Adobe Systems Incorporated
//  All Rights Reserved.
//
//  NOTICE: Adobe permits you to use, modify, and distribute this file
//  in accordance with the terms of the license agreement accompanying it.
//
////////////////////////////////////////////////////////////////////////////////
package flex.webtier.server.j2ee.jsp;

import flash.swf.Movie;
import flash.util.FileUtils;
import flex.webtier.server.j2ee.SWFInfo;
import flex.webtier.services.ServiceFactory;
import flex.webtier.services.config.ServerConfiguration;
import flex2.compiler.swc.SwcCache;
import flex2.compiler.common.CompilerConfiguration;
import flex2.compiler.io.VirtualFile;
import flex.webtier.server.j2ee.BaseCompileFilter;
import flex.webtier.server.j2ee.MxmlContext;
import flex2.tools.Target;
import java.util.HashMap;
import java.util.Map;
import java.io.ByteArrayOutputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;

public class JspCompileFilter extends BaseCompileFilter
{
    // SwcCache keeps SWCs around between compiles.  This allows SWCs (like mx.swc) to load quickly when
    // it has already been loaded and wasn't modified
    private SwcCache swcCache = new SwcCache();

    public JspCompileFilter(String swfExt)
    {
        super(swfExt);
    }

    protected SWFInfo compileMxml(SWFInfo swfInfo, String filename, MxmlContext context, HashMap dependencies) throws Exception
    {
        SourceCache sourceCache = SourceCache.getInstance(context.getServletContext());
        SourceCacheEntry sourceEntry = sourceCache.getEntry(context.getDependencyKey());

        assert(sourceEntry != null) : "jsp source code entries should always be in the source cache";

        if (sourceEntry == null)
        {
            throw new Exception("MXML source not available.");
        }

        VirtualFile appPathVirtual = new JspTextFile(sourceEntry.sourceCode, context.getAppPath(), sourceEntry.realPath + " (compiled)", context.getParentDir(),"text/mxml");

        // create request-specific configuration
        ServerConfiguration configuration = ServiceFactory.getConfigurator().generateConfiguration(context.getRequest(), null, dependencies);
        CompilerConfiguration compilerConfig = configuration.getFlexConfigConfiguration().getCompilerConfiguration();

        if (compilerConfig.keepGeneratedActionScript())
        {
            File gendir = new File( FileUtils.addPathComponents(context.getParentDir(), "generated", File.separatorChar));
            gendir.mkdirs();
            compilerConfig.setGeneratedDirectory(gendir.getCanonicalPath());
        }

        Map licenseMap = ServiceFactory.getLicenseService().getLicenseMap();
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
                keepGenerated(bos, new File(context.getParentDir()), new File(context.getAppPath()));
            }
        }

        return swfInfo;
    }
}
