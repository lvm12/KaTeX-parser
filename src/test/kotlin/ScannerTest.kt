import org.junit.jupiter.api.Test
import uk.co.purpleeagle.tokeniser.Scanner
import uk.co.purpleeagle.tokeniser.Token
import uk.co.purpleeagle.tokeniser.TokenType
import kotlin.test.assertEquals

class ScannerTest {

    private val testScanner = Scanner("\\Omega^{2} + 3\\Omega + 5.5 = 6")

    @Test
    fun scanTokens() {
        val test = "\\Omega^{2} + 3\\Omega + 5.5 = 6"
        val expected = listOf(
            Token(
                tokenType = TokenType.BACK_SLASH,
                lexeme = "\\",
                literal = null
            ),
            Token(
                tokenType = TokenType.IDENTIFIER,
                lexeme = "Omega",
                literal = null
            ),
            Token(
                tokenType = TokenType.POWER,
                lexeme = "^",
                literal = null
            ),
            Token(
                tokenType = TokenType.LEFT_BRACE,
                lexeme = "{",
                literal = null
            ),
            Token(
                tokenType = TokenType.NUMBER,
                lexeme = "2",
                literal = 2.0
            ),
            Token(
                tokenType = TokenType.RIGHT_BRACE,
                lexeme = "}",
                literal = null
            ),
            Token(
                tokenType = TokenType.PLUS,
                lexeme = "+",
                literal = null
            ),
            Token(
                tokenType = TokenType.NUMBER,
                lexeme = "3",
                literal = 3.0
            ),
            Token(
                tokenType = TokenType.BACK_SLASH,
                lexeme = "\\",
                literal = null
            ),
            Token(
                tokenType = TokenType.IDENTIFIER,
                lexeme = "Omega",
                literal = null
            ),
            Token(
                tokenType = TokenType.PLUS,
                lexeme = "+",
                literal = null
            ),
            Token(
                tokenType = TokenType.NUMBER,
                lexeme = "5.5",
                literal = 5.5
            ),
            Token(
                tokenType = TokenType.EQUAL,
                lexeme = "=",
                literal = null
            ),
            Token(
                tokenType = TokenType.NUMBER,
                lexeme = "6",
                literal = 6.0
            ),
            Token(
                tokenType = TokenType.END,
                lexeme = "",
                literal = null
            )
        )

        assertEquals(expected, testScanner.scanTokens())
    }

}