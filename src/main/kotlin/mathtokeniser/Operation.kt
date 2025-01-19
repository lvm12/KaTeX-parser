package uk.co.purpleeagle.mathtokeniser

import kotlin.math.acos
import kotlin.math.asin
import kotlin.math.atan
import kotlin.math.cos
import kotlin.math.cosh
import kotlin.math.ln
import kotlin.math.log
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sinh
import kotlin.math.tan
import kotlin.math.tanh

data class Operation(
    val identifier: String,
    val parameters: Pair<Int, Boolean?>,
    val before: Boolean,
    val function: (expression: List<Any>, parameters: List<Any>, variables: Map<String, Double>) -> Double
)

object Operations : HashMap<String, Operation>(){

    private fun checkDataTypesAndEvaluate(value: Any, variables: Map<String, Double>): Double =
        if (value is Double) value.toDouble()
        else if (value is MathToken) value.evaluate()
        else if (value is List<*>) (value as List<Any>).evaluate(variables)
        else throw UnsupportedTypeException(value::class)

    private fun init () {
        this.putAll(hashMap)
    }

    //Basic maths
    val negate = Operation(
        identifier = "-",
        parameters = Pair(0, false),
        before = true,
        function = {expression, parameters, variables ->
            -expression.evaluate(variables)
        }
    )

    val power = Operation(
        identifier = "^",
        parameters = Pair(1, null),
        before = false,
        function = {expression, parameters, variables ->
            //println(parameters)
            val power = checkDataTypesAndEvaluate(parameters.first(), variables)

            expression.evaluate(variables).pow(power)
        },
    )

    val frac = Operation(
        identifier = "frac",
        parameters = Pair(1, true),
        before = true,
        function = {expression, parameters, variables ->
            val e = expression.evaluate(variables)
            val p = checkDataTypesAndEvaluate(parameters.first(), variables)
            p/e
        }
    )

    val divide = Operation(
        identifier = "/",
        parameters = Pair(1, null),
        before = false,
        function = { expression, parameters, variables ->
            val e = expression.evaluate(variables)
            val p = checkDataTypesAndEvaluate(parameters.first(), variables)
            e/p
        }
    )

    val multiply = Operation(
        identifier = "*",
        parameters = Pair(1, null),
        before = false,
        function = { expression, parameters, variables ->
            val e = expression.evaluate(variables)
            val p = checkDataTypesAndEvaluate(parameters.first(), variables)
            e*p
        }
    )

    val mult = Operation(
        identifier = "times",
        parameters = Pair(1, null),
        before = false,
        function = { expression, parameters, variables ->
            val e = expression.evaluate(variables)
            val p = checkDataTypesAndEvaluate(parameters.first(), variables)
            e*p
        }
    )

    //Trig functions
    val cos = Operation(
        identifier = "cos",
        parameters = Pair(0, false),
        before = true,
        function = {expression, parameters, variables ->
            cos(expression.evaluate(variables))
        }
    )

    val sin = Operation(
        identifier = "sin",
        parameters = Pair(0, false),
        before = true,
        function = {expression, parameters, variables ->
            sin(expression.evaluate(variables))
        }
    )

    val tan = Operation(
        identifier = "tan",
        parameters = Pair(0, false),
        before = true,
        function = {expression, parameters, variables ->
            tan(expression.evaluate(variables))
        }
    )

    //Inverse trig functions
    val arcsin = Operation(
        identifier = "arcsin",
        parameters = Pair(0, false),
        before = true,
        function = {expression, parameters, variables ->
            asin(expression.evaluate(variables))
        }
    )

    val arccos = Operation(
        identifier = "arccos",
        parameters = Pair(0, false),
        before = true,
        function = {expression, parameters, variables ->
            acos(expression.evaluate(variables))
        }
    )

    val arctan = Operation(
        identifier = "arctan",
        parameters = Pair(0, false),
        before = true,
        function = {expression, parameters, variables ->
            atan(expression.evaluate(variables))
        }
    )

    //Hyperbolic trig functions
    val sinh = Operation(
        identifier = "sinh",
        parameters = Pair(0, false),
        before = true,
        function = {expression, parameters, variables ->
            sinh(expression.evaluate(variables))
        }
    )

    val cosh = Operation(
        identifier = "cosh",
        parameters = Pair(0, false),
        before = true,
        function = {expression, parameters, variables ->
            cosh(expression.evaluate(variables))
        }
    )

    val tanh = Operation(
        identifier = "tanh",
        parameters = Pair(0, false),
        before = true,
        function = {expression, parameters, variables ->
            tanh(expression.evaluate(variables))
        }
    )

    val log = Operation(
        identifier = "log",
        parameters = Pair(1, false),
        before = true,
        function = {expression, parameters, variables ->
            log(expression.evaluate(variables), checkDataTypesAndEvaluate(parameters.first(), variables))
        }
    )

    val ln = Operation(
        identifier = "ln",
        parameters = Pair(0, false),
        before = true,
        function = {expression, parameters, variables ->
            ln(expression.evaluate(variables))
        }
    )

    val hashMap = hashMapOf<String, Operation>(
        "^" to power,
        "-" to negate,
        "frac" to frac,
        "/" to divide,
        "*" to multiply,
        "times" to mult,
        "sin" to sin,
        "cos" to cos,
        "tan" to tan,
        "arcsin" to arcsin,
        "arccos" to arccos,
        "arctan" to arctan,
        "sinh" to sinh,
        "cosh" to cosh,
        "tanh" to tanh,
    )
}

class UnsupportedOperationException(
    type: String,
) : Exception("Operation [[$type]] is not supported.")
