package vklang

import vklang.lex.Position
import vklang.lex.Token
import vklang.lex.TokenType
import vklang.lex.TokenType.*


@Suppress("MemberVisibilityCanBePrivate")
internal object Constant {
    val arithmeticOp: List<TokenType> = listOf(
        PLUS, MINUS,
        MULTIPLY, DIVIDE,
        MODULO,
        POWER,
    )

    val comparativeOp: List<TokenType> = listOf(
        EQUAL, NOT_EQUAL,
        LESS, LESS_EQUAL,
        GREATER, GREATER_EQUAL,
    )

    val definitiveOp: List<TokenType> = listOf(
        ASSIGN,
//        TokenType.INCREMENT, TokenType.DECREMENT,
        PLUS_ASSIGN, MINUS_ASSIGN,
        MULTIPLY_ASSIGN, DIVIDE_ASSIGN,
        MODULO_ASSIGN, POWER_ASSIGN,
    )

    val logicalOp: List<TokenType> = listOf(
        AND, OR, NOT,
    )

    val binaryOps: List<TokenType> = arithmeticOp + comparativeOp + definitiveOp + logicalOp - listOf(NOT) + listOf(DOT, L_PAREN)

    val bracket: Map<TokenType, TokenType> = mapOf(
        L_PAREN to R_PAREN,
        L_BRACKET to R_BRACKET,
        L_BRACE to R_BRACE,
    )

    val fixValuedTokenPair: Map<String, TokenType> = mapOf(
        // Key char
        "(" to L_PAREN,
        ")" to R_PAREN,
        "[" to L_BRACKET,
        "]" to R_BRACKET,
        "{" to L_BRACE,
        "}" to R_BRACE,
        "," to COMMA,
        "." to DOT,
        ":" to COLON,
        ";" to SEMICOLON,
        "?" to QUESTION,
        "#" to HASH,
        "^" to CARET,
        "$" to DOLLAR,
        "~" to TILDE,
        "`" to GRAVE,

        // Arithmetic operator
        "+" to PLUS,
        "-" to MINUS,
        "*" to MULTIPLY,
        "/" to DIVIDE,
        "**" to POWER,
        "%" to MODULO,

        // Comparative operator
        "==" to EQUAL,  // Combined from 2 ASSIGN
        "!=" to NOT_EQUAL,  // Combined from NOT and ASSIGN
        "<" to LESS,
        ">" to GREATER,
        "<=" to LESS_EQUAL,  // Combined from LESS and ASSIGN
        ">=" to GREATER_EQUAL,  // Combined from GREATER and ASSIGN

        // Definitive operator
        "=" to ASSIGN,
        "+=" to PLUS_ASSIGN,  // Combined from PLUS and ASSIGN
        "-=" to MINUS_ASSIGN,  // Combined from MINUS and ASSIGN
        "*=" to MULTIPLY_ASSIGN,  // Combined from MULTIPLY and ASSIGN
        "/=" to DIVIDE_ASSIGN,  // Combined from DIVIDE and ASSIGN
        "%=" to MODULO_ASSIGN,  // Combined from MODULO and ASSIGN
        "**=" to POWER_ASSIGN,  // Combined from POWER and ASSIGN

        // Logic operator
        "&" to AND,
        "|" to OR,
        "!" to NOT,

        // Keyword
        "if" to IF,
        "elif" to ELIF,
        "else" to ELSE,
        "true" to TRUE,
        "false" to FALSE,

        "for" to FOR,
        "while" to WHILE,
        "complete" to COMPLETE,
        "incomplete" to INCOMPLETE,
        "break" to BREAK,
        "continue" to CONTINUE,

        "return" to RETURN,
        "fun" to FUNC,
        "class" to CLASS,

        "var" to VAR,
        "val" to VAL,
        "is" to IS,
        "in" to IN,
        "import" to IMPORT
    )

