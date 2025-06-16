package uk.co.purpleeagle

import uk.co.purpleeagle.algorithms.NewtonRaphson
import uk.co.purpleeagle.constants.ConstantSets
import uk.co.purpleeagle.mathtokeniser.MathScanner
import uk.co.purpleeagle.mathtokeniser.MathToken
import uk.co.purpleeagle.mathtokeniser.Operations
import uk.co.purpleeagle.mathtokeniser.evaluate
import uk.co.purpleeagle.tokeniser.Scanner
import uk.co.purpleeagle.util.printOutAllFunctions

fun main(args: Array<String>) {
    printOutAllFunctions()
    do {
        println("Mode (n or d): ")
        val mode = readLine()?.get(0)?.lowercase()
        println("Expression: ")
        val tex = readLine()
        if (tex != null && mode != null) {
            when (mode) {
                "n" -> standardEquation(tex)
                "d" -> differentialEquation(tex)
            }
        }
    }while (tex != null)
}

fun standardEquation(tex: String) {
    println("Variable: ")
    val variable = readLine() ?: "x"
    val expression = Scanner(tex).useConstants(ConstantSets.defaultConstants).scanTokens()
    val equation = MathScanner(expression.toMutableList(), true).getEquation()
    println("x=${NewtonRaphson(equation, variable, 0.00001, 0.0001).solve()}")
}

fun differentialEquation(tex: String) {
    var count = 0
    val variables = mutableMapOf<String, Double>()
    do {
        println("Enter variable name: ")
        val variable = readLine() ?: break
        if (variable.isEmpty()) break
        println("Initial value ($count): ")
        val read = readLine() ?: break
        variables[variable] = read.toDoubleOrNull() ?: break
        count++
    }while (true)
    println("Target: ")
    val target = readLine()?.toDouble() ?: 0.0
    val expression = Scanner(tex).scanTokens()
    val equation = MathScanner(expression.toMutableList(), true).getEquation()
}