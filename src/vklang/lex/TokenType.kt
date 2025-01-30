package vklang.lex

enum class TokenType {
    // Key char
    L_PAREN, R_PAREN, L_BRACKET, R_BRACKET, L_BRACE, R_BRACE,
    COMMA, DOT, COLON, SEMICOLON, QUESTION,

    // Operator
    PLUS, MINUS, MULTIPLY, DIVIDE, POWER, MODULO,
    EQUAL, LESS, LESS_EQUAL, GREATER, GREATER_EQUAL, NOT_EQUAL,
    ASSIGN,
    PLUS_ASSIGN, MINUS_ASSIGN, MULTIPLY_ASSIGN, DIVIDE_ASSIGN, POWER_ASSIGN, MODULO_ASSIGN,

    // LOGIC
    AND, OR, NOT,

    // Keyword
    IF, ELIF, ELSE, TRUE, FALSE,
    FOR, WHILE, COMPLETE, INCOMPLETE, BREAK, CONTINUE,
    FUNC, CLASS, RETURN,
    VAR, VAL, IS, IN,
    IMPORT,

    // Literal
    IDENTIFIER, NUMBER, STRING,

    // Format
    SPACE, LINEBREAK, EOF,

    ;

    override fun toString(): String {
        return this.name
    }
}