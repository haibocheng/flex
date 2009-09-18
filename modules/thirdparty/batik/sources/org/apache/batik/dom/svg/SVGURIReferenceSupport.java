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

import org.apache.batik.dom.util.XLinkSupport;
import org.apache.batik.util.SVGConstants;
import org.w3c.dom.Element;
import org.w3c.dom.svg.SVGAnimatedString;

/**
 * This class provides support for the SVGURIReference interface methods.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id: SVGURIReferenceSupport.java,v 1.8 2004/08/18 07:13:19 vhardy Exp $
 */
public class SVGURIReferenceSupport implements SVGConstants {

    /**
     * To implement {@link org.w3c.dom.svg.SVGURIReference#getHref()}.
     */
    public static SVGAnimatedString getHref(Element elt) {
        return ((SVGOMElement)elt).
            getAnimatedStringAttribute(XLinkSupport.XLINK_NAMESPACE_URI,
                                       "href");
    }
}
