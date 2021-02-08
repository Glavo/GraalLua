package org.glavo.lua;

import com.oracle.truffle.api.TruffleLanguage;

@TruffleLanguage.Registration(
        id = LuaLanguage.ID,
        name = "lua",
        defaultMimeType = LuaLanguage.MIME_TYPE,
        characterMimeTypes = LuaLanguage.MIME_TYPE,
        fileTypeDetectors = LuaFileDetector.class
)
public final class LuaLanguage extends TruffleLanguage<LuaContext> {
    public static volatile int counter;

    public static final String ID = "lua";
    public static final String MIME_TYPE = "application/x-lua";

    public LuaLanguage() {
        //noinspection NonAtomicOperationOnVolatileField
        ++counter;

    }

    @Override
    protected final LuaContext createContext(Env env) {
        return null;
    }
}
