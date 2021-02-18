package org.glavo.lua;

import java.nio.charset.StandardCharsets;
import java.util.WeakHashMap;

public final class LuaContext {
    final WeakHashMap<String, byte[]> cachedStrings = new WeakHashMap<>();

    public final byte[] toBytes(String str) {
        assert str != null;
        return cachedStrings.computeIfAbsent(str, s -> s.getBytes(StandardCharsets.UTF_8));
    }
}
