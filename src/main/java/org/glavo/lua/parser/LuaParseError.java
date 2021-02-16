package org.glavo.lua.parser;

import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.exception.AbstractTruffleException;
import com.oracle.truffle.api.interop.ExceptionType;
import com.oracle.truffle.api.interop.InteropLibrary;
import com.oracle.truffle.api.interop.TruffleObject;
import com.oracle.truffle.api.interop.UnsupportedMessageException;
import com.oracle.truffle.api.library.ExportLibrary;
import com.oracle.truffle.api.library.ExportMessage;
import com.oracle.truffle.api.source.Source;
import com.oracle.truffle.api.source.SourceSection;

@ExportLibrary(InteropLibrary.class)
public class LuaParseError extends AbstractTruffleException {
    private static final long serialVersionUID = 0L;

    private final Source source;
    private final int offset;
    private final int length;

    public LuaParseError(Source source, int offset, int length) {
        this(source, offset, length, null);
    }

    public LuaParseError(Source source, int offset, int length, String message) {
        super(message);
        this.source = source;
        this.offset = offset;
        this.length = length;
    }

    @ExportMessage
    ExceptionType getExceptionType() {
        return ExceptionType.PARSE_ERROR;
    }

    @ExportMessage
    boolean hasSourceLocation() {
        return source != null;
    }

    @ExportMessage(name = "getSourceLocation")
    @CompilerDirectives.TruffleBoundary
    SourceSection getSourceSection() throws UnsupportedMessageException {
        if (source == null) {
            throw UnsupportedMessageException.create();
        }
        return source.createSection(offset, length);
    }
}
