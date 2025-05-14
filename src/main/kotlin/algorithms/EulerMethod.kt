package uk.co.purpleeagle.algorithms

import uk.co.purpleeagle.mathtokeniser.Equation
import uk.co.purpleeagle.mathtokeniser.MathScanner
import uk.co.purpleeagle.mathtokeniser.MathToken
import uk.co.purpleeagle.mathtokeniser.Operations
import uk.co.purpleeagle.mathtokeniser.evaluate
import uk.co.purpleeagle.util.get

class NoDifferentialEquationException : Exception()

class EulerMethod(
    private var equation: Equation,
    private val delta: Double,
    private val variables: MutableMap<String, Double>,
    private val target: Double
):  Algorithm(equation) {
    var order: Int = 0
    var maxIndex: Int = 0
    init {
        equation = equation.copy(
            lhs = (equation.lhs + MathToken(
                coefficient = null,
                expression = equation.rhs,
                parameters = listOf(),
                function = Operations.negate.function
            )).toMutableList()
        )
        val orderTriple = equation.lhs.getOrder()
        order = orderTriple.second
        maxIndex = orderTriple.third

        equation.lhs.forEachIndexed { index, token ->
            if (index != maxIndex) {
                equation.rhs.add(
                    MathToken(
                        coefficient = null,
                        expression = listOf(token),
                        parameters = listOf(),
                        function = Operations.negate.function
                    )
                )
            }
        }
        equation = equation.copy(
            rhs = mutableListOf(MathToken(
                coefficient = null,
                expression = equation.rhs,
                parameters = listOf(orderTriple.first),
                function = Operations.frac.function
            ))
        )

    }

    override fun solve(): Double = solveWithEmits {}

    fun solveWithEmits(onEmit: (Double) -> Unit): Double{
        if ((variables[variables.keys.first()] ?: 0.0) >= target) return variables[variables.keys.first()] ?: 0.0
        while ((variables[variables.keys.first()] ?: 0.0) < target){
            val old = variables
            for (i in 0..order-1) {
                variables[variables.keys[i]] = (variables[variables.keys[i]] ?: 0.0) + (variables[variables.keys[i+1]] ?: 0.0) * delta
            }
            variables[variables.keys.last()] = (variables[variables.keys.last()] ?: 0.0) +
                    delta * equation.rhs.evaluate(old)
        }
        return variables[variables.keys[1]] ?: 0.0
    }

    private fun MathToken.extractDifferentialEquation(): Pair<MathToken, Triple<Double, String, String>>{
        var order = 0.0
        var top = ""
        var bottom = ""

        val other = mutableListOf<MathToken>()
        var current = this
        while (true){
            if (current.function == Operations.diffequation.function){
                order = (current.parameters[0] as MathToken).evaluate(emptyMap())
                top = current.parameters[1].toString()
                bottom = current.parameters[2].toString()

                if (current.coefficient != null) {
                    other.add(MathToken(
                        coefficient = null,
                        expression = listOf(current.coefficient),
                        parameters = listOf(),
                        function = null
                    ))
                }
                break
            }else{
                other.add(current.copy(coefficient = null))
                if (current.coefficient != null) {
                    current = current.coefficient
                }else{
                    throw NoDifferentialEquationException()
                }
            }
        }
        return Pair(MathScanner(mutableListOf()).assembleMathTokens(other), Triple(order, top, bottom))
    }

    private fun List<MathToken>.getOrder(): Triple<MathToken, Int, Int> {
        var max = 0
        var index = 0
        var other: MathToken? = null
        for (i in equation.lhs.indices) {
            val result = try {
                equation.lhs[i].extractDifferentialEquation()
            }catch (e: NoDifferentialEquationException){continue}
            println(result)
            if (result.second.first>max){
                max = result.second.first.toInt()
                index = i
                other = result.first
            }
        }
        return Triple(other!!, max, index)
    }
}