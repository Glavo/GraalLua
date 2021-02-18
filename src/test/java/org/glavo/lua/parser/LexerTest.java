package org.glavo.lua.parser;

import com.oracle.truffle.api.source.Source;
import org.junit.jupiter.api.*;
import org.opentest4j.AssertionFailedError;

import static org.junit.jupiter.api.Assertions.*;

public class LexerTest {
    private static Lexer createLexer(String str) {
        return new Lexer(Source.newBuilder("lua", str, "Test").build());
    }

    @Test
    void scanLongBracketLevelTest() {
        assertEquals(-1, Lexer.scanLongBracketLevel("", 0));
        assertEquals(-1, Lexer.scanLongBracketLevel("foo", 0));
        assertEquals(-1, Lexer.scanLongBracketLevel("]", 0));
        assertEquals(0, Lexer.scanLongBracketLevel("[", 0));
        assertEquals(1, Lexer.scanLongBracketLevel("=[", 0));
        assertEquals(2, Lexer.scanLongBracketLevel("==[", 0));
        assertEquals(0, Lexer.scanLongBracketLevel("[==[", 0));
        assertEquals(-1, Lexer.scanLongBracketLevel("==", 0));
    }

    @Test
    void scanLongStringEndTest() {
        assertEquals(-1, Lexer.scanLongStringEnd("]", 0, 0));
        assertEquals(0, Lexer.scanLongStringEnd("]]", 0, 0));
        assertEquals(0, Lexer.scanLongStringEnd("]=]", 0, 1));
        assertEquals(-1, Lexer.scanLongStringEnd("]=]", 0, 2));
        assertEquals(1, Lexer.scanLongStringEnd("]]=]", 0, 1));
        assertEquals(3, Lexer.scanLongStringEnd("]= ]=]", 0, 1));
    }

    @Test
    void skipWhiteSpacesAndCommentTest() {
        Lexer lexer1 = createLexer("--[==[  ]==]str");
        lexer1.skipWhiteSpacesAndComment();
        assertEquals(12, lexer1.getOffset());

        Lexer lexer2 = createLexer("--[==[ ]]] foo bar ]=]");
        assertThrows(LuaParseError.class, () -> {
            //noinspection Convert2MethodRef
            lexer2.skipWhiteSpacesAndComment();
        });

        Lexer lexer3 = createLexer(" ");
        lexer3.skipWhiteSpacesAndComment();
        assertEquals(1, lexer3.getOffset());
    }

    private static void assertTokenEquals(@Token long expected, @Token long actual) {
        if (expected != actual) {
            String es =
                    String.format("Token[kind=%d,offset=%d,length=%d]",
                            TokenUtils.getKind(expected), TokenUtils.getOffset(expected), TokenUtils.getLength(expected));
            String as =
                    String.format("Token[kind=%d,offset=%d,length=%d]",
                            TokenUtils.getKind(actual), TokenUtils.getOffset(actual), TokenUtils.getLength(actual));
            throw new AssertionFailedError("expected: " + es + " but was: " + as, es, as);
        }
    }

    private static void assertTokens(Lexer lexer, @Token long... tokens) {
        for (@Token long token : tokens) {
            assertTokenEquals(token, lexer.nextToken());
        }
        assertEquals(TokenKind.TOKEN_EOF, TokenUtils.getKind(lexer.nextToken()), "TokenStream is not finished");
    }

