/*

   Copyright 2000-2001,2003  The Apache Software Foundation 

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

import org.w3c.dom.Attr;
import org.w3c.dom.DOMException;
import org.w3c.dom.Element;
import org.w3c.dom.svg.SVGAnimatedNumberList;
import org.w3c.dom.svg.SVGException;
import org.w3c.dom.svg.SVGNumberList;

/**
 * This class is the implementation of
 * the SVGAnimatedNumberList interface.
 *
 * @author  tonny@kiyut.com
 */
public class SVGOMAnimatedNumberList 
    implements SVGAnimatedNumberList,
               LiveAttributeValue {
    
    /**
     * The associated element.
     */
    protected AbstractElement element;

    /**
     * The attribute's namespace URI.
     */
    protected String namespaceURI;

    /**
     * The attribute's local name.
     */
    protected String localName;

    /**
     * Whether the list is changing.
     */
    protected boolean changing;

    /**
     * SVGNumberList
     */
    protected AbstractSVGNumberList numbers;

    /**
     * Default value for the 'points' attribute.
     */
    protected String defaultValue;
    
    /**
     */
    public SVGOMAnimatedNumberList(AbstractElement elt,
                                   String ns,
                                   String ln,
                                   String defaultValue) {
        
        element = elt;
        namespaceURI = ns;
        localName = ln;
        this.defaultValue = defaultValue;
    }
    
    /**
     * return the SVGNumberList mapping
     * the static attribute
     * of the element
     *
     * @return a number list.
     */
    public SVGNumberList getBaseVal(){
        if ( numbers == null ){
            numbers = new SVGOMNumberList();
        }
         return numbers;
    }

    /**
     */
    public SVGNumberList getAnimVal(){
        throw new RuntimeException("TODO :  getAnimVal() !!");
    }
    
    /**
     * Called when an Attr node has been added.
     */
    public void attrAdded(Attr node, String newv) {
        if (!changing && numbers != null) {
            numbers.invalidate();
        }
    }

    /**
     * Called when an Attr node has been modified.
     */
    public void attrModified(Attr node, String oldv, String newv) {
        if (!changing && numbers != null) {
            numbers.invalidate();
        }
    }

    /**
     * Called when an Attr node has been removed.
     */
    public void attrRemoved(Attr node, String oldv) {
        if (!changing && numbers != null) {
            numbers.invalidate();
        }
    }
    
    /**
     * SVGNumberList implementation.
     */
    public class SVGOMNumberList extends AbstractSVGNumberList {

        public SVGOMNumberList(){
            super();
        }

        /**
         * Create a DOMException.
         */
        protected DOMException createDOMException(short    type,
                                                  String   key,
                                                  Object[] args){
            return element.createDOMException(type,key,args);
        }

        /**
         * Create a SVGException.
         */
        protected SVGException createSVGException(short    type,
                                                  String   key,
                                                  Object[] args){

            return ((SVGOMElement)element).createSVGException(type,key,args);
        }

        /**
         */
        protected Element getElement(){
            return element;
        }

        /**
         * Retrieve the value of the attribute 'points'.
         */
        protected String getValueAsString(){
            Attr attr = element.getAttributeNodeNS(namespaceURI, localName);
            if (attr == null) {
                return defaultValue;
            }
            return attr.getValue();
        }

        /**
         * Set the value of the attribute 'points'
         */
        protected void setAttributeValue(String value){
            try{
                changing = true;
                element.setAttributeNS(namespaceURI, localName, value);
            }
            finally{
                changing = false;
            }
        }
    }
}
