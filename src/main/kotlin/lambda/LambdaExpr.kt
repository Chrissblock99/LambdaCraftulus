package me.chriss99.lambda

import java.util.UUID

sealed class LambdaExpr {
    class Var(val name: String, val id: UUID) : LambdaExpr()
    class Lambda(val variable: Var, val body: LambdaExpr) : LambdaExpr()
    class Apply(val apply: LambdaExpr, val to: LambdaExpr) : LambdaExpr()

    override fun toString(): String {
        return when (this) {
            is Var -> name
            is Lambda -> "\\$variable.$body"
            is Apply -> "(${if (apply is Lambda) "($apply)" else apply.toString()}$to)"
        }
    }
}