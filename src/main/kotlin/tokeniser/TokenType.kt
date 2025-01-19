package uk.co.purpleeagle.tokeniser

enum class TokenType {
    RIGHT_BRACE,
    LEFT_BRACE,
    RIGHT_BRACKET,
    LEFT_BRACKET,
    BACK_SLASH,

    PLUS,
    MINUS,
    MULTIPLY,
    DIVIDE,
    POWER,
    EQUAL,

    NUMBER,
    IDENTIFIER,
    OPERATION,

    END,
    REPLACED
}