////////////////////////////////////////////////////////////////////////////////
//
//  ADOBE SYSTEMS INCORPORATED
//  Copyright 2007-2008 Adobe Systems Incorporated
//  All Rights Reserved.
//
//  NOTICE: Adobe permits you to use, modify, and distribute this file
//  in accordance with the terms of the license agreement accompanying it.
//
////////////////////////////////////////////////////////////////////////////////

/*

   Copyright 2002  The Apache Software Foundation 

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
package org.apache.batik.css.parser;

import org.w3c.css.sac.DescendantSelector;
import org.w3c.css.sac.Selector;
import org.w3c.css.sac.SimpleSelector;

/**
 * This class provides an abstract implementation of the
 * {@link DescendantSelector} interface.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id: AbstractDescendantSelector.java,v 1.3 2004/08/18 07:13:02 vhardy Exp $
 */
public abstract class AbstractDescendantSelector extends AbstractSelector
    implements DescendantSelector {

    /**
     * The ancestor selector.
     */
    protected Selector ancestorSelector;

    /**
     * The simple selector.
     */
    protected SimpleSelector simpleSelector;

    /**
     * Creates a new DescendantSelector object.
     */
    protected AbstractDescendantSelector(Selector ancestor,
                                         SimpleSelector simple) {
	ancestorSelector = ancestor;
	simpleSelector = simple;
    }

    /**
     * <b>SAC</b>: Implements {@link DescendantSelector#getAncestorSelector()}.
     */    
    public Selector getAncestorSelector() {
	return ancestorSelector;
    }

    /**
     * <b>SAC</b>: Implements {@link DescendantSelector#getSimpleSelector()}.
     */    
    public SimpleSelector getSimpleSelector() {
	return simpleSelector;
    }
}
