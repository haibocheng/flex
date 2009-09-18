/*

   Copyright 2000-2003  The Apache Software Foundation 

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
package org.apache.batik.dom.svg;

import org.apache.batik.dom.AbstractDocument;
import org.w3c.dom.Node;
import org.w3c.dom.svg.SVGAnimatedNumber;
import org.w3c.dom.svg.SVGAnimatedString;
import org.w3c.dom.svg.SVGFESpecularLightingElement;

/**
 * This class implements {@link SVGFESpecularLightingElement}.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id: SVGOMFESpecularLightingElement.java,v 1.7 2004/08/18 07:13:16 vhardy Exp $
 */
public class SVGOMFESpecularLightingElement
    extends    SVGOMFilterPrimitiveStandardAttributes
    implements SVGFESpecularLightingElement {

    /**
     * Creates a new SVGOMFESpecularLightingElement object.
     */
    protected SVGOMFESpecularLightingElement() {
    }

    /**
     * Creates a new SVGOMFESpecularLightingElement object.
     * @param prefix The namespace prefix.
     * @param owner The owner document.
     */
    public SVGOMFESpecularLightingElement(String prefix,
                                          AbstractDocument owner) {
        super(prefix, owner);
    }

    /**
     * <b>DOM</b>: Implements {@link Node#getLocalName()}.
     */
    public String getLocalName() {
        return SVG_FE_SPECULAR_LIGHTING_TAG;
    }

    /**
     * <b>DOM</b>: Implements {@link SVGFESpecularLightingElement#getIn1()}.
     */
    public SVGAnimatedString getIn1() {
        return getAnimatedStringAttribute(null, SVG_IN_ATTRIBUTE);
    }

    /**
     * <b>DOM</b>: Implements {@link
     * SVGFESpecularLightingElement#getSurfaceScale()}.
     */
    public SVGAnimatedNumber getSurfaceScale() {
        return getAnimatedNumberAttribute(null,
                                          SVG_SURFACE_SCALE_ATTRIBUTE,
                                          1f);
    }

    /**
     * <b>DOM</b>: Implements {@link
     * SVGFESpecularLightingElement#getSpecularConstant()}.
     */
    public SVGAnimatedNumber getSpecularConstant() {
        return getAnimatedNumberAttribute(null,
                                          SVG_SPECULAR_CONSTANT_ATTRIBUTE,
                                          1f);
    }

    /**
     * <b>DOM</b>: Implements {@link
     * SVGFESpecularLightingElement#getSpecularExponent()}.
     */
    public SVGAnimatedNumber getSpecularExponent() {
        return getAnimatedNumberAttribute(null,
                                          SVG_SPECULAR_EXPONENT_ATTRIBUTE,
                                          1f);
    }

    /**
     * Returns a new uninitialized instance of this object's class.
     */
    protected Node newNode() {
        return new SVGOMFESpecularLightingElement();
    }
}
