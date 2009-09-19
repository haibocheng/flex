package flex2.compiler.extensions;

import java.util.List;

import flex2.compiler.CompilationUnit;
import flex2.compiler.CompilerSwcContext;
import flex2.compiler.FileSpec;
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
public interface IPreLinkExtension
    extends IExtension
{
    void run( List<Source> sources, List<CompilationUnit> units, FileSpec fileSpec, SourceList sourceList,
              SourcePath sourcePath, ResourceBundlePath bundlePath, ResourceContainer resources,
              SymbolTable symbolTable, CompilerSwcContext swcContext, Configuration configuration );
}
