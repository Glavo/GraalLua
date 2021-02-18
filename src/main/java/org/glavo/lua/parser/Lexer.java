package org.glavo.lua.parser;

import com.oracle.truffle.api.source.Source;
import org.glavo.lua.runtime.LuaString;

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

    static boolean isDigit(char ch) {
        return ch >= '0' && ch <= '9';
    }

    static int parseDigit(char ch) {
        assert isDigit(ch);
        return ch - '0';
    }

    static boolean isHexDigit(char ch) {
        return (ch >= '0' && ch <= '9') || (ch >= 'a' && ch <= 'f') || (ch >= 'A' && ch <= 'F');
    }

    static int parseHexDigit(char ch) {
        assert isHexDigit(ch);
        if (ch >= '0' && ch <= '9') {
            return ch - '0';
        }
        if (ch >= 'a' && ch <= 'f') {
            return ch - 'a' + 10;
        }
        if (ch >= 'A' && ch <= 'F') {
            return ch - 'A' + 10;
        }
        throw new AssertionError();
    }

    static boolean isWhiteSpace(char ch) {
        switch (ch) {
            case '\t':
            case '\f':
            case '\n':
            case '\r':
            case ' ':
            case 11: // \v
                return true;
            default:
                return false;
        }
    }

    final void skipWhiteSpacesAndComment() {
        final CharSequence sourceString = this.sourceString;
        final int length = sourceString.length();

        int currentOffset = this.offset;

        while (currentOffset < length) {
            char ch0 = sourceString.charAt(currentOffset);

            if (isWhiteSpace(ch0)) {
                currentOffset += 1;
                continue;
            }

            if (ch0 == '-'
                    && currentOffset + 1 < length
                    && sourceString.charAt(currentOffset + 1) == '-') {
                currentOffset += 2;
                if (currentOffset < length && sourceString.charAt(currentOffset) == '[') {
                    int level = scanLongBracketLevel(sourceString, currentOffset + 1);
                    if (level >= 0) {
                        int end = scanLongStringEnd(sourceString, currentOffset + level + 2, level);
                        if (end >= 0) {
                            currentOffset = end + level + 2;
                            continue;
                        } else {
                            throw new LuaParseError(source, currentOffset, length - currentOffset);
                        }
                    }
                }
                continue;
            }

            break;
        }

        this.offset = currentOffset;
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
        int currentOffset = this.offset;
        final CharSequence sourceString = this.sourceString;
        final int length = sourceString.length();

        if (currentOffset == length) {
            return TokenUtils.tokenOf(TokenKind.TOKEN_EOF, currentOffset, 0);
        }

        char ch = sourceString.charAt(currentOffset);
        switch (ch) {
            case ';':
                this.offset += 1;
                return TokenUtils.tokenOf(TokenKind.TOKEN_SEP_SEMI, currentOffset, 1);
            case ',':
                this.offset += 1;
                return TokenUtils.tokenOf(TokenKind.TOKEN_SEP_COMMA, currentOffset, 1);
            case '(':
                this.offset += 1;
                return TokenUtils.tokenOf(TokenKind.TOKEN_SEP_LPAREN, currentOffset, 1);
            case ')':
                this.offset += 1;
                return TokenUtils.tokenOf(TokenKind.TOKEN_SEP_RPAREN, currentOffset, 1);
            case ']':
                this.offset += 1;
                return TokenUtils.tokenOf(TokenKind.TOKEN_SEP_RBRACK, currentOffset, 1);
            case '{':
                this.offset += 1;
                return TokenUtils.tokenOf(TokenKind.TOKEN_SEP_LCURLY, currentOffset, 1);
            case '}':
                this.offset += 1;
                return TokenUtils.tokenOf(TokenKind.TOKEN_SEP_RCURLY, currentOffset, 1);
            case '+':
                this.offset += 1;
                return TokenUtils.tokenOf(TokenKind.TOKEN_OP_ADD, currentOffset, 1);
            case '-':
                this.offset += 1;
                return TokenUtils.tokenOf(TokenKind.TOKEN_OP_MINUS, currentOffset, 1);
            case '*':
                this.offset += 1;
                return TokenUtils.tokenOf(TokenKind.TOKEN_OP_MUL, currentOffset, 1);
            case '^':
                this.offset += 1;
                return TokenUtils.tokenOf(TokenKind.TOKEN_OP_POW, currentOffset, 1);
            case '%':
                this.offset += 1;
                return TokenUtils.tokenOf(TokenKind.TOKEN_OP_MOD, currentOffset, 1);
            case '&':
                this.offset += 1;
                return TokenUtils.tokenOf(TokenKind.TOKEN_OP_BAND, currentOffset, 1);
            case '|':
                this.offset += 1;
                return TokenUtils.tokenOf(TokenKind.TOKEN_OP_BOR, currentOffset, 1);
            case '#':
                this.offset += 1;
                return TokenUtils.tokenOf(TokenKind.TOKEN_OP_LEN, currentOffset, 1);
            case ':':
                if (currentOffset + 1 < length && sourceString.charAt(currentOffset + 1) == ':') {
                    this.offset += 2;
                    return TokenUtils.tokenOf(TokenKind.TOKEN_SEP_LABEL, currentOffset, 2);
                } else {
                    this.offset += 1;
                    return TokenUtils.tokenOf(TokenKind.TOKEN_SEP_COLON, currentOffset, 1);
                }
            case '/':
                if (currentOffset + 1 < length && sourceString.charAt(currentOffset + 1) == '/') {
                    this.offset += 2;
                    return TokenUtils.tokenOf(TokenKind.TOKEN_OP_IDIV, currentOffset, 2);
                } else {
                    this.offset += 1;
                    return TokenUtils.tokenOf(TokenKind.TOKEN_OP_DIV, currentOffset, 1);
                }
            case '~':
                if (currentOffset + 1 < length && sourceString.charAt(currentOffset + 1) == '=') {
                    this.offset += 2;
                    return TokenUtils.tokenOf(TokenKind.TOKEN_OP_NE, currentOffset, 2);
                } else {
                    this.offset += 1;
                    return TokenUtils.tokenOf(TokenKind.TOKEN_OP_WAVE, currentOffset, 1);
                }
            case '=':
                if (currentOffset + 1 < length && sourceString.charAt(currentOffset + 1) == '=') {
                    this.offset += 2;
                    return TokenUtils.tokenOf(TokenKind.TOKEN_OP_EQ, currentOffset, 2);
                } else {
                    this.offset += 1;
                    return TokenUtils.tokenOf(TokenKind.TOKEN_OP_ASSIGN, currentOffset, 1);
                }
            case '<':
                if (currentOffset + 1 < length && sourceString.charAt(currentOffset + 1) == '<') {
                    this.offset += 2;
                    return TokenUtils.tokenOf(TokenKind.TOKEN_OP_SHL, currentOffset, 2);
                } else if (currentOffset + 1 < length && sourceString.charAt(currentOffset + 1) == '=') {
                    this.offset += 2;
                    return TokenUtils.tokenOf(TokenKind.TOKEN_OP_LE, currentOffset, 2);
                } else {
                    this.offset += 1;
                    return TokenUtils.tokenOf(TokenKind.TOKEN_OP_LT, currentOffset, 1);
                }
            case '>':
                if (currentOffset + 1 < length && sourceString.charAt(currentOffset + 1) == '>') {
                    this.offset += 2;
                    return TokenUtils.tokenOf(TokenKind.TOKEN_OP_SHR, currentOffset, 2);
                } else if (currentOffset + 1 < length && sourceString.charAt(currentOffset + 1) == '=') {
                    this.offset += 2;
                    return TokenUtils.tokenOf(TokenKind.TOKEN_OP_GE, currentOffset, 2);
                } else {
                    this.offset += 1;
                    return TokenUtils.tokenOf(TokenKind.TOKEN_OP_GT, currentOffset, 1);
                }
            case '.':
                if (currentOffset + 2 < length
                        && sourceString.charAt(currentOffset + 1) == '.'
                        && sourceString.charAt(currentOffset + 2) == '.') {
                    this.offset += 3;
                    return TokenUtils.tokenOf(TokenKind.TOKEN_VARARG, currentOffset, 3);
                } else if (currentOffset + 1 < length && sourceString.charAt(currentOffset + 1) == '.') {
                    this.offset += 2;
                    return TokenUtils.tokenOf(TokenKind.TOKEN_OP_CONCAT, currentOffset, 2);
                } else if (currentOffset == length - 1 || !isDigit(sourceString.charAt(currentOffset))) {
                    this.offset += 1;
                    return TokenUtils.tokenOf(TokenKind.TOKEN_SEP_DOT, currentOffset, 1);
                } else {
                    break;
                }
            case '[':
                if (currentOffset + 1 < length) {
                    char ch1 = sourceString.charAt(currentOffset + 1);
                    if (ch1 == '[' || ch1 == '=') {
                        int level = scanLongBracketLevel(sourceString, currentOffset + 1);
                        if (level < 0) {
                            throw new LuaParseError(source, currentOffset, 2,
                                    "invalid long string delimiter");
                        }
                        final int infoBegin = currentOffset + level + 2;
                        int end = scanLongStringEnd(sourceString, infoBegin, level);
                        if (end >= 0) {
                            this.offset = end + level + 2;
                            return TokenUtils.tokenOf(TokenKind.TOKEN_STRING, currentOffset, end + level + 2 - currentOffset);
                        } else {
                            throw new LuaParseError(source, currentOffset, length - currentOffset);
                        }
                    }
                } else {
                    this.offset += 1;
                    return TokenUtils.tokenOf(TokenKind.TOKEN_SEP_LBRACK, currentOffset, 1);
                }
            case '\'':
            case '"':
                return nextStringToken(currentOffset);
        }


        if (ch == '.' || isDigit(ch)) {
            return nextNumberToken(currentOffset);
        }
        if (ch == '_' || (ch >= 'a' && ch <= 'z') || (ch >= 'A' && ch <= 'Z')) {
            StringBuilder builder = new StringBuilder();
            builder.append(ch);
            int i = 1;
            while (currentOffset + i < length) {
                char c = sourceString.charAt(currentOffset + i);
                if (c == '_' || (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z') || (c >= '0' && c <= '9')) {
                    builder.append(c);
                    i += 1;
                } else {
                    break;
                }
            }
            this.offset += i;
            String id = builder.toString();
            putCache(currentOffset, id);
            int kind = TokenUtils.keywordKind(id);
            return TokenUtils.tokenOf(kind == -1 ? TokenKind.TOKEN_IDENTIFIER : kind, currentOffset, i);
        }

        throw new UnsupportedOperationException(String.format("offset=%d, ch=%s", currentOffset, ch));
    }

    @Token long nextStringToken(final int beginOffset) {
        final CharSequence sourceString = this.sourceString;
        final int length = sourceString.length();

        char ch0 = sourceString.charAt(beginOffset);
        assert ch0 == '\'' || ch0 == '"';

        StringBuilder builder = new StringBuilder();
        int currentOffset = beginOffset + 1;
        loop:
        while (currentOffset < length) {
            char ch1 = sourceString.charAt(currentOffset);
            if (ch1 == ch0) {
                long token = TokenUtils.tokenOf(TokenKind.TOKEN_STRING, beginOffset, currentOffset + 1 - beginOffset);
                this.offset = currentOffset + 1;
                putCache(beginOffset, builder.toString());
                return token;
            }
            if (ch1 == '\\') {
                if (currentOffset + 1 >= length) {
                    throw new LuaParseError(source, beginOffset, currentOffset + 1 - beginOffset, "unfinished string");
                }
                char ch2 = sourceString.charAt(currentOffset + 1);
                switch (ch2) {
                    case 'a':
                        builder.append((char) 7);
                        currentOffset += 2;
                        continue;
                    case 'b':
                        builder.append('\b');
                        currentOffset += 2;
                        continue;
                    case 'f':
                        builder.append('\f');
                        currentOffset += 2;
                        continue;
                    case 'n':
                        builder.append('\n');
                        currentOffset += 2;
                        continue;
                    case 'r':
                        builder.append('\r');
                        currentOffset += 2;
                        continue;
                    case 't':
                        builder.append('\t');
                        currentOffset += 2;
                        continue;
                    case 'v':
                        builder.append((char) 11);
                        currentOffset += 2;
                        continue;
                    case '\\':
                        builder.append('\\');
                        currentOffset += 2;
                        continue;
                    case '"':
                        builder.append('"');
                        currentOffset += 2;
                        continue;
                    case '\'':
                        builder.append('\'');
                        currentOffset += 2;
                        continue;
                    case '0':
                    case '1':
                    case '2':
                    case '3':
                    case '4':
                    case '5':
                    case '6':
                    case '7':
                    case '8':
                    case '9': {
                        int v = parseDigit(ch2);
                        int n = 2;

                        char tmp;

                        if (currentOffset + 2 < length) {
                            tmp = sourceString.charAt(currentOffset + 2);
                            if (isDigit(tmp)) {
                                n = 3;
                                v *= 10;
                                v += parseDigit(tmp);

                                if (currentOffset + 3 < length) {
                                    tmp = sourceString.charAt(currentOffset + 3);
                                    if (isDigit(tmp)) {
                                        n = 4;
                                        v *= 10;
                                        v += parseDigit(tmp);
                                    }
                                }

                            }
                        }

                        if (v > 0xFF) {
                            throw new LuaParseError(source, currentOffset, n,
                                    "decimal escape too large"
                            );
                        }

                        builder.append((char) v);
                        currentOffset += n;
                        break;
                    }
                    case 'x': {
                        if (currentOffset + 3 >= length) {

                            throw new LuaParseError(source,
                                    currentOffset,
                                    currentOffset + 2 > length ? 3 : 4,
                                    "hexadecimal digit expected");
                        }

                        char ch3 = sourceString.charAt(currentOffset + 2);
                        char ch4 = sourceString.charAt(currentOffset + 3);

                        if (!isHexDigit(ch3) || !isHexDigit(ch4)) {
                            throw new LuaParseError(source, beginOffset, 4,
                                    "hexadecimal digit expected");
                        }

                        builder.append((char) (parseHexDigit(ch3) * 16 + parseHexDigit(ch4)));
                        currentOffset += 4;
                        break;
                    }
                    case 'u': {
                        if (currentOffset + 2 >= length || sourceString.charAt(currentOffset + 2) != '{') {
                            throw new LuaParseError(source, currentOffset, 2,
                                    "missing '{'");
                        }

                        int i = 3;
                        int v = 0;
                        while (currentOffset + i < length) {
                            char ch = sourceString.charAt(currentOffset + i);
                            i += 1;
                            if (ch == '}') {
                                if (i == 4) {
                                    throw new LuaParseError(source, currentOffset, i, "hexadecimal digit expected");
                                }
                                if (v > 0x10FFFF) {
                                    throw new LuaParseError(source, currentOffset, i,
                                            "UTF-8 value too large");
                                }
                                builder.append((char) v);
                                currentOffset += i;
                                continue loop;
                            } else if (isHexDigit(ch)) {
                                v *= 16;
                                v += parseHexDigit(ch);
                            } else {
                                throw new LuaParseError(source, currentOffset, i - 1, "hexadecimal digit expected");
                            }
                        }
                        throw new LuaParseError(source, currentOffset, i, "missing '}'");
                    }
                    case 'z':
                        int i = 2;
                        while (currentOffset + i < length) {
                            if (!isWhiteSpace(sourceString.charAt(currentOffset + i))) {
                                break;
                            }
                            i++;
                        }
                        currentOffset += i;

                }
                continue;
            }

            builder.append(ch1);
            currentOffset += 1;
        }

        throw new LuaParseError(source, beginOffset, length - beginOffset, "unfinished string");
    }

    @Token long nextNumberToken(final int beginOffset) {
        final CharSequence sourceString = this.sourceString;
        final int length = sourceString.length();

        int currentOffset = beginOffset;
        char ch = sourceString.charAt(currentOffset);

        if (ch == '0' && currentOffset + 1 < length
                && (sourceString.charAt(currentOffset + 1) == 'x' || sourceString.charAt(currentOffset + 1) == 'X')) {
            currentOffset += 2;

            boolean empty = true;

            while (currentOffset < length) {
                ch = sourceString.charAt(currentOffset);
                if (isHexDigit(ch)) {
                    empty = false;
                    currentOffset += 1;
                } else {
                    break;
                }
            }

            if (currentOffset < length && (sourceString.charAt(currentOffset) == '.')) {
                currentOffset += 1;
                while (currentOffset < length) {
                    ch = sourceString.charAt(currentOffset);
                    if (isHexDigit(ch)) {
                        empty = false;
                        currentOffset += 1;
                    } else {
                        break;
                    }
                }
            }
            if (empty) {
                throw new LuaParseError(source, beginOffset, currentOffset - beginOffset, "malformed number");
            }
            empty = true;
            if (currentOffset < length
                    && (sourceString.charAt(currentOffset) == 'p' || sourceString.charAt(currentOffset) == 'P')) {
                currentOffset += 1;
                if (currentOffset < length && sourceString.charAt(currentOffset) == '-') {
                    currentOffset += 1;
                }
                while (currentOffset < length) {
                    ch = sourceString.charAt(currentOffset);
                    if (isHexDigit(ch)) {
                        empty = false;
                        currentOffset += 1;
                    } else {
                        break;
                    }
                }
                if (empty) {
                    throw new LuaParseError(source, beginOffset, currentOffset - beginOffset, "malformed number");
                }
            }
            this.offset = currentOffset;
            return TokenUtils.tokenOf(TokenKind.TOKEN_NUMBER, beginOffset, currentOffset - beginOffset);
        } else {
            boolean empty = true;

            while (currentOffset < length) {
                ch = sourceString.charAt(currentOffset);
                if (isDigit(ch)) {
                    empty = false;
                    currentOffset += 1;
                } else {
                    break;
                }
            }

            if (currentOffset < length && (sourceString.charAt(currentOffset) == '.')) {
                currentOffset += 1;
                while (currentOffset < length) {
                    ch = sourceString.charAt(currentOffset);
                    if (isDigit(ch)) {
                        empty = false;
                        currentOffset += 1;
                    } else {
                        break;
                    }
                }
            }
            if (empty) {
                throw new LuaParseError(source, beginOffset, currentOffset - beginOffset, "malformed number");
            }
            empty = true;
            if (currentOffset < length
                    && (sourceString.charAt(currentOffset) == 'e' || sourceString.charAt(currentOffset) == 'E')) {
                currentOffset += 1;
                if (currentOffset < length && sourceString.charAt(currentOffset) == '-') {
                    currentOffset += 1;
                }
                while (currentOffset < length) {
                    ch = sourceString.charAt(currentOffset);
                    if (isDigit(ch)) {
                        empty = false;
                        currentOffset += 1;
                    } else {
                        break;
                    }
                }
                if (empty) {
                    throw new LuaParseError(source, beginOffset, currentOffset - beginOffset, "malformed number");
                }
            }
            this.offset = currentOffset;
            return TokenUtils.tokenOf(TokenKind.TOKEN_NUMBER, beginOffset, currentOffset - beginOffset);
        }
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
