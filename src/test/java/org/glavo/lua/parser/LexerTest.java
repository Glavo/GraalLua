package org.glavo.lua.parser;

import com.oracle.truffle.api.source.Source;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

public class LexerTest {
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
        Lexer lexer = new Lexer(Source.newBuilder("lua", "--[==[  ]==]str", "Test").build());
        lexer.skipWhiteSpacesAndComment();
        assertEquals(12, lexer.getOffset());
    }
}
