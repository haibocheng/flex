////////////////////////////////////////////////////////////////////////////////
//
//  ADOBE SYSTEMS INCORPORATED
//  Copyright 2008 Adobe Systems Incorporated
//  All Rights Reserved.
//
//  NOTICE: Adobe permits you to use, modify, and distribute this file
//  in accordance with the terms of the license agreement accompanying it.
//
////////////////////////////////////////////////////////////////////////////////

package flex2.compiler.as3;

import macromedia.asc.parser.InputBuffer;

/**
 * This class extends InputBuffer by offsetting the initial position
 * to reflect the relative position of a code fragment in an Mxml
 * document.
 *
 * @author Paul Reilly
 */
class OffsetInputBuffer extends InputBuffer
{   
    public OffsetInputBuffer(String in, String origin, int offset)
    {
        super(in, origin, offset, 0);
    }

}
