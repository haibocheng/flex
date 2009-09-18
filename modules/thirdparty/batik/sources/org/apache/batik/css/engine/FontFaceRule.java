/*

   Copyright 2003  The Apache Software Foundation 

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
package org.apache.batik.css.engine;

import org.apache.batik.util.ParsedURL;

/**
 * This class represents a @font-face CSS rule.
 *
 * This mostly exists to give us a place to store the 
 * URI to be used for 'src' URI resolution.
 *
 * @author <a href="mailto:deweese@apache.org">l449433</a>
 * @version $Id: FontFaceRule.java,v 1.5 2005/03/27 08:58:31 cam Exp $
 */
public class FontFaceRule implements Rule {
    /**
     * The type constant.
     */
    public final static short TYPE = (short)3;

    StyleMap sm;
    ParsedURL purl;
    public FontFaceRule(StyleMap sm, ParsedURL purl) {
        this.sm = sm;
        this.purl = purl;
    }
    
    /**
     * Returns a constant identifying the rule type.
     */
    public short getType() { return TYPE; }

    /**
     * Returns the URI of the @font-face rule.
     */
    public ParsedURL getURL() {
        return purl;
    }

    /**
     * Returns the StyleMap from the @font-face rule.
     */
    public StyleMap getStyleMap() {
        return sm;
    }

    /**
     * Returns a printable representation of this rule.
     */
    public String toString(CSSEngine eng) {
        StringBuffer sb = new StringBuffer();
        sb.append("@font-face { ");
        sb.append(sm.toString(eng));
        sb.append(" }\n");
        return sb.toString();
    }
}
