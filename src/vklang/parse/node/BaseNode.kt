package vklang.parse.node

import vklang.lex.Position

abstract class BaseNode(val startPos: Position, val endPos: Position) {
    var call: ArgumentsNode? = null
    val callStr: String
        get() = if (call == null) "" else call.toString()
}