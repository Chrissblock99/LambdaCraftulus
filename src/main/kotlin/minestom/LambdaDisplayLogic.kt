package me.chriss99.minestom

import me.chriss99.lambda.lazyReduce
import me.chriss99.minestom.LambdaSymbolManager.createErrorSymbol
import me.chriss99.parse.ParsingException
import me.chriss99.parse.lex
import me.chriss99.parse.parse
import minestom.BASE_BLOCK
import net.kyori.adventure.text.Component
import net.minestom.server.MinecraftServer
import net.minestom.server.coordinate.Point
import net.minestom.server.coordinate.Vec
import net.minestom.server.entity.Entity
import net.minestom.server.entity.EntityType
import net.minestom.server.entity.metadata.display.TextDisplayMeta
import net.minestom.server.instance.Instance
import net.minestom.server.instance.block.Block
import net.minestom.server.timer.TaskSchedule
import java.util.*

object LambdaParser {

    fun parseBlocks(start: Point = Vec.ONE, instance: Instance): String {
        var expression = ""

        LambdaBlockManager.getNextBlocks(100, start, instance).forEach { block ->
            if (block == BASE_BLOCK)
                return@forEach
            expression += fromBlock(block)?.symbol ?: " "
        }

        return try {
            val parsed = parse(lex(expression))
            lazyReduce(parsed).toString()
        } catch (p: ParsingException) {

            val marker = MutableList(p.tokens.size) { " " }
            for (i in p.cameFrom..p.problem)
                marker[i] = "-"
            marker[p.cameFrom] = "+"
            marker[p.problem] = "^"


            for (i in 0..<marker.size) {
                val char = marker[i]
                if (char != " ")
                    createErrorSymbol(char, start.add(-1-1.0 * i, -2.5, 0.0), instance)
            }
            return p.message ?: "No message provided"
        }
    }
}

object LambdaSymbolManager {
    fun createLambdaSymbol(block: Block, clicked: Point, instance: Instance): UUID {
        val (meta, display) = createSymbol()
        meta.text = Component.text(fromBlock(block)?.prettySymbol ?: "")
        display.setInstance(instance, clicked.add(0.65, 1.0, -0.01))
        display.setView(-180f, 0f)
        return display.uuid
    }

    fun createErrorSymbol(symbol: String, point: Point, instance: Instance): UUID {
        val (meta, display) = createSymbol()
        meta.text = Component.text(symbol)
        display.setInstance(instance, point.add(0.65, 1.0, -0.01))
        display.setView(-180f, 0f)

        MinecraftServer.getSchedulerManager().buildTask {
            display.remove()
        }.delay(TaskSchedule.seconds(5)).schedule()

        return display.uuid
    }

    private fun createSymbol(): Pair<TextDisplayMeta, Entity> {
        val display = Entity(EntityType.TEXT_DISPLAY)
        val meta = display.entityMeta as TextDisplayMeta
        meta.scale = Vec(6.0, 6.0, 0.01)
        meta.isShadow = true
        meta.backgroundColor = 0x00000000
        meta.isHasNoGravity = true
        meta.isSeeThrough = true
        return Pair(meta, display)
    }
}