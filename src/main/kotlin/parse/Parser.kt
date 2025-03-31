package me.chriss99.parse

import me.chriss99.lambda.LambdaExpr
import java.util.UUID
import kotlin.collections.HashMap
import kotlin.reflect.KClass

fun parse(tokens: List<Token>): LambdaExpr {
    return parse(listOf(*tokens.toTypedArray(), Token.EndOfFile), 0, HashMap(), 0).first
}

private fun parse(tokens: List<Token>, i: Int, ids: HashMap<String, Pair<UUID, Int>>, cameFrom: Int, searching: KClass<out Token>? = null): Pair<LambdaExpr, Int> {
    when (val current = tokens[i]) {
        is Token.Var -> return LambdaExpr.Var(current.name, idOf(current.name, i, ids)) to i
        is Token.Lambda -> {
            var next = i+1
            val variables = mutableListOf<LambdaExpr.Var>()
            while (tokens[next] is Token.Var || next == i+1) {
                variables.add(verifyToken<Token.Var>(tokens, next, i).name.let { LambdaExpr.Var(it, newID(it, ids, tokens, next)) })
                next++
            }
            verifyToken<Token.Dot>(tokens, next, i)
            val (body, i) = parse(tokens, next+1, HashMap(ids), i)

            var lambda = LambdaExpr.Lambda(variables.removeLast(), body)
            while (variables.isNotEmpty())
                lambda = LambdaExpr.Lambda(variables.removeLast(), lambda)

            return lambda to i
        }
        is Token.LParen -> {
            var (expr, exprEnd) = parse(tokens, i+1, HashMap(ids), i)

            while (tokens[exprEnd+1] !is Token.RParen) {
                val res = parse(tokens, exprEnd+1, HashMap(ids), i, Token.RParen::class)
                expr = LambdaExpr.Apply(expr, res.first)
                exprEnd = res.second
            }

            return expr to exprEnd+1
        }
        else -> throw ParsingException.NoMatchingPatternException(tokens, cameFrom, i, *(if (searching != null) listOf(searching) else listOf()).toTypedArray(), Token.Var::class, Token.Lambda::class, Token.LParen::class)
    }
}

private fun newID(name: String, ids: HashMap<String, Pair<UUID, Int>>, tokens: List<Token>, index: Int): UUID {
    val prevBound = ids[name]
    if (prevBound != null)
        throw ParsingException.NonUniqueVariableException(tokens, name, prevBound.second, index)
    val id = UUID.randomUUID()
    ids[name] = id to index
    return id
}

private fun idOf(name: String, index: Int, ids: HashMap<String, Pair<UUID, Int>>): UUID {
    return ids[name]?.first ?: UUID.randomUUID().also { ids[name] = it to index }
}

private inline fun <reified T : Token> verifyToken(tokens: List<Token>, index: Int, cameFrom: Int): T {
    val token = tokens[index]
    if (token !is T)
        throw ParsingException.UnexpectedTokenException(tokens, T::class, token::class, cameFrom, index,)
    return token
}

sealed class ParsingException(val tokens: List<Token>, val cameFrom: Int, val problem: Int, message: String) : Exception(message) {
    override fun toString(): String {
        var message = "${this::class.simpleName}: $message" + System.lineSeparator()
        message += tokens.fold("") { a, b -> a + tokenToString(b) }

        val marker = MutableList(tokens.size) { " " }
        for (i in cameFrom..problem)
            marker[i] = "-"
        marker[cameFrom] = "+"
        marker[problem] = "^"


        return message + System.lineSeparator() + marker.fold("") { a, b -> a + b }
    }

    class UnexpectedTokenException(tokens: List<Token>, expected: KClass<out Token>, found: KClass<out Token>, cameFrom: Int, problem: Int) : ParsingException(tokens, cameFrom, problem,
        message = "Expected ${expected.simpleName} but found ${found.simpleName}.")
    class NoMatchingPatternException(tokens: List<Token>, cameFrom: Int, problem: Int, vararg expected: KClass<out Token>) : ParsingException(tokens, cameFrom, problem,
        message = "Expected one of ${expected.map { it.simpleName }} but found ${tokens[problem]::class.simpleName}.")
    class NonUniqueVariableException(tokens: List<Token>, name: String, prevBound: Int, problem: Int) : ParsingException(tokens, prevBound, problem,
        message = "\"$name\" is already bound, but found Lambda binding it as its variable!")
}