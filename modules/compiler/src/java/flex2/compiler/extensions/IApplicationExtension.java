package flex2.compiler.extensions;

import flex2.tools.oem.Configuration;

/**
 * @author Andrew Westberg
 */
public interface IApplicationExtension
    extends IExtension
{
    void run( Configuration config );
}
