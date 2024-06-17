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

import com.oracle.truffle.api.TruffleFile;

import java.io.IOException;
import java.nio.charset.Charset;

public class LuaFileDetector implements TruffleFile.FileTypeDetector {
    @Override
    public String findMimeType(TruffleFile file) throws IOException {
        String name = file.getName();
        if (name != null && name.endsWith(".lua")) {
            return LuaLanguage.MIME_TYPE;
        }
        return null;
    }

    @Override
    public Charset findEncoding(TruffleFile file) throws IOException {
        return null;
    }
}
