package vklang.interpret.`object`

import vklang.interpret.SymbolTable
import vklang.lex.Position

class CustomObj(override val type: String, override val property: SymbolTable, override val startPos: Position, override val endPos: Position): BaseObject {
    override val value = "<Object $type>"

    override fun toString(): String = "<Object $type>"
}