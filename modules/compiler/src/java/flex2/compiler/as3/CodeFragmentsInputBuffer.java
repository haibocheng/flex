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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import macromedia.asc.parser.InputBuffer;

/**
 * This class extends InputBuffer by adding support for mapping a line
 * number in a code fragment back to the line number in the Mxml
 * document.
 *
 * @author Paul Reilly
 */
public class CodeFragmentsInputBuffer extends InputBuffer
{
    private List<CodeFragment> codeFragments = new ArrayList<CodeFragment>();
    // Initialized to 1, because the default position for generated
    // ASC nodes is 0 and without this, they were getting mapped to
    // line 1 by getLnNum().  If the default position is changed to
    // -1, then length can be initialized to 0.
    private int length = 1;
    private Map<Integer, Integer> positionToLineNumberMap = new HashMap<Integer, Integer>();

    public CodeFragmentsInputBuffer(String path)
    {
        origin = path;
    }

    public void addCodeFragment(int fragmentLength, InputBuffer inputBuffer, int lineNumberOffset)
    {
        int startPosition = length;
        int endPosition = length + fragmentLength;
        codeFragments.add(new CodeFragment(startPosition, endPosition,
                                           inputBuffer, lineNumberOffset - 1));
        length = endPosition;
    }

    /**
     * Columns are not reported for Mxml, so we short circuit by returning -1.
     */
    public int getColPos(int pos)
    {
        return -1;
    }

    /**
     * Columns are not reported for Mxml, so we short circuit by returning -1.
     */
    public int getColPos(int pos, int lineNumber)
    {
        return -1;
    }

    public int getLnNum(int pos)
    {
        int result = -1;
        Integer lineNumber = positionToLineNumberMap.get(pos);

        if (lineNumber != null)
        {
            result = lineNumber;
        }
        else
        {
            CodeFragment codeFragment = lookupCodeFragment(pos);

            if (codeFragment != null)
            {
                int codeFragmentLineNumber = codeFragment.inputBuffer.getLnNum(pos);
                result = codeFragmentLineNumber + codeFragment.lineNumberOffset;
            }
        }

        return result;
    }

    public int getLength()
    {
        return length;
    }

    public String getLineText(int pos)
    {
        String result = null;
        CodeFragment codeFragment = lookupCodeFragment(pos);

        if (codeFragment != null)
        {
            result = codeFragment.inputBuffer.getLineText(pos - codeFragment.startPosition);
        }

        return result;
    }

    private CodeFragment lookupCodeFragment(int pos)
    {
        CodeFragment result = null;

        if (pos != -1)
        {
            Iterator<CodeFragment> iterator = codeFragments.iterator();

            while (iterator.hasNext())
            {
                CodeFragment codeFragment = iterator.next();
            
                if ((codeFragment.startPosition <= pos) &&
                    (pos < codeFragment.endPosition))
                {
                    result = codeFragment;
                    break;
                }
            }
        }

        return result;
    }

    public int positionOfMark()
    {
        return 0;
    }

    /**
     * Adds a new line number to the positionToLineNumberMap at the
     * current position and increases the length by 1.  This is used
     * by AST generation to associate generated nodes with mxml line
     * numbers.  It's important when emitting debug info in the byte
     * code.
     */
    public void addLineNumber(int lineNumber)
    {
        positionToLineNumberMap.put(length++, lineNumber);
    }

    public static class CodeFragment
    {
        public int startPosition;
        public int endPosition;
        public InputBuffer inputBuffer;
        public int lineNumberOffset;

        public CodeFragment(int startPosition, int endPosition,
                            InputBuffer inputBuffer, int lineNumberOffset)
        {
            this.startPosition = startPosition;
            this.endPosition = endPosition;
            this.inputBuffer = inputBuffer;
            this.lineNumberOffset = lineNumberOffset;
        }
    }
}
