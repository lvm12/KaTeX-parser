package uk.co.purpleeagle

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import uk.co.purpleeagle.mathtokeniser.AltMathScanner
import uk.co.purpleeagle.mathtokeniser.MathScanner
import uk.co.purpleeagle.mathtokeniser.evaluate
import uk.co.purpleeagle.tokeniser.Scanner
import uk.co.purpleeagle.tokeniser.TokenType
import uk.co.purpleeagle.util.removeAtIndexes
import uk.co.purpleeagle.util.repeatWithCreate

fun main(args: Array<String>) {
//    do {
//        println("Enter expression")
//        var input = readLine()
//
//        if (input != null) {
//            input += "=0"
//            val tokens = Scanner(input).scanTokens().toMutableList()
//            val mathScanner = MathScanner(tokens)
//            println(mathScanner.generateExpressions(mathScanner.lhs).let {
//                var total = 0.0
//                it.forEach {
//                    total += it()
//                }
//                total
//            })
//        }
//
//    } while (input != null)

    do {
        val tex = readLine()
        if (tex != null) {
            val tokens = Scanner(tex).scanTokens()
            val mathScanner = AltMathScanner(tokens.takeWhile { it.tokenType != TokenType.EQUAL }.toMutableList())
            val expressions = mathScanner.evaluateExpression()
            println("------")
            println(expressions.evaluate(emptyMap()))
        }
    }while (tex != null)

}