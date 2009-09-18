/*
 * Copyright 1999-2004 The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.batik.css.dom;

import org.apache.batik.test.svg.SelfContainedSVGOnLoadTest;

/**
 * Helper class to simplify writing the unitTesting.xml file for 
 * CSS DOM Tests.
 *
 * @author <a href="mailto:vhardy@apache.org">Vincent Hardy</a>
 * @version $Id: EcmaScriptCSSDOMTest.java,v 1.3 2005/04/01 02:28:16 deweese Exp $
 */

public class EcmaScriptCSSDOMTest extends SelfContainedSVGOnLoadTest {
    public void setId(String id){
        super.setId(id);
        svgURL = resolveURL("test-resources/org/apache/batik/css/dom/" + id + ".svg");
    }
}
