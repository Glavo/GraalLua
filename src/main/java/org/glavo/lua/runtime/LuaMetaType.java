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
package org.glavo.lua.runtime;

import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.TruffleLanguage;
import com.oracle.truffle.api.dsl.Cached;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.interop.InteropLibrary;
import com.oracle.truffle.api.interop.TruffleObject;
import com.oracle.truffle.api.library.CachedLibrary;
import com.oracle.truffle.api.library.ExportLibrary;
import com.oracle.truffle.api.library.ExportMessage;
import org.glavo.lua.LuaLanguage;

import java.util.function.BiPredicate;

@ExportLibrary(InteropLibrary.class)
public final class LuaMetaType implements TruffleObject {

    public static final LuaMetaType NULL = new LuaMetaType("nil", InteropLibrary::isNull);
    public static final LuaMetaType BOOLEAN = new LuaMetaType("boolean", InteropLibrary::isBoolean);
    public static final LuaMetaType STRING = new LuaMetaType("string", InteropLibrary::isString);
    public static final LuaMetaType NUMBER = new LuaMetaType("number", InteropLibrary::isNumber);
    public static final LuaMetaType FUNCTION = new LuaMetaType("function", InteropLibrary::isExecutable);

    private final String name;
    private final BiPredicate<InteropLibrary, Object> typeCheck;

    private LuaMetaType(String name, BiPredicate<InteropLibrary, Object> typeCheck) {
        this.name = name;
        this.typeCheck = typeCheck;
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
    boolean isMetaObject() {
        return true;
    }

    @ExportMessage
    static class IsMetaInstance {
        @Specialization(guards = "type == cachedType", limit = "3")
        static boolean doCached(LuaMetaType type, Object value,
                                @Cached("type") LuaMetaType cachedType,
                                @CachedLibrary("value") InteropLibrary valueLib) {
            return cachedType.typeCheck.test(valueLib, value);
        }

        @CompilerDirectives.TruffleBoundary
        @Specialization(replaces = "doCached")
        static boolean doGeneric(LuaMetaType type, Object value) {
            return type.typeCheck.test(InteropLibrary.getUncached(), value);
        }
    }

    @CompilerDirectives.TruffleBoundary
    @Override
    public String toString() {
        return "LuaMetaType[" + name + "]";
    }
}
