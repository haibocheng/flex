package flex2.compiler.mxml.dom;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import flex2.compiler.util.QName;

/**
 * @author Paul Reilly
 */
public class DeclarationsNode extends Node
{	
    public static final Set<QName> attributes;

    static
    {
        attributes = new HashSet<QName>();
    }
    
    DeclarationsNode(String uri, String localName, int size)
    {
        super(uri, localName, size);
    }

	public void analyze(Analyzer analyzer)
	{
		analyzer.prepare(this);
		analyzer.analyze(this);
	}
}
