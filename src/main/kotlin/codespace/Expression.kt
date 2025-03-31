package me.chriss99.codespace

import net.minestom.server.coordinate.Point
import net.minestom.server.instance.block.Block

class Expression(val codeSpace: CodeSpace, source: Point, macro: Block? = null) {

    var source: Point = source
        private set
    val blocks = mutableListOf<Block>()
    var macro: Block? = macro
        set(value) {

            if (value == null) {
                codeSpace.macros.remove(field)
                field = null
                return
            }

            codeSpace.macros[value] = this
            field = value

        }



}