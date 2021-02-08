package org.glavo.lua.runtime;

import com.oracle.truffle.api.interop.TruffleObject;

public final class LuaNil implements TruffleObject {
    public static final LuaNil Nil = new LuaNil();

    private LuaNil() {
    }
}
