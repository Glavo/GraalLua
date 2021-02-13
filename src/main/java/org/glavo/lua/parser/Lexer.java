package org.glavo.lua.parser;

import com.oracle.truffle.api.source.Source;

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

    public final void skipWhiteSpacesAndComment() {
        throw new UnsupportedOperationException(); // TODO
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
}
