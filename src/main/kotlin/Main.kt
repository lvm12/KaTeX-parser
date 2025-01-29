package uk.co.purpleeagle

import uk.co.purpleeagle.algorithms.NewtonRaphson
import uk.co.purpleeagle.mathtokeniser.MathScanner
import uk.co.purpleeagle.mathtokeniser.MathToken
import uk.co.purpleeagle.mathtokeniser.Operations
import uk.co.purpleeagle.mathtokeniser.evaluate
import uk.co.purpleeagle.tokeniser.Scanner

fun main(args: Array<String>) {

    do {
        println("Expression: ")
        val tex = readLine()
//        println("X value : ")
//        val x = readLine()
        if (tex != null /*&& x != null*/) {
            val expression = Scanner(tex).scanTokens()
            //val varTokens = Scanner(x).scanTokens()
            //val varValue = MathScanner.evaluateExpression(varTokens).evaluate(emptyMap())
            //val mathTokens = MathScanner.evaluateExpression(expression)
            val equation = MathScanner(expression.toMutableList()).getEquation()
            println("x=${NewtonRaphson(equation, "x", 0.00001, 0.0001).solve()}")
//            val diff = MathToken(
//                coefficient = null,
//                expression = mathTokens,
//                parameters = listOf(0.1),
//                function = Operations.differentiate.function
//            )
//            println(diff.evaluate(mapOf("x" to 0.1)))
        //println(mathTokens.evaluate(mapOf("x" to varValue)))
        }
    }while (tex != null)
}