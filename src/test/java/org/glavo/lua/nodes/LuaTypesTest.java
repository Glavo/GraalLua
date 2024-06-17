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
package org.glavo.lua.nodes;

import org.glavo.lua.runtime.LuaNil;
import org.glavo.lua.runtime.LuaString;
import org.glavo.lua.runtime.LuaTable;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

public final class LuaTypesTest {
    @Test
    public void stringArithmetic() {
        assertEquals(LuaTypes.castDouble(new LuaString("1.1")) + 1, 2.1);
        assertEquals(LuaTypes.castLong(new LuaString("123")) + 10, 133);
        assertEquals(LuaTypes.castLong(new LuaString("0xfff")) + 1, 0x1000);
        assertThrows(NumberFormatException.class, () -> {
            LuaTypes.castLong(new LuaString("hello"));
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
