package uk.co.purpleeagle.mathtokeniser

import uk.co.purpleeagle.h
import uk.co.purpleeagle.util.get
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

/**
 * @property identifier - Separates different functions
 * @property parameters - How many things are needed by the function before the expression,
 * slightly funky with \frac, as the denominator is treated as the expression. The boolean decides
 * whether an expression is required or not.
 * @property before - Whether the function comes before or after the expression it acts on
 * @property function - Definition of how the function acts
 */
data class Operation(
    val identifier: String,
    val parameters: Pair<Int, Boolean>,
    val before: Boolean,
    val function: (expression: List<Any>, parameters: List<Any>, variables: Map<String, Double>) -> Double
)

object Operations : HashMap<String, Operation>(){

    private fun checkDataTypesAndEvaluate(value: Any, variables: Map<String, Double>): Double =
        if (value is Double) value.toDouble()
        else if (value is MathToken) value.evaluate(variables)
        else if (value is List<*>) (value as List<Any>).evaluate(variables)
        else throw UnsupportedTypeException(value::class)

    private fun init () {
        this.putAll(hashMap)
    }

    /**Represents d/dx
     * No parameters needed
     */
    val differentiate = Operation(
        identifier = "differentiate",
        parameters = Pair(1, true),
        before = true,
        function = { expression, parameters, variables ->
            //println(parameters)
            //println(variables)
            val fx = expression.evaluate(variables)
            val hvars = variables.toMutableMap()
            variables.forEach { key, value ->
                hvars[key] = value+h
            }
            val fxh = expression.evaluate(hvars)
            (fxh-fx)/h
        }
    )

    /**Represents d^{k}y/dx^{k}
     * Parameters:
     * 0 - Order
     * 1 - Numerator variable
     * 2 - Denominator variable
     */
    val diffequation = Operation(
        identifier = "diffequation",
        parameters = Pair(3, false),
        before = false,
        function = { expression, parameters, variables ->
            variables[variables.keys[checkDataTypesAndEvaluate(parameters[0], variables=variables).toInt()]] ?: 0.0
        }
    )

    /**Represents -f(x)
     * No parameters needed
     */
    val negate = Operation(
        identifier = "-",
        parameters = Pair(0, true),
        before = true,
        function = {expression, parameters, variables ->
            -expression.evaluate(variables)
        }
    )

    /**Represents f(x)^g(x)
     * Parameters:
     * 0 - Power (g(x))
     */
    val power = Operation(
        identifier = "^",
        parameters = Pair(1, true),
        before = false,
        function = {expression, parameters, variables ->
            //println(parameters)
            val power = checkDataTypesAndEvaluate(parameters.first(), variables)

            expression.evaluate(variables).pow(power)
        },
    )

    /**Represents f(x)/g(x)
     * Parameters:
     * 0 - Numerator
     * Expression - Denominator
     */
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

    /**Represents f(x)/g(x)
     * Parameters:
     * 0 - Denominator
     * Expression - Numerator
     */
    val divide = Operation(
        identifier = "/",
        parameters = Pair(1, true),
        before = false,
        function = { expression, parameters, variables ->
            val e = expression.evaluate(variables)
            val p = checkDataTypesAndEvaluate(parameters.first(), variables)
            e/p
        }
    )

    /**Represents f(x)*g(x)
     * Parameters:
     * 0 - f(x)
     * Expression - g(x)
     */
    val multiply = Operation(
        identifier = "*",
        parameters = Pair(1, true),
        before = false,
        function = { expression, parameters, variables ->
            val e = expression.evaluate(variables)
            val p = checkDataTypesAndEvaluate(parameters.first(), variables)
            e*p
        }
    )

    /**Represents f(x)*g(x)
     * Parameters:
     * 0 - f(x)
     * Expression - g(x)
     */
    val mult = Operation(
        identifier = "times",
        parameters = Pair(1, true),
        before = false,
        function = { expression, parameters, variables ->
            val e = expression.evaluate(variables)
            val p = checkDataTypesAndEvaluate(parameters.first(), variables)
            e*p
        }
    )

    //Trig functions
    /**Represents cos(f(x))
     * No parameters required
     */
    val cos = Operation(
        identifier = "cos",
        parameters = Pair(0, true),
        before = true,
        function = {expression, parameters, variables ->
            cos(expression.evaluate(variables))
        }
    )

    /**Represents sin(f(x))
     * No parameters required
     */
    val sin = Operation(
        identifier = "sin",
        parameters = Pair(0, true),
        before = true,
        function = {expression, parameters, variables ->
            sin(expression.evaluate(variables))
        }
    )

    /**Represents tan(f(x))
     * No parameters required
     */
    val tan = Operation(
        identifier = "tan",
        parameters = Pair(0, true),
        before = true,
        function = {expression, parameters, variables ->
            tan(expression.evaluate(variables))
        }
    )

    /**Represents arcsin(f(x))
     * No parameters required
     */
    val arcsin = Operation(
        identifier = "arcsin",
        parameters = Pair(0, true),
        before = true,
        function = {expression, parameters, variables ->
            asin(expression.evaluate(variables))
        }
    )

    /**Represents arccos(f(x))
     * No parameters required
     */
    val arccos = Operation(
        identifier = "arccos",
        parameters = Pair(0, true),
        before = true,
        function = {expression, parameters, variables ->
            acos(expression.evaluate(variables))
        }
    )

    /**Represents arctan(f(x))
     * No parameters required
     */
    val arctan = Operation(
        identifier = "arctan",
        parameters = Pair(0, true),
        before = true,
        function = {expression, parameters, variables ->
            atan(expression.evaluate(variables))
        }
    )

    //Hyperbolic trig functions
    /**Represents sinh(f(x))
     * No parameters required
     */
    val sinh = Operation(
        identifier = "sinh",
        parameters = Pair(0, true),
        before = true,
        function = {expression, parameters, variables ->
            sinh(expression.evaluate(variables))
        }
    )

    /**Represents cosh(f(x))
     * No parameters required
     */
    val cosh = Operation(
        identifier = "cosh",
        parameters = Pair(0, true),
        before = true,
        function = {expression, parameters, variables ->
            cosh(expression.evaluate(variables))
        }
    )

    /**Represents tanh(f(x))
     * No parameters required
     */
    val tanh = Operation(
        identifier = "tanh",
        parameters = Pair(0, true),
        before = true,
        function = {expression, parameters, variables ->
            tanh(expression.evaluate(variables))
        }
    )

    /**Represents log_{g(x)}(f(x))
     * Parameters:
     * 0 - Base of the log (g(x))
     */
    val log = Operation(
        identifier = "log",
        parameters = Pair(1, true),
        before = true,
        function = {expression, parameters, variables ->
            log(expression.evaluate(variables), checkDataTypesAndEvaluate(parameters.first(), variables))
        }
    )

    /**Represents ln(f(x))
     * No parameters required
     */
    val ln = Operation(
        identifier = "ln",
        parameters = Pair(0, true),
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
        "log" to log,
        "ln" to ln,
        "differentiate" to differentiate,
        "diffequation" to diffequation,
    )
}

class UnsupportedOperationException(
    type: String,
) : Exception("Operation [[$type]] is not supported.")
