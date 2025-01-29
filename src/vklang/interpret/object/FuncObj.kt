package vklang.interpret.`object`

import vklang.interpret.Referables
import vklang.parse.node.FuncNode

class FuncObj(val node: FuncNode): BaseObject {
    override val type = "Function"
    override val value = node.name.value
    override val startPos = node.startPos
    override val endPos = node.endPos
    override val property = Referables(null, isRoot=false)

    override fun toString(): String {
        return "<function $value>"
    }
}