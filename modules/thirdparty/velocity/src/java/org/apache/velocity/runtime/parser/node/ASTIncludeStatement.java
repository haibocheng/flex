/* Generated By:JJTree: Do not edit this line. ASTIncludeStatement.java */

package org.apache.velocity.runtime.parser.node;

import org.apache.velocity.runtime.parser.*;

public class ASTIncludeStatement extends SimpleNode
{
    public ASTIncludeStatement(int id)
    {
        super(id);
    }

    public ASTIncludeStatement(Parser p, int id)
    {
        super(p, id);
    }

    /** Accept the visitor. **/
    public Object jjtAccept(ParserVisitor visitor, Object data)
    {
        return visitor.visit(this, data);
    }
}
