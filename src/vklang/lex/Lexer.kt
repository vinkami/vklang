package vklang.lex

import vklang.Constant
import vklang.Constant.contains


class Lexer(private val text: String, fileName: String) {
    private var pos = Position(-1, 0, -1, fileName, text)
    private val currentChar: String?  // Not Char? because Regex hates Char
        get() = if (pos.index < text.length) text[pos.index].toString() else null
    private val nextChar: String?
        get() = if (pos.index + 1 < text.length) text[pos.index + 1].toString() else null

    init {pos.advance(null)}

    private fun advance() = pos.advance(currentChar)

    fun tokenize(): List<Token> {
        val tokens: MutableList<Token> = mutableListOf()

        while (currentChar != null) {
            val startPos = pos.copy()

            if (currentChar == "/" && nextChar == "/") {  // Comment
                while (currentChar != null && currentChar != "\n") {
                    advance()
                }
                if (currentChar == null) break
            }

            val section = when (currentChar!!) {
                in Regex("[\\d.]") -> makeNumber()
                in Regex("[a-zA-Z]") -> makeIdentifier()
                in Regex("[\'\"]") -> makeString()
                in Regex("\$") -> makeVariable()
                else -> {
                    val c = currentChar!!
                    advance()
                    c
                }
            }

            val token = Token(section, startPos, pos.copy())

            tokens += token
        }

        // EOF token
        advance()
        tokens += Token(TokenType.EOF, "", pos.copy(), pos.copy())

        return combineTokens(tokens)  // Combine tokens like >= and ++
    }

    private fun makeNumber(): String {  // Note: Doesn't care the decimal points' number and placement
        var section = ""

        while (currentChar != null && Regex("[\\d.]") matches currentChar!!) {
            section += currentChar
            advance()
        }

        return section
    }

    private fun makeIdentifier(): String {
        var section = ""

        while (currentChar != null && Regex("[a-zA-Z]") matches currentChar!!) {
            section += currentChar
            advance()
        }

        return section
    }

    private fun makeString(): String {
        var section = "$currentChar"
        val quote = currentChar!!
        advance()

        while (currentChar != null && (currentChar != quote || pos.index == 0 || text[pos.index - 1] == '\\')) {
            section += currentChar
            advance()
        }

        if (currentChar != null) {
            section += quote
            advance()  // Current char is the quote, need to advance 1 more time
        }

        return section
    }

    private fun makeVariable(): String {
        var section = "$currentChar"
        advance()

        while (currentChar != null && Regex("[a-zA-Z0-9_]") matches currentChar!!) {
            section += currentChar
            advance()
        }

        return section
    }

    /**
     * For tokens like >=, **, etc. they are not combined in the first lexing step, here will do it.
     * Working principle: loop all tokens, replace it and the next token if combinable, increment i otherwise
     *
     * @param tokens The tokens to be combined
     * @return The combined tokens (duh)
     */
    private fun combineTokens(tokens: MutableList<Token>): MutableList<Token> {
        // For tokens like >=, ++, etc. they are not combined in the first lexing step, here will do it.
        // Working principle: loop all tokens, replace it and the next token if combinable, increment i otherwise

        var i = 0

        while (i < tokens.size) {
            val currentToken = tokens[i]
            if (currentToken.type == TokenType.EOF) {
                return tokens.dropLast(tokens.size - i - 1) as MutableList<Token>
            }
            val nextToken = tokens[i + 1]

            if (Constant.combinableTokens.contains(currentToken.type to nextToken.type)) {
                val procedure = Constant.combinableTokens[currentToken.type to nextToken.type]!!

                val newTT = procedure.first
                val newValue = procedure.second(currentToken, nextToken)


                tokens[i] = Token(newTT, newValue, currentToken.startPos, nextToken.endPos)
                tokens.removeAt(i + 1)
            } else { i++ }  // Some tokens can combine multiple times, so don't increment if a combination happens
        }

        return tokens
    }
}