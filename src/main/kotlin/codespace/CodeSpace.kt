package me.chriss99.codespace

import me.chriss99.minestom.LambdaSymbolManager
import me.chriss99.minestom.fromMaterial
import net.minestom.server.coordinate.Point
import net.minestom.server.instance.Instance
import net.minestom.server.instance.block.Block
import net.minestom.server.item.ItemStack
import net.minestom.server.tag.Tag

class CodeSpace(val instance: Instance, val expressions: HashMap<Int, MutableList<Expression>>, val macros: HashMap<Block, Expression>) {

    fun addBlock(at: Point, item: ItemStack) {

        val block = fromMaterial(item.material())?.block ?: item.material().block() ?: return
        val symbol = LambdaSymbolManager.createLambdaSymbol(block, at, instance)
        val lambdaBlock = block.withTag(Tag.UUID("link"), symbol)

        instance.setBlock(at, lambdaBlock)
    }

    private fun getExpression(from: Point): Expression? {

        var expression: Expression? = null

        for (expr in expressions[from.blockY()] ?: return null) {

            if (expr.source.blockX() < from.blockX()) continue

            if (expr.source.blockX() < (expression?.source?.blockX() ?: Int.MAX_VALUE )) {
                expression = expr
            }

        }

        return expression
    }

}