package me.chriss99.lambda

import me.chriss99.lambda.LambdaExpr.*
import java.util.*

fun lazyReducible(expr: LambdaExpr): Apply? {
    val queue: LinkedList<LambdaExpr> = LinkedList()
    queue.add(expr)

    while (!queue.isEmpty())
        when (val current = queue.pop()) {
            is Var -> {}
            is Lambda -> queue.add(current.body)
            is Apply -> if (current.apply is Lambda)
                return current
            else {
                queue.add(current.apply)
                queue.add(current.to)
            }
        }

    return null
}

fun lazyReduce(expr: LambdaExpr): LambdaExpr {
    return reduceAll(expr, ::lazyReducible)
}