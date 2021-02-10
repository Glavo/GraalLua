package org.glavo.lua.runtime;

import com.oracle.truffle.api.TruffleLanguage;
import com.oracle.truffle.api.interop.InteropLibrary;
import com.oracle.truffle.api.interop.TruffleObject;
import com.oracle.truffle.api.library.ExportLibrary;
import com.oracle.truffle.api.library.ExportMessage;
import org.glavo.lua.LuaLanguage;

@ExportLibrary(InteropLibrary.class)
public final class LuaNil implements TruffleObject {
    public static final LuaNil Nil = new LuaNil();

    private LuaNil() {
    }

    @ExportMessage
    boolean hasLanguage() {
        return true;
    }

    @ExportMessage
    Class<? extends TruffleLanguage<?>> getLanguage() {
        return LuaLanguage.class;
    }

    @ExportMessage
    boolean isNull() {
        return true;
    }

    @Override
    public final String toString() {
        return "nil";
    }
}
