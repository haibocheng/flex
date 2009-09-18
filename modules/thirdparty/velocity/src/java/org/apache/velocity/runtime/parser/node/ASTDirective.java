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

package org.apache.velocity.runtime.parser.node;

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

import java.io.Writer;
import java.io.IOException;

import org.apache.velocity.context.InternalContextAdapter;
import org.apache.velocity.runtime.directive.Directive;
import org.apache.velocity.runtime.parser.Parser;

import org.apache.velocity.exception.MethodInvocationException;
import org.apache.velocity.exception.ParseErrorException;
import org.apache.velocity.exception.ResourceNotFoundException;

/**
 * This class is responsible for handling the pluggable
 * directives in VTL. ex.  #foreach()
 * 
 * Please look at the Parser.jjt file which is
 * what controls the generation of this class.
 *
 * @author <a href="mailto:jvanzyl@apache.org">Jason van Zyl</a>
 * @author <a href="mailto:geirm@optonline.net">Geir Magnusson Jr.</a>
 * @author <a href="mailto:kav@kav.dk">Kasper Nielsen</a>
 * @version $Id: ASTDirective.java,v 1.21.4.1 2004/03/03 23:22:58 geirm Exp $ 
 */
public class ASTDirective extends SimpleNode
{
    private transient Directive directive;
    private String directiveName = "";

    public ASTDirective(int id)
    {
        super(id);
    }

    public ASTDirective(Parser p, int id)
    {
        super(p, id);
    }

    /** Accept the visitor. **/
    public Object jjtAccept(ParserVisitor visitor, Object data)
    {
        return visitor.visit(this, data);
    }
    
    public void jjtClose()
    {
    	
    }
    
    public Object init( InternalContextAdapter context, Object data) 
        throws Exception
    {
        super.init( context, data );
        
        /*
         *  only do things that are not context dependant
         */

        if (rsvc.getDirectives().containsKey( directiveName ))
        {
            directive = (Directive) rsvc.getDirectives().get( directiveName ).getClass().newInstance();    
        }          
        else 
        {
            directive = (Directive) rsvc.getVelocimacro( directiveName,  context.getCurrentTemplateName() );
        }   

        if(directive != null) {
	        directive.init(rsvc, context,this);
	        directive.setLocation( getLine(), getColumn() );
        }
        // in case of recursive macro's
        super.init( context, data );
        return data;
    }

    public boolean render( InternalContextAdapter context, Writer writer)
        throws IOException,MethodInvocationException, ResourceNotFoundException, ParseErrorException
    {
        /*
         *  normal processing
         */

        if (directive != null)
        {           
            directive.render(context, writer, this);
        }
        else
        {
            writer.write( "#");
            writer.write( directiveName );
        }

        return true;
    }

    /**
     *   Sets the directive name.  Used by the parser.  This keeps us from having to 
     *   dig it out of the token stream and gives the parse the change to override.
     */
    public void setDirectiveName( String str )
    {
        directiveName = str;
        return;
    }

    /**
     *  Gets the name of this directive.
     */
    public String getDirectiveName()
    {
        return directiveName;
    }
}


