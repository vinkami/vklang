package vklang.interpret.`object`

import vklang.interpret.SymbolTable
import vklang.lex.Position

class BoolObj(override val value: Boolean, override val startPos: Position, override val endPos: Position): BaseObject {
    override val type = "Boolean"
    override val property = SymbolTable(null, isRoot=false)
    override val boolVal
        get() = value
    val intObj
        get() = NumberObj(if (value) 1f else 0f, startPos, endPos)

    override fun toString(): String {
        return if (value) "true" else "false"
    }

    override fun plus(other: BaseObject): BaseObject = intObj.plus(other)
    override fun minus(other: BaseObject): BaseObject = intObj.minus(other)
    override fun times(other: BaseObject): BaseObject = intObj.times(other)
}