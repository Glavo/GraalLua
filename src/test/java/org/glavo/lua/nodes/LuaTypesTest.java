package org.glavo.lua.nodes;

import org.glavo.lua.nodes.LuaTypes;
import org.glavo.lua.runtime.LuaNil;
import org.glavo.lua.runtime.LuaTable;
import org.junit.jupiter.api.*;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

public final class LuaTypesTest {
    @Test
    public void stringArithmetic() {
        assertEquals(LuaTypes.castDouble("1.1") + 1, 2.1);
        assertEquals(LuaTypes.castLong("123") + 10, 133);
        assertEquals(LuaTypes.castLong("0xfff") + 1, 0x1000);
        assertThrows(NumberFormatException.class, () -> {
            LuaTypes.castLong("hello");
        });
    }

    @Test
    public void truthValue() {
        assertTrue(LuaTypes.truthValue(1));
        assertTrue(LuaTypes.truthValue(true));
        assertFalse(LuaTypes.truthValue(LuaNil.Nil));
        assertTrue(LuaTypes.truthValue(""));
        assertTrue(LuaTypes.truthValue(0));
        assertTrue(LuaTypes.truthValue(-1));
        assertTrue(LuaTypes.truthValue(new LuaTable()));
    }
}
