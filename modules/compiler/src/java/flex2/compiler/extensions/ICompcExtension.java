package flex2.compiler.extensions;

import flex2.tools.CompcConfiguration;

/**
 * @author Andrew Westberg
 */
public interface ICompcExtension
    extends IExtension
{
    void run( CompcConfiguration configuration );
}
