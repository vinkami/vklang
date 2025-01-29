package vklang.parse.node

import vklang.lex.Position
import vklang.lex.TokenType

class InterruptNode(val innerNode: BaseNode, val type: TokenType, startPos: Position): BaseNode(startPos, innerNode.endPos) {
    override fun toString(): String {
        return "($type $innerNode)"
    }
}