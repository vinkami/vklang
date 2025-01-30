package vklang.exception

import vklang.interpret.SymbolTable
import vklang.interpret.`object`.BaseObject
import vklang.lex.Position

abstract class BaseLangException(message: String, override val startPos: Position, override val endPos: Position): BaseObject, Exception(message) {
    override val type = "Error"
    override val value = message
    override val property = SymbolTable(null, isRoot=false)
    override val boolVal = true

    override fun toString(): String {
        return """
            ${this::class.simpleName}: $message
            Traceback:
                File: ${startPos.fileName}, line: ${startPos.line + 1}
                    ${startPos.code.split('\n')[startPos.line].trimIndent()}
                ${makeArrow(startPos, endPos)}
        """.trimIndent()
    }

    private fun makeArrow(startPos: Position, endPos: Position): String {
        val result = StringBuilder()
        result.append(" ".repeat(startPos.column))
        result.append("^".repeat(endPos.column - startPos.column))
        return result.toString()
    }
}