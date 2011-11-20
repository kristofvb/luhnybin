package com.justme.luhny;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;

class Masker {

    private StringBuilder sb = new StringBuilder();
    private Reader in;
    private Writer out;

    /** Class holding start index and end index in the buffer for the sequence that needs marking. */
    private static class Mark {
        int start, end;

        Mark(int start, int end) {
            this.start = start;
            this.end = end;
        }
    }

    public Masker(Reader in, Writer out) {
        this.in = in;
        this.out = out;
    }

    /** The actual masking routine. */
    void mask() throws IOException {
        int c = in.read();
        while (c != -1) {
            c = bufferCardCharacters(c);
            outputBuffer(markAllLuhnSequences());
            c = writeNonCardCharacters(c);
        }
        out.flush();
    }

    /** Reads and outputs all characters that do not qualify as credit card characters. Returns the first character that
     * might appear in the next credit card number.
     */
    private int writeNonCardCharacters(int c) throws IOException {
        if (c != -1) do {
            out.write(c);
        } while ((c = in.read()) != -1 && !isCardChar(c));
        return c;
    }

    /** Reads and buffers all characters that qualify as credit card characters. Returns the first character that
     * no longer qualifies as a credit card character. */
    private int bufferCardCharacters(int c) throws IOException {
        if (isCardChar(c)) do {
            sb.append(Character.toChars(c));
            c = in.read();
        } while (isCardChar(c));
        return c;
    }

    /** Computes the largest sequence in the buffer that needs to be masked. Returns the [start, end[ range that needs
     * masking.
     */
    private Mark markAllLuhnSequences() {
        int candidateMark = 0;
        int markStart = -1;
        int markEnd = -1;
        do {
            for (int digitCount = 16; digitCount >= 14; --digitCount) {
                int leftMost = candidateMark;
                int rightMost = findRightMost(leftMost, digitCount);
                if (rightMost - leftMost >= 14 && passesLuhn(leftMost, rightMost)) {
                    if (markStart == -1) markStart = leftMost;
                    if (rightMost > markEnd) markEnd = rightMost;
                    break;
                }
            }
        } while (++candidateMark < sb.length());
        return new Mark(markStart, markEnd);
    }

    /** Finds the right-most index in the buffer so that [leftMost, rightMost[ is a sequence with up to
     * digitCount digits.
     */
    private int findRightMost(int leftMost, int digitCount) {
        int rightMost = leftMost;
        int digitsUsed = 0;
        while (rightMost < sb.length() && digitsUsed < digitCount) {
            if (Character.isDigit(sb.charAt(rightMost)))
                digitsUsed++;
            rightMost++;
        }
        return rightMost;
    }

    /** Outputs all buffered characters masking out all digits in the masked range. */
    private void outputBuffer(Mark m) throws IOException {
        for (int i = 0; i < sb.length(); i++) {
            char ch = sb.charAt(i);
            out.write(i >= m.start && i < m.end && Character.isDigit(ch) ? 'X' : ch);
        }
        sb.setLength(0);
    }

    private boolean isCardChar(int c) {
        return Character.isDigit(c) || Character.isSpaceChar(c) || c == Character.valueOf('-');
    }

    /** Checks if the buffered sequence [start, end[ passes the Luhn check. */
    private boolean passesLuhn(int start, int end) {
        int sum = 0;
        int pos = 0;
        for (int i = end - 1; i >= start; i--) {
            if (!Character.isDigit(sb.charAt(i)))
                continue;
            int charValue = sb.charAt(i) - '0';
            if ((pos % 2) == 0) {
                sum += charValue;
            } else {
                sum += computeCharSum(charValue * 2);
            }
            ++pos;
        }
        return (sum % 10) == 0;
    }

    private int computeCharSum(int i) {
        int sum = 0;
        for (char c : String.valueOf(i).toCharArray()) {
            sum += c - '0';
        }
        return sum;
    }
}
