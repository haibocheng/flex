package flex2.compiler.extensions;

import java.util.List;

/**
 * @author Andrew Westberg
 */
public interface IConfigurableExtension
{
    void configure( List<String> parameters );
}
