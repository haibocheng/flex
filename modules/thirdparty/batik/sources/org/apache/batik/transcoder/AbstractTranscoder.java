/*

   Copyright 2001  The Apache Software Foundation 

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
package org.apache.batik.transcoder;

/**
 * This class can be the base class of a transcoder which may support
 * transcoding hints and/or error handler.
 *
 * @author <a href="mailto:Thierry.Kormann@sophia.inria.fr">Thierry Kormann</a>
 * @version $Id: AbstractTranscoder.java,v 1.5 2004/08/18 07:15:41 vhardy Exp $
 */
public abstract class AbstractTranscoder extends TranscoderSupport
    implements Transcoder {

    /**
     * Constructs a new <tt>AbstractTranscoder</tt>.
     */
    protected AbstractTranscoder() {}
}
