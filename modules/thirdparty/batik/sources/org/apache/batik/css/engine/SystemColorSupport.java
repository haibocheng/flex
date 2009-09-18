/*

   Copyright 1999-2003  The Apache Software Foundation 

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

import java.awt.SystemColor;
import java.util.HashMap;
import java.util.Map;

import org.apache.batik.css.engine.value.FloatValue;
import org.apache.batik.css.engine.value.RGBColorValue;
import org.apache.batik.css.engine.value.Value;
import org.apache.batik.util.CSSConstants;
import org.w3c.dom.css.CSSPrimitiveValue;

/**
 * This class provides support for AWT system colors.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id: SystemColorSupport.java,v 1.4 2004/10/30 18:38:04 deweese Exp $
 */
public class SystemColorSupport implements CSSConstants {
    
    /**
     * Returns the Value corresponding to the given system color.
     */
    public static Value getSystemColor(String ident) {
        ident = ident.toLowerCase();
        SystemColor sc = (SystemColor)factories.get(ident);
        return new RGBColorValue
            (new FloatValue(CSSPrimitiveValue.CSS_NUMBER, sc.getRed()),
             new FloatValue(CSSPrimitiveValue.CSS_NUMBER, sc.getGreen()),
             new FloatValue(CSSPrimitiveValue.CSS_NUMBER, sc.getBlue()));
    }

    /**
     * The color factories.
     */
    protected final static Map factories = new HashMap();
    static {
        factories.put(CSS_ACTIVEBORDER_VALUE,
                      SystemColor.windowBorder);
        factories.put(CSS_ACTIVECAPTION_VALUE,
                      SystemColor.activeCaption);
        factories.put(CSS_APPWORKSPACE_VALUE,
                      SystemColor.desktop);
        factories.put(CSS_BACKGROUND_VALUE,
                      SystemColor.desktop);
        factories.put(CSS_BUTTONFACE_VALUE,
                      SystemColor.control);
        factories.put(CSS_BUTTONHIGHLIGHT_VALUE,
                      SystemColor.controlLtHighlight);
        factories.put(CSS_BUTTONSHADOW_VALUE,
                      SystemColor.controlDkShadow);
        factories.put(CSS_BUTTONTEXT_VALUE,
                      SystemColor.controlText);
        factories.put(CSS_CAPTIONTEXT_VALUE,
                      SystemColor.activeCaptionText);
        factories.put(CSS_GRAYTEXT_VALUE,
                      SystemColor.textInactiveText);
        factories.put(CSS_HIGHLIGHT_VALUE,
                      SystemColor.textHighlight);
        factories.put(CSS_HIGHLIGHTTEXT_VALUE,
                      SystemColor.textHighlightText);
        factories.put(CSS_INACTIVEBORDER_VALUE,
                      SystemColor.windowBorder);
        factories.put(CSS_INACTIVECAPTION_VALUE,
                      SystemColor.inactiveCaption);
        factories.put(CSS_INACTIVECAPTIONTEXT_VALUE,
                      SystemColor.inactiveCaptionText);
        factories.put(CSS_INFOBACKGROUND_VALUE,
                      SystemColor.info);
        factories.put(CSS_INFOTEXT_VALUE,
                      SystemColor.infoText);
        factories.put(CSS_MENU_VALUE,
                      SystemColor.menu);
        factories.put(CSS_MENUTEXT_VALUE,
                      SystemColor.menuText);
        factories.put(CSS_SCROLLBAR_VALUE,
                      SystemColor.scrollbar);
        factories.put(CSS_THREEDDARKSHADOW_VALUE,
                      SystemColor.controlDkShadow);
        factories.put(CSS_THREEDFACE_VALUE,
                      SystemColor.control);
        factories.put(CSS_THREEDHIGHLIGHT_VALUE,
                      SystemColor.controlHighlight);
        factories.put(CSS_THREEDLIGHTSHADOW_VALUE,
                      SystemColor.controlLtHighlight);
        factories.put(CSS_THREEDSHADOW_VALUE,
                      SystemColor.controlShadow);
        factories.put(CSS_WINDOW_VALUE,
                      SystemColor.window);
        factories.put(CSS_WINDOWFRAME_VALUE,
                      SystemColor.windowBorder);
        factories.put(CSS_WINDOWTEXT_VALUE,
                      SystemColor.windowText);
    }

    /**
     * This class does not need to be instantiated.
     */
    protected SystemColorSupport() {
    }
}
