package vklang.interpret.`object`

import vklang.interpret.Referables
import vklang.lex.Position

class ListObj(val elements: List<BaseObject>, override val startPos: Position, override val endPos: Position): BaseObject {
    override val type = "List"
    override val value = elements.joinToString(", ", "[", "]")
    override val property = Referables(null, isRoot=false)
    override val boolVal
        get() = elements.isNotEmpty()
    override fun toString() = value
}