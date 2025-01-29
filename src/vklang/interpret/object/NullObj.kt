package vklang.interpret.`object`

import vklang.interpret.Referables
import vklang.lex.Position

class NullObj(override val startPos: Position, override val endPos: Position): BaseObject {
    override val type = "Null"
    override val value = "null"
    override val property = Referables(null, isRoot=false)
    override val boolVal = false
    override fun toString() = "null"
}