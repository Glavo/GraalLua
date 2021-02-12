package org.glavo.lua.parser;

import com.oracle.truffle.api.source.Source;
import com.oracle.truffle.api.source.SourceSection;

import java.util.Objects;

public final class Token {
    public final TokenKind kind;
    public final int offset;
    public final String value;

    public Token(TokenKind kind, int offset, String value) {
        this.kind = kind;
        this.offset = offset;
        this.value = value;
    }

    public final SourceSection getSourceSection(Source source) {
        return source.createSection(offset, value.length());
    }

    @Override
    public final int hashCode() {
        return Objects.hash(kind, offset, value);
    }

    @Override
    public final boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Token)) {
            return false;
        }
        Token token = (Token) o;
        return offset == token.offset
                && kind == token.kind
                && Objects.equals(value, token.value);
    }

    @Override
    public final String toString() {
        return String.format("Token[kind=%s, offset=%d, value='%s']", kind, offset, value);
    }
}