    val bindingPower: Map<TokenType, Pair<Int, Int>> = mapOf(
        // Used in Pratt parser
        PLUS to Pair(104, 105), MINUS to Pair(104, 105),
        MULTIPLY to Pair(106, 107), DIVIDE to Pair(106, 107),
        MODULO to Pair(108, 109), POWER to Pair(110, 111),

        EQUAL to Pair(52, 53), NOT_EQUAL to Pair(52, 53),
        LESS to Pair(52, 53), LESS_EQUAL to Pair(52, 53),
        GREATER to Pair(52, 53), GREATER_EQUAL to Pair(52, 53),

        AND to Pair(40, 41), OR to Pair(40, 41),

        ASSIGN to Pair(150, 1),
        PLUS_ASSIGN to Pair(150, 1), MINUS_ASSIGN to Pair(150, 1),
        MULTIPLY_ASSIGN to Pair(150, 1), DIVIDE_ASSIGN to Pair(150, 1),
        MODULO_ASSIGN to Pair(150, 1), POWER_ASSIGN to Pair(150, 1),

    )

    val combinableTokens: Map<Pair<TokenType, TokenType>, Pair<TokenType, (Token, Token) -> String>> = mapOf(
//        Pair(TokenType.PLUS, TokenType.PLUS) to Pair(TokenType.INCREMENT) { _, _ -> "++" },
        Pair(PLUS, ASSIGN) to Pair(PLUS_ASSIGN) { _, _ -> "+=" },

//        Pair(TokenType.MINUS, TokenType.MINUS) to Pair(TokenType.DECREMENT) { _, _ -> "--" },
        Pair(MINUS, ASSIGN) to Pair(MINUS_ASSIGN) { _, _ -> "-=" },

        Pair(MULTIPLY, MULTIPLY) to Pair(POWER) { _, _ -> "**" },
        Pair(MULTIPLY, ASSIGN) to Pair(MULTIPLY_ASSIGN) { _, _ -> "*=" },

        Pair(POWER, ASSIGN) to Pair(POWER_ASSIGN) { _, _ -> "**=" },

        Pair(MODULO, ASSIGN) to Pair(MODULO_ASSIGN) { _, _ -> "%=" },

        Pair(ASSIGN, ASSIGN) to Pair(EQUAL) {_, _ -> "==" },
        Pair(LESS, ASSIGN) to Pair(LESS_EQUAL) { _, _ -> "<=" },
        Pair(GREATER, ASSIGN) to Pair(GREATER_EQUAL) { _, _ -> ">=" },
        Pair(NOT, ASSIGN) to Pair(NOT_EQUAL) { _, _ -> "!=" },

        Pair(SPACE, SPACE) to Pair(SPACE) { t1, t2 -> t1.value + t2.value },
        Pair(LINEBREAK, LINEBREAK) to Pair(LINEBREAK) { t1, t2 -> t1.value + t2.value },

        Pair(QUESTION, QUESTION) to Pair(IF) { _, _ -> "??" },
        Pair(OR, QUESTION) to Pair(ELIF) { _, _ -> "|?" },
        Pair(OR, OR) to Pair(ELSE) { _, _ -> "||" },

        Pair(QUESTION, CARET) to Pair(WHILE) { _, _ -> "?^" },
        Pair(QUESTION, PLUS) to Pair(COMPLETE) { _, _ -> "?+" },
        Pair(QUESTION, MINUS) to Pair(INCOMPLETE) { _, _ -> "?-" },

        Pair(HASH, LESS) to Pair(BREAK) { _, _ -> "#<" },
        Pair(HASH, GREATER) to Pair(CONTINUE) { _, _ -> "#>" },
        Pair(HASH, CARET) to Pair(RETURN) { _, _ -> "#^" },

        Pair(DOLLAR, NOT) to Pair(VAL) { _, _ -> "$!" },
    )

    val loopCompleteTT: List<TokenType> = listOf(
        COMPLETE, INCOMPLETE
    )

    /**
     * Provides `String in Regex` syntax, which can be used in when statements
     *
     * @param other the string to be matched with the regex
     */
    operator fun Regex.contains(other: CharSequence): Boolean = this.matches(other)

    val builtinPos = Position(-1, -1, -1, "builtin", "builtin")
}