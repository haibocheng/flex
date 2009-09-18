/*

   Copyright 2004 The Apache Software Foundation 

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

package org.apache.batik.css.engine.value.svg12;

import org.apache.batik.css.engine.CSSEngine;
import org.apache.batik.css.engine.value.LengthManager;
import org.apache.batik.css.engine.value.Value;
import org.apache.batik.css.engine.value.ValueManager;
import org.apache.batik.css.engine.value.svg.SVGValueConstants;
import org.w3c.css.sac.LexicalUnit;
import org.w3c.dom.DOMException;

/**
 * This class provides a factory for the 'margin-*' properties values.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id: MarginLengthManager.java,v 1.2 2005/03/27 08:58:31 cam Exp $
 */
public class MarginLengthManager extends LengthManager {

    protected String  prop;

    public MarginLengthManager(String prop) {
        this.prop = prop;
    }
    
    /**
     * Implements {@link ValueManager#isInheritedProperty()}.
     */
    public boolean isInheritedProperty() {
	return true;
    }

    /**
     * Implements {@link ValueManager#getPropertyName()}.
     */
    public String getPropertyName() {
	return prop;
    }
    
    /**
     * Implements {@link ValueManager#getDefaultValue()}.
     */
    public Value getDefaultValue() {
        return SVGValueConstants.NUMBER_0;
    }

    /**
     * Implements {@link ValueManager#createValue(LexicalUnit,CSSEngine)}.
     */
    public Value createValue(LexicalUnit lu, CSSEngine engine)
        throws DOMException {
        if (lu.getLexicalUnitType() == LexicalUnit.SAC_INHERIT) {
            return SVGValueConstants.INHERIT_VALUE;
        }
        return super.createValue(lu, engine);
    }


    /**
     * Indicates the orientation of the property associated with
     * this manager.
     */
    protected int getOrientation() {
        // Margins are always wrt to block width, event for top/bottom.
        return HORIZONTAL_ORIENTATION;
    }
}
