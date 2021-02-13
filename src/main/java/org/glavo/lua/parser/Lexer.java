package org.glavo.lua.parser;

import com.oracle.truffle.api.source.Source;
import org.glavo.lua.runtime.LuaString;

import java.util.Arrays;

public final class Lexer {
    private final Source source;
    private final CharSequence sourceString;
    private int offset = 0;

    private @Token long nextToken = Token.Undefined;
    private int nextOffset = Integer.MAX_VALUE;

    public Lexer(Source source) {
        assert source != null;
        this.source = source;
        this.sourceString = source.getCharacters();
    }

    static int scanLongBracketLevel(CharSequence str, int offset) {
        final int length = str.length();
        if (offset >= length) {
            return -1;
        }
        int level = 0;
        while (offset < length) {
            char ch = str.charAt(offset++);
            if (ch == '=') {
                ++level;
            } else if (ch == '[') {
                return level;
            } else {
                return -1;
            }
        }
        return -1;
    }

    static int scanLongStringEnd(CharSequence str, int offset, int level) {
        final int length = str.length();
        int limit = length - level - 2;
        loop:
        while (offset <= limit) {
            if (str.charAt(offset) == ']') {
                for (int i = 1; i <= level; i++) {
                    if (str.charAt(offset + i) != '=') {
                        offset += i;
                        continue loop;
                    }
                }
                if (str.charAt(offset + level + 1) == ']') {
                    return offset;
                } else {
                    offset += level;
                }
            } else {
                offset += 1;
            }
        }
        return -1;
    }

    static boolean isNewLine(char ch) {
        return ch == '\n' || ch == '\r';
    }

    final void skipWhiteSpacesAndComment() {
        final CharSequence sourceString = this.sourceString;
        final int length = sourceString.length();

        int offset = this.offset;

        while (offset < length) {
            char ch0 = sourceString.charAt(offset);

            if (isNewLine(ch0) || ch0 == '\t' || ch0 == '\f' || ch0 == ' ') {
                offset += 1;
                continue;
            }

            if (ch0 == '-'
                    && offset + 1 < length
                    && sourceString.charAt(offset + 1) == '-') {
                offset += 2;
                if (offset < length && sourceString.charAt(offset) == '[') {
                    int level = scanLongBracketLevel(sourceString, offset + 1);
                    if (level >= 0) {
                        int end = scanLongStringEnd(sourceString, offset + level + 2, level);
                        if (end >= 0) {
                            offset = end + level + 2;
                            continue;
                        }
                    }
                }
                continue;
            }

            break;
        }

        this.offset = offset;
    }

    public final @Token long nextToken() {
        if (nextToken == Token.EOF) {
            return Token.EOF;
        }

        if (nextToken != Token.Undefined) {
            @Token long token = this.nextToken;
            this.offset = this.nextOffset;

            this.nextToken = Token.Undefined;

            return token;
        }

        skipWhiteSpacesAndComment();

        throw new UnsupportedOperationException(); // TODO
    }

    public final @Token long lookAhead() {
        if (nextToken != Token.Undefined) {
            return nextToken;
        }

        int currentOffset = this.offset;
        long token = nextToken();

        this.nextToken = token;
        this.nextOffset = this.offset;
        this.offset = currentOffset;

        return token;
    }

    public final int getOffset() {
        return offset;
    }

    private static final int DEFAULT_CAPACITY = 16;

    private int[] cacheOffsets = new int[DEFAULT_CAPACITY];
    private Object[] cacheValues = new Object[DEFAULT_CAPACITY];
    private int cacheCount = 0;

    public final void putCache(int offset, Object value) {
        int[] cacheOffsets = this.cacheOffsets;
        Object[] cacheValues = this.cacheValues;
        int cacheCount = this.cacheCount;
        int arrayLength = cacheOffsets.length;

        assert cacheCount < 1 || cacheOffsets[cacheCount - 1] < offset;

        if (cacheCount == arrayLength) {
            int newCapacity = Math.max(arrayLength + 1, arrayLength + (arrayLength >> 1));

            cacheOffsets = new int[newCapacity];
            cacheValues = new Object[newCapacity];

            System.arraycopy(cacheOffsets, 0, cacheOffsets, 0, arrayLength);
            System.arraycopy(cacheValues, 0, cacheValues, 0, arrayLength);

            this.cacheOffsets = cacheOffsets;
            this.cacheValues = cacheValues;
        }
        cacheOffsets[cacheCount] = offset;
        cacheValues[cacheCount] = value;
        ++this.cacheCount;
    }

    public final Object getCache(int offset) {
        final int[] cacheOffsets = this.cacheOffsets;
        final Object[] cacheValues = this.cacheValues;
        final int cacheCount = this.cacheCount;

        if (offset < 0 || cacheCount == 0) {
            return null;
        }

        int idx = Arrays.binarySearch(cacheOffsets, 0, cacheCount, offset);
        return idx < 0 ? null : cacheValues[idx];
    }
}
