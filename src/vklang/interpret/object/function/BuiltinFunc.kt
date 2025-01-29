package vklang.interpret.`object`.function

import vklang.Constant.builtinPos
import vklang.interpret.Referables
import vklang.interpret.`object`.BaseObject
import vklang.lex.Position

abstract class BuiltinFunc(val name: String): BaseObject {
    override val type = "Function"
    override val value = name
    override val startPos = builtinPos  // these startPos and endPos are to act as a placeholder for default values of parameters.
    override val endPos = builtinPos    // the actual startPos and endPos are passed in the invoke() function.
    override val property = Referables(null, isRoot=false)

    override fun toString(): String {
        return "<function $name>"
    }

    abstract operator fun invoke(ref: Referables, startPos: Position, endPos: Position): BaseObject
    abstract val parameters: List<Parameter>
}