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
import org.w3c.dom.svg.SVGAnimatedString;
import org.w3c.dom.svg.SVGFEComponentTransferElement;

/**
 * This class implements {@link SVGFEComponentTransferElement}.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id: SVGOMFEComponentTransferElement.java,v 1.8 2004/08/18 07:13:15 vhardy Exp $
 */
public class SVGOMFEComponentTransferElement
    extends    SVGOMFilterPrimitiveStandardAttributes
    implements SVGFEComponentTransferElement {

    /**
     * Creates a new SVGOMFEComponentTransferElement object.
     */
    protected SVGOMFEComponentTransferElement() {
    }

    /**
     * Creates a new SVGOMFEComponentTransferElement object.
     * @param prefix The namespace prefix.
     * @param owner The owner document.
     */
    public SVGOMFEComponentTransferElement(String prefix,
                                           AbstractDocument owner) {
        super(prefix, owner);
    }

    /**
     * <b>DOM</b>: Implements {@link org.w3c.dom.Node#getLocalName()}.
     */
    public String getLocalName() {
        return SVG_FE_COMPONENT_TRANSFER_TAG;
    }

    /**
     * <b>DOM</b>: Implements {@link SVGFEComponentTransferElement#getIn1()}.
     */
    public SVGAnimatedString getIn1() {
        return getAnimatedStringAttribute(null, SVG_IN_ATTRIBUTE);
    }

    /**
     * Returns a new uninitialized instance of this object's class.
     */
    protected Node newNode() {
        return new SVGOMFEComponentTransferElement();
    }
}
