package me.chriss99.lambda

import me.chriss99.lambda.Expression.*
import me.chriss99.parse.idOf
import java.util.UUID

fun reduce(appl: Apply): Expression {
    return when (appl.apply) {
        is Var, is Apply -> appl
        is Lambda -> replace(appl.apply.body, appl.apply.variable, appl.to)
    }
}

private fun replace(expr: Expression, replace: Var, with: Expression): Expression {
    return when (expr) {
        is Var -> if (expr.id == replace.id) replaceIDs(with) else expr
        is Lambda -> Lambda(expr.variable, replace(expr.body, replace, with))
        is Apply -> Apply(replace(expr.apply, replace, with), replace(expr.to, replace, with))
    }
}

private fun replaceIDs(expr: Expression, ids: HashMap<UUID, UUID> = HashMap()): Expression {
    return when (expr) {
        is Var -> Var(expr.name, idOf(expr.id, ids))
        is Lambda -> Lambda(Var(expr.variable.name, newID(expr.variable.id, ids)), replaceIDs(expr.body, ids))
        is Apply -> Apply(replaceIDs(expr.apply, ids), replaceIDs(expr.to, ids))
    }
}

private fun newID(name: UUID, ids: java.util.HashMap<UUID, UUID>): UUID {
    if (ids.containsKey(name))
        throw IllegalStateException("Non unique variables! \"$name\" is already bound, but found Lambda defining it as its variable!")
    val id = UUID.randomUUID()
    ids[name] = id
    return id
}

private fun reduceAt(expr: Expression, appl: Apply): Expression {
    if (expr === appl)
        return reduce(expr)

    return when (expr) {
        is Var -> expr
        is Lambda -> Lambda(expr.variable, reduceAt(expr.body, appl))
        is Apply -> Apply(reduceAt(expr.apply, appl), reduceAt(expr.to, appl))
    }
}

fun reduceAll(expr: Expression, strategy: (expr: Expression) -> Apply?): Expression {
    var current = expr
    while (true) {
        val reducible = strategy(current) ?: break
        current = reduceAt(current, reducible)
    }
    return current
}