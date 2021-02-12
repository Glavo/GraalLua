package org.glavo.lua.parser;

import com.oracle.truffle.api.source.Source;

import java.io.IOException;
import java.io.Reader;

public final class Lexer implements AutoCloseable {
    private final Source source;
    private final Reader reader;
    private int charOffset = 0;

    public Lexer(Source source) {
        assert source != null;
        this.source = source;
        reader = source.getReader();

    }

    @Override
    public final void close() throws IOException {
        reader.close();
    }
}
