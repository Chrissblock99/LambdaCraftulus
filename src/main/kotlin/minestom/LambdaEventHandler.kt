package me.chriss99.minestom

import me.chriss99.minestom.LambdaParser.parseBlocks
import minestom.BASE_BLOCK
import net.minestom.server.coordinate.Point
import net.minestom.server.entity.Player
import net.minestom.server.event.Event
import net.minestom.server.event.GlobalEventHandler
import net.minestom.server.event.player.PlayerHandAnimationEvent
import net.minestom.server.event.player.PlayerSwapItemEvent
import net.minestom.server.event.player.PlayerUseItemEvent
import net.minestom.server.event.trait.CancellableEvent
import net.minestom.server.event.trait.InstanceEvent
import net.minestom.server.event.trait.PlayerEvent
import net.minestom.server.event.trait.PlayerInstanceEvent

class LambdaEventHandler(private val eventHandler: GlobalEventHandler) {

    // FIXME: Doesn't work at all, if someone could make it work with packets or something
    //          that'd be great
    private val playerInteractionAssertionMap = mutableSetOf<Player>()

    fun registerEvents() {

        eventHandler.addListener(PlayerUseItemEvent::class.java) { event ->
            val blockPosition = event.player.getTargetBlockPosition(100) ?: return@addListener
            handlePlayerPlaceLambda(event, blockPosition)
        }

        eventHandler.addListener(PlayerHandAnimationEvent::class.java) { event ->

            event.isCancelled = true

            val blockPosition = event.player.getTargetBlockPosition(100) ?: return@addListener
            val instance = event.instance
            val block = instance.getBlock(blockPosition)

            if (block.compare(BASE_BLOCK))
                return@addListener

            LambdaBlockManager.removeLambdaBlock(blockPosition, instance)

        }

        eventHandler.addListener(PlayerSwapItemEvent::class.java) { event ->
            event.isCancelled = true
            val blockPosition = event.player.getTargetBlockPosition(100) ?: return@addListener
            val instance = event.instance

            val blocks = LambdaBlockManager.getNextBlocks(100, blockPosition, instance).toMutableList()
            var start = blockPosition

            for (element in blocks) {
                LambdaBlockManager.removeLambdaBlock(start, instance)
                LambdaBlockManager.setLambdaBlock(element, start, instance)

                start = start.add(-1.0, 0.0, 0.0)
            }
        }
    }

    /**
     * @param event Must inherit [CancellableEvent], [PlayerEvent], and [InstanceEvent]
     */
    private fun handlePlayerPlaceLambda(event: Event, blockPosition: Point) {
        if (!(event is CancellableEvent && event is PlayerEvent && event is PlayerInstanceEvent)) return

        try {
            event.isCancelled = true

            val instance = event.instance
            val item = event.player.itemInMainHand

            val block = instance.getBlock(blockPosition)

            if (block.compare(BlockSymbol.DEFINE.block)) {
                event.player.sendMessage(parseBlocks(blockPosition, instance))
                return
            } else if (block.compare(BASE_BLOCK)) {
                LambdaBlockManager.setLambdaBlock(item, blockPosition, instance)
                return
            }

            var start = blockPosition
            val blocks = LambdaBlockManager.getNextBlocks(100, blockPosition, instance).toMutableList()

            blocks.addFirst(block)
            blocks.addFirst(fromMaterial(item.material())?.block ?: item.material().block() ?: return)

            for (element in blocks) {
                LambdaBlockManager.removeLambdaBlock(start, instance)
                LambdaBlockManager.setLambdaBlock(element, start, instance)

                start = start.add(-1.0, 0.0, 0.0)
            }

        } finally {
            playerInteractionAssertionMap.remove(event.player)
        }

    }
}