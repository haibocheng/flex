/* 
 * This Xerces 2.4.0 class was modified for use by the Flex SDK at
 * Macromedia. The changes are labeled with the comment "modified by rsun"
 * and the date the modifications were made.
 */

/*
 * The Apache Software License, Version 1.1
 *
 *
 * Copyright (c) 2000-2002 The Apache Software Foundation.  All rights 
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer. 
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgment:  
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgment may appear in the software itself,
 *    if and wherever such third-party acknowledgments normally appear.
 *
 * 4. The names "Xerces" and "Apache Software Foundation" must
 *    not be used to endorse or promote products derived from this
 *    software without prior written permission. For written 
 *    permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache",
 *    nor may "Apache" appear in their name, without prior written
 *    permission of the Apache Software Foundation.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation and was
 * originally based on software copyright (c) 1999, International
 * Business Machines, Inc., http://www.apache.org.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 */

package org.apache.xerces.util;

import org.apache.xerces.xni.QName;
import org.apache.xerces.xni.XMLString;
import org.apache.xerces.xni.Augmentations;

/**
 * The XMLAttributesImpl class is an implementation of the XMLAttributes
 * interface which defines a collection of attributes for an element. 
 * In the parser, the document source would scan the entire start element 
 * and collect the attributes. The attributes are communicated to the
 * document handler in the startElement method.
 * <p>
 * The attributes are read-write so that subsequent stages in the document
 * pipeline can modify the values or change the attributes that are
 * propogated to the next stage.
 *
 * @see org.apache.xerces.xni.XMLDocumentHandler#startElement
 *
 * @author Andy Clark, IBM 
 * @author Elena Litani, IBM
 *
 * @version $Id: XMLAttributesImpl.java,v 1.19 2003/03/10 16:29:01 neilg Exp $
 */
public class XMLAttributesMMImpl
	extends XMLAttributesImpl {

    //
    // Constructors
    //

    /** Default constructor. */
    // modified by rsun 11/06/03 - changed AttributeImpl to AttributeMMImpl
    public XMLAttributesMMImpl() {
	    super();
	    fAttributes = new AttributeMMImpl[4];
	    
        for (int i = 0; i < fAttributes.length; i++) {
            fAttributes[i] = new AttributeMMImpl();
        }
    } // <init>()
    
    /**
     * Adds an attribute. The attribute's non-normalized value of the
     * attribute will have the same value as the attribute value until
     * set using the <code>setNonNormalizedValue</code> method. Also,
     * the added attribute will be marked as specified in the XML instance
     * document unless set otherwise using the <code>setSpecified</code>
     * method.
     * <p>
     * <strong>Note:</strong> If an attribute of the same name already
     * exists, the old values for the attribute are replaced by the new
     * values.
     * 
     * @param attrName  The attribute name.
     * @param attrType  The attribute type. The type name is determined by
     *                  the type specified for this attribute in the DTD.
     *                  For example: "CDATA", "ID", "NMTOKEN", etc. However,
     *                  attributes of type enumeration will have the type
     *                  value specified as the pipe ('|') separated list of
     *                  the enumeration values prefixed by an open 
     *                  parenthesis and suffixed by a close parenthesis.
     *                  For example: "(true|false)".
     * @param attrValue The attribute value.
     * 
     * @return Returns the attribute index.
     *
     * @see #setNonNormalizedValue
     * @see #setSpecified
     */
    public int addAttribute(QName name, String type, String value) {

        // find attribute; create, if necessary
        int index = name.uri != null && !name.uri.equals("")
                  ? getIndex(name.uri, name.localpart)
                  : getIndex(name.rawname);
        if (index == -1) {
            index = fLength;
            if (fLength++ == fAttributes.length) {
		// modified by rsun 11/06/03 - changed AttributeImpl to AttributeMMImpl
                Attribute[] attributes = new AttributeMMImpl[fAttributes.length + 4];
                Augmentations[] augs = new AugmentationsImpl[fAttributes.length +4];
                System.arraycopy(fAttributes, 0, attributes, 0, fAttributes.length);
                System.arraycopy(fAugmentations, 0, augs, 0, fAttributes.length);
                for (int i = fAttributes.length; i < attributes.length; i++) {
                    attributes[i] = new AttributeMMImpl();
                    augs[i] = new AugmentationsImpl();
                }
                fAttributes = attributes;
                fAugmentations = augs;
            }
        }

        // clear augmentations
        fAugmentations[index].removeAllItems();

        // set values
        Attribute attribute = fAttributes[index];
        attribute.name.setValues(name);
        attribute.type = type;
        attribute.value = value;
        attribute.nonNormalizedValue = value;
        attribute.specified = false;

        // return
        return index;

    } // addAttribute(QName,String,XMLString)


    //
    // Public methods
    //

    /**
     * modified by rsun 11/06/03 - added functions
     * Get/Set the line number on which this attribute
     * was found.
     */
    
    public void setLineNumber(int index, int lineno) {
	    if (index < 0 || index >= fLength) {
		    return;
	    }
	    if(!(fAttributes[index] instanceof AttributeMMImpl)) {
		    return;
	    }
	    
	    ((AttributeMMImpl)fAttributes[index]).lineno = lineno;
    }

    public int getLineNumber(int index) {
	    if (index < 0 || index >= fLength) {
		    return -1;
	    }
	    if(!(fAttributes[index] instanceof AttributeMMImpl)) {
		    return -1;
	    }
	    
		return ((AttributeMMImpl)fAttributes[index]).lineno;
    }

    //
    // Classes
    //

    /**
     * Attribute information.
     *
     * @author Andy Clark, IBM
     * modified by rsun 11/06/03 - subclassed original Attribute class,
     * adding lineno property.
     */
    static class AttributeMMImpl extends Attribute {
        
        //
        // Data
        //

	/** Line number. */
	public int lineno;
        
    } // class AttributeMMImpl

} // class XMLAttributesMMImpl
