package vklang.parse.node

import vklang.lex.Position
import vklang.lex.Token

class DictNode(val dict: Map<Token, BaseNode>, startPos: Position, endPos: Position): BaseNode(startPos, endPos) {
    override fun toString(): String {
        val sb = StringBuilder()
        sb.append("[")
        for ((key, value) in dict) {
            sb.append("$key=$value, ")
        }
        sb.append("]")
        sb.append(callStr)
        return sb.toString()
    }
}