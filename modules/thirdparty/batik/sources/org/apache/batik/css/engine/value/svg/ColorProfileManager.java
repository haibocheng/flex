/*

   Copyright 2002-2003  The Apache Software Foundation 

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
package org.apache.batik.css.engine.value.svg;

import org.apache.batik.css.engine.CSSEngine;
import org.apache.batik.css.engine.value.AbstractValueManager;
import org.apache.batik.css.engine.value.StringValue;
import org.apache.batik.css.engine.value.URIValue;
import org.apache.batik.css.engine.value.Value;
import org.apache.batik.css.engine.value.ValueManager;
import org.apache.batik.util.CSSConstants;
import org.w3c.css.sac.LexicalUnit;
import org.w3c.dom.DOMException;
import org.w3c.dom.css.CSSPrimitiveValue;

/**
 * This class provides a manager for the 'color-interpolation' property values.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id: ColorProfileManager.java,v 1.6 2005/03/27 08:58:31 cam Exp $
 */
public class ColorProfileManager extends AbstractValueManager {
    
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
	return CSSConstants.CSS_COLOR_PROFILE_PROPERTY;
    }
    
    /**
     * Implements {@link ValueManager#getDefaultValue()}.
     */
    public Value getDefaultValue() {
        return SVGValueConstants.AUTO_VALUE;
    }

    /**
     * Implements {@link ValueManager#createValue(LexicalUnit,CSSEngine)}.
     */
    public Value createValue(LexicalUnit lu, CSSEngine engine)
        throws DOMException {
	switch (lu.getLexicalUnitType()) {
	case LexicalUnit.SAC_INHERIT:
	    return SVGValueConstants.INHERIT_VALUE;

	case LexicalUnit.SAC_IDENT:
            String s = lu.getStringValue().toLowerCase();
            if (s.equals(CSSConstants.CSS_AUTO_VALUE)) {
                return SVGValueConstants.AUTO_VALUE;
            }
            if (s.equals(CSSConstants.CSS_SRGB_VALUE)) {
                return SVGValueConstants.SRGB_VALUE;
            }
            return new StringValue(CSSPrimitiveValue.CSS_IDENT, s);
            
        case LexicalUnit.SAC_URI:
            return new URIValue(lu.getStringValue(),
                                resolveURI(engine.getCSSBaseURI(),
                                           lu.getStringValue()));
        }
        throw createInvalidLexicalUnitDOMException(lu.getLexicalUnitType());
    }

    /**
     * Implements {@link
     * ValueManager#createStringValue(short,String,CSSEngine)}.
     */
    public Value createStringValue(short type, String value, CSSEngine engine)
	throws DOMException {
        switch (type) {
        case CSSPrimitiveValue.CSS_IDENT:
            String s = value.toLowerCase();
            if (s.equals(CSSConstants.CSS_AUTO_VALUE)) {
                return SVGValueConstants.AUTO_VALUE;
            }
            if (s.equals(CSSConstants.CSS_SRGB_VALUE)) {
                return SVGValueConstants.SRGB_VALUE;
            }
            return new StringValue(CSSPrimitiveValue.CSS_IDENT, s);

        case CSSPrimitiveValue.CSS_URI:
            return new URIValue(value,
                                resolveURI(engine.getCSSBaseURI(), value));
        }
        throw createInvalidStringTypeDOMException(type);
    }
}