    @Test
    void nextStringTokenTest() {
        assertTokenEquals(
                TokenUtils.tokenOf(TokenKind.TOKEN_STRING, 0, 2),
                createLexer("\"\"").nextStringToken(0)
        );

        assertTokenEquals(
                TokenUtils.tokenOf(TokenKind.TOKEN_STRING, 0, 6),
                createLexer("\"str1\"").nextStringToken(0)
        );
        assertTokenEquals(
                TokenUtils.tokenOf(TokenKind.TOKEN_STRING, 0, 8),
                createLexer("\"\\b\\n\\f\"").nextStringToken(0)
        );
        assertTokenEquals(
                TokenUtils.tokenOf(TokenKind.TOKEN_STRING, 1, 13),
                createLexer(" \"\\u{1234}foo\"").nextStringToken(1)
        );

        assertTokens(createLexer("\"\" \"\""),
                TokenUtils.tokenOf(TokenKind.TOKEN_STRING, 0, 2),
                TokenUtils.tokenOf(TokenKind.TOKEN_STRING, 3, 2)
        );

        //language=TEXT
        assertTokens(
                createLexer("function foo()\n" +
                        "  local str = \"\\u{4f60}\\u{597d}\"\n" +
                        "  return str\n" +
                        "end"),
                TokenUtils.tokenOf(TokenKind.TOKEN_KW_FUNCTION, 0, 8),
                TokenUtils.tokenOf(TokenKind.TOKEN_IDENTIFIER, 9, 3),
                TokenUtils.tokenOf(TokenKind.TOKEN_SEP_LPAREN, 12, 1),
                TokenUtils.tokenOf(TokenKind.TOKEN_SEP_RPAREN, 13, 1),
                TokenUtils.tokenOf(TokenKind.TOKEN_KW_LOCAL, 17, 5),
                TokenUtils.tokenOf(TokenKind.TOKEN_IDENTIFIER, 23, 3),
                TokenUtils.tokenOf(TokenKind.TOKEN_OP_ASSIGN, 27, 1),
                TokenUtils.tokenOf(TokenKind.TOKEN_STRING, 29, 18),
                TokenUtils.tokenOf(TokenKind.TOKEN_KW_RETURN, 50, 6),
                TokenUtils.tokenOf(TokenKind.TOKEN_IDENTIFIER, 57, 3),
                TokenUtils.tokenOf(TokenKind.TOKEN_KW_END, 61, 3)
        );
    }

    @Test
    void nextNumberTokenTest() {
        assertTokenEquals(
                TokenUtils.tokenOf(TokenKind.TOKEN_NUMBER, 0, 6),
                createLexer("0x1p-1").nextNumberToken(0)
        );
        assertTokens(
                createLexer("0x1abc.0apa 0xa"),
                TokenUtils.tokenOf(TokenKind.TOKEN_NUMBER, 0, 11),
                TokenUtils.tokenOf(TokenKind.TOKEN_NUMBER, 12, 3));

        assertTokens(createLexer(
                "3 345 0xff 0xBEBADA\n" +
                        "3.0 3.1416 314.16e-2 0.31416E1 34e1\n" +
                        "0x0.1E 0xA23p-4 0X1.921FB54442D18P+1\n" +
                        "3. .3 00001"),
                TokenUtils.tokenOf(TokenKind.TOKEN_NUMBER, 0, 1),
                TokenUtils.tokenOf(TokenKind.TOKEN_NUMBER, 2, 3),
                TokenUtils.tokenOf(TokenKind.TOKEN_NUMBER, 6, 4),
                TokenUtils.tokenOf(TokenKind.TOKEN_NUMBER, 11, 8),
                TokenUtils.tokenOf(TokenKind.TOKEN_NUMBER, 20, 3),
                TokenUtils.tokenOf(TokenKind.TOKEN_NUMBER, 24, 6),
                TokenUtils.tokenOf(TokenKind.TOKEN_NUMBER, 31, 9),
                TokenUtils.tokenOf(TokenKind.TOKEN_NUMBER, 41, 9),
                TokenUtils.tokenOf(TokenKind.TOKEN_NUMBER, 51, 4),
                TokenUtils.tokenOf(TokenKind.TOKEN_NUMBER, 56, 6),
                TokenUtils.tokenOf(TokenKind.TOKEN_NUMBER, 63, 8),
                TokenUtils.tokenOf(TokenKind.TOKEN_NUMBER, 72, 20),
                TokenUtils.tokenOf(TokenKind.TOKEN_NUMBER, 93, 2),
                TokenUtils.tokenOf(TokenKind.TOKEN_NUMBER, 96, 2),
                TokenUtils.tokenOf(TokenKind.TOKEN_NUMBER, 99, 5)
        );
    }
}
