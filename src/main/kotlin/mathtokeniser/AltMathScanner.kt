package uk.co.purpleeagle.mathtokeniser

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import uk.co.purpleeagle.models.stackOf
import uk.co.purpleeagle.tokeniser.Scanner
import uk.co.purpleeagle.tokeniser.Token
import uk.co.purpleeagle.tokeniser.TokenType
import uk.co.purpleeagle.util.log
import uk.co.purpleeagle.util.removeAtIndexes
import kotlin.system.exitProcess

class AltMathScanner(
    val source: MutableList<Token>,
) {
    inner class Power(
        val expression: List<Token>,
        val power: List<Token>
    ) {
        fun assembleMathToken(coefficient: MathToken?) : MathToken = MathToken(
            coefficient = coefficient,
            expression = assembleExpression(expression),
            variables = emptyMap(),
            parameters = assembleExpression(power),
            function = Operations.power.function

        )

        override fun toString(): String {
            return "Power(expression=$expression, power=$power)"
        }
    }

    data class Function (
        val function: Operation?,
        val expression: Power,
        val parameters: List<Power>
    ) {
        fun assembleMathToken(coefficient: MathToken?) : MathToken = MathToken(
            coefficient = coefficient,
            expression = listOf(expression.assembleMathToken(null)),
            variables = emptyMap(),
            parameters = parameters.map {
                it.assembleMathToken(null)
            },
            function = function?.function
        )
    }

    fun evaluateExpression() = generateExpressions(source)

    companion object {
        fun evaluateExpression(tokens : List<Token>) = MathScanner(mutableListOf()).generateExpressions(tokens)

    }

    fun getEquation() : Equation = runBlocking{
        var lhs: List<Token> = emptyList()
        var rhs: List<Token> = emptyList()
        var equalsIndex = 0
        var bracketsOpened = 0
        source.forEachIndexed { index, token ->
            when (token.tokenType) {
                TokenType.LEFT_BRACKET -> bracketsOpened++
                TokenType.RIGHT_BRACKET -> bracketsOpened--
                TokenType.LEFT_BRACE -> bracketsOpened++
                TokenType.RIGHT_BRACE -> bracketsOpened--
                TokenType.EQUAL -> {
                    if (bracketsOpened == 0) {
                        equalsIndex = index
                        lhs = source.subList(0, equalsIndex)
                    }
                }
                else -> {}
            }
        }
        rhs = source.subList(equalsIndex, source.size)
        var lhsMathToken: List<MathToken> = emptyList()
        val lhsJob = CoroutineScope(Dispatchers.Unconfined).launch {
            lhsMathToken = generateExpressions(lhs)
        }

        var rhsMathToken: List<MathToken> = emptyList()
        val rhsJob = CoroutineScope(Dispatchers.Unconfined).launch {
            lhsMathToken = generateExpressions(lhs)
        }

        lhsJob.join()
        rhsJob.join()

        return@runBlocking Equation(
            lhs = lhsMathToken,
            rhs = rhsMathToken,
        )
    }

    private fun generateExpressions(givenTokens: List<Token>): List<MathToken> {
        val sourceTokens = if (givenTokens.first().tokenType != TokenType.PLUS) regenerateTokens(givenTokens)
        else givenTokens
        //println("Source tokens are $sourceTokens")
        var bracketOpened = stackOf<Int>()
        var braceOpened = stackOf<Int>()
        var expressions = mutableListOf<List<Token>>()
        var current = 0

        var plusIndexes = mutableListOf<Int>()

        sourceTokens.forEach { token ->
            when (token.tokenType) {
                TokenType.LEFT_BRACKET -> bracketOpened.push(current)
                TokenType.RIGHT_BRACKET -> bracketOpened.pop()

                TokenType.LEFT_BRACE -> braceOpened.push(current)
                TokenType.RIGHT_BRACE -> braceOpened.pop()

                TokenType.PLUS -> {
                    if (bracketOpened.isEmpty() && braceOpened.isEmpty()) plusIndexes.add(current)
                }
                else -> {}
            }
            current++
        }
        //println("Plus indexes are $plusIndexes")

        for (i in plusIndexes.indices) {
            try {
                expressions.add(sourceTokens.subList(plusIndexes[i]+1, plusIndexes[i+1]))
                //println("Expressions are $expressions")
            }catch (_: Exception){}
        }

        do {
            val start = expressions

            expressions.forEachIndexed { index, expression ->
                var bracketsClosedEarly = false
                var bracketOpened = stackOf<Int>()
                if (expression.any { it.tokenType == TokenType.LEFT_BRACKET }) {
                    expression.forEachIndexed { i,token ->
                        when (token.tokenType) {
                            in listOf(TokenType.LEFT_BRACKET, TokenType.LEFT_BRACE) -> bracketOpened.push(i)
                            in listOf(TokenType.RIGHT_BRACKET, TokenType.RIGHT_BRACE) -> {
                                bracketOpened.pop()
                                if (bracketOpened.isEmpty() && index != expression.lastIndex) bracketsClosedEarly = true
                            }
                            else -> {}
                        }
                    }
                }

                if (!bracketsClosedEarly) {
                    if (expression.first().tokenType in listOf(TokenType.LEFT_BRACKET, TokenType.LEFT_BRACE) && expression.last().tokenType in listOf(TokenType.RIGHT_BRACKET,
                            TokenType.RIGHT_BRACE)) {
                        val new = expression.toMutableList()
                        new.removeLast()
                        new.removeFirst()
                        expressions[index] = new
                    }
                }
            }
        } while (start != expressions)

        expressions.removeAll { it.isEmpty() }

        expressions.log("Generate expressions")
        return expressions.map { splitByFunctions(it) }
    }

    /**
     * The boolean in the list of pairs reflects whether the value is inside brackets or part of a function expression
     * This is important for the [splitByPowers] method, which must know this.
     */
    private fun splitByFunctions(tokens: List<Token>): MathToken{
        val bracketsOpened = stackOf<Int>()
        val split: MutableList<Pair<List<Token>, Boolean>> = mutableListOf()
        val functionsPositions = mutableListOf<Int>()
        var lastSavedIndex = 0
        var functionsOpened = stackOf<Boolean>()

        tokens.forEachIndexed {index,  token ->
            println("LastSavedIndex is $lastSavedIndex")
            println("Index is $index")
            tokens.log("Tokens", index)
            when (token.tokenType) {
                in listOf(TokenType.LEFT_BRACKET, TokenType.LEFT_BRACE) -> {
                    if (bracketsOpened.isEmpty()) {
                        split.add(Pair(tokens.subList(lastSavedIndex, index), functionsOpened.isNotEmpty()))
                        lastSavedIndex = index + 1
                    }
                    bracketsOpened.push(index)
                }
                in listOf(TokenType.RIGHT_BRACKET, TokenType.RIGHT_BRACE) -> {
                    val last = bracketsOpened.pop()
                    if (bracketsOpened.isEmpty()) {
                        split.add(Pair(tokens.subList(last + 1, index), true))
                        lastSavedIndex = index+1
                    }
                }
                TokenType.OPERATION -> {
                    if (bracketsOpened.isEmpty() && functionsOpened.isEmpty()) {
                        split.add(Pair(tokens.subList(lastSavedIndex, index), true))
                        functionsPositions.add(index)
                        split.add(Pair(listOf(tokens[index]), false))
                        lastSavedIndex = index + 1
                        functionsOpened.push(true)
                    }else{

                    }
                }
                else -> if (functionsOpened.isNotEmpty()) functionsOpened.pop()
            }
        }
        split.add(Pair(tokens.subList(lastSavedIndex, tokens.size), false))
        val cleared = mutableListOf<Pair<List<Token>, Boolean>>()
        split.forEach {
            if (it.first.isNotEmpty()) cleared.add(it)
        }
        println("From split by functions is $cleared")

        return splitByPowers(cleared)
    }
    private fun splitByPowers(allTokens: List<Pair<List<Token>, Boolean>>): MathToken {
        val result: MutableList<MutableList<MutableList<Token>>> = mutableListOf()

        allTokens.forEachIndexed { index, tokens ->
            //allTokens.log("AllTokens", index)

            if (!tokens.second) {
                var powerIndex: Int? = null
                for (index in tokens.first.indices) {
                    val token = tokens.first[index]
                    if (token.tokenType == TokenType.POWER) {powerIndex = index ; break}
                }
                if (powerIndex != null) {
                    val first = tokens.first.subList(0, powerIndex).toMutableList()
                    val power = mutableListOf(tokens.first[powerIndex])
                    val second = tokens.first.subList(powerIndex + 1, tokens.first.size).toMutableList()
                    result.add(
                        run {
                            val adding = mutableListOf<MutableList<Token>>()
                            if (first.isNotEmpty()) adding.add(first)
                            adding.add(power)
                            if (second.isNotEmpty()) adding.add(second)
                            adding
                        }
                    )
                } else {
                    result.add(
                        mutableListOf(tokens.first.toMutableList())
                    )
                }
            }else {
                result.add(
                    mutableListOf(tokens.first.toMutableList())
                )
            }
            //println("Result : ")
            //println(result)
        }
        //println("FROM RESULT : $result")
        try {
            return fixSplitPowerResult(result)
        }catch (_: NotImplementedError){
            exitProcess(0)
        }
    }
    private fun fixSplitPowerResult(givenTokens: MutableList<MutableList<MutableList<Token>>>): MathToken {
        var skipping = false

        val result: MutableList<MutableList<MutableList<Token>>> = mutableListOf()

        for (i in givenTokens.indices) {
            val index = givenTokens.lastIndex - i
            if (skipping) {skipping = false; continue}
            val tokenList = givenTokens[index]

            when {
                tokenList.first().first().tokenType == TokenType.POWER && tokenList.size == 1 -> {
                    val last = result.removeLast()
                    result.addFirst(
                        (givenTokens[index-1] + tokenList + last).toMutableList()
                    )
                    skipping = true
                }
                tokenList.first().first().tokenType == TokenType.POWER -> {
                    result.addFirst(
                        (givenTokens[index-1] + tokenList).toMutableList()
                    )
                    skipping = true
                }
                tokenList.last().last().tokenType == TokenType.POWER -> {
                    val last = result.removeLast()
                    result.addFirst(
                        (tokenList + last).toMutableList()
                    )
                }
                else -> result.addFirst(tokenList)
            }
            //result.log("Result", null)
        }
        println("Result After Fix : ")
        println(result)
        return assembleFunctionExpressions(result)
    }
    private fun assembleFunctionExpressions(givenTokens: MutableList<MutableList<MutableList<Token>>>): MathToken {
        val functionPositions = mutableMapOf<Int, Operation>()
        val intRanges = mutableListOf<Int>()
        givenTokens.forEachIndexed { index, tokensList ->
            if (tokensList.first().first().tokenType == TokenType.OPERATION && tokensList.first().size == 1 && tokensList.size == 1)
                functionPositions.put(index, Operations.hashMap[tokensList.first().first().lexeme] ?: throw UnsupportedOperationException(tokensList.first().first().lexeme))
        }

        val result = mutableListOf<Function>()

        functionPositions.forEach {
            val operation = it.value
            val index = it.key

            val parameters = mutableListOf<MutableList<MutableList<Token>>>()
            repeat (operation.parameters.first){ parameters.add(givenTokens[index+it + 1]) }
            parameters.log("Parameters")

            val expression = if (operation.before) {
                givenTokens[index + operation.parameters.first+1]
            }else {
                givenTokens[index-1]
            }
            expression.log("Expression")

            result.add(
                Function(
                    function = operation,
                    expression = assemblePowerExpressions(expression),
                    parameters = parameters.map { parameter -> assemblePowerExpressions(parameter) }
                )
            )

            intRanges.addAll(
                if (operation.before) index..index + operation.parameters.first + 1
                else index - 1..index + operation.parameters.first
            )
        }
        givenTokens.removeAtIndexes(intRanges)
        givenTokens.log("Given tokens")

        result.addAll(givenTokens.map {
            Function(
                function = null,
                expression = assemblePowerExpressions(it),
                parameters = emptyList(),
            )
        })

        result.log("Function result")

        return assembleMathTokens(result.map { function -> function.assembleMathToken(null) })
    }
    private fun assemblePowerExpressions(givenTokens: MutableList<MutableList<Token>>) : Power {
        if (!givenTokens.any { it.first().tokenType == TokenType.POWER }) return Power(
            expression = givenTokens.first(),
            power = listOf(Token(TokenType.NUMBER, "1", 1))
        )

        return Power(
            expression = givenTokens.first(),
            power = givenTokens.last()
        )
    }
    private fun assembleExpression(tokens: List<Token>) : List<MathToken> {
        val expression =
            if (tokens.size == 1 && tokens.first().tokenType == TokenType.NUMBER) listOf(tokens.first().literal.toString().toDouble())
            else if (tokens.size == 1 && tokens.first().tokenType == TokenType.IDENTIFIER) listOf(tokens.first().lexeme)
            else return generateExpressions(tokens)

        return listOf(MathToken(
            coefficient = null,
            expression = expression,
            variables = emptyMap(),
            parameters = emptyList(),
            function = null
        ))
    }
    private fun assembleMathTokens(tokens: List<MathToken>) : MathToken {
        var mathToken: MathToken = tokens.first()

        for (index in 1..tokens.lastIndex) {
            mathToken = tokens[index].copy(
                coefficient = mathToken
            )
        }
        //println("From assemble token $mathToken")
        return mathToken
    }
    private fun regenerateTokens(tokens: List<Token>): List<Token> {
        var str = " "
        tokens.forEach {
            str += it.lexeme+" "
        }
        return Scanner(str).scanTokens()
    }
}