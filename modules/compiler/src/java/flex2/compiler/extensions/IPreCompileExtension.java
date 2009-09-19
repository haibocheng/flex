package flex2.compiler.extensions;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import flex2.compiler.CompilerSwcContext;
import flex2.compiler.FileSpec;
import flex2.compiler.PreLink;
import flex2.compiler.ResourceBundlePath;
import flex2.compiler.ResourceContainer;
import flex2.compiler.Source;
import flex2.compiler.SourceList;
import flex2.compiler.SourcePath;
import flex2.compiler.SymbolTable;
import flex2.compiler.common.Configuration;

/**
 * @author Andrew Westberg
 */
public interface IPreCompileExtension
{
    public void run( FileSpec fileSpec, SourceList sourceList, Collection<Source> classes, SourcePath sourcePath,
                     ResourceContainer resources, ResourceBundlePath bundlePath, CompilerSwcContext swcContext,
                     SymbolTable symbolTable, Configuration configuration, flex2.compiler.SubCompiler[] compilers,
                     PreLink preLink, Map licenseMap, List<Source> sources );
}
