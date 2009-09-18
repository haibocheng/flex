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
package org.apache.batik.gvt;

import java.util.LinkedList;
import java.util.List;

import org.apache.batik.gvt.event.GraphicsNodeChangeListener;

/**
 * The top-level graphics node of the GVT tree.
 *
 * @author <a href="mailto:Thierry.Kormann@sophia.inria.fr">Thierry Kormann</a>
 * @version $Id: RootGraphicsNode.java,v 1.9 2004/08/18 07:14:27 vhardy Exp $
 */
public class RootGraphicsNode extends CompositeGraphicsNode {

    List treeGraphicsNodeChangeListeners = null;

   /**
     * Constructs a new empty <tt>RootGraphicsNode</tt>.
     */
    public RootGraphicsNode() {}

    /**
     * Returns the root of the GVT tree or null if the node is not
     * part of a GVT tree.  
     */
    public RootGraphicsNode getRoot() {
        return this;
    }

    public List getTreeGraphicsNodeChangeListeners() {
        if (treeGraphicsNodeChangeListeners == null) {
            treeGraphicsNodeChangeListeners = new LinkedList();
        }
        return treeGraphicsNodeChangeListeners;
    }

    public void addTreeGraphicsNodeChangeListener
        (GraphicsNodeChangeListener l) {
        getTreeGraphicsNodeChangeListeners().add(l);
    }

    public void removeTreeGraphicsNodeChangeListener
        (GraphicsNodeChangeListener l) {
        getTreeGraphicsNodeChangeListeners().remove(l);
    }

}
