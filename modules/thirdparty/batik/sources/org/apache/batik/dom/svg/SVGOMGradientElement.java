/*

   Copyright 2000-2004  The Apache Software Foundation 

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
import org.apache.batik.dom.util.XLinkSupport;
import org.apache.batik.dom.util.XMLSupport;
import org.w3c.dom.Node;
import org.w3c.dom.svg.SVGAnimatedBoolean;
import org.w3c.dom.svg.SVGAnimatedEnumeration;
import org.w3c.dom.svg.SVGAnimatedString;
import org.w3c.dom.svg.SVGAnimatedTransformList;
import org.w3c.dom.svg.SVGGradientElement;

/**
 * This class implements {@link org.w3c.dom.svg.SVGGradientElement}.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id: SVGOMGradientElement.java,v 1.9 2004/08/18 07:13:17 vhardy Exp $
 */
public abstract class SVGOMGradientElement
    extends    SVGStylableElement
    implements SVGGradientElement {

    /**
     * The attribute initializer.
     */
    protected final static AttributeInitializer attributeInitializer;
    static {
        attributeInitializer = new AttributeInitializer(4);
        attributeInitializer.addAttribute(XMLSupport.XMLNS_NAMESPACE_URI,
                                          null, "xmlns:xlink",
                                          XLinkSupport.XLINK_NAMESPACE_URI);
        attributeInitializer.addAttribute(XLinkSupport.XLINK_NAMESPACE_URI,
                                          "xlink", "type", "simple");
        attributeInitializer.addAttribute(XLinkSupport.XLINK_NAMESPACE_URI,
                                          "xlink", "show", "other");
        attributeInitializer.addAttribute(XLinkSupport.XLINK_NAMESPACE_URI,
                                          "xlink", "actuate", "onLoad");
    }

    /**
     * The units values.
     */
    protected final static String[] UNITS_VALUES = {
        "",
        SVG_USER_SPACE_ON_USE_VALUE,
        SVG_OBJECT_BOUNDING_BOX_VALUE
    };

    /**
     * The 'spreadMethod' attribute values.
     */
    protected final static String[] SPREAD_METHOD_VALUES = {
        "",
        SVG_PAD_VALUE,
        SVG_REFLECT_VALUE,
        SVG_REPEAT_VALUE
    };

    /**
     * Creates a new SVGOMGradientElement object.
     */
    protected SVGOMGradientElement() {
    }

    /**
     * Creates a new SVGOMGradientElement object.
     * @param prefix The namespace prefix.
     * @param owner The owner document.
     */
    protected SVGOMGradientElement(String prefix, AbstractDocument owner) {
        super(prefix, owner);
    }

    /**
     * To implement {@link
     * org.w3c.dom.svg.SVGGradientElement#getGradientTransform()}.
     */
    public SVGAnimatedTransformList getGradientTransform() {
	throw new RuntimeException(" !!! TODO: getGradientTransform()");
    }

    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.svg.SVGGradientElement#getGradientUnits()}.
     */
    public SVGAnimatedEnumeration getGradientUnits() {
        return getAnimatedEnumerationAttribute
            (null, SVG_GRADIENT_UNITS_ATTRIBUTE, UNITS_VALUES,
             (short)2);
    }
 
    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.svg.SVGGradientElement#getSpreadMethod()}.
     */
    public SVGAnimatedEnumeration getSpreadMethod() {
        return getAnimatedEnumerationAttribute
            (null, SVG_SPREAD_METHOD_ATTRIBUTE, SPREAD_METHOD_VALUES,
             (short)1);
    }
 
    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.svg.SVGURIReference#getHref()}.
     */
    public SVGAnimatedString getHref() {
        return SVGURIReferenceSupport.getHref(this);
    }

    // SVGExternalResourcesRequired support /////////////////////////////

    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.svg.SVGExternalResourcesRequired#getExternalResourcesRequired()}.
     */
    public SVGAnimatedBoolean getExternalResourcesRequired() {
	return SVGExternalResourcesRequiredSupport.
            getExternalResourcesRequired(this);
    }

    /**
     * Returns the AttributeInitializer for this element type.
     * @return null if this element has no attribute with a default value.
     */
    protected AttributeInitializer getAttributeInitializer() {
        return attributeInitializer;
    }

    /**
     * Returns a new uninitialized instance of this object's class.
     */
    protected Node newNode() {
        return new SVGOMAElement();
    }
}
