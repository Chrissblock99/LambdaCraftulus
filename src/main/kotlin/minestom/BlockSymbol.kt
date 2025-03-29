package me.chriss99.minestom

import net.minestom.server.instance.block.Block
import net.minestom.server.instance.block.Block.*

enum class BlockSymbol(val block: Block, val symbol: String, val prettySymbol: String = symbol) {
    DEFINE(JIGSAW.withProperty("orientation", "west_up"), "!", ""),
    L_PAREN(STICKY_PISTON.withProperty("facing", "west"), "("),
    R_PAREN(PISTON.withProperty("facing", "east"), ")"),
    LAMBDA(CRAFTING_TABLE, "\\", "λ"),
    DOT(LODESTONE, "."),
    VAR_R(RED_CONCRETE, "r"),
    VAR_B(BLUE_CONCRETE, "b"),
    VAR_G(GREEN_CONCRETE, "g"),
    VAR_C(CYAN_CONCRETE, "c"),
    VAR_Y(YELLOW_CONCRETE, "y"),
    VAR_M(MAGENTA_CONCRETE, "m"),
    VAR_P(PURPLE_CONCRETE, "p"),
    VAR_O(ORANGE_CONCRETE, "o"),
    VAR_L(LIME_CONCRETE, "l"),
    VAR_W(WHITE_CONCRETE, "w"),
    IMPORT(BOOKSHELF, ":", ""),
}

private val blockToSymbolMap = BlockSymbol.entries.associate { it.block to it.symbol }
private val blockToPrettySymbolMap = BlockSymbol.entries.associate { it.block to it.prettySymbol }
private val symbolToBlockMap = BlockSymbol.entries.associate { it.symbol to it.block }
val materialToBlockMap = BlockSymbol.entries.associate { it.block.registry().material() to it.block }

fun getParsableLambdaSymbol(block: Block): String {
    return blockToSymbolMap[block] ?: ""
}

fun getPrettyLambdaSymbol(block: Block): String {
    return blockToPrettySymbolMap[block] ?: ""
}

@SuppressWarnings
fun getBlockFromSymbol(symbol: String): Block? {
    return symbolToBlockMap[symbol]
}