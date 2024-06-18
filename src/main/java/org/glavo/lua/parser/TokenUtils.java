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
package org.glavo.lua.parser;

import com.oracle.truffle.api.source.Source;
import com.oracle.truffle.api.source.SourceSection;
import org.jetbrains.annotations.NotNull;

public final class TokenUtils {
    public static final int LENGTH_MASK = 0xfffffff;

    private static final int LENGTH_SHIFT = 8;
    private static final int OFFSET_SHIFT = 36;

    public static @Token long tokenOf(
            @TokenKind int kind,
            int offset, int length
    ) {
        assert kind >= TokenKind.TOKEN_EOF && kind <= TokenKind.TOKEN_STRING;
        assert length >= 0 && offset >= 0;
        assert offset <= LENGTH_MASK && length <= LENGTH_MASK;

        return (long) offset << OFFSET_SHIFT
                | (long) length << LENGTH_SHIFT
                | kind;
    }

    public static @TokenKind int getKind(@Token long token) {
        return (int) token & 0xff;
    }

    public static int getOffset(@Token long token) {
        return (int) (token >>> OFFSET_SHIFT);
    }

    public static int getLength(@Token long token) {
        return (int) ((token >>> LENGTH_SHIFT) & LENGTH_MASK);
    }

    public static SourceSection getSourceSection(Source source, @Token long token) {
        return source.createSection(getOffset(token), getLength(token));
    }

    public static boolean isEOF(@Token long token) {
        return getKind(token) == TokenKind.TOKEN_EOF;
    }

    public static @TokenKind int keywordKind(@NotNull String keyword) {
        return switch (keyword) {
            case "and" -> TokenKind.TOKEN_OP_AND;
            case "break" -> TokenKind.TOKEN_KW_BREAK;
            case "do" -> TokenKind.TOKEN_KW_DO;
            case "else" -> TokenKind.TOKEN_KW_ELSE;
            case "elseif" -> TokenKind.TOKEN_KW_ELSEIF;
            case "end" -> TokenKind.TOKEN_KW_END;
            case "false" -> TokenKind.TOKEN_KW_FALSE;
            case "for" -> TokenKind.TOKEN_KW_FOR;
            case "function" -> TokenKind.TOKEN_KW_FUNCTION;
            case "goto" -> TokenKind.TOKEN_KW_GOTO;
            case "if" -> TokenKind.TOKEN_KW_IF;
            case "in" -> TokenKind.TOKEN_KW_IN;
            case "local" -> TokenKind.TOKEN_KW_LOCAL;
            case "nil" -> TokenKind.TOKEN_KW_NIL;
            case "not" -> TokenKind.TOKEN_OP_NOT;
            case "or" -> TokenKind.TOKEN_OP_OR;
            case "repeat" -> TokenKind.TOKEN_KW_REPEAT;
            case "return" -> TokenKind.TOKEN_KW_RETURN;
            case "then" -> TokenKind.TOKEN_KW_THEN;
            case "true" -> TokenKind.TOKEN_KW_TRUE;
            case "until" -> TokenKind.TOKEN_KW_UNTIL;
            case "while" -> TokenKind.TOKEN_KW_WHILE;
            default -> -1;
        };
    }
}
