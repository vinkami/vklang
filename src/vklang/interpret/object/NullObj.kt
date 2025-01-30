package vklang.interpret.`object`

import vklang.interpret.SymbolTable
import vklang.lex.Position

class NullObj(override val startPos: Position, override val endPos: Position): BaseObject {
    override val type = "Null"
    override val value = "null"
    override val property = SymbolTable(null, isRoot=false)
    override val boolVal = false
    override fun toString() = "null"
}