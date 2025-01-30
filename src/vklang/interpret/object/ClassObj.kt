package vklang.interpret.`object`

import vklang.interpret.SymbolTable
import vklang.parse.node.ClassNode

class ClassObj(val node: ClassNode): BaseObject {
    override val type = "Class"
    override val value = node.name.value
    override val startPos = node.startPos
    override val endPos = node.endPos
    override val property = SymbolTable(null, isRoot=false)

    override fun toString(): String {
        return "<class $value>"
    }
}