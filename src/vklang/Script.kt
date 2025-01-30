package vklang

import vklang.exception.BaseError
import vklang.interpret.Interpreter
import vklang.interpret.SymbolTable
import vklang.lex.Lexer
import vklang.lex.Token
import vklang.parse.Parser
import vklang.parse.node.BaseNode
import java.io.File

class Script {
    val code: String
    val name: String

    constructor(filename: String) {
        this.name = filename
            .replace("\\", ".")
            .replace(".vk", "")
        this.code = File(filename).readText()
    }

    constructor(file: File) {
        this.name = file
            .toString()
            .replace("\\", ".")
            .replace(".vk", "")
        code = file.readText()
    }

    constructor(name: String, code: String) {
        this.name = name
        this.code = code
    }

    override fun toString(): String {
        return "<Script $name>"
    }

    fun lex(): Pair<List<Token>?, BaseError?> {
        return try {
            Lexer(code, name).tokenize() to null
        } catch (e: BaseError) {
            null to e
        }
    }

    fun parse(): Pair<BaseNode?, BaseError?> {
        try {
            val tokens = lex().also { if (it.second != null) return null to it.second }.first!!
            return Parser(tokens).parse() to null
        } catch (e: BaseError) {
            return null to e
        }
    }

    fun interpret(st: SymbolTable): BaseError? {
        val node = parse().also { if (it.second != null) return it.second }.first!!
        return Interpreter(node, st).interpret()
    }
}
