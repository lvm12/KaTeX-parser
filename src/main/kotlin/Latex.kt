package uk.co.purpleeagle

import uk.co.purpleeagle.algorithms.NewtonRaphson
import uk.co.purpleeagle.mathtokeniser.Equation
import uk.co.purpleeagle.mathtokeniser.MathScanner
import uk.co.purpleeagle.mathtokeniser.MathToken
import uk.co.purpleeagle.mathtokeniser.evaluate
import uk.co.purpleeagle.tokeniser.Scanner
import uk.co.purpleeagle.tokeniser.Token


/**
 * Main Latex parser class
 * @property main Takes a LaTeX String atm gets tokens
 */
class Latex {
    private var tex: String = ""
    private var tokens: MutableList<Token> = mutableListOf()
    private var mathTokens: List<MathToken> = listOf()
    private var equation: Equation = Equation(mutableListOf(), mutableListOf())

    fun setSource(source: String) = apply { tex = source }
    fun genTokens() = apply { tokens = Scanner(source = tex).scanTokens() }
    fun genEquation() = apply { equation = MathScanner(tokens).getEquation() }
    fun genExpressions() = apply { mathTokens = MathScanner(tokens).evaluateExpression() }
    fun newtonRaphson(
        variable: String = "x",
        accuracy: Double = 0.00001,
        h: Double = 0.00001
    ): Double = NewtonRaphson(equation, variable, h, accuracy).solve()
    fun evaluate(variables: Map<String, Double>) = mathTokens.evaluate(variables)

}