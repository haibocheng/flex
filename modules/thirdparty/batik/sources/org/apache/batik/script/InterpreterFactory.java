/*

   Copyright 2000-2002  The Apache Software Foundation 

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.

 */
package org.apache.batik.script;

import java.net.URL;

/**
 * An hight level interface that represents a factory allowing
 * to create instances of a particular <code>Interpreter</code> interface
 * implementation.
 * @author <a href="mailto:cjolif@ilog.fr">Christophe Jolif</a>
 * @version $Id: InterpreterFactory.java,v 1.6 2004/08/27 00:42:06 deweese Exp $
 */
public interface InterpreterFactory {

    /**
     * Returns the mime-type to register this interpereter with.
     */
    public String getMimeType();

    /**
     * This method should create an instance of <code>Interpreter</code>
     * interface implementation.
     * 
     * @param documentURL the url for the document which will be scripted
     */
    public Interpreter createInterpreter(URL documentURL);
}
