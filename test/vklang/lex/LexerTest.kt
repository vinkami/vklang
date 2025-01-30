package vklang.lex

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class LexerTest {
    private val fileName = "<test>"

    private fun compareTokens(expected: Token, actual: Token, message: String? = null, checkPos: Boolean = true) {
        assertEquals(expected.type, actual.type, message)
        assertEquals(expected.value, actual.value, message)
        if (checkPos) {
            assertEquals(expected.startPos, actual.startPos, message)
            assertEquals(expected.endPos, actual.endPos, message)
        }
    }

    private fun compareTokens(expected: List<Token>, actual: List<Token>, message: String? = null) {
        assertEquals(expected.size, actual.size)
        for (i in expected.indices) {
            compareTokens(expected[i], actual[i], message)
        }
    }

    @Test
    fun string() {
        val text = "\"Hello, world!\""
        val actual: List<Token> = Lexer(text, fileName).tokenize()
        val expected = listOf(
            Token(TokenType.STRING, "Hello, world!", Position(0, 0, 0, fileName, text), Position(15, 0, 15, fileName, text)),
            Token(TokenType.EOF, "", Position(16, 0, 16, fileName, text), Position(16, 0, 16, fileName, text))
        )

        compareTokens(expected, actual, "String tokenization failed")
    }

    @Test
    fun number() {
        val text = "12.3"
        val actual: List<Token> = Lexer(text, fileName).tokenize()
        val expected = listOf(
            Token(TokenType.NUMBER, "12.3", Position(0, 0, 0, fileName, text), Position(4, 0, 4, fileName, text)),
            Token(TokenType.EOF, "", Position(5, 0, 5, fileName, text), Position(5, 0, 5, fileName, text))
        )

        compareTokens(expected, actual, "Number tokenization failed")
    }
}