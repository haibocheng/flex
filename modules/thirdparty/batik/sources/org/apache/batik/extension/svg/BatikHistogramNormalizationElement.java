/*

   Copyright 2001,2003  The Apache Software Foundation 

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
package org.apache.batik.extension.svg;

import org.apache.batik.dom.AbstractDocument;
import org.apache.batik.extension.PrefixableStylableExtensionElement;
import org.w3c.dom.Node;


/**
 * This class implements a histogram normalization extension to SVG.
 *
 * @author <a href="mailto:thomas.deweese@kodak.com">Thomas DeWeese</a>
 * @version $Id: BatikHistogramNormalizationElement.java,v 1.4 2004/08/18 07:14:21 vhardy Exp $
 */
public class BatikHistogramNormalizationElement
    extends    PrefixableStylableExtensionElement 
    implements BatikExtConstants {

    /**
     * Creates a new BatikHistogramNormalizationElement object.
     */
    protected BatikHistogramNormalizationElement() {
    }

    /**
     * Creates a new BatikHistogramNormalizationElement object.
     * @param prefix The namespace prefix.
     * @param owner The owner document.
     */
    public BatikHistogramNormalizationElement(String prefix, AbstractDocument owner) {
        super(prefix, owner);
    }

    /**
     * <b>DOM</b>: Implements {@link org.w3c.dom.Node#getLocalName()}.
     */
    public String getLocalName() {
        return BATIK_EXT_HISTOGRAM_NORMALIZATION_TAG;
    }

    /**
     * <b>DOM</b>: Implements {@link org.w3c.dom.Node#getNamespaceURI()}.
     */
    public String getNamespaceURI() {
        return BATIK_EXT_NAMESPACE_URI;
    }

    /**
     * Returns a new uninitialized instance of this object's class.
     */
    protected Node newNode() {
        return new BatikHistogramNormalizationElement();
    }
}
