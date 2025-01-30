package vklang.interpret.`object`.function

import vklang.exception.NotYourFaultError
import vklang.interpret.SymbolTable
import vklang.interpret.`object`.BaseObject
import vklang.interpret.`object`.NullObj
import vklang.interpret.`object`.StringObj
import vklang.lex.Position

class PrintFunc: BuiltinFunc("print") {
    // prints the string representation of s to standard output
    // s: string to print; optional; defaults to ""
    // returns: null

    override val parameters = listOf(
        Parameter("s", null, StringObj("", startPos, endPos))
    )

    override operator fun invoke(ref: SymbolTable, startPos: Position, endPos: Position): BaseObject {
        val stdout = ref.stdout
        if (stdout != null) {
            stdout(ref.get("s").toString())
        } else throw NotYourFaultError("No standard output", startPos, endPos)
        return NullObj(startPos, endPos)
    }
}