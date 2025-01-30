package vklang.interpret

import vklang.Script
import vklang.exception.BaseError

fun run(code: String): Pair<String, BaseError?> {
    var output = ""
    val script = Script("<test>", code)
    val err = script.interpret(SymbolTable({ s: String -> output += s + "\n" }))
    return output to err
}