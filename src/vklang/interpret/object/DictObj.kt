package vklang.interpret.`object`

import vklang.interpret.Referables
import vklang.lex.Position

class DictObj(val elements: Map<BaseObject, BaseObject>, override val startPos: Position, override val endPos: Position): BaseObject {
    override val type = "Dict"
    override val value = elements.entries.joinToString(", ", "[", "]") { "${it.key}: ${it.value}" }
    override val property = Referables(null, isRoot=false)
    override val boolVal
        get() = elements.isNotEmpty()
    override fun toString() = value
}