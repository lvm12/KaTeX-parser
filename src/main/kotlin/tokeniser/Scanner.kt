package uk.co.purpleeagle.tokeniser

import uk.co.purpleeagle.constants.ConstantSets
import uk.co.purpleeagle.mathtokeniser.Operations

/**
 * Scanner to get every token
 * @property scanTokens scan tokens
 * @property source The given LaTeX string
 * @property tokens The list of currently found tokens
 */
class Scanner(
    val source: String,
) {

    private val constants: Map<String, Double> = emptyMap()
    fun useConstants(vararg constants: Pair<String, Double>) = apply { this.constants + mapOf(*constants) }
    fun useConstants(vararg constants: Map<String, Double>) = apply{
        constants.forEach { this.constants + it }
    }

    val tokens: ArrayList<Token> = arrayListOf()

    fun reset() {
        tokens.clear()
        start = 0
        current = 0
    }

    //Keep track of the current position in the latex statement
    private var start: Int = 0
    private var current: Int = 0
    private var addRightBracketOnNext = false

    fun scanTokens(): MutableList<Token> {
        addToken(TokenType.PLUS)
        while (!isAtEnd()) {
            start = current
            scanToken()
        }
        addToken(TokenType.PLUS)
        return tokens
    }

    /**
     * Checks whether the scanner has reached the end of the string
     */
    private fun isAtEnd() = current >= source.length

    private fun isDigit(char: Char) = char in '0'..'9'
    private fun isLetter(char: Char) = char in 'a'..'z' || char in 'A'..'Z'
    private fun isAlphaNumeric(char: Char) = isLetter(char) || isDigit(char)

    private fun scanToken() {
        val c = advance()
        when (c) {
            '{' -> addToken(TokenType.LEFT_BRACE)
            '}' -> addToken(TokenType.RIGHT_BRACE)
            '(' -> addToken(TokenType.LEFT_BRACKET)
            ')' -> addToken(TokenType.RIGHT_BRACKET)
            '+' -> addToken(TokenType.PLUS)
            //Added before plus, use it for error stuff
            '-' -> {
                if (tokens.last().tokenType != TokenType.PLUS) addToken(TokenType.PLUS)
                addToken(TokenType.OPERATION)
            }
            '*' -> addToken(TokenType.OPERATION)
            '/' -> addToken(TokenType.OPERATION)
            '^' -> addToken(TokenType.POWER)
            //Added before and after pluses, use it for error stuff
            '=' -> {
                if (tokens.last().tokenType != TokenType.PLUS) addToken(TokenType.PLUS)
                addToken(TokenType.EQUAL);addToken(TokenType.PLUS)}
            //Removed to improve math scanner
            //'\\' -> addToken(TokenType.BACK_SLASH)
            '_' -> {addRightBracketOnNext = true; addToken(TokenType.LEFT_BRACE); return}
            in listOf(' ', '\r', '\t', '\n', '\\') -> {}
            else -> {
                if (isDigit(c)) {
                    number()
                }else if (isLetter(c)) {
                    identifier()
                }else{
                    println("ERROR at $c")
                }
            }
        }
        if (addRightBracketOnNext && tokens.last().tokenType != TokenType.LEFT_BRACE) {
            addToken(TokenType.RIGHT_BRACE)
            addRightBracketOnNext = false
        }else{
            addRightBracketOnNext = false
        }
    }

    /**
     * Consumes the next character of the string
     */
    private fun advance(): Char {
        return source[current++]
    }

    /**
     * Returns the next character without consuming it
     */
    private fun peek(): Char {
        if (isAtEnd()) return '\u0000'
        return source[current]
    }

    /**
     * Returns the next-next character without consuming it
     */
    private fun peekNext(): Char {
        if (current + 1 >= source.length) return '\u0000'
        return source[current + 1]
    }

    /**
     * Handles number literals
     */
    private fun number() {
        while (isDigit(peek())) advance()

        if (peek() == '.' && isDigit(peekNext())) {
            advance()

            while (isDigit(peek())) advance()
        }

        addToken(TokenType.NUMBER, source.substring(start, current).toDouble())
    }

    /**
     * Handles identifier literals
     */
    private fun identifier() {
        while (isAlphaNumeric(peek())) advance()

        val substring = source.substring(start, current)
        if (constants[substring] != null) {
            addToken(TokenType.NUMBER, constants[substring])
        }else if (Operations.hashMap.containsKey(substring)) {
            addToken(TokenType.OPERATION)
        }else {
            addToken(TokenType.IDENTIFIER)
        }
    }

    private fun addToken(tokenType: TokenType) {
        addToken(tokenType, null)
    }

    private fun addToken(tokenType: TokenType, literal: Any? ) {
        val string = source.substring(start, current)
        tokens.add(Token(tokenType, string, literal))
    }
}