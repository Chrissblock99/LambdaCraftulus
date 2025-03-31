package me.chriss99

import me.chriss99.lambda.LambdaExpr
import java.util.UUID

fun namedVar(name: String): LambdaExpr.Var {
    return LambdaExpr.Var(name, UUID.randomUUID())
}

fun idExpr(): LambdaExpr {
    val a = namedVar("a")
    return LambdaExpr.Lambda(a ,a)
}

fun trueExpr(): LambdaExpr {
    val x = namedVar("x")
    val y = namedVar("y")
    return LambdaExpr.Lambda(x, LambdaExpr.Lambda(y, x))
}

fun falseExpr(): LambdaExpr {
    val x = namedVar("x")
    val y = namedVar("y")
    return LambdaExpr.Lambda(x, LambdaExpr.Lambda(y, y))
}

fun notExpr(): LambdaExpr {
    val b = namedVar("b")
    return LambdaExpr.Lambda(b, LambdaExpr.Apply(LambdaExpr.Apply(b, falseExpr()), trueExpr()))
}

fun numberExpr(num: Int): LambdaExpr {
    val f = namedVar("f")
    val v = namedVar("v")
    var expr: LambdaExpr = v

    for (i in 1..num)
        expr = LambdaExpr.Apply(f, expr)

    return LambdaExpr.Lambda(f, LambdaExpr.Lambda(v, expr))
}

fun succExpr(): LambdaExpr {
    val n = namedVar("n")
    val f = namedVar("f")
    val v = namedVar("v")

    return LambdaExpr.Lambda(n, LambdaExpr.Lambda(f, LambdaExpr.Lambda(v, LambdaExpr.Apply(f, LambdaExpr.Apply(LambdaExpr.Apply(n, f), v)))))
}

fun addExpr(): LambdaExpr {
    val m = namedVar("m")
    val n = namedVar("n")
    val f = namedVar("f")
    val v = namedVar("v")

    return LambdaExpr.Lambda(m, LambdaExpr.Lambda(n, LambdaExpr.Lambda(f, LambdaExpr.Lambda(v, LambdaExpr.Apply(LambdaExpr.Apply(m, f), LambdaExpr.Apply(LambdaExpr.Apply(n, f), v))))))
}