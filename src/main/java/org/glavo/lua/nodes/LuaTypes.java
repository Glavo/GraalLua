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

import com.oracle.truffle.api.dsl.ImplicitCast;
import com.oracle.truffle.api.dsl.TypeCast;
import com.oracle.truffle.api.dsl.TypeCheck;
import com.oracle.truffle.api.dsl.TypeSystem;
import org.glavo.lua.runtime.LuaNil;
import org.glavo.lua.runtime.LuaString;

import java.nio.charset.StandardCharsets;

/**
 * Type System of Lua.
 *
 * @see <a href="https://www.lua.org/pil/2.html">Types and Values</a>
 * @see <a href="https://developer.roblox.com/en-us/articles/Type-Coercion-in-Lua">Type Coercion in Lua</a>
 * @see <a href="http://www.lua.org/manual/5.1/manual.html#2.2.1">Coercion</a>
 */
@TypeSystem({LuaString.class, long.class, double.class, boolean.class})
public class LuaTypes {
    @TypeCheck(LuaNil.class)
    public static boolean isLuaNil(Object value) {
        return value == LuaNil.Nil;
    }

    @TypeCast(LuaNil.class)
    public static LuaNil asLuaNil(Object value) {
        assert isLuaNil(value);
        return LuaNil.Nil;
    }

    /**
     * Arithmetic Casting of String
     * Notice that Inequality Comparison should not coerce the types
     */
    @ImplicitCast
    public static long castLong(LuaString value) {
        return Long.decode(value.asString());
    }

    @ImplicitCast
    public static double castDouble(LuaString value) {
        return Double.parseDouble(value.asString());
    }

    // Numbers to Strings
    @ImplicitCast
    public static LuaString castString(long value) {
        // Lua 5 seems to keep all format in decimal
        return LuaString.valueOf(value);
    }

    @ImplicitCast
    public static LuaString castString(double value) {
        return new LuaString(Double.toString(value));
    }

    @ImplicitCast
    public static LuaString castString(boolean value) {
        return value ? LuaString.TRUE_STRING : LuaString.FALSE_STRING;
    }

    // Truth values
    // This should not be implicit,
    // because Lua's short circuit operation returns
    // the original value rather than the truth value
    public static boolean truthValue(Object value) {
        if (value instanceof Boolean) {
            return (Boolean) value;
        } else {
            return value != LuaNil.Nil;
        }
    }

}
