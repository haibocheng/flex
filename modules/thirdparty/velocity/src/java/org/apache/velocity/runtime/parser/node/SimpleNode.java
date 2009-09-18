////////////////////////////////////////////////////////////////////////////////
//
//  ADOBE SYSTEMS INCORPORATED
//  Copyright 2005-2008 Adobe Systems Incorporated
//  All Rights Reserved.
//
//  NOTICE: Adobe permits you to use, modify, and distribute this file
//  in accordance with the terms of the license agreement accompanying it.
//
////////////////////////////////////////////////////////////////////////////////

/*
 * Copyright 2000-2001,2004 The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.velocity.runtime.parser.node;

import java.io.Writer;
import java.io.IOException;

import org.apache.velocity.context.InternalContextAdapter;
import org.apache.velocity.runtime.RuntimeServices;
import org.apache.velocity.runtime.exception.ReferenceException;
import org.apache.velocity.runtime.parser.Parser;
import org.apache.velocity.runtime.parser.Token;

import org.apache.velocity.exception.MethodInvocationException;
import org.apache.velocity.exception.ParseErrorException;
import org.apache.velocity.exception.ResourceNotFoundException;


public class SimpleNode implements Node
{
    protected transient RuntimeServices rsvc = null;

    protected Node parent;
    protected Node[] children;
    protected int id;
    protected transient Parser parser;
    
    protected int info; // added
    public boolean state;
    protected boolean invalid = false;
    
    /* Added */
    // java serialization chokes on long linked lists so these are
    // transient and node are responsible for getting everything they
    // need from the token's at parse time
    protected transient Token first, last;
    String firstTokenImage;
    int beginLine;
    int beginColumn;

    public SimpleNode(int i)
    {
        id = i;
    }

    public SimpleNode(Parser p, int i)
    {
        this(i);
        parser = p;
    }

    public void jjtOpen()
    {
    	// FIXME: shouldn't hold on to token's like this
        first = parser.getToken(1); // added
        firstTokenImage = first.image;
        beginLine = first.beginLine;
        beginColumn = first.beginColumn;        
    }

    public void jjtClose()
    {
        last = parser.getToken(0); // added
    }

    public String getFirstTokenImage()
    {
        return firstTokenImage;
    }
    
    public Token getFirstToken()
    {
        return first;
    }
    
    public Token getLastToken()
    {
        return last;
    }

    public void jjtSetParent(Node n)
    {
        parent = n;
    }
    public Node jjtGetParent()
    {
        return parent;
    }

    public void jjtAddChild(Node n, int i)
    {
        if (children == null)
        {
            children = new Node[i + 1];
        }
        else if (i >= children.length)
        {
            Node c[] = new Node[i + 1];
            System.arraycopy(children, 0, c, 0, children.length);
            children = c;
        }
        children[i] = n;
    }

    public Node jjtGetChild(int i)
    {
        return children[i];
    }

    public int jjtGetNumChildren()
    {
        return (children == null) ? 0 : children.length;
    }

    /** Accept the visitor. **/
    public Object jjtAccept(ParserVisitor visitor, Object data)
    {
        return visitor.visit(this, data);
    }

    /** Accept the visitor. **/
    public Object childrenAccept(ParserVisitor visitor, Object data)
    {
        if (children != null)
        {
            for (int i = 0; i < children.length; ++i)
            {
                children[i].jjtAccept(visitor, data);
            }
        }
        return data;
    }

    /* You can override these two methods in subclasses of SimpleNode to
        customize the way the node appears when the tree is dumped.  If
        your output uses more than one line you should override
        toString(String), otherwise overriding toString() is probably all
        you need to do. */

    //    public String toString()
    // {
    //    return ParserTreeConstants.jjtNodeName[id];
    // }
    public String toString(String prefix)
    {
        return prefix + toString();
    }

    /* Override this method if you want to customize how the node dumps
        out its children. */

    public void dump(String prefix)
    {
        System.out.println(toString(prefix));
        if (children != null)
        {
            for (int i = 0; i < children.length; ++i)
            {
                SimpleNode n = (SimpleNode) children[i];
                if (n != null)
                {
                    n.dump(prefix + " ");
                }
            }
        }
    }

    // All additional methods

    public String literal()
    {
        Token t = first;
        StringBuffer sb = new StringBuffer(t.image);
        
        while (t != last)
        {
            t = t.next;
            sb.append(t.image);
        }
        
        return sb.toString();
    }

    public Object init( InternalContextAdapter context, Object data) throws Exception
    {
        /*
         * hold onto the RuntimeServices
         */

        rsvc = (RuntimeServices) data;

        int i, k = jjtGetNumChildren();

        for (i = 0; i < k; i++)
        {
            try
            {
                jjtGetChild(i).init( context, data);
            }
            catch (ReferenceException re)
            {
                rsvc.error(re);
            }
        }            
    
        return data;
    }

    public boolean evaluate( InternalContextAdapter  context)
        throws MethodInvocationException
    {
        return false;
    }        

    public Object value( InternalContextAdapter context)
        throws MethodInvocationException
    {
        return null;
    }        

    public boolean render( InternalContextAdapter context, Writer writer)
        throws IOException, MethodInvocationException, ParseErrorException, ResourceNotFoundException
    {
        int i, k = jjtGetNumChildren();

        for (i = 0; i < k; i++)
            jjtGetChild(i).render(context, writer);
    
        return true;
    }

    public Object execute(Object o, InternalContextAdapter context)
      throws MethodInvocationException
    {
        return null;
    }

    public int getType()
    {
        return id;
    }

    public void setInfo(int info)
    {
        this.info = info;
    }
    
    public int getInfo()
    {
        return info;
    }        

    public void setInvalid()
    {
        invalid = true;
    }        

    public boolean isInvalid()
    {
        return invalid;
    }        

    public int getLine()
    {
        return beginLine;
    }
    
    public int getColumn()
    {
        return beginColumn;
    }        
}

