package uk.co.purpleeagle.algorithms

import uk.co.purpleeagle.mathtokeniser.Equation
import uk.co.purpleeagle.mathtokeniser.MathToken
import uk.co.purpleeagle.mathtokeniser.Operations
import uk.co.purpleeagle.mathtokeniser.evaluate
import kotlin.math.absoluteValue

class NewtonRaphson (
    var equation: Equation,
    private var variable: String,
    private var h: Double,
    private var accuracy: Double,
): Algorithm(equation) {

    init {
        equation = equation.copy(
            lhs = equation.lhs + MathToken(
                coefficient = null,
                expression = equation.rhs,
                parameters = listOf(),
                function = Operations.negate.function
            )
        )
    }

    override fun solve(): Double {
        val fx = equation.lhs
        val fdashx = MathToken(
            coefficient = null,
            expression = equation.lhs,
            parameters = listOf(h),
            function = Operations.differentiate.function
        )

        var result =10.0
        var new = 0.0
        while ((result-new).absoluteValue > accuracy) {
            result = new
            val fxvalue = fx.evaluate(mapOf(variable to new))
            val fdashxvalue = fdashx.evaluate(mapOf(variable to new))
            new = new - (fxvalue/fdashxvalue)
        }
        return new
    }

    tailrec fun recursiveSolve(x: Double): Double {
        val fx = equation.lhs
        val fdashx = MathToken(
            coefficient = null,
            expression = equation.lhs,
            parameters = listOf(h),
            function = Operations.differentiate.function
        )
        val result = x - (fx.evaluate(mapOf(variable to x))/fdashx.evaluate(mapOf(variable to x)))
        if ((result-x).absoluteValue < accuracy) return result
        return recursiveSolve(result)
    }
}