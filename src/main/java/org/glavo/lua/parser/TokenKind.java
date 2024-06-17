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

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE_USE)
@Retention(RetentionPolicy.SOURCE)
public @interface TokenKind {
    int TOKEN_EOF = 0;                  // end-of-file
    int TOKEN_VARARG = 1;               // ...
    int TOKEN_SEP_SEMI = 2;             // ;
    int TOKEN_SEP_COMMA = 3;            // ,
    int TOKEN_SEP_DOT = 4;              // .
    int TOKEN_SEP_COLON = 5;            // :
    int TOKEN_SEP_LABEL = 6;            // ::
    int TOKEN_SEP_LPAREN = 7;           // (
    int TOKEN_SEP_RPAREN = 8;           // )
    int TOKEN_SEP_LBRACK = 9;           // [
    int TOKEN_SEP_RBRACK = 10;          // ]
    int TOKEN_SEP_LCURLY = 11;          // {
    int TOKEN_SEP_RCURLY = 12;          // }
    int TOKEN_OP_ASSIGN = 13;           // =
    int TOKEN_OP_MINUS = 14;            // - (sub or unm)
    int TOKEN_OP_WAVE = 15;             // ~ (bnot or bxor)
    int TOKEN_OP_ADD = 16;              // +
    int TOKEN_OP_MUL = 17;              // *
    int TOKEN_OP_DIV = 18;              // /
    int TOKEN_OP_IDIV = 19;             // //
    int TOKEN_OP_POW = 20;              // ^
    int TOKEN_OP_MOD = 21;              // %
    int TOKEN_OP_BAND = 22;             // &
    int TOKEN_OP_BOR = 23;              // |
    int TOKEN_OP_SHR = 24;              // >>
    int TOKEN_OP_SHL = 25;              // <<
    int TOKEN_OP_CONCAT = 26;           // ..
    int TOKEN_OP_LT = 27;               // <
    int TOKEN_OP_LE = 28;               // <=
    int TOKEN_OP_GT = 29;               // >
    int TOKEN_OP_GE = 30;               // >=
    int TOKEN_OP_EQ = 31;               // ==
    int TOKEN_OP_NE = 32;               // ~=
    int TOKEN_OP_LEN = 33;              // #
    int TOKEN_OP_AND = 34;              // and
    int TOKEN_OP_OR = 35;               // or
    int TOKEN_OP_NOT = 36;              // not
    int TOKEN_KW_BREAK = 37;            // break
    int TOKEN_KW_DO = 38;               // do
    int TOKEN_KW_ELSE = 39;             // else
    int TOKEN_KW_ELSEIF = 40;           // elseif
    int TOKEN_KW_END = 41;              // end
    int TOKEN_KW_FALSE = 42;            // false
    int TOKEN_KW_FOR = 43;              // for
    int TOKEN_KW_FUNCTION = 44;         // function
    int TOKEN_KW_GOTO = 45;             // goto
    int TOKEN_KW_IF = 46;               // if
    int TOKEN_KW_IN = 47;               // in
    int TOKEN_KW_LOCAL = 48;            // local
    int TOKEN_KW_NIL = 49;              // nil
    int TOKEN_KW_REPEAT = 50;           // repeat
    int TOKEN_KW_RETURN = 51;           // return
    int TOKEN_KW_THEN = 52;             // then
    int TOKEN_KW_TRUE = 53;             // true
    int TOKEN_KW_UNTIL = 54;            // until
    int TOKEN_KW_WHILE = 55;            // while
    int TOKEN_IDENTIFIER = 56;          // identifier
    int TOKEN_NUMBER = 57;              // number literal
    int TOKEN_STRING = 58;              // string literal

    // --------
    int TOKEN_OP_UNM = TOKEN_OP_MINUS;  // unary minus
    int TOKEN_OP_SUB = TOKEN_OP_MINUS;
    int TOKEN_OP_BNOT = TOKEN_OP_WAVE;
    int TOKEN_OP_BXOR = TOKEN_OP_WAVE;
}
