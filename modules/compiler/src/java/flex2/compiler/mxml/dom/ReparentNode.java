package flex2.compiler.mxml.dom;

import java.util.HashSet;
import java.util.Set;

import flex2.compiler.mxml.lang.StandardDefs;
import flex2.compiler.util.QName;

/**
 * @author Corey Lucier
 */
public class ReparentNode extends Node
{
    public static final Set<QName> attributes;

    static
    {
        attributes = new HashSet<QName>();
        attributes.add(new QName("", "target"));
        attributes.add(new QName("", StandardDefs.PROP_INCLUDE_STATES));
        attributes.add(new QName("", StandardDefs.PROP_EXCLUDE_STATES));
    }
    
    ReparentNode(String uri, String localName, int size)
    {
        super(uri, localName, size);
    }

    public void analyze(Analyzer analyzer)
    {
        analyzer.prepare(this);
        analyzer.analyze(this);
    }
}
