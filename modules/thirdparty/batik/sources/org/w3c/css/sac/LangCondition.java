/*
 * (c) COPYRIGHT 1999 World Wide Web Consortium
 * (Massachusetts Institute of Technology, Institut National de Recherche
 *  en Informatique et en Automatique, Keio University).
 * All Rights Reserved. http://www.w3.org/Consortium/Legal/
 *
 * $Id: LangCondition.java,v 1.2 2000/11/10 17:14:20 hillion Exp $
 */
package org.w3c.css.sac;

/**
 * @version $Revision: 1.2 $
 * @author  Philippe Le Hegaret
 * @see Condition#SAC_LANG_CONDITION
 */
public interface LangCondition extends Condition {
    /**
     * Returns the language
     */
    public String getLang();
}
