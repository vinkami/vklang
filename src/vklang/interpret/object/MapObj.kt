package vklang.interpret.`object`

import vklang.interpret.SymbolTable
import vklang.lex.Position

class MapObj(val elements: Map<String, BaseObject>, override val startPos: Position, override val endPos: Position): BaseObject {
    override val type = "Map"
    override val value = elements.entries.joinToString(", ", "{", "}") { "${it.key}: ${it.value}" }
    override val property = SymbolTable(null, isRoot=false)
    override val boolVal
        get() = elements.isNotEmpty()
    override fun toString() = value
}