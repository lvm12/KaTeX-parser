package uk.co.purpleeagle.mathtokeniser

import kotlin.reflect.KClass

/**Class to represent a mathematical expression
 * @property coefficient What the expression is multiplied by. Set to null to not multiply by anything.
 * @property expression List of MathTokens, Integers or Strings, error if neither. This is something like 3x + 1.
 * @property variables Identifies variables in the expression to be used by the function
 * @property parameters List of doubles that may be required by the function
 * @property function Equivalent of the mathematical f(x), simply represents a mathematical operation
 * @property evaluate Evaluates a token to a double
 * @property invoke Shorthand for the evaluate method
 */


data class MathToken(
    val coefficient : MathToken?,
    val expression : List<Any>,
    val parameters: List<Any>,
    val function : ((expression: List<Any>, parameters: List<Any>, variables: Map<String, Double>) -> Double)?,
) {
    init {
        expression.forEach {
            if (!(it is MathToken || it is Double || it is String)) {
                throw IllegalArgumentException("Expression does not match expected type ${it.javaClass}")
            }
        }
    }

    override fun toString(): String {
        return "MathToken(coeffient = $coefficient, expression = $expression, parameters = $parameters, function = ${run { 
            var str = "null"
            val operations = Operations.hashMap.values
            operations.forEach { 
                if (it.function == function) str = it.identifier
            }
            str
        }})"
    }

    fun evaluate(variables: Map<String, Double>): Double {
        return if (coefficient == null) {
            if (function != null) {
                function(expression, parameters, variables)
            }else {
                expression.evaluate(variables)
            }
        }else {
            if (function != null) {
                coefficient.evaluate(variables) * function(expression, parameters, variables)
            }else {
                var total = 0.0
                expression.forEach {
                    if (it is MathToken) {
                        total += it.evaluate(variables)
                    }else if (it is Double) {
                        total += it
                    }else if (it is String) {
                        total += variables[it] ?: throw IllegalVariableException(it)
                    }else{
                        UnsupportedTypeException(it::class)
                    }
                }
                return coefficient.evaluate(variables) * total
            }
        }
    }

    operator fun invoke(variables: Map<String, Double>): Double = evaluate(variables)
}

/**
 * Utility to evaluate a list of MathTokens
 */
fun List<Any>.evaluate(variables: Map<String, Double>): Double {
    var total = 0.0
    //println("THIS IS $this")
    forEach {
        //println("TOTAL $total")
        if (it is Double) {
            //println("IT is $it")
            total += it
        }else if (it is MathToken) {
            //println("IT is $it")
            total += it.copy().evaluate(variables)
        }else if (it is String) {
            total += variables[it] ?: throw IllegalVariableException(it)
        }else{
            UnsupportedTypeException(it::class)
        }
    }
    return total
}

operator fun List<MathToken>.invoke(variables: Map<String, Double>): Double = evaluate(variables)

class UnsupportedTypeException(type: KClass<*>) : Exception("Unsupported type: $type")
class IllegalVariableException(variable: String) : Exception("Illegal variable: $variable")