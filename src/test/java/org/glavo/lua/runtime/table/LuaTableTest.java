/*
 * Copyright 2024 Glavo
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.glavo.lua.runtime.table;

import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

public final class LuaTableTest {
    @Test
    public void arrayTest() {
        var table = new DefaultLuaTable();
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
