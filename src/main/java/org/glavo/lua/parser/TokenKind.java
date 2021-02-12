package org.glavo.lua.parser;

public enum TokenKind {
    TOKEN_EOF,              // end-of-file
    TOKEN_VARARG,           // ...
    TOKEN_SEP_SEMI,         // ;
    TOKEN_SEP_COMMA,        // ,
    TOKEN_SEP_DOT,          // .
    TOKEN_SEP_COLON,        // :
    TOKEN_SEP_LABEL,        // ::
    TOKEN_SEP_LPAREN,       // (
    TOKEN_SEP_RPAREN,       // )
    TOKEN_SEP_LBRACK,       // [
    TOKEN_SEP_RBRACK,       // ]
    TOKEN_SEP_LCURLY,       // {
    TOKEN_SEP_RCURLY,       // }
    TOKEN_OP_ASSIGN,        // =
    TOKEN_OP_MINUS,         // - (sub or unm)
    TOKEN_OP_WAVE,          // ~ (bnot or bxor)
    TOKEN_OP_ADD,           // +
    TOKEN_OP_MUL,           // *
    TOKEN_OP_DIV,           // /
    TOKEN_OP_IDIV,          // //
    TOKEN_OP_POW,           // ^
    TOKEN_OP_MOD,           // %
    TOKEN_OP_BAND,          // &
    TOKEN_OP_BOR,           // |
    TOKEN_OP_SHR,           // >>
    TOKEN_OP_SHL,           // <<
    TOKEN_OP_CONCAT,        // ..
    TOKEN_OP_LT,            // <
    TOKEN_OP_LE,            // <=
    TOKEN_OP_GT,            // >
    TOKEN_OP_GE,            // >=
    TOKEN_OP_EQ,            // ==
    TOKEN_OP_NE,            // ~=
    TOKEN_OP_LEN,           // #
    TOKEN_OP_AND,           // and
    TOKEN_OP_OR,            // or
    TOKEN_OP_NOT,           // not
    TOKEN_KW_BREAK,         // break
    TOKEN_KW_DO,            // do
    TOKEN_KW_ELSE,          // else
    TOKEN_KW_ELSEIF,        // elseif
    TOKEN_KW_END,           // end
    TOKEN_KW_FALSE,         // false
    TOKEN_KW_FOR,           // for
    TOKEN_KW_FUNCTION,      // function
    TOKEN_KW_GOTO,          // goto
    TOKEN_KW_IF,            // if
    TOKEN_KW_IN,            // in
    TOKEN_KW_LOCAL,         // local
    TOKEN_KW_NIL,           // nil
    TOKEN_KW_REPEAT,        // repeat
    TOKEN_KW_RETURN,        // return
    TOKEN_KW_THEN,          // then
    TOKEN_KW_TRUE,          // true
    TOKEN_KW_UNTIL,         // until
    TOKEN_KW_WHILE,         // while
    TOKEN_IDENTIFIER,       // identifier
    TOKEN_NUMBER,           // number literal
    TOKEN_STRING;           // string literal
    public static final TokenKind TOKEN_OP_UNM = TOKEN_OP_MINUS;    // unary minus
    public static final TokenKind TOKEN_OP_SUB = TOKEN_OP_MINUS;
    public static final TokenKind TOKEN_OP_BNOT = TOKEN_OP_WAVE;
    public static final TokenKind TOKEN_OP_BXOR = TOKEN_OP_WAVE;

    public static TokenKind keywordKind(String keyword) {
        switch (keyword) {
            case "and":
                return TOKEN_OP_AND;
            case "break":
                return TOKEN_KW_BREAK;
            case "do":
                return TOKEN_KW_DO;
            case "else":
                return TOKEN_KW_ELSE;
            case "elseif":
                return TOKEN_KW_ELSEIF;
            case "end":
                return TOKEN_KW_END;
            case "false":
                return TOKEN_KW_FALSE;
            case "for":
                return TOKEN_KW_FOR;
            case "function":
                return TOKEN_KW_FUNCTION;
            case "goto":
                return TOKEN_KW_GOTO;
            case "if":
                return TOKEN_KW_IF;
            case "in":
                return TOKEN_KW_IN;
            case "local":
                return TOKEN_KW_LOCAL;
            case "nil":
                return TOKEN_KW_NIL;
            case "not":
                return TOKEN_OP_NOT;
            case "or":
                return TOKEN_OP_OR;
            case "repeat":
                return TOKEN_KW_REPEAT;
            case "return":
                return TOKEN_KW_RETURN;
            case "then":
                return TOKEN_KW_THEN;
            case "true":
                return TOKEN_KW_TRUE;
            case "until":
                return TOKEN_KW_UNTIL;
            case "while":
                return TOKEN_KW_WHILE;
            default:
                return null;
        }
    }
}
