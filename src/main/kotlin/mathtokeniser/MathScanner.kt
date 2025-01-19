package uk.co.purpleeagle.mathtokeniser

import uk.co.purpleeagle.models.stackOf
import uk.co.purpleeagle.tokeniser.Scanner
import uk.co.purpleeagle.tokeniser.Token
import uk.co.purpleeagle.tokeniser.TokenType

class MathScanner(
    val source: MutableList<Token>
) {
    var tokens = arrayListOf<MathToken>()

    var lhs: List<Token>
    var rhs: List<Token>

    init {
        source.forEachIndexed { index, token ->
            if (token.tokenType == TokenType.IDENTIFIER) {
                if (Operations.hashMap.containsKey(token.lexeme)) source[index] = source[index].copy(TokenType.OPERATION)
            }
        }

        val equalsIndex = source.indexOfFirst { it.tokenType == TokenType.EQUAL }
        lhs = source.subList(0, equalsIndex)
        rhs = source.subList(equalsIndex + 1, source.size)
    }

    fun generateExpressions(givenTokens: List<Token>): List<MathToken> {
        val sourceTokens = if (givenTokens.first().tokenType != TokenType.PLUS) regenerateTokens(givenTokens)
        else givenTokens
        println("Source tokens are $sourceTokens")
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
        println("Plus indexes are $plusIndexes")

        for (i in plusIndexes.indices) {
            try {
                expressions.add(sourceTokens.subList(plusIndexes[i]+1, plusIndexes[i+1]))
                println("Expressions are $expressions")
            }catch (_: Exception){}
        }
        expressions.removeAll { it.isEmpty() }
        println("From generate expression is $expressions")
        return expressions.map { splitExpressions(it) }
    }

    private fun splitExpressions(tokens: List<Token>): MathToken {
        val splitTokens = mutableListOf<List<Token>>()
        var bracketOpened = stackOf<Int>()
        var braceOpened = stackOf<Int>()
        var parametersRequired = 0
        var lastSavedIndex = 0

        for (index in tokens.indices) {
            println("After loop $index, lastSavedIndex $lastSavedIndex")
            var token = tokens[index]
            when (token.tokenType) {
                TokenType.LEFT_BRACKET -> {
                    bracketOpened.push(index)
                    splitTokens.add(tokens.subList(lastSavedIndex, index))
                }
                TokenType.RIGHT_BRACKET -> {
                    val last = bracketOpened.pop()
                    if(bracketOpened.isEmpty()) splitTokens.add(tokens.subList(last+1, index))
                    lastSavedIndex = index
                }
                TokenType.LEFT_BRACE -> {
                    braceOpened.push(index)
                    splitTokens.add(tokens.subList(lastSavedIndex, index))
                    println("Left bracket caused save of : ${tokens.subList(lastSavedIndex, index)}")
                }
                TokenType.RIGHT_BRACE -> {
                    val last = braceOpened.pop()
                    if(braceOpened.isEmpty()) splitTokens.add(tokens.subList(last+1, index))
                    parametersRequired--
                    lastSavedIndex = index
                }
                TokenType.OPERATION -> {
                    splitTokens.add(tokens.subList(lastSavedIndex, index))
                    println("First add, Just Added : ${tokens.subList(lastSavedIndex, index)}")
                    splitTokens.add(listOf(token))
                    println("Second add, Just Added : $token")
                    val operation = Operations.hashMap[token.lexeme] ?: throw Exception("Function [${token.lexeme}] not yet supported")
                    parametersRequired += operation.parameters.first
                    lastSavedIndex = index+1
                }

                TokenType.POWER -> {
                    splitTokens.add(tokens.subList(lastSavedIndex, index))
                    splitTokens.add(listOf(token))
                    lastSavedIndex = index
                }
                else -> {}
            }
        }
        try {
            //if (lastSavedIndex > 0) lastSavedIndex ++
            println("End, lastSavedIndex $lastSavedIndex")
            println("Size is ${tokens.size}")
            splitTokens.add(tokens.takeLast(tokens.size-lastSavedIndex))
        }catch (_: Exception){}
        println("Pre clearing is $splitTokens")
        val cleared = mutableListOf<List<Token>>()
        for (token in splitTokens) {
            if (token.isNotEmpty()) {
                if (token.first().tokenType !in listOf(TokenType.RIGHT_BRACKET, TokenType.RIGHT_BRACE, TokenType.LEFT_BRACKET,
                        TokenType.RIGHT_BRACE)) {
                    if (token.size > 1 && token.first().tokenType == TokenType.OPERATION) {
                        cleared.add(token.takeLast(token.size-1))
                    }else {
                        cleared.add(token)
                    }
                }
            }
        }
        println("From split expressions $cleared")
        return assembleMathToken(processSingleCoefficient(cleared))
    }

    private fun processSingleCoefficient(tokens: List<List<Token>>): List<MathToken> {
        val mutableTokens = tokens.toMutableList()
        val mathTokens = mutableListOf<Pair<MathToken, List<Int>>>()

        val functionPositions = stackOf<Int>()
        mutableTokens.forEachIndexed {index, tokenList ->
            if (tokenList.first().tokenType == TokenType.OPERATION) functionPositions.push(index)
        }

        functionPositions.forEach {pos ->
            val operation = Operations.hashMap[mutableTokens[pos].first().lexeme] ?: throw Exception("Function [${mutableTokens[pos].first().lexeme}] not yet supported")
            val parameters = mutableListOf<List<Token>>()
            repeat (operation.parameters.first){ parameters.add(mutableTokens[pos + it + 1]) }
            println("At pos we find ${mutableTokens[pos]}")
            println("POS is $pos")
            println("Parameters is ${operation.parameters.first}")
            println("Parameters are $parameters")
            println("Mutable tokens is $mutableTokens")
            println("Math tokens are $mathTokens")
            var expression = if (operation.before) {
                mutableTokens[pos + operation.parameters.first + 1]
            } else {
                mutableTokens[pos - 1]
            }
            println("Expression is $expression")
            val rangeUsed = if (operation.before) {
                pos..(pos + operation.parameters.first + 1)
            }else {
                (pos - 1)..(pos + operation.parameters.first)
            }
            val mathTokenExpression = if (expression == listOf(Token.replacedToken())) {
                println("Searching for token containing ${pos+1}")
                listOf(mathTokens.find { pos+1 in it.second }?.first ?: throw Exception("WEIRD"))
            }else {

                rangeUsed.forEach {
                    println("REPLACING AT $it, TOKEN: ${mutableTokens[it]}")
                    mutableTokens[it] = listOf(Token.replacedToken())
                }

                generateExpressions(expression)
            }

            val mathTokenParameters = parameters.map {
                println(it)
                val result = generateExpressions(it)
                println("Result is $result")
                result
            }
            println("MathTokenParameters are $mathTokenParameters")

            mathTokens.add(
                Pair(
                    MathToken(
                        coefficient = null,
                        expression = mathTokenExpression,
                        parameters = mathTokenParameters,
                        variables = emptyMap(),
                        function = operation.function
                    ),
                    rangeUsed.toList()
                )
            )
        }

        mutableTokens.removeAll { it.first() == Token.replacedToken() }
        println(mutableTokens)
        mutableTokens.forEach {
            val token = if (it.size == 1 && it.first().tokenType == TokenType.NUMBER) listOf(it.first().literal as Double)
                        else if (it.size == 1 && it.first().tokenType == TokenType.IDENTIFIER) listOf(it.first().lexeme)
                        else generateExpressions(it)
            mathTokens.add(
                Pair(
                    MathToken(
                        coefficient = null,
                        expression = token,
                        variables = emptyMap(),
                        parameters = emptyList(),
                        function = null
                    ),
                    emptyList()
                )
            )
        }
        println("From single coefficient $mathTokens")
        return mathTokens.map { it.first }
    }

    private fun assembleMathToken(tokens: List<MathToken>) : MathToken {
        var mathToken: MathToken = tokens.first()

        for (index in 1..tokens.lastIndex) {
            mathToken = tokens[index].copy(
                coefficient = mathToken
            )
        }
        println("From assemble token $mathToken")
        return mathToken
    }

    fun regenerateTokens(tokens: List<Token>): List<Token> {
        var str = " "
        tokens.forEach {
            str += it.lexeme+" "
        }
        return Scanner(str).scanTokens()
    }

    private fun joinFunctions(split: List<List<Token>>): List<List<List<Token>>> {
        val mutableSplit = split.toMutableList()
        val result: MutableList<Pair<MutableList<MutableList<Token>>, IntRange>> = mutableListOf()

        val functionPositions = stackOf<Int>()
        split.forEachIndexed { index, tokenList ->
            if (tokenList.first().tokenType == TokenType.OPERATION) {
                functionPositions.push(index)
            }
        }

        functionPositions.forEach{ pos ->
            val tba = mutableListOf<MutableList<Token>>()
            val first = mutableSplit[pos].first()

            val operation = Operations.hashMap[first.lexeme] ?: throw Exception("Function [${first.lexeme}] not yet supported")
            val parameters = mutableListOf<List<Token>>()
            repeat (operation.parameters.first){ parameters.add(mutableSplit[pos + it + 1]) }

            var expression = if (operation.before) {
                mutableSplit[pos + 1 + operation.parameters.first]
            }else {
                mutableSplit[pos - 1]
            }

            expression = if (expression == listOf(Token.replacedToken())) {
                result.find { pos in it.second }?.first?.combine() ?: throw Exception("Weird")
            } else {

                expression
            }
        }

        return result.map { it.first }
    }

    fun List<List<Token>>.combine(): List<Token> {
        val result: MutableList<Token> = mutableListOf()
        forEach { result.addAll(it) }
        return result
    }
}