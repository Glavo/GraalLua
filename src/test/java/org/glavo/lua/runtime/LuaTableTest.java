package org.glavo.lua.runtime;

import org.junit.jupiter.api.*;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

public final class LuaTableTest {
    @Test
    public void arrayTest() {
        var table = new LuaTable();
        assertNull(table.array);
        assertNull(table.table);
        assertEquals(0, table.size());

        assertNull(table.put(1, "str1"));
        assertEquals("str1", table.array[0]);
        assertEquals("str1", table.get(1));
        assertEquals(1, table.size());
        assertNull(table.table);

        for (int i = 2; i <= 10; i++) {
            table.put(i, "str" + i);
        }
        assertEquals(10, table.size());
        for (int i = 1; i <= 10; i++) {
            assertEquals("str" + i, table.get(i));
        }

    }
}
