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

    private static void assertTokenKinds(Lexer lexer, @TokenKind int... kinds) {
        for (@TokenKind int kind : kinds) {
            assertEquals(kind, TokenUtils.getKind(lexer.nextToken()));
        }
        assertEquals(TokenKind.TOKEN_EOF, TokenUtils.getKind(lexer.nextToken()));
    }

    @Test
    void takeNextStringTest() {
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

        assertTokenKinds(createLexer("\"\" \"\""), TokenKind.TOKEN_STRING, TokenKind.TOKEN_STRING);
    }
}
