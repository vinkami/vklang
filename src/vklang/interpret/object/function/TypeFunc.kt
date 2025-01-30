package vklang.interpret.`object`.function

import vklang.interpret.SymbolTable
import vklang.interpret.`object`.BaseObject
import vklang.interpret.`object`.StringObj
import vklang.lex.Position

class TypeFunc: BuiltinFunc("type") {
    // obj: any non-null object; required
    // returns: type of obj as a string

    override val parameters = listOf(Parameter("obj", null, null))

    override operator fun invoke(ref: SymbolTable, startPos: Position, endPos: Position): BaseObject {
        return StringObj(ref.get("obj")!!.type, startPos, endPos)
    }
}