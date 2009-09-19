package flex2.compiler.mxml.reflect;

import flex2.compiler.util.CompilerMessage.CompilerError;

public class ElementTypeNotFound extends CompilerError
{
    private static final long serialVersionUID = 2373851015809929644L;
    public String metadata;
    public String elementTypeName;

    public ElementTypeNotFound(String metadata, String elementTypeName)
    {
        this.metadata = metadata;
        this.elementTypeName = elementTypeName;
    }
}