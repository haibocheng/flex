package flex2.compiler.extensions;

import flex2.tools.oem.Configuration;
import flex2.tools.oem.Library;

/**
 * @author Andrew Westberg
 */
public interface ILibraryExtension
    extends IExtension
{
    void run(Library library, Configuration config );
}
