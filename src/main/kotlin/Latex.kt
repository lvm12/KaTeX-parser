package uk.co.purpleeagle

import uk.co.purpleeagle.tokeniser.Scanner


/**
 * Main Latex parser class
 * @property main Takes a LaTeX String atm gets tokens
 */
class Latex {
    companion object {
        @JvmStatic
        fun main(string: String) {
            val scanner = Scanner(string)
            val tokens = scanner.scanTokens()

            for (token in tokens) {
                println(token)
            }
        }
    }
}