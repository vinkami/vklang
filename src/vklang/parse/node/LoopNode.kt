package vklang.parse.node

import vklang.lex.Position
import vklang.lex.TokenType

class LoopNode(val loopTokenType: TokenType, val condition: BaseNode, val mainAction: BaseNode,
               val compAction: BaseNode?, val incompAction: BaseNode?,
               startPos: Position, endPos: Position): BaseNode(startPos, endPos) {

    override fun toString(): String {
        val sb = StringBuilder()
        sb.append("($loopTokenType $condition -> $mainAction")
        if (compAction != null) {
            sb.append(" | complete -> $compAction")
        }
        if (incompAction != null) {
            sb.append(" | incomplete -> $incompAction")
        }
        sb.append(")")
        sb.append(callStr)
        return sb.toString()
    }
}