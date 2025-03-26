package me.chriss99

import minestom.initInstance
import minestom.onJoin
import net.minestom.server.MinecraftServer
import net.minestom.server.extras.MojangAuth

fun main() {
    val minecraftServer = MinecraftServer.init()
    val instanceManager = MinecraftServer.getInstanceManager()
    val instanceContainer = instanceManager.createInstanceContainer()
    val eventHandler = MinecraftServer.getGlobalEventHandler()
    initInstance(instanceContainer)
    onJoin(eventHandler, instanceContainer)
    MojangAuth.init()
    minecraftServer.start("0.0.0.0", 25565)
}