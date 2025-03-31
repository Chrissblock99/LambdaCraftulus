package me.chriss99.minestom

import minestom.BASE_BLOCK
import net.minestom.server.coordinate.Point
import net.minestom.server.instance.Instance
import net.minestom.server.instance.block.Block
import net.minestom.server.item.ItemStack
import net.minestom.server.tag.Tag

// TODO: Migrate most if not all of LambdaBlockManager to CodeSpace/Expression
@Deprecated("Creating CodeSpace code base")
object LambdaBlockManager {

    fun getNextBlocks(amount: Int, from: Point, instance: Instance): List<Block> {
        val blocks = mutableListOf<Block>()
        var target = from
        repeat(amount) {
            target = target.add(-1.0, 0.0, 0.0)
            val block = instance.getBlock(target)
            if (BlockSymbol.DEFINE.block.compare(block))
                return@repeat
            blocks.add(block)
        }
        return blocks
    }

    fun getNextLambdaBlock(max: Int, start: Point, instance: Instance, includeDefineBlock: Boolean): Block? {
        var target = start
        repeat(max) {
            target = target.add(-1.0, 0.0, 0.0)
            val block = instance.getBlock(target)
            if (isLambdaBlock(block, instance, includeDefineBlock)) return block

        }
        return null
    }

    fun isLambdaBlock(at: Point, instance: Instance, includeDefineBlock: Boolean): Boolean {
        return isLambdaBlock(instance.getBlock(at), instance, includeDefineBlock)
    }

    fun isLambdaBlock(block: Block, instance: Instance, includeDefineBlock: Boolean): Boolean {
        if (BlockSymbol.DEFINE.block.compare(block) && includeDefineBlock)
            return true
        if (!block.compare(BASE_BLOCK)) return true
        return false
    }

    fun setLambdaBlock(item: ItemStack, clicked: Point, instance: Instance) {
        val block = fromMaterial(item.material())?.block ?: item.material().block() ?: return

        setLambdaBlock(block, clicked, instance)
    }

    fun setLambdaBlock(block: Block, clicked: Point, instance: Instance) {
        val symbol = LambdaSymbolManager.createLambdaSymbol(block, clicked, instance)
        val lambdaBlock = block.withTag(Tag.UUID("link"), symbol)

        instance.setBlock(clicked, lambdaBlock)
    }

    fun removeLambdaBlock(at: Point, instance: Instance) {
        val block = instance.getBlock(at)
        val tag = block.getTag(Tag.UUID("link")) ?: return

        instance.getEntityByUuid(tag)?.remove()
        instance.setBlock(at, BASE_BLOCK)
    }

}