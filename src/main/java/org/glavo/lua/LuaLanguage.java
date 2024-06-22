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
package org.glavo.lua;

import com.oracle.truffle.api.CallTarget;
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
        counter += 1;
    }

    @Override
    protected LuaContext createContext(Env env) {
        return new LuaContext(this, env);
    }

    @Override
    protected CallTarget parse(ParsingRequest request) throws Exception {
        throw new UnsupportedOperationException("language is not implemented yet");
    }
}
