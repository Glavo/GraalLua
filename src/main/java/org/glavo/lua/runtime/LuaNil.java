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

    /**
     * {@link InteropLibrary#hasLanguage(Object)}
     */
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

    @ExportMessage
    Object toDisplayString(boolean allowSideEffects) {
        return "nil";
    }

    @Override
    public String toString() {
        return "nil";
    }
}
